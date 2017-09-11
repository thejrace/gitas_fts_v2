package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Jeppe on 19.05.2017.
 */
public class Takip_Scene extends Application {

    private Takip_Scene_Controller controller;
    private Map<String, Otobus_Box> otobus_kutular = new LinkedHashMap<>();
    private Map<String, ArrayList<Alarm_Data>> orer_alarmlar = new HashMap<>();
    private Sefer_Rapor_Data canli_durum;
    private Otobus_Box_Filtre_Data prev_filtre_data;
    private String prev_filtre_kapi;
    private Stage stage;
    private boolean FILTRE_INIT = true;
    private int temp_elem_index = 0;
    private int OTOBUS_SAYAC = 0;
    private Sefer_Rapor_Data header_rapor_data;
    private Filo_Rapor_Data filo_header_data;
    private boolean ozet_thread_shutdown = false;
    private Thread plaka_kontrol_thread;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader;
        try {
            fxmlLoader = new FXMLLoader(getClass().getResource("resources/fxml/gitas_main.fxml"));
            Parent root = fxmlLoader.load();
            controller = fxmlLoader.getController();
            primaryStage.setTitle("Gitaş Filo Takip");
            primaryStage.initStyle(StageStyle.DECORATED);
            try {
                Font.loadFont(getClass().getResource("resources/font/montserratbold.otf").toExternalForm().replace("%20", " "), 10);
                Font.loadFont(getClass().getResource("resources/font/montserratsemibold.otf").toExternalForm().replace("%20", " "), 10);
                Font.loadFont(getClass().getResource("resources/font/montserratlight.otf").toExternalForm().replace("%20", " "), 10);
            } catch (Exception e) {
                e.printStackTrace();
            }
            primaryStage.setScene(new Scene(root, 1024, 768));
            primaryStage.getIcons().add(new Image(getClass().getResource("resources/img/app_ico.png").toExternalForm()));
            primaryStage.show();
            stage = primaryStage;
        } catch( Exception e ){
            e.printStackTrace();
        }
        // pencere boyutuna gore kutulari dizme event
        controller.screen_resize_cb();
        controller.init_filtre();
        controller.ayarlar_init();
        controller.init_piechart();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                // cookie leri sıfırla
                //User_Config.cookie_json_dosyasi_olustur();
                Platform.exit();
                System.exit(0);
            }
        });
        controller.add_filtre_listener(new Filtre_Listener() {
            @Override
            public void on_filtre_set( String kapi, Otobus_Box_Filtre_Data filtre_data ) {
                otobus_kutular_filtre( kapi, filtre_data );
            }
        });
        // grafik, istatistik ve piechart data
        header_rapor_data = new Sefer_Rapor_Data("", 0, 0, 0, 0, 0, 0, 0,0,0 );
        filo_header_data = new Filo_Rapor_Data();
        canli_durum = new Sefer_Rapor_Data(0, 0, 0, 0, 0);
        otobus_kutular_init();
        Filo_Login_Task filo_login_task = new Filo_Login_Task( User_Config.app_filo5_data );
        controller.app_status_guncelle("Filoya giriş yapılıyor..");
        filo_login_task.yap(new Cookie_Refresh_Listener() {
            @Override
            public void on_refresh(Map<String, String> cookies) {
                if( otobus_kutular.isEmpty() ) return;
                for (Map.Entry<String, Otobus_Box> entry : otobus_kutular.entrySet()) {
                    entry.getValue().cookie_guncelle( cookies.get(entry.getValue().get_bolge() ) );
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        controller.app_status_guncelle("Aktif");
                    }
                });
            }
        });
        plaka_kontrol_thread();
        controller.alarm_popup_init();
        ozet_thread();
    }


    private void otobus_kutular_init(){
        temp_elem_index = 0;
        try {
            //JSONObject config = User_Config.config_oku();
            JSONArray otobusler = User_Config.app_otobusler;
            // hazir tum otobusleri loop ederken arada kutulari da olusturuyoruz
            JSONObject otobus_object;
            for( int j = 0; j < otobusler.length(); j++ ){
                otobus_object = otobusler.getJSONObject(j);
                String kod = otobus_object.getString("kapi_kodu");
                final Otobus_Box box_item = new Otobus_Box(kod, temp_elem_index, otobus_object.getString("ruhsat_plaka"), otobus_object.getString("aktif_plaka"));
                otobus_kutular.put(kod, box_item);
                box_item.add_alarm_listener( new Alarm_Listener(){
                    @Override
                    public void on_ui_finished( ArrayList<Alarm_Data> yeni_alarmlar ){
                        controller.alarmlari_guncelle( kod, yeni_alarmlar );
                        if( OTOBUS_SAYAC < otobus_kutular.size() ) OTOBUS_SAYAC++;
                    }
                });
                controller.add_otobus_box(box_item.get_ui());
                temp_elem_index++;
            }
        } catch( JSONException e ){
            e.printStackTrace();
        }
    }

    private void ozet_thread(){

        Thread th = new Thread(new Runnable() {
            private int sleep = 10000;
            @Override
            public void run() {
                Otobus_Box otobus_box;
                while( !ozet_thread_shutdown ){
                    if( OTOBUS_SAYAC == otobus_kutular.size() ){
                        sleep = 20000;

                        header_rapor_data = new Sefer_Rapor_Data("", 0, 0, 0, 0, 0, 0, 0,0,0 );
                        filo_header_data = new Filo_Rapor_Data();
                        canli_durum = new Sefer_Rapor_Data(0, 0, 0, 0, 0);

                        for (Map.Entry<String, Otobus_Box> entry : otobus_kutular.entrySet()) {
                            otobus_box = entry.getValue();
                            for (Map.Entry<String, Integer> ozet_entry : otobus_box.get_ozet().entrySet()) for( int k = 0; k < ozet_entry.getValue(); k++ ) header_rapor_data.arttir( ozet_entry.getKey() );
                            if( otobus_box.get_filtre_data(Otobus_Box_Filtre.FD_ZAYI) ){
                                filo_header_data.ekle( Otobus_Box_Filtre.FD_ZAYI, otobus_box.get_ozet().get(Sefer_Data.DIPTAL));
                                filo_header_data.ekle( Otobus_Box_Filtre.FD_ZAYI, otobus_box.get_ozet().get(Sefer_Data.DYARIM));
                            }
                            if( otobus_box.get_filtre_data(Otobus_Box_Filtre.FD_NOT) ) filo_header_data.ekle( Otobus_Box_Filtre.FD_NOT, otobus_box.get_ekstra_ozet().get(Otobus_Box_Filtre.FD_NOT));
                            if( otobus_box.get_filtre_data(Otobus_Box_Filtre.FD_IYS) ) filo_header_data.ekle( Otobus_Box_Filtre.FD_IYS, otobus_box.get_ekstra_ozet().get(Otobus_Box_Filtre.FD_IYS));
                            if( otobus_box.get_filtre_data(Otobus_Box_Filtre.FD_PLAKA) ) filo_header_data.arttir( Otobus_Box_Filtre.FD_PLAKA);
                            canli_durum.arttir( otobus_box.get_durum() );
                            header_rapor_data.km_arttir(otobus_box.get_gitas_km(), otobus_box.get_iett_km());
                        }

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                if( FILTRE_INIT ){
                                    // ilk veriyi aldiktan sonra kayitli filtreyi uygula
                                    Otobus_Box_Filtre otobus_box_filtre = controller.get_filtre_obj();
                                    Otobus_Box_Filtre_Data filtre_data = new Otobus_Box_Filtre_Data();
                                    filtre_data.set_str_vals( otobus_box_filtre.get_filtre_data() );
                                    otobus_kutular_filtre( otobus_box_filtre.get_kapi(), filtre_data );
                                    FILTRE_INIT = false;
                                    controller.enable_filtre_btn_container();
                                } else {
                                    otobus_kutular_filtre(prev_filtre_kapi, prev_filtre_data);
                                    controller.header_ozet_guncelle( header_rapor_data, canli_durum, filo_header_data );
                                }
                            }
                        });
                    }
                    try {
                        Thread.sleep( sleep );
                    } catch( InterruptedException e ){
                        e.printStackTrace();
                    }
                }

            }
        });
        th.setDaemon(true);
        th.start();
    }

    private void otobus_kutular_filtre( String kapi, Otobus_Box_Filtre_Data filtre_data ){
        boolean lojik;
        //System.out.println(filtre_data.get_str_vals());
        prev_filtre_data = filtre_data;
        prev_filtre_kapi = kapi;
        Otobus_Box otobus_box;
        ArrayList<String> filtre_str_vals;
        for (Map.Entry<String, Otobus_Box> entry : otobus_kutular.entrySet()) {
            otobus_box = entry.getValue();
            filtre_str_vals = filtre_data.get_str_vals();
            if (kapi.equals(Otobus_Box_Filtre.VE)) {
                lojik = filtre_str_vals.contains("D" + otobus_box.get_durum()) &&
                        filtre_str_vals.contains(entry.getKey().substring(0, 1));
                if (filtre_str_vals.contains(Otobus_Box_Filtre.FD_NOT))
                    lojik = lojik && otobus_box.get_filtre_data(Otobus_Box_Filtre.FD_NOT);
                if (filtre_str_vals.contains(Otobus_Box_Filtre.FD_PLAKA))
                    lojik = lojik && otobus_box.get_filtre_data(Otobus_Box_Filtre.FD_PLAKA);
                if (filtre_str_vals.contains(Otobus_Box_Filtre.FD_IYS))
                    lojik = lojik && otobus_box.get_filtre_data(Otobus_Box_Filtre.FD_IYS);
                if (filtre_str_vals.contains(Otobus_Box_Filtre.FD_ZAYI))
                    lojik = lojik && otobus_box.get_filtre_data(Otobus_Box_Filtre.FD_ZAYI);
            } else {
                lojik = filtre_str_vals.contains("D" + otobus_box.get_durum()) ||
                        filtre_str_vals.contains(entry.getKey().substring(0, 1)) ||
                        (filtre_str_vals.contains(Otobus_Box_Filtre.FD_NOT) && otobus_box.get_filtre_data(Otobus_Box_Filtre.FD_NOT)) &&
                                (filtre_str_vals.contains(Otobus_Box_Filtre.FD_PLAKA) && otobus_box.get_filtre_data(Otobus_Box_Filtre.FD_PLAKA)) &&
                                (filtre_str_vals.contains(Otobus_Box_Filtre.FD_IYS) && otobus_box.get_filtre_data(Otobus_Box_Filtre.FD_IYS)) &&
                                (filtre_str_vals.contains(Otobus_Box_Filtre.FD_ZAYI) && otobus_box.get_filtre_data(Otobus_Box_Filtre.FD_ZAYI));
            }
            if( lojik ){
                try {
                    controller.add_otobus_box(entry.getValue().get_ui());
                } catch (IllegalArgumentException | NullPointerException e) {}
            } else {
                controller.remove_otobus_box( entry.getKey() );
            }
        }
    }

    private void plaka_kontrol_thread(){

        plaka_kontrol_thread = new Thread(new Runnable() {
            private boolean shutdown = false;
            private Web_Request req, req_komple;
            private long komple_update_timestamp = 0;

            @Override
            public void run() {
                while( !shutdown ){
                    req = new Web_Request(Web_Request.SERVIS_URL, "&req=plaka_kontrol_toplu");
                    req.kullanici_pc_parametreleri_ekle();
                    req.action();
                    JSONArray degisimler = new JSONObject(req.get_value()).getJSONObject("data").getJSONArray("plaka_degisimler");
                    JSONObject item;
                    for( int j = 0; j < degisimler.length(); j++ ){
                        item = degisimler.getJSONObject(j);
                        otobus_kutular.get( item.getString("kapi_kodu") ).plakalari_guncelle( item.getString("aktif_plaka"), item.getString("ruhsat_plaka") );
                    }

                    if( Common.get_unix() - komple_update_timestamp >= 120 ){
                        req_komple = new Web_Request(Web_Request.SERVIS_URL, "&req=plaka_komple_kontrol_toplu");
                        req_komple.kullanici_pc_parametreleri_ekle();
                        req_komple.action();
                        JSONArray plaka_data = new JSONObject(req_komple.get_value()).getJSONObject("data").getJSONArray("plaka_data");
                        for( int j = 0; j < plaka_data.length(); j++ ){
                            item = plaka_data.getJSONObject(j);
                            otobus_kutular.get( item.getString("kapi_kodu") ).plakalari_guncelle( item.getString("aktif_plaka"), item.getString("ruhsat_plaka") );
                        }
                        komple_update_timestamp = Common.get_unix();
                    }

                    try {
                        Thread.sleep(30000 );
                    } catch( InterruptedException e ){
                        e.printStackTrace();
                    }
                }
            }
        });
        plaka_kontrol_thread.setDaemon(true);
        plaka_kontrol_thread.start();



    }





}
