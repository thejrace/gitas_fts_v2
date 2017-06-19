package sample;

import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jeppe on 20.06.2017.
 */
public class Zayiat_Rapor_Datatable extends ScrollPane {
    private JSONObject data;
    private JSONArray sort_keys;
    public Zayiat_Rapor_Datatable( JSONObject data, JSONArray sort_keys ){
        super();
        setMaxHeight(450);
        this.sort_keys = sort_keys;
        this.data = data;
    }

    public void init(){
        VBox main_vb = new VBox();
        main_vb.setAlignment(Pos.CENTER);
        main_vb.setSpacing(10);
        JSONObject item;
        for( int j = 0; j < sort_keys.length(); j++ ){
            try {
                item = data.getJSONObject(sort_keys.getString(j));
                Sefer_Rapor_Data temp_data = new Sefer_Rapor_Data(
                        item.getInt("zayi_sefer"),
                        item.getDouble("zayi_gkm"),
                        item.getDouble("zayi_iett")
                );
                main_vb.getChildren().add(new Zayiat_Box(sort_keys.getString(j), temp_data));
            } catch( JSONException e ){
                e.printStackTrace();
            }
        }
        setContent( main_vb );
    }

}
