package sample;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OAS_Aktif_Liste_Download {

    private boolean durdur = false;
    private static String EMO = "EMRE MEHMET ÖZBEK";
    private static String YC  = "YAVUZ ÇİMEN";

    private ArrayList<String> otobusler = new ArrayList<>();
    private OAS_Listener listener;
    private Map<String, Integer> tekrar_kontrol = new HashMap<>();
    private Map<String, JSONArray> seferler_temp = new HashMap<>();
    public OAS_Aktif_Liste_Download(){

    }

    public void durdur(){
        durdur = true;
    }

    public void set_listener( OAS_Listener _listener ){
        listener = _listener;
    }

    public void set_otobusler( ArrayList<String> _otobusler ){
        otobusler = _otobusler;
    }

    public void start(){

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                while( !durdur ){

                    tekrar_kontrol  = new HashMap<>();
                    seferler_temp  = new HashMap<>();

                    try {
                        org.jsoup.Connection.Response sefer_verileri_req = Jsoup.connect("http://filo5.iett.gov.tr/oas.aktif.liste.php")
                                .method(org.jsoup.Connection.Method.POST)
                                .timeout(50000)
                                .execute();

                        Document sefer_doc = sefer_verileri_req.parse();
                        Elements table = null;
                        Elements rows = null;
                        Element row = null;
                        Elements cols = null;
                        Element col = null;


                        table = sefer_doc.select("table");
                        rows = table.select("tr");

                        if (rows.size() == 1 || rows.size() == 0) {
                            System.out.println(" ORER Filo Veri Yok");
                            return;
                        }
                        /*
                        * td indexler
                        * 0: isletmeci ( Emre Mehmet Özbek )
                        * 1: kapi ( B-1473 34 ZN 3311 )
                        * 2: hat
                        * 3: sofor adi
                        * 4: sofor_sicil_no
                        * 5: oto ( B-1432-30A )
                        * 6 ... seferler
                        * */
                        String oto;

                        Elements fonts;
                        for( int j = 1; j < rows.size(); j++ ){
                            row = rows.get(j);
                            cols = row.select("td");
                            oto = cols.get(1).text().substring(0,6);
                            if( otobusler.contains(oto) && (cols.get(0).text().equals(EMO) || cols.get(0).text().equals(YC))  ){
                                JSONArray seferler;
                                if( seferler_temp.containsKey(oto) ){
                                    seferler = seferler_temp.get(oto);
                                    int prev_val = tekrar_kontrol.get(oto);
                                    tekrar_kontrol.put(oto, prev_val+1);
                                } else {
                                    seferler = new JSONArray();
                                    tekrar_kontrol.put(oto, 1);
                                }
                                //System.out.println(oto);

                                /*System.out.println("KAPI: " + cols.get(1).text() );
                                System.out.println("HAT:  " + cols.get(2).text() );
                                System.out.println("SOFOR ADI:  " + cols.get(3).text() );
                                System.out.println("SOFOR SNO:  " + cols.get(4).text() );*/

                                int sno = 0;
                                for( int k = 6; k < cols.size(); k++ ){
                                    col = cols.get(k);
                                    if( !col.text().equals("") ){
                                        fonts = col.select("font");
                                        if( fonts.size() == 2 ){
                                            sno++;
                                            //tek orer
                                            seferler.put( return_sefer_data( String.valueOf(sno), cols.get(2).text(), oto, cols.get(3).text(), cols.get(4).text(), Common.regex_trim(fonts.get(0).text()), get_durum(fonts.get(0).attr("color")), Common.regex_trim(fonts.get(1).text()) ) );
                                        } else {
                                            // iki orer birden
                                            sno++;
                                            seferler.put( return_sefer_data( String.valueOf(sno), cols.get(2).text(), oto, cols.get(3).text(), cols.get(4).text(), Common.regex_trim(fonts.get(0).text()), get_durum(fonts.get(0).attr("color")), Common.regex_trim(fonts.get(1).text()) ) );
                                            sno++;
                                            seferler.put( return_sefer_data( String.valueOf(sno), cols.get(2).text(), oto, cols.get(3).text(), cols.get(2).text(), Common.regex_trim(fonts.get(0).text()), get_durum(fonts.get(0).attr("color")), Common.regex_trim(fonts.get(3).text()) ) );
                                        }
                                    }
                                }
                                seferler_temp.put(oto, seferler);
                                listener.on_download( oto, seferler );
                                //break;

                            }
                        }

                        for (Map.Entry<String, Integer> entry : tekrar_kontrol.entrySet()) {
                            String key = entry.getKey();
                            int value = entry.getValue();

                            if( value == 1 ) continue;

                            boolean ters = false;
                            int sw_no = 0;
                            String prev_orer = "YOK";
                            JSONObject sef_data_temp;
                            for( int k = 0; k < seferler_temp.get(key).length(); k++  ){
                                sef_data_temp = seferler_temp.get(key).getJSONObject(k);
                                if( !prev_orer.equals("YOK") ){
                                    if( Sefer_Sure.gecmis(prev_orer, sef_data_temp.getString("orer"))  ){
                                        ters = true;
                                        sw_no = k;
                                        System.out.println(key + " ters ulen no: " + k);
                                        break;
                                    }
                                }
                                if( k > 0 ) prev_orer = sef_data_temp.getString("orer");
                            }
                            if( ters ){

                                JSONArray dogru_data = new JSONArray();
                                for( int k = sw_no; k < seferler_temp.get(key).length(); k++ ){
                                    dogru_data.put(seferler_temp.get(key).getJSONObject(k));
                                }
                                for( int k = 0; k < sw_no; k++ ){
                                    dogru_data.put(seferler_temp.get(key).getJSONObject(k));
                                }
                                seferler_temp.put(key, dogru_data);
                                listener.on_download( key, dogru_data );

                            }

                        }
                    } catch (IOException  e) {
                        System.out.println( "["+Common.get_current_hmin() + "] veri alım hatası. Tekrar deneniyor[1].");
                        e.printStackTrace();
                    }



                    try{
                        Thread.sleep(60000);
                    } catch( InterruptedException e ){
                        e.printStackTrace();
                    }

                }
            }
        });
        th.setDaemon(true);
        th.start();


    }


    private String get_durum( String ccode ){
        switch(ccode){
            case "#00FF00": // tamam
                return "T";
            case "#":       // bekleyen
                return "B";
            case "#0000FF": // aktif
                return "A";
            case "#FF0000": // iptal
                return "I";
            case "#FF00FF": // yarim
                return "Y";
        }
        return "T";
    }

    private JSONObject return_sefer_data( String no, String hat, String oto, String surucu, String suruc_sno, String orer, String durum, String durum_kodu ){
        return new Sefer_Data(
                no,
                hat,
                "",
                "",
                oto,
                surucu,
                suruc_sno,
                "",
                "",
                orer.substring(0, 5),
                "",
                "",
                "",
                "",
                "",
                durum,
                durum_kodu,
                "",
                1,
                0
        ).tojson();
    }


}
