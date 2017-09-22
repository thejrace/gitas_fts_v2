package sample;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.json.JSONObject;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jeppe on 11.03.2017.
 */
public class Secenekler_Popup {

    private TabPane secenekler_tab;
    private Obarey_Popup secenekler_popup;

    private int EKSEFER_ONERI_DONE = 0;
    private Map<String, ArrayList<Oneri_Sefer_Data>> EKSEFER_TABS = new HashMap<>();
    private GButton eksefer_btn, surucu_rapor_btn, zayiat_rapor_btn, hat_rapor_btn, otobus_rapor_btn, plaka_rapor_btn;
    private Node root;

    public void init( Node root ){

        this.root = root;
        secenekler_tab = new TabPane();
        if( User_Config.izin_kontrol(User_Config.ISE_EXCEL) ) secenekler_tab.getTabs().add( excel_rapor_init() );
        if( User_Config.izin_kontrol(User_Config.ISE_EKSEFERONERI) ) secenekler_tab.getTabs().add( eksefer_oneri_init() );
        if( User_Config.izin_kontrol(User_Config.ISE_ISTATISTIK_RAPOR) ) secenekler_tab.getTabs().add( istatistik_rapor_init() );
        if( User_Config.izin_kontrol(User_Config.ISE_ZAYI_RAPORLAR) ) secenekler_tab.getTabs().add( raporlar_init() );
        secenekler_tab.getTabs().add( geribildirim_init() );
        secenekler_popup = new Obarey_Popup("Seçenekler", root );
        secenekler_popup.init( true );
        secenekler_popup.set_content( ( secenekler_tab ));

    }

    public void show( double x, double y ){
        secenekler_popup.show( x, y );
    }

    private Tab geribildirim_init(){

        Tab geribildirim_tab = new Tab("Geri Bildirim");
        geribildirim_tab.setClosable(false);

        Label geribildirim_tab_header = new Label("Geri Bildirim");
        geribildirim_tab_header.getStyleClass().addAll("fbold", "cbeyaz", "fs14");

        ScrollPane geribildirim_wrapper  = new ScrollPane();
        geribildirim_wrapper.setMinWidth(700);
        geribildirim_wrapper.setPrefHeight(250);
        VBox geribildirim_tab_cont = new VBox();
        geribildirim_tab_cont.setMinWidth(700);
        geribildirim_tab_cont.setPrefHeight(250);
        geribildirim_tab_cont.setAlignment(Pos.TOP_CENTER);
        geribildirim_tab_cont.setSpacing(20);
        geribildirim_tab_cont.getStyleClass().add("secenekler-tab");
        geribildirim_tab_cont.setPadding(new Insets( 10, 10, 20, 10 ));

        TextArea gb_textarea = new TextArea();
        final Label info = new Label( "Hata, öneri, istek bildirimi yapın.");
        info.getStyleClass().addAll("cbeyaz", "fs12");
        GButton gonder_btn = new GButton("Gönder", GButton.CMORK );

        GInputGrup tip_select = new GInputGrup("Konu", new GTextField(GTextField.UZUN, "") );

        geribildirim_tab_cont.getChildren().addAll( geribildirim_tab_header, info, tip_select, gb_textarea, gonder_btn );
        geribildirim_wrapper.setContent(geribildirim_tab_cont );
        geribildirim_tab.setContent( geribildirim_wrapper );

        gonder_btn.setOnAction(ev->{
            gonder_btn.setText( "Mesaj gönderiliyor..." );
            gonder_btn.setDisable(true);
            String  konu = tip_select.get_input_val(),
                    mesaj = gb_textarea.getText();


            if( konu.equals("") || mesaj.equals("") ){
                info.setText( "Formda eksiklikler var!" );
                gonder_btn.setText( "Gönder" );
                gonder_btn.setDisable(false);
                return;
            }

            /* TODO Lisans_Kontrol kontrol = new Lisans_Kontrol(User_Config.get_eposta());
            kontrol.set_req_type("geribildirim");
            kontrol.set_extra_data("&konu="+konu+"&mesaj="+mesaj);

            Thread th = new Thread( kontrol );
            th.setDaemon(true);
            th.start();

            kontrol.setOnSucceeded(evo->{
                JSONObject output = kontrol.getValue();
                gonder_btn.setDisable(false);
                gonder_btn.setText( "Gönder" );
                int ok = output.getInt("ok");
                if( ok == 1 ){
                    info.setText("Mesajınız bize ulaştı teşekkürler." );
                } else {
                    info.setText("Bir hata oluştu. Lütfen tekrar deneyin." );
                }
            });*/

        });

        return geribildirim_tab;

    }

