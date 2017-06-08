package sample;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Created by Jeppe on 09.03.2017.
 */
public class GInputGrup extends VBox {

    public static int   TEXT = 1,
                        PASS = 2,
                        SELECT = 3;

    private GTextField textf;
    private GPasswordField passf;
    private GChoiceBox select;
    private int tip;


    public GInputGrup( String label, GTextField tf ){
        super();
        this.setSpacing(2.0);
        textf = tf;
        Label lbl = new Label(label);
        lbl.getStyleClass().add("form-label");
        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll( lbl, textf );
    }

    public GInputGrup( String label, GPasswordField pf ){
        super();
        this.setSpacing(2.0);
        passf = pf;
        Label lbl = new Label(label);
        lbl.getStyleClass().add("form-label");
        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll( lbl, passf );
    }

    public GInputGrup( String label, GChoiceBox cb ){
        super();
        this.setSpacing(2.0);
        select = cb;
        Label lbl = new Label(label);
        lbl.getStyleClass().add("form-label");
        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll( lbl, select );
    }

    public String get_input_val(){
        return textf.getText();
    }

    public String get_pass_val(){
        return passf.getText();
    }

    public String get_select_val(){
        return select.getValue().toString();
    }

}
