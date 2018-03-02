package sample.kahya;

import org.json.JSONArray;
import org.json.JSONObject;
import sample.User_Config;
import sample.Web_Request;

public class Kahya {

    private String oto;
    private Orer_Download orer_download;
    private boolean aktif = true;
    public Kahya( String _oto ){
        oto = _oto;
    }

    public void init(){

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {

                String aktif_durak_data;
                int aktif_durak_index, sonraki_durak_index;

                // baslangic verilerini filodan indir
                orer_download = new Orer_Download(oto, User_Config.filo5_cookie );
                orer_download.set_kahya_flag();
                orer_download.yap();

                System.out.println(orer_download.get_aktif_sefer_verisi());
                System.out.println(orer_download.get_hat());
                System.out.println(orer_download.get_aktif_orer());
                System.out.println(orer_download.get_aktif_sefer_index());
                System.out.println(orer_download.get_guzergahlar());


                // otobüsün aktif seferi yoksa iptal ediyoruz işlemleri
                if( orer_download.get_aktif_sefer_verisi().equals("YOK") ){
                    System.out.println("Otobüsün aktif seferi yok.");
                    return;
                }

                // hattın duraklarını indiriyoruz sunucudan
                Web_Request request = new Web_Request(Web_Request.SERVIS_URL, "&req=hat_durak_download&hat="+orer_download.get_hat());
                request.kullanici_pc_parametreleri_ekle();
                request.action();
                JSONArray data = new JSONObject(request.get_value()).getJSONObject("data").getJSONArray("durak_data");

                // duragın ismini substring yapiyoruz
                aktif_durak_data = orer_download.get_aktif_sefer_verisi().substring(19, orer_download.get_aktif_sefer_verisi().indexOf(" ("));
                System.out.println("aktif_durak_data: "  + aktif_durak_data );


                // yön tayini yapiyoruz
                // ring ve normal hat için degisik kontroller yapacagiz cunku
                int otobus_yon = Yon_Tayini.yap( orer_download.get_hat(), orer_download.get_aktif_sefer_index(), orer_download.get_guzergahlar() );
                // gidişte duraklari ileriye dogru sayicaz, donuste geriye dogru
                // bir sonraki duragın indexini bulmak için, aktif index ile toplayacagımız islem elemani
                int durak_counter_eleman = 0;
                if( otobus_yon == Yon_Tayini.RING ){
                    System.out.println("Otobüs RING hatta, yön bulma işlemi başlatıldı..");

                    //



                } else if( otobus_yon == Yon_Tayini.DONUS ){
                    durak_counter_eleman = -1;
                    System.out.println("Otobüs DÖNÜŞ yönünde");
                } else {
                    // gidis
                    durak_counter_eleman = +1;
                    System.out.println("Otobüs GİDİŞ yönünde");
                }

                JSONObject durak_json_temp;
                for( int k = 0; k < data.length(); k++ ){
                    durak_json_temp = data.getJSONObject(k);
                    if( aktif_durak_data.equals( durak_json_temp.getString("ad") )){
                        // aktif duragın sirasini buluyoruz
                        // inceleyecegimiz durak bundan sonraki olacak
                        aktif_durak_index = k;
                        if( data.getJSONObject(k+durak_counter_eleman) != null ){
                            sonraki_durak_index = k+durak_counter_eleman;
                        } else {
                            System.out.println("Son duraga gelinmiş.");
                        }
                    }
                }






                /*
                while( aktif ){
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

}
