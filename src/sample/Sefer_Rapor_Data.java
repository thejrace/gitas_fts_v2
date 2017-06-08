package sample;

/**
 * Created by Jeppe on 13.03.2017.
 */
public class Sefer_Rapor_Data {

    private int toplam, tamam, bekleyen, aktif, ek, iptal, yarim;
    private double iett_km, gitas_km, sefer_yuzdesi;
    private String id;
    public Sefer_Rapor_Data( String id, int toplam, int tamam, int bekleyen, int aktif, int ek, int iptal, int yarim, double iett_km, double gitas_km ){
        this.id = id;
        this.toplam = toplam;
        this.tamam = tamam;
        this.bekleyen = bekleyen;
        this.aktif = aktif;
        this.ek = ek;
        this.iptal = iptal;
        this.yarim = yarim;
        this.iett_km = iett_km;
        this.gitas_km = gitas_km;

        sefer_yuzdesi_hesapla();
    }

    public Sefer_Rapor_Data( int tamam, int aktif, int bekleyen, int iptal, int yarim ){
        this.tamam = tamam;
        this.bekleyen = bekleyen;
        this.aktif = aktif;
        this.iptal = iptal;
        this.yarim = yarim;
    }

    public void sefer_yuzdesi_hesapla(){

        double payda = toplam + ek - bekleyen - aktif;
        if( payda <= 0 ) payda = 1;
        //System.out.println( "%" + String.format("%.1f", Math.ceil( tamam  * 100 / payda  ) ) );
        //sefer_yuzdesi =  "%" + String.format("%.1f", Math.ceil( Integer.valueOf( tamam ) * 100 / payda  ) );
        sefer_yuzdesi =  Math.ceil( tamam  * 100 / payda );
    }

    public double get_sefer_yuzdesi(){
        return this.sefer_yuzdesi;
    }

    public String get_id(){
        return this.id;
    }

    public int get_toplam(){
        return this.toplam;
    }

    public int get_tamam(){
        return this.tamam;
    }

    public int get_bekleyen(){
        return this.bekleyen;
    }

    public int get_aktif(){
        return this.aktif;
    }

    public int get_ek(){
        return this.ek;
    }

    public int get_iptal(){
        return this.iptal;
    }

    public int get_yarim(){
        return this.yarim;
    }

    public double get_iett_km(){
        return this.iett_km;
    }

    public double get_gitas_km(){
        return this.gitas_km;
    }

    public void toplam_arttir(){
        this.toplam++;
    }

    public void tamam_arttir(){
        this.tamam++;
    }

    public void bekleyen_arttir(){
        this.bekleyen++;
    }

    public void ek_arttir(){
        this.ek++;
    }

    public void aktif_arttir(){
        this.aktif++;
    }

    public void yarim_arttir(){
        this.yarim++;
    }

    public void iptal_arttir(){
        this.iptal++;
    }

    public void arttir( String tip ){
        if( tip.equals(Sefer_Data.DTAMAM ) ){
            tamam_arttir();
        } else if( tip.equals(Sefer_Data.DAKTIF) ){
            aktif_arttir();
        } else if( tip.equals(Sefer_Data.DBEKLEYEN) ){
            bekleyen_arttir();
        } else if( tip.equals(Sefer_Data.DIPTAL) ){
            iptal_arttir();
        } else if( tip.equals(Sefer_Data.DYARIM)){
            yarim_arttir();
        }
        toplam_arttir();

    }


}
