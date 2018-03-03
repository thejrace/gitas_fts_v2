package sample.kahya;

import org.json.JSONArray;
import org.json.JSONObject;
import sample.Common;
import sample.User_Config;
import sample.Web_Request;

import java.util.ArrayList;

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

                String aktif_durak_data;
                String hat;
                int aktif_durak_index, sonraki_durak_index;
                ArrayList<String> guzergahlar = new ArrayList<>();

                // baslangic verilerini filodan indir
                orer_download = new Orer_Download(oto, User_Config.filo5_cookie );
                orer_download.set_kahya_flag();
                orer_download.yap();

                System.out.println(orer_download.get_aktif_sefer_verisi());
                System.out.println(orer_download.get_hat());
                System.out.println(orer_download.get_aktif_orer());
                System.out.println(orer_download.get_aktif_sefer_index());
                System.out.println(orer_download.get_guzergahlar());

                hat = orer_download.get_hat();
                guzergahlar = orer_download.get_guzergahlar();

                // otobüsün aktif seferi yoksa iptal ediyoruz işlemleri
                if( orer_download.get_aktif_sefer_verisi().equals("YOK") ){
                    System.out.println("Otobüsün aktif seferi yok ya da durak verisi yok.");
                    return;
                }

                // hattın duraklarını indiriyoruz sunucudan
                Web_Request request = new Web_Request(Web_Request.SERVIS_URL, "&req=hat_durak_download&hat="+orer_download.get_hat());
                request.kullanici_pc_parametreleri_ekle();
                request.action();
                durak_json_data = new JSONObject(request.get_value()).getJSONObject("data").getJSONArray("durak_data");

                // duragın ismini substring yapiyoruz
                aktif_durak_data = orer_download.get_aktif_sefer_verisi().substring(orer_download.get_aktif_sefer_verisi().indexOf("-")+1, orer_download.get_aktif_sefer_verisi().indexOf(" ("));
                System.out.println("aktif_durak_data: "  + aktif_durak_data );


                // yön tayini yapiyoruz
                // ring ve normal hat için degisik kontroller yapacagiz cunku
                int otobus_yon = Yon_Tayini.yap( orer_download.get_hat(), orer_download.get_aktif_sefer_index(), orer_download.get_guzergahlar() );
                // gidişte duraklari ileriye dogru sayicaz, donuste geriye dogru
                // bir sonraki duragın indexini bulmak için, aktif index ile toplayacagımız islem elemani
                int durak_counter_eleman = 0;
                // otobsun durak indexini bul
                aktif_durak_index = durak_index_bul( aktif_durak_data, false );
                if( aktif_durak_index == -1 ){
                    aktif_durak_index = durak_index_bul( aktif_durak_data, true );
                    System.out.println("Aktif durak bulunamadı.[ DURAK ADI: "+aktif_durak_data+" ]");
                    return;
                }
                if( otobus_yon == Yon_Tayini.RING ){
                    System.out.println("Otobüs RING hatta, yön bulma işlemi başlatıldı..");
                    // aktif durakla, bir sonraki duragi karsilastiriyoruz
                    boolean yon_bulundu = false;
                    while( !yon_bulundu ){
                        try {
                            Thread.sleep(15000);
                        } catch( InterruptedException e ){
                            e.printStackTrace();
                        }
                        orer_download = new Orer_Download( oto, User_Config.filo5_cookie );
                        orer_download.set_kahya_flag();
                        orer_download.yap();
                        // ilk aldigimiz durak ismi, yeni veride varsa daha durak değişmemiş
                        // biraz bekleyip tekrar veri indiricez filodan
                        if( !orer_download.get_aktif_sefer_verisi().contains(aktif_durak_data) ){
                           // durak degismis, ileri mi geri mi gitmişiz onu bulucaz
                           aktif_durak_data = orer_download.get_aktif_sefer_verisi().substring(orer_download.get_aktif_sefer_verisi().indexOf("-")+1, orer_download.get_aktif_sefer_verisi().indexOf(" ("));
                           // burada distance_flag true yapiyoruz, cunku 1 kere yapiyoruz, olmazsa tekrar kontrol
                           // et demeye gerek yok, garanti olsun
                           sonraki_durak_index = durak_index_bul( aktif_durak_data, true );
                           if( sonraki_durak_index < aktif_durak_index ){
                               durak_counter_eleman = -1;
                               System.out.println("Ring hatta dönüş yönünde.");
                           } else {
                               durak_counter_eleman = 1;
                               System.out.println("Ring hatta gidiş yönünde.");
                           }
                           // artik duragimiz degisti yeni index oldu
                           aktif_durak_index = sonraki_durak_index;
                           yon_bulundu = true;
                        } else{
                            System.out.println("Durak değişmedi henüz. Tekrar deneniyor.");
                        }
                    }
                } else if( otobus_yon == Yon_Tayini.DONUS ){
                    durak_counter_eleman = -1;
                    System.out.println("Otobüs DÖNÜŞ yönünde");
                } else {
                    // gidis
                    durak_counter_eleman = +1;
                    System.out.println("Otobüs GİDİŞ yönünde");
                }

                String kontrol_edilecek_durak_data;
                while( aktif ){
                    kontrol_edilecek_durak_data = durak_json_data.getJSONObject(aktif_durak_index+durak_counter_eleman).getString("ad");

                    // kendi hattini tara
                    Orer_Download hat_tarama = new Orer_Download("", User_Config.filo5_cookie );
                    hat_tarama.set_hat_tarama( hat );
                    hat_tarama.yap();

                    // mobiett durak tara

                    // index i guncelliyoruz
                    aktif_durak_index = aktif_durak_index+durak_counter_eleman;
                    try {
                        Thread.sleep(10000);
                    } catch( InterruptedException e ){
                        e.printStackTrace();
                    }
                }

            }
        });
        th.setDaemon(true);
        th.start();






    }

    private int durak_index_bul( String durak_isim, boolean distance_flag ){
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
    }

}
