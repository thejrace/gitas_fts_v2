package sample;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Obarey on 13.02.2017.
 */
public class Filo_Mesaj_Data {

    private String mesaj, kaynak, tarih, oto, plaka, surucu, saat;
    private int hareket_dokumu;

    public Filo_Mesaj_Data( String kaynak, String tarih, String mesaj  ){
        this.mesaj = mesaj;
        this.kaynak = kaynak;
        this.tarih = tarih;
    }

    public Filo_Mesaj_Data( String oto, String plaka, String surucu, String kaynak, String mesaj, String saat, String tarih, int hareket_dokumu ){
        this.oto = oto;
        this.plaka = plaka;
        this.mesaj = mesaj;
        this.kaynak = kaynak;
        this.tarih = tarih;
        this.hareket_dokumu = hareket_dokumu;
        this.surucu = surucu;
        this.saat = saat;
    }


    public String get_kaynak(){
        return kaynak;
    }
    public String get_mesaj(){
        return mesaj;
    }
    public String get_tarih(){
        return tarih;
    }
    public String get_surucu(){ return surucu; }
    public String get_oto(){ return oto; }
    public String get_plaka(){ return plaka; }
    public int get_hareket_dokumu(){ return hareket_dokumu; }

}