    private Tab raporlar_init(){
        Tab tab = new Tab("Zayi Raporlar");
        tab.setClosable(false);

        Label tab_header = new Label("Zayi Raporlar");
        tab_header.getStyleClass().addAll("fbold", "cbeyaz", "fs14");

        ScrollPane wrapper  = new ScrollPane();
        wrapper.setMinWidth(700);
        wrapper.setPrefHeight(250);
        VBox tab_cont = new VBox();
        tab_cont.setMinWidth(700);
        tab_cont.setPrefHeight(250);
        tab_cont.setAlignment(Pos.TOP_CENTER);
        tab_cont.getStyleClass().add("secenekler-tab");
        tab_cont.setFillWidth(true);
        tab_cont.setPadding(new Insets( 10, 10, 20, 10 ));

        surucu_rapor_btn = new GButton("Sürücü - Zayi Sefer Raporları", GButton.CMORB );
        hat_rapor_btn = new GButton("Hat - Zayi Sefer Raporları", GButton.CMORB );
        plaka_rapor_btn = new GButton("Plaka - Zayi Sefer Raporları", GButton.CMORB );
        otobus_rapor_btn = new GButton("Otobüs - Zayi Sefer Raporları", GButton.CMORB );
        zayiat_rapor_btn = new GButton("Tarih - Zayi Sefer Raporları", GButton.CMORB );


        zayiat_rapor_btn.setOnMousePressed( ev -> {
            zayiat_popup_init(Popup_Zayiat_Rapor_Box.TARIH, "Tarih - Zayi Sefer Raporları", zayiat_rapor_btn.getScene().getRoot(), ev.getScreenX(), ev.getScreenY() );
        });

        otobus_rapor_btn.setOnMousePressed( ev -> {
            zayiat_popup_init(Popup_Zayiat_Rapor_Box.OTOBUS, "Otobüs - Zayi Sefer Raporları", otobus_rapor_btn.getScene().getRoot(), ev.getScreenX(), ev.getScreenY() );
        });

        plaka_rapor_btn.setOnMousePressed( ev -> {
            zayiat_popup_init(Popup_Zayiat_Rapor_Box.PLAKA, "Plaka - Zayi Sefer Raporları", plaka_rapor_btn.getScene().getRoot(), ev.getScreenX(), ev.getScreenY() );
        });

        hat_rapor_btn.setOnMousePressed( ev -> {
            zayiat_popup_init(Popup_Zayiat_Rapor_Box.HAT, "Hat - Zayi Sefer Raporları", hat_rapor_btn.getScene().getRoot(), ev.getScreenX(), ev.getScreenY() );
        });

        surucu_rapor_btn.setOnMousePressed( ev -> {
            zayiat_popup_init(Popup_Zayiat_Rapor_Box.SURUCU, "Sürücü - Zayi Sefer Raporları", surucu_rapor_btn.getScene().getRoot(), ev.getScreenX(), ev.getScreenY() );
        });


        tab_cont.setSpacing(20);
        tab_cont.getChildren().addAll(tab_header, zayiat_rapor_btn, otobus_rapor_btn, plaka_rapor_btn, hat_rapor_btn, surucu_rapor_btn );

        wrapper.setContent(tab_cont );
        tab.setContent( wrapper );


        return tab;

    }
    private void zayiat_popup_init( String dtkey, String title, Node root, double x, double y ){
        Thread th = new Thread( new Task<Void>(){
            private Obarey_Popup popup;
            @Override
            protected Void call(){
                Popup_Zayiat_Rapor_Box rb = new Popup_Zayiat_Rapor_Box(dtkey);
                popup = new Obarey_Popup(title, root );
                popup.init(true);
                popup.set_content( rb );
                return null;
            }

            @Override
            protected void succeeded(){
                popup.show(x, y);
            }

        });
        th.setDaemon(true);
        th.start();
    }

