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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jeppe on 19.05.2017.
 */
public class Otobus_Box extends VBox{

    private int index;
    private String kod, ruhsat_plaka, aktif_plaka;
    private String hat = "#", aktif_guzergah = "";
    private boolean run = true;
    private String cookie = "INIT", bolge;
    private Orer_Download orer_download;
    private PDKS_Download pdks_download;
    private ArrayList<Surucu> suruculer = new ArrayList<>();
    private ArrayList<String> filo_vdl_log = new ArrayList<>();

    private Label   main_info,
                    info,
                    ozet_tamam,
                    ozet_aktif,
                    ozet_bekleyen,
                    ozet_iptal,
                    ozet_yarim,
                    box_header_hat,
                    box_header_plaka;
    private Circle led;
    private Button btn_sefer, btn_surucu, btn_alarm, btn_mesaj, btn_harita, btn_rapor, btn_iys, btn_notlar;
    private Button btn_vdl_filo, btn_vdl_plaka, btn_vdl_gecmis; // veri download log
    private Label lbl_vdl_filo, lbl_vdl_plaka;
    private VBox ui_container;
    private VBox box_content;
    private String  ui_led = "VY",
                    ui_notf,
                    ui_main_notf,
                    ui_hat_data;

    private String plaka_class = "", plaka_prev_class = "";
    private Obarey_Popup    sefer_popup,
                            surucu_popup,
                            mesaj_popup,
                            iys_popup,
                            harita_popup,
                            rapor_popup,
                            plaka_popup,
                            not_popup,
                            vd_log_popup;

    private Filo_Table sefer_plan_table;
    private Surucu_Box surucu_box;
    private Rapor_Tarih_Filtre_Box rapor_filtre_box;
    private Popup_Mesaj_Box popup_mesaj_box;
    private Popup_IYS_Box popup_iys_box;
    private Popup_Not_Box popup_not_box;
    private JSONArray new_data = new JSONArray();

    private final ArrayList<Alarm_Listener> listeners = new ArrayList<Alarm_Listener>();
    private Map<String, Alarm_Data> alarmlar = new HashMap<>();
    private Map<String, Integer> sefer_ozet = new HashMap<>();
    private Map<String, Integer> ekstra_ozet = new HashMap<>();
    private Map<String, Boolean> filtre_data = new HashMap<>();
    private double gitas_km = 0, iett_km = 0;
    private double hat_gitas_km = 0, hat_iett_km = 0;
    private boolean hat_km_alindi = false;
    public Otobus_Box(  String kod, int index, String ruhsat_plaka, String aktif_plaka ){
        super();
        this.kod = kod;
        this.index = index;
        this.ruhsat_plaka = ruhsat_plaka;
        this.aktif_plaka = aktif_plaka;
        this.bolge = kod.substring(0,1);

        filtre_data.put(Otobus_Box_Filtre.FD_IYS, false);
        filtre_data.put(Otobus_Box_Filtre.FD_NOT, false);
        filtre_data.put(Otobus_Box_Filtre.FD_PLAKA, false);
        filtre_data.put(Otobus_Box_Filtre.FD_ZAYI, false);

        ekstra_ozet.put(Otobus_Box_Filtre.FD_NOT, 0);
        ekstra_ozet.put(Otobus_Box_Filtre.FD_IYS, 0);

        create_ui();
        set_event_handlers();
        update_thread_start();
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
        //ui_container = new VBox();
        this.setId( String.valueOf(this.index) );
        this.getStyleClass().add(CLASS_OTOBUS_BOX);

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

        if( User_Config.izin_kontrol(User_Config.IOB_PLAN ) ) nav_cont_test.getChildren().add(btn_sefer);
        if( User_Config.izin_kontrol(User_Config.IOB_KMRAPOR) ) nav_cont_test.getChildren().add(btn_rapor);
        if( User_Config.izin_kontrol(User_Config.IOB_MESAJ) ) nav_cont_test.getChildren().add(btn_mesaj);
        if( User_Config.izin_kontrol(User_Config.IOB_SURUCU ) ) nav_cont_test.getChildren().add(btn_surucu);
        if( User_Config.izin_kontrol(User_Config.IOB_IYS ) ) nav_cont_test.getChildren().add(btn_iys);
        if( User_Config.izin_kontrol(User_Config.IOB_HARITA ) ) nav_cont_test.getChildren().add(btn_harita);
        if( User_Config.izin_kontrol(User_Config.IOB_NOTLAR ) ) nav_cont_test.getChildren().add(btn_notlar);

        AnchorPane vd_log_container = new AnchorPane();
        vd_log_container.getStyleClass().add("box-vd-log-container");

        HBox fd_container = new HBox();
        fd_container.setSpacing(5);
        fd_container.setAlignment(Pos.CENTER);

        HBox sw_container = new HBox();
        sw_container.setSpacing(10);
        sw_container.setAlignment(Pos.CENTER);

        btn_vdl_filo = new Button("F");
        btn_vdl_filo.getStyleClass().add("btn-vd-log-default");
        btn_vdl_plaka = new Button("P");
        btn_vdl_plaka.getStyleClass().add("btn-vd-log-default");
        btn_vdl_gecmis = new Button("G");
        btn_vdl_gecmis.getStyleClass().add("btn-vd-log-mavi");


        lbl_vdl_filo = new Label("xx:xx (x)");
        lbl_vdl_plaka = new Label("xx:xx");

        fd_container.getChildren().addAll( btn_vdl_filo, lbl_vdl_filo, btn_vdl_plaka, lbl_vdl_plaka );
        sw_container.getChildren().addAll( btn_vdl_gecmis );
        vd_log_container.getChildren().addAll( fd_container, sw_container );

        AnchorPane.setLeftAnchor(fd_container, 10.0);
        AnchorPane.setTopAnchor(fd_container, 1.0);
        AnchorPane.setTopAnchor(sw_container, 1.0);
        AnchorPane.setRightAnchor(sw_container, 10.0);

        // box content elemanlari ekle
        box_content.getChildren().addAll( main_info, info, ozet_cont,nav_cont_test, vd_log_container );
        this.getChildren().addAll( box_header, box_content );
    }

