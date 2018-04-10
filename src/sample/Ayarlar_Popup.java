package sample;

import com.sun.prism.image.Coords;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * Created by Jeppe on 18.04.2017.
 */
public class Ayarlar_Popup {

    private TabPane ayarlar_tab;
    private Obarey_Popup popup;
    private Node root;
    private double x,y;

    private Obarey_Popup ekle_popup;

    private ArrayList<Ayarlar_Listener> listeners = new ArrayList<>();
    private int anchorx = 0, anchory = 0;

    public void add_listener( Ayarlar_Listener listener ){
        listeners.add( listener );
    }

    public void init( Node root ){

        this.root = root;
        ayarlar_tab = new TabPane();
        ayarlar_tab.getTabs().add( genel_ayarlar_init( true ) );
        //ayarlar_tab.getTabs().add( otobus_ayarlar_init( false ) );
        //ayarlar_tab.getTabs().add( hat_ayarlar_init( false ) );

        // tablarin icerigini aktive edildiginde olusturuyoruz
        /*ayarlar_tab.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
                if (newValue.getContent() == null) {
                    String id = newValue.getId();
                    if( id.equals("genel") ){
                        newValue.setContent( genel_ayarlar_init( true ).getContent() );
                    } else if( id.equals("otobusler") ){
                        newValue.setContent( otobus_ayarlar_init( true ).getContent() );
                    } else if( id.equals("hatlar") ){
                        newValue.setContent( hat_ayarlar_init( true ).getContent() );
                    }
                }
            }
        });*/
        popup = new Obarey_Popup("Ayarlar", root );
        popup.init( true );
        popup.set_content( ( ayarlar_tab ));

    }

    public void show( double x, double y ){
        this.x = x;
        this.y = y;
        popup.show( x, y );
    }

    public boolean is_on(){
        return popup.ison();
    }


