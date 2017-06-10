package sample;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Jeppe on 08.04.2017.
 */
public class Otobus_Not {

    private String gid, icerik, yazan, tamamlayan, baslangic, bitis, alarm_bitis, kapi_kodu, plaka;
    private int durum, goruldu, tip, alarm_flag;
    public static int   KAZA = 1,
                        SERVIS = 2,
                        UYARI = 3;

    public Otobus_Not(){

    }

    public Otobus_Not( String gid ){
        this.gid = gid;
    }

    public static String tip_to_str( int tip ){
        if( tip == KAZA ){
            return "Kaza";
        } else if( tip == SERVIS ){
            return "Servis";
        } else {
            return "Uyarı";
        }
    }

    public String get_gid(){
        return gid;
    }
    public String get_icerik(){
        return icerik;
    }
    public String get_yazan(){
        return yazan;
    }
    public String get_tamamlayan(){
        return tamamlayan;
    }
    public String get_baslangic(){
        return baslangic;
    }
    public String get_bitis(){
        return bitis;
    }
    public String get_alarm_bitis(){
        return alarm_bitis;
    }
    public int get_alarm_flag(){
        return alarm_flag;
    }
    public int get_durum(){
        return durum;
    }
    public int get_goruldu(){
        return goruldu;
    }
    public int get_tip(){
        return tip;
    }
    public String get_kapi_kodu(){
        return kapi_kodu;
    }
    public String get_plaka(){
        return plaka;
    }

    public ArrayList<Otobus_Not_Bildirim> get_bildirimler(){
        ArrayList<Otobus_Not_Bildirim> output = new ArrayList<>();

        return output;
    }



    // tamamlanmis not
    public void ekle( String gid, String kapi_kodu, String plaka, String yazan, String icerik, int tip, String tamamlayan, int alarm, String alarm_bitis, String baslangic, String bitis, int durum ){

    }



    // kullanici kendi tamamladiginda notu ( parametreler server dan geliyor )
    public void tamamla( String bitis, String tamamlayan ){

    }


    // aktif not ekleme
    public void ekle( String gid, String kapi_kodu, String plaka, String yazan, String icerik, int tip, int alarm, String alarm_bitis, String baslangic, int durum, int goruldu ){

    }

}
