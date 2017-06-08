package sample;

/**
 * Created by Jeppe on 28.04.2017.
 */
public class Ayarlar_Genel_Data {

    private String alarmlar;
    private int orer_frekans, iys_frekans, mesaj_frekans;

    public Ayarlar_Genel_Data( String alarmlar, int orer_frekans, int iys_frekans, int mesaj_frekans ){
        this.alarmlar = alarmlar;
        this.orer_frekans = orer_frekans;
        this.iys_frekans = iys_frekans;
        this.mesaj_frekans = mesaj_frekans;
    }

    public int get_orer_frekans(){
        return orer_frekans;
    }
    public int get_iys_frekans(){
        return iys_frekans;
    }
    public int get_mesaj_frekans(){
        return mesaj_frekans;
    }
    public boolean alarm_cb_kontrol( int index ){
        try {
            return alarmlar.charAt(index) == '1';
        } catch( IndexOutOfBoundsException e ){
            e.printStackTrace();
        }
        return false;
    }

}
