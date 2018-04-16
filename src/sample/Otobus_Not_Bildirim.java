package sample;

/**
 * Created by Jeppe on 21.04.2017.
 */
public class Otobus_Not_Bildirim {

    private String not_gid, gid, icerik, yazan, tarih;

    public Otobus_Not_Bildirim( String gid ){
        this.gid = gid;
    }
    public Otobus_Not_Bildirim(){

    }

    public Otobus_Not_Bildirim( String gid, String not_gid, String icerik, String yazan, String tarih ){
        this.not_gid = not_gid;
        this.gid = gid;
        this.icerik = icerik;
        this.yazan = yazan;
        this.tarih = tarih;
    }

    public void ekle( String not_gid, String gid, String icerik, String yazan, String tarih  ){

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
    public String get_tarih(){
        return tarih;
    }
    public String get_not_gid(){
        return not_gid;
    }



}
