package sample;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jeppe on 19.05.2017.
 */
public class Otobus_Box {

    private int index;
    private String kod, ruhsat_plaka, aktif_plaka;

    private String hat = "#", aktif_guzergah = "";
    private JSONArray new_data = new JSONArray();
    private JSONArray old_data = new JSONArray();
    private Map<String, Alarm_Data> alarmlar = new HashMap<>();
    private Label   main_info,
                    info,
                    ozet_tamam,
                    ozet_aktif,
                    ozet_bekleyen,
                    ozet_iptal,
                    ozet_yarim,
                    box_header_hat,
                    box_header_plaka;

    private Filo_Table sefer_plan_table;
    //private Surucu_Box surucu_box;
    //private Rapor_Tarih_Filtre_Box rapor_filtre_box;
    private Popup_Mesaj_Box popup_mesaj_box;
    private Popup_IYS_Box popup_iys_box;
    //private Popup_Not_Box popup_not_box;

    private Circle led;
    private Button btn_sefer, btn_surucu, btn_alarm, btn_mesaj, btn_harita, btn_rapor, btn_iys, btn_notlar;
    private Map<String, Integer> sefer_ozet = new HashMap<>();
    private Map<String, Integer> ekstra_ozet = new HashMap<>();

    private VBox ui_container;
    private VBox box_content;
    private Map<String, Boolean> filtre_data = new HashMap<>();

    private Obarey_Popup    sefer_popup,
            surucu_popup,
            mesaj_popup,
            iys_popup,
            harita_popup,
            rapor_popup,
            plaka_popup,
            not_popup;



    private String  ui_led,
                    ui_notf,
                    ui_main_notf,
                    ui_hat_data;

    private ArrayList<String> suruculer = new ArrayList<>();
    private final ArrayList<Alarm_Listener> listeners = new ArrayList<Alarm_Listener>();

    public Otobus_Box( String kod, int index, String ruhsat_plaka, String aktif_plaka ){
        this.kod = kod;
        this.index = index;
        this.ruhsat_plaka = ruhsat_plaka;
        this.aktif_plaka = aktif_plaka;
        filtre_data.put(Otobus_Box_Filtre.FD_IYS, false);
        filtre_data.put(Otobus_Box_Filtre.FD_NOT, false);
        filtre_data.put(Otobus_Box_Filtre.FD_PLAKA, false);
        filtre_data.put(Otobus_Box_Filtre.FD_ZAYI, false);

        ekstra_ozet.put(Otobus_Box_Filtre.FD_NOT, 0);
        ekstra_ozet.put(Otobus_Box_Filtre.FD_IYS, 0);

        create_ui();
        set_event_handlers();
    }

