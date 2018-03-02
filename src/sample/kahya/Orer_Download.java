package sample.kahya;

import org.json.JSONArray;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import sample.Common;
import sample.Filo_Task_Template;
import sample.Sefer_Data;

import java.util.ArrayList;

public class Orer_Download extends Filo_Task_Template {
    private String aktif_sefer_verisi = "";
    private JSONArray seferler = new JSONArray();
    private boolean kaydet = false;
    private boolean kahya_flag = false;
    private ArrayList<String> guzergahlar_data = new ArrayList<>();
    private String hat, aktif_orer = "BEKLEMEDE";
    private int aktif_sefer_index;
    public Orer_Download( String oto, String cookie ){
        this.oto = oto;
        this.cookie = cookie;
    }

    public void set_kahya_flag(){
        kahya_flag = true;
    }

    public void yap(){
        error = false;
        // veri yokken nullpointer yemeyek diye resetliyoruz başta
        System.out.println("ORER download [ " + oto + " ]");
        org.jsoup.Connection.Response sefer_verileri_req = istek_yap("http://filo5.iett.gov.tr/_FYS/000/sorgu.php?konum=ana&konu=sefer&otobus=");
        Document sefer_doc = parse_html( sefer_verileri_req );
        sefer_veri_ayikla( sefer_doc );

    }
    public void sefer_veri_ayikla( Document document ){
        if( error ){
            seferler = new JSONArray();
            return;
        }
        Elements table = null;
        Elements rows = null;
        Element row = null;
        Elements cols = null;

        try {
            table = document.select("table");
            rows = table.select("tr");

            if( rows.size() == 1 || rows.size() == 0 ){
                System.out.println(oto + " ORER Filo Veri Yok");
                return;
            }
            hat = "";
            String orer;
            Sefer_Data tek_sefer_data;
            boolean hat_alindi = false;
            aktif_sefer_verisi = "YOK";
            for( int i = 1; i < rows.size(); i++ ){
                row = rows.get(i);
                cols = row.select("td");

                orer = Common.regex_trim(cols.get(7).getAllElements().get(2).text());

                if( !hat_alindi ){
                    hat = cols.get(1).text().trim();
                    if( cols.get(1).text().trim().contains("!")  ) hat = cols.get(1).text().trim().substring(1, cols.get(1).text().trim().length() - 1 );
                    if( cols.get(1).text().trim().contains("#") ) hat = cols.get(1).text().trim().substring(1, cols.get(1).text().trim().length() - 1 );
                    if( cols.get(1).text().trim().contains("*") ) hat = cols.get(1).text().trim().substring(1, cols.get(1).text().trim().length() - 1);
                    hat_alindi = true;
                }

                if( cols.get(12).text().replaceAll("\u00A0", "").equals("A") && cols.get(3).getAllElements().size() > 2 ){
                    aktif_sefer_verisi = Common.regex_trim(cols.get(3).getAllElements().get(2).attr("title"));
                }

                if( kahya_flag ){

                    guzergahlar_data.add(Common.regex_trim(cols.get(3).getAllElements().get(1).text()));
                    if( Common.regex_trim(cols.get(12).text()).equals("A") ){
                        aktif_orer = orer;
                        aktif_sefer_index = i;
                    }

                } else{
                    tek_sefer_data = new Sefer_Data(
                            Common.regex_trim(cols.get(0).text()),
                            hat,
                            Common.regex_trim(cols.get(2).text()),
                            Common.regex_trim(cols.get(3).getAllElements().get(1).text()),
                            Common.regex_trim(cols.get(4).getAllElements().get(2).text()),
                            "",
                            "",
                            "",
                            Common.regex_trim(cols.get(6).text()),
                            orer,
                            "",
                            Common.regex_trim(cols.get(8).text()),
                            Common.regex_trim(cols.get(9).text()),
                            Common.regex_trim(cols.get(10).text()),
                            Common.regex_trim(cols.get(11).text()),
                            Common.regex_trim(cols.get(12).text()),
                            cols.get(13).text().substring(5),
                            "",
                            1,
                            0
                    );
                    seferler.put(tek_sefer_data.tojson());
                }
                cols.clear();
            }
            rows.clear();
        } catch( NullPointerException e ){
            e.printStackTrace();
            System.out.println( "["+Common.get_current_hmin() + "]  "+  oto+ " ORER sefer veri ayıklama hatası. Tekrar deneniyor.");
            seferler = new JSONArray();
            error = true;
            //yap();
        }
    }
    public JSONArray get_seferler(){
        return seferler;
    }
    public String get_aktif_sefer_verisi(){
        return aktif_sefer_verisi;
    }
    public ArrayList<String> get_guzergahlar(){
        return guzergahlar_data;
    }
    public String get_hat(){
        return hat;
    }
    public String get_aktif_orer(){
        return aktif_orer;
    }
    public int get_aktif_sefer_index(){
        return aktif_sefer_index;
    }
}
