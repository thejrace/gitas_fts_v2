package sample;

import org.jsoup.nodes.Document;

public class Hiz_Download extends Filo_Task_Template {
    private int hiz = 0;
    public Hiz_Download( String _oto, String _cookie ){
        cookie = _cookie;
        oto = _oto;
    }
    public void yap(){
        error = false;
        // veri yokken nullpointer yemeyek diye resetliyoruz başta
        System.out.println("Hız download [ " + oto + " ]");
        org.jsoup.Connection.Response sefer_verileri_req = istek_yap("http://filo5.iett.gov.tr/_FYS/000/harita.php?konu=oto&oto=");
        Document sefer_doc = parse_html( sefer_verileri_req );
        sefer_veri_ayikla( sefer_doc );
    }
    public void sefer_veri_ayikla( Document document ){
        if( error ){
            hiz = 0;
            return;
        }
        try {
            String sayfa = document.toString();
            String data_string = sayfa.substring( sayfa.indexOf("veri_ilklendir")+14, sayfa.indexOf("veri_hatcizgi") );
            String[] exploded = data_string.split("\\|");
            //System.out.println("SAAT:" + exploded[1]);
            //System.out.println( oto + " HIZ:" + exploded[4]);
            hiz = Integer.valueOf(exploded[4]);
        } catch( NullPointerException e ){
            e.printStackTrace();
            System.out.println( "["+Common.get_current_hmin() + "]  "+  oto+ " Hız sefer veri ayıklama hatası. Tekrar deneniyor.");
            hiz = 0;
            error = true;
            //yap();
        }
    }
    public int get_hiz(){
        return hiz;
    }

}
