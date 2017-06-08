package sample;

import javafx.scene.control.PasswordField;

/**
 * Created by Jeppe on 09.03.2017.
 */
public class GPasswordField extends PasswordField {

    public static int   UZUN = 1,
                        MID = 2,
                        KISA = 3,
                        NUM = 4;

    public GPasswordField( int tip ){
        super();

        if( tip == UZUN ){
            this.getStyleClass().add("input-uzun");
        } else if( tip == MID ){
            this.getStyleClass().add("input-mid");
        } else if( tip == KISA ){
            this.getStyleClass().add("input-kisa");
        } else if( tip == NUM ){
            this.getStyleClass().add("input-num");
        }

    }

}
