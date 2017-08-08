package sample;

import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.naming.MalformedLinkException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * Created by Obarey on 19.02.2017.
 */
public class Filo_Harita_Request_Task {

    public static int    HAT_HARITA = 1,
                         TAKIP_HARITA = 2,
                         KONUM_HARITA = 3;

    private String oto;
    private String bolge;
    private int type;
    private String hat_guz, js_action;
    private URL url;
    private WebView web_view;
    public Filo_Harita_Request_Task( String oto, String hat_guz, int type ){
        this.oto = oto;
        this.bolge = oto.substring(0,1);
        this.type = type;
        this.hat_guz = hat_guz;

    }

    public WebView get_webview(){
        return web_view;
    }
    public void init() {
        String cookie;
        JSONObject cookies = User_Config.cookie_config_oku();
        System.out.println(cookies);
        try {
            // init varsa daha giris yapilmamis demektir bekliyoruz
            if (cookies.getBoolean("init")) {
            }
            return;
        } catch (JSONException e) {
            //e.printStackTrace();
            cookie = cookies.getString(bolge);
        }


        //  http://maps.google.com/m/maps?f=q&hl=tr&z=17&q=41.1084,28.801027
        //  http://maps.google.com/m/maps?f=q&hl=tr&z=17&q=40.937275,29.29112
        //  http://maps.google.com/maps/api/staticmap?sensor=false&center=40.937275,29.29112&zoom=15&size=512x512&markers=color:green|label:X|40.937275,%2029.29112

        web_view = new WebView();
        if (type == TAKIP_HARITA) {
            try {
                url = new URL("http://filo5.iett.gov.tr/_FYS/000/cevrimDisi.php?gzg=" + hat_guz + "&kapino=" + oto + "&hiz=&saat=&sure=60&limit=-1&ara=ARA"); // 00:00 dan itibaren
            } catch( MalformedURLException e ){
                e.printStackTrace();
            }

            js_action = "$(document).ready(function(){" +
                    "$(\"[title='Başlat']\").click(function(){" +
                    "var temp_zoom = map.getZoom(); map.setZoom(1); map.setZoom(temp_zoom);" +
                    "});" +

                    "var boddy = document.body;  boddy.style.background = '#fff'; var harita = document.getElementById('harita_google'); harita.style.width = 600 + 'px'; harita.style.height = 490 + 'px';  " +
                    " $('table').css( { 'borderCollapse': 'collapse', 'fontSize': 11 + 'px' } );  $('div[style=\"overflow:scroll; height:600px;\"]').css({ height:510 + 'px' });  " +
                    " $('#id_saat').css({ width: 90 + 'px' }); $('a[href=\"#\"]').html('');  " +
                    "});" +
                    "function izleoto_no(){" +
                    "if(animasyon_aktif==1 && noktasira<adet-3){" +
                    "var temp_zoom = map.getZoom(); map.setZoom(1); map.setZoom(temp_zoom);" +
                    "clearOverlays(noktasira);" +
                    "    izleoto(konum[noktasira][0],konum[noktasira][1],konum[noktasira][2]);" +
                    " if(noktasira>adet) {noktasira=0;}" +
                    "else { setTimeout(\"izleoto_no()\",oynatmaHizi); }" +
                    "  }" +
                    "}" +
                    "function izle_oto(){" +
                    "animasyon_aktif=1;" +
                    "noktasira=0;" +
                    "var temp_zoom = map.getZoom(); map.setZoom(1); map.setZoom(temp_zoom);" +
                    "clearOverlays(null);" +
                    "izleoto_no();" +
                    "}function izleoto(y,x,canli){ " +
                    "  var nokta = new google.maps.LatLng(y,x);" +
                    "      if(canli==1) {var ikon = ortaIsaretResim; var uyar = \"\";}" +
                    "  else  {var ikon = ortaIsaretResimUyari; var uyar = \" - SINYAL YOK\";}" +
                    "  var markerOrtaIsaret = new google.maps.Marker({" +
                    "icon: ikon," +
                    "position: nokta," +
                    "map: map" +
                    "});" +
                    "" +
                    "gmarkers.push(markerOrtaIsaret); " +
                    "" +
                    "   if($(\"input:checked\").length>0) map.setCenter(nokta,15);" +
                    " " +
                    "   ortaIsaretResimVar=1;" +
                    "   document.getElementById(\"id_saat\").value=konum[noktasira][3]+\" \"+uyar;" +
                    "" +
                    "var temp_zoom = map.getZoom(); map.setZoom(1); map.setZoom(temp_zoom);" +
                    "   noktasira++;" +
                    "}";


        } else if( type == KONUM_HARITA ){
            web_view.setPrefWidth(512);
            web_view.setMaxWidth(512);
            web_view.setMaxHeight(512);
            web_view.setMaxHeight(512);
            try {
                org.jsoup.Connection.Response js_con = Jsoup.connect("http://filo.iett.gov.tr/akyolbil/uyg.php?abc=1&talep=5&grup=3&hat="+oto)
                        .cookie("PHPSESSID", cookie)
                        .method(org.jsoup.Connection.Method.POST)
                        .execute();
                Document sefer_doc = js_con.parse();
                Elements table = sefer_doc.select("table");
                Elements trs = table.select("tr");
                String href;
                try {

                    href = trs.get(7).getElementsByAttribute("href").get(0).attr("href").substring(47);
                    System.out.println(href);
                } catch( NullPointerException | IndexOutOfBoundsException  e ){
                    e.printStackTrace();
                    return;
                }
                try {
                    url = new URL( "http://maps.google.com/maps/api/staticmap?sensor=false&center="+href+"&zoom=15&size=512x512&markers=color:green|label:X|"+href); // 00:00 dan itibaren
                } catch( MalformedURLException e ){
                    e.printStackTrace();
                }

            } catch( IOException e ){
                e.printStackTrace();
            }






        } else{
            web_view.setPrefWidth(790);
            web_view.setMaxWidth(790);
            try {
                url = new URL( "http://filo5.iett.gov.tr/_FYS/000/harita.php?konu=hat&hat="+hat_guz ); // 00:00 dan itibaren
            } catch( MalformedURLException e ){
                e.printStackTrace();
            }

            js_action = "var boddy = document.body;  boddy.style['overflow-x'] = 'hidden'; boddy.style['overflow-y'] = 'hidden'; boddy.style.fontFamily = 'Trebuchet MS'; boddy.style.fontSize = 14 + 'px';" +
                    " var yanbar = document.getElementById('side_bar'); yanbar.style.color = 'purple'; yanbar.style.lineHeight = 1.2;  yanbar.style.border = '2px solid purple'; yanbar.style.height = 'auto'; yanbar.style.padding = '13px 0'; yanbar.style.textAlign = 'center'; yanbar.style['textDecoration'] = 'none'; " +
                    "yanbar.style.fontSize = 14 + 'px'; var links = document.links; for( var x = 0; x < links.length; x++ ) { links[x].style['textDecoration'] = 'none'; links[x].style.color = 'purple'; }  google.maps.event.addListenerOnce(map, 'tilesloaded', function(){" +
                    "    document.getElementById(\"map_canvas\").style.width = 650+ 'px'; document.getElementById('map_canvas').style.height = 549+ 'px'; "  +
                    "});";
        }

        System.out.println(url);
        try{
            // FIXME Thread icinde webView olusturamadım. Nedense exception da vermedi. Simdilik bi sıkıntı yok ama UI_Thread i kitlememek lazım.
            // WEBVIEW SESSION -> http://stackoverflow.com/questions/14385233/setting-a-cookie-using-javafxs-webengine-webview

           // URI uri = URI.create(url);
            String nullFragment = null;
            URI uri = null;
            try {
                uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), nullFragment);
            } catch( URISyntaxException | NullPointerException e ){
                e.printStackTrace();
            }

