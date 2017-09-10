package sample;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Created by Jeppe on 13.03.2017.
 */
public class Rapor_Popup {

    private ScrollPane main_container;
    private Obarey_Popup popup;
    private String kod;

    private int tip;
    private double x, y;
    private String tfrom = "", tto = "";

    private boolean otobus_dt_eklendi = false,
                    hat_dt_eklendi = false,
                    surucu_dt_eklendi = false;


    public Rapor_Popup( int tip, String kod, String tarih_baslangic, String tarih_bitis ){
        this.kod = kod;
        this.tip = tip;
        this.tfrom = tarih_baslangic;
        this.tto = tarih_bitis;
    }

    public void init(  double x, double y, Node root ){
        Thread th = new Thread( new Task<Void>(){
            @Override
            protected void succeeded(){
                popup.show( x, y );
            }

            @Override
            protected Void call(){
                main_container = new ScrollPane();
                //main_container.setPrefHeight(220);
                main_container.setMaxHeight(Common.get_screen_res().get("H")-250);
                main_container.setPrefWidth(600);
                main_container.setMinWidth(600);

                VBox wrapper = new VBox();
                wrapper.getStyleClass().add("rapor-popup");
                wrapper.setAlignment(Pos.CENTER);
                wrapper.setSpacing(10);

                final String params = "&baslangic="+tfrom+"&bitis="+tto;
                Web_Request request = new Web_Request(Web_Request.SERVIS_URL, "&req=filo_rapor&oto="+kod+params );
                request.kullanici_pc_parametreleri_ekle();
                request.action();
                JSONObject data = new JSONObject(request.get_value()).getJSONObject("data").getJSONObject("rapor_data");

                if( data.getInt("P") == 0 ){
                    popup = new Obarey_Popup("Filo İstatistikler", root );
                    popup.init( true );
                    VBox error = new VBox();
                    error.setAlignment(Pos.CENTER);
                    error.getChildren().add(new Label("Veri Yok"));
                    popup.set_content( error );
                } else {
                    HBox alt_buton_cont = new HBox();
                    VBox.setMargin( alt_buton_cont, new Insets(10, 0,0,0) );
                    alt_buton_cont.setSpacing(10);
                    alt_buton_cont.setAlignment(Pos.CENTER);
                    if( tip == Rapor_Box_Toplam.TOPLAM_HAT ){
                        GButton otobus_stat_btn = new GButton("OTOBÜS İSTATİSTİKLERİ", GButton.CMORK);
                        GButton surucu_stat_btn = new GButton("SÜRÜCÜ İSTATİSTİKLERİ", GButton.CMORK);
                        alt_buton_cont.getChildren().addAll( otobus_stat_btn, surucu_stat_btn );
                    } else if( tip == Rapor_Box_Toplam.TOPLAM_OTOBUS ){
                        final GButton hat_stat_btn = new GButton("HAT İSTATİSTİKLERİ", GButton.CMORK);
                        final GButton surucu_stat_btn = new GButton("SÜRÜCÜ İSTATİSTİKLERİ", GButton.CMORK);
                        alt_buton_cont.getChildren().addAll( hat_stat_btn, surucu_stat_btn );

                        surucu_stat_btn.setOnAction( ev -> {
                            if( !surucu_dt_eklendi ){
                                surucu_stat_btn.setDisable(true);
                                Rapor_Datatable dt = new Rapor_Datatable(kod, Rapor_Datatable.SURUCU_DT, params );
                                Thread th = new Thread( dt );
                                th.setDaemon(true);
                                th.start();
                                dt.setOnSucceeded( eve -> {
                                    try {
                                        if( wrapper.getChildren().get(3) != null ) wrapper.getChildren().remove(3);
                                    } catch( IndexOutOfBoundsException e ) { e.printStackTrace(); }
                                    wrapper.getChildren().add(3, dt.get_table() );
                                    surucu_stat_btn.setDisable(false);
                                });
                                //main_container.setPrefHeight( 750 );
                                surucu_dt_eklendi = true;
                                hat_dt_eklendi = false;
                            }
                        });

                        hat_stat_btn.setOnAction( ev -> {
                            if( !hat_dt_eklendi ){
                                hat_stat_btn.setDisable(true);
                                Rapor_Datatable dt = new Rapor_Datatable(kod, Rapor_Datatable.HAT_DT, params );
                                Thread th = new Thread( dt );
                                th.setDaemon(true);
                                th.start();
                                dt.setOnSucceeded( eve -> {
                                    try {
                                        if( wrapper.getChildren().get(3) != null ) wrapper.getChildren().remove(3);
                                    } catch( IndexOutOfBoundsException e ) { e.printStackTrace(); }
                                    wrapper.getChildren().add(3, dt.get_table() );
                                    hat_stat_btn.setDisable(false);
                                });
                                hat_dt_eklendi = true;
                                surucu_dt_eklendi = false;
                            }
                        });

                    } else if( tip == Rapor_Box_Toplam.TOPLAM_SURUCU ){
                        GButton otobus_stat_btn = new GButton("OTOBÜS İSTATİSTİKLERİ", GButton.CMORK);
                        GButton hat_stat_btn = new GButton("HAT İSTATİSTİKLERİ", GButton.CMORK);
                        alt_buton_cont.getChildren().addAll( otobus_stat_btn, hat_stat_btn );
                    } else if( tip == Rapor_Box_Toplam.TOPLAM_FILO ){
                        final GButton otobus_stat_btn = new GButton("OTOBÜS İSTATİSTİKLERİ", GButton.CMORK);
                        final GButton hat_stat_btn = new GButton("HAT İSTATİSTİKLERİ", GButton.CMORK);
                        final GButton surucu_stat_btn = new GButton("SÜRÜCÜ İSTATİSTİKLERİ", GButton.CMORK);
                        alt_buton_cont.getChildren().addAll( otobus_stat_btn, hat_stat_btn, surucu_stat_btn );

                        surucu_stat_btn.setOnAction( ev -> {
                            if( !surucu_dt_eklendi ){
                                surucu_stat_btn.setDisable(true);
                                Rapor_Datatable dt = new Rapor_Datatable(kod, Rapor_Datatable.SURUCU_DT, params );
                                Thread th = new Thread( dt );
                                th.setDaemon(true);
                                th.start();
                                dt.setOnSucceeded( eve -> {
                                    try {
                                        if( wrapper.getChildren().get(3) != null ) wrapper.getChildren().remove(3);
                                    } catch( IndexOutOfBoundsException e ) { e.printStackTrace(); }
                                    wrapper.getChildren().add(3, dt.get_table() );
                                    surucu_stat_btn.setDisable(false);
                                });
                                surucu_dt_eklendi = true;
                                otobus_dt_eklendi = false;
                                hat_dt_eklendi = false;
                            }
                        });

                        hat_stat_btn.setOnAction( ev -> {
                            if( !hat_dt_eklendi ){
                                hat_stat_btn.setDisable(true);
                                Rapor_Datatable dt = new Rapor_Datatable(kod, Rapor_Datatable.HAT_DT, params );
                                Thread th = new Thread( dt );
                                th.setDaemon(true);
                                th.start();
                                dt.setOnSucceeded( eve -> {
                                    try {
                                        if( wrapper.getChildren().get(3) != null ) wrapper.getChildren().remove(3);
                                    } catch( IndexOutOfBoundsException e ) { e.printStackTrace(); }
                                    wrapper.getChildren().add(3, dt.get_table() );
                                    hat_stat_btn.setDisable(false);
                                });
                                hat_dt_eklendi = true;
                                otobus_dt_eklendi = false;
                                surucu_dt_eklendi = false;
                            }
                        });

                        otobus_stat_btn.setOnAction( ev -> {
                            if( !otobus_dt_eklendi ){
                                otobus_stat_btn.setDisable(true);
                                Rapor_Datatable dt = new Rapor_Datatable(kod, Rapor_Datatable.OTOBUS_DT, params );
                                Thread th = new Thread( dt );
                                th.setDaemon(true);
                                th.start();
                                dt.setOnSucceeded( eve -> {
                                    try {
                                        if( wrapper.getChildren().get(3) != null ) wrapper.getChildren().remove(3);
                                    } catch( IndexOutOfBoundsException e ) { e.printStackTrace(); }
                                    wrapper.getChildren().add(3, dt.get_table() );
                                    otobus_stat_btn.setDisable(false);
                                });
                                otobus_dt_eklendi = true;
                                hat_dt_eklendi = false;
                                surucu_dt_eklendi = false;
                            }
                        });

                    }

                    wrapper.getChildren().addAll( new Rapor_Box_Header( tip, kod, tfrom, tto ), new Rapor_Box_Toplam( tip, kod,
                            new Sefer_Rapor_Data(kod,
                                    data.getInt("P"),
                                    data.getInt(Sefer_Data.DTAMAM),
                                    data.getInt(Sefer_Data.DBEKLEYEN),
                                    data.getInt(Sefer_Data.DAKTIF),
                                    0,
                                    data.getInt(Sefer_Data.DIPTAL),
                                    data.getInt(Sefer_Data.DYARIM),
                                    data.getDouble("IETTKM"),
                                    data.getDouble("GKM"))), alt_buton_cont);
                    main_container.setContent( wrapper  );

                    popup = new Obarey_Popup("Filo İstatistikler", root );
                    popup.init(true);
                    popup.set_content( main_container );
                }
                return null;
            }
        });
        th.setDaemon(true);
        th.start();

    }