    private void orer_download( boolean thread ){

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                btn_vdl_filo.getStyleClass().clear();
                btn_vdl_filo.getStyleClass().addAll("button","btn-vd-log-yesil");
            }
        });
        if( thread ){
            // buttonla tetik
            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                    orer_download_inner();
                }
            });
            th.setDaemon(true);
            th.start();
        } else {
            // normal sayacli tetik
           orer_download_inner();
        }
    }

    private void orer_download_inner(){
        final long fd_ts = Common.get_unix();
        // orer kontrol
        orer_download = new Orer_Download( kod, cookie );
        orer_download.yap();
        if( !orer_download.get_error() ){
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    btn_vdl_filo.getStyleClass().clear();
                    btn_vdl_filo.getStyleClass().addAll("button","btn-vd-log-default");
                    String lupdate = String.valueOf(Common.get_unix() - fd_ts);

                    lbl_vdl_filo.setText( Common.get_current_hmin() + " (" + lupdate + ")" );
                    vd_log_ekle( "[OK] " + Common.get_current_hmin() + " (" + lupdate + ")" );
                }
            });
            update( orer_download.get_seferler(), orer_download.get_aktif_sefer_verisi() );
        } else {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    btn_vdl_filo.getStyleClass().clear();
                    btn_vdl_filo.getStyleClass().addAll("button", "btn-vdl-log-kirmizi");
                    vd_log_ekle( "[HATA] " + Common.get_current_hmin()  );
                }
            });
        }
    }

    private void vd_log_ekle( String log ){
        if( filo_vdl_log.size() == 25 ) filo_vdl_log.clear();
        filo_vdl_log.add( log );
    }

    private void update_thread_start(){

        Thread th = new Thread(new Runnable() {
            private int sleep;
            @Override
            public void run() {

                try {
                    Thread.sleep( index * 2000 );
                } catch( InterruptedException e ){
                    e.printStackTrace();
                }
                sleep = 90000;
                while( run ){

                    if( !cookie.equals("INIT") ){
                        sleep = 90000;
                        orer_download( false );
                        if( !hat_km_alindi && !hat.equals("#") ){
                            Web_Request request = new Web_Request(Web_Request.SERVIS_URL, "&req=hat_km_download&hat="+hat);
                            request.kullanici_pc_parametreleri_ekle();
                            request.action();
                            JSONObject data_json = new JSONObject( request.get_value()).getJSONObject("data");
                            hat_iett_km = data_json.getDouble("iett_km");
                            hat_gitas_km = data_json.getDouble("gitas_km");
                            hat_km_alindi = true;
                        }
                        for( Alarm_Listener listener : listeners ) listener.on_ui_finished( get_alarmlar() );
                    } else {
                        sleep = 5000;
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

    private void update( JSONArray data, String durak_data ) {

        if( data.length() == 0 ) return;

        new_data = data;

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

        iett_km = 0;
        gitas_km = 0;
        JSONObject sefer, onceki_sefer, sonraki_sefer;
        for (int x = 0; x < data.length(); x++) {
            sefer = data.getJSONObject(x);
            String durum = sefer.getString("durum");
            if (sefer_ozet.containsKey(durum)) {
                sefer_ozet.replace(durum, sefer_ozet.get(durum), sefer_ozet.get(durum) + 1);
            } else {
                sefer_ozet.put(durum, 1);
            }
            if( durum.equals(Sefer_Data.DTAMAM) ) {
                if( x == 0 && sefer.getString("guzergah").substring( sefer.getString("hat").length() + 1, sefer.getString("hat").length() + 2  ).equals("D") ){
                    // ilk sefer donus
                    // yarim sefer km si ekliyoruz
                    //System.out.println("Başlangıç D");
                    try {
                        iett_km += hat_iett_km / 2;
                        gitas_km += hat_gitas_km / 2;
                    } catch( NullPointerException e ){
                        e.printStackTrace();
                    }
                }
                if( sefer.getString("guzergah").substring( sefer.getString("hat").length() + 1, sefer.getString("hat").length() + 2  ).equals("G") ) {
                    iett_km += hat_iett_km;
                    gitas_km += hat_gitas_km;
                }
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
            update_ui();
            return;
        } else if (tum_seferler_bekleyen) {
            // hic baslamamis seferlerine
            ui_main_notf = "Seferini bekliyor.";
            ui_notf = "Bir sonraki sefer: " + data.getJSONObject(0).getString("orer");
            ui_led = Sefer_Data.DBEKLEYEN;
            ui_hat_data = data.getJSONObject(0).getString("hat");
            update_ui();
            return;
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
            //sefer_surucu_sicil_no = sefer.getString("surucu");
            sefer_orer = sefer.getString("orer");
            sefer_tahmin = sefer.getString("tahmin");
            sefer_durum = sefer.getString("durum");
            sefer_durum_kodu = sefer.getString("durum_kodu");
            sefer_amir = sefer.getString("amir");


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
                        alarm_kontrol(new Alarm_Data(Alarm_Data.SEFER_IPTAL, Alarm_Data.KIRMIZI, kod, Alarm_Data.MESAJ_IPTAL, "1"));
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

        if (sefer_ozet.get(Sefer_Data.DIPTAL) > 0 || sefer_ozet.get(Sefer_Data.DYARIM) > 0) {
            filtre_data.put(Otobus_Box_Filtre.FD_ZAYI, true);
        } else {
            filtre_data.put(Otobus_Box_Filtre.FD_ZAYI, false);
        }
        int suruculer_size = suruculer.size();
        if ( suruculer_size == 0 ) {
            alarm_kontrol(new Alarm_Data(Alarm_Data.BELIRSIZ_SURUCU, Alarm_Data.MAVI, kod, Alarm_Data.MESAJ_BELIRSIZ_SURUCU, "1"));
        } else if( suruculer_size == 1 ) {
            // surucu bilgisi gelmişse önceki alarmi kaldir
            String alarm_id = "";
            for (Map.Entry<String, Alarm_Data> entry : alarmlar.entrySet()) {
                if( entry.getValue().get_type() == Alarm_Data.BELIRSIZ_SURUCU ){
                    alarm_id = entry.getKey();
                }
            }
            if( !alarm_id.equals("") ) alarmlar.remove(alarm_id);
        } else {
            // sefer nonun cokta onemi yok bi kere vericez alarmi
            alarm_kontrol(new Alarm_Data(Alarm_Data.SURUCU_DEGISIMI, Alarm_Data.MAVI, kod, Alarm_Data.MESAJ_SURUCU_DEGISTI, "1"));
        }

        update_ui();
        otobus_plaka_kontrol();


    }

    public void update_ui(){
        Platform.runLater(new Runnable() {
            @Override
            public void run(){
                otobus_plaka_kontrol();
                main_info.setText(ui_main_notf);
                info.setText(ui_notf);
                update_led(ui_led);
                box_header_hat.setText(ui_hat_data);

                ozet_tamam.setText(String.valueOf(sefer_ozet.get(Sefer_Data.DTAMAM)));
                ozet_aktif.setText(String.valueOf(sefer_ozet.get(Sefer_Data.DAKTIF)));
                ozet_bekleyen.setText(String.valueOf(sefer_ozet.get(Sefer_Data.DBEKLEYEN)));
                ozet_iptal.setText(String.valueOf(sefer_ozet.get(Sefer_Data.DIPTAL)));
                ozet_yarim.setText(String.valueOf(sefer_ozet.get(Sefer_Data.DYARIM)));
            }
        });
    }

    public void plaka_download(){
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        btn_vdl_plaka.getStyleClass().clear();
                        btn_vdl_plaka.getStyleClass().addAll("button", "btn-vd-log-yesil");
                    }
                });
                Web_Request req = new Web_Request(Web_Request.SERVIS_URL, "&req=otobus_plaka_kontrol&oto="+kod);
                req.kullanici_pc_parametreleri_ekle();
                req.action();
                JSONObject plaka_data = new JSONObject(req.get_value()).getJSONObject("data").getJSONObject("plaka_data");
                plakalari_guncelle(plaka_data.getString("aktif_plaka"), plaka_data.getString("ruhsat_plaka"));
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        btn_vdl_plaka.getStyleClass().clear();
                        btn_vdl_plaka.getStyleClass().addAll("button", "btn-vd-log-default");
                        lbl_vdl_plaka.setText(Common.get_current_hmin());
                    }
                });
            }
        });
        th.setDaemon(true);
        th.start();

    }

    public synchronized void plakalari_guncelle( String _aktif_plaka, String _ruhsat_plaka ){
        aktif_plaka = _aktif_plaka;
        ruhsat_plaka = _ruhsat_plaka;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                btn_vdl_plaka.getStyleClass().clear();
                btn_vdl_plaka.getStyleClass().addAll("button", "btn-vd-log-default");
                lbl_vdl_plaka.setText( Common.get_current_hmin() );
                otobus_plaka_kontrol();
            }
        });
    }

    private void set_event_handlers(){

        btn_vdl_plaka.setOnMousePressed( ev -> {
            plaka_download();
        });

        btn_vdl_filo.setOnMousePressed( ev -> {
            if( !cookie.equals("INIT") ) orer_download( true );
        });

        btn_vdl_gecmis.setOnMousePressed( ev -> {
            if( vd_log_popup == null ){
                vd_log_popup = new Obarey_Popup(kod + " VD Log", this.getScene().getRoot());
            }
            if( vd_log_popup.ison() ) return;
            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                    VBox ul = new VBox();
                    ul.setSpacing(5);
                    Label li;
                    for( int j = filo_vdl_log.size() - 1; j >= 0; j-- ){
                        li = new Label(filo_vdl_log.get(j));
                        li.getStyleClass().addAll("fbold", "cbeyaz");
                        ul.getChildren().add(li);
                    }
                    vd_log_popup.init(true);
                    vd_log_popup.set_content( ul );
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            vd_log_popup.show(ev.getScreenX(), ev.getScreenY() );
                        }
                    });

                }
            });
            th.setDaemon(true);
            th.start();
        });

        if( User_Config.izin_kontrol( User_Config.IOB_PLAKA_DEGISTIRME ) ) {
            box_header_plaka.setOnMousePressed(event -> {
                if (plaka_popup == null) {
                    plaka_popup = new Obarey_Popup(kod + " Plaka Bilgisi", this.getScene().getRoot());
                }
                if (plaka_popup.ison()) return;
                Thread plaka_ui_th = new Thread(new Task<String>() {
                    private VBox content = new VBox();
                    private GButton kaydet;
                    GInputGrup ruhsat;
                    GInputGrup aktif;

                    @Override
                    protected void succeeded() {
                        plaka_popup.show(event.getScreenX(), event.getScreenY());
                    }

                    @Override
                    protected String call() {
                        plaka_popup.init(true);

                        content.setAlignment(Pos.CENTER);
                        content.setSpacing(10);
                        content.getStyleClass().add("popup-siyah");

                        ruhsat = new GInputGrup("Ruhsat Plaka", new GTextField(GTextField.MID, ruhsat_plaka));
                        aktif = new GInputGrup("Aktif Plaka", new GTextField(GTextField.MID, aktif_plaka));
                        kaydet = new GButton("Kaydet", GButton.CMORB);
                        content.getChildren().addAll(ruhsat, aktif, kaydet);
                        plaka_popup.set_content(content);
                        kaydet.setOnAction(evo -> {
                            kaydet.setDisable(true);
                            kaydet.setText("İşlem yapılıyor...");
                            Thread th = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Web_Request request = new Web_Request(Web_Request.SERVIS_URL, "&req=plaka_bildirim&oto=" + kod + "&aktif_plaka=" + aktif.get_input_val() + "&ruhsat_plaka=" + ruhsat.get_input_val());
                                    request.kullanici_pc_parametreleri_ekle();
                                    request.action();
                                    JSONObject output = new JSONObject(request.get_value());
                                    int ok = output.getInt("ok");
                                    if (ok == 1) {
                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                kaydet.setText("Kaydet");
                                                aktif_plaka = aktif.get_input_val();
                                                otobus_plaka_kontrol();
                                                kaydet.setDisable(false);
                                            }
                                        });
                                    } else {
                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                kaydet.setText("Herhangi bir değişiklik yapılmadı.");
                                                kaydet.setDisable(false);
                                            }
                                        });
                                    }
                                }
                            });
                            th.setDaemon(true);
                            th.start();
                        });
                        return null;
                    }
                });
                plaka_ui_th.setDaemon(true);
                plaka_ui_th.start();
            });
        }

        btn_sefer.setOnMousePressed( ev -> {
            btn_sefer.setDisable(true);
            Otobus_Box this_ref = this;
            Thread plan_thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if( sefer_popup == null || sefer_plan_table == null ) {
                        sefer_popup = new Obarey_Popup(kod + " Sefer Planı", this_ref.getScene().getRoot());
                        sefer_popup.init(true);
                        sefer_plan_table = new Filo_Table( kod );
                        sefer_plan_table.init();
                    }
                    if( sefer_popup.ison() ){
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                btn_sefer.setDisable(false);
                            }
                        });
                        return;
                    }
                    sefer_plan_table.set_canli_data( new_data );
                    sefer_plan_table.set_data( new_data, new JSONArray() );
                    sefer_plan_table.update_data();
                    sefer_plan_table.update_ui();
                    sefer_popup.set_content( sefer_plan_table.get() );
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            sefer_popup.show( ev.getScreenX(), ev.getScreenY() );
                            btn_sefer.setDisable(false);
                        }
                    });
                }
            });
            plan_thread.setDaemon(true);
            plan_thread.start();
        });

        btn_mesaj.setOnMousePressed( ev -> {
            if( mesaj_popup == null || popup_mesaj_box == null ) {
                mesaj_popup = new Obarey_Popup(kod + " Filo Mesajları", this.getScene().getRoot() );
                popup_mesaj_box = new Popup_Mesaj_Box( kod );
            }
            if( mesaj_popup.ison() ) return;
            btn_mesaj.setDisable(true);
            Thread filo_mesaj_th = new Thread( new Task<String>(){
                @Override
                protected void succeeded(){
                    mesaj_popup.set_content( popup_mesaj_box );
                    mesaj_popup.show( ev.getScreenX(), ev.getScreenY() );
                    btn_mesaj.setDisable(false);
                }
                @Override
                protected String call(){
                    mesaj_popup.init(true);
                    popup_mesaj_box.init();
                    return null;
                }
            });
            filo_mesaj_th.setDaemon(true);
            filo_mesaj_th.start();
        });

        btn_iys.setOnMousePressed( ev -> {
            if( iys_popup == null || popup_iys_box == null ) {
                iys_popup = new Obarey_Popup(kod + " IYS", this.getScene().getRoot());
                popup_iys_box = new Popup_IYS_Box( kod );
            }
            if( iys_popup.ison() ) return;
            btn_iys.setDisable(true);
            Thread filo_mesaj_th = new Thread( new Task<String>(){
                @Override
                protected void succeeded(){
                    iys_popup.set_content( popup_iys_box );
                    iys_popup.show( ev.getScreenX(), ev.getScreenY() );
                    btn_iys.setDisable(false);
                }
                @Override
                protected String call(){
                    iys_popup.init(true);
                    popup_iys_box.init();
                    return null;
                }
            });
            filo_mesaj_th.setDaemon(true);
            filo_mesaj_th.start();
        });

        btn_rapor.setOnMousePressed( ev -> {
            if( rapor_popup == null  ) {
                rapor_popup = new Obarey_Popup(kod + " Rapor", this.getScene().getRoot());
            }
            if( rapor_popup.ison() ) return;
            btn_rapor.setDisable(true);
            final Otobus_Box this_ref = this;
            Thread filo_mesaj_th = new Thread( new Task<String>(){
                @Override
                protected void succeeded(){
                    rapor_popup.show( ev.getScreenX(), ev.getScreenY() );
                    btn_rapor.setDisable(false);
                }
                @Override
                protected String call(){
                    rapor_popup.init(true);
                    rapor_filtre_box = new Rapor_Tarih_Filtre_Box( Rapor_Box_Toplam.TOPLAM_OTOBUS, kod, this_ref.getScene().getRoot() );
                    rapor_popup.set_content( rapor_filtre_box );
                    return null;
                }
            });
            filo_mesaj_th.setDaemon(true);
            filo_mesaj_th.start();
        });

        btn_notlar.setOnMousePressed( ev -> {
            if( not_popup == null || popup_not_box == null ) {
                not_popup = new Obarey_Popup(kod + " Otobüs Notlar", this.getScene().getRoot() );
                popup_not_box = new Popup_Not_Box( kod, aktif_plaka );
            }
            if( not_popup.ison() ) return;
            btn_notlar.setDisable(true);
            Thread filo_mesaj_th = new Thread( new Task<String>(){
                @Override
                protected void succeeded(){
                    not_popup.set_content( popup_not_box );
                    not_popup.show( ev.getScreenX(), ev.getScreenY() );
                    btn_notlar.setDisable(false);
                }
                @Override
                protected String call(){
                    not_popup.init(true);
                    popup_not_box.init();
                    return null;
                }
            });
            filo_mesaj_th.setDaemon(true);
            filo_mesaj_th.start();
        });

        btn_surucu.setOnMousePressed(event -> {
            if( surucu_popup == null || surucu_box == null ) {
                surucu_popup = new Obarey_Popup(kod + " Sürücü Bilgileri", this.getScene().getRoot());
                surucu_box = new Surucu_Box( kod );
            }
            if( surucu_popup.ison() ) return;
            btn_surucu.setDisable(true);
            Thread filo_surucu_th = new Thread( new Task<String>(){
                @Override
                protected void succeeded(){
                    surucu_popup.set_content( surucu_box.get_ui() );
                    surucu_popup.show( event.getScreenX(), event.getScreenY() );
                    btn_surucu.setDisable(false);
                }
                @Override
                protected String call(){

                    Web_Request request = new Web_Request(Web_Request.SERVIS_URL, "&req=pdks_detay&oto="+kod );
                    request.kullanici_pc_parametreleri_ekle();
                    request.action();
                    JSONArray data = new JSONObject(request.get_value()).getJSONObject("data").getJSONArray("pdks_data");
                    int size = data.length();
                    if( size > 0 ){
                        suruculer = new ArrayList<>();
                        JSONObject surucu_item;
                        for( int j = 0; j < size; j++ ){
                            surucu_item = data.getJSONObject(j);
                            suruculer.add(new Surucu( surucu_item.getString("sicil_no"), surucu_item.getString("isim"), surucu_item.getString("telefon") ) );
                        }
                    }
                    surucu_popup.init(true);
                    surucu_box.init( suruculer );
                    return null;
                }
            });
            filo_surucu_th.setDaemon(true);
            filo_surucu_th.start();
        });

        btn_harita.setOnMousePressed(event -> {
            if( harita_popup == null  ) {
                harita_popup = new Obarey_Popup(kod + " Harita", ui_container.getScene().getRoot());
            }
            if( harita_popup.ison() ) return;
            Thread filo_mesaj_th = new Thread( new Task<String>(){
                HBox harita_btn_cont = new HBox();
                @Override
                protected void succeeded(){
                    harita_popup.set_content( harita_btn_cont );
                    harita_popup.show( event.getScreenX(), event.getScreenY() );
                }
                @Override
                protected String call(){
                    GButton hat_harita = new GButton("Hat Haritası", GButton.CMORB);
                    GButton gecmis_takip = new GButton("Takip Harita", GButton.CMORB);
                    GButton konum_harita = new GButton("Son Konum", GButton.CMORB);
                    harita_btn_cont.getChildren().addAll( hat_harita, gecmis_takip, konum_harita );
                    harita_btn_cont.setAlignment(Pos.CENTER);
                    harita_btn_cont.setPadding(new Insets(0, 0, 10, 0));
                    HBox.setMargin( gecmis_takip, new Insets(0, 0, 0, 10 ) );
                    HBox.setMargin( konum_harita, new Insets(0, 0, 0, 10 ) );
                    // harita buton events
                    gecmis_takip.setOnMousePressed( ev -> {
                        Filo_Harita_Request_Task gecmis_takip_req = new Filo_Harita_Request_Task( kod, aktif_guzergah, Filo_Harita_Request_Task.TAKIP_HARITA );
                        gecmis_takip_req.init();
                        harita_btn_cont.getChildren().clear();
                        harita_btn_cont.getChildren().add( gecmis_takip_req.get_webview() );
                    });
                    hat_harita.setOnMousePressed( ev -> {
                        //System.out.println(hat);
                        Filo_Harita_Request_Task gecmis_takip_req = new Filo_Harita_Request_Task( kod, hat, Filo_Harita_Request_Task.HAT_HARITA );
                        gecmis_takip_req.init();
                        harita_btn_cont.getChildren().clear();
                        harita_btn_cont.getChildren().add( gecmis_takip_req.get_webview() );
                    });
                    /*konum_harita.setOnMousePressed( ev -> {
                        //System.out.println(hat);
                        Filo_Harita_Request_Task gecmis_takip_req = new Filo_Harita_Request_Task( kod, hat, Filo_Harita_Request_Task.KONUM_HARITA );
                        gecmis_takip_req.init();
                        harita_btn_cont.getChildren().clear();
                        harita_btn_cont.getChildren().add( gecmis_takip_req.get_webview() );
                    });*/
                    harita_popup.init(true);
                    return null;
                }
            });
            filo_mesaj_th.setDaemon(true);
            filo_mesaj_th.start();
        });

    }

    public void otobus_plaka_kontrol(){
        if( !aktif_plaka.equals(ruhsat_plaka) ){
            plaka_class = "box-header-plaka-uyari";
            filtre_data.put(Otobus_Box_Filtre.FD_PLAKA, true);
        } else {
            plaka_class =  "box-header-plaka";
            filtre_data.put(Otobus_Box_Filtre.FD_PLAKA, false);
        }
        if( !plaka_prev_class.equals(plaka_class) ){
            Platform.runLater(new Runnable(){
                @Override
                public void run(){
                    box_header_plaka.getStyleClass().clear();
                    box_header_plaka.getStyleClass().add(plaka_class);
                }
            });
        }
        Platform.runLater(new Runnable(){
            @Override
            public void run(){
                box_header_plaka.setText(aktif_plaka);

            }
        });
        plaka_prev_class = plaka_class;
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

    public void alarm_goruldu_tetik( String key ){
        //System.out.println( key + " --- ALARM GÖRÜLDÜ YAP2222!");
        try {
            alarmlar.get(key).goruldu(true);
            //System.out.println(alarmlar.get(key).get_goruldu());
        } catch( NullPointerException e ){
            e.printStackTrace();
        }
    }

    public ArrayList<Alarm_Data> get_alarmlar(){
        ArrayList<Alarm_Data> output = new ArrayList<>();
        Alarm_Data alarm_item;
        ArrayList<String> silinecekler = new ArrayList<>();
        Ayarlar_Genel_Data alarm_ayarlar = User_Config.ayarlari_oku();
        //System.out.println(kod+ " Alarmlar: -- >" + alarmlar.size() );
        for (Map.Entry<String, Alarm_Data> entry : alarmlar.entrySet()) {
            alarm_item = entry.getValue();

            if( !alarm_item.get_goruldu() && alarm_ayarlar.alarm_cb_kontrol( alarm_item.get_type() )  ){
                //alarm_item.goruldu(true);
                //System.out.println( alarm_item.get_key() + " gorulmemis");
                output.add(alarm_item);
            } else {
                //System.out.println( entry.getKey() + " -- SIL ALARMI");
                silinecekler.add( entry.getKey() );
            }
        }
        for( int j = 0; j < silinecekler.size(); j++ ) alarmlar.get(silinecekler.get(j)).goruldu(true);
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

    public String get_kod(){
        return kod;
    }

    public boolean get_filtre_data( String key ){
        return filtre_data.get(key);
    }

    public Map<String, Integer> get_ozet(){
        return sefer_ozet;
    }

    public Map<String, Integer> get_ekstra_ozet(){
        return ekstra_ozet;
    }

    public double get_iett_km(){
        return iett_km;
    }

    public double get_gitas_km(){
        return gitas_km;
    }

    public String get_durum(){
        return ui_led;
    }

    public void cookie_guncelle( String yeni_cookie ){
        cookie = yeni_cookie;
    }

    public String get_bolge(){
        return bolge;
    }

    public void add_alarm_listener( Alarm_Listener listener ){
        listeners.add( listener );
    }

    /*public VBox get_ui(){
        return this.ui_container;
    }*/

    public void set_id( int _index ){
        index = _index;
        this.setId( String.valueOf(index));
    }

}

class Filo_Task_Template {
    protected String oto, cookie, aktif_tarih, logprefix;
    protected boolean error = false;
    protected org.jsoup.Connection.Response istek_yap( String url ){
        try {
            return Jsoup.connect(url + oto)
                    .cookie("PHPSESSID", cookie )
                    .method(org.jsoup.Connection.Method.POST)
                    .timeout(50000)
                    .execute();
        } catch (IOException | NullPointerException e) {
            System.out.println( "["+Common.get_current_hmin() + "]  "+  oto + " " + logprefix + "veri alım hatası. Tekrar deneniyor[1].");
            e.printStackTrace();
            error = true;
        }
        return null;
    }
    protected Document parse_html(org.jsoup.Connection.Response req ){
        try {
            return req.parse();
        } catch( IOException | NullPointerException e ){
            System.out.println(  "["+Common.get_current_hmin() + "]  "+ aktif_tarih  + " " +  oto + " "+ logprefix + " parse hatası. Tekrar deneniyor.");
            error = true;
        }
        return null;
    }
    public boolean get_error(){
        return error;
    }
}

class Orer_Download extends Filo_Task_Template {

    private String aktif_sefer_verisi = "";
    private JSONArray seferler = new JSONArray();
    private boolean kaydet = false;
    public Orer_Download( String oto, String cookie ){
        this.oto = oto;
        this.cookie = cookie;
    }

    public void yap(){
        error = false;
        // veri yokken nullpointer yemeyek diye resetliyoruz başta
        System.out.println("ORER download [ " + oto + " ]");
        org.jsoup.Connection.Response sefer_verileri_req = istek_yap("http://filo5.iett.gov.tr/_FYS/000/sorgu.php?konum=ana&konu=sefer&otobus=");
        Document sefer_doc = parse_html( sefer_verileri_req );
        sefer_veri_ayikla( sefer_doc );

    }
    public void sefer_veri_ayikla( Document document ){
        if( error ){
            seferler = new JSONArray();
            return;
        }
        Elements table = null;
        Elements rows = null;
        Element row = null;
        Elements cols = null;

        try {
            table = document.select("table");
            rows = table.select("tr");

            if( rows.size() == 1 || rows.size() == 0 ){
                System.out.println(oto + " ORER Filo Veri Yok");
                return;
            }

            String hat = "", orer;
            Sefer_Data tek_sefer_data;
            boolean hat_alindi = false;

            aktif_sefer_verisi = "YOK";
            for( int i = 1; i < rows.size(); i++ ){
                row = rows.get(i);
                cols = row.select("td");

                orer = Common.regex_trim(cols.get(7).getAllElements().get(2).text());

                if( !hat_alindi ){
                    hat = cols.get(1).text().trim();
                    if( cols.get(1).text().trim().contains("!")  ) hat = cols.get(1).text().trim().substring(1, cols.get(1).text().trim().length() - 1 );
                    if( cols.get(1).text().trim().contains("#") ) hat = cols.get(1).text().trim().substring(1, cols.get(1).text().trim().length() - 1 );
                    if( cols.get(1).text().trim().contains("*") ) hat = cols.get(1).text().trim().substring(1, cols.get(1).text().trim().length() - 1);
                    hat_alindi = true;
                }

                if( cols.get(12).text().replaceAll("\u00A0", "").equals("A") && cols.get(3).getAllElements().size() > 2 ){
                    aktif_sefer_verisi = Common.regex_trim(cols.get(3).getAllElements().get(2).attr("title"));
                }

                tek_sefer_data = new Sefer_Data(
                        Common.regex_trim(cols.get(0).text()),
                        hat,
                        Common.regex_trim(cols.get(2).text()),
                        Common.regex_trim(cols.get(3).getAllElements().get(1).text()),
                        Common.regex_trim(cols.get(4).getAllElements().get(2).text()),
                        "",
                        "",
                        "",
                        Common.regex_trim(cols.get(6).text()),
                        orer,
                        "",
                        Common.regex_trim(cols.get(8).text()),
                        Common.regex_trim(cols.get(9).text()),
                        Common.regex_trim(cols.get(10).text()),
                        Common.regex_trim(cols.get(11).text()),
                        Common.regex_trim(cols.get(12).text()),
                        cols.get(13).text().substring(5),
                        "",
                        1,
                        0
                );
                seferler.put(tek_sefer_data.tojson());
                cols.clear();
            }
            rows.clear();
        } catch( NullPointerException e ){
            e.printStackTrace();
            System.out.println( "["+Common.get_current_hmin() + "]  "+  oto+ " ORER sefer veri ayıklama hatası. Tekrar deneniyor.");
            seferler = new JSONArray();
            error = true;
            //yap();
        }
    }
    public JSONArray get_seferler(){
        return seferler;
    }
    public String get_aktif_sefer_verisi(){
        return aktif_sefer_verisi;
    }



}

class PDKS_Download extends Filo_Task_Template {
    private Map<String, String> data = new HashMap<>();
    public PDKS_Download( String oto, String cookie ){
        this.oto = oto;
        this.cookie = cookie;
        this.logprefix = "Sürücü PDKS";
    }
    public Map<String, String> get_suruculer(){
        return data;
    }
    public void yap(){
        error = false;
        System.out.println( "["+Common.get_current_hmin() + "]  "+ aktif_tarih  + " " +  "[ " + oto + " PDKS DOWNLOAD ]");
        org.jsoup.Connection.Response pdks_req = istek_yap("http://filo5.iett.gov.tr/_FYS/000/sorgu.php?konu=mesaj&mtip=PDKS&oto=");
        Document pdks_doc = parse_html( pdks_req );
        pdks_ayikla( pdks_doc );
    }
    private void pdks_ayikla( Document document ){
        if( error ){
            data = new HashMap<>();
            return;
        }
        Elements table = null;
        Elements rows = null;
        Element row = null;
        Elements cols = null;
        String kart_basma_col_text, surucu, sicil_no;
        try {
            table = document.select("table");
            rows = table.select("tr");
            for (int i = 2; i < rows.size(); i++) {
                row = rows.get(i);
                cols = row.select("td");
                kart_basma_col_text = cols.get(4).text();
                //System.out.println(kart_basma_col_text);

                try {
                    surucu = Common.regex_trim(kart_basma_col_text.substring(25));
                    sicil_no = Common.regex_trim(cols.get(4).text()).substring(16, 22);
                    if (kart_basma_col_text.contains("PDKS_Kart Binen ")) {
                        if( !data.containsKey(sicil_no)) data.put( sicil_no, surucu );

                        //put("sicil_no", Common.regex_trim(cols.get(4).text()).substring(16, 22));
                        //data.put("isim", Common.regex_trim(kart_basma_col_text.substring(25)) );
                    } else if ((kart_basma_col_text.contains("PDKS_Kart inen"))) {
                        // todo inen binen pdks tema yapilicak
                        /*data.put("sicil_no", Common.regex_trim(cols.get(4).text()).substring(16, 22));
                        data.put("isim", Common.regex_trim(kart_basma_col_text.substring(25)) );

                        sicil_no = Common.regex_trim(cols.get(4).text()).substring(15, 21);
                        isim = Common.regex_trim(kart_basma_col_text.substring(24));*/
                    }

                    //System.out.println(oto + " PDKS --> [" + tip + " " + sicil_no + "] [" + isim + "]");
                } catch( NullPointerException | IndexOutOfBoundsException e ){
                    e.printStackTrace();
                }
                cols.clear();
            }
            rows.clear();
        } catch( NullPointerException e ){
            System.out.println( "["+Common.get_current_hmin() + "]  "+ aktif_tarih  + " " +  oto + " ORER sürücü PDKS ayıklama hatası. Tekrar deneniyor.");
            e.printStackTrace();
            data = new HashMap<>();
        }
    }
    // noktaya istek, surucu isim ve telefon alma
    private void surucu_noktaya_istek(){
        org.jsoup.Connection.Response nokta_req = istek_yap("http://filo5.iett.gov.tr/_FYS/000/uyg.0.2.php?abc=1&talep=5&grup=0&hat=");
        Document nokta_doc;
        try {
            nokta_doc = nokta_parse_html( nokta_req );
            nokta_ayikla( nokta_doc );
        } catch( NullPointerException e ){
            // sürücü bilgisi yok noktada, bir keresinde veritabanı hatası falan vermişti onun için önlem
        }

    }
    private Document nokta_parse_html( org.jsoup.Connection.Response req ){
        Document doc;
        try {
            doc = req.parse();
            if( doc.select("body").text().contains("Database") ){
                System.out.println(  "["+Common.get_current_hmin() + "]  "+ aktif_tarih  + " " +  oto + " Sürücü detay, Veri yok");
                return null;
            } else {
                return doc;
            }
        } catch (IOException | NullPointerException e) {
            System.out.println( "["+Common.get_current_hmin() + "]  "+ aktif_tarih  + " " +  oto + "Surucu detay parse hatası. Tekrar deneniyor.");
            e.printStackTrace();
        }
        return null;
    }
    private void nokta_ayikla( Document document ){
        try {
            if( document == null ){}
        } catch( NullPointerException e ){
            e.printStackTrace();
            yap();
        }
        Elements table_sur = document.select("table");
        String surucu_string = table_sur.select("tr").get(1).getAllElements().get(2).text();
        surucu_string = surucu_string.substring(2);
        String[] surucu_split_data = surucu_string.split(" ");
        String surucu_ad = "";
        for (int j = 1; j < surucu_split_data.length - 1; j++) {
            if( j < surucu_split_data.length - 2 ){
                surucu_ad += surucu_split_data[j] + " ";
            } else {
                surucu_ad += surucu_split_data[j];
            }
        }
        if( !surucu_ad.equals("") && !surucu_ad.equals("-1")){
            //Surucu_Data surucu = new Surucu_Data();
            //surucu.ekle( Common.regex_trim(surucu_split_data[0]), surucu_ad, surucu_split_data[surucu_split_data.length - 1].substring(1, surucu_split_data[surucu_split_data.length - 1].length() - 1 ) );
            //System.out.println(oto + " SÜrücü detay alindi -> [" + surucu_split_data[0] + "] " + surucu_ad );
        }
    }
}

