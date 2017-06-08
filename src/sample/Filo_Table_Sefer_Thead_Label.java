package sample;

import javafx.geometry.Pos;
import javafx.scene.control.Label;

/**
 * Created by Obarey on 30.01.2017.
 */
public class Filo_Table_Sefer_Thead_Label {

    private Label sefer_label;

    public Filo_Table_Sefer_Thead_Label( String str, String wclass ){
        sefer_label = new Label();
        sefer_label.setAlignment(Pos.CENTER);
        sefer_label.getStyleClass().add("filo-table-thead");
        sefer_label.getStyleClass().add(wclass);
        sefer_label.setText( str );
    }

    public Label get(){
        return sefer_label;
    }
}
