package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.json.JSONObject;

import java.net.URL;
import java.util.*;

/**
 * Created by Jeppe on 19.05.2017.
 */
public class Takip_Scene_Controller implements Initializable {

    @FXML private ImageView header_logo;
    @FXML private FlowPane coklu_tables_container;
    @FXML private AnchorPane main_container;
    /*@FXML
    private Button btn_header_alarmlar;*/
    @FXML private Button btn_header_secenekler;
    @FXML private Button btn_header_ayarlar;
    @FXML private Label lbl_canli_otobus_aktif;
    @FXML private Label lbl_canli_otobus_bekleyen;
    @FXML private Label lbl_canli_otobus_tamamlayan;
    @FXML private Label lbl_canli_otobus_iptal;
    @FXML private Label lbl_canli_otobus_yarim;
    @FXML private Label lbl_stats_toplam_sefer;
    @FXML private Label lbl_stats_iett_km;
    @FXML private Label lbl_stats_gitas_km;
    @FXML private Label lbl_stats_plaka_degisim;
    @FXML private Label lbl_stats_aktif_not;
    @FXML private Label lbl_stats_yeni_iys;
    @FXML private Label lbl_kullanici_eposta;
    @FXML private Label lbl_stats_zayi_sefer;
    @FXML private Button btn_filtre_a;
    @FXML private Button btn_filtre_b;
    @FXML private Button btn_filtre_c;
    @FXML private Button btn_filtre_st;
    @FXML private Button btn_filtre_sa;
    @FXML private Button btn_filtre_sb;
    @FXML private Button btn_filtre_si;
    @FXML private Button btn_filtre_sy;
    @FXML private Button btn_filtre_not;
    @FXML private Button btn_filtre_plaka;
    @FXML private Button btn_filtre_iys;
    @FXML private Button btn_filtre_sifirla;
    @FXML private Button btn_filtre_kapi;
    @FXML private Button btn_filtre_kaydet;
    @FXML private Button btn_filtre_zayi;
    @FXML private VBox filtre_container;
    @FXML private HBox filtre_btn_container;
    @FXML private HBox header_pie_chart_container;

    private PieChart header_pie_chart;
    private ObservableList<PieChart.Data> pie_data;

    private Otobus_Box_Filtre otobus_box_filtre;
    private Alarm_Popup alarm_popup;
    private Ayarlar_Popup ayarlar_popup;
    private ArrayList<Filtre_Listener> filtre_listeners = new ArrayList<>();
    private ArrayList<Ayarlar_Listener> ayarlar_listeners = new ArrayList<>();

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

