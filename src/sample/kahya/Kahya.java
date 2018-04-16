package sample.kahya;

import org.json.JSONArray;

public class Kahya {

    private String oto;
    private Orer_Download orer_download;
    private boolean aktif = true;
    private JSONArray durak_json_data;
    public Kahya( String _oto ){
        oto = _oto;
    }

    public void init(){

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {

                Otobus_Kahya_Data aktif_otobus = new Otobus_Kahya_Data(oto);
                aktif_otobus.set_aktif_otobus_flag();
                aktif_otobus.yon_bul();
                aktif_otobus.add_listener(new Kahya_Hat_Download_Listener() {
                    @Override
                    public void download_durak_data(JSONArray durak_data) {
                        System.out.println(durak_data);
                    }
                });




               /* String kontrol_edilecek_durak_data;
                while( aktif ){
                    //kontrol_edilecek_durak_data = durak_json_data.getJSONObject(aktif_durak_index+durak_counter_eleman).getString("ad");

                    // kendi hattini tara
                    Orer_Download hat_tarama = new Orer_Download("", User_Config.filo5_cookie );
                    hat_tarama.set_durum_param(Sefer_Data.DAKTIF);
                    //hat_tarama.set_hat_tarama( hat, oto );
                    hat_tarama.yap();

                    // mobiett durak tara

                    // index i guncelliyoruz
                    //aktif_durak_index = aktif_durak_index+durak_counter_eleman;
                    try {
                        Thread.sleep(10000);
                    } catch( InterruptedException e ){
                        e.printStackTrace();
                    }
                }*/

            }
        });
        th.setDaemon(true);
        th.start();






    }

    /*private int durak_index_bul( String durak_isim, boolean distance_flag ){
        JSONObject durak_json_temp;
        for( int k = 0; k < durak_json_data.length(); k++ ){
            durak_json_temp = durak_json_data.getJSONObject(k);
            System.out.println(durak_isim + "  " + durak_json_temp.getString("ad") );
            if( distance_flag ){
                // ufak tefek isim farkliliklari icin, farklilik ölçme algoritmasi kullaniyoruz
                // bu durum seyrek olacagi icin, tum index taramalarinda degil yalnizca distance_flag false iken
                // benzetemedigimiz durumda bu kontrolü yapiyoruz
                if(Common.Levenshtein_distance(durak_isim, durak_json_temp.getString("ad") ) < 7 ) {
                    return k;
                }
            } else {
                if( durak_isim.equals( durak_json_temp.getString("ad") )){
                    // aktif duragın sirasini buluyoruz
                    // inceleyecegimiz durak bundan sonraki veya onceki olacak
                    return k;
                }
            }

        }
        return -1;
    }*/

}
