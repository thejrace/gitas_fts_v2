package sample;

import jxl.CellView;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.write.*;
import jxl.write.Border;
import jxl.write.BorderLineStyle;
import jxl.write.Colour;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Obarey on 17.02.2017.
 */
public class Excel_Filo_Plan {



    private String tarih, tarih_ters, oto, excel_isim_extra = "";
    private boolean oto_flag;
    private JSONArray oto_data = new JSONArray();
    public Excel_Filo_Plan( String tarih ){
        String[] tarihler = tarih.split("-");
        this.tarih = tarih;
        try {
            this.tarih_ters = tarihler[2]+"-"+tarihler[1]+"-"+tarihler[0];
        } catch( ArrayIndexOutOfBoundsException e ){

        }
    }

    public void set_oto_flag( String _oto ){
        oto_flag = true;
        oto = _oto;
    }
    public void set_data( JSONArray _data, Map<String, String> _suruculer_temp, char _kaynak  ){
        JSONObject sefer;
        String plaka = "Veri Yok";
        for( int k = 0; k < User_Config.app_otobusler.length(); k++ ){
            if( User_Config.app_otobusler.getJSONObject(k).getString("kapi_kodu").equals(oto)){
                plaka = User_Config.app_otobusler.getJSONObject(k).getString("aktif_plaka");
            }
        }
        for( int x = 0; x < _data.length(); x++ ) {
            sefer = _data.getJSONObject(x);
            String surucu_isim;
            if( _kaynak == 'A' ){
                if( _suruculer_temp.containsKey(sefer.getString("orer") ) ){
                    surucu_isim = _suruculer_temp.get(sefer.getString("orer"));
                } else {
                    surucu_isim = "BELIRSIZ SURUCU";
                }
            } else {
                surucu_isim = sefer.getString("surucu");
            }
            JSONObject _sefer_data = new JSONObject();
            _sefer_data.put( "oto", oto );
            _sefer_data.put("no", sefer.getString("no"));
            _sefer_data.put("hat", sefer.getString("hat"));
            _sefer_data.put("servis", sefer.getString("servis"));
            _sefer_data.put("guzergah", sefer.getString("guzergah"));
            _sefer_data.put("orer", sefer.getString("orer"));
            _sefer_data.put("gelis", sefer.getString("gelis"));
            _sefer_data.put("bitis", sefer.getString("bitis"));
            _sefer_data.put("gidis", sefer.getString("gidis"));
            _sefer_data.put("tahmin", sefer.getString("tahmin"));
            _sefer_data.put("amir", sefer.getString("amir"));
            _sefer_data.put("surucu", surucu_isim );
            _sefer_data.put("durum_kodu", sefer.getString("durum_kodu"));
            _sefer_data.put("durum", sefer.getString("durum"));
            _sefer_data.put("plaka", plaka);
            oto_data.put(_sefer_data);
        }
        if( _kaynak == 'A' ){
            excel_isim_extra = "_AKTIF_FILO_VERI";
        } else {
            excel_isim_extra = "_SUNUCU";
        }
    }

