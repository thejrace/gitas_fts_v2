package sample;

import javafx.concurrent.Task;
import javafx.scene.control.Button;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Jeppe on 27.04.2017.
 */
public class Otobus_Box_Filtre {

    public static String    FD_BOLGE_A = "A",
                            FD_BOLGE_B = "B",
                            FD_BOLGE_C = "C",
                            FD_DTAMAM = "D"+Sefer_Data.DTAMAM,
                            FD_DAKTIF = "D"+Sefer_Data.DAKTIF,
                            FD_DBEKLEYEN = "D"+Sefer_Data.DBEKLEYEN,
                            FD_DIPTAL = "D"+Sefer_Data.DIPTAL,
                            FD_DYARIM = "D"+Sefer_Data.DYARIM,
                            FD_NOT = "NOT",
                            FD_IYS = "IYS",
                            FD_PLAKA = "PLAKA",
                            FD_ZAYI = "ZAYI",
                            FD_SCL = "SÇL";
    public static String    VE = "VE",
                            VEYA = "VEYA";

    private String kapi, g1 = "", g2 = "", g3 = "";
    public static String    AKTIF_CLASS = "filtre-button-aktif",
                            PASIF_CLASS = "filtre-button-pasif";

    private ArrayList<String> filtre_data = new ArrayList<>();
    private ArrayList<Filtre_Listener> listeners = new ArrayList<>();
    private Button bolgea, bolgeb, bolgec, st, sb, sa, si, sy, uyari_not, uyari_plaka, uyari_iys, sifirla_btn, kapi_btn, kaydet_btn, zayi_btn, scl_btn;

    public Otobus_Box_Filtre( Button _bolgea, Button _bolgeb, Button _bolgec, Button _st, Button _sb, Button _sa, Button _si, Button _sy, Button _uyari_not, Button _uyari_plaka, Button _uyari_iys, Button _sifirla_btn, Button _kapi_btn, Button _kaydet_btn, Button _zayi_btn, Button _scl_btn  ){

        bolgea = _bolgea;
        bolgeb = _bolgeb;
        bolgec = _bolgec;
        st = _st;
        sb = _sb;
        sa = _sa;
        si = _si;
        sy = _sy;
        uyari_not = _uyari_not;
        uyari_plaka = _uyari_plaka;
        uyari_iys = _uyari_iys;
        sifirla_btn = _sifirla_btn;
        kapi_btn = _kapi_btn;
        kaydet_btn = _kaydet_btn;
        zayi_btn = _zayi_btn;
        scl_btn = _scl_btn;

        sifirla();

        sifirla_btn.setOnMousePressed( ev->{
            sifirla();
            listener_init();
        });

        zayi_btn.setOnMousePressed( ev -> {
            eksin( FD_ZAYI, zayi_btn );
        });

        scl_btn.setOnMousePressed( ev -> {
            eksin( FD_SCL, scl_btn );
        });

        kaydet_btn.setOnMousePressed( ev -> {
            g1 = "";
            g2 = "";
            g3 = "";
            kaydet_btn.setDisable(true);
            if( filtre_data.contains(FD_BOLGE_A) ) { g1 += "1"; } else { g1 += "0"; }
            if( filtre_data.contains(FD_BOLGE_B) ) { g1 += "1"; } else { g1 += "0"; }
            if( filtre_data.contains(FD_BOLGE_C) ) { g1 += "1"; } else { g1 += "0"; }

            if( filtre_data.contains(FD_DTAMAM) ) { g2 += "1"; } else { g2 += "0"; }
            if( filtre_data.contains(FD_DBEKLEYEN) ) { g2 += "1"; } else { g2 += "0"; }
            if( filtre_data.contains(FD_DAKTIF) ) { g2 += "1"; } else { g2 += "0"; }
            if( filtre_data.contains(FD_DIPTAL) ) { g2 += "1"; } else { g2 += "0"; }
            if( filtre_data.contains(FD_DYARIM) ) { g2 += "1"; } else { g2 += "0"; }


            if( filtre_data.contains(FD_SCL) ) { g3 += "1"; } else { g3 += "0"; }
            if( filtre_data.contains(FD_ZAYI) ) { g3 += "1"; } else { g3 += "0"; }
            if( filtre_data.contains(FD_NOT) ) { g3 += "1"; } else { g3 += "0"; }
            if( filtre_data.contains(FD_PLAKA) ) { g3 += "1"; } else { g3 += "0"; }
            if( filtre_data.contains(FD_IYS) ) { g3 += "1"; } else { g3 += "0"; }


            Thread th = new Thread( new Task<Void>(){
                @Override
                protected void succeeded(){
                    kaydet_btn.setDisable(false);
                }

                @Override
                protected Void call(){
                    Web_Request request = new Web_Request(Web_Request.SERVIS_URL, "&req=filtre_guncelleme&g1="+g1+"&g2="+g2+"&g3="+g3+"&filtre_kapi="+kapi );
                    request.kullanici_pc_parametreleri_ekle();
                    request.action();
                    // TODO
                    JSONObject new_data = new JSONObject();
                    new_data.put("filtre_kapi", kapi );
                    new_data.put("g1", g1 );
                    new_data.put("g2", g2 );
                    new_data.put("g3", g3 );
                    User_Config.init_app_data("filtre_data", new_data );
                    return null;
                }
            });
            th.setDaemon(true);
            th.start();


        });

        kapi_btn.setOnMousePressed( ev -> {
            if( kapi.equals(VE) ){
                kapi = VEYA;
            } else {
                kapi = VE;
            }
            kapi_btn.setText(kapi);
            listener_init();
        });

        bolgea.setOnMousePressed( ev -> {
            eksin( FD_BOLGE_A, bolgea );
        });
        bolgeb.setOnMousePressed( ev -> {
            eksin( FD_BOLGE_B, bolgeb );
        });
        bolgec.setOnMousePressed( ev -> {
            eksin( FD_BOLGE_C, bolgec );
        });
        st.setOnMousePressed( ev -> {
            eksin( FD_DTAMAM, st );
        });
        sa.setOnMousePressed( ev -> {
            eksin( FD_DAKTIF, sa );
        });
        sb.setOnMousePressed( ev -> {
            eksin( FD_DBEKLEYEN, sb );
        });
        si.setOnMousePressed( ev -> {
            eksin( FD_DIPTAL, si );
        });
        sy.setOnMousePressed( ev -> {
            eksin( FD_DYARIM, sy );
        });
        uyari_not.setOnMousePressed( ev -> {
            eksin( FD_NOT, uyari_not );
        });
        uyari_plaka.setOnMousePressed( ev -> {
            eksin( FD_PLAKA, uyari_plaka );
        });
        uyari_iys.setOnMousePressed( ev -> {
            eksin( FD_IYS, uyari_iys );
        });


    }

