package sample;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Created by Jeppe on 15.03.2017.
 */
public class Rapor_Tarih_Filtre_Box extends VBox {

    public Rapor_Tarih_Filtre_Box( int tip, String kod, Node root ){
        super();

        this.setMinWidth(700);
        this.setPrefHeight(250);
        this.setAlignment(Pos.TOP_CENTER);
        this.setSpacing(20);
        this.getStyleClass().add("secenekler-tab");
        this.setPadding(new Insets( 10, 10, 20, 10 ));

        final Label lbl_notf = new Label("Otobüs istatistiklerini raporla.");
        lbl_notf.getStyleClass().addAll("fs13");

        VBox tarih_filtre = new VBox();
        tarih_filtre.setSpacing(20);
        tarih_filtre.setAlignment(Pos.CENTER);

        VBox gunluk_filtre_item = new VBox();
        HBox gunluk_filtre_content = new HBox();
        gunluk_filtre_content.setSpacing(10);

        GButton gunluk_rapor_btn = new GButton("Raporu Oluştur", GButton.CMORK );
        final DatePicker gunluk_dp = new DatePicker();
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
        GButton aralik_rapor_btn = new GButton("Raporu Oluştur", GButton.CMORK );
        Label aralik_filtre_header = new Label("Tarih Aralığı");
        aralik_filtre_header.getStyleClass().addAll("fbold", "cbeyaz", "fs11");

        final DatePicker baslangic_dp = new DatePicker();
        final DatePicker bitis_dp = new DatePicker();
        aralik_filtre_content.getChildren().addAll( baslangic_dp, bitis_dp, aralik_rapor_btn );
        aralik_filtre_item.getChildren().addAll( aralik_filtre_header, aralik_filtre_content );

        GButton tum_rapor = new GButton("Baştan Sonra Rapor Oluştur", GButton.CMORK );

        tarih_filtre.getChildren().addAll( gunluk_filtre_item, aralik_filtre_item, tum_rapor );

        this.getChildren().addAll( lbl_notf, tarih_filtre );
        gunluk_rapor_btn.setOnMousePressed(ev->{
            String  gun   = gunluk_dp.getValue().toString();
            if( gun.equals("") ) return;
            Rapor_Popup rapor_popup = new Rapor_Popup(Rapor_Box_Toplam.TOPLAM_OTOBUS, kod, gun, "" );
            rapor_popup.init( ev.getScreenX(), ev.getScreenY(), root );
            rapor_popup.show();
        });

        aralik_rapor_btn.setOnMousePressed(ev->{
            String  baslangic = baslangic_dp.getValue().toString(),
                    bitis = bitis_dp.getValue().toString();
            if( baslangic.equals("") ||  bitis.equals("") ) return;
            Rapor_Popup rapor_popup = new Rapor_Popup(Rapor_Box_Toplam.TOPLAM_OTOBUS, kod, baslangic, bitis );
            rapor_popup.init( ev.getScreenX(), ev.getScreenY(), root );
            rapor_popup.show();
        });

        tum_rapor.setOnMousePressed(ev->{
            Rapor_Popup rapor_popup = new Rapor_Popup(Rapor_Box_Toplam.TOPLAM_OTOBUS, kod, "", "" );
            rapor_popup.init( ev.getScreenX(), ev.getScreenY(), root );
            rapor_popup.show();
        });


    }



}
