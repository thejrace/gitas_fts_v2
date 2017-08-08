package sample;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jeppe on 09.03.2017.
 */
public class Web_Request {

    private String url, params, output;
    public static String MAIN_URL = "http://sadece100.com/fts/";
    public static String SERVIS_URL = MAIN_URL + "servis.php";

    // @todo internet baglantisi yoksa hata ver

    public Web_Request( String url, String params ){

        this.url = url;
        this.params = params;

    }

    public void kullanici_pc_parametreleri_ekle( boolean eposta_ekle ){
        if( eposta_ekle ){
            this.params += "&eposta="+User_Config.eposta_veri_al()+"&bilgisayar_adi="+Common.bilgisayar_adini_al()+"&bilgisayar_hash="+Common.mac_hash();
        } else {
            this.params += "&bilgisayar_adi="+Common.bilgisayar_adini_al()+"&bilgisayar_hash="+Common.mac_hash();
        }

    }

    public void action(){
        HttpURLConnection connection = null;
        System.out.println("İstek yapılıyor.. ( URL : " + this.url );
        try {

            URL url = new URL(this.url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded; charset=ISO-8859-1");
            //connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            connection.setRequestProperty("Content-Length",

                    Integer.toString(this.params.getBytes().length));
            connection.setRequestProperty("Content-Language", "tr-TR");
            connection.setRequestProperty( "charset", "ISO-8859-1");
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream());
            //wr.writeUTF(this.params);
            byte[] utf8JsonString = this.params.getBytes("UTF8");
            wr.write(utf8JsonString, 0, utf8JsonString.length);
            wr.close();

            // donen
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // StringBuffer Java 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            System.out.println(response.toString());
            output = response.toString();
        } catch (Exception e) {
            System.out.println("İstek yapılırken bir hata oluştu. Tekrar deneniyor.");
            e.printStackTrace();
            action();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public String get_value(){
        return output;
    }



}
