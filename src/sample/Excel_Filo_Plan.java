package sample;

import javafx.scene.paint.Color;
import jxl.Workbook;
import jxl.format.*;
import jxl.format.Alignment;
import jxl.write.*;
import jxl.write.Border;
import jxl.write.BorderLineStyle;
import jxl.write.Colour;
import jxl.write.Number;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Obarey on 17.02.2017.
 */
public class Excel_Filo_Plan {



    private String tarih, tarih_ters;
    public Excel_Filo_Plan( String tarih ){
        String[] tarihler = tarih.split("-");
        this.tarih = tarih;
        this.tarih_ters = tarihler[2]+"-"+tarihler[1]+"-"+tarihler[0];
    }

    public boolean init( boolean cb_tamam, boolean cb_bekleyen, boolean cb_aktif, boolean cb_iptal, boolean cb_yarim, boolean cb_plaka ){

        WritableWorkbook myFirstWbook = null;
        try {
            try {

                Web_Request request = new Web_Request(Web_Request.SERVIS_URL, "&req=orer_download&oto=OBAREY&excel=true&baslangic="+tarih+"&bitis=" );
                request.kullanici_pc_parametreleri_ekle(true);
                request.action();
                JSONArray data = new JSONObject(request.get_value()).getJSONObject("data").getJSONArray("orer_data");

                int row = 1, col = 0;
                String onceki_sefer_bitis = "", son_oto = "", amir_str;

                if( data.length() == 0 ) return false;

                myFirstWbook = Workbook.createWorkbook(new File("C:\\temp\\ORER_Tablo_"+tarih+".xls"));
                WritableSheet excelSheet = myFirstWbook.createSheet("ORER Tablo", 0);

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
