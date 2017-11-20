package sample;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jeppe on 07.07.2017.
 */
public class Filo_Login_Task {

    private Map<String, String> cookies = new HashMap<>();
    private boolean run = true;
    private JSONObject filo5_data;
    public Filo_Login_Task( JSONObject _filo5_data ){
        // cookies.json temizle
       /* try{
            PrintWriter writer = new PrintWriter(User_Config.COOKIES_JSON, "UTF-8");
            writer.println("{}");
            writer.close();
        } catch (IOException e) {
            System.out.println("Kaydet hatası!");
        }*/
        filo5_data = _filo5_data;
    }

    public void yap( Cookie_Refresh_Listener listener ){
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean filo_login;
                while( run ){
                    filo_login = false;
                    String cache_str = Common.json_file_read( User_Config.COOKIES_JSON );
                    JSONObject cache = new JSONObject(cache_str);
                    try {
                        if( cache.getBoolean("init") ){
                            filo_login = true;
                        }
                    } catch( JSONException e ){
                        // 10 dk dan yeniyse cache ten al
                        try {
                            if( Common.get_unix() - cache.getDouble("timestamp") <= 600 ){
                                cookies.put("A", cache.getString("A") );
                                cookies.put("B", cache.getString("B") );
                                cookies.put("C", cache.getString("C") );
                                listener.on_refresh( cookies );
                            } else {
                                filo_login = true;
                            }
                        } catch( Exception ex ){
                            ex.printStackTrace();
                        }
                        //e.printStackTrace();
                    }
                    if( filo_login ){
                        JSONObject bolge_data = filo5_data.getJSONObject("A");
                        login_thread( "A", bolge_data.getString("login"), bolge_data.getString("pass"));
                        bolge_data = filo5_data.getJSONObject("B");
                        login_thread( "B", bolge_data.getString("login"), bolge_data.getString("pass"));
                        bolge_data = filo5_data.getJSONObject("C");
                        login_thread( "C", bolge_data.getString("login"), bolge_data.getString("pass"));
                        listener.on_refresh( cookies );
                        try{
                            PrintWriter writer = new PrintWriter(User_Config.COOKIES_JSON, "UTF-8");
                            writer.print("{ \"timestamp\":"+Common.get_unix()+",  \"A\":\""+cookies.get("A")+"\", \"B\":\""+cookies.get("B")+"\", \"C\":\""+cookies.get("C")+"\"  }");
                            writer.close();
                        } catch (IOException e) {
                            System.out.println("Kaydet hatası!");
                        }
                    }

                    try {
                        Thread.sleep(21600000 ); // 6 saat
                    } catch( InterruptedException e ){
                        e.printStackTrace();
                    }
                }
            }
        });
        th.setDaemon(true);
        th.start();
    }

    /*private void kaydet(){
        if( sayac == 3 ){
            try{
                PrintWriter writer = new PrintWriter(User_Config.COOKIES_JSON, "UTF-8");
                writer.println("{ \"A\":\""+cookies.get("A")+"\", \"B\":\""+cookies.get("B")+"\", \"C\":\""+cookies.get("C")+"\"  }");
                writer.close();
            } catch (IOException e) {
                System.out.println("Kaydet hatası!");
            }
        }
    }*/

    private void login_thread( String bolge, String kullanici, String pass ){
        org.jsoup.Connection.Response res;
        try{
            System.out.println( bolge + " Bölgesi filoya giriş yapılıyor..");
            res = Jsoup.connect("http://filo5.iett.gov.tr/login.php?sayfa=/_FYS.php&aday=x")
                    .data("login", kullanici, "password", pass )
                    .method(org.jsoup.Connection.Method.POST)
                    .timeout(0)
                    .execute();

            cookies.put(bolge, res.cookies().get("PHPSESSID") );
            System.out.println( bolge + " Bölgesi filoya giriş yapıldı! phpssid["+res.cookies().get("PHPSESSID")+"] -ts["+Common.get_unix()+"] ");
            //kaydet();
        } catch( IOException e ){
            System.out.println( bolge + " Bölgesi filo giriş hatası tekrar deneniyor.");
            cookies.put(bolge, "INIT" );
            login_thread( bolge, kullanici, pass );
        }
    }


}
