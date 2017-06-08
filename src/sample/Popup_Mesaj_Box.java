package sample;

import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by Jeppe on 18.04.2017.
 */
public class Popup_Mesaj_Box extends ScrollPane {

    private String oto;
    private ArrayList<Filo_Mesaj_Data> mesajlar = new ArrayList<>();
    private Label notf_lbl;
    private VBox mesaj_ul;

    public Popup_Mesaj_Box( String oto ){
        super();
        this.oto = oto;
        this.setMaxHeight(500);
        this.setMaxWidth(780);
        this.setHbarPolicy( ScrollPane.ScrollBarPolicy.NEVER );
        this.setVbarPolicy( ScrollPane.ScrollBarPolicy.NEVER );
    }

    public void init(){
        VBox main = new VBox();
        main.getStyleClass().add("obarey-box-v2");

        main.setSpacing(10);
        main.setAlignment(Pos.CENTER);

        HBox dp_container = new HBox();
        DatePicker dp = new DatePicker();
        dp.getStyleClass().add("grigrad");
        GButton getir_btn = new GButton("Getir", GButton.CMORK);
        notf_lbl = new Label("Veri bulunamadı!");

        dp_container.setAlignment(Pos.CENTER);
        dp_container.setSpacing(5);
        dp_container.getChildren().addAll( dp, getir_btn, notf_lbl );

        mesaj_ul = new VBox();
        mesaj_ul.setAlignment(Pos.CENTER);
        mesaj_ul.setSpacing(10);

        main.getChildren().addAll( dp_container, mesaj_ul );
        setContent(main);

        //update_data( "AKTIF");
        update_ui();

        getir_btn.setOnMousePressed(ev -> {
            System.out.println(dp.getValue());
            if( dp.getValue() == null ) return;
            notf_lbl.setText("Veri alınıyor...");
            Thread mesaj_th = new Thread( new Task<String>(){
                @Override
                protected void succeeded(){
                    try {
                        update_ui();
                    } catch (Exception e ){
                        e.printStackTrace();
                    }
                }
                @Override
                protected String call(){
                    //update_data( dp.getValue().toString() );
                    return null;
                }
            });
            mesaj_th.setDaemon(true);
            mesaj_th.start();
        });

    }

    public void update_ui(){
        if( mesajlar.size() == 0 ) {
            notf_lbl.setText("Veri yok.");
        }
        mesaj_ul.getChildren().clear();
        try {
            ImageView ico;
            Label mesaj, tarih;
            AnchorPane mesaj_li;
            //Tooltip kaynak_tt;
            for( Filo_Mesaj_Data data: mesajlar ){
               mesaj_li = new AnchorPane();
               mesaj_li.getStyleClass().add("li");
               ico = new ImageView( new Image(getClass().getResourceAsStream("resources/img/ico_obareybox_mesaj.png")));
               mesaj = new Label(data.get_mesaj());
               mesaj.getStyleClass().addAll("fs12", "flight");
               mesaj.setMaxWidth(400);
               mesaj.setWrapText(true);
               tarih = new Label(data.get_tarih() + " ( "+data.get_kaynak()+" )");

               tarih.setTooltip( new Tooltip(data.get_kaynak()) );
               tarih.getStyleClass().addAll("fs10", "flight");
               mesaj_li.getChildren().addAll( ico, mesaj, tarih );
               AnchorPane.setLeftAnchor(ico, 5.0);
               AnchorPane.setTopAnchor(ico, 5.0);
               AnchorPane.setBottomAnchor(ico, 5.0);
               AnchorPane.setLeftAnchor(mesaj, 50.0);
               AnchorPane.setTopAnchor(mesaj, 5.0);
               AnchorPane.setBottomAnchor(mesaj, 5.0);
               AnchorPane.setRightAnchor(tarih, 5.0);
               AnchorPane.setTopAnchor(tarih, 5.0);
               AnchorPane.setBottomAnchor(tarih, 5.0);
               mesaj_ul.getChildren().addAll( mesaj_li );
            }

            notf_lbl.setText("");
            System.out.println(mesajlar);
            mesajlar.clear();
        } catch( Exception e ){
            e.printStackTrace();
        }
    }

    /*public void update_data( String tarih ){
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        String db_tarih;
        if( tarih.equals("AKTIF") ){
            Filo_Senkronizasyon.aktif_gun_hesapla();
            db_tarih = Filo_Senkronizasyon.get_aktif_gun();
            if( db_tarih.equals("BEKLEMEDE") ) db_tarih = Common.get_yesterday_date();
        } else {
            db_tarih = tarih;
        }

        try {
            con = DBC.getInstance().getConnection();
            st = con.createStatement();
            res = st.executeQuery("SELECT * FROM " + GitasDBT.FILO_MESAJLAR + " WHERE ( oto = '"+oto+"' && tarih = '"+db_tarih+"' ) ORDER BY saat DESC");
            while( res.next() ){
                mesajlar.add( new Filo_Mesaj_Data( res.getString("kaynak"), res.getString("saat"),  res.getString("mesaj") ) );
            }
        } catch( SQLException e ){ e.printStackTrace(); }

        try {
            if( res != null ) res.close();
            if( st != null ) st.close();
            if( con != null ) con.close();
        } catch( SQLException e ) { e.printStackTrace(); }
    }*/

}