    private void create_ui(){
        String  CLASS_OTOBUS_BOX            = "otobus-takip-box",
                CLASS_OTOBUS_BOX_HEADER     = "otobus-takip-box-header",
                CLASS_OTOBUS_BOX_CONTENT    = "otobus-takip-box-content",
                CLASS_DURUM_LED             = "durum-led",
                CLASS_HEADER_LABEL          = "otobus-takip-header-label",
                CLASS_BOX_INFO              = "box-info",
                CLASS_BOX_MAIN_INFO         = "box-main-info",
                CLASS_BOX_OZET_CONTAINER    = "box-ozet-container",
                CLASS_BOX_OZET_ITEM         = "box-ozet-item",
                CLASS_BOX_ALT_NAV           = "box-alt-nav-container",
                CLASS_BOX_NAV_ITEM          = "box-nav-item",
                CLASS_NAV_BTN               = "box-nav-btn",
                ID_DURUM_LED                = "durum_led",
                ID_MAIN_INFO                = "main_info",
                ID_INFO                     = "info",
                ID_OZET                     = "box_ozet",
                ID_OTOBUS_HAT               = "hat";

        // [1] ana container
        ui_container = new VBox();
        ui_container.setId( this.kod );
        ui_container.getStyleClass().add(CLASS_OTOBUS_BOX);

        // [1][1] box header
        AnchorPane box_header = new AnchorPane();
        box_header.getStyleClass().add( CLASS_OTOBUS_BOX_HEADER );
        // [1][1][1] durum led
        led = new Circle( 11 );
        led.getStyleClass().addAll( CLASS_DURUM_LED, CLASS_DURUM_LED + "-default");
        led.setId( ID_DURUM_LED );
        // [1][1][2] otobus kapi kodu
        Label box_header_kod = new Label( kod );
        box_header_kod.getStyleClass().add( CLASS_HEADER_LABEL );

        box_header_plaka = new Label(aktif_plaka);
        otobus_plaka_kontrol();

        // [1][1][3] otobus hat
        box_header_hat = new Label( "#" );
        box_header_hat.setId( ID_OTOBUS_HAT );
        box_header_hat.getStyleClass().add( CLASS_HEADER_LABEL );

        // header anchor a ekle hepsini sonra pozisyonlarini ayarla
        box_header.getChildren().addAll( led, box_header_kod, box_header_plaka, box_header_hat);
        // led
        AnchorPane.setTopAnchor( led, 4.0 );
        AnchorPane.setLeftAnchor( led, 5.0 );
        // otobus kapi kodu
        AnchorPane.setTopAnchor( box_header_kod, 5.0);
        AnchorPane.setLeftAnchor( box_header_kod, 32.0);
        AnchorPane.setLeftAnchor( box_header_plaka, 95.0);
        AnchorPane.setTopAnchor( box_header_plaka, 5.0);
        // otobus hat
        AnchorPane.setTopAnchor( box_header_hat, 5.0);
        AnchorPane.setRightAnchor( box_header_hat, 5.0);

        // [1][2] box content
        box_content = new VBox();
        box_content.setAlignment(Pos.CENTER);
        box_content.getStyleClass().add( CLASS_OTOBUS_BOX_CONTENT );

        // [1][2][1] buyuk notf
        main_info = new Label( "Veri bekleniyor...");
        main_info.setId( ID_MAIN_INFO );
        main_info.getStyleClass().add( CLASS_BOX_MAIN_INFO );
        // [1][2][2] kucuk notf ( durak vs. )
        info = new Label( "Veri bekleniyor...");
        info.setId( ID_INFO );
        info.getStyleClass().add( CLASS_BOX_INFO );
        // sigmazsa alt satira gec
        info.setTextOverrun( OverrunStyle.CLIP );

        // [1][2][3] ozet container
        HBox ozet_cont = new HBox();
        ozet_cont.getStyleClass().add( CLASS_BOX_OZET_CONTAINER );
        ozet_cont.setAlignment(Pos.CENTER);
        // [1][2][3][1-5] ozet sefer kutuculari
        ozet_tamam = new Label( "0" );
        ozet_tamam.getStyleClass().addAll( CLASS_BOX_OZET_ITEM, CLASS_BOX_OZET_ITEM+"-tamam" );
        ozet_tamam.setId( ID_OZET+"_tamam");
        ozet_tamam.setAlignment(Pos.CENTER);
        HBox.setMargin( ozet_tamam, new Insets( 0, 5, 0, 5) );

        ozet_aktif = new Label( "0" );
        ozet_aktif.getStyleClass().addAll( CLASS_BOX_OZET_ITEM, CLASS_BOX_OZET_ITEM+"-aktif" );
        ozet_aktif.setId( ID_OZET+"_aktif");
        ozet_aktif.setAlignment(Pos.CENTER);
        HBox.setMargin( ozet_aktif, new Insets( 0, 5, 0, 5) );

        ozet_bekleyen = new Label( "0" );
        ozet_bekleyen.getStyleClass().addAll( CLASS_BOX_OZET_ITEM, CLASS_BOX_OZET_ITEM+"-bekleyen" );
        ozet_bekleyen.setId( ID_OZET+"_bekleyen");
        ozet_bekleyen.setAlignment(Pos.CENTER);
        HBox.setMargin( ozet_bekleyen, new Insets( 0, 5, 0, 5) );

        ozet_iptal = new Label( "0" );
        ozet_iptal.getStyleClass().addAll( CLASS_BOX_OZET_ITEM, CLASS_BOX_OZET_ITEM+"-iptal" );
        ozet_iptal.setId( ID_OZET+"_iptal");
        ozet_iptal.setAlignment(Pos.CENTER);
        HBox.setMargin( ozet_iptal, new Insets( 0, 5, 0, 5) );

        ozet_yarim = new Label( "0" );
        ozet_yarim.getStyleClass().addAll( CLASS_BOX_OZET_ITEM, CLASS_BOX_OZET_ITEM+"-yarim" );
        ozet_yarim.setId( ID_OZET+"_yarim");
        ozet_yarim.setAlignment(Pos.CENTER);
        HBox.setMargin( ozet_yarim, new Insets( 0, 5, 0, 5) );

        // ozet kutucuklarini ekle
        ozet_cont.getChildren().addAll( ozet_tamam, ozet_aktif, ozet_bekleyen, ozet_iptal, ozet_yarim );

        // [1][2][4] nav container
        HBox nav_cont = new HBox();
        nav_cont.getStyleClass().add( CLASS_BOX_ALT_NAV );
        nav_cont.setAlignment(Pos.CENTER);

        HBox nav_cont_2 = new HBox();
        nav_cont_2.getStyleClass().add( CLASS_BOX_ALT_NAV );
        nav_cont_2.setAlignment(Pos.CENTER);

        btn_sefer = new Button( "PLAN");
        btn_sefer.getStyleClass().add( CLASS_NAV_BTN );
        btn_alarm = new Button( "#");
        btn_alarm.getStyleClass().add( CLASS_NAV_BTN );
        btn_mesaj = new Button( "MESAJ");
        btn_mesaj.getStyleClass().add( CLASS_NAV_BTN );
        btn_surucu = new Button( "SÜRÜCÜ");
        btn_surucu.getStyleClass().add( CLASS_NAV_BTN );

        btn_iys = new Button( "IYS");
        btn_iys.getStyleClass().add( CLASS_NAV_BTN );
        btn_notlar = new Button( "NOTLAR");
        btn_notlar.getStyleClass().add( CLASS_NAV_BTN );
        btn_harita = new Button( "HARİTA");
        btn_harita.getStyleClass().add( CLASS_NAV_BTN );
        btn_rapor = new Button( "RAPOR");
        btn_rapor.getStyleClass().add( CLASS_NAV_BTN );

        FlowPane nav_cont_test = new FlowPane();
        //nav_cont_test.setAlignment(Pos.CENTER);
        nav_cont_test.getStyleClass().add( CLASS_BOX_ALT_NAV );
        nav_cont_test.setOrientation(Orientation.HORIZONTAL);

        // butonlari ekle
        //nav_cont.getChildren().addAll( btn_sefer, btn_rapor, btn_mesaj, btn_surucu );
        // nav_cont_2.getChildren().addAll( btn_iys, btn_harita, btn_alarm, btn_gecmis );

        /*if( User_Config.izin_kontrol(User_Config.IOB_PLAN ) ) nav_cont_test.getChildren().add(btn_sefer);
        if( User_Config.izin_kontrol(User_Config.IOB_KMRAPOR) ) nav_cont_test.getChildren().add(btn_rapor);
        if( User_Config.izin_kontrol(User_Config.IOB_MESAJ) ) nav_cont_test.getChildren().add(btn_mesaj);
        if( User_Config.izin_kontrol(User_Config.IOB_SURUCU ) ) nav_cont_test.getChildren().add(btn_surucu);
        if( User_Config.izin_kontrol(User_Config.IOB_IYS ) ) nav_cont_test.getChildren().add(btn_iys);
        if( User_Config.izin_kontrol(User_Config.IOB_HARITA ) ) nav_cont_test.getChildren().add(btn_harita);
        if( User_Config.izin_kontrol(User_Config.IOB_NOTLAR ) ) nav_cont_test.getChildren().add(btn_notlar);*/

        nav_cont_test.getChildren().add(btn_sefer);
        nav_cont_test.getChildren().add(btn_rapor);
        nav_cont_test.getChildren().add(btn_mesaj);
        nav_cont_test.getChildren().add(btn_surucu);
        nav_cont_test.getChildren().add(btn_iys);
        nav_cont_test.getChildren().add(btn_harita);
        nav_cont_test.getChildren().add(btn_notlar);

        // box content elemanlari ekle
        box_content.getChildren().addAll( main_info, info, ozet_cont,nav_cont_test );
        ui_container.getChildren().addAll( box_header, box_content );
    }

