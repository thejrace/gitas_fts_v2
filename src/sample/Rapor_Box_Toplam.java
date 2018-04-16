package sample;

import javafx.geometry.Pos;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Created by Jeppe on 15.03.2017.
 */
public class Rapor_Box_Toplam extends HBox {

    private int tip;
    private String id;
    private Sefer_Rapor_Data data;

    public static int   TOPLAM_FILO = 1,
                        TOPLAM_OTOBUS = 2,
                        TOPLAM_HAT = 3,
                        TOPLAM_SURUCU = 4;

    public Rapor_Box_Toplam( int tip, String id, Sefer_Rapor_Data data ){
        super();
        this.tip = tip;
        this.id = id;
        this.data = data;

        VBox total_sol = new VBox();
        total_sol.setSpacing(10);
        total_sol.setAlignment(Pos.CENTER);
        this.setAlignment(Pos.CENTER);

        Rapor_Stat_VBox total_sef_yuzdesi = new Rapor_Stat_VBox( Rapor_Stat_Box.BOX_BG_120x90, Rapor_Stat_Box.LBL_BEKLEYEN, "SEFER YÜZDESİ",  data.get_sefer_yuzdesi());
        Rapor_Stat_HBox iett_km = new Rapor_Stat_HBox( Rapor_Stat_Box.BOX_BG_W120, Rapor_Stat_Box.LBL_IPTAL,"İETT",  data.get_iett_km() );
        Rapor_Stat_HBox gitas_km = new Rapor_Stat_HBox( Rapor_Stat_Box.BOX_BG_W120, Rapor_Stat_Box.LBL_MOR,"HAR", data.get_gitas_km() );

        total_sol.getChildren().addAll( total_sef_yuzdesi, iett_km, gitas_km );

        Rapor_Stat_VBox total_toplam = new Rapor_Stat_VBox( Rapor_Stat_Box.BOX_BG_96x50, Rapor_Stat_Box.LBL_MOR, "TOPLAM",  data.get_toplam());
        Rapor_Stat_VBox total_tamam = new Rapor_Stat_VBox( Rapor_Stat_Box.BOX_BG_96x50, Rapor_Stat_Box.LBL_TAMAM, "TAMAM",  data.get_tamam());
        Rapor_Stat_VBox total_bekleyen = new Rapor_Stat_VBox( Rapor_Stat_Box.BOX_BG_96x50, Rapor_Stat_Box.LBL_BEKLEYEN, "BEKLEYEN",  data.get_bekleyen());
        Rapor_Stat_VBox total_aktif = new Rapor_Stat_VBox( Rapor_Stat_Box.BOX_BG_96x50, Rapor_Stat_Box.LBL_AKTIF, "AKTİF",  data.get_aktif());
        Rapor_Stat_VBox total_ek = new Rapor_Stat_VBox( Rapor_Stat_Box.BOX_BG_96x50, Rapor_Stat_Box.LBL_EK, "EK",  data.get_ek() );
        Rapor_Stat_VBox total_iptal = new Rapor_Stat_VBox( Rapor_Stat_Box.BOX_BG_96x50, Rapor_Stat_Box.LBL_IPTAL, "İPTAL",  data.get_iptal() );
        Rapor_Stat_VBox total_yarim = new Rapor_Stat_VBox( Rapor_Stat_Box.BOX_BG_96x50, Rapor_Stat_Box.LBL_YARIM, "YARIM",  data.get_yarim() );

        FlowPane test_fp = new FlowPane();
        test_fp.setAlignment(Pos.CENTER);
        test_fp.setHgap(10);
        test_fp.setVgap(10);
        test_fp.getStyleClass().add("rapor-box-header");

        test_fp.getChildren().addAll( total_toplam, total_tamam, total_bekleyen, total_aktif, total_ek, total_iptal, total_yarim );


        this.getChildren().addAll( total_sol, test_fp );

    }


}