   private void sifirla(){
        filtre_data = new ArrayList<>();
        JSONObject config = User_Config.app_filtre;
        String kayit_str = config.getString("g1");
        if( kayit_str.charAt(0) == '1' ) { filtre_data.add(FD_BOLGE_A); button_aktif(bolgea); } else { button_pasif(bolgea); }
        if( kayit_str.charAt(1) == '1' ) { filtre_data.add(FD_BOLGE_B); button_aktif(bolgeb); } else { button_pasif(bolgeb); }
        if( kayit_str.charAt(2) == '1' ) { filtre_data.add(FD_BOLGE_C); button_aktif(bolgec); } else { button_pasif(bolgec); }


        kayit_str = config.getString("g2");
        if( kayit_str.charAt(0) == '1' ) { filtre_data.add(FD_DTAMAM); button_aktif(st); } else { button_pasif(st); }
        if( kayit_str.charAt(1) == '1' ) { filtre_data.add(FD_DBEKLEYEN); button_aktif(sb); } else { button_pasif(sb); }
        if( kayit_str.charAt(2) == '1' ) { filtre_data.add(FD_DAKTIF); button_aktif(sa); } else { button_pasif(sa); }
        if( kayit_str.charAt(3) == '1' ) { filtre_data.add(FD_DIPTAL); button_aktif(si); } else { button_pasif(si); }
        if( kayit_str.charAt(4) == '1' ) { filtre_data.add(FD_DYARIM); button_aktif(sy); } else { button_pasif(sy); }

        kayit_str = config.getString("g3");
        if( kayit_str.charAt(0) == '1' ) { filtre_data.add(FD_SCL); button_aktif(scl_btn); } else { button_pasif(scl_btn); }
        if( kayit_str.charAt(1) == '1' ) { filtre_data.add(FD_ZAYI); button_aktif(zayi_btn); } else { button_pasif(zayi_btn); }
        if( kayit_str.charAt(2) == '1' ) { filtre_data.add(FD_NOT); button_aktif(uyari_not); } else { button_pasif(uyari_not); }
        if( kayit_str.charAt(3) == '1' ) { filtre_data.add(FD_PLAKA); button_aktif(uyari_plaka); } else {  button_pasif(uyari_plaka); }
        if( kayit_str.charAt(4) == '1' ) { filtre_data.add(FD_IYS); button_aktif(uyari_iys); } else { button_pasif(uyari_iys); }

        kapi = config.getString("filtre_kapi");
        kapi_btn.setText(kapi);
    }

    public String get_kapi(){
        return kapi;
    }

    public void add_listener( Filtre_Listener listener ){
        listeners.add(listener);
    }

    private void button_aktif( Button btn ){
        btn.getStyleClass().remove(2 );
        btn.getStyleClass().add(AKTIF_CLASS);
    }
    private void button_pasif( Button btn ){
        btn.getStyleClass().remove(2 );
        btn.getStyleClass().add(PASIF_CLASS);
    }
    private void eksin(  String val, Button btn ){
        if( filtre_data.contains(val) ){
            for( int j = 0; j < filtre_data.size(); j++ ) if( filtre_data.get(j).equals( val ) ) filtre_data.remove( j );
            button_pasif( btn );
        } else {
            filtre_data.add( val );
            button_aktif( btn );
        }
        listener_init();
    }
    private void listener_init(){
        Otobus_Box_Filtre_Data box_filtre_data = new Otobus_Box_Filtre_Data();
        box_filtre_data.set_str_vals( filtre_data );
        for( Filtre_Listener listener : listeners ) listener.on_filtre_set( kapi, box_filtre_data );
    }


    public ArrayList<String> get_filtre_data(){
        return filtre_data;
    }

}
