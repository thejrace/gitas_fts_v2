package sample;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Obarey on 17.02.2017.
 */
public class Surucu_Box {

    private String oto;
    private VBox surucu_ul, surucu_li;
    private Map<String, String> suruculer;

    public Surucu_Box( String _oto, Map<String, String> _suruculer ){
        oto = _oto;
        suruculer = _suruculer;
    }

    public VBox get_ui(){
        return surucu_ul;
    }

    public void init(){
        surucu_ul = new VBox();

        surucu_ul.getStylesheets().addAll( Filo_Table.class.getResource("resources/css/common.css").toExternalForm() );
        for (Map.Entry<String, String> entry : suruculer.entrySet()) {

            surucu_li = new VBox();
            surucu_li.getStyleClass().add("popup-surucu-container");
            surucu_li.setAlignment(Pos.CENTER);

            Label lbl_surucu_isim = new Label( entry.getValue() );
            final GButton surucu_tel_al = new GButton("Telefonu Al", GButton.CMORK );

            Label lbl_surucu_sicil = new Label( "Sicil No: " + entry.getKey() );
            lbl_surucu_isim.setAlignment(Pos.CENTER);
            lbl_surucu_sicil.setAlignment(Pos.CENTER);

            lbl_surucu_isim.getStyleClass().add("popup-surucu-isim");
            lbl_surucu_sicil.getStyleClass().add("popup-surucu-info");
            surucu_li.getChildren().addAll( lbl_surucu_isim, surucu_tel_al, lbl_surucu_sicil );
            surucu_ul.getChildren().add( surucu_li );

            surucu_tel_al.setOnMousePressed( ev -> {
                surucu_tel_al.setDisable(true);
                surucu_tel_al.setText("Veri indiriliyor...");
                Thread th = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Web_Request request = new Web_Request(Web_Request.SERVIS_URL, "&req=surucu_telefon_download&surucu_sicil_no="+entry.getKey() );
                        request.kullanici_pc_parametreleri_ekle();
                        request.action();
                        String data = new JSONObject(request.get_value()).getString("data");
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Label lbl_surucu_tel = new Label( "Telefon: " + data );
                                lbl_surucu_tel.setAlignment(Pos.CENTER);
                                lbl_surucu_tel.getStyleClass().add("popup-surucu-info");
                                surucu_li.getChildren().remove(1);
                                surucu_li.getChildren().add(1, lbl_surucu_tel );
                            }
                        });
                    }
                });
                th.setDaemon(true);
                th.start();
            });
        }
    }

}