    private Tab istatistik_rapor_init(){

        Tab km_tab = new Tab("İstatistikler");
        km_tab.setClosable(false);

        Label km_tab_header = new Label("İstatistikler");
        km_tab_header.getStyleClass().addAll("fbold", "cbeyaz", "fs14");

        ScrollPane km_wrapper  = new ScrollPane();
        km_wrapper.setMinWidth(700);
        km_wrapper.setPrefHeight(250);
        VBox km_tab_cont = new VBox();
        km_tab_cont.setMinWidth(700);
        km_tab_cont.setPrefHeight(250);
        km_tab_cont.setAlignment(Pos.TOP_CENTER);
        km_tab_cont.setSpacing(20);
        km_tab_cont.getStyleClass().add("secenekler-tab");
        km_tab_cont.setPadding(new Insets( 10, 10, 20, 10 ));

        final Label lbl_notf = new Label("Otobüs istatistiklerini raporla.");
        lbl_notf.getStyleClass().addAll("fs13");

        VBox tarih_filtre = new VBox();
        tarih_filtre.setSpacing(20);
        tarih_filtre.setAlignment(Pos.CENTER);

        VBox gunluk_filtre_item = new VBox();
        HBox gunluk_filtre_content = new HBox();
        gunluk_filtre_content.setSpacing(10);

        final GButton gunluk_rapor_btn = new GButton("Raporu Oluştur", GButton.CMORK );
        final DatePicker gunluk_dp = new DatePicker();
        gunluk_dp.setValue(Common.dp_placeholder(Common.get_current_date()));
        Label gunluk_filtre_header = new Label("Günlük");
        gunluk_filtre_header.getStyleClass().addAll("fbold", "cbeyaz", "fs11");
        gunluk_filtre_item.setSpacing(10);
        gunluk_filtre_item.setAlignment(Pos.CENTER);

        gunluk_filtre_content.getChildren().addAll( gunluk_dp, gunluk_rapor_btn );
        gunluk_filtre_content.setAlignment(Pos.CENTER);
        gunluk_filtre_item.getChildren().addAll( gunluk_filtre_header, gunluk_filtre_content );


        VBox aralik_filtre_item = new VBox();
        HBox aralik_filtre_content = new HBox();
        aralik_filtre_item.setAlignment(Pos.CENTER);
        aralik_filtre_content.setAlignment(Pos.CENTER);
        aralik_filtre_content.setSpacing(10);
        aralik_filtre_item.setSpacing(10);
        final GButton aralik_rapor_btn = new GButton("Raporu Oluştur", GButton.CMORK );
        Label aralik_filtre_header = new Label("Tarih Aralığı");
        aralik_filtre_header.getStyleClass().addAll("fbold", "cbeyaz", "fs11");

        final DatePicker baslangic_dp = new DatePicker();
        final DatePicker bitis_dp = new DatePicker();
        bitis_dp.setValue(Common.dp_placeholder(Common.get_current_date()));
        aralik_filtre_content.getChildren().addAll( baslangic_dp, bitis_dp, aralik_rapor_btn );
        aralik_filtre_item.getChildren().addAll( aralik_filtre_header, aralik_filtre_content );

        final GButton tum_rapor = new GButton("Tüm Filo Raporu Oluştur", GButton.CMORK );

        tarih_filtre.getChildren().addAll( gunluk_filtre_item, aralik_filtre_item, tum_rapor );

        km_tab_cont.getChildren().addAll( km_tab_header, lbl_notf, tarih_filtre );
        km_wrapper.setContent( km_tab_cont );
        km_tab.setContent( km_wrapper );

        gunluk_rapor_btn.setOnMousePressed(ev->{
            String  gun   = gunluk_dp.getValue().toString();
            if( gun.equals("") ) return;
            Rapor_Popup rapor_popup = new Rapor_Popup( Rapor_Box_Toplam.TOPLAM_FILO, "OBAREY", gun, "" );
            rapor_popup.init( ev.getScreenX(), ev.getScreenY(), root );
            rapor_popup.show();
        });

        aralik_rapor_btn.setOnMousePressed(ev->{
            String  baslangic = baslangic_dp.getValue().toString(),
                    bitis = bitis_dp.getValue().toString();
            if( baslangic.equals("") ||  bitis.equals("") ) return;
            Rapor_Popup rapor_popup = new Rapor_Popup(Rapor_Box_Toplam.TOPLAM_FILO, "OBAREY", baslangic, bitis );
            rapor_popup.init( ev.getScreenX(), ev.getScreenY(), root );
            rapor_popup.show();
        });

        tum_rapor.setOnMousePressed(ev->{
            Rapor_Popup rapor_popup = new Rapor_Popup( Rapor_Box_Toplam.TOPLAM_FILO, "OBAREY","", "" );
            rapor_popup.init( ev.getScreenX(), ev.getScreenY(), root );
            rapor_popup.show();
        });

        return km_tab;
    }

