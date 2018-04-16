package sample;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;


/**
 * Created by Jeppe on 13.03.2017.
 */
public class Rapor_Stat_HBox extends HBox {



    public Rapor_Stat_HBox( String tip, String renk, String baslik, int val ){
        super();

        Label lbl_baslik = new Label(baslik);
        lbl_baslik.getStyleClass().add(renk);
        Label lbl_val = new Label( String.valueOf(val) );
        lbl_val.getStyleClass().add("fbold");
        lbl_baslik.getStyleClass().add("fbold");
        HBox.setMargin(lbl_baslik, new Insets(0, 15, 0, 0));
        if( tip.equals(Rapor_Stat_Box.BOX_BG_120x90) ) lbl_val.getStyleClass().add("fs25");
        lbl_val.getStyleClass().add(renk);
        this.setAlignment(Pos.CENTER);
        this.getStyleClass().add(tip);
        this.getChildren().addAll( lbl_baslik, lbl_val );

    }

    public Rapor_Stat_HBox( String tip, String renk, String baslik, double val ){
        super();

        Label lbl_baslik = new Label(baslik);
        lbl_baslik.getStyleClass().add(renk);
        Label lbl_val = new Label( String.valueOf( String.format("%.2f", val ) ) );
        lbl_val.getStyleClass().add("fbold");
        lbl_baslik.getStyleClass().add("fbold");
        HBox.setMargin(lbl_baslik, new Insets(0, 15, 0, 0));
        if( tip.equals(Rapor_Stat_Box.BOX_BG_120x90) ) lbl_val.getStyleClass().add("fs25");
        lbl_val.getStyleClass().add(renk);
        this.setAlignment(Pos.CENTER);
        this.getStyleClass().add(tip);
        this.getChildren().addAll( lbl_baslik, lbl_val );

    }

    public Rapor_Stat_HBox( String tip, String renk, String baslik, String val ){
        super();

        Label lbl_baslik = new Label(baslik);
        lbl_baslik.getStyleClass().add(renk);
        Label lbl_val = new Label( val );
        lbl_val.getStyleClass().add("fbold");
        lbl_baslik.getStyleClass().add("fbold");
        HBox.setMargin(lbl_baslik, new Insets(0, 15, 0, 0));
        if( tip.equals(Rapor_Stat_Box.BOX_BG_120x90) ) lbl_val.getStyleClass().add("fs25");
        lbl_val.getStyleClass().add(renk);
        this.setAlignment(Pos.CENTER);
        this.getStyleClass().add(tip);
        this.getChildren().addAll( lbl_baslik, lbl_val );

    }





}
