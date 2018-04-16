package sample;

import jxl.CellView;
import jxl.Workbook;
import jxl.write.*;
import jxl.write.Colour;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Obarey on 18.02.2017.
 */
public class Excel_Surucu_Rapor {

    public String tarih;
    public Excel_Surucu_Rapor( String tarih ){
        this.tarih = tarih;
    }

    public boolean init(){
        WritableWorkbook myFirstWbook = null;
        try {
            ArrayList<String> ekli_suruculer = new ArrayList<>();
            try {
                Web_Request request = new Web_Request(Web_Request.SERVIS_URL, "&req=excel_surucu_rapor&oto=OBAREY&baslangic="+tarih+"&bitis=" );
                request.kullanici_pc_parametreleri_ekle();
                request.action();
                JSONArray data = new JSONObject(request.get_value()).getJSONObject("data").getJSONArray("surucu_data");

                int row = 1;

                if ( data.length() == 0 ) return false;

                myFirstWbook = Workbook.createWorkbook(new File("C:\\temp\\ORER_Surucu_Tablo_" + tarih + ".xls"));
                WritableSheet excelSheet = myFirstWbook.createSheet("ORER Sürücü Tablo", 0);

                Label label;
                WritableCellFormat cFormat = new WritableCellFormat();
                WritableFont font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
                cFormat.setFont(font);

                label = new Label(0, 0, "SÜRÜCÜ", cFormat);
                excelSheet.addCell(label);

                label = new Label(1, 0, "SİCİL NO", cFormat);
                excelSheet.addCell(label);

                label = new Label(2, 0, "TELEFON", cFormat);
                excelSheet.addCell(label);

                label = new Label(3, 0, "OTO", cFormat);
                excelSheet.addCell(label);

                label = new Label(4, 0, "HAT", cFormat);
                excelSheet.addCell(label);

                label = new Label(5, 0, "RUHSAT PLAKA", cFormat);
                excelSheet.addCell(label);

                label = new Label(6, 0, "AKTİF PLAKA", cFormat);
                excelSheet.addCell(label);

                WritableCellFormat cFormattbody_beyaz = new WritableCellFormat();
                cFormattbody_beyaz.setBackground(Colour.WHITE);

                WritableCellFormat cFormattbody_gri = new WritableCellFormat();
                cFormattbody_gri.setBackground(Colour.GREY_25_PERCENT);

                String son_oto = "";
                WritableCellFormat son_stil = cFormattbody_beyaz;
                WritableCellFormat aktif_stil;

                JSONObject res;
                for (int j = 0; j < data.length(); j++) {
                    res = data.getJSONObject(j);
                    if( !ekli_suruculer.contains(res.getString("surucu") ) ){

                        if( son_oto.equals(res.getString("oto") ) ){
                            aktif_stil = son_stil;
                        } else {
                            if( son_stil.equals( cFormattbody_beyaz  ) ){
                                aktif_stil = cFormattbody_gri;
                            } else {
                                aktif_stil = cFormattbody_beyaz;
                            }
                        }
                        label = new Label(0, row, res.getString("surucu_isim"), aktif_stil);
                        excelSheet.addCell(label);

                        label = new Label(1, row, res.getString("surucu"), aktif_stil);
                        excelSheet.addCell(label);

                        label = new Label(2, row, res.getString("surucu_telefon"), aktif_stil);
                        excelSheet.addCell(label);

                        label = new Label(3, row, res.getString("oto"), aktif_stil);
                        excelSheet.addCell(label);

                        label = new Label(4, row, res.getString("hat"), aktif_stil);
                        excelSheet.addCell(label);

                        label = new Label(5, row, res.getString("ruhsat_plaka"), aktif_stil);
                        excelSheet.addCell(label);

                        label = new Label(6, row, res.getString("plaka"), aktif_stil);
                        excelSheet.addCell(label);

                        row++;
                        ekli_suruculer.add(res.getString("surucu"));
                        son_oto = res.getString("oto");
                        son_stil = aktif_stil;
                    }
                }

                CellView cv = excelSheet.getColumnView(0);
                cv.setSize(8200);
                excelSheet.setColumnView(0, cv);

                cv = excelSheet.getColumnView(5);
                cv.setSize(4000);
                excelSheet.setColumnView(5, cv);

                cv = excelSheet.getColumnView(6);
                cv.setSize(4000);
                excelSheet.setColumnView(6, cv);

            } catch (JSONException e) {
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