    private void set_event_handlers(){

        btn_sefer.setOnMousePressed( ev -> {

            Thread plan_thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Web_Request request = new Web_Request(Web_Request.SERVIS_URL, "&req=orer_download&oto="+kod+"&baslangic=AT&bitis=" );
                    request.kullanici_pc_parametreleri_ekle(true);
                    request.action();
                    new_data = new JSONObject(request.get_value()).getJSONObject("data").getJSONArray("orer_data");

                    if( sefer_popup == null || sefer_plan_table == null ) {
                        sefer_popup = new Obarey_Popup(kod + " Sefer Planı", ui_container.getScene().getRoot());
                        sefer_popup.init(true);
                    }
                    if( sefer_popup.ison() ) return;
                    sefer_plan_table = new Filo_Table( kod );
                    sefer_plan_table.init();
                    sefer_plan_table.update_data( new_data, false );
                    sefer_plan_table.update_ui();
                    sefer_popup.set_content( sefer_plan_table.get() );
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            sefer_popup.show( ev.getScreenX(), ev.getScreenY() );
                        }
                    });
                }
            });
            plan_thread.setDaemon(true);
            plan_thread.start();
        });

    }
    public Map<String, Integer> get_ozet(){
        return sefer_ozet;
    }
    public Map<String, Integer> get_ekstra_ozet(){
        return ekstra_ozet;
    }

    public void add_alarm_listener( Alarm_Listener listener ){
        listeners.add( listener );
    }

    public void alarmlari_ayikla( String yeni_alarm_val ){
        ArrayList<String> silinecekler = new ArrayList<>();
        for (Map.Entry<String, Alarm_Data> entry : alarmlar.entrySet()) if(yeni_alarm_val.charAt(entry.getValue().get_type()) == '0' ) silinecekler.add(entry.getKey());
        for( String key : silinecekler ) alarmlar.remove(key);
    }

    public void update_ui(){

        main_info.setText( ui_main_notf );
        info.setText(ui_notf);
        update_led( ui_led );
        box_header_hat.setText(ui_hat_data);

        this.ozet_tamam.setText( String.valueOf( this.sefer_ozet.get(Sefer_Data.DTAMAM) ) );
        this.ozet_aktif.setText( String.valueOf( this.sefer_ozet.get(Sefer_Data.DAKTIF) ) );
        this.ozet_bekleyen.setText( String.valueOf( this.sefer_ozet.get(Sefer_Data.DBEKLEYEN) ) );
        this.ozet_iptal.setText( String.valueOf( this.sefer_ozet.get(Sefer_Data.DIPTAL) ) );
        this.ozet_yarim.setText( String.valueOf( this.sefer_ozet.get(Sefer_Data.DYARIM) ) );

    }

    public void update( JSONArray data, String durak_data, JSONObject alarmlar ) throws Exception {
        // filo boş veri gonderiyorsa dokunmuyoruz
        if( data.length() == 0 ) return;
        this.new_data = data;
        Thread update_thread = new Thread( new Task<String>(){
            @Override
            protected void succeeded(){
                update_ui();
                // TODO  if( sefer_popup != null && sefer_popup.ison() && !sefer_plan_table.get_eski_veri_goruntuleniyor() ) sefer_plan_table.update_ui();
                for( Alarm_Listener listener : listeners ){
                    listener.on_ui_finished( get_alarmlar() );
                }
            }
            @Override
            protected String call(){
                // popup aciksa ve gecmis goruntulenmiyorsa guncelle popupu
                // TODO if( sefer_popup != null && sefer_popup.ison() && !sefer_plan_table.get_eski_veri_goruntuleniyor() )  sefer_plan_table.update_data( new_data, false );
                try {
                    int o;
                    JSONArray tamamlanan_notlar = alarmlar.getJSONArray("tamamlanan_notlar");
                    JSONArray yeni_notlar = alarmlar.getJSONArray("yeni_notlar");
                    JSONArray iys = alarmlar.getJSONArray("iys");
                    JSONArray not_bildirimleri = alarmlar.getJSONArray("not_bildirimleri");

                    int     yeni_notlar_length = yeni_notlar.length(),
                            tamamlanan_notlar_length = tamamlanan_notlar.length(),
                            iys_length = iys.length(),
                            not_bildirimleri_length = not_bildirimleri.length();

                    if( tamamlanan_notlar_length > 0 ){
                        if( tamamlanan_notlar_length > 5 ){
                            alarm_kontrol( new Alarm_Data( Alarm_Data.NOT_TAMAMLANDI, Alarm_Data.YESIL, kod, tamamlanan_notlar_length + " not tamamlandı!", "-1" ));
                        } else {
                            for( o = 0; o < tamamlanan_notlar_length; o++ ) alarm_kontrol( new Alarm_Data( Alarm_Data.NOT_TAMAMLANDI, Alarm_Data.YESIL, kod, tamamlanan_notlar.getString(o), String.valueOf(o)));
                        }
                    }
                    if( yeni_notlar_length > 0 ){
                        if( yeni_notlar_length > 5 ){
                            alarm_kontrol( new Alarm_Data( Alarm_Data.NOT_VAR, Alarm_Data.MAVI, kod, yeni_notlar_length + " yeni not var!","-1"));
                        } else {
                            for( o = 0; o < yeni_notlar_length; o++ ) alarm_kontrol( new Alarm_Data( Alarm_Data.NOT_VAR, Alarm_Data.MAVI, kod, "Yeni Not '" + yeni_notlar.getString(o) + "'",String.valueOf(o)));
                        }
                        filtre_data.put(Otobus_Box_Filtre.FD_NOT, true);
                        ekstra_ozet.put(Otobus_Box_Filtre.FD_NOT, yeni_notlar_length);
                    } else {
                        filtre_data.put(Otobus_Box_Filtre.FD_NOT, false);
                        ekstra_ozet.put(Otobus_Box_Filtre.FD_NOT, 0);
                    }
                    if( iys_length > 0 ){
                        if( iys_length > 5 ){
                            alarm_kontrol( new Alarm_Data( Alarm_Data.IYS_UYARISI_VAR, Alarm_Data.KIRMIZI, kod, iys_length + " IYS uyarısı var!", "-1" ));
                        } else {
                            for( o = 0; o < iys_length; o++ ) alarm_kontrol( new Alarm_Data( Alarm_Data.IYS_UYARISI_VAR, Alarm_Data.KIRMIZI, kod, "Yeni IYS: '" + iys.getString(o) + "'",String.valueOf(o)));
                        }
                        filtre_data.put(Otobus_Box_Filtre.FD_IYS, true);
                        ekstra_ozet.put(Otobus_Box_Filtre.FD_IYS, iys_length);
                    } else {
                        filtre_data.put(Otobus_Box_Filtre.FD_IYS, false);
                        ekstra_ozet.put(Otobus_Box_Filtre.FD_IYS, 0);
                    }
                    if( not_bildirimleri_length > 0 ){
                        if( not_bildirimleri_length > 5 ){
                            alarm_kontrol( new Alarm_Data( Alarm_Data.YENI_NOT_BILDIRIMI, Alarm_Data.MAVI, kod, not_bildirimleri_length + " yeni not bildirimi var!", "-1" ));
                        } else {
                            for( o = 0; o < not_bildirimleri_length; o++ ) alarm_kontrol( new Alarm_Data( Alarm_Data.YENI_NOT_BILDIRIMI, Alarm_Data.MAVI, kod, not_bildirimleri.getString(o), String.valueOf(o)));
                        }
                    }
                    aktif_plaka = User_Config.config_oku().getJSONArray("otobusler").getJSONObject(index).getString("aktif_plaka");
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            otobus_plaka_kontrol();
                        }
                    });

                    DateFormat dateFormat = new SimpleDateFormat("HH:mm");
                    Date date = new Date();
                    String SIMDI = dateFormat.format(date);

                    // sefer özeti hesaplamalari init
                    sefer_ozet.put(Sefer_Data.DTAMAM, 0);
                    sefer_ozet.put(Sefer_Data.DAKTIF, 0);
                    sefer_ozet.put(Sefer_Data.DBEKLEYEN, 0);
                    sefer_ozet.put(Sefer_Data.DIPTAL, 0);
                    sefer_ozet.put(Sefer_Data.DYARIM, 0);

                    // durum degiskenleri
                    boolean tum_seferler_tamam = false,
                            hat_verisi_alindi = false,
                            tum_seferler_bekleyen = false;

                    JSONObject sefer, onceki_sefer, sonraki_sefer;
                    for (int x = 0; x < data.length(); x++) {
                        sefer = data.getJSONObject(x);
                        String durum = sefer.getString("durum");
                        if (sefer_ozet.containsKey(durum)) {
                            sefer_ozet.replace(durum, sefer_ozet.get(durum), sefer_ozet.get(durum) + 1);
                        } else {
                            sefer_ozet.put(durum, 1);
                        }
                    }

                    if (sefer_ozet.get(Sefer_Data.DTAMAM) == data.length()) tum_seferler_tamam = true;
                    if (sefer_ozet.get(Sefer_Data.DBEKLEYEN) == data.length()) tum_seferler_bekleyen = true;

                    if (tum_seferler_tamam) {
                        // ledi ve notflari guncelle
                        // sefer yuzdesi hesapla
                        ui_main_notf = "Tüm Seferler Tamam";
                        ui_notf = "Sefer yüzdesi: %100";
                        ui_led = Sefer_Data.DTAMAM;
                        ui_hat_data = data.getJSONObject(0).getString("hat");
                        return null;
                    } else if (tum_seferler_bekleyen) {
                        // hic baslamamis seferlerine
                        ui_main_notf = "Seferini bekliyor.";
                        ui_notf = "Bir sonraki sefer: " + data.getJSONObject(0).getString("orer");
                        ui_led = Sefer_Data.DBEKLEYEN;
                        ui_hat_data = data.getJSONObject(0).getString("hat");
                        return null;
                    }
                    String sefer_no,
                            sefer_hat,
                            sefer_guzergah,
                            sefer_surucu_sicil_no,
                            sefer_orer,
                            sefer_tahmin,
                            sefer_durum,
                            sefer_amir,
                            sefer_durum_kodu;


                    // hat harita icin, eger aktif sefer varsa onu alicak loopta
                    // eger guzergah bos harita req yaparsak tum otobusleri cekiyor, bunu engellemek icin
                    // bi kere ilk seferin verisini tanimliyoruz guzergah olarak
                    aktif_guzergah = data.getJSONObject(0).getString("guzergah");
                    hat = data.getJSONObject(0).getString("hat");

                    for (int j = 0; j < data.length(); j++) {
                        sefer = data.getJSONObject(j);
                        sefer_no = sefer.getString("no");
                        sefer_hat = sefer.getString("hat");
                        sefer_guzergah = sefer.getString("guzergah");
                        sefer_surucu_sicil_no = sefer.getString("surucu");
                        sefer_orer = sefer.getString("orer");
                        sefer_tahmin = sefer.getString("tahmin");
                        sefer_durum = sefer.getString("durum");
                        sefer_durum_kodu = sefer.getString("durum_kodu");
                        sefer_amir = sefer.getString("amir");

                        // soforleri listele
                        if (!suruculer.contains(sefer_surucu_sicil_no)) {
                            suruculer.add(sefer_surucu_sicil_no);
                        }

                        // hat ve güzergah verisini al
                        // aktif sefer yokmuş gibi burada sadece hatti aliyoruz
                        if (!hat_verisi_alindi) {
                            ui_hat_data = sefer_hat;
                            hat_verisi_alindi = true;
                        }

                        sonraki_sefer = null;
                        // bir sonraki sefer varsa aliyoruz verisini
                        if (!data.isNull(j + 1)) sonraki_sefer = data.getJSONObject(j + 1);

                        // yarim sefer
                        if (sefer_durum.equals(Sefer_Data.DYARIM)) {
                            if (sonraki_sefer != null) {
                                int k = j + 1;
                                int duzeltilmis_sefer_index = 0;
                                boolean sonraki_seferler_duzeltilmis = false;
                                while (!data.isNull(k)) {
                                    JSONObject yarim_sonrasi_sefer = data.getJSONObject(k);
                                    // yarim kalan seferden sonra bekleyen veya tamamlanan sefer yoksa yarim kaldi durumuna getiriyoruz
                                    // varsa dokunmuyoruz zaten yukarıda sonraki seferleri geçerken bekleyen veya tamamlandi olarak degisecek durum
                                    if (yarim_sonrasi_sefer.getString("durum").equals("B") || yarim_sonrasi_sefer.getString("durum").equals("T")) {
                                        duzeltilmis_sefer_index = k;
                                        sonraki_seferler_duzeltilmis = true;

                                        alarm_kontrol(new Alarm_Data(Alarm_Data.SEFERLER_DUZELTILDI, Alarm_Data.YESIL, kod, Alarm_Data.MESAJ_SEFERLER_DUZELTILDI, sefer_no));
                                        break;
                                    }
                                    k++;
                                }
                                // seferler duzeltilmemişse yarim kaldi diyoruz
                                // bu muhtemelen son sefer yarim kaldiginda olur cunku genelde yarim kalan sefer sonrasi iptal oluyor sonrakiler
                                if (!sonraki_seferler_duzeltilmis) {

                                    alarm_kontrol(new Alarm_Data(Alarm_Data.SEFER_YARIM, Alarm_Data.KIRMIZI, kod, Alarm_Data.MESAJ_YARIM, sefer_no));

                                    //System.out.println("[ " + kod + " ALARM ]" + "SEFER YARIM KALDIIIII");
                                    ui_main_notf = "Sefer Yarım Kaldı";
                                    ui_notf = "Durum Kodu: " + sefer_durum_kodu;
                                    ui_led = Sefer_Data.DYARIM;
                                } else {
                                    // bekleyen sefer ( durum led icin )
                                    JSONObject iptal_sonrasi_sefer = data.getJSONObject(duzeltilmis_sefer_index);
                                    if (iptal_sonrasi_sefer.getString("durum").equals(Sefer_Data.DBEKLEYEN)) {
                                        ui_led = Sefer_Data.DBEKLEYEN;
                                        ui_main_notf = "Seferini Bekliyor";
                                        if (!iptal_sonrasi_sefer.getString("amir").equals("") && !iptal_sonrasi_sefer.getString("amir").equals("[ 10 5 2 ]")) {
                                            ui_notf = "Bir sonraki sefer " + iptal_sonrasi_sefer.getString("amir") + " ( Amir )";
                                        } else {
                                            ui_notf = "Bir sonraki sefer " + iptal_sonrasi_sefer.getString("orer");
                                        }
                                    }
                                }
                            } else {
                                // son sefer yarım kalmis

                                alarm_kontrol(new Alarm_Data(Alarm_Data.SEFER_YARIM, Alarm_Data.KIRMIZI, kod, Alarm_Data.MESAJ_YARIM, sefer_no));
                                System.out.println("[ " + kod + " ALARM ]" + "SEFER YARIM KALDIIIII");

                                ui_main_notf = "Sefer Yarım Kaldı";
                                ui_notf = "Durum Kodu: " + sefer_durum_kodu;
                                ui_led = Sefer_Data.DYARIM;
                            }
                        }

                        // sefer iptalse
                        if (sefer_durum.equals(Sefer_Data.DIPTAL)) {
                            if (sonraki_sefer != null) {
                                int k = j + 1;
                                boolean sonraki_seferler_duzeltilmis = false;
                                int duzeltilmis_sefer_index = 0;
                                while (!data.isNull(k)) {
                                    JSONObject yarim_sonrasi_sefer = data.getJSONObject(k);
                                    // iptal seferden sonra bekleyen veya tamamlanan sefer yoksa yarim kaldi durumuna getiriyoruz
                                    // varsa dokunmuyoruz zaten yukarıda sonraki seferleri geçerken bekleyen veya tamamlandi olarak degisecek durum
                                    if (yarim_sonrasi_sefer.getString("durum").equals("B") || yarim_sonrasi_sefer.getString("durum").equals("T")) {
                                        duzeltilmis_sefer_index = k;
                                        sonraki_seferler_duzeltilmis = true;
                                        break;
                                    }
                                    k++;
                                }
                                // seferler duzeltilmemişse durum iptal diyoruz
                                if (!sonraki_seferler_duzeltilmis) {

                                    System.out.println("[ " + kod + " ALARM ]" + "SEFER İPTAAAAAAl");
                                    alarm_kontrol(new Alarm_Data(Alarm_Data.SEFER_IPTAL, Alarm_Data.KIRMIZI, kod, Alarm_Data.MESAJ_IPTAL, "1"));

                                    ui_main_notf = "Sefer İptal";
                                    ui_notf = "Durum Kodu: " + sefer_durum_kodu;
                                    ui_led = Sefer_Data.DIPTAL;
                                } else {
                                    // bekleyen sefer ( durum led icin )
                                    alarm_kontrol(new Alarm_Data(Alarm_Data.SEFERLER_DUZELTILDI, Alarm_Data.YESIL, kod, Alarm_Data.MESAJ_SEFERLER_DUZELTILDI, "1"));

                                    JSONObject iptal_sonrasi_sefer = data.getJSONObject(duzeltilmis_sefer_index);
                                    if (iptal_sonrasi_sefer.getString("durum").equals(Sefer_Data.DBEKLEYEN)) {
                                        ui_led = Sefer_Data.DBEKLEYEN;
                                        ui_main_notf = "Seferini Bekliyor";
                                        if (!iptal_sonrasi_sefer.getString("amir").equals("") && !iptal_sonrasi_sefer.getString("amir").equals("[ 10 5 2 ]")) {
                                            ui_notf = "Bir sonraki sefer " + iptal_sonrasi_sefer.getString("amir") + " ( Amir )";
                                        } else {
                                            ui_notf = "Bir sonraki sefer " + iptal_sonrasi_sefer.getString("orer");
                                        }
                                    }
                                }
                            } else {
                                // son sefer iptal
                                ui_main_notf = "Sefer İptal";
                                ui_notf = "Durum Kodu: " + sefer_durum_kodu;
                                ui_led = Sefer_Data.DIPTAL;

                                alarm_kontrol(new Alarm_Data(Alarm_Data.SEFER_IPTAL, Alarm_Data.KIRMIZI, kod, Alarm_Data.MESAJ_IPTAL, "1"));
                                System.out.println("[ " + kod + " ALARM ]" + "SEFER İPTAAAAAAl");

                            }
                        }

                        // aktif sefer
                        if (sefer_durum.equals(Sefer_Data.DAKTIF)) {

                            // hat ve güzergah verisini al
                            // sadece aktif seferde gidiş - donüş var
                            hat = sefer_hat;
                            // gecmis harita takip icin
                            aktif_guzergah = sefer_guzergah;
                            ui_hat_data = hat_detay_olustur(sefer_guzergah);

                            // ui durumlari
                            ui_led = Sefer_Data.DAKTIF;
                            if (sefer_tahmin.equals("")) {
                                ui_main_notf = "Aktif Sefer " + sefer_orer;
                            } else {
                                ui_main_notf = "Aktif Sefer " + sefer_orer + " (T " + sefer_tahmin + ")";
                            }

                            if (!durak_data.equals("YOK")) {
                                ui_notf = durak_data.substring(16, durak_data.indexOf(" ("));
                            } else {
                                ui_notf = "Durak bilgisi yok.";
                            }
                            if (sonraki_sefer != null && !sefer_tahmin.equals("")) {
                                // gec kalip kalmama kontrolu
                                if (!sonraki_sefer.getString("amir").equals("") && !sonraki_sefer.getString("amir").equals("[ 10 5 2 ]")) {
                                    // bir sonraki sefere amir saat atamis
                                    if (Sefer_Sure.hesapla(sefer_tahmin, sonraki_sefer.getString("amir")) < 0) {
                                        // amir saat atamis ama gene de geç kalacak
                                        alarm_kontrol(new Alarm_Data(Alarm_Data.GEC_KALMA, Alarm_Data.TURUNCU, kod, Alarm_Data.MESAJ_GEC_KALMA, sefer_no));

                                        System.out.println("[ " + kod + " ALARM ]" + "GEÇ KALABİLİR AMİR SAAT ATAMIŞ AMA YEMEMİŞ");

                                    }
                                } else {
                                    // amir saat atamamis
                                    if (Sefer_Sure.hesapla(sefer_tahmin, sonraki_sefer.getString("orer")) < 0) {
                                        // amir saat atamis ama gene de geç kalacak
                                        alarm_kontrol(new Alarm_Data(Alarm_Data.GEC_KALMA, Alarm_Data.TURUNCU, kod, Alarm_Data.MESAJ_GEC_KALMA, sefer_no));
                                        System.out.println("[ " + kod + " ALARM ]" + "GEÇ KALABİLİR LUAAAN");
                                    }
                                }
                            }
                        }

                        // tamamlanmis sefer
                        if (sefer_durum.equals(Sefer_Data.DTAMAM)) {


                            if (sonraki_sefer != null) {
                                if (sonraki_sefer.getString("durum").equals(Sefer_Data.DBEKLEYEN)) {
                                    // sonraki seferi var ve durumu bekleyense, bekleyen seferin saatini aliyoruz
                                    ui_led = Sefer_Data.DBEKLEYEN;
                                    ui_main_notf = "Seferini Bekliyor";
                                    if (!sonraki_sefer.getString("amir").equals("") && !sonraki_sefer.getString("amir").equals("[ 10 5 2 ]")) {
                                        // eger amir saat atamişsa
                                        ui_notf = "Bir sonraki sefer: " + sonraki_sefer.getString("amir") + " ( Amir )";
                                        alarm_kontrol(new Alarm_Data(Alarm_Data.AMIR_SAAT_ATADI, Alarm_Data.MAVI, kod, Alarm_Data.MESAJ_AMIR_SAAT_ATADI, sefer_no));

                                        //System.out.println("[ " + kod + " ALARM ]" + "AMİR SAAT ATAMIŞ ARKADAŞA");
                                    } else {
                                        // amirsiz bir sonraki sefer
                                        ui_notf = "Bir sonraki sefer " + sonraki_sefer.getString("orer");
                                    }
                                }
                            } else {

                                // bir sonraki seferi yoksa tamamlamis demektir
                                // bunun 'tum_seferler_tamam' dan farki yukaridaki tamamladi kontrolu
                                // tum seferlerin T olmasina bakiyor. burada ise yapacak seferi kalmamis anlamina geliyor.
                                // yani arada iptal, yarim sefer olabilir
                                ui_main_notf = "Günü Tamamladı";
                                ui_notf = "Sefer yüzdesi: %" + (sefer_ozet.get("T") * 100) / data.length();
                                ui_led = Sefer_Data.DTAMAM;
                                if (kod.equals("B-1741")) System.out.println("helo2  " + sefer_no);
                            }
                        }

                        if (sefer_durum.equals(Sefer_Data.DBEKLEYEN)) {
                            if (!sefer_amir.equals("") && !sefer_amir.equals("[ 10 5 2 ]")) {
                                if (Sefer_Sure.gecmis(SIMDI, sefer_amir)) {
                                    alarm_kontrol(new Alarm_Data(Alarm_Data.SEFER_BASLAMADI, Alarm_Data.TURUNCU, kod, Alarm_Data.MESAJ_SEFER_BASLAMADI, sefer_no));
                                    //System.out.println(kod + " ----> Seferine başlmadı -->  " + Sefer_Sure.gecmis( SIMDI, sefer_orer  ));
                                }
                            } else {
                                if (Sefer_Sure.gecmis(SIMDI, sefer_orer)) {
                                    //System.out.println("[ " + kod + " ALARM ]" + "SEFERINE BAŞLAMADI ARKADAŞ ");
                                    //System.out.println(kod + " ----> Seferine başlmadı -->  " + Sefer_Sure.gecmis( SIMDI, sefer_orer  ));
                                    alarm_kontrol(new Alarm_Data(Alarm_Data.SEFER_BASLAMADI, Alarm_Data.TURUNCU, kod, Alarm_Data.MESAJ_SEFER_BASLAMADI, sefer_no));
                                }
                            }
                        }
                    } // for

                    if( sefer_ozet.get(Sefer_Data.DIPTAL) > 0 || sefer_ozet.get(Sefer_Data.DYARIM) > 0 ){
                        filtre_data.put(Otobus_Box_Filtre.FD_ZAYI, true );
                    } else {
                        filtre_data.put(Otobus_Box_Filtre.FD_ZAYI, false );
                    }


                    if (suruculer.size() > 1) {
                        // sefer nonun cokta onemi yok bi kere vericez alarmi
                        alarm_kontrol(new Alarm_Data(Alarm_Data.SURUCU_DEGISIMI, Alarm_Data.MAVI, kod, Alarm_Data.MESAJ_SURUCU_DEGISTI, "1"));
                    }

                    if (suruculer.contains("") || suruculer.contains("-1") || suruculer.contains("-111111")) {
                        alarm_kontrol(new Alarm_Data(Alarm_Data.BELIRSIZ_SURUCU, Alarm_Data.MAVI, kod, Alarm_Data.MESAJ_BELIRSIZ_SURUCU, "1"));
                    }
                } catch( Exception e ){ e.printStackTrace(); }

                return null;
            }
        });
        update_thread.setDaemon(true);
        update_thread.start();
    }

    public void otobus_plaka_kontrol(){
        box_header_plaka.getStyleClass().clear();
        if( !aktif_plaka.equals(ruhsat_plaka) ){
            box_header_plaka.getStyleClass().add("box-header-plaka-uyari");
            filtre_data.put(Otobus_Box_Filtre.FD_PLAKA, true);
        } else {
            box_header_plaka.getStyleClass().add("box-header-plaka");
            filtre_data.put(Otobus_Box_Filtre.FD_PLAKA, false);
        }
        Platform.runLater(new Runnable(){ @Override public void run(){ box_header_plaka.setText(aktif_plaka); } });

    }

    private String hat_detay_olustur( String guz ){
        if( guz.equals("") ){
            return this.hat;
        } else {
            return this.hat + " [" + guz.substring( this.hat.length() + 1,  this.hat.length() + 2 ) + "]";
        }

    }

    public void alarm_kontrol( Alarm_Data alarm ){
        Ayarlar_Genel_Data alarm_ayarlar = User_Config.ayarlari_oku();
        if( !alarm_ayarlar.alarm_cb_kontrol( alarm.get_type() ) ) return;
        // TODO ALARMLAR AYARLAR
        if( this.alarmlar.size() == 0 ){
            this.alarmlar.put( alarm.get_key(), alarm );
        } else {
            if( !this.alarmlar.containsKey(alarm.get_key()) ) this.alarmlar.put( alarm.get_key(), alarm );
        }
    }

    public ArrayList<Alarm_Data> get_alarmlar(){
        ArrayList<Alarm_Data> output = new ArrayList<>();
        Alarm_Data alarm_item;
        //System.out.println(kod+ " Alarmlar: -- >" + alarmlar.size() );
        for (Map.Entry<String, Alarm_Data> entry : alarmlar.entrySet()) {
            alarm_item = entry.getValue();
            if( !alarm_item.get_goruldu() ){
                //alarm_item.goruldu(true);
                output.add(alarm_item);
            }
        }
        return output;
    }

    private void update_led( String d ){
        String CLASS_LED = "durum-led";
        this.led.getStyleClass().remove(1);
        switch( d ){
            case "T":
                this.led.getStyleClass().add( CLASS_LED +"-tamam" );
                break;
            case "A":
                this.led.getStyleClass().add( CLASS_LED +"-aktif" );
                break;
            case "B":
                this.led.getStyleClass().add( CLASS_LED +"-bekleyen" );
                break;
            case "I":
                this.led.getStyleClass().add( CLASS_LED +"-iptal" );
                break;
            case "Y":
                this.led.getStyleClass().add( CLASS_LED +"-yarim" );
                break;
        }
    }

    public void set_filtre_data( String key, boolean val ){
        filtre_data.put( key, val );
    }
    public Map<String, Boolean> get_filtre_data(){
        return filtre_data;
    }
    public boolean get_filtre_data( String key ){
        return filtre_data.get(key);
    }
    public int get_index(){
        return index;
    }
    public String get_durum(){
        return ui_led;
    }

    public VBox get_ui(){
        return this.ui_container;
    }


}
