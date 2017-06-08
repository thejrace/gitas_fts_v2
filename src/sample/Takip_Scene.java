package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader;
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

        // pencere boyutuna gore kutulari dizme event
        controller.screen_resize_cb();

        otobus_kutular_init();

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


        Filo_Download filo_download = new Filo_Download();
        filo_download.add_listener(new Filo_Download_Listener() {
            @Override
            public void on_download_finish( JSONObject data ) {
                Otobus_Box kutu;
                JSONObject otobus_data;
                JSONObject seferler = data.getJSONObject("filo");
                User_Config.config_guncelle( data.getString("default_config_json"));
                for (Map.Entry<String, Otobus_Box> entry : otobus_kutular.entrySet()) {
                    kutu = entry.getValue();
                    otobus_data = seferler.getJSONObject(entry.getKey());
                    try{
                        kutu.update( otobus_data.getJSONArray("orer"), otobus_data.getString("durak_bilgisi"), otobus_data.getJSONObject("alarmlar") );
                    } catch( Exception e ){
                        e.printStackTrace();
                    }
                }
                orer_alarmlar.clear();
                orer_download_ui();
            }
        });
        filo_download.start();

        controller.add_ayarlar_listener(new Ayarlar_Listener() {
            @Override
            public void alarm_ayarlar_degisim( String yeni_str ) {
                for (Map.Entry<String, Otobus_Box> entry : otobus_kutular.entrySet()) {
                    entry.getValue().alarmlari_ayikla(yeni_str);
                }
            }
            @Override
            public void frekans_ayarlar_degisim(){
                //GENEL_AYARLAR = User_Config.genel_ayarlar();
            }
        });

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                Platform.exit();
                System.exit(0);
            }
        });

    }

    private void orer_download_ui(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                controller.orer_senkron_guncelle(Common.get_current_hmin());
                if( FILTRE_INIT ){
                    // ilk veriyi aldiktan sonra kayitli filtreyi uygula
                    Otobus_Box_Filtre otobus_box_filtre = controller.get_filtre_obj();
                    Otobus_Box_Filtre_Data filtre_data = new Otobus_Box_Filtre_Data();
                    filtre_data.set_str_vals( otobus_box_filtre.get_filtre_data() );
                    otobus_kutular_filtre( otobus_box_filtre.get_kapi(), filtre_data );
                    FILTRE_INIT = false;
                    controller.enable_filtre_btn_container();
                }
            }
        });
    }

    private void otobus_kutular_filtre( String kapi, Otobus_Box_Filtre_Data filtre_data ){
        boolean lojik;
        System.out.println(filtre_data.get_str_vals());
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

    private void gunluk_ozet(){

        if( OTOBUS_SAYAC == otobus_kutular.size() ){
            System.out.println( orer_alarmlar );
            controller.alarm_popup( orer_alarmlar );
            otobus_kutular_filtre( prev_filtre_kapi, prev_filtre_data );
            controller.header_ozet_guncelle( header_rapor_data, canli_durum, filo_header_data );
            canli_durum = new Sefer_Rapor_Data(0, 0, 0, 0, 0 );
            header_rapor_data = new Sefer_Rapor_Data("", 0, 0, 0, 0, 0, 0, 0,0,0 );
            filo_header_data = new Filo_Rapor_Data();
            OTOBUS_SAYAC = 0;
        }
    }


    private void otobus_kutular_init(){
        temp_elem_index = 0;
        try {
            JSONObject config = User_Config.config_oku();
            JSONArray otobusler = config.getJSONArray("otobusler");
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
                        orer_alarmlar.put(kod, yeni_alarmlar);
                        for (Map.Entry<String, Integer> entry : box_item.get_ozet().entrySet()) for( int k = 0; k < entry.getValue(); k++ ) header_rapor_data.arttir( entry.getKey() );
                        if( box_item.get_filtre_data(Otobus_Box_Filtre.FD_ZAYI) ){
                            filo_header_data.ekle( Otobus_Box_Filtre.FD_ZAYI, box_item.get_ozet().get(Sefer_Data.DIPTAL));
                            filo_header_data.ekle( Otobus_Box_Filtre.FD_ZAYI, box_item.get_ozet().get(Sefer_Data.DYARIM));
                        }
                        if( box_item.get_filtre_data(Otobus_Box_Filtre.FD_NOT) ) filo_header_data.ekle( Otobus_Box_Filtre.FD_NOT, box_item.get_ekstra_ozet().get(Otobus_Box_Filtre.FD_NOT));
                        if( box_item.get_filtre_data(Otobus_Box_Filtre.FD_IYS) ) filo_header_data.ekle( Otobus_Box_Filtre.FD_IYS, box_item.get_ekstra_ozet().get(Otobus_Box_Filtre.FD_IYS));
                        if( box_item.get_filtre_data(Otobus_Box_Filtre.FD_PLAKA) ) filo_header_data.arttir( Otobus_Box_Filtre.FD_PLAKA);
                        canli_durum.arttir( box_item.get_durum() );
                        OTOBUS_SAYAC++;
                        gunluk_ozet();
                    }
                });
                controller.add_otobus_box(box_item.get_ui());
                temp_elem_index++;
            }
        } catch( JSONException e ){
            e.printStackTrace();
        }
    }
}
