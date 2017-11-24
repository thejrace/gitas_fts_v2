package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends Application {

    private ScrollPane root;
    private Scene scene;
    private Stage stage;
    private VBox start_container;
    private int popup_height = 286, form_height = 480;
    private Label alt_notf;
    private String config_json_data;
    private GInputGrup input_email;
    private GInputGrup input_pass;
    private GButton giris_button/*, kayit_button*/;
    private String kontrol_eposta;
    private long KONTROL_TIMESTAMP = Common.get_unix();
    private boolean onay_kontrol_aktif = false;
    private Timer onay_sayac;
    private Label form_info_notf;

    @Override
    public void start(Stage primaryStage) throws Exception{
        root = FXMLLoader.load(getClass().getResource("resources/fxml/start.fxml"));
        stage = primaryStage;
        stage.setTitle("Gitaş Filo Takip");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.getIcons().add(new Image(getClass().getResource("resources/img/app_ico.png").toExternalForm()));

        try {
            Font.loadFont( getClass().getResource("resources/font/montserratbold.otf").toExternalForm().replace("%20", " "), 10);
            Font.loadFont( getClass().getResource("resources/font/montserratsemibold.otf").toExternalForm().replace("%20", " "), 10);
            Font.loadFont( getClass().getResource("resources/font/montserratlight.otf").toExternalForm().replace("%20", " "), 10);
        } catch( Exception e ){
            e.printStackTrace();
        }

        User_Config.filo5_cookie = "INIT";

        acilis_popup_olustur();
        alt_notf.setText("GİRİŞ KONTROLLERİ YAPILIYOR...");

        scene = new Scene(root, 660, popup_height);
        stage.setScene(scene);
        stage.show();
        scene.getWindow().setHeight( popup_height );

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {

                int kontrol = User_Config.baslangic_config_kontrol();
                if( kontrol == User_Config.KAYIT_MOD ){
                    // form aç
                    System.out.println("[ BAŞLANGIÇ KULLANICI KONTROLÜ ] KAYIT FORMUNU AÇ");
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            form_olustur();
                        }
                    });
                } else if( kontrol == User_Config.GIRIS_MOD ){
                    // eposta var giriş yap ( beni hatirla )
                    System.out.println("[ BAŞLANGIÇ KULLANICI KONTROLÜ ] GİRİŞ YAP");
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            //giris_kayit_sunucu_donus_kod_kontrol( User_Config.ilk_acilis_kullanici_kontrolu(), false );
                            //kontrol_eposta = User_Config.eposta_veri_al();
                            Thread th = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Web_Request req = new Web_Request( Web_Request.SERVIS_URL2 , "&req=app_data" );
                                        req.kullanici_pc_parametreleri_ekle();
                                        req.action();
                                        JSONObject val = new JSONObject(req.get_value()).getJSONObject("data");
                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    stage.close();
                                                    User_Config.init_app_data("init", val );
                                                    Filo_Captcha_Scene main_scene = new Filo_Captcha_Scene();
                                                    main_scene.start(new Stage());
                                                } catch(Exception e ){
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    } catch( JSONException e ){
                                        e.printStackTrace();
                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                stage.close();
                                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                                alert.setTitle("Gitaş Filo Takip Sistemi");
                                                alert.setHeaderText("Hata oluştu. Kod: KYB_1");
                                                alert.setContentText("Sistem yöneticisine hatayı bildirin.");
                                                ButtonType button_gonder = new ButtonType("Bildirim Gönder");
                                                ButtonType button_iptal = new ButtonType("İptal", ButtonBar.ButtonData.CANCEL_CLOSE);
                                                alert.getButtonTypes().setAll(button_gonder, button_iptal );
                                                Optional<ButtonType> result = alert.showAndWait();
                                                if (result.get() == button_gonder ){
                                                    Web_Request req = new Web_Request( Web_Request.SERVIS_URL2 , "&req=app_hata_log&hata_kodu=KYB_1" );
                                                    req.kullanici_pc_parametreleri_ekle();
                                                    req.action();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                            th.setDaemon(true);
                            th.start();
                        }
                    });
                } else if( kontrol == User_Config.CONFIG_YOK_MOD ){
                    // config olustur
                    System.out.println("[ BAŞLANGIÇ KULLANICI KONTROLÜ ] CONFIG DOSYASI YOK");
                    User_Config.config_dosyasi_olustur();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            form_olustur();
                        }
                    });
                }

            }
        });
        th.setDaemon(true);
        th.start();



    }




    private void acilis_popup_olustur( ){
        start_container = new VBox();
        start_container.getStyleClass().add("start-wrapper");
        start_container.setAlignment(Pos.CENTER);
        VBox start_row_mor = new VBox();
        start_row_mor.getStyleClass().add("start-row-mor");
        start_row_mor.setAlignment(Pos.CENTER);
        VBox start_row_gri = new VBox();
        start_row_gri.getStyleClass().add("start-row-gri");
        start_row_gri.setAlignment(Pos.CENTER);
        alt_notf = new Label("");
        alt_notf.getStyleClass().addAll("fs14", "flight", "cbeyaz");
        start_row_gri.getChildren().add(alt_notf);
        ImageView logo = new ImageView( new Image(getClass().getResource("resources/img/acilis_logo.png").toExternalForm()));
        Label giris_label = new Label("");
        giris_label.getStyleClass().addAll("h3-beyaz", "padtopbot10", "fbold");
        start_row_mor.getChildren().addAll( logo, giris_label );
        start_container.getChildren().addAll( start_row_mor, start_row_gri );
        root.setContent( start_container );
    }

    private void form_olustur(){
        scene.getWindow().setHeight( form_height );
        start_container = new VBox();
        start_container.getStyleClass().add("start-wrapper");
        start_container.setAlignment(Pos.CENTER);
        VBox start_row_mor = new VBox();
        start_row_mor.getStyleClass().add("start-row-mor");
        start_row_mor.setAlignment(Pos.CENTER);
        VBox start_row_gri = new VBox();
        start_row_gri.getStyleClass().add("start-row-gri");
        start_row_gri.setAlignment(Pos.CENTER);
        alt_notf = new Label("");
        alt_notf.getStyleClass().addAll("fs14", "flight", "cbeyaz");
        alt_notf.setText( "Giriş bilgileri bekleniyor..." );
        start_row_gri.getChildren().add(alt_notf);
        ImageView logo = new ImageView( new Image(getClass().getResource("resources/img/acilis_logo.png").toExternalForm()));
        VBox form_section = new VBox();
        form_section.getStyleClass().add("acilis-form-section");
        form_section.setAlignment(Pos.CENTER);
        Label form_info = new Label("Hesabınız yoksa sistem yöneticisi ile irtibat kurun.");
        form_info.getStyleClass().addAll("fs13", "cbeyaz", "fbold");
        Label form_info_2 = new Label( "Onay işleminden sonra program açılacaktır.");
        form_info_2.getStyleClass().addAll("fs12", "cbeyaz", "flight");
        form_info_notf = new Label();
        form_info_notf.getStyleClass().addAll("fs13", "cbeyaz", "fbold");
        HBox butonlar = new HBox();
        butonlar.setSpacing(10.0);
        butonlar.setAlignment(Pos.CENTER);
        giris_button = new GButton("Giriş Yap", GButton.CGRIB);
        //kayit_button = new GButton("Kayıt Ol", GButton.CGRIB );
        butonlar.getChildren().addAll( giris_button/*, kayit_button */);
        form_section.setSpacing(5.0);
        input_email = new GInputGrup( "Eposta",  new GTextField(GTextField.UZUN) );
        input_pass = new GInputGrup( "Şifre",  new GPasswordField(GTextField.UZUN) );
        form_section.getChildren().addAll( form_info, form_info_2, form_info_notf, input_email, input_pass, butonlar );

        giris_button.setOnAction( ev -> {
            form_submit( "giris" );
        });
        /*kayit_button.setOnAction( ev -> {
            form_submit( "kayit" );
        });*/
        start_row_mor.getChildren().addAll(logo, form_section);
        start_container.getChildren().addAll( start_row_mor, start_row_gri );
        root.setContent( start_container );

    }

    private void form_submit( String form_type ){
        String  eposta = input_email.get_input_val(),
                pass  = input_pass.get_pass_val();
        if( eposta.equals("") || pass.equals("") ){
            alt_notf.setText("Formda eksiklikler var!");
        } else {
            form_info_notf.setText("");
            kontrol_eposta = eposta;
            input_email.setDisable(true);
            input_pass.setDisable(true);
            giris_button.setDisable(true);
            //kayit_button.setDisable(true);
            alt_notf.setText( "İşlem yapılıyor..." );

            Thread th = new Thread(new Task<Void>() {
                private JSONObject val;
                @Override
                protected void succeeded(){
                    alt_notf.setText(val.getString("text"));
                    if( val.getInt("ok") == 0 ){
                        input_email.setDisable(false);
                        input_pass.setDisable(false);
                        giris_button.setDisable(false);
                        //kayit_button.setDisable(false);
                    } else {
                        try {
                            User_Config.eposta_guncelle( eposta );
                            stage.close();
                            User_Config.init_app_data("init", val.getJSONObject("data") );
                            Takip_Scene main_scene = new Takip_Scene();
                            main_scene.start(new Stage());
                        } catch( Exception e ){
                            e.printStackTrace();
                        }
                    }
                }
                @Override
                protected Void call(){
                    Web_Request req = new Web_Request( Web_Request.SERVIS_URL , "&req=form&form_type="+form_type+"&eposta="+eposta+"&pass="+pass+"&bilgisayar_adi="+Common.bilgisayar_adini_al()+"&bilgisayar_hash="+ DigestUtils.sha256Hex(Common.bilgisayar_adini_al()) );
                    req.action();
                    val = new JSONObject(req.get_value());
                    return null;
                }
            });
            th.setDaemon(true);
            th.start();

            //giris_kayit_sunucu_donus_kod_kontrol(  User_Config.giris_kayit_form_submit( eposta, pass ), true  );
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