    public void show(){




    }

}
class Rapor_Box_Header extends Label {

    public Rapor_Box_Header( int tip, String kod, String tbaslangic, String tbitis ){
        super();
        this.getStyleClass().addAll( "flight", "fs12", "cbeyaz");
        String tarih_str = "";
        if( !tbaslangic.equals("") && tbitis.equals("") ){
            tarih_str = " [ " + tbaslangic + " ] tarihli ";
        } else if( !tbaslangic.equals("") && !tbitis.equals("")){
            tarih_str = " [ " + tbaslangic + " ] - [ " + tbitis + " ] tarihleri arasındaki ";
        }

        if( tip == Rapor_Box_Toplam.TOPLAM_FILO ){
            this.setText( tarih_str + "filo istatistikleri");
        } else if( tip == Rapor_Box_Toplam.TOPLAM_OTOBUS ){
            this.setText( kod + " " + tarih_str + "sefer istatistikleri");
        } else if( tip == Rapor_Box_Toplam.TOPLAM_HAT ){
            this.setText( kod + " hattının " + tarih_str + "sefer istatistikleri");
        } else if( tip == Rapor_Box_Toplam.TOPLAM_SURUCU ){
            this.setText( kod + " " + tarih_str + "sefer istatistikleri");
        }

    }

}