    private Tab eksefer_oneri_init(){

        Tab eksefer_tab = new Tab("Ek Sefer Önerileri");
        eksefer_tab.setClosable(false);

        Label eksefer_tab_header = new Label("Ek Sefer Önerileri");
        eksefer_tab_header.getStyleClass().addAll("fbold", "cbeyaz", "fs14");

        ScrollPane eksefer_wrapper  = new ScrollPane();
        eksefer_wrapper.setMinWidth(700);
        eksefer_wrapper.setPrefHeight(250);
        VBox eksefer_tab_cont = new VBox();
        eksefer_tab_cont.setMinWidth(700);
        eksefer_tab_cont.setPrefHeight(250);
        eksefer_tab_cont.setAlignment(Pos.TOP_CENTER);
        eksefer_tab_cont.getStyleClass().add("secenekler-tab");
        eksefer_tab_cont.setFillWidth(true);
        eksefer_tab_cont.setPadding(new Insets( 10, 10, 20, 10 ));

        eksefer_btn = new GButton("Önerileri Listele", GButton.CMORB );
        final Label a_notf = new Label("Beklemede..");
        final Label b_notf = new Label("Beklemede..");
        final Label c_notf = new Label("Beklemede..");

        eksefer_tab_cont.setSpacing(20);
        eksefer_tab_cont.getChildren().addAll(eksefer_tab_header, eksefer_btn,a_notf, b_notf, c_notf );

        eksefer_wrapper.setContent(eksefer_tab_cont );
        eksefer_tab.setContent( eksefer_wrapper );

        eksefer_btn.setOnAction( event -> {
            eksefer_btn.setText("İŞLEM YAPILIYOR..");
            eksefer_btn.setDisable(true);

            EKSEFER_ONERI_DONE = 0;
            EKSEFER_TABS.clear();

            Filo_Eksefer_Oneri_Task ek_oneri_a = new Filo_Eksefer_Oneri_Task("A");
            Filo_Eksefer_Oneri_Task ek_oneri_b = new Filo_Eksefer_Oneri_Task("B");
            Filo_Eksefer_Oneri_Task ek_oneri_c = new Filo_Eksefer_Oneri_Task("C");
            Thread ek_oneri_a_th = new Thread( ek_oneri_a );
            Thread ek_oneri_b_th = new Thread( ek_oneri_b );
            Thread ek_oneri_c_th = new Thread( ek_oneri_c );

            ek_oneri_a_th.setDaemon(true);
            ek_oneri_a_th.start();
            ek_oneri_b_th.setDaemon(true);
            ek_oneri_b_th.start();
            ek_oneri_c_th.setDaemon(true);
            ek_oneri_c_th.start();

            a_notf.textProperty().bind( ek_oneri_a.messageProperty() );
            b_notf.textProperty().bind( ek_oneri_b.messageProperty() );
            c_notf.textProperty().bind( ek_oneri_c.messageProperty() );

            ek_oneri_a.setOnSucceeded( ev -> {
                EKSEFER_ONERI_DONE++;
                EKSEFER_TABS.put( "A", ek_oneri_a.getValue());
                eksefer_oneri_cb();
            });

            ek_oneri_b.setOnSucceeded( ev -> {
                EKSEFER_ONERI_DONE++;
                EKSEFER_TABS.put( "B", ek_oneri_b.getValue());
                eksefer_oneri_cb();
            });

            ek_oneri_c.setOnSucceeded( ev -> {
                EKSEFER_ONERI_DONE++;
                EKSEFER_TABS.put( "C", ek_oneri_c.getValue());
                eksefer_oneri_cb();
            });

        });

        return eksefer_tab;
    }

