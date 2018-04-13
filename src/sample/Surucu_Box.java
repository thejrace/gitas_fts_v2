package sample;


import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
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

        for (Map.Entry<String, Surucu> entry : suruculer.entrySet()) {

            surucu_li = new VBox();
            surucu_li.getStyleClass().add("popup-surucu-container");
            surucu_li.setAlignment(Pos.CENTER);

            Label lbl_surucu_isim = new Label(entry.getValue().get_isim());
            Label lbl_surucu_sicil = new Label( "Sicil No: " + entry.getValue().get_sicil_no() );
            Label lbl_surucu_tel = new Label( "Telefon: " + entry.getValue().get_telefon() );
            Label lbl_surucu_calisma_saati = new Label( String.valueOf(Sefer_Sure.hesapla_uzun( entry.getValue().get_orer(), entry.getValue().get_bitis() ) / 60 ) + " saattir çalışıyor." );
            lbl_surucu_isim.setAlignment(Pos.CENTER);
            lbl_surucu_sicil.setAlignment(Pos.CENTER);
            lbl_surucu_tel.setAlignment(Pos.CENTER);
            lbl_surucu_isim.getStyleClass().add("popup-surucu-isim");
            lbl_surucu_sicil.getStyleClass().add("popup-surucu-info");
            lbl_surucu_tel.getStyleClass().add("popup-surucu-info");
            lbl_surucu_calisma_saati.getStyleClass().add("popup-surucu-info");

            surucu_li.getChildren().addAll( lbl_surucu_isim, lbl_surucu_tel, lbl_surucu_sicil, lbl_surucu_calisma_saati );
            this.getChildren().add( surucu_li );

        }


        /*for (Map.Entry<String, String> entry : suruculer.entrySet()) {

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
        }*/
    }

}
