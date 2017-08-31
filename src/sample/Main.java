package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.JSONObject;

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
    private GButton giris_button;
    private String kontrol_eposta;
    private long KONTROL_TIMESTAMP = Common.get_unix();
    private boolean onay_kontrol_aktif = false;
    private Timer onay_sayac;

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

        acilis_popup_olustur("", false );
        alt_notf.setText("GİRİŞ KONTROLLERİ YAPILIYOR...");

        scene = new Scene(root, 660, popup_height);
        stage.setScene(scene);
        stage.show();
        scene.getWindow().setHeight( popup_height );

        Thread baslangic_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int kontrol = User_Config.baslangic_config_kontrol();
                if( kontrol == User_Config.CONFIG_YOK_MOD ){
                    System.out.println("[ BAŞLANGIÇ KULLANICI KONTROLÜ ] CONFIG DOSYASI YOK");
                    User_Config.config_dosyasi_olustur();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            form_olustur();
                        }
                    });
                    // alert + system exit
                } else if( kontrol == User_Config.GIRIS_MOD ) {
                    System.out.println("[ BAŞLANGIÇ KULLANICI KONTROLÜ ] GİRİŞ YAP");
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            giris_kayit_sunucu_donus_kod_kontrol( User_Config.ilk_acilis_kullanici_kontrolu(), false );
                            kontrol_eposta = User_Config.eposta_veri_al();
                        }
                    });
                } else if( kontrol == User_Config.KAYIT_MOD ){
                    System.out.println("[ BAŞLANGIÇ KULLANICI KONTROLÜ ] KAYIT FORMUNU AÇ");

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            form_olustur();
                        }
                    });
                }
            }
        });
        baslangic_thread.setDaemon(true);
        baslangic_thread.start();
    }

    private void giris_kayit_sunucu_donus_kod_kontrol( JSONObject data, boolean form_submit ){
        int kontrol_kod = data.getInt("kod");
        String default_config_json = data.getJSONObject("data").getString("default_config_json");
        if( kontrol_kod == User_Config.GIRIS_ONAYBEKLENIYOR ){
            System.out.println("[ KULLANICI KONTROL KOD ] GİRİŞ ONAY BEKLENİYOR");
            acilis_popup_olustur( kontrol_eposta, true );
            scene.getWindow().setHeight( popup_height );
            alt_notf.setText( "ONAY BEKLENİYOR" );
            onay_sayac_baslat();
        } else if( kontrol_kod == User_Config.KAYIT_ONAYBEKLENIYOR ){
            System.out.println("[ KULLANICI KONTROL KOD ] KAYIT ONAY BEKLENİYOR");
            acilis_popup_olustur( kontrol_eposta, true );
            scene.getWindow().setHeight( popup_height );
            alt_notf.setText( "ONAY BEKLENİYOR" );
            onay_sayac_baslat();
        } else if( kontrol_kod == User_Config.HATA ){
            System.out.println("[ KULLANICI KONTROL KOD ] HATA");
            alt_notf.setText( data.getString("text") );
            if( form_submit ){
                input_email.setDisable(false);
                input_pass.setDisable(false);
                giris_button.setDisable(false);
            }
        } else if( kontrol_kod == User_Config.SIFRE_BOSGELDI ){
            System.out.println("[ KULLANICI KONTROL KOD ] ŞİFRE BOŞ GELDİ ");
            User_Config.config_reset();
            form_olustur();
        } else if( kontrol_kod == User_Config.GIRIS_ONAYLANDI ){
            System.out.println("[ KULLANICI KONTROL KOD ] GİRİŞ ONAYLANDI");
            User_Config.config_guncelle( default_config_json );
            try {
                onay_sayac.cancel();
            } catch ( NullPointerException e ){
                //e.printStackTrace();
            }
            Filo_Login_Task filo_login_task = new Filo_Login_Task();
            filo_login_task.yap();
            stage.close();
            try {
                System.out.println("OK");
                Takip_Scene main_scene = new Takip_Scene();
                main_scene.start(new Stage());
            } catch( Exception e ){
                e.printStackTrace();
            }

        }
    }

    private void onay_sayac_baslat(){
        if( onay_kontrol_aktif ) return;
        onay_sayac = new Timer();
        onay_sayac.schedule( new TimerTask(){
            @Override
            public void run(){
                // arka plan islemi oldugu icin runlaterla calistiriyoruz ( BUNU ANLAMAK LAZIM )
                Platform.runLater(new Runnable(){
                    @Override
                    public void run(){
                        giris_kayit_sunucu_donus_kod_kontrol( User_Config.kullanici_kontrol( kontrol_eposta ), false );
                    }
                });

            }
        }, 0, 10000);
        onay_kontrol_aktif = true;
    }

    private void acilis_popup_olustur( String eposta, boolean giris_onay ){

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
        Label giris_label = new Label(eposta);
        giris_label.getStyleClass().addAll("h3-beyaz", "padtopbot10", "fbold");
        if( giris_onay ){
            GButton degistir = new GButton("Farklı Hesap Kullan", GButton.CGRIK);
            HBox hesap = new HBox( giris_label, degistir );
            hesap.setAlignment(Pos.CENTER);
            HBox.setMargin(degistir, new Insets(3, 0, 0, 10) );
            degistir.setOnAction( ev -> {
                User_Config.config_reset();
                kontrol_eposta = "";
                try {
                    onay_sayac.cancel();
                } catch ( NullPointerException e ){
                    e.printStackTrace();
                }
                form_olustur();
            });
            start_row_mor.getChildren().addAll( logo, hesap );
        } else {
            start_row_mor.getChildren().addAll( logo, giris_label );
        }
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
        Label form_info = new Label("Hesabınız yoksa otomatik olarak oluşturulacaktır.");
        form_info.getStyleClass().addAll("fs13", "cbeyaz", "fbold");
        Label form_info_2 = new Label( "Onay işleminden sonra program açılacaktır.");
        form_info_2.getStyleClass().addAll("fs12", "cbeyaz", "flight");
        final Label form_info_notf = new Label();
        form_info_notf.getStyleClass().addAll("fs13", "cbeyaz", "fbold");
        giris_button = new GButton("Giriş Yap", GButton.CGRIB);
        form_section.setSpacing(5.0);
        input_email = new GInputGrup( "Eposta",  new GTextField(GTextField.UZUN) );
        input_pass = new GInputGrup( "Şifre",  new GPasswordField(GTextField.UZUN) );
        form_section.getChildren().addAll( form_info, form_info_2, form_info_notf, input_email, input_pass, giris_button );

        giris_button.setOnAction( ev -> {
            String  eposta = input_email.get_input_val(),
                    pass  = input_pass.get_pass_val();
            if( eposta.equals("") || pass.equals("") ){
                form_info_notf.setText("Formda eksiklikler var!");
            } else {
                form_info_notf.setText("");
                kontrol_eposta = eposta;
                input_email.setDisable(true);
                input_pass.setDisable(true);
                giris_button.setDisable(true);
                alt_notf.setText( "İşlem yapılıyor..." );
                giris_kayit_sunucu_donus_kod_kontrol(  User_Config.giris_kayit_form_submit( eposta, pass ), true  );
            }
        });
        start_row_mor.getChildren().addAll(logo, form_section);
        start_container.getChildren().addAll( start_row_mor, start_row_gri );
        root.setContent( start_container );

    }

    public static void main(String[] args) {
        launch(args);
    }
}
