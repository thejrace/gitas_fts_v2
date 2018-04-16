package sample;

import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Jeppe on 18.04.2017.
 */
public class Popup_Not_Ekle extends VBox {

    private String oto, plaka, tarih_val, alarm_params;
    private ArrayList<Refresh_Listener> listeners = new ArrayList<>();
    private int not_tip = 0, alarm = 0;
    private HBox tip_buton_cont;
    public Popup_Not_Ekle( String kod, String plaka ){
        super();
        this.oto = kod;
        this.plaka = plaka;
        setAlignment(Pos.CENTER);
        getStyleClass().add("obarey-box-v2");
        setSpacing(15);
        tip_buton_cont = new HBox();
        VBox konu_cont = new VBox();
        VBox tarih_cont = new VBox();
        VBox plaka_cont = new VBox();
        Button kaza_btn = new Button( "", new ImageView(new Image(getClass().getResourceAsStream("resources/img/ico_obareybox_kaza.png"))));
        Button uyari_btn = new Button("", new ImageView(new Image(getClass().getResourceAsStream("resources/img/ico_obareybox_uyari.png"))));
        Button servis_btn = new Button("", new ImageView(new Image(getClass().getResourceAsStream("resources/img/ico_obareybox_servis.png"))));
        kaza_btn.getStyleClass().addAll("not-ekle-ico-bg", "grigrad", "pasif");
        uyari_btn.getStyleClass().addAll("not-ekle-ico-bg", "grigrad", "pasif");
        servis_btn.getStyleClass().addAll("not-ekle-ico-bg", "grigrad", "pasif");
        tip_buton_cont.getChildren().addAll( kaza_btn, uyari_btn, servis_btn );
        tip_buton_cont.getStyleClass().add("not-ekle-ico-container");
        tip_buton_cont.setAlignment(Pos.CENTER);
        Label lbl_not = new Label("Not");
        lbl_not.getStyleClass().addAll( "fs11", "cbeyaz", "fbold");
        final TextArea not_input = new TextArea();
        not_input.getStyleClass().addAll("grigrad", "uzun");
        konu_cont.setAlignment(Pos.CENTER);
        konu_cont.getChildren().addAll( lbl_not, not_input );
        Label lbl_tarih = new Label("Tarih");
        lbl_tarih.getStyleClass().addAll( "fs11", "cbeyaz", "fbold");
        final DatePicker tarih_input = new DatePicker();
        tarih_input.getStyleClass().add("grigrad");
        tarih_cont.setAlignment(Pos.CENTER);
        tarih_cont.getChildren().addAll( lbl_tarih, tarih_input );

        // otobusun plakasini alicaz
        Label lbl_plaka = new Label("Plaka");
        lbl_plaka.getStyleClass().addAll( "fs11", "cbeyaz", "fbold");
        final TextField plaka_input = new TextField();
        plaka_input.getStyleClass().addAll("grigrad", "input-mid");
        plaka_input.setText(plaka);
        plaka_cont.setAlignment(Pos.CENTER);
        plaka_cont.getChildren().addAll( lbl_plaka, plaka_input );

        final GButton ekle_btn = new GButton("Ekle", GButton.CMORK );
        final Label notf = new Label();
        notf.getStyleClass().addAll( "fs11", "cbeyaz", "fbold");
        getChildren().addAll( tip_buton_cont, konu_cont, plaka_cont, tarih_cont, notf, ekle_btn );

        ekle_btn.setOnMousePressed(ev->{
            notf.setText("Ekleniyor.. Lütfen bekleyin.");
            ekle_btn.setDisable(true);
            String  not_val = not_input.getText(),
                    plaka_val = plaka_input.getText();
            if( not_tip == 0 || not_val.equals("") || plaka_val.equals("") ){
                notf.setText("Formda eksiklikler var!");
                ekle_btn.setDisable(false);
                return;
            }
            alarm_params = "alarm=0";
            tarih_val = Common.get_current_date();
            try {
                tarih_val = tarih_input.getValue().toString();
                if( !tarih_val.equals("") ){
                    alarm_params = "alarm=1&alarm_bitis="+tarih_val;
                    alarm = 1;
                }
            } catch( NullPointerException e ){
                //e.printStackTrace();
            }

            Thread ekle_th = new Thread(new Task<Void>() {
                JSONObject data;
                @Override
                protected void succeeded(){
                    int ok = data.getInt("ok");
                    if( ok == 1 ){
                        for( Refresh_Listener ref_listener : listeners ) ref_listener.on_refresh();
                    } else {
                        notf.setText("Not eklenirken bir hata oluştu. Lütfen tekrar deneyin.");
                        ekle_btn.setDisable(false);
                        return;
                    }
                    notf.setText("Not eklendi.");
                    not_input.setText("");
                    ekle_btn.setDisable(false);
                }

                @Override
                protected Void call(){
                    Web_Request dt_request = new Web_Request(Web_Request.SERVIS_URL, "&req=not_ekle&kapi_kodu="+oto+"&plaka="+plaka_val+"&icerik="+not_val+"&"+alarm_params+"&tip="+not_tip );
                    dt_request.kullanici_pc_parametreleri_ekle();
                    dt_request.action();
                    data = new JSONObject(dt_request.get_value());
                    return null;
                }

            });
            ekle_th.setDaemon(true);
            ekle_th.start();
        });

        kaza_btn.setOnMousePressed( ev -> {
            tip_btn_cb(kaza_btn, Otobus_Not.KAZA);
        });
        uyari_btn.setOnMousePressed( ev -> {
            tip_btn_cb(uyari_btn, Otobus_Not.UYARI);
        });
        servis_btn.setOnMousePressed( ev -> {
            tip_btn_cb(servis_btn, Otobus_Not.SERVIS);
        });

    }
    private void tip_btn_cb( Button btn, int tip ){
        not_tip = tip;
        for( int j = 0; j < 3; j++ ){
            tip_buton_cont.getChildren().get(j).getStyleClass().remove(3);
            tip_buton_cont.getChildren().get(j).getStyleClass().add("pasif");
            //System.out.println(tip_buton_cont.getChildren().get(j).getStyleClass());
        }
        btn.getStyleClass().remove(3);
        btn.getStyleClass().add("aktif");
    }

    public void add_listener( Refresh_Listener listener ){
        listeners.add(listener);
    }



}