    private void eksefer_oneri_cb(){
        if( EKSEFER_ONERI_DONE == 3 ){
            eksefer_btn.setText("ÖNERİLER LİSTELENİYOR...");
            Obarey_Popup oneri_popup = new Obarey_Popup("Ek Sefer Önerileri", root);
            Thread eksefer_ui_prep = new Thread( new Task<Void>(){
                @Override
                protected void succeeded(){
                    oneri_popup.show(200, 200);
                    eksefer_btn.setText("EKSEFER ÖNERİLERİ");
                    eksefer_btn.setDisable(false);
                }
                @Override
                protected Void call(){

                    TabPane tab_cont = new TabPane();
                    VBox container = new VBox();
                    oneri_popup.init(true);
                    GButton excel_out = new GButton("Excel Çıktısı Al", GButton.CMORK);
                    Label excel_progress = new Label("");
                    HBox excel_cont = new HBox();
                    excel_cont.setAlignment(Pos.CENTER);
                    excel_cont.setPadding(new Insets( 0, 0, 10, 0 ));
                    HBox.setMargin(excel_progress, new Insets(2, 0, 0, 10 ) );
                    excel_cont.getChildren().addAll( excel_out, excel_progress );
                    excel_out.setOnMousePressed( ev -> {
                        Thread eksefer_excel_out = new Thread( new Runnable(){
                            @Override
                            public void run(){
                                Platform.runLater(new Runnable(){ @Override public void run(){ excel_progress.setText("İşlem yapılıyor.."); }});
                                Excel_Eksefer_Onerileri excel_out = new Excel_Eksefer_Onerileri( EKSEFER_TABS.get("A"), EKSEFER_TABS.get("B"), EKSEFER_TABS.get("C") );
                                excel_out.action();

                                Platform.runLater(new Runnable(){ @Override public void run(){ excel_progress.setText("İşlem tamamlandı."); }});
                            }
                        });
                        eksefer_excel_out.setDaemon(true);
                        eksefer_excel_out.start();
                    });

                    Filo_Oneri_Sefer_Table a_oneri_table = new Filo_Oneri_Sefer_Table( EKSEFER_TABS.get("A") );
                    a_oneri_table.init();
                    Tab a_tab = new Tab();
                    a_tab.setText( "A Bölgesi");
                    a_tab.setContent( a_oneri_table.get_ui() );
                    a_tab.setClosable(false);

                    Filo_Oneri_Sefer_Table b_oneri_table = new Filo_Oneri_Sefer_Table( EKSEFER_TABS.get("B") );
                    b_oneri_table.init();
                    Tab b_tab = new Tab();
                    b_tab.setText( "B Bölgesi");
                    b_tab.setContent( b_oneri_table.get_ui() );
                    b_tab.setClosable(false);

                    Filo_Oneri_Sefer_Table c_oneri_table = new Filo_Oneri_Sefer_Table( EKSEFER_TABS.get("C") );
                    c_oneri_table.init();
                    Tab c_tab = new Tab();
                    c_tab.setText( "C Bölgesi");
                    c_tab.setContent( c_oneri_table.get_ui() );
                    c_tab.setClosable(false);

                    tab_cont.getTabs().addAll( a_tab, b_tab, c_tab );
                    container.getChildren().addAll( excel_cont, tab_cont );
                    oneri_popup.set_content(container);
                    return null;
                }
            });
            eksefer_ui_prep.setDaemon(true);
            eksefer_ui_prep.start();

        }
    }

    private Tab excel_rapor_init(){

        Tab excel_tab = new Tab("Excel Çıktı");
        excel_tab.setClosable(false);

        Label excel_tab_header = new Label("Excel Çıktı");
        excel_tab_header.getStyleClass().addAll("fbold", "cbeyaz", "fs14");

        ScrollPane excel_wrapper  = new ScrollPane();
        excel_wrapper.setMinWidth(700);
        excel_wrapper.setPrefHeight(250);
        VBox excel_tab_cont = new VBox();
        excel_tab_cont.setMinWidth(700);
        excel_tab_cont.setPrefHeight(250);
        excel_tab_cont.setAlignment(Pos.TOP_CENTER);
        excel_tab_cont.setSpacing(20);
        excel_tab_cont.getStyleClass().add("secenekler-tab");
        excel_tab_cont.setPadding(new Insets( 10, 10, 20, 10 ));
        final DatePicker excel_dp = new DatePicker();
        excel_dp.setValue(Common.dp_placeholder(Common.get_current_date()));
        final Label lbl_notf = new Label("Sefer verilerinin excel çıktısını al.");
        lbl_notf.getStyleClass().addAll("fs13");
        HBox button_cont = new HBox();
        button_cont.setAlignment(Pos.CENTER);
        button_cont.setSpacing(10);
        GButton excel_btn = new GButton("GÜNLÜK RAPOR", GButton.CMORK);
        GButton surucu_excel_btn = new GButton( "SÜRÜCÜ RAPOR", GButton.CMORK);
        GButton iys_excel_btn = new GButton( "IYS RAPOR", GButton.CMORK);
        GButton pdks_excel_btn = new GButton( "PDKS RAPOR", GButton.CMORK);
        GButton mesaj_excel_btn = new GButton( "MESAJLAR RAPOR", GButton.CMORK);
        button_cont.getChildren().addAll( excel_btn, surucu_excel_btn, pdks_excel_btn, iys_excel_btn, mesaj_excel_btn );

        CheckBox cb_tamam = new CheckBox("Tamam");
        CheckBox cb_bekleyen = new CheckBox("Bekleyen");
        CheckBox cb_aktif = new CheckBox("Aktif");
        CheckBox cb_iptal = new CheckBox("İptal");
        CheckBox cb_yarim = new CheckBox("Yarım");
        CheckBox cb_plaka = new CheckBox("Plaka Ekle");
        cb_plaka.setSelected(false);
        cb_tamam.setSelected(true);
        cb_bekleyen.setSelected(true);
        cb_aktif.setSelected(true);
        cb_iptal.setSelected(true);
        cb_yarim.setSelected(true);

        HBox cb_cont = new HBox( cb_tamam, cb_bekleyen, cb_aktif, cb_iptal, cb_yarim, cb_plaka );
        cb_cont.setSpacing(15);
        cb_cont.setAlignment(Pos.CENTER);

        excel_tab_cont.getChildren().addAll( excel_tab_header, lbl_notf, excel_dp, cb_cont, button_cont );
        excel_wrapper.setContent( excel_tab_cont );
        excel_tab.setContent( excel_wrapper );

        excel_btn.setOnMousePressed( event -> {
            if( excel_dp.getValue() == null ) return;
            excel_btn.setDisable(true);
            excel_btn.setText("İşlem yapılıyor...");
            Thread th = new Thread( new Task<String>(){

                @Override
                protected String call(){
                    //Platform.runLater( new Runnable(){ @Override public void run(){ lbl_notf.setText("İşlem Yapılıyor.."); }});
                    Excel_Filo_Plan fp = new Excel_Filo_Plan(excel_dp.getValue().toString());
                    fp.init( cb_tamam.isSelected(), cb_bekleyen.isSelected(), cb_aktif.isSelected(), cb_iptal.isSelected(), cb_yarim.isSelected(), cb_plaka.isSelected() );
                    Platform.runLater( new Runnable(){ @Override public void run(){
                        //lbl_notf.setText("İşlem Tamamlandı.");
                        excel_btn.setDisable(false);
                        excel_btn.setText("GÜNLÜK RAPOR");
                    }});

                    /*if( fp.init( cb_tamam.isSelected(), cb_bekleyen.isSelected(), cb_aktif.isSelected(), cb_iptal.isSelected(), cb_yarim.isSelected(), cb_plaka.isSelected() ) ){
                        Platform.runLater( new Runnable(){ @Override public void run(){ lbl_notf.setText("İşlem Tamamlandı.."); }});
                    } else {
                        Platform.runLater( new Runnable(){ @Override public void run(){ lbl_notf.setText("Verilen tarihin verisi yok."); }});
                    }*/
                    return null;
                }


            });
            th.setDaemon(true);
            th.start();
        });

        surucu_excel_btn.setOnMousePressed( ev -> {
            if( excel_dp.getValue() == null ) return;
            surucu_excel_btn.setDisable(true);
            surucu_excel_btn.setText("İşlem yapılıyor...");
            Thread th = new Thread( new Task<String>(){
                @Override
                protected String call(){
                    //Platform.runLater( new Runnable(){ @Override public void run(){ lbl_notf.setText("İşlem Yapılıyor.."); }});
                    Excel_Surucu_Rapor fp = new Excel_Surucu_Rapor(excel_dp.getValue().toString());
                    fp.init();
                    Platform.runLater( new Runnable(){ @Override public void run(){
                        //lbl_notf.setText("İşlem Tamamlandı.");
                        surucu_excel_btn.setDisable(false);
                        surucu_excel_btn.setText("SÜRÜCÜ RAPOR");
                    }});
                    /*if( fp.init() ){
                        Platform.runLater( new Runnable(){ @Override public void run(){ lbl_notf.setText("İşlem Tamamlandı.."); }});
                    } else {
                        Platform.runLater( new Runnable(){ @Override public void run(){ lbl_notf.setText("Verilen tarihin verisi yok."); }});
                    }*/
                    return null;
                }
            });
            th.setDaemon(true);
            th.start();
        });

        iys_excel_btn.setOnMousePressed( ev -> {
            if( excel_dp.getValue() == null ) return;
            iys_excel_btn.setDisable(true);
            iys_excel_btn.setText("İşlem yapılıyor...");
            Excel_IYS_Rapor rapor = new Excel_IYS_Rapor( excel_dp.getValue().toString() );
            rapor.on_finish(new Refresh_Listener() {
                @Override
                public void on_refresh() {
                    Platform.runLater( new Runnable(){ @Override public void run(){
                        //lbl_notf.setText("İşlem Tamamlandı.");
                        iys_excel_btn.setDisable(false);
                        iys_excel_btn.setText("IYS RAPOR");
                    }});
                }
            });
            rapor.init();
        });

        pdks_excel_btn.setOnMousePressed( ev -> {
            if( excel_dp.getValue() == null ) return;
            pdks_excel_btn.setDisable(true);
            pdks_excel_btn.setText("İşlem yapılıyor...");
            Excel_PDKS_Rapor rapor = new Excel_PDKS_Rapor( excel_dp.getValue().toString() );
            rapor.on_finish(new Refresh_Listener() {
                @Override
                public void on_refresh() {
                    Platform.runLater( new Runnable(){ @Override public void run(){
                        lbl_notf.setText("İşlem Tamamlandı.");
                        pdks_excel_btn.setDisable(false);
                        pdks_excel_btn.setText("PDKS RAPOR");
                    }});
                }
            });
            rapor.init();
        });

        mesaj_excel_btn.setOnMousePressed( ev -> {
            if( excel_dp.getValue() == null ) return;
            mesaj_excel_btn.setDisable(true);
            mesaj_excel_btn.setText("İşlem yapılıyor...");
            Excel_Mesaj_Rapor rapor = new Excel_Mesaj_Rapor( excel_dp.getValue().toString() );
            rapor.on_finish(new Refresh_Listener() {
                @Override
                public void on_refresh() {
                    Platform.runLater( new Runnable(){ @Override public void run(){
                        lbl_notf.setText("İşlem Tamamlandı.");
                        mesaj_excel_btn.setDisable(false);
                        mesaj_excel_btn.setText("MESAJLAR RAPOR");
                    }});
                }
            });
            rapor.init();
        });



        return excel_tab;

    }


}
