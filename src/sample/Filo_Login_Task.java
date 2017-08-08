package sample;

import org.jsoup.Jsoup;

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
    public Filo_Login_Task( ){
        // cookies.json temizle
        try{
            PrintWriter writer = new PrintWriter(User_Config.COOKIES_JSON, "UTF-8");
            writer.println("{}");
            writer.close();
        } catch (IOException e) {
            System.out.println("Kaydet hatası!");
        }
    }

    public void yap(){
        login_thread( "A", "dk_oasa", "oas145");
        login_thread( "B", "dk_oasb", "oas125");
        login_thread( "C", "dk_oasc", "oas165");
    }

    private void kaydet(){
        if( sayac == 3 ){
            try{
                PrintWriter writer = new PrintWriter(User_Config.COOKIES_JSON, "UTF-8");
                writer.println("{ \"A\":\""+cookies.get("A")+"\", \"B\":\""+cookies.get("B")+"\", \"C\":\""+cookies.get("C")+"\"  }");
                writer.close();
            } catch (IOException e) {
                System.out.println("Kaydet hatası!");
            }
        }
    }

    private void login_thread( String bolge, String kullanici, String pass ){

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
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
                    kaydet();
                } catch( IOException e ){
                    System.out.println( bolge + " Bölgesi filo giriş hatası tekrar deneniyor.");
                    login_thread( bolge, kullanici, pass );
                }
            }
        });
        thread.setDaemon(true);
        thread.start();


    }


}
