package sample;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

/**
 * Created by Obarey on 16.02.2017.
 */
public class Filo_Oneri_Sefer_Table {

    private ArrayList<Oneri_Sefer_Data> data;
    private ScrollPane main_container;
    private VBox sefer_ul;

    public Filo_Oneri_Sefer_Table( ArrayList<Oneri_Sefer_Data> data ){
        this.data = data;
    }

    public ScrollPane get_ui(){
        return this.main_container;
    }

    public void init(){

        main_container = new ScrollPane();
        main_container.getStylesheets().add(Filo_Table.class.getResource("resources/css/common.css").toExternalForm());
        //main_container.setPadding(new Insets(0,10,10, 5) );
        main_container.setMaxHeight(550);
        main_container.setPrefWidth(380);

        main_container.setHbarPolicy( ScrollPane.ScrollBarPolicy.NEVER );
        sefer_ul = new VBox();

        sefer_ul.setMinWidth(800);
        sefer_ul.setMaxWidth(850);

        HBox main_thead = new HBox();
        Label lbl_th_oto = new Label("OTO");
        Label lbl_th_hg = new Label("HAT - GÜZ.");
        Label lbl_th_orer = new Label("ORER");
        Label lbl_th_dk = new Label("DK");

        lbl_th_oto.setAlignment(Pos.CENTER);
        lbl_th_hg.setAlignment(Pos.CENTER);
        lbl_th_orer.setAlignment(Pos.CENTER);
        lbl_th_dk.setAlignment(Pos.CENTER);
        lbl_th_oto.getStyleClass().addAll("filo-table-thead", "filo-td-78");
        lbl_th_hg.getStyleClass().addAll("filo-table-thead", "filo-td-150");
        lbl_th_orer.getStyleClass().addAll("filo-table-thead", "filo-td-78");
        lbl_th_dk.getStyleClass().addAll("filo-table-thead", "filo-td-78");

        main_thead.getChildren().addAll( lbl_th_oto, lbl_th_hg, lbl_th_orer, lbl_th_dk );
        // ilk li thead olucak
        sefer_ul.getChildren().add( main_thead );

        VBox sefer_tr; // sefer li
        HBox sefer_tr_data; // sefer li > sari hbox
        VBox sefer_tr_otolar; // sefer li > otolar vbox
        for( Oneri_Sefer_Data oneri_sefer : data ){

            sefer_tr = new VBox();
            sefer_tr.setAlignment(Pos.CENTER);

            sefer_tr_data = new HBox();
            sefer_tr_data.getStyleClass().add("filo-table-tr-eksefer");

            // sari sefer oneri tr
            Label lbl_tr_oto = new Label( oneri_sefer.get_oto() );
            Label lbl_tr_hg = new Label( oneri_sefer.get_guzergah() );
            Label lbl_tr_orer = new Label( oneri_sefer.get_orer() );
            Label lbl_tr_dk = new Label( oneri_sefer.get_dkodu() );
            lbl_tr_oto.setAlignment(Pos.CENTER);
            lbl_tr_hg.setAlignment(Pos.CENTER);
            lbl_tr_orer.setAlignment(Pos.CENTER);
            lbl_tr_dk.setAlignment(Pos.CENTER);


            lbl_tr_oto.getStyleClass().addAll("filo-table-tbody-oneri-main", "filo-td-78");
            lbl_tr_hg.getStyleClass().addAll("filo-table-tbody-oneri-main", "filo-td-150");
            lbl_tr_orer.getStyleClass().addAll("filo-table-tbody-oneri-main", "filo-td-78");
            lbl_tr_dk.getStyleClass().addAll("filo-table-tbody-oneri-main", "filo-td-78");
            sefer_tr_data.getChildren().addAll( lbl_tr_oto, lbl_tr_hg, lbl_tr_orer, lbl_tr_dk );

            // otolar
            sefer_tr_otolar = new VBox();
            HBox otolar_thead = new HBox();
            Label lbl_th_otolar_oto = new Label("OTO");
            Label lbl_th_otolar_hg = new Label("HAT - GÜZ.");
            Label lbl_th_otolar_sorer = new Label("SON ORER");
            lbl_th_otolar_oto.setAlignment(Pos.CENTER);
            lbl_th_otolar_hg.setAlignment(Pos.CENTER);
            lbl_th_otolar_sorer.setAlignment(Pos.CENTER);
            lbl_th_otolar_oto.getStyleClass().addAll("filo-table-thead", "filo-td-78");
            lbl_th_otolar_hg.getStyleClass().addAll("filo-table-thead", "filo-td-150");
            lbl_th_otolar_sorer.getStyleClass().addAll("filo-table-thead", "filo-td-78");

            otolar_thead.getChildren().addAll( lbl_th_otolar_oto, lbl_th_otolar_hg, lbl_th_otolar_sorer );
            // burada da ilk li thead
            sefer_tr_otolar.getChildren().add( otolar_thead );

            HBox sefer_oto_tr;
            for( Otobus_Data otobus : oneri_sefer.get_otolar() ){

                sefer_oto_tr = new HBox();

                Label lbl_oto_kod = new Label( otobus.get_oto() );
                Label lbl_oto_guz = new Label( otobus.get_guz() );
                Label lbl_oto_sorer = new Label( otobus.get_son_orer() );
                //Button btn_not_ekle = new Button("NOT AL");
                //btn_not_ekle.getStyleClass().addAll("filo-table-row-button", "filo-td-78");

                if( oneri_sefer.get_guzergah().equals( otobus.get_guz()) ){
                    lbl_oto_kod.getStyleClass().addAll("filo-table-tbody-oneri-alt-main", "filo-td-78",  "tbold");
                    lbl_oto_guz.getStyleClass().addAll("filo-table-tbody-oneri-alt-main", "filo-td-150",  "tbold");
                    lbl_oto_sorer.getStyleClass().addAll("filo-table-tbody-oneri-alt-main", "filo-td-78", "tbold");
                } else {
                    lbl_oto_kod.getStyleClass().addAll("filo-table-tbody-oneri-alt-main", "filo-td-78");
                    lbl_oto_guz.getStyleClass().addAll("filo-table-tbody-oneri-alt-main", "filo-td-150");
                    lbl_oto_sorer.getStyleClass().addAll("filo-table-tbody-oneri-alt-main", "filo-td-78");
                }


                lbl_oto_kod.setAlignment(Pos.CENTER);
                lbl_oto_guz.setAlignment(Pos.CENTER);
                lbl_oto_sorer.setAlignment(Pos.CENTER);


                sefer_oto_tr.getChildren().addAll( lbl_oto_kod, lbl_oto_guz, lbl_oto_sorer/*, btn_not_ekle*/ );
                sefer_tr_otolar.getChildren().add( sefer_oto_tr );
            }

            // sari ust - alt container
            sefer_tr.getChildren().addAll( sefer_tr_data, sefer_tr_otolar );
            sefer_ul.getChildren().add( sefer_tr );
        }
        main_container.setContent( sefer_ul );
    }

}
