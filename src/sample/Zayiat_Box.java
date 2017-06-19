package sample;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Created by Jeppe on 20.06.2017.
 */
public class Zayiat_Box extends HBox {

    public Zayiat_Box( String key, Sefer_Rapor_Data data ){
        super();

        VBox rapor_box_sol = new VBox();
        VBox rapor_box_sag = new VBox();

        rapor_box_sol.setAlignment(Pos.CENTER);
        rapor_box_sag.setAlignment(Pos.CENTER);
        rapor_box_sol.setSpacing(10);
        rapor_box_sag.setSpacing(10);

        rapor_box_sol.getStyleClass().add("rapor-box-sol");
        rapor_box_sag.getStyleClass().add("rapor-box-sag");


        Label box_info = new Label(key);
        box_info.getStyleClass().add("rapor-box-info");
        box_info.setWrapText(true);

        rapor_box_sol.getChildren().addAll( box_info );

        Rapor_Stat_VBox box_zayi_sefer = new Rapor_Stat_VBox( Rapor_Stat_Box.BOX_DEF, Rapor_Stat_Box.LBL_MAVI, "SEFER", data.get_zayi_sefer());
        Rapor_Stat_VBox box_gkm = new Rapor_Stat_VBox( Rapor_Stat_Box.BOX_DEF, Rapor_Stat_Box.LBL_TAMAM, "GKM", data.get_gitas_km());
        Rapor_Stat_VBox box_iett = new Rapor_Stat_VBox( Rapor_Stat_Box.BOX_DEF, Rapor_Stat_Box.LBL_BEKLEYEN, "IETT KM", data.get_iett_km());
        HBox box_stats_row_1 = new HBox(box_zayi_sefer, box_gkm, box_iett );
        box_stats_row_1.setAlignment(Pos.CENTER);
        box_stats_row_1.setSpacing(10);


        rapor_box_sag.getChildren().addAll(box_stats_row_1);

        this.getChildren().addAll(rapor_box_sol, rapor_box_sag );

    }

}
