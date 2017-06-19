package sample;

import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by Jeppe on 18.04.2017.
 */
public class Popup_Mesaj_Box extends ScrollPane {

    private String oto;
    private JSONArray mesajlar;
    private Label notf_lbl;
    private VBox mesaj_ul;

    public Popup_Mesaj_Box( String oto ){
        super();
        this.oto = oto;
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

        HBox dp_container = new HBox();
        DatePicker dp = new DatePicker();
        dp.setValue(Common.dp_placeholder(Common.get_current_date()));
        dp.getStyleClass().add("grigrad");
        GButton getir_btn = new GButton("Getir", GButton.CMORK);
        notf_lbl = new Label("Veri bulunamadı!");

        dp_container.setAlignment(Pos.CENTER);
        dp_container.setSpacing(5);
        dp_container.getChildren().addAll( dp, getir_btn, notf_lbl );

        mesaj_ul = new VBox();
        mesaj_ul.setAlignment(Pos.CENTER);
        mesaj_ul.setSpacing(10);

        main.getChildren().addAll( dp_container, mesaj_ul );
        setContent(main);

        update_data( "AKTIF");
        update_ui();

        getir_btn.setOnMousePressed(ev -> {
            System.out.println(dp.getValue());
            if( dp.getValue() == null ) return;
            notf_lbl.setText("Veri alınıyor...");
            Thread mesaj_th = new Thread( new Task<String>(){
                @Override
                protected void succeeded(){
                    try {
                        update_ui();
                    } catch (Exception e ){
                        e.printStackTrace();
                    }
                }
                @Override
                protected String call(){
                    update_data( dp.getValue().toString() );
                    return null;
                }
            });
            mesaj_th.setDaemon(true);
            mesaj_th.start();
        });

    }

    public void update_ui(){
        if( mesajlar.length() == 0 ) {
            notf_lbl.setText("Veri yok.");
        }
        mesaj_ul.getChildren().clear();
        try {
            ImageView ico;
            Label mesaj, tarih;
            AnchorPane mesaj_li;
            JSONObject item;
            for( int x = 0; x < mesajlar.length(); x++ ){
               item = mesajlar.getJSONObject(x);
               mesaj_li = new AnchorPane();
               mesaj_li.getStyleClass().add("li");
               ico = new ImageView( new Image(getClass().getResourceAsStream("resources/img/ico_obareybox_mesaj.png")));
               mesaj = new Label(item.getString("mesaj"));
               mesaj.getStyleClass().addAll("fs12", "flight");
               mesaj.setMaxWidth(400);
               mesaj.setWrapText(true);
               tarih = new Label(item.getString("saat") + " ( "+item.getString("kaynak")+" )");

               tarih.setTooltip( new Tooltip(item.getString("kaynak")) );
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
            }

            notf_lbl.setText("");
            System.out.println(mesajlar);
            mesajlar = new JSONArray();
        } catch( Exception e ){
            e.printStackTrace();
        }
    }

    public void update_data( String tarih ){

        Web_Request request = new Web_Request(Web_Request.SERVIS_URL, "&req=mesaj_download&oto="+oto+"&tarih="+tarih );
        request.kullanici_pc_parametreleri_ekle(true);
        request.action();
        JSONObject data = new JSONObject(request.get_value()).getJSONObject("data");
        mesajlar = data.getJSONArray("mesaj_data");

    }

}
