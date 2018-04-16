package sample;

import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Jeppe on 18.04.2017.
 */
public class Popup_Not_Goruntule extends ScrollPane {
    private String gid, alarm_bitis_str;
    private ArrayList<Refresh_Listener> listeners = new ArrayList<>();
    private VBox bildirimler, main;
    private JSONObject data;
    public Popup_Not_Goruntule( JSONObject not ){
        super();
        this.data = not;

        getStyleClass().addAll("obarey-box-v2", "popup");
        main = new VBox();
        main.getStyleClass().add("obarey-box-v2");
        main.setSpacing(15);
        main.setAlignment(Pos.CENTER);

        init();

    }

    public void add_listener( Refresh_Listener listener ){
        listeners.add( listener );
    }

    private void init(){
        main.getChildren().clear();

        final HBox not_header = new HBox();
        not_header.setSpacing(10);
        not_header.setAlignment(Pos.CENTER);
        String not_tip_str;
        Button not_ico;
        if( data.getInt("tip") == Otobus_Not.KAZA ){
            not_ico = new Button("", new ImageView(new Image(getClass().getResourceAsStream("resources/img/ico_obareybox_kaza.png"))));
            not_tip_str = "Kaza Bildirimi";
        } else if(data.getInt("tip") == Otobus_Not.SERVIS ){
            not_ico = new Button("", new ImageView(new Image(getClass().getResourceAsStream("resources/img/ico_obareybox_servis.png"))));
            not_tip_str = "Servis Bildirimi";
        } else {
            // uyari
            not_ico = new Button("", new ImageView(new Image(getClass().getResourceAsStream("resources/img/ico_obareybox_uyari.png"))));
            not_tip_str = "Uyarı";
        }
        not_ico.getStyleClass().addAll("not-ekle-ico-bg", "grigrad");

        alarm_bitis_str = "Planlanan Bitiş: YOK";
        if( data.getInt("alarm") == 1 ){
            alarm_bitis_str = "Planlanan Bitiş: " + Common.rev_date(data.getString("alarm_bitis"));
        }

        String durum_str;
        if( data.getInt("durum") == 1 ){
            durum_str = "AKTİF";
        } else {
            durum_str = "TAMAM";
        }

        VBox not_detay = new VBox();
        not_detay.setSpacing(5);
        not_detay.setAlignment(Pos.CENTER_LEFT);

        Label not_detay_1_lbl = new Label( not_tip_str + " ( " + data.getString("yazan") + " ) ( " + data.getString("plaka") + " ) " );
        final Label not_detay_2_lbl = new Label( Common.rev_datetime(data.getString("baslangic")) + " - " + durum_str  );
        not_detay_1_lbl.getStyleClass().addAll("fbold", "fs11");
        not_detay_2_lbl.getStyleClass().addAll("fbold", "fs11");
        Label not_planlanan_bitis_lbl = new Label( alarm_bitis_str );
        not_planlanan_bitis_lbl.getStyleClass().addAll("fbold", "fs11");
        not_detay.getChildren().addAll( not_detay_1_lbl, not_detay_2_lbl, not_planlanan_bitis_lbl );

        not_header.getChildren().addAll( not_ico, not_detay );
        if( data.getInt("durum") == 1 ){
            final GButton tamamla_btn = new GButton("TAMAMLA", GButton.CMORK );
            not_header.getChildren().add( tamamla_btn );
            tamamla_btn.setOnMousePressed( ev -> {
                tamamla_btn.setDisable(true);
                Thread thread = new Thread( new Task<Void>(){
                    private int durum = 0;
                    private JSONObject guncel_not;
                    @Override
                    protected void succeeded(){
                        if( durum == 1 ){
                            data = guncel_not;
                            init();
                            for( Refresh_Listener ref_listener : listeners ) ref_listener.on_refresh();

                        }
                    }
                    @Override
                    protected Void call(){
                        Web_Request dt_request = new Web_Request(Web_Request.SERVIS_URL, "&req=not_tamamla&not_gid="+data.getString("gid"));
                        dt_request.kullanici_pc_parametreleri_ekle();
                        dt_request.action();
                        JSONObject output =   new JSONObject(dt_request.get_value());
                        durum = output.getInt("ok");
                        guncel_not = output.getJSONObject("data").getJSONObject("guncel_not");
                        return null;
                    }
                });
                thread.setDaemon(true);
                thread.start();
            });
        } else {

            Label bitis_lbl = new Label( "Bitiş: " + Common.rev_datetime(data.getString("bitis")) );
            Label tamamlayan_lbl = new Label( "Tamamlayan: " +  data.getString("tamamlayan") );
            bitis_lbl.getStyleClass().addAll("fbold", "fs11");
            tamamlayan_lbl.getStyleClass().addAll("fbold", "fs11");
            VBox bitis_cont = new VBox( bitis_lbl, tamamlayan_lbl );
            bitis_cont.setSpacing(5);
            bitis_cont.setAlignment(Pos.CENTER_RIGHT);
            not_header.getChildren().add( bitis_cont  );

        }
        Label not_icerik = new Label( data.getString("icerik") );
        not_icerik.getStyleClass().addAll("gri-box", "flight");
        not_icerik.setMinWidth(500);

        GButton excel_btn = new GButton("EXCEL ÇIKTI", GButton.CMORK );

        /*excel_btn.setOnMousePressed( ev -> {
            // TODO OBAAAAAAAAAAREY
        });*/

        VBox bildirim_input = new VBox();
        Label lbl_not = new Label("Yeni Bildirim Ekle");
        lbl_not.getStyleClass().addAll( "fs11", "cbeyaz", "fbold");
        final TextArea not_input = new TextArea();
        not_input.getStyleClass().addAll("grigrad", "uzun");
        bildirim_input.setAlignment(Pos.CENTER);
        bildirim_input.getChildren().addAll( lbl_not, not_input );

        final GButton bildirim_ekle_btn = new GButton("BİLDİRİM EKLE", GButton.CMORK );

        bildirim_ekle_btn.setOnMousePressed( ev -> {
            bildirim_ekle_btn.setDisable(true);
            String bildirim_val = not_input.getText();
            if( !bildirim_val.equals("") ){

                Thread thread = new Thread( new Task<Void>(){
                    private int durum = 0;
                    private JSONObject guncel_not;
                    @Override
                    protected void succeeded(){
                        if( durum == 1 ){
                            data = guncel_not;
                            init();
                            for( Refresh_Listener ref_listener : listeners ) ref_listener.on_refresh();
                        }
                    }
                    @Override
                    protected Void call(){
                        Web_Request dt_request = new Web_Request(Web_Request.SERVIS_URL, "&req=not_bildirim_ekle&not_gid="+data.getString("gid")+"&icerik="+bildirim_val);
                        dt_request.kullanici_pc_parametreleri_ekle();
                        dt_request.action();
                        JSONObject output =   new JSONObject(dt_request.get_value());
                        durum = output.getInt("ok");
                        guncel_not = output.getJSONObject("data").getJSONObject("guncel_not");
                        return null;
                    }
                });
                thread.setDaemon(true);
                thread.start();

            } else {
                bildirim_ekle_btn.setDisable(false);
            }

        });

        bildirimler = new VBox();
        bildirimler.setAlignment(Pos.CENTER);
        bildirimler.setSpacing(10);

        JSONArray bildirimler_data = data.getJSONArray("bildirimler");
        System.out.println(bildirimler_data);
        JSONObject bildirim_item;
        for( int k = 0; k < bildirimler_data.length(); k++ ){
            bildirim_item = bildirimler_data.getJSONObject(k);
            bildirimler.getChildren().add( bildirim_item( new Otobus_Not_Bildirim( bildirim_item.getString("gid"), bildirim_item.getString("not_gid"), bildirim_item.getString("icerik"), bildirim_item.getString("yazan"), bildirim_item.getString("tarih")) ) );
        }


        main.getChildren().addAll( not_header, not_icerik,/* excel_btn, */bildirim_input, bildirim_ekle_btn, bildirimler );
        setContent( main );
    }

    private VBox bildirim_item( Otobus_Not_Bildirim bildirim ){
        System.out.println(bildirim.get_icerik());
        VBox bildirim_cont;
        Label bildirim_info, bildirim_icerik;

        bildirim_cont = new VBox();
        bildirim_cont.setSpacing(5);
        bildirim_info = new Label(  bildirim.get_yazan() + " ( "+Common.rev_datetime(bildirim.get_tarih() )+" )");
        bildirim_info.getStyleClass().addAll("fbold");
        bildirim_icerik = new Label( bildirim.get_icerik());
        bildirim_icerik.getStyleClass().addAll("gri-box", "flight");
        bildirim_icerik.setMinWidth(500);
        bildirim_cont.getChildren().addAll( bildirim_info, bildirim_icerik );

        return bildirim_cont;
    }

}
