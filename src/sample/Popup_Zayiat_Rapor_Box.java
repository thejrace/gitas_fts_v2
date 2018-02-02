package sample;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jeppe on 19.06.2017.
 */
public class Popup_Zayiat_Rapor_Box extends VBox {

    public static String    OTOBUS = "oto",
                            HAT = "hat",
                            SURUCU = "surucu",
                            PLAKA = "plaka",
                            TARIH = "tarih";

    private String dtkey;
    private GButton gunluk_rapor_btn,  aralik_rapor_btn, tum_rapor_btn;

    public Popup_Zayiat_Rapor_Box( String dtkey ){
        super();
        this.dtkey = dtkey;

        this.setMinWidth(700);
        //this.setPrefHeight(250);
        this.setAlignment(Pos.TOP_CENTER);
        this.setSpacing(20);
        this.getStyleClass().add("secenekler-tab");
        this.setPadding(new Insets( 10, 10, 20, 10 ));

        final Label lbl_notf = new Label("Zayiat İstatistik Raporla.");
        lbl_notf.getStyleClass().addAll("fs13");

        VBox tarih_filtre = new VBox();
        tarih_filtre.setSpacing(20);
        tarih_filtre.setAlignment(Pos.CENTER);

        VBox gunluk_filtre_item = new VBox();
        HBox gunluk_filtre_content = new HBox();
        gunluk_filtre_content.setSpacing(10);

        gunluk_rapor_btn = new GButton("Raporu Oluştur", GButton.CMORK );
        final DatePicker gunluk_dp = new DatePicker();
        gunluk_dp.setValue(Common.dp_placeholder(Common.get_current_date()));
        Label gunluk_filtre_header = new Label("Günlük");
        gunluk_filtre_header.getStyleClass().addAll("fbold", "cbeyaz", "fs11");
        gunluk_filtre_item.setSpacing(10);
        gunluk_filtre_item.setAlignment(Pos.CENTER);

        gunluk_filtre_content.getChildren().addAll( gunluk_dp, gunluk_rapor_btn );
        gunluk_filtre_content.setAlignment(Pos.CENTER);
        gunluk_filtre_item.getChildren().addAll( gunluk_filtre_header, gunluk_filtre_content );


        VBox aralik_filtre_item = new VBox();
        HBox aralik_filtre_content = new HBox();
        aralik_filtre_item.setAlignment(Pos.CENTER);
        aralik_filtre_content.setAlignment(Pos.CENTER);
        aralik_filtre_content.setSpacing(10);
        aralik_filtre_item.setSpacing(10);
        aralik_rapor_btn = new GButton("Raporu Oluştur", GButton.CMORK );
        Label aralik_filtre_header = new Label("Tarih Aralığı");
        aralik_filtre_header.getStyleClass().addAll("fbold", "cbeyaz", "fs11");

        final DatePicker baslangic_dp = new DatePicker();
        final DatePicker bitis_dp = new DatePicker();
        bitis_dp.setValue(Common.dp_placeholder(Common.get_current_date()));
        aralik_filtre_content.getChildren().addAll( baslangic_dp, bitis_dp, aralik_rapor_btn );
        aralik_filtre_item.getChildren().addAll( aralik_filtre_header, aralik_filtre_content );

        tum_rapor_btn = new GButton("Baştan Sonra Rapor Oluştur", GButton.CMORK );

        tarih_filtre.getChildren().addAll( gunluk_filtre_item, aralik_filtre_item, tum_rapor_btn );

        this.getChildren().addAll( lbl_notf, tarih_filtre );
        gunluk_rapor_btn.setOnMousePressed(ev->{
            String  gun   = gunluk_dp.getValue().toString();
            if( gun.equals("") ) return;
            gunluk_rapor_btn.setDisable(true);
            Thread th = new Thread( new Task<Void>(){
                Zayiat_Rapor_Datatable dt;
                @Override
                protected Void call(){
                    Web_Request request = new Web_Request(Web_Request.SERVIS_URL, "&req=zayiat_raporu&dtkey="+dtkey+"&baslangic="+gun+"&bitis=" );
                    request.kullanici_pc_parametreleri_ekle();
                    request.action();

                    try {
                        JSONObject data = new JSONObject(request.get_value()).getJSONObject("data");
                        System.out.println(data);
                        dt = new Zayiat_Rapor_Datatable( data.getJSONObject("rapor_data"), data.getJSONArray("sort_keys") );
                        dt.init();
                    } catch(JSONException e ){
                        e.printStackTrace();
                    }
                    return null;
                }
                @Override
                protected void succeeded(){
                    try {
                        getChildren().remove(2);
                    } catch( IndexOutOfBoundsException e ){
                        //e.printStackTrace();
                    }
                    try {
                        getChildren().add( dt );
                    } catch( NullPointerException e ){
                        //e.printStackTrace();
                    }
                    gunluk_rapor_btn.setDisable(false);
                }
            });
            th.setDaemon(true);
            th.start();
        });

        aralik_rapor_btn.setOnMousePressed(ev->{
            String  baslangic = baslangic_dp.getValue().toString(),
                    bitis = bitis_dp.getValue().toString();
            if( baslangic.equals("") ||  bitis.equals("") ) return;
            aralik_rapor_btn.setDisable(true);
            Thread th = new Thread( new Task<Void>(){
                Zayiat_Rapor_Datatable dt;
                @Override
                protected Void call(){
                    Web_Request request = new Web_Request(Web_Request.SERVIS_URL, "&req=zayiat_raporu&dtkey="+dtkey+"&baslangic="+baslangic+"&bitis="+bitis );
                    request.kullanici_pc_parametreleri_ekle();
                    request.action();
                    try {
                        JSONObject data = new JSONObject(request.get_value()).getJSONObject("data");
                        System.out.println(data);
                        dt = new Zayiat_Rapor_Datatable( data.getJSONObject("rapor_data"), data.getJSONArray("sort_keys") );
                        dt.init();
                    } catch(JSONException e ){
                        e.printStackTrace();
                    }
                    return null;
                }
                @Override
                protected void succeeded(){
                    try {
                        getChildren().remove(2);
                    } catch( IndexOutOfBoundsException e ){
                        e.printStackTrace();
                    }
                    try {
                        getChildren().add( dt );
                    } catch( NullPointerException e ){
                        e.printStackTrace();
                    }
                    aralik_rapor_btn.setDisable(false);
                }
            });
            th.setDaemon(true);
            th.start();
        });

        tum_rapor_btn.setOnMousePressed(ev->{
            tum_rapor_btn.setDisable(true);
            Thread th = new Thread( new Task<Void>(){
                Zayiat_Rapor_Datatable dt;
                @Override
                protected Void call(){
                    Web_Request request = new Web_Request(Web_Request.SERVIS_URL, "&req=zayiat_raporu&dtkey="+dtkey+"&baslangic=&bitis=" );
                    request.kullanici_pc_parametreleri_ekle();
                    request.action();
                    try {
                        JSONObject data = new JSONObject(request.get_value()).getJSONObject("data");
                        System.out.println(data);
                        dt = new Zayiat_Rapor_Datatable( data.getJSONObject("rapor_data"), data.getJSONArray("sort_keys") );
                        dt.init();
                    } catch(JSONException e ){
                        e.printStackTrace();
                    }
                    return null;
                }
                @Override
                protected void succeeded(){
                    try {
                        getChildren().remove(2);
                    } catch( IndexOutOfBoundsException e ){
                        e.printStackTrace();
                    }
                    try {
                        getChildren().add( dt );
                    } catch( NullPointerException e ){
                        e.printStackTrace();
                    }
                    tum_rapor_btn.setDisable(false);
                }
            });
            th.setDaemon(true);
            th.start();
        });

    }




}
