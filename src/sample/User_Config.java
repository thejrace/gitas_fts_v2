package sample;

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
            IOB_NOTLAR                  = 24;

    public static int   GIRIS_MOD = 1,
                        KAYIT_MOD = 2,
                        CONFIG_YOK_MOD = 3;

    public static int   KAYIT_ONAYBEKLENIYOR = 1,
                        GIRIS_ONAYBEKLENIYOR = 2,
                        GIRIS_ONAYLANDI = 3,
                        HATA = 4,
                        SIFRE_BOSGELDI = 6;

    public static String CONFIG_JSON = "C://temp/config.json", // TODO setup a eklicez bunlari
                         GITAS_JSON = "src/sample/data/gitas.json";


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
        } catch (IOException e) {
            // do something
        }
    }

    public static JSONObject config_oku(){
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

    // configte eposta var, onun kontrolu
    public static JSONObject ilk_acilis_kullanici_kontrolu(){
        Web_Request request = new Web_Request( Web_Request.SERVIS_URL, "&req=kullanici_kontrol");
        request.kullanici_pc_parametreleri_ekle( true );
        request.action();
        return new JSONObject( request.get_value() );
    }

    // formla giriş - kayıt
    public static JSONObject giris_kayit_form_submit( String eposta, String pass ){
        Web_Request request = new Web_Request( Web_Request.SERVIS_URL, "&req=kullanici_kontrol&eposta="+eposta+"&pass="+pass);
        request.kullanici_pc_parametreleri_ekle( false );
        request.action();
        return new JSONObject( request.get_value() );
    }

    // onay bekleme kullanici kontrolu
    public static JSONObject kullanici_kontrol( String eposta ){
        Web_Request request = new Web_Request( Web_Request.SERVIS_URL, "&req=kullanici_kontrol&eposta="+eposta);
        request.kullanici_pc_parametreleri_ekle( false );
        request.action();
        return new JSONObject( request.get_value() );
    }

    // sunucudan gelen config sync
    public static void config_guncelle( String yeni_config ){
        try{
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(CONFIG_JSON, false)));
            writer.print(yeni_config);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
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
        JSONObject config = config_oku();
        return new Ayarlar_Genel_Data( config.getString("alarmlar"), config.getInt("orer_frekans"), config.getInt("iys_frekans"), config.getInt("mesaj_frekans"));
    }

    public static boolean izin_kontrol( int tip ){
        JSONObject config = config_oku();
        return config.getString("gtiz").charAt(tip) == '1';
    }


}
