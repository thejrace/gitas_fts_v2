package sample;

import java.util.ArrayList;

/**
 * Created by Obarey on 17.02.2017.
 */
public class Oneri_Sefer_Data {

    private String guzergah, orer, dkodu, durum, kapi_kodu;
    private ArrayList<Otobus_Data> onerilen_otolar = new ArrayList<>();

    public Oneri_Sefer_Data( String guzergah, String orer, String durum, String dkodu, String kapi_kodu ){
        this.guzergah = guzergah;
        this.orer = orer;
        this.dkodu = dkodu;
        this.durum = durum;
        this.kapi_kodu = kapi_kodu;
    }

    public void oto_ekle( ArrayList<Otobus_Data> otolar ){
        this.onerilen_otolar = otolar;
    }

    public void test_print(){
        StringBuilder sb = new StringBuilder();
        for( Otobus_Data oto: onerilen_otolar ) {
            sb.append( oto.get_oto() );
            sb.append( " GUZ: ");
            sb.append( oto.get_guz() );
            sb.append( " SON ORER: ");
            sb.append( oto.get_son_orer() );
            sb.append( ", ");
        }


        System.out.println( "[ GÜZERGAH " + this.guzergah + " ] [ ORER "+this.orer+" ] [ DURUM "+this.durum+" ] \n"
            + "    [ OTOLAR " + sb.toString() + " ] ");

    }

    public String get_oto(){
        return this.kapi_kodu;
    }

    public String get_guzergah(){
        return this.guzergah;
    }

    public String get_orer(){
        return this.orer;
    }

    public String get_dkodu(){
        return this.dkodu;
    }

    public String get_durum(){
        return this.durum;
    }

    public ArrayList<Otobus_Data> get_otolar(){
        return this.onerilen_otolar;
    }


}
