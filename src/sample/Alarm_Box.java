package sample;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

/**
 * Created by Jeppe on 16.03.2017.
 */
public class Alarm_Box extends VBox {

    private int index;
    private int silinen_count = 0, li_count = 0, gorunen_count = 0;

    public Alarm_Box( int index, ArrayList<Alarm_Data> data, Alarm_Goruldu_Listener listener ){
        super();

        if( data.size() == 0 ) return;

        this.index = index;
        this.setAlignment(Pos.CENTER);
        this.setSpacing(10);

        String style_class = "";
        HBox alarm_li;
        VBox.setMargin(this, new Insets(5, 0, 0, 0));
        this.getStylesheets().add(Obarey_Popup.class.getResource("resources/css/common.css").toExternalForm());
        this.getStyleClass().add("alarm-box");
        for( Alarm_Data alarm : data ){

            if( alarm.get_goruldu() ){
                /*try {
                    HBox li = (HBox) this.lookup( "#" + alarm.get_key() );
                    li.setVisible(false);
                    li.getChildren().clear();
                } catch( NullPointerException e ){
                    e.printStackTrace();

                }*/

                continue;
            }
            gorunen_count++;
            alarm_li = new HBox();
            alarm_li.getStyleClass().add("alarm-li");
            alarm_li.setId( alarm.get_key() );
            if( alarm.get_oncelik() == Alarm_Data.KIRMIZI ){
                style_class = "kirmizi";
            } else if( alarm.get_oncelik() == Alarm_Data.TURUNCU ){
                style_class = "turuncu";
            } else if( alarm.get_oncelik() == Alarm_Data.MAVI ){
                style_class = "mavi";
            } else if( alarm.get_oncelik() == Alarm_Data.YESIL ){
                style_class = "yesil";
            } else {
                style_class = "kirmizi";
            }

            Label lbl_mesaj = new Label( alarm.get_kod() + " " + alarm.get_mesaj() + " [ "+alarm.get_tarih()+" ] ");
            lbl_mesaj.getStyleClass().addAll("fsemibold", "alarm-label", "alarm-label-" + style_class );
            ImageView img = new ImageView( new Image( getClass().getResource("resources/img/ico_alarm_"+style_class+".png").toExternalForm() ));
            alarm_li.getChildren().addAll( img, lbl_mesaj );

            this.getChildren().add( alarm_li );
            li_count++;

            alarm_li.setOnMousePressed( ev -> {
                silinen_count++;

                HBox li = (HBox) this.lookup( "#" + alarm.get_key() );
                //alarm.goruldu(true);
                listener.goruldu_yap( alarm.get_key() );
                System.out.println( alarm.get_key() + " --- ALARM GÖRÜLDÜ YAP! 111");
                li.setVisible(false);
                li.getChildren().clear();
                if( silinen_count == li_count  ){
                    this.setVisible(false);
                    this.getChildren().clear();
                }

            });



        }

    }

    public int alarm_li_sayisi(){
        return gorunen_count;
    }

}
