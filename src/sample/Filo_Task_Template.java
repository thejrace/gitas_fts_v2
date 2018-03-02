package sample;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class Filo_Task_Template {
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

