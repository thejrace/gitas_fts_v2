package sample;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
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
            String calisma_suresi_text = "", sefer_suresi_text = "";
            surucu_li = new VBox();
            surucu_li.getStyleClass().add("popup-surucu-container");
            surucu_li.setAlignment(Pos.CENTER);
            Label lbl_surucu_isim = new Label(entry.getValue().get_isim());
            Label lbl_surucu_sicil = new Label( "Sicil No: " + entry.getValue().get_sicil_no() );
            Label lbl_surucu_tel = new Label( "Telefon: " + entry.getValue().get_telefon() );
            NumberFormat formatter = new DecimalFormat("#0.00");
            double sefer_sure = 0, calisma_sure = 0;
            try {
                ArrayList<String> gidisler = entry.getValue().get_gidisler();
                calisma_sure = Sefer_Sure.hesapla_uzun( entry.getValue().get_orer(), entry.getValue().get_bitis() );
                calisma_suresi_text = String.valueOf(formatter.format(calisma_sure / 60)) + " saattir çalışıyor.";
                for( int k = 0; k < gidisler.size(); k++ ){
                    sefer_sure += Sefer_Sure.hesapla( gidisler.get(k), entry.getValue().get_bitisler().get(k) );
                }
                sefer_suresi_text = String.valueOf(formatter.format(sefer_sure / 60))+ " saat toplam sefer süresi.";
            } catch( NullPointerException | IndexOutOfBoundsException e ){
                //e.printStackTrace();
            }
            lbl_surucu_isim.setAlignment(Pos.CENTER);
            lbl_surucu_sicil.setAlignment(Pos.CENTER);
            lbl_surucu_tel.setAlignment(Pos.CENTER);
            lbl_surucu_isim.getStyleClass().add("popup-surucu-isim");
            lbl_surucu_sicil.getStyleClass().add("popup-surucu-info");
            lbl_surucu_tel.getStyleClass().add("popup-surucu-info");
            Label lbl_surucu_calisma_saati = new Label( calisma_suresi_text );
            Label lbl_surucu_sefer_suresi = new Label( sefer_suresi_text );
            lbl_surucu_calisma_saati.getStyleClass().addAll("popup-surucu-info", "cek");
            lbl_surucu_sefer_suresi.getStyleClass().addAll("popup-surucu-info", "cek");
            surucu_li.getChildren().addAll( lbl_surucu_isim, lbl_surucu_tel, lbl_surucu_sicil, lbl_surucu_calisma_saati, lbl_surucu_sefer_suresi );
            this.getChildren().add( surucu_li );
        }
    }

}
