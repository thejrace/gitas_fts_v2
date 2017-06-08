package sample;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
/**
 * Created by Obarey on 30.01.2017.
 */
public class Filo_Table_Sefer_Label {

    private Label sefer_label;
    public static String WTD_80 = "filo-td-80";
    public static String WTD_60 = "filo-td-60";
    public static String WTD_37 = "filo-td-37";
    public static String WTD_150 = "filo-td-150";

    public Filo_Table_Sefer_Label( String str, String wclass ){

        sefer_label = new Label();
        sefer_label.setAlignment(Pos.CENTER);
        sefer_label.getStyleClass().add("filo-table-tbody-main");
        sefer_label.getStyleClass().add(wclass);

        if( str.equals("ORER") || str.equals("BİTİŞ") || str.equals("AMİR") ) sefer_label.getStyleClass().add("fbold");
        sefer_label.setText( str );

       /* DropShadow ds = new DropShadow();
        ds.setOffsetY(1.0f);
        ds.setColor(Color.color(0.4f, 0.4f, 0.4f));*/


    }

    public Label get(){
        return sefer_label;
    }
}
