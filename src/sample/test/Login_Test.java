package sample.test;


import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.jsoup.Jsoup;

import javax.naming.MalformedLinkException;
import java.io.IOException;
import java.net.*;
import java.util.List;

public class Login_Test extends Application{


    private static WebView wv;
    private static WebEngine we;
    private static AnchorPane root;
    private static URI uri = null;
    @Override
    public void start(Stage primaryStage) throws Exception{
        root = FXMLLoader.load(getClass().getResource("../resources/fxml/test.fxml"));
        primaryStage.setTitle("Gitaş FTS Sunucu Versiyon");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();



        //web_view_init();


        /*Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                login_thread("A", "dk_oasa", "oas145" );
                login_thread("B", "dk_oasb", "oas125" );
                login_thread("C", "dk_oasc", "oas165" );
            }
        });
        th.start();*/


    }


    private static void web_view_init(){

        wv = new WebView();
        we = wv.getEngine();
        try {
            URL url = new URL("http://filo5.iett.gov.tr/login.php?sayfa=/_FYS.php&aday=x");

            try {



                we.setJavaScriptEnabled(true);
                we.getLoadWorker().stateProperty().addListener(
                        (ObservableValue<? extends Worker.State> observable,
                         Worker.State oldValue,
                         Worker.State newValue) -> {
                            if( newValue != Worker.State.SUCCEEDED ) {
                                return;
                            }
                            we.executeScript(" " +
                                    "var form_login = document.querySelectorAll('[name=\"login\"]');" +
                                    "var form_pass = document.querySelectorAll('[name=\"password\"]');" +
                                    "form_login[0].value=\"dk_oasa\"; form_pass[0].value=\"oas145\";  " +
                                    "var divo = document.createElement(\"div\"); divo.id = \"hederoy\"; document.body.appendChild(divo); document.getElementById(\"hederoy\").innerHTML = document.cookie;");
                        } );
                we.load(url.toString());



                root.getChildren().add(wv);

            } catch( NullPointerException e ){
                e.printStackTrace();
            }

        } catch( MalformedURLException e ){
            e.printStackTrace();
        }




    }

    /*
    * Filoya webview le giriş yaptirip phpsessid yi calmam lazim
    *
    * */
    private static void login_thread( String bolge, String kullanici, String pass, String captcha, String psid ){
        org.jsoup.Connection.Response res;
        try{
            System.out.println( bolge + " Bölgesi filoya giriş yapılıyor..");
            //res = Jsoup.connect("http://filo5.iett.gov.tr/login.php?sayfa=/_FYS.php&aday=x")
            res = Jsoup.connect("http://filo5.iett.gov.tr/_FYS.php")
                    .data("login", kullanici, "password", pass, "captcha", captcha )
                    .cookie("PHPSESSID", psid )
                    .method(org.jsoup.Connection.Method.POST)
                    .timeout(0)
                    .execute();



            System.out.println(res.parse());

            //kaydet();
        } catch( IOException e ){
            System.out.println( bolge + " Bölgesi filo giriş hatası tekrar deneniyor.");
            //login_thread( bolge, kullanici, pass );
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}