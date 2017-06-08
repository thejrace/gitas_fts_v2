package sample;

import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Jeppe on 15.03.2017.
 */
public class Rapor_Datatable extends Task<Void> {

    public static String    OTOBUS_DT = "oto",
                            HAT_DT = "hat",
                            SURUCU_DT = "surucu";
    private VBox cont;
    private JSONArray data;
    private String tip, params;

    public Rapor_Datatable( String tip, String params ){
        this.tip = tip;
        this.params = params;
    }

    @Override
    protected Void call() {
        cont = new VBox();
        cont.setAlignment(Pos.CENTER);
        cont.setSpacing(10);

        Web_Request dt_request = new Web_Request(Web_Request.SERVIS_URL, "&req=filo_rapor_dt&oto=OBAREY&key="+tip+params );
        dt_request.kullanici_pc_parametreleri_ekle(true);
        dt_request.action();
        JSONArray dt_data = new JSONObject(dt_request.get_value()).getJSONObject("data").getJSONArray("dt_veri");

        JSONObject item;
        try {
            for( int j = 0; j < dt_data.length(); j++ ) {
                item = dt_data.getJSONObject(j);
                Sefer_Rapor_Data temp_data = new Sefer_Rapor_Data(
                        item.getString(tip),
                        item.getInt("PS"),
                        item.getInt("TS"),
                        item.getInt("BS"),
                        item.getInt("AS"),
                        0,
                        item.getInt("IS"),
                        item.getInt("YS"),
                        item.getDouble("IETTKM"),
                        item.getDouble("GKM"));
                cont.getChildren().add(new Rapor_Box(Rapor_Box.OTOBUS, item.getString(tip), temp_data));
            }
        } catch( JSONException e ){
            e.printStackTrace();
        }
        return null;
    }


    public VBox get_table(){
        return cont;
    }

}