    public Tab otobus_ayarlar_init( boolean icerik ){
        Tab tab = new Tab("Otobüsler");
        tab.setClosable(false);
        tab.setId("otobusler");
        if( !icerik ) return tab;

        VBox main = new VBox();


        AnchorPane otobusler_container = new AnchorPane();
        otobusler_container.setPrefWidth(500);
        otobusler_container.setMinHeight(500);

        ArrayList<String> keys = new ArrayList<>();
        Map<String, Integer> data_index = new HashMap<>();
        for( int k = 0; k < User_Config.app_otobusler.length(); k++ ){
            keys.add(User_Config.app_otobusler.getJSONObject(k).getString("kapi_kodu"));
            data_index.put(User_Config.app_otobusler.getJSONObject(k).getString("kapi_kodu"), k);
        }
        Collections.sort(keys, new Comparator<String>() {
            @Override
            public int compare(String str1, String str2) {
                return  str1.compareTo(str2);
            }
        });

        JSONObject otobus_object;



        int hline = 0;
        for( int j = 0; j < keys.size(); j++ ) {
            otobus_object = User_Config.app_otobusler.getJSONObject(data_index.get(keys.get(j)));
            String kod = otobus_object.getString("kapi_kodu");
            final Otobus_Item item = new Otobus_Item( kod, anchorx, anchory );

            // 7 col olacak
            if( hline == 6 ){
                anchory += 30;
                anchorx = 0;
                hline = 0;
            } else {
                anchorx += 100;
                hline++;
            }

            final Common.Delta dragDelta = new Common.Delta();
            item.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent mouseEvent) {
                    // record a delta distance for the drag and drop operation.
                    dragDelta.x = item.getX() - mouseEvent.getSceneX();
                    dragDelta.y = item.getY() - mouseEvent.getSceneY();
                    item.setCursor(Cursor.MOVE);
                }
            });
            item.setOnMouseReleased(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent mouseEvent) {
                    item.setCursor(Cursor.HAND);
                }
            });
            item.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent mouseEvent) {

                    double drag_x = (mouseEvent.getSceneX() + dragDelta.x);
                    double drag_y = (mouseEvent.getSceneY() + dragDelta.y);


                    //System.out.println("DragX-> " + drag_x  +  "  XPrev-> " + item.getXPrev() );
                    //System.out.println("DragY-> " + drag_y  +  "  YPrev-> " + item.getYPrev() );

                    System.out.println(item.getXPrev()-drag_x);
                    if( item.getXPrev() > drag_x ){

                        // sola kayiyor
                        System.out.println("SOLA KAYIYOR");

                        item.inc_xlimit();
                        System.out.println(item.get_xlimit());
                        if( item.get_xlimit() >= 30 ){
                            if( item.getXPrev() > 100 ){
                                // 0 dan sola 100 gitmiyoruz
                                item.setX( item.getXPrev() - 100 );
                            }
                            item.reset_xlimit();
                        }


                    } else {
                        System.out.println("SAGA KAYIYOR");
                        // saga kayiyior

                        item.inc_xlimit();
                        System.out.println(item.get_xlimit());
                        if( item.get_xlimit() >= 30 ){
                            if( item.getXPrev() < 500 ){
                                item.setX(item.getXPrev() + 100 );
                            }
                            item.reset_xlimit();
                        }

                    }
                    item.setXPrev(drag_x);
                    /*item.setX(drag_x);
                    item.setY(drag_y);*/
                }
            });
            item.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent mouseEvent) {
                    item.setCursor(Cursor.HAND);
                }
            });


            otobusler_container.getChildren().add( item );
        }




        main.getChildren().add( otobusler_container );

        tab.setContent(main);
        return tab;

    }

    public Tab genel_ayarlar_init( boolean icerik ){

        Tab tab = new Tab("Genel");
        tab.setClosable(false);
        tab.setId("genel");
        if( !icerik ) return tab;

        System.out.println( User_Config.app_ayarlar.getString("izinler") );

        Ayarlar_Genel_Data ayarlar_data = new Ayarlar_Genel_Data( User_Config.app_ayarlar.getString("alarmlar"), 0, 0, 0 );
        ScrollPane main = new ScrollPane();
        main.getStyleClass().addAll("ayarlar-tab", "tab-gri-bg");
        main.setPadding(new Insets(15, 15, 15 , 15));
        HBox orer_container = new HBox();
        orer_container.setPadding(new Insets(10, 0, 10, 0) );

        VBox frekans_ayarlar = new VBox();
        frekans_ayarlar.setSpacing(25);
        orer_container.setSpacing(25);
        Label orer_header = new Label("Bildirim & Veri Alma ( Saniye )");
        orer_header.getStyleClass().addAll("cek", "fs13", "fbold");
        frekans_ayarlar.getChildren().add( orer_header );

        HBox orer_frekans_input_cont = new HBox();
        orer_frekans_input_cont.setAlignment(Pos.CENTER);
        orer_frekans_input_cont.setSpacing(5);
        Label orer_lbl = new Label("Alarm Frekans");
        orer_lbl.getStyleClass().addAll("cbeyaz", "fs11", "fbold");
        orer_lbl.setPrefWidth(100);
        final TextField alarm_freakans = new TextField();
        alarm_freakans.getStyleClass().addAll("input-kucuk", "grigrad");
        alarm_freakans.setText(String.valueOf(User_Config.app_ayarlar.getInt("alarm_frekans")));
        orer_frekans_input_cont.getChildren().addAll( orer_lbl, alarm_freakans );
        frekans_ayarlar.getChildren().add( orer_frekans_input_cont );

        orer_frekans_input_cont = new HBox();
        orer_frekans_input_cont.setAlignment(Pos.CENTER);
        orer_frekans_input_cont.setSpacing(5);
        orer_lbl = new Label("Alarm Kaybolma ( 0: iptal )");
        orer_lbl.setWrapText(true);
        orer_lbl.getStyleClass().addAll("cbeyaz", "fs11", "fbold");
        orer_lbl.setPrefWidth(100);
        final TextField alarm_kaybolma_frekans = new TextField();
        alarm_kaybolma_frekans.getStyleClass().addAll("input-kucuk", "grigrad");
        alarm_kaybolma_frekans.setText(String.valueOf(User_Config.app_ayarlar.getInt("alarm_kaybolma_frekans")));
        orer_frekans_input_cont.getChildren().addAll( orer_lbl, alarm_kaybolma_frekans );
        frekans_ayarlar.getChildren().add( orer_frekans_input_cont );


        /*

        HBox orer_frekans_input_cont = new HBox();
        orer_frekans_input_cont.setAlignment(Pos.CENTER);
        orer_frekans_input_cont.setSpacing(5);
        Label orer_lbl = new Label("ORER Frekans");
        orer_lbl.getStyleClass().addAll("cbeyaz", "fs11", "fbold");
        orer_lbl.setPrefWidth(100);
        final TextField orer_frekans_input = new TextField();
        orer_frekans_input.getStyleClass().addAll("input-kucuk", "grigrad");
        orer_frekans_input.setText(String.valueOf(ayarlar_data.get_orer_frekans()));
        orer_frekans_input_cont.getChildren().addAll( orer_lbl, orer_frekans_input );
        frekans_ayarlar.getChildren().add( orer_frekans_input_cont );

        orer_frekans_input_cont = new HBox();
        orer_frekans_input_cont.setAlignment(Pos.CENTER);
        orer_frekans_input_cont.setSpacing(5);
        orer_lbl = new Label("Mesaj Frekans");
        orer_lbl.getStyleClass().addAll("cbeyaz", "fs11", "fbold");
        orer_lbl.setPrefWidth(100);
        final TextField mesaj_frekans_input = new TextField();
        mesaj_frekans_input.getStyleClass().addAll("input-kucuk", "grigrad");
        mesaj_frekans_input.setText(String.valueOf(ayarlar_data.get_mesaj_frekans()));
        orer_frekans_input_cont.getChildren().addAll( orer_lbl, mesaj_frekans_input );
        frekans_ayarlar.getChildren().add( orer_frekans_input_cont );


        orer_frekans_input_cont = new HBox();
        orer_frekans_input_cont.setAlignment(Pos.CENTER);
        orer_frekans_input_cont.setSpacing(5);
        orer_lbl = new Label("IYS Frekans");
        orer_lbl.getStyleClass().addAll("cbeyaz", "fs11", "fbold");
        orer_lbl.setPrefWidth(100);
        final TextField iys_frekans_input = new TextField();
        iys_frekans_input.setText(String.valueOf(ayarlar_data.get_iys_frekans()));
        iys_frekans_input.getStyleClass().addAll("input-kucuk", "grigrad");
        orer_frekans_input_cont.getChildren().addAll( orer_lbl, iys_frekans_input );
        frekans_ayarlar.getChildren().add( orer_frekans_input_cont );*/

        VBox orer_alarm_cont = new VBox();
        orer_alarm_cont.setSpacing(25);
        orer_header = new Label("Alarmlar");
        orer_header.getStyleClass().addAll("cek", "fs13", "fbold");

        HBox alarm_cb_container = new HBox();
        VBox cb_grup_1 = new VBox();
        VBox cb_grup_2 = new VBox();
        VBox cb_grup_3 = new VBox();
        VBox cb_grup_4 = new VBox();
        VBox cb_grup_5 = new VBox();
        cb_grup_1.setSpacing(10);
        cb_grup_2.setSpacing(10);
        cb_grup_3.setSpacing(10);
        cb_grup_4.setSpacing(10);
        cb_grup_5.setSpacing(10);

        final CheckBox cb_iptal = new CheckBox("İptal Sefer");
        final CheckBox cb_yarim = new CheckBox("Yarım Sefer");
        final CheckBox cb_sefer_duzeltildi = new CheckBox("Seferler Düzeltildi");
        final CheckBox cb_surucu_degisimi = new CheckBox("Sürücü Değişimi");
        final CheckBox cb_surucu_bilgisi_yok = new CheckBox("Sürücü Bilgisi Yok");
        final CheckBox cb_surucu_cok_calisti = new CheckBox("Sürücü Çalışma Süre");
        final CheckBox cb_gec_kalabilir = new CheckBox("Geç Kalabilir");
        final CheckBox cb_seferine_baslamadi = new CheckBox("Seferine Başlamadı");
        final CheckBox cb_amir_saat = new CheckBox("Amir Saat");
        final CheckBox cb_yeni_not = new CheckBox("Yeni Not");
        final CheckBox cb_not_tamamlandi = new CheckBox("Not Tamamlandı");
        final CheckBox cb_not_bildirim_eklendi = new CheckBox("Not Bildirim Eklendi");
        final CheckBox cb_iys_uyari = new CheckBox("IYS Uyarı");

        if( ayarlar_data.alarm_cb_kontrol(Alarm_Data.SEFER_IPTAL) ) cb_iptal.setSelected(true);
        if( ayarlar_data.alarm_cb_kontrol(Alarm_Data.SEFER_YARIM) ) cb_yarim.setSelected(true);
        if( ayarlar_data.alarm_cb_kontrol(Alarm_Data.SEFERLER_DUZELTILDI) ) cb_sefer_duzeltildi.setSelected(true);
        if( ayarlar_data.alarm_cb_kontrol(Alarm_Data.SURUCU_DEGISIMI) ) cb_surucu_degisimi.setSelected(true);
        if( ayarlar_data.alarm_cb_kontrol(Alarm_Data.BELIRSIZ_SURUCU) ) cb_surucu_bilgisi_yok.setSelected(true);
        if( ayarlar_data.alarm_cb_kontrol(Alarm_Data.GEC_KALMA) ) cb_gec_kalabilir.setSelected(true);
        if( ayarlar_data.alarm_cb_kontrol(Alarm_Data.SEFER_BASLAMADI) ) cb_seferine_baslamadi.setSelected(true);
        if( ayarlar_data.alarm_cb_kontrol(Alarm_Data.AMIR_SAAT_ATADI) ) cb_amir_saat.setSelected(true);
        if( ayarlar_data.alarm_cb_kontrol(Alarm_Data.NOT_VAR) ) cb_yeni_not.setSelected(true);
        if( ayarlar_data.alarm_cb_kontrol(Alarm_Data.NOT_TAMAMLANDI) ) cb_not_tamamlandi.setSelected(true);
        if( ayarlar_data.alarm_cb_kontrol(Alarm_Data.YENI_NOT_BILDIRIMI) ) cb_not_bildirim_eklendi.setSelected(true);
        if( ayarlar_data.alarm_cb_kontrol(Alarm_Data.IYS_UYARISI_VAR) ) cb_iys_uyari.setSelected(true);
        if( ayarlar_data.alarm_cb_kontrol(Alarm_Data.SURUCU_COK_CALISTI) ) cb_surucu_cok_calisti.setSelected(true);

        cb_grup_1.getChildren().addAll(cb_iptal, cb_yarim, cb_sefer_duzeltildi );
        cb_grup_2.getChildren().addAll(cb_surucu_degisimi, cb_surucu_bilgisi_yok, cb_gec_kalabilir );

        cb_grup_3.getChildren().addAll(cb_seferine_baslamadi, cb_amir_saat, cb_yeni_not );
        cb_grup_4.getChildren().addAll(cb_not_tamamlandi, cb_not_bildirim_eklendi, cb_iys_uyari );
        cb_grup_5.getChildren().addAll( cb_surucu_cok_calisti );
        alarm_cb_container.setPrefWidth(650);
        alarm_cb_container.setSpacing(15);
        alarm_cb_container.getChildren().addAll( cb_grup_1, cb_grup_2, cb_grup_3, cb_grup_4, cb_grup_5);
        orer_alarm_cont.getChildren().addAll( orer_header, alarm_cb_container );

        orer_container.getChildren().addAll( frekans_ayarlar, orer_alarm_cont );
        GButton kaydet = new GButton("KAYDET", GButton.CMORB );
        main.setContent( new VBox( orer_container, kaydet ) );

        kaydet.setOnMousePressed(ev->{
            kaydet.setDisable(true);
            final String alarm_frekans_val = alarm_freakans.getText(),
                    alarm_kaybolma_frekans_val = alarm_kaybolma_frekans.getText();
            /*final String  orer_frekans_val = orer_frekans_input.getText(),
                    mesaj_frekans_val = mesaj_frekans_input.getText(),
                    iys_frekans_val = iys_frekans_input.getText();
            if( !Common.is_numeric( orer_frekans_val ) || !Common.is_numeric( mesaj_frekans_val ) || !Common.is_numeric( iys_frekans_val ) ){
                kaydet.setDisable(false);
                kaydet.setText("Kaydet");
                return;
            }*/
            if( alarm_frekans_val.equals("") || !Common.is_numeric(alarm_frekans_val) || alarm_kaybolma_frekans_val.equals("") || !Common.is_numeric(alarm_kaybolma_frekans_val)  ) return;
            kaydet.setText("Kaydediliyor...");
            String alarm_str = "";
            alarm_str += alarm_str_olustur(cb_iptal.isSelected());
            alarm_str += alarm_str_olustur(cb_yarim.isSelected());
            alarm_str += alarm_str_olustur(cb_sefer_duzeltildi.isSelected());
            alarm_str += alarm_str_olustur(cb_surucu_degisimi.isSelected());
            alarm_str += alarm_str_olustur(cb_surucu_bilgisi_yok.isSelected());
            alarm_str += alarm_str_olustur(cb_gec_kalabilir.isSelected());
            alarm_str += alarm_str_olustur(cb_seferine_baslamadi.isSelected());
            alarm_str += alarm_str_olustur(cb_amir_saat.isSelected());
            alarm_str += alarm_str_olustur(cb_yeni_not.isSelected());
            alarm_str += alarm_str_olustur(cb_not_tamamlandi.isSelected());
            alarm_str += alarm_str_olustur(cb_not_bildirim_eklendi.isSelected());
            alarm_str += alarm_str_olustur(cb_iys_uyari.isSelected());
            alarm_str += alarm_str_olustur(cb_surucu_cok_calisti.isSelected());
            final String alarm_str_final = alarm_str;

            Thread kaydet_th = new Thread( new Task<Void>(){
                private Web_Request request;
                @Override
                protected void succeeded(){
                    JSONObject output = new JSONObject(request.get_value());
                    int ok = output.getInt("ok");
                    if( ok == 1 ){
                        String izinler_cache = User_Config.app_ayarlar.getString("izinler");
                        JSONObject new_data = new JSONObject();
                        new_data.put("alarmlar", alarm_str_final );
                        new_data.put("izinler",  izinler_cache );
                        new_data.put("alarm_frekans", alarm_frekans_val );
                        new_data.put("alarm_kaybolma_frekans", alarm_kaybolma_frekans_val );
                        User_Config.init_app_data("ayarlar_data", new_data );
                        for( Ayarlar_Listener listener : listeners ) {
                            listener.alarm_ayarlar_degisim( alarm_str_final );
                            listener.frekans_ayarlar_degisim();
                        }
                    }
                    kaydet.setDisable(false);
                    kaydet.setText("Kaydet");
                }
                @Override
                protected Void call(){

                    request = new Web_Request(Web_Request.SERVIS_URL, "&req=ayar_guncelleme&alarmlar="+alarm_str_final+"&orer_frekans=0&mesaj_frekans=0&iys_frekans=0&alarm_frekans="+alarm_frekans_val+"&alarm_kaybolma_frekans="+alarm_kaybolma_frekans_val );
                    request.kullanici_pc_parametreleri_ekle();
                    request.action();

                    return null;
                }
            });
            kaydet_th.setDaemon(true);
            kaydet_th.start();

        });

        tab.setContent( main );
        return tab;
    }

    private String alarm_str_olustur( boolean val ){
        if( val ) return "1";
        return "0";
    }



    public Tab hat_ayarlar_init( boolean icerik ){
        Tab tab = new Tab("Hatlar");
        tab.setClosable(false);
        tab.setId("hatlar");
        if( !icerik ) return tab;


        Label tab_header = new Label("Hatlar");
        tab_header.getStyleClass().addAll("fbold", "cbeyaz", "fs14");
        tab.setContent( tab_header );


        return tab;

    }

}

