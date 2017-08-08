package sample;

/**
 * Created by Jeppe on 09.07.2017.
 */
public class Surucu {

    private String sicil_no, isim, telefon;
    public Surucu( String _sicil_no, String _isim, String _telefon ){
        sicil_no = _sicil_no;
        isim = _isim;
        telefon = _telefon;
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
