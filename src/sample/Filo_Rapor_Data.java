package sample;

/**
 * Created by Jeppe on 05.06.2017.
 */
public class Filo_Rapor_Data {

    private int zayi_sefer = 0,
                iys = 0,
                not = 0,
                plaka = 0;

    public Filo_Rapor_Data(){

    }

    public Filo_Rapor_Data( int _zayi_sefer, int _iys, int _not, int _plaka ){
        zayi_sefer = _zayi_sefer;
        iys = _iys;
        not = _not;
        plaka = _plaka;
    }

    public void arttir( String tip ){
        if( tip.equals(Otobus_Box_Filtre.FD_ZAYI) ){
            zayi_sefer++;
        } else if( tip.equals(Otobus_Box_Filtre.FD_PLAKA)){
            plaka++;
        } else if( tip.equals(Otobus_Box_Filtre.FD_NOT)){
            not++;
        } else if( tip.equals(Otobus_Box_Filtre.FD_IYS)){
            iys++;
        }
    }

    public void ekle( String tip, int eklenecek ){
        if( tip.equals(Otobus_Box_Filtre.FD_ZAYI) ){
            zayi_sefer+= eklenecek;
        } else if( tip.equals(Otobus_Box_Filtre.FD_PLAKA)){
            plaka+= eklenecek;
        } else if( tip.equals(Otobus_Box_Filtre.FD_NOT)){
            not+= eklenecek;
        } else if( tip.equals(Otobus_Box_Filtre.FD_IYS)){
            iys+= eklenecek;
        }
    }

    public int get_zayi(){
        return zayi_sefer;
    }
    public int get_iys(){
        return iys;
    }
    public int get_not(){
        return not;
    }
    public int get_plaka(){
        return plaka;
    }

}