class Otobus_Item extends HBox{
    private String oto;
    private CheckBox cb;
    private double x, y, xprev, yprev, xlimit, ylimit;
    public Otobus_Item( String oto, double _x, double _y ){
        super();
        this.oto = oto;
        setId(oto);
        setSpacing(5);
        setPadding(new Insets(5, 10, 5, 10 ) );
        getStyleClass().add("otobus-item-bg");
        setAlignment(Pos.CENTER);
        Label oto_lbl = new Label( oto );
        oto_lbl.setPrefWidth(50);
        oto_lbl.getStyleClass().addAll("fbold", "fs11", "cbeyaz");
        cb = new CheckBox();
        getChildren().addAll( oto_lbl, cb );

        System.out.println(oto +  " X: " + _x + " Y: " + _y );

        x = _x;
        y = _y;
        xprev = _x;
        yprev = _y;
        xlimit = 0;
        ylimit = 0;

        AnchorPane.setLeftAnchor( this, x );
        AnchorPane.setTopAnchor( this, y );


    }

    public void inc_ylimit(){
        ylimit++;
    }
    public void inc_xlimit(){
        xlimit++;
    }
    public void reset_xlimit(){
        xlimit = 0;
    }
    public void reset_ylimit(){
        ylimit = 0;
    }
    public double get_xlimit(){
        return xlimit;
    }
    public double get_ylimit(){
        return ylimit;
    }

    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }
    public double getXPrev(){
        return xprev;
    }
    public double getYPrev(){
        return yprev;
    }
    public void setXPrev( double xp ){
        xprev = xp;
    }
    public void setYPrev( double yp ){
        yprev = yp;
    }
    public void setX( double _x ){
        x = _x;
        AnchorPane.setLeftAnchor(this, x);
    }

    public void setY( double _y ){
        y = _y;
        AnchorPane.setTopAnchor(this, y);
    }


}