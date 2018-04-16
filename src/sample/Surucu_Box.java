package sample;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.util.Map;

/**
 * Created by Obarey on 17.02.2017.
 */
public class Surucu_Box extends VBox {

    private String oto;

    public Surucu_Box( String _oto ){
        super();
        oto = _oto;
    }

    public void init(  Map<String, Surucu> suruculer ){
        VBox surucu_li;
        this.getStylesheets().addAll( Filo_Table.class.getResource("resources/css/common.css").toExternalForm() );
        this.getChildren().clear();
        for (Map.Entry<String, Surucu> entry : suruculer.entrySet()) {
            String calisma_suresi_text = "";
            surucu_li = new VBox();
            surucu_li.getStyleClass().add("popup-surucu-container");
            surucu_li.setAlignment(Pos.CENTER);
            Label lbl_surucu_isim = new Label(entry.getValue().get_isim());
            Label lbl_surucu_sicil = new Label( "Sicil No: " + entry.getValue().get_sicil_no() );
            Label lbl_surucu_tel = new Label( "Telefon: " + entry.getValue().get_telefon() );
            try {
                calisma_suresi_text = String.valueOf(Sefer_Sure.hesapla_uzun( entry.getValue().get_orer(), entry.getValue().get_bitis() ) / 60 ) + " saattir çalışıyor.";
            } catch( NullPointerException e ){
                //e.printStackTrace();
            }
            lbl_surucu_isim.setAlignment(Pos.CENTER);
            lbl_surucu_sicil.setAlignment(Pos.CENTER);
            lbl_surucu_tel.setAlignment(Pos.CENTER);
            lbl_surucu_isim.getStyleClass().add("popup-surucu-isim");
            lbl_surucu_sicil.getStyleClass().add("popup-surucu-info");
            lbl_surucu_tel.getStyleClass().add("popup-surucu-info");
            Label lbl_surucu_calisma_saati = new Label( calisma_suresi_text );
            lbl_surucu_calisma_saati.getStyleClass().add("popup-surucu-info");
            surucu_li.getChildren().addAll( lbl_surucu_isim, lbl_surucu_tel, lbl_surucu_sicil, lbl_surucu_calisma_saati );
            this.getChildren().add( surucu_li );
        }
    }

}
