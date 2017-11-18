package sample;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;


public class Filo_Captcha_Controller implements Initializable {

    @FXML
    private WebView wv_1, wv_2, wv_3;
    @FXML
    private TextField tf1_sessid;
    @FXML
    private Button actionbtn;
    @FXML
    private Label info_lbl;

    private int step_index = 0;

    private Refresh_Listener listener;
    private URL url;
    private int login_th_counter = 0;

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

        try {
            url = new URL("http://filo5.iett.gov.tr/login.php?sayfa=/_FYS.php&aday=x");
        } catch( MalformedURLException e ){
            e.printStackTrace();
        }
        User_Config.filo5_cookies = new HashMap<String, String>();
        random_sessid_al();



    }

    public void add_finish_listener( Refresh_Listener _listener ){
        listener = _listener;
    }

    private void web_view_init( final String bolge, final String login, final String password ){
        URI uri = null;
        try {
            uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
        } catch( URISyntaxException | NullPointerException e ){
            e.printStackTrace();
        }
        Map<String, List<String>> headers = new LinkedHashMap<>();
        System.out.println(bolge + " :::: " + User_Config.filo5_cookies.get(bolge));
        headers.put("Set-Cookie", Arrays.asList("PHPSESSID="+User_Config.filo5_cookies.get(bolge)));
        try {
            java.net.CookieHandler.getDefault().put(uri, headers);
        } catch( IOException e ){
            e.printStackTrace();
        }

        WebEngine we = wv_1.getEngine();
        wv_1.setVisible(false);
        try {
            URL url = new URL("http://filo5.iett.gov.tr/login.php?sayfa=/_FYS.php&aday=x");
            we.setJavaScriptEnabled(true);
            we.getLoadWorker().stateProperty().addListener(
                    (ObservableValue<? extends Worker.State> observable,
                     Worker.State oldValue,
                     Worker.State newValue) -> {
                        if (newValue != Worker.State.SUCCEEDED) {
                            return;
                        }



                        try {
                            // ilk açılış veya yanlış captcha girme de calisacak js
                            we.executeScript(" " +
                                    " function hide( elem ){ elem.style.display = \"none\"; } "+
                                    " document.body.style.backgroundColor = \"#302e2e\"; document.body.style.overflowY = \"hidden\";" +
                                    " document.body.style.color = \"#272727\"; document.body.style.fontSize = \"0px\";" +
                                    " var link = document.getElementsByTagName(\"a\"); link[0].style.color= \"#d1d1d1\"; link[0].style.marginLeft = \"-50px\"; link[0].style.fontFamily = \"Tahoma\"; link[0].innerHTML = \"Kodu Değiştir\";" +
                                    " link[0].style.textDecoration = \"none\"; link[0].style.fontSize = \"11\";  link[0].style.fontWeight = \"bold\";  "+
                                    " var trs = document.getElementsByTagName(\"tr\"); hide(trs[0]); hide(trs[2]); hide(trs[3]); " +
                                    " var cin = document.querySelectorAll('[name=\"captcha\"]'); cin[0].style.width = \"45px\"; cin[0].style.position = \"relative\"; cin[0].style.top = \"-68px\"; cin[0].style.left = \"220px\";"+
                                    " var form = document.getElementById(\"aday\"); form.style.marginTop = \"-30px\"; form.style.marginLeft = \"-50px\"; " +
                                    " var submitbtn = document.querySelectorAll('[value=\"Giriş\"]');  submitbtn[0].style.marginTop = \"-68px\"; submitbtn[0].style.marginLeft = \"220px\";" +
                                    " submitbtn[0].style.backgroundColor = \"#7b3275\"; submitbtn[0].style.color = \"#d1d1d1\"; submitbtn[0].style.fontWeight = \"bold\";  submitbtn[0].style.border = \"none\"; " +
                                    " submitbtn[0].style.padding = \"6px 10px 6px 10px\"; submitbtn[0].style.borderRadius = \"3px\"; submitbtn[0].style.fontSize = \"11px\"; submitbtn[0].style.cursor = \"pointer\"; "+
                                    " var form_login = document.querySelectorAll('[name=\"login\"]');" +
                                    " var form_pass = document.querySelectorAll('[name=\"password\"]');" +
                                    " if( form_login[0] != undefined ) hide(form_login[0]); form_login[0].value=\""+login+"\"; if( form_pass[0] != undefined ) hide(form_pass[0]); form_pass[0].value=\""+password+"\";  " +
                                    " var divo = document.createElement(\"div\"); divo.id = \"hederoy\"; document.body.appendChild(divo); document.getElementById(\"hederoy\").innerHTML = document.cookie;");
                        } catch( netscape.javascript.JSException e ){
                            // filo anasayfa
                            we.executeScript("" +
                                    " document.body.style.backgroundColor = \"#302e2e\"; document.body.style.overflowY = \"hidden\";  document.body.style.overflowX = \"hidden\"; var ust = document.getElementById('ust'); ust.style.color = \"#fff\";" +
                                    " ust.innerHTML = \"\"; ust.style.width= \"250px\"; ust.style.borderColor = \"#00ff36\"; ust.style.padding = \"10px\"; " +
                                    " ust.style.textAlign = \"center\"; ust.style.fontSize = \"12px\"; ust.style.color = \"#fff\"; ust.innerHTML = document.cookie.substring(10);  ");
                        }
                        wv_1.setVisible(true);
                    });
            we.load(url.toString());
        } catch( MalformedURLException e ){
            e.printStackTrace();
        }
    }

    private void random_sessid_al(){
        login_thread("A", 0.1);
        login_thread("B", 0.2);
        login_thread("C", 0.3);
    }
    private synchronized void login_action_init(){
        System.out.println(login_th_counter);
        if( login_th_counter == 3 ){
            web_view_init("A", "dk_oasa", "oas145");
            actionbtn.setOnMousePressed(ev -> {

                if( step_index == 0 ){
                    info_lbl.setText("B Bölge");
                    web_view_init("B", "dk_oasb", "oas125");
                } else if( step_index == 1 ){
                    info_lbl.setText("C Bölge");
                    web_view_init("C", "dk_oasc", "oas165");
                } else if( step_index == 2){
                    System.out.println("Tamamlandı");
                    listener.on_refresh();
                }
                step_index++;

            });
        }
    }
    private void login_thread( final String bolge, double delay ){
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {

                /*try {
                    Thread.sleep( delay * 1000 );
                } catch( InterruptedException e ){
                    e.printStackTrace();
                }*/

                org.jsoup.Connection.Response res;
                try{
                    System.out.println( "giriş yapılıyor..");
                    //res = Jsoup.connect("http://filo5.iett.gov.tr/login.php?sayfa=/_FYS.php&aday=x")
                    res = Jsoup.connect("http://filo5.iett.gov.tr/login.php?sayfa=/_FYS.php&aday=x")
                            //.cookie("PHPSESSID", cookie )
                            .method(org.jsoup.Connection.Method.POST)
                            .timeout(0)
                            .execute();

                    User_Config.filo5_cookies.put( bolge, res.cookies().get("PHPSESSID"));
                    System.out.println( bolge+" ---> " + res.cookies().get("PHPSESSID"));
                    login_th_counter++;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            login_action_init();
                        }
                    });
                    //kaydet();
                } catch( IOException e ){
                    System.out.println( " Bölgesi filo giriş hatası tekrar deneniyor.");
                    //login_thread( bolge, kullanici, pass );
                }
            }
        });
        th.setDaemon(true);
        th.start();

    }

}
