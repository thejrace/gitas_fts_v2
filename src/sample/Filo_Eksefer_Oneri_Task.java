package sample;

import javafx.concurrent.Task;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Obarey on 17.02.2017.
 *
 *
 * YENİ VERSYİON BU KARIŞTIRMA
 *
 */
public class Filo_Eksefer_Oneri_Task extends Task<ArrayList<Oneri_Sefer_Data>> {

    private String bolge;
    private ArrayList<Oneri_Sefer_Data> output = new ArrayList<>();
    public boolean shutdown = false;
    private boolean error = false;
    public Filo_Eksefer_Oneri_Task( String bolge ){
        this.bolge = bolge;

    }

    private void veri_al(){
        if( shutdown ) return;

        error = false;

        org.jsoup.Connection.Response js_con = null;
        Document sefer_doc = null;

        Web_Request request = new Web_Request(Web_Request.SERVIS_URL, "&req=ek_sefer_oneri_init&bolge="+bolge );
        request.kullanici_pc_parametreleri_ekle();
        request.action();
        JSONObject data = new JSONObject(request.get_value()).getJSONObject("data");

        String cookie;
        JSONObject cookies = User_Config.cookie_config_oku();
        try {
            // init varsa daha giris yapilmamis demektir bekliyoruz
            if (cookies.getBoolean("init")) {
            }
            return;
        } catch (JSONException e) {
            e.printStackTrace();
            cookie = cookies.getString(bolge);
        }

        String AKTIF_TARIH = data.getString("aktif_tarih");
        JSONArray otobus_db_data = data.getJSONArray("otobus_db_data");
        JSONArray hatlar_data = data.getJSONArray("hatlar");

        if( !Common.get_current_date().equals(AKTIF_TARIH) ){
            System.out.println("Saat 12 yi geçti yat artık!");
            return;
        }

        Map<String, Otobus_Data> otobusler_data = new HashMap<>();
        JSONObject item;
        try {
            for( int j = 0; j < otobus_db_data.length(); j++ ){
                item = otobus_db_data.getJSONObject(j);
                // bizim otonun da son seferi iptal degilse tabi
                // todo burada durum kodlari da onem olabilir RK falansa ek sefere gidebilir
                if( !item.getString("durum").equals("I") && !item.getString("durum").equals("Y") ){
                    otobusler_data.put( item.getString("oto"), new Otobus_Data( item.getString("oto"), item.getString("guzergah"), item.getString("orer") ) );
                }
            }
        } catch( JSONException e ){
            e.printStackTrace();
            error = true;
            updateMessage(( bolge + " Bölgesi otobüsleri alınırken bir hata oluştu. Tekrar deneniyor."));
            veri_al();
        }

        // 1 - tum hatlarin iptal ve yarim kalmis seferlerini aliyoruz
        // 2 - bizim otobuslere ek sefer itelemişler mi bakiyoruz
        Elements table = null;
        Elements rows  = null;
        Element row    = null;
        Elements cols  = null;
        try {
            String hat_kod;
            String durum, orer;
            boolean oneri_oto_var;
            ArrayList<Otobus_Data> sefer_otolar_temp;
            Oneri_Sefer_Data oneri_sefer_data;

            for( int k = 0; k < hatlar_data.length(); k++ ){
                hat_kod = hatlar_data.getString(k);
                try {
                    js_con = Jsoup.connect("http://filo5.iett.gov.tr/_FYS/000/sorgu.php?konum=ana&konu=sefer&hat=" + Common.hat_kod_sef(hat_kod))
                            .cookie("PHPSESSID", cookie)
                            .method(org.jsoup.Connection.Method.POST)
                            .timeout(0)
                            .execute();

                    sefer_doc = js_con.parse();

                    updateMessage(( hat_kod + " hattı taranıyor..."));

                    table = sefer_doc.select("table");
                    rows = table.select("tr");
                    System.out.println(hat_kod + " ek sefer  veri işleniyor.." + " " + rows.size());
                    for (int i = 1; i < rows.size(); i++) {
                        oneri_oto_var = false;
                        sefer_otolar_temp = new ArrayList<>();
                        row = rows.get(i);
                        cols = row.select("td");

                        durum = cols.get(12).text().replaceAll("\u00A0", "");
                        orer = cols.get(7).getAllElements().get(2).text().replaceAll("\u00A0", "");
                        // sefer iptal veya yarimsa VE orer i geçmemişse işlem yapicaz
                        if( ( durum.equals("I") || durum.equals("Y") ) && !Sefer_Sure.gecmis( Common.get_current_hmin(), orer )  ){
                            // bolgedeki otobuslerden son seferi, iptal seferin orer ine uyanlari listelemek icin
                            // son orer listemizi kontrol edicez seferin orer ile karşılaştırıp
                            for (Map.Entry<String, Otobus_Data> otobus_item : otobusler_data.entrySet()) {
                                if( !Sefer_Sure.gecmis( otobus_item.getValue().get_son_orer(), orer ) && Sefer_Sure.hesapla( otobus_item.getValue().get_son_orer(), orer ) > 60 ){
                                    // son orer, iptal seferin baslangicindan geride yani ok
                                    if( !oneri_oto_var ) oneri_oto_var = true;
                                    // otobusun kodunu listeledik
                                    sefer_otolar_temp.add( otobus_item.getValue() );
                                }
                            }
                            // otobus - sonorer listemizi kontrol ettikten sonra uyum olan varsa
                            // output a ekliyoruz veriyi
                            if( oneri_oto_var ){
                                oneri_sefer_data = new Oneri_Sefer_Data( cols.get(3).getAllElements().get(1).text().replaceAll("\u00A0", ""), orer, durum, cols.get(13).text().substring(5), cols.get(4).getAllElements().get(2).text().replaceAll("\u00A0", "" ) );
                                oneri_sefer_data.oto_ekle( sefer_otolar_temp );
                                output.add( oneri_sefer_data );
                            }
                        }
                    }
                } catch (IOException | NullPointerException e) {
                    System.out.println("Ek sefer veri alım hatası.");
                    e.printStackTrace();
                    error = true;
                    updateMessage(( hat_kod + " veri alınırken bir hata oluştu. Tekrar deneniyor."));
                    //shutdown = true;
                    this.veri_al();

                }
            }
        } catch( JSONException e ){
            e.printStackTrace();
        }
    }


    @Override
    protected ArrayList<Oneri_Sefer_Data> call(){


        /*for( Oneri_Sefer_Data sefer_data: output ){
            sefer_data.test_print();
        }*/
        veri_al();
        updateMessage(( bolge + " bölgesi hat taraması tamamlandı."));
        //return a_tab;
        return this.output;
    }

    public boolean error(){
        return error;
    }


}
