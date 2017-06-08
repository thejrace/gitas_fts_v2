package sample;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.*;

/**
 * Created by Jeppe on 18.04.2017.
 */
public class Popup_IYS_Box extends ScrollPane {

    private String oto;
    public Popup_IYS_Box( String oto ){
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
        VBox mesaj_ul = new VBox();
        mesaj_ul.setAlignment(Pos.CENTER);
        mesaj_ul.setSpacing(10);
        Connection con = null;
        Statement st = null;
        PreparedStatement pst = null;
        ResultSet res = null;
        /*try {
            con = DBC.getInstance().getConnection();
            st = con.createStatement();
            res = st.executeQuery("SELECT * FROM " + GitasDBT.IYS_KAYIT + " WHERE oto = '"+oto+"' ORDER BY id");
            if( Common.result_count(res) > 0 ){
                ImageView ico;
                Label mesaj, tarih;
                AnchorPane mesaj_li;
                while( res.next() ){
                    mesaj_li = new AnchorPane();
                    mesaj_li.getStyleClass().add("li");
                    ico = new ImageView( new Image(getClass().getResourceAsStream("resources/img/ico_obareybox_iys.png")));
                    mesaj = new Label(res.getString("ozet"));
                    mesaj.getStyleClass().addAll("fs12", "flight");
                    mesaj.setMaxWidth(400);
                    mesaj.setWrapText(true);
                    tarih = new Label(Common.rev_date(res.getString("tarih")) + " ( "+res.getString("kaynak").substring(0, 10 )+" )");
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

                    // goruldu yap arkadaslari
                    pst = con.prepareStatement("UPDATE " + GitasDBT.IYS_KAYIT + " SET goruldu = ? WHERE id = ?");
                    pst.setInt(1, 1);
                    pst.setInt(2, res.getInt("id") );
                    pst.executeUpdate();

                }
            }
            main.getChildren().addAll( mesaj_ul );
            setContent(main);
        } catch( SQLException e ){ e.printStackTrace(); }
        try {
            if( res != null ) res.close();
            if( st != null ) st.close();
            if( pst != null ) pst.close();
            if( con != null ) con.close();
        } catch( SQLException e ) { e.printStackTrace(); }
*/

    }

}