        //HBox.setMargin(btn_header_alarmlar, new Insets( 0, 5, 0, 5));
        HBox.setMargin(btn_header_ayarlar, new Insets( 0, 5, 0, 5));
        HBox.setMargin(btn_header_secenekler, new Insets( 0, 5, 0, 5));
        btn_header_secenekler.setOnMousePressed(event -> {
            Thread secenekler_popup_th = new Thread( new Task<String>(){
                private Secenekler_Popup secenekler_popup;
                @Override
                protected void succeeded(){
                    secenekler_popup.show( event.getScreenX(), event.getScreenY());
                }
                @Override
                protected String call(){
                    secenekler_popup = new Secenekler_Popup();
                    secenekler_popup.init(get_root());
                    return null;
                }
            });
            secenekler_popup_th.setDaemon(true);
            secenekler_popup_th.start();
        });
        lbl_kullanici_eposta.setText( User_Config.eposta_veri_al() );
    }

    public void init_piechart(){
        pie_data = FXCollections.observableArrayList(
                new PieChart.Data("Tamam", 0),
                new PieChart.Data("Bekleyen", 0),
                new PieChart.Data("Aktif", 0),
                new PieChart.Data("İptal", 0),
                new PieChart.Data("Yarım", 0)
        );
        header_pie_chart = new PieChart( pie_data );
        header_pie_chart.setLegendVisible(false);
        header_pie_chart.setMinWidth(300);
        header_pie_chart_container.getChildren().add( header_pie_chart );
    }

    public void init_filtre(){
        otobus_box_filtre = new Otobus_Box_Filtre(
                btn_filtre_a,
                btn_filtre_b,
                btn_filtre_c,
                btn_filtre_st,
                btn_filtre_sb,
                btn_filtre_sa,
                btn_filtre_si,
                btn_filtre_sy,
                btn_filtre_not,
                btn_filtre_plaka,
                btn_filtre_iys,
                btn_filtre_sifirla,
                btn_filtre_kapi,
                btn_filtre_kaydet,
                btn_filtre_zayi
        );
        otobus_box_filtre.add_listener(new Filtre_Listener() {
            @Override
            public void on_filtre_set( String kapi, Otobus_Box_Filtre_Data data) {
                for( Filtre_Listener listener : filtre_listeners ) listener.on_filtre_set( kapi, data );
            }
        });
    }

    public void ayarlar_init(){
        ayarlar_popup = new Ayarlar_Popup();
        ayarlar_popup.init( get_root());
        ayarlar_popup.add_listener(new Ayarlar_Listener() {
            @Override
            public void alarm_ayarlar_degisim( String yeni_str ) {
                for( Ayarlar_Listener listener : ayarlar_listeners ) listener.alarm_ayarlar_degisim( yeni_str );
            }
            @Override
            public void frekans_ayarlar_degisim(){
                for( Ayarlar_Listener listener : ayarlar_listeners ) listener.frekans_ayarlar_degisim();
            }
        });

        btn_header_ayarlar.setOnMousePressed( ev -> {
            if( !ayarlar_popup.is_on() ){
                ayarlar_popup.init( get_root()); // ?
                ayarlar_popup.show( ev.getScreenX(), ev.getScreenY() );
            }
        });
    }

    private void pie_chart_filo_renkler( ObservableList<PieChart.Data> chart_data, String... renkler) {
        int i = 0;
        for (PieChart.Data data : chart_data) {
            data.getNode().setStyle("-fx-pie-color: " + renkler[i % renkler.length] + ";");
            i++;
        }
    }

    public void pie_data_guncelle(String name, double value) {
        for(PieChart.Data d : pie_data) {
            if(d.getName().equals(name)) {
                d.setPieValue(value);
                return;
            }
        }
        pie_data.add(new PieChart.Data(name, value));
    }

    public void header_ozet_guncelle( Sefer_Rapor_Data data, Sefer_Rapor_Data canli_data, Filo_Rapor_Data filo_data ){
        lbl_stats_toplam_sefer.setText(String.valueOf(data.get_toplam()));
        lbl_stats_iett_km.setText(String.valueOf(String.format("%.2f",data.get_iett_km())));
        lbl_stats_gitas_km.setText(String.valueOf(String.format("%.2f",data.get_gitas_km())));
        lbl_stats_plaka_degisim.setText(String.valueOf(filo_data.get_plaka()));
        lbl_stats_aktif_not.setText( String.valueOf(filo_data.get_not()));
        lbl_stats_yeni_iys.setText( String.valueOf(filo_data.get_iys()));
        lbl_stats_zayi_sefer.setText(String.valueOf(filo_data.get_zayi()));

        lbl_canli_otobus_aktif.setText(String.valueOf(canli_data.get_aktif()));
        lbl_canli_otobus_bekleyen.setText(String.valueOf(canli_data.get_bekleyen()));
        lbl_canli_otobus_tamamlayan.setText(String.valueOf(canli_data.get_tamam()));
        lbl_canli_otobus_iptal.setText(String.valueOf(canli_data.get_iptal()));
        lbl_canli_otobus_yarim.setText(String.valueOf(canli_data.get_yarim()));

        pie_data_guncelle( "Tamam",data.get_tamam() );
        pie_data_guncelle( "Bekleyen",data.get_bekleyen() );
        pie_data_guncelle( "Aktif",data.get_aktif() );
        pie_data_guncelle( "İptal",data.get_iptal() );
        pie_data_guncelle( "Yarım",data.get_yarim() );
    }

    public void enable_filtre_btn_container(){
        filtre_btn_container.setDisable( false );
    }

    public void screen_resize_cb(){
        header_logo.getScene().widthProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                // direk .prefWidth ile yapinca onclick lerde css de tanimli cozunurluge geciyordu ??
                coklu_tables_container.setStyle("-fx-pref-width:" + newSceneWidth.doubleValue() );
                filtre_container.setStyle("-fx-min-width:" + (newSceneWidth.doubleValue() - 200) );
            }
        });
    }

    public void add_filtre_listener( Filtre_Listener listener ){
        filtre_listeners.add(listener);
    }
    public void add_ayarlar_listener( Ayarlar_Listener listener ){
        ayarlar_listeners.add(listener);
    }



    public void alarmlari_guncelle( String kod, ArrayList<Alarm_Data> data ){
        alarm_popup.alarm_ekle( kod, data );
    }

    public void alarm_popup_init(){
        alarm_popup = new Alarm_Popup( get_root() );
        alarm_popup.init();
    }

    public Node get_root(){
        return coklu_tables_container.getScene().getRoot();
    }

    public void add_otobus_box( VBox otobus_box ){
        coklu_tables_container.getChildren().add( otobus_box );
        otobus_kutular_sort();
    }
    public void remove_otobus_box( String kod ) {
        try{
            for( int j = 0; j < coklu_tables_container.getChildren().size(); j++ ){
                if( coklu_tables_container.getChildren().get(j).getId().equals(kod) ) coklu_tables_container.getChildren().remove(j);
            }
            otobus_kutular_sort();
        } catch( IndexOutOfBoundsException e ){
            e.printStackTrace();
        }

    }
    public Otobus_Box_Filtre get_filtre_obj(){
        return otobus_box_filtre;
    }

    public void otobus_kutular_sort(){
        // her filtre hareketinden sonra kutulari kapi kodlarina gore diziyoruz
        try {
            ObservableList<Node> obs_array = FXCollections.observableArrayList( coklu_tables_container.getChildren() );
            Collections.sort(obs_array, new Comparator<Node>(){
                @Override
                public int compare( Node vb1, Node vb2 ){
                    return vb1.getId().compareTo(vb2.getId());
                }
            });
            coklu_tables_container.getChildren().setAll(obs_array);
        } catch( IndexOutOfBoundsException e ){
            e.printStackTrace();
        }

    }

}
