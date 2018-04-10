package sample;

/**
 * Created by Jeppe on 09.07.2017.
 */
public class Surucu {

    private String sicil_no, isim, telefon, orer, bitis;
    public Surucu( String _sicil_no, String _isim, String _telefon ){
        sicil_no = _sicil_no;
        isim = _isim;
        telefon = _telefon;
    }

    public Surucu( String _isim, String _orer, String _bitis, String _durum ){
        isim = _isim;
        orer = _orer;
        bitis = _bitis;
    }

    public String get_orer(){
        return orer;
    }
    public String get_bitis(){
        return bitis;
    }
    public void set_orer( String _orer ){
        orer = _orer;
    }
    public void set_bitis( String _bitis ){
        bitis = _bitis;
    }

    public String get_sicil_no(){
        return sicil_no;
    }
    public String get_isim(){
        return isim;
    }
    public String get_telefon(){
        return telefon;
    }

}
