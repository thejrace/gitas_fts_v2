package sample;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.management.PlatformLoggingMXBean;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Text;

/**
 * Created by Obarey on 30.01.2017.
 */
public class Filo_Table {

    private VBox table;
    private HBox thead, versiyon_cont;
    private VBox height_cont, ust_cont;
    private HBox dp_container;
    private ArrayList<HBox> rows = new ArrayList<>();
    private String kod;
    private boolean eski_veri_goruntuleniyor = false;
    private JSONArray versiyonlar;
    private boolean versiyon_birden_fazla;
    private int versiyon = 0;
    private boolean dp_gizle = false;

    // eski veriler incelendikten sonra aktif gune dondugu zaman db den almiyoruz veriyi
    // cunku 1 saat kadar gecmis olabilir db senkrona gore
    // popup acildiginda aldigimiz en son veriyi tutuyoruz
    private JSONArray son_aktif_data = new JSONArray();


    public Filo_Table( String kod ){
        this.kod = kod;
    }

    public void update_ui(){
        try {
            height_cont.getChildren().clear();
            versiyon_cont.getChildren().clear();
            //height_cont.getChildren().addAll(dp_container, thead);
            for( int j = 0; j < rows.size(); j++ ) height_cont.getChildren().add( rows.get(j) );
            if( versiyon_birden_fazla ){
                for( int k = 1; k <= versiyonlar.length() ; k++ ){
                    final int k_ref = k;
                    GButton vb =  new GButton("VER."+k, GButton.CMORB );
                    vb.setPadding(new Insets(10, 0, 10, 0) );
                    vb.setOnMousePressed( ev -> {

                        Obarey_Popup sefer_popup = new Obarey_Popup(kod + " Sefer Planı VERSİYON " + k_ref , table.getScene().getRoot());
                        sefer_popup.init(true);

                        Filo_Table sefer_plan_table = new Filo_Table( kod );
                        sefer_plan_table.dp_gizle();
                        sefer_plan_table.init();
                        sefer_plan_table.versiyon_sec( k_ref );
                        sefer_plan_table.update_data( son_aktif_data, new JSONArray() );
                        sefer_plan_table.update_ui();
                        sefer_popup.set_content( sefer_plan_table.get() );
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                sefer_popup.show( ev.getScreenX(), ev.getScreenY() );
                            }
                        });
                    });
                    versiyon_cont.getChildren().add(vb);

                }
            }
        } catch( Exception e ){
            e.printStackTrace();
        }
    }

    public void versiyon_sec( int ver ){
        versiyon = ver;
    }
    public void dp_gizle(){
        dp_gizle = true;
    }
    public boolean get_eski_veri_goruntuleniyor(){
        return eski_veri_goruntuleniyor;
    }
    public void update_data( JSONArray oto, JSONArray versiyon_data  ){

        // db den degil direk orer requestten gelen taze veriyi tutuyoruz
        son_aktif_data = oto;

        JSONObject sefer, onceki_sefer = new JSONObject();
        rows.clear();
        versiyonlar = versiyon_data;
        versiyon_birden_fazla = false;
        if( versiyon_data.length() > 1 ){
            versiyon_birden_fazla = true;
        }


        for( int x = 0; x < oto.length(); x++ ){
            sefer = oto.getJSONObject(x);
            // en son versiyon harici olanlari gosterme
            if( versiyon_birden_fazla && sefer.getInt("versiyon") != versiyon_data.getInt(versiyon_data.length()-1) ) continue;
            // ayni versiyon olan orerleri listelicez kontrol
            if( versiyon != 0 && sefer.getInt("versiyon") != versiyon ) continue;

            if( !oto.isNull(x-1) ) onceki_sefer = oto.getJSONObject(x-1);
            HBox sefer_row = new HBox();
            sefer_row.setPadding( new Insets( 2, 0 ,2, 0));
            switch( sefer.getString("durum") ){
                case "T":
                    sefer_row.getStyleClass().add("filo-table-tr-tamam");
                    break;
                case "B":
                    sefer_row.getStyleClass().add("filo-table-tr-bekleyen");
                    break;
                case "A":
                    sefer_row.getStyleClass().add("filo-table-tr-aktif");
                    break;
                case "I":
                    sefer_row.getStyleClass().add("filo-table-tr-iptal");
                    break;
                case "Y":
                    sefer_row.getStyleClass().add("filo-table-tr-yarim");
                    break;
            }
            String bekleme = "0";
            if( onceki_sefer.length() > 0 ) bekleme = String.valueOf(Sefer_Sure.hesapla( onceki_sefer.getString("bitis"), sefer.getString("gidis") ) )+ " DK";

            sefer_row.getChildren().addAll(
                    new Filo_Table_Sefer_Label( sefer.getString("no"), Filo_Table_Sefer_Label.WTD_37 ).get(),
                    new Filo_Table_Sefer_Label( sefer.getString("guzergah"), Filo_Table_Sefer_Label.WTD_80 ).get(),
                    new Filo_Table_Sefer_Label( sefer.getString("surucu"), Filo_Table_Sefer_Label.WTD_150 ).get(),
                    new Filo_Table_Sefer_Label( sefer.getString("gelis"), Filo_Table_Sefer_Label.WTD_60 ).get(),
                    new Filo_Table_Sefer_Label( sefer.getString("orer"), Filo_Table_Sefer_Label.WTD_60 ).get(),
                    new Filo_Table_Sefer_Label( bekleme, Filo_Table_Sefer_Label.WTD_60 ).get(),
                    new Filo_Table_Sefer_Label( sefer.getString("amir"), Filo_Table_Sefer_Label.WTD_60 ).get(),
                    new Filo_Table_Sefer_Label( sefer.getString("gidis"), Filo_Table_Sefer_Label.WTD_60 ).get(),
                    new Filo_Table_Sefer_Label( sefer.getString("tahmin"), Filo_Table_Sefer_Label.WTD_60 ).get(),
                    new Filo_Table_Sefer_Label( sefer.getString("bitis"), Filo_Table_Sefer_Label.WTD_60 ).get(),
                    new Filo_Table_Sefer_Label( sefer.getString("durum_kodu"), Filo_Table_Sefer_Label.WTD_37 ).get(),
                    new Filo_Table_Sefer_Label( String.valueOf(Sefer_Sure.hesapla( sefer.getString("gidis"), sefer.getString("bitis") ) )+ " DK", Filo_Table_Sefer_Label.WTD_60 ).get(),
                    new Filo_Table_Sefer_Label( String.valueOf(sefer.getInt("versiyon")), Filo_Table_Sefer_Label.WTD_37 ).get()
            );
            rows.add(sefer_row);
        }

    }

    public void init(){

        table = new VBox();
        table.getStyleClass().add( "filo-table-item" );
        //table.setId(kod);

        ust_cont = new VBox();
        versiyon_cont = new HBox();
        versiyon_cont.setSpacing(10);
        versiyon_cont.setAlignment(Pos.CENTER);

        table.getStylesheets().addAll( Filo_Table.class.getResource("resources/css/common.css").toExternalForm() );
        dp_container = new HBox();
        if( !dp_gizle ){
            dp_container.setAlignment(Pos.CENTER);
            dp_container.setPadding(new Insets(0, 0, 10, 0 ));
            DatePicker dp = new DatePicker();
            dp.setValue(Common.dp_placeholder(Common.get_current_date()));

            GButton dp_getir = new GButton("GETİR", GButton.CMORK );
            dp_container.getChildren().addAll( dp, dp_getir);
            ust_cont.getChildren().addAll( dp_container, versiyon_cont );
            HBox.setMargin( dp_getir, new Insets(0, 0 , 0, 20 ) );

            dp_getir.setOnMousePressed(ev -> {
                try {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String dp_val = dp.getValue().toString();
                            Web_Request request = new Web_Request(Web_Request.SERVIS_URL, "&req=orer_download&oto="+kod+"&baslangic="+dp_val+"&bitis=" );
                            request.kullanici_pc_parametreleri_ekle(true);
                            request.action();
                            JSONObject data = new JSONObject(request.get_value()).getJSONObject("data");
                            update_data( data.getJSONArray("orer_data"), data.getJSONArray("orer_versiyon_data") );
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    update_ui();
                                }
                            });

                        }
                    });
                    thread.setDaemon(true);
                    thread.start();
                } catch( NullPointerException e ){
                    e.printStackTrace();
                }
            });

        }


        height_cont = new VBox(); // row 2
        height_cont.getStyleClass().add( "filo-table-height-cont" );

        VBox thead_container = new VBox();

        thead = new HBox();
        thead.getChildren().addAll(
                new Filo_Table_Sefer_Thead_Label( "NO", Filo_Table_Sefer_Label.WTD_37 ).get(),
                new Filo_Table_Sefer_Thead_Label( "YÖN", Filo_Table_Sefer_Label.WTD_80 ).get(),
                new Filo_Table_Sefer_Thead_Label( "SÜRÜCÜ", Filo_Table_Sefer_Label.WTD_150 ).get(),
                new Filo_Table_Sefer_Thead_Label( "GELİŞ", Filo_Table_Sefer_Label.WTD_60 ).get(),
                new Filo_Table_Sefer_Thead_Label( "ORER", Filo_Table_Sefer_Label.WTD_60 ).get(),
                new Filo_Table_Sefer_Thead_Label( "BEKLEME", Filo_Table_Sefer_Label.WTD_60 ).get(),
                new Filo_Table_Sefer_Thead_Label( "AMİR", Filo_Table_Sefer_Label.WTD_60 ).get(),
                new Filo_Table_Sefer_Thead_Label( "GİDİŞ", Filo_Table_Sefer_Label.WTD_60 ).get(),
                new Filo_Table_Sefer_Thead_Label( "TAHMİN", Filo_Table_Sefer_Label.WTD_60 ).get(),
                new Filo_Table_Sefer_Thead_Label( "BİTİŞ", Filo_Table_Sefer_Label.WTD_60 ).get(),
                new Filo_Table_Sefer_Thead_Label( "DKODU", Filo_Table_Sefer_Label.WTD_37 ).get(),
                new Filo_Table_Sefer_Thead_Label( "SÜRE", Filo_Table_Sefer_Label.WTD_60 ).get(),
                new Filo_Table_Sefer_Thead_Label( "VER", Filo_Table_Sefer_Label.WTD_60 ).get()
        );
        thead_container.getChildren().addAll( ust_cont, thead );
        //height_cont.getChildren().add(thead);

        table.getChildren().addAll( thead_container, height_cont );


    }


    public VBox get(){
       return table;

    }

}
