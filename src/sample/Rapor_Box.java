package sample;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Created by Jeppe on 13.03.2017.
 */
public class Rapor_Box extends HBox {

    public static int   OTOBUS = 1,
                        HAT = 2;

    private int tip;
    private String id;
    private Sefer_Rapor_Data data;

    public Rapor_Box( int tip, String id, Sefer_Rapor_Data data ){
        super();
        this.tip = tip;
        this.id = id;
        this.data = data;

        VBox rapor_box_sol = new VBox();
        VBox rapor_box_sag = new VBox();

        rapor_box_sol.setAlignment(Pos.CENTER);
        rapor_box_sag.setAlignment(Pos.CENTER);
        rapor_box_sol.setSpacing(10);
        rapor_box_sag.setSpacing(10);

        rapor_box_sol.getStyleClass().add("rapor-box-sol");
        rapor_box_sag.getStyleClass().add("rapor-box-sag");


        Label box_info = new Label(id);
        box_info.getStyleClass().add("rapor-box-info");
        box_info.setWrapText(true);

        //GButton incele_btn = new GButton("İNCELE", GButton.CGRIK );
        //GButton suruculer_btn = new GButton("SÜRÜCÜLER", GButton.CGRIK );

        rapor_box_sol.getChildren().addAll( box_info/*, incele_btn, suruculer_btn*/ );

        Rapor_Stat_VBox box_toplam = new Rapor_Stat_VBox( Rapor_Stat_Box.BOX_DEF, Rapor_Stat_Box.LBL_MAVI, "TOPLAM", data.get_toplam());
        Rapor_Stat_VBox box_tamam = new Rapor_Stat_VBox( Rapor_Stat_Box.BOX_DEF, Rapor_Stat_Box.LBL_TAMAM, "TAMAM", data.get_tamam());
        Rapor_Stat_VBox box_bekleyen = new Rapor_Stat_VBox( Rapor_Stat_Box.BOX_DEF, Rapor_Stat_Box.LBL_BEKLEYEN, "BEKLEYEN", data.get_bekleyen());
        Rapor_Stat_VBox box_aktif = new Rapor_Stat_VBox( Rapor_Stat_Box.BOX_DEF, Rapor_Stat_Box.LBL_AKTIF, "AKTİF", data.get_aktif());
        Rapor_Stat_VBox box_ek = new Rapor_Stat_VBox( Rapor_Stat_Box.BOX_DEF, Rapor_Stat_Box.LBL_EK, "EK", data.get_ek());
        Rapor_Stat_VBox box_iptal = new Rapor_Stat_VBox( Rapor_Stat_Box.BOX_DEF, Rapor_Stat_Box.LBL_IPTAL, "İPTAL", data.get_iptal());
        Rapor_Stat_VBox box_yarim = new Rapor_Stat_VBox( Rapor_Stat_Box.BOX_DEF, Rapor_Stat_Box.LBL_YARIM, "YARIM", data.get_yarim());
        HBox box_stats_row_1 = new HBox(box_toplam, box_tamam, box_bekleyen, box_aktif, box_ek, box_iptal, box_yarim);
        box_stats_row_1.setAlignment(Pos.CENTER);
        box_stats_row_1.setSpacing(10);


        Rapor_Stat_VBox box_sy = new Rapor_Stat_VBox( Rapor_Stat_Box.BOX_DEF, Rapor_Stat_Box.LBL_TURKUAZ, "SEFER YÜZDESİ", data.get_sefer_yuzdesi());
        Rapor_Stat_VBox box_iettkm = new Rapor_Stat_VBox( Rapor_Stat_Box.BOX_DEF, Rapor_Stat_Box.LBL_TURUNCU, "İETT", data.get_iett_km());
        Rapor_Stat_VBox box_gitaskm = new Rapor_Stat_VBox( Rapor_Stat_Box.BOX_DEF, Rapor_Stat_Box.LBL_TURUNCU, "HAR", data.get_gitas_km());



        HBox box_stats_row_2 = new HBox(box_sy, box_iettkm, box_gitaskm);
        box_stats_row_2.setAlignment(Pos.CENTER);
        box_stats_row_2.setSpacing(10);

        rapor_box_sag.getChildren().addAll(box_stats_row_1, box_stats_row_2);

        this.getChildren().addAll(rapor_box_sol, rapor_box_sag );


    }






}
