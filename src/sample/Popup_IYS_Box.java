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

/**
 * Created by Jeppe on 18.04.2017.
 */
public class Popup_IYS_Box extends ScrollPane {

    private String oto;
    public Popup_IYS_Box( String oto ){
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
        VBox mesaj_ul = new VBox();
        mesaj_ul.setAlignment(Pos.CENTER);
        mesaj_ul.setSpacing(10);

        Web_Request request = new Web_Request(Web_Request.SERVIS_URL, "&req=iys_download&oto="+oto );
        request.kullanici_pc_parametreleri_ekle(true);
        request.action();
        JSONObject data = new JSONObject(request.get_value()).getJSONObject("data");
        JSONArray iys_data = data.getJSONArray("iys_data");

        try {
            ImageView ico;
            Label mesaj, tarih;
            AnchorPane mesaj_li;
            JSONObject item;
            for( int j = 0; j < iys_data.length(); j++ ){
                item = iys_data.getJSONObject(j);
                mesaj_li = new AnchorPane();
                mesaj_li.getStyleClass().add("li");
                ico = new ImageView( new Image(getClass().getResourceAsStream("resources/img/ico_obareybox_iys.png")));
                mesaj = new Label(item.getString("ozet"));
                mesaj.getStyleClass().addAll("fs12", "flight");
                mesaj.setMaxWidth(400);
                mesaj.setWrapText(true);
                tarih = new Label(Common.rev_date(item.getString("tarih")) + " ( "+item.getString("kaynak").substring(0, 10 )+" )");
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

                // TODO goruldu yap arkadaslari

            }

            main.getChildren().addAll( mesaj_ul );
            setContent(main);
        } catch( JSONException e ){ e.printStackTrace(); }


    }

}
