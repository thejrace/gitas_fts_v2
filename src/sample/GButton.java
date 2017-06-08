package sample;

import javafx.scene.control.Button;

/**
 * Created by Jeppe on 09.03.2017.
 */
public class GButton extends Button {

    public static int   CMORK = 1,
                        CGRIK = 2,
                        CMORB = 3,
                        CGRIB = 4;

    public GButton(String text, int tip ){
        super(text);
        if( tip == CMORK ){
            this.getStyleClass().add("btnmorkucuk");
        } else if( tip == CGRIK ){
            this.getStyleClass().add("btngrikucuk");
        } else if( tip == CMORB ){
            this.getStyleClass().add("btnmor");
        } else if( tip == CGRIB ){
            this.getStyleClass().add("btngri");
        }
    }

}
