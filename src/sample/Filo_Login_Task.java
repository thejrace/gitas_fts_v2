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

    private int sayac = 0;
    private Map<String, String> cookies = new HashMap<>();
    private boolean run = true;
    public Filo_Login_Task( ){
        // cookies.json temizle
       /* try{
            PrintWriter writer = new PrintWriter(User_Config.COOKIES_JSON, "UTF-8");
            writer.println("{}");
            writer.close();
        } catch (IOException e) {
            System.out.println("Kaydet hatası!");
        }*/
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
                        // 3 saatten yeniyse cache ten al
                        try {
                            if( Common.get_unix() - cache.getDouble("timestamp") <= 10800 ){
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
                        login_thread( "A", "dk_oasa", "oas145");
                        login_thread( "B", "dk_oasb", "oas125");
                        login_thread( "C", "dk_oasc", "oas165");
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
                    .execute();

            cookies.put(bolge, res.cookies().get("PHPSESSID") );
            sayac++;
            System.out.println( bolge + " Bölgesi filoya giriş yapıldı!");
            //kaydet();
        } catch( IOException e ){
            System.out.println( bolge + " Bölgesi filo giriş hatası tekrar deneniyor.");
            cookies.put(bolge, "INIT" );
            //login_thread( bolge, kullanici, pass );
        }
    }


}
