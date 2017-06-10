package sample;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jeppe on 21.04.2017.
 */
public class Popup_Not_Box extends ScrollPane {

    private String oto, plaka;
    private Map<String, Obarey_Popup> popups = new HashMap<>();
    private Obarey_Popup yeni_ekle_popup;

    public Popup_Not_Box( String oto, String plaka ){
        super();
        this.oto = oto;
        this.plaka = plaka;
        this.setMaxHeight(500);
        this.setMaxWidth(780);
        this.setHbarPolicy( ScrollPane.ScrollBarPolicy.NEVER );
        this.setVbarPolicy( ScrollPane.ScrollBarPolicy.NEVER );
    }

    public void init(){
        VBox main = new VBox();
        main.getStyleClass().add("obarey-box-v2");
        main.setSpacing(10);
        main.setAlignment(Pos.CENTER);
        GButton yeni_ekle = new GButton( "Yeni Not Ekle", GButton.CMORK );
        VBox mesaj_ul = new VBox();
        mesaj_ul.setAlignment(Pos.CENTER);
        mesaj_ul.setSpacing(10);

        try {

            Web_Request dt_request = new Web_Request(Web_Request.SERVIS_URL, "&req=not_download&oto="+oto );
            dt_request.kullanici_pc_parametreleri_ekle(true);
            dt_request.action();
            JSONArray dt_data = new JSONObject(dt_request.get_value()).getJSONObject("data").getJSONArray("not_data");

            ImageView ico;
            Label mesaj, tarih;
            int tip;
            AnchorPane mesaj_li;
            for( int j = 0; j < dt_data.length(); j++ ){
                final JSONObject item = dt_data.getJSONObject(j);
                mesaj_li = new AnchorPane();
                mesaj_li.getStyleClass().addAll("li", "cursor-hand");
                if( item.getInt("durum") == 0 ) mesaj_li.getStyleClass().add("pasif");
                tip = item.getInt("tip");
                if( tip == Otobus_Not.SERVIS ){
                    ico = new ImageView( new Image(getClass().getResourceAsStream("resources/img/ico_obareybox_servis.png")));
                } else if( tip == Otobus_Not.UYARI ){
                    ico = new ImageView( new Image(getClass().getResourceAsStream("resources/img/ico_obareybox_uyari.png")));
                } else {
                    // kaza
                    ico = new ImageView( new Image(getClass().getResourceAsStream("resources/img/ico_obareybox_kaza.png")));
                }

                mesaj = new Label(item.getString("icerik"));
                mesaj.getStyleClass().addAll("fs12", "flight");
                mesaj.setMaxWidth(400);
                mesaj.setWrapText(true);
                tarih = new Label(Common.rev_datetime(item.getString("baslangic")) + " ( "+item.getString("yazan").substring(0, 10 )+" )");
                tarih.getStyleClass().addAll("fs10", "flight");
                mesaj_li.getChildren().addAll( ico, mesaj, tarih );
                AnchorPane.setLeftAnchor(ico, 5.0);
                AnchorPane.setTopAnchor(ico, 5.0);
                AnchorPane.setBottomAnchor(ico, 5.0);
                AnchorPane.setLeftAnchor(mesaj, 50.0);
                AnchorPane.setTopAnchor(mesaj, 5.0);
                AnchorPane.setBottomAnchor(mesaj, 5.0);
                AnchorPane.setRightAnchor(tarih, 5.0);
                AnchorPane.setTopAnchor(tarih, 5.0);
                AnchorPane.setBottomAnchor(tarih, 5.0);
                mesaj_ul.getChildren().addAll( mesaj_li );
                final String gid = item.getString("gid");
                mesaj_li.setOnMousePressed( ev -> {
                    System.out.println(item.toString());
                    // ayni notu birden fazla popupta actirma
                    if( !popups.containsKey( gid ) ){
                        Obarey_Popup pop = new Obarey_Popup(oto + " Not Görüntüle", getScene().getRoot() );
                        pop.init(true);
                        Popup_Not_Goruntule not_goruntule = new Popup_Not_Goruntule( item );
                        not_goruntule.add_listener(new Refresh_Listener() {
                            @Override
                            public void on_refresh() {
                                init();
                            }
                        });
                        pop.set_content( not_goruntule );
                        pop.show( ev.getScreenX(), ev.getScreenY() );
                        popups.put( gid, pop );
                    } else if( !popups.get(gid).ison() ){
                        popups.get(gid).init(true);
                        popups.get(gid).set_content( new Popup_Not_Goruntule( item ) );
                        popups.get(gid).show( ev.getScreenX(), ev.getScreenY() );
                    }
                });
            }
            main.getChildren().addAll( yeni_ekle, mesaj_ul );
            setContent(main);

            yeni_ekle.setOnMousePressed( ev -> {
                if( yeni_ekle_popup == null ){
                    yeni_ekle_popup = new Obarey_Popup(oto + " Not Ekle", getScene().getRoot() );
                    yeni_ekle_popup.init(true);
                    Popup_Not_Ekle not_ekle = new Popup_Not_Ekle(oto, plaka);
                    not_ekle.add_listener(new Refresh_Listener() {
                        @Override
                        public void on_refresh() {
                            init();
                        }
                    });
                    yeni_ekle_popup.set_content( not_ekle );
                    yeni_ekle_popup.show( ev.getScreenX(), ev.getScreenY() );
                } else if( !yeni_ekle_popup.ison() ){
                    yeni_ekle_popup.init(true);
                    Popup_Not_Ekle not_ekle = new Popup_Not_Ekle(oto, plaka);
                    not_ekle.add_listener(new Refresh_Listener() {
                        @Override
                        public void on_refresh() {
                            init();
                        }
                    });
                    yeni_ekle_popup.set_content( not_ekle );
                    yeni_ekle_popup.show( ev.getScreenX(), ev.getScreenY() );
                }

            });

        } catch( JSONException e ){ e.printStackTrace(); }



    }



}
