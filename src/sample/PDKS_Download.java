package sample;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PDKS_Download extends Filo_Task_Template {
    private Map<String, String> data = new HashMap<>();
    public PDKS_Download( String oto, String cookie ){
        this.oto = oto;
        this.cookie = cookie;
        this.logprefix = "Sürücü PDKS";
    }
    public Map<String, String> get_suruculer(){
        return data;
    }
    public void yap(){
        error = false;
        System.out.println( "["+Common.get_current_hmin() + "]  "+ aktif_tarih  + " " +  "[ " + oto + " PDKS DOWNLOAD ]");
        org.jsoup.Connection.Response pdks_req = istek_yap("http://filo5.iett.gov.tr/_FYS/000/sorgu.php?konu=mesaj&mtip=PDKS&oto=");
        Document pdks_doc = parse_html( pdks_req );
        pdks_ayikla( pdks_doc );
    }
    private void pdks_ayikla( Document document ){
        if( error ){
            data = new HashMap<>();
            return;
        }
        Elements table = null;
        Elements rows = null;
        Element row = null;
        Elements cols = null;
        String kart_basma_col_text, surucu, sicil_no;
        try {
            table = document.select("table");
            rows = table.select("tr");
            for (int i = 2; i < rows.size(); i++) {
                row = rows.get(i);
                cols = row.select("td");
                kart_basma_col_text = cols.get(4).text();
                //System.out.println(kart_basma_col_text);

                try {
                    surucu = Common.regex_trim(kart_basma_col_text.substring(25));
                    sicil_no = Common.regex_trim(cols.get(4).text()).substring(16, 22);
                    if (kart_basma_col_text.contains("PDKS_Kart Binen ")) {
                        if( !data.containsKey(sicil_no)) data.put( sicil_no, surucu );

                        //put("sicil_no", Common.regex_trim(cols.get(4).text()).substring(16, 22));
                        //data.put("isim", Common.regex_trim(kart_basma_col_text.substring(25)) );
                    } else if ((kart_basma_col_text.contains("PDKS_Kart inen"))) {
                        // todo inen binen pdks tema yapilicak
                        /*data.put("sicil_no", Common.regex_trim(cols.get(4).text()).substring(16, 22));
                        data.put("isim", Common.regex_trim(kart_basma_col_text.substring(25)) );

                        sicil_no = Common.regex_trim(cols.get(4).text()).substring(15, 21);
                        isim = Common.regex_trim(kart_basma_col_text.substring(24));*/
                    }

                    //System.out.println(oto + " PDKS --> [" + tip + " " + sicil_no + "] [" + isim + "]");
                } catch( NullPointerException | IndexOutOfBoundsException e ){
                    e.printStackTrace();
                }
                cols.clear();
            }
            rows.clear();
        } catch( NullPointerException e ){
            System.out.println( "["+Common.get_current_hmin() + "]  "+ aktif_tarih  + " " +  oto + " ORER sürücü PDKS ayıklama hatası. Tekrar deneniyor.");
            e.printStackTrace();
            data = new HashMap<>();
        }
    }
    // noktaya istek, surucu isim ve telefon alma
    private void surucu_noktaya_istek(){
        org.jsoup.Connection.Response nokta_req = istek_yap("http://filo5.iett.gov.tr/_FYS/000/uyg.0.2.php?abc=1&talep=5&grup=0&hat=");
        Document nokta_doc;
        try {
            nokta_doc = nokta_parse_html( nokta_req );
            nokta_ayikla( nokta_doc );
        } catch( NullPointerException e ){
            // sürücü bilgisi yok noktada, bir keresinde veritabanı hatası falan vermişti onun için önlem
        }

    }
    private Document nokta_parse_html( org.jsoup.Connection.Response req ){
        Document doc;
        try {
            doc = req.parse();
            if( doc.select("body").text().contains("Database") ){
                System.out.println(  "["+Common.get_current_hmin() + "]  "+ aktif_tarih  + " " +  oto + " Sürücü detay, Veri yok");
                return null;
            } else {
                return doc;
            }
        } catch (IOException | NullPointerException e) {
            System.out.println( "["+Common.get_current_hmin() + "]  "+ aktif_tarih  + " " +  oto + "Surucu detay parse hatası. Tekrar deneniyor.");
            e.printStackTrace();
        }
        return null;
    }
    private void nokta_ayikla( Document document ){
        try {
            if( document == null ){}
        } catch( NullPointerException e ){
            e.printStackTrace();
            yap();
        }
        Elements table_sur = document.select("table");
        String surucu_string = table_sur.select("tr").get(1).getAllElements().get(2).text();
        surucu_string = surucu_string.substring(2);
        String[] surucu_split_data = surucu_string.split(" ");
        String surucu_ad = "";
        for (int j = 1; j < surucu_split_data.length - 1; j++) {
            if( j < surucu_split_data.length - 2 ){
                surucu_ad += surucu_split_data[j] + " ";
            } else {
                surucu_ad += surucu_split_data[j];
            }
        }
        if( !surucu_ad.equals("") && !surucu_ad.equals("-1")){
            //Surucu_Data surucu = new Surucu_Data();
            //surucu.ekle( Common.regex_trim(surucu_split_data[0]), surucu_ad, surucu_split_data[surucu_split_data.length - 1].substring(1, surucu_split_data[surucu_split_data.length - 1].length() - 1 ) );
            //System.out.println(oto + " SÜrücü detay alindi -> [" + surucu_split_data[0] + "] " + surucu_ad );
        }
    }
}