            Map<String, List<String>> headers = new LinkedHashMap<>();
            headers.put("Set-Cookie", Arrays.asList("PHPSESSID="+cookie));
            java.net.CookieHandler.getDefault().put(uri, headers);
            WebEngine engine = web_view.getEngine();

            // DOM ready gibi javascript kodlarını sayfa yuklendikten sonra calistiriyoruz
            engine.getLoadWorker().stateProperty().addListener(
                    (ObservableValue<? extends Worker.State> observable,
                     Worker.State oldValue,
                     Worker.State newValue) -> {
                        if( newValue != Worker.State.SUCCEEDED ) {
                            return;
                        }
                        // oynatma esnasında onceki frame in marker lari silinmiyordu, harita refresh etmiyo gibi
                        // zoom yapinca gidiyolar o yuzden filonun haritada animasyonu yapan fonksiyonlarını ( izleoto_no, izle_oto, izleoto
                        // override ediyorum, clearOverlays fonksiyonu bir onceki frame in marker ı haritadan silen fonksiyon ondan hemen once
                        // haritanınn aktif zoom degerini 1 yapıp tekrar eski haline getiriyorum. hızlı refresh gibi oluyor.
                        // yavas hızlarda problem yok ama cok hızlıya alınca biraz garip oluyor. idare eder simdilik ama ( 19.02.2017 )
                        // @FIXME Google Maps ve WebView ile ilgili refresh sıkıntısı var. Simdilik idare ediyor ama bakmak lazım.
                        engine.executeScript(js_action);
                    } );
            engine.load(url.toString());

        } catch( IOException e ){
            e.printStackTrace();
        }

    }




}