    public boolean init( boolean cb_tamam, boolean cb_bekleyen, boolean cb_aktif, boolean cb_iptal, boolean cb_yarim, boolean cb_plaka ){

        WritableWorkbook myFirstWbook = null;
        try {
            try {
                JSONArray data;
                if( oto_flag ){
                    myFirstWbook = Workbook.createWorkbook(new File("C:\\temp\\"+oto+"_ORER_Tablo_"+tarih+excel_isim_extra+".xls"));
                    data = oto_data;
                } else {
                    myFirstWbook = Workbook.createWorkbook(new File("C:\\temp\\ORER_Tablo_"+tarih+".xls"));

                    Web_Request request = new Web_Request(Web_Request.SERVIS_URL, "&req=orer_download&oto=OBAREY&excel=true&baslangic="+tarih+"&bitis=" );
                    request.kullanici_pc_parametreleri_ekle();
                    request.action();
                    data = new JSONObject(request.get_value()).getJSONObject("data").getJSONArray("orer_data");
                }
                WritableSheet excelSheet = myFirstWbook.createSheet("ORER Tablo", 0);

                int row = 1, col = 0;
                String onceki_sefer_bitis = "", son_oto = "", amir_str;
                if( data.length() == 0 ) return false;

                myFirstWbook.setColourRGB(Colour.ROSE, 244, 188, 188);
                myFirstWbook.setColourRGB(Colour.RED, 255, 232, 232);
                myFirstWbook.setColourRGB(Colour.GREEN, 202, 234, 174);
                myFirstWbook.setColourRGB(Colour.GREY_25_PERCENT, 230, 230, 230);

                Label label;
                WritableCellFormat cFormat = new WritableCellFormat();
                WritableFont font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
                cFormat.setFont(font);

                label = new Label(0, 0, "DKODU", cFormat);
                excelSheet.addCell(label);

                label = new Label(1, 0, "TARİH", cFormat);
                excelSheet.addCell(label);

                label = new Label(2, 0, "SIRA", cFormat);
                excelSheet.addCell(label);

                label = new Label(3, 0, "OTO", cFormat);
                excelSheet.addCell(label);

                label = new Label(4, 0, "SERVİS", cFormat);
                excelSheet.addCell(label);

                label = new Label(5, 0, "HAT", cFormat);
                excelSheet.addCell(label);

                label = new Label(6, 0, "GÜZERGAH", cFormat);
                excelSheet.addCell(label);

                label = new Label(7, 0, "SÜRÜCÜ", cFormat);
                excelSheet.addCell(label);

                label = new Label(8, 0, "GELİŞ", cFormat);
                excelSheet.addCell(label);

                label = new Label(9, 0, "ORER", cFormat);
                excelSheet.addCell(label);

                label = new Label(10, 0, "BEKLEME", cFormat);
                excelSheet.addCell(label);

                label = new Label(11, 0, "AMİR", cFormat);
                excelSheet.addCell(label);

                label = new Label(12, 0, "GİDİŞ", cFormat);
                excelSheet.addCell(label);

                label = new Label(13, 0, "TAHMİN", cFormat);
                excelSheet.addCell(label);

                label = new Label(14, 0, "BİTİŞ", cFormat);
                excelSheet.addCell(label);

                label = new Label(15, 0, "SÜRE", cFormat);
                excelSheet.addCell(label);

                label = new Label(16, 0, "DKODU", cFormat);
                excelSheet.addCell(label);

                int opt_col  = 16;
                Map<String, Integer> opt_col_index = new HashMap<>();

                if( cb_plaka ){
                    opt_col++;
                    label = new Label(opt_col, 0, "PLAKA", cFormat);
                    excelSheet.addCell(label);
                    opt_col_index.put("plaka", opt_col);
                }

                WritableFont fontbody = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD);
                WritableCellFormat cFormatNot = new WritableCellFormat();
                cFormatNot.setFont(fontbody);

                JSONObject res;
                for (int j = 0; j < data.length(); j++) {
                    res = data.getJSONObject(j);


                    WritableCellFormat cFormattbody = new WritableCellFormat();

                    cFormattbody.setBorder(Border.BOTTOM, BorderLineStyle.THIN, Colour.GRAY_50);
                    cFormattbody.setFont(fontbody);
                    cFormattbody.setAlignment(Alignment.CENTRE);

                    cFormat.setFont(fontbody);
                    if (res.getString("durum").equals("I")) {
                        if (!cb_iptal) continue;
                        cFormattbody.setBackground(Colour.ROSE);
                    } else if (res.getString("durum").equals("Y")) {
                        if (!cb_yarim) continue;
                        cFormattbody.setBackground(Colour.RED);
                    } else if (res.getString("durum").equals("A")) {
                        if (!cb_aktif) continue;
                        cFormattbody.setBackground(Colour.GREEN);
                    } else if (res.getString("durum").equals("T")) {
                        if (!cb_tamam) continue;
                        cFormattbody.setBackground(Colour.GREY_25_PERCENT);
                    } else {
                        if (!cb_bekleyen) continue;
                        cFormattbody.setBackground(Colour.WHITE);
                    }

                    if (!res.getString("oto").equals(son_oto)) {
                        onceki_sefer_bitis = "";
                    }

                    label = new Label(0, row, "", cFormattbody);
                    excelSheet.addCell(label);

                    label = new Label(1, row, tarih_ters, cFormattbody);
                    excelSheet.addCell(label);

                    label = new Label(2, row, res.getString("no"), cFormattbody);
                    excelSheet.addCell(label);

                    label = new Label(3, row, res.getString("oto"), cFormattbody);
                    excelSheet.addCell(label);

                    label = new Label(4, row, res.getString("servis"), cFormattbody);
                    excelSheet.addCell(label);

                    label = new Label(5, row, res.getString("hat"), cFormattbody);
                    excelSheet.addCell(label);

                    label = new Label(6, row, res.getString("guzergah"), cFormattbody);
                    excelSheet.addCell(label);

                    label = new Label(7, row, res.getString("surucu"), cFormattbody);
                    excelSheet.addCell(label);

                    label = new Label(8, row, res.getString("gelis"), cFormattbody);
                    excelSheet.addCell(label);

                    label = new Label(9, row, res.getString("orer"), cFormattbody);
                    excelSheet.addCell(label);

                    label = new Label(10, row, String.valueOf(Sefer_Sure.hesapla(onceki_sefer_bitis, res.getString("gidis"))), cFormattbody);
                    excelSheet.addCell(label);

                    amir_str = res.getString("amir");
                    if (amir_str.equals("[ 10 5 2 ]")) amir_str = "";
                    label = new Label(11, row, amir_str, cFormattbody);
                    excelSheet.addCell(label);

                    label = new Label(12, row, res.getString("gidis"), cFormattbody);
                    excelSheet.addCell(label);

                    label = new Label(13, row, res.getString("tahmin"), cFormattbody);
                    excelSheet.addCell(label);

                    label = new Label(14, row, res.getString("bitis"), cFormattbody);
                    excelSheet.addCell(label);

                    label = new Label(15, row, String.valueOf(Sefer_Sure.hesapla(res.getString("gidis"), res.getString("bitis"))), cFormattbody);
                    excelSheet.addCell(label);

                    label = new Label(16, row, res.getString("durum_kodu"), cFormattbody);
                    excelSheet.addCell(label);

                    if (cb_plaka) {
                        label = new Label(opt_col_index.get("plaka"), row, res.getString("plaka"), cFormattbody);
                        excelSheet.addCell(label);
                    }
                    onceki_sefer_bitis = res.getString("bitis");
                    son_oto = res.getString("oto");
                    row++;
                }

                CellView cv = excelSheet.getColumnView(7);
                cv.setSize(8200);
                excelSheet.setColumnView(7, cv);

                cv = excelSheet.getColumnView(1);
                cv.setSize(4000);
                excelSheet.setColumnView(1, cv);

                cv = excelSheet.getColumnView(6);
                cv.setSize(4000);
                excelSheet.setColumnView(6, cv);


            } catch( JSONException e ){
                e.printStackTrace();
            }
            myFirstWbook.write();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } finally {
            if (myFirstWbook != null) {
                try {
                    myFirstWbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
}
