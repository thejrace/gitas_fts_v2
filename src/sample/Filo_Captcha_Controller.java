package sample;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.jsoup.Jsoup;

import javax.jws.soap.SOAPBinding;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;


public class Filo_Captcha_Controller implements Initializable {

    //@FXML
    private WebView wv_1, wv_2, wv_3;
    @FXML
    private TextField tf1_sessid, tf2_sessid, tf3_sessid;
    @FXML
    private Button actionbtn, aok_btn, bok_btn, cok_btn;
    @FXML
    private Label info_lbl;
    @FXML
    private HBox wv_container, wv2_container, wv3_container;

    private int step_index = 0;

    private Refresh_Listener listener;
    private URL url;
    private int login_th_counter = 0;
    private String cap1, cap2, cap3;


    private int wv_init_count = 0;
    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

        try {
            url = new URL("http://filo5.iett.gov.tr/login.php?sayfa=/_FYS.php&aday=x");
            //url = new URL("http://filo5.iett.gov.tr/login.php");
        } catch( MalformedURLException e ){
            e.printStackTrace();
        }
        User_Config.filo5_cookies = new HashMap<String, String>();

        //random_sessid_al();

        aok_btn.setOnMousePressed(ev -> { aok_btn.setDisable(true); login_th_counter++; User_Config.filo5_cookies.put("A", tf1_sessid.getText()); });
        bok_btn.setOnMousePressed(ev -> { bok_btn.setDisable(true); login_th_counter++; User_Config.filo5_cookies.put("B", tf2_sessid.getText()); });
        cok_btn.setOnMousePressed(ev -> { cok_btn.setDisable(true); login_th_counter++; User_Config.filo5_cookies.put("C", tf3_sessid.getText()); });

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {

                /**  WV1 **/

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        wv_1 = new WebView();
                        web_view( wv_1 );
                        wv_container.getChildren().add(0, wv_1);
                        //execute_login_script(wv_2, "dk_oasb", "oas125");
                        ///execute_login_script(wv_3, "dk_oasc", "oas165");
                    }
                });

                while( wv_init_count < 1 ){
                    System.out.println("wv_1 init bekleniyor..");
                    try {
                        Thread.sleep(2000);
                    } catch( InterruptedException e ){
                        e.printStackTrace();
                    }
                }

                //dk_oasfilo ** 1040-filo

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        execute_login_script(wv_1, "dk_oasa", "oas145");
                    }
                });

                while( login_th_counter < 1 ){
                    System.out.println("wv_1 cookie bekleniyor..");
                    try {
                        Thread.sleep(2000);
                    } catch( InterruptedException e ){
                        e.printStackTrace();
                    }
                }

                /**  WV2 **/



                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        wv_2 = new WebView();
                        web_view( wv_2 );
                        wv2_container.getChildren().add(0, wv_2);
                    }
                });

                System.out.println(wv_init_count);

                while( wv_init_count < 2 ){
                    System.out.println("wv_2 init bekleniyor..");
                    try {
                        Thread.sleep(2000);
                    } catch( InterruptedException e ){
                        e.printStackTrace();
                    }
                }

                System.out.println(wv_init_count);

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        execute_login_script(wv_2, "dk_oasb", "oas125");
                    }
                });

                System.out.println(wv_init_count);

                while( login_th_counter < 2 ){
                    System.out.println("wv_2 cookie bekleniyor..");
                    try {
                        Thread.sleep(2000);
                    } catch( InterruptedException e ){
                        e.printStackTrace();
                    }
                }


                /**  WV3 **/

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        wv_3 = new WebView();
                        web_view( wv_3 );
                        wv3_container.getChildren().add(0, wv_3);
                    }
                });

                System.out.println(wv_init_count);

                while( wv_init_count < 3 ){
                    System.out.println("wv_3 init bekleniyor..");
                    try {
                        Thread.sleep(2000);
                    } catch( InterruptedException e ){
                        e.printStackTrace();
                    }
                }

                System.out.println(wv_init_count);

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        execute_login_script(wv_3, "dk_oasc", "oas165");
                    }
                });

                System.out.println(wv_init_count);

                while( login_th_counter < 3 ){
                    System.out.println("wv_3 cookie bekleniyor..");
                    try {
                        Thread.sleep(2000);
                    } catch( InterruptedException e ){
                        e.printStackTrace();
                    }
                }

                System.out.println("OK");
            }
        });
        th.setDaemon(true);
        th.start();

        actionbtn.setOnMousePressed( ev -> {


            User_Config.filo5_cookies.put("A", tf1_sessid.getText());
            User_Config.filo5_cookies.put("B", tf2_sessid.getText());
            User_Config.filo5_cookies.put("C", tf3_sessid.getText());

            listener.on_refresh();


        });

        /*web_view( wv_1 );
        web_view( wv_2 );
        web_view( wv_3 );*/


    }

    private void execute_login_script( WebView wv, String login, String password ){
        wv.getEngine().executeScript(" " +
                " function hide( elem ){ elem.style.display = \"none\"; }  "+
                " document.body.style.backgroundColor = \"#302e2e\"; document.body.style.overflowY = \"hidden\";" +
                " document.body.style.color = \"#272727\"; document.body.style.fontSize = \"0px\";" +
                " var link = document.getElementsByTagName(\"a\"); link[0].style.color= \"#d1d1d1\"; link[0].style.marginLeft = \"-50px\"; link[0].style.fontFamily = \"Tahoma\"; link[0].innerHTML = \"Kodu Değiştir\";" +
                " link[0].style.textDecoration = \"none\"; link[0].style.fontSize = \"11\";  link[0].style.fontWeight = \"bold\";  "+
                " var trs = document.getElementsByTagName(\"tr\"); hide(trs[0]); hide(trs[2]); hide(trs[3]); " +
                " var cin = document.querySelectorAll('[name=\"captcha\"]'); cin[0].value = document.cookie; cin[0].style.width = \"45px\"; cin[0].style.position = \"relative\"; cin[0].style.top = \"-68px\"; cin[0].style.left = \"220px\";"+
                " var form = document.getElementById(\"aday\"); form.style.marginTop = \"-30px\"; form.style.marginLeft = \"-50px\"; " +
                " var submitbtn = document.querySelectorAll('[value=\"Giriş\"]');  submitbtn[0].style.marginTop = \"-68px\"; submitbtn[0].style.marginLeft = \"220px\";" +
                " submitbtn[0].style.backgroundColor = \"#7b3275\"; submitbtn[0].style.color = \"#d1d1d1\"; submitbtn[0].style.fontWeight = \"bold\";  submitbtn[0].style.border = \"none\"; " +
                " submitbtn[0].style.padding = \"6px 10px 6px 10px\"; submitbtn[0].style.borderRadius = \"3px\"; submitbtn[0].style.fontSize = \"11px\"; submitbtn[0].style.cursor = \"pointer\"; "+
                " var form_login = document.querySelectorAll('[name=\"login\"]');" +
                " var form_pass = document.querySelectorAll('[name=\"password\"]');" +
                " if( form_login[0] != undefined ) hide(form_login[0]); form_login[0].value=\""+login+"\"; if( form_pass[0] != undefined ) hide(form_pass[0]); form_pass[0].value=\""+password+"\";  " +
                " var divo = document.createElement(\"div\"); divo.id = \"hederoy\"; document.body.appendChild(divo); document.getElementById(\"hederoy\").innerHTML = document.cookie;");
        wv.setVisible(true);
    }

    private void web_view( final WebView wv ){
        wv.setPrefWidth(350);
        URI uri = null;
        try {
            uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
        } catch( URISyntaxException | NullPointerException e ){
            e.printStackTrace();
        }

        java.net.CookieManager manager = new java.net.CookieManager();
        java.net.CookieHandler.setDefault(manager);
        manager.getCookieStore().removeAll();

        WebEngine we = wv.getEngine();
        wv.setVisible(false);
        //try {
            //URL url = new URL("http://filo5.iett.gov.tr/login.php?sayfa=/_FYS.php&aday=x");
            //URL url = new URL("http://filo5.iett.gov.tr/login.php");
            we.setJavaScriptEnabled(true);
            we.getLoadWorker().stateProperty().addListener(
                    (ObservableValue<? extends Worker.State> observable,
                     Worker.State oldValue,
                     Worker.State newValue) -> {
                        if (newValue != Worker.State.SUCCEEDED) {
                            return;
                        }
                        wv_init_count++;
                    });
            we.load(url.toString());
       /* } catch( MalformedURLException e ){
            e.printStackTrace();
        }*/
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
        login_thread("A", false, "dk_oasa", "", "");
        login_thread("B", false, "dk_oasb", "", "");
        login_thread("C", false, "dk_oasc", "", "");

    }
    private synchronized  void inc_login_counter(){
        login_th_counter++;
    }
    private void login_action_init(){
        //if( login_th_counter == 3 ){
            web_view_init("A", "dk_oasa", "oas145");
            actionbtn.setOnMousePressed(ev -> {

                if( tf1_sessid.getText().equals("")) return;

                if( step_index == 0 ){
                    info_lbl.setText("B Bölge");
                    cap1 = tf1_sessid.getText();
                    //login_thread("A", true, "dk_oasa", "oas145", tf1_sessid.getText() );
                    tf1_sessid.setText("");
                    web_view_init("B", "dk_oasb", "oas125");
                } else if( step_index == 1 ){
                    info_lbl.setText("C Bölge");
                    cap2 = tf1_sessid.getText();
                    //login_thread("B", true, "dk_oasb", "oas125", tf1_sessid.getText() );
                    tf1_sessid.setText("");
                    web_view_init("C", "dk_oasc", "oas165");
                } else if( step_index == 2){
                    cap3 = tf1_sessid.getText();
                    System.out.println("Tamamlandı");
                    login_thread("C", true, "dk_oasc", "oas165", cap3 );
                    login_thread("B", true, "dk_oasb", "oas125", cap3 );
                    login_thread("A", true, "dk_oasa", "oas145", cap3 );
                    tf1_sessid.setText("");
                    listener.on_refresh();
                }
                step_index++;

            });
        //}
    }
    private void login_thread( final String bolge, boolean ikinci_login, final String login, final String password, final String captcha ){
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                org.jsoup.Connection.Response res;
                try{
                    String ua = "";
                    if( bolge.equals("A") ){
                        ua = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";
                    } else if( bolge.equals("B") ){
                        ua = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1";
                    } else {
                        ua = "Mozilla/5.0 (Windows; U; Windows NT 6.1; rv:2.2) Gecko/20110201";
                    }


                    if( ikinci_login ){


                        System.out.println( bolge + " filo giriş yapılıyor.. -l["+login+"] -p["+password+"] -c["+captcha+"] -ssid["+User_Config.filo5_cookies.get(bolge)+"]");
                        //res = Jsoup.connect("http://filo5.iett.gov.tr/login.php")
                        res = Jsoup.connect("http://filo5.iett.gov.tr/login.php?sayfa=/_FYS.php")
                                .cookie("PHPSESSID", User_Config.filo5_cookies.get(bolge) )
                                .userAgent(ua)
                                .data("login", login, "password", password, "captcha", captcha )
                                .method(org.jsoup.Connection.Method.POST)
                                .timeout(0)
                                .execute();

                        System.out.println( bolge + " giriş yapıldı..");
                        //System.out.println( res.parse());

                    } else {
                        System.out.println( bolge + " phpsessid alınıyor..");
                        // random phpssid

                        res = Jsoup.connect("http://filo5.iett.gov.tr/login.php?sayfa=/_FYS.php&aday=x")
                                .method(org.jsoup.Connection.Method.POST)
                                .userAgent(ua)
                                .timeout(0)
                                .execute();

                        User_Config.filo5_cookies.put( bolge, res.cookies().get("PHPSESSID"));
                        inc_login_counter();
                        System.out.println( bolge + " phpsessid alındı. -c["+res.cookies().get("PHPSESSID")+"]");
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                login_action_init();
                            }
                        });
                    }
                } catch( IOException e ){
                    System.out.println( " Bölgesi filo giriş hatası tekrar deneniyor.");
                }
            }
        });
        th.setDaemon(true);
        th.start();

    }

}
