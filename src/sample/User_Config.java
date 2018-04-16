package sample;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;

/**
 * Created by Jeppe on 18.05.2017.
 */
public class User_Config {

    public static int
            IAY_GENEL                   = 0,
            IAY_OTOBUSLER               = 1,
            IAY_HATLAR                  = 2,
            IAY_SURUCULER               = 3,
            IAY_KM                      = 4,
            IAY_EKSEFERONERI            = 5,
            IAY_TEMA                    = 6,
            IAY_ALARMLAR                = 7,
            IAY_HESAP                   = 8,
            ISE_EXCEL                   = 9,
            ISE_EKSEFERONERI            = 10,
            ISE_ISTATISTIK_RAPOR        = 11,
            IOB_PLAN                    = 12,
            IOB_ALARMLAR                = 13,
            IOB_MESAJ                   = 14,
            IOB_SURUCU                  = 15,
            IOB_IYS                     = 16,
            IOB_HARITA                  = 17,
            IOB_KMRAPOR                 = 18,
            IFS_ORER                    = 19,
            IFS_MESAJ                   = 20,
            IFS_IYS                     = 21,
            IFS_EKSEFER                 = 22,
            IOB_PLAKA_DEGISTIRME        = 23,
            IOB_NOTLAR                  = 24,
            ISE_ZAYI_RAPORLAR           = 25;

    public static int   GIRIS_MOD = 1,
                        KAYIT_MOD = 2,
                        CONFIG_YOK_MOD = 3;


    public static String CONFIG_JSON = "C://temp/config.json", // TODO setup a eklicez bunlari
                         GITAS_JSON = "src/sample/data/gitas.json",
                         COOKIES_JSON = "C://temp/cookies.json";

    public static JSONObject app_ayarlar, app_filtre, app_filo5_data;
    public static JSONArray app_otobusler;
    public static JSONArray app_profiller;
    public static String app_aktif_profil;
    public static int app_aktif_profil_index; // combobox için
    public static String filo5_cookie;

    public static void init_app_data( String key, JSONObject guncel_data ){
        if( key.equals("init") ){
            app_ayarlar = guncel_data.getJSONObject("ayarlar");
            app_filtre = guncel_data.getJSONObject("filtre");
            app_filo5_data = guncel_data.getJSONObject("filo5_data");
            app_otobusler = guncel_data.getJSONArray("otobusler");
            app_profiller = guncel_data.getJSONArray("app_data_profiller");
            for( int k = 0; k < app_profiller.length(); k++ ){
                if(app_profiller.getJSONObject(k).getInt("durum") == 1 ){
                    User_Config.app_aktif_profil = app_profiller.getJSONObject(k).getString("profil_ismi");
                    app_aktif_profil_index = k;
                    break;
                }
            }
        } else {
            if( key.equals("ayarlar_data") ){
                app_ayarlar = guncel_data;
            } else if( key.equals("filtre_data") ){
                app_filtre = guncel_data;
            } else if( key.equals("filo5_data")){
                app_filo5_data = guncel_data;
            }
        }
    }

    // Program ilk açıldığında giriş - kayıt işlemlerinden hangisini yapacagimizi
    // anladigimiz metod
    public static int baslangic_config_kontrol(){
        System.out.println("BASLANGIÇ CONFIG KONTROLU YAPILIYOR");
        try {
            FileReader fr = new FileReader(CONFIG_JSON);
            BufferedReader br = new BufferedReader(fr);
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();
                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                JSONObject config = new JSONObject( sb.toString() );
                try{
                    if( config.getBoolean("init") ){
                        System.out.println("GİTAŞ FTS İLK AÇILIŞ AYARLAMALARI YAPILIYOR");
                        return KAYIT_MOD;
                    }
                } catch( NullPointerException | JSONException e ){
                    e.printStackTrace();
                    System.out.println("İLK AÇILIŞ DEĞİL, EPOSTA KONTROLU YAPILIYOR");
                    try {
                        if( !config.getString("eposta").equals("") ) return GIRIS_MOD;
                    } catch ( NullPointerException e2 ){
                        System.out.println("EPOSTA YOK");
                        e2.printStackTrace();
                        return KAYIT_MOD;
                    }
                    return KAYIT_MOD;
                }
            } finally {
                br.close();
            }
        } catch( IOException e ){
            System.out.println("CONFIG DOSYASI YOK");
            e.printStackTrace();
        }
        return CONFIG_YOK_MOD;
    }

    public static void config_dosyasi_olustur(){
        try{
            PrintWriter writer = new PrintWriter(CONFIG_JSON, "UTF-8");
            writer.println("{\"init\":true}");
            writer.close();

            cookie_json_dosyasi_olustur();
        } catch (IOException e) {

        }
    }

    public static void cookie_json_dosyasi_olustur(){
        try{
            PrintWriter writer = new PrintWriter(COOKIES_JSON, "UTF-8");
            writer.println("{\"init\":true}");
            writer.close();
        } catch (IOException e) {

        }
    }

    public static JSONObject cookie_config_oku(){
        return config_tojson_cevir(COOKIES_JSON);
    }

    public static JSONObject config_oku(){
        return config_tojson_cevir(CONFIG_JSON);
    }

    public static JSONObject config_tojson_cevir( String src ){
        try {
            FileReader fr = new FileReader(src);
            BufferedReader br = new BufferedReader(fr);
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();
                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                return new JSONObject(sb.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch( IOException e ){
            e.printStackTrace();
        }
        return new JSONObject();
    }

    public static String eposta_veri_al(){
        JSONObject config = config_oku();
        return config.getString("eposta");
    }

    public static void eposta_guncelle( String eposta ){
        try{
            PrintWriter writer = new PrintWriter(CONFIG_JSON, "UTF-8");
            writer.print("{\"eposta\":\""+eposta+"\"}");
            writer.close();
        } catch (IOException e) {

        }
    }


    public static void config_reset(){
        try{
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(CONFIG_JSON, false)));
            writer.print("{\"init\":true}");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Ayarlar_Genel_Data ayarlari_oku(){
        return new Ayarlar_Genel_Data( app_ayarlar.getString("alarmlar"), 0,0,0);
    }

    public static boolean izin_kontrol( int tip ){
        return  app_ayarlar.getString("izinler").charAt(tip) == '1';
    }


}
