package sample;

import javafx.scene.control.ChoiceBox;


/**
 * Created by Jeppe on 09.03.2017.
 */
public class GChoiceBox extends ChoiceBox<KeyValue_Pair> {

    public static int   UZUN = 1,
                        MID = 2,
                        KISA = 3;

    public GChoiceBox( int tip ){
        super();

        if( tip == UZUN ){
            this.getStyleClass().add("input-uzun");
        } else if( tip == MID ){
            this.getStyleClass().add("input-mid");
        } else if( tip == KISA ){
            this.getStyleClass().add("input-kisa");
        }

    }

}
