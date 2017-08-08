package sample;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Obarey on 17.02.2017.
 */
public class Surucu_Box {

    private String oto;
    private VBox surucu_ul;

    public Surucu_Box( String _oto ){
        oto = _oto;
    }

    public VBox get_ui(){
        return surucu_ul;
    }

    public void init(){

        surucu_ul = new VBox();
        VBox surucu_li;
        surucu_ul.getStylesheets().addAll( Filo_Table.class.getResource("resources/css/common.css").toExternalForm() );

        Web_Request request = new Web_Request(Web_Request.SERVIS_URL, "&req=pdks_detay&oto="+oto );
        request.kullanici_pc_parametreleri_ekle(true);
        request.action();
        JSONObject data = new JSONObject(request.get_value()).getJSONObject("data");

        JSONArray suruculer = data.getJSONArray("pdks_data");
        JSONObject surucu;
        for( int j = 0; j < suruculer.length(); j++ ){
            surucu = suruculer.getJSONObject(j);
            surucu_li = new VBox();
            surucu_li.getStyleClass().add("popup-surucu-container");
            surucu_li.setAlignment(Pos.CENTER);

            Label lbl_surucu_isim = new Label( surucu.getString("isim") );
            Label lbl_surucu_tel = new Label( "Telefon: " + surucu.getString("telefon") );
            Label lbl_surucu_sicil = new Label( "Sicil No: " + surucu.getString("sicil_no") );
            lbl_surucu_isim.setAlignment(Pos.CENTER);
            lbl_surucu_tel.setAlignment(Pos.CENTER);
            lbl_surucu_sicil.setAlignment(Pos.CENTER);

            lbl_surucu_isim.getStyleClass().add("popup-surucu-isim");
            lbl_surucu_tel.getStyleClass().add("popup-surucu-info");
            lbl_surucu_sicil.getStyleClass().add("popup-surucu-info");

            surucu_li.getChildren().addAll( lbl_surucu_isim, lbl_surucu_tel, lbl_surucu_sicil );
            surucu_ul.getChildren().add( surucu_li );
        }

    }

}
