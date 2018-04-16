package sample;

import jxl.CellView;
import jxl.Workbook;
import jxl.write.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.File;
import java.io.IOException;

public class Excel_PDKS_Rapor {

    private String tarih;
    private Refresh_Listener listener;
    public Excel_PDKS_Rapor( String _tarih ){
        tarih = _tarih;
    }

    public void on_finish( Refresh_Listener _listener ){
        listener = _listener;
    }

    public void init(){

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                WritableWorkbook myFirstWbook = null;
                try {
                    try {
                        Web_Request request = new Web_Request(Web_Request.SERVIS_URL, "&req=pdks_excel_veri_download&baslangic="+tarih+"&bitis=" );
                        request.kullanici_pc_parametreleri_ekle();
                        request.action();
                        JSONArray data = new JSONObject(request.get_value()).getJSONObject("data").getJSONArray("pdks_data");

                        int row = 1;

                        if ( data.length() == 0 ) return;

                        myFirstWbook = Workbook.createWorkbook(new File("C:\\temp\\PDKS_Rapor_" + tarih + ".xls"));
                        WritableSheet excelSheet = myFirstWbook.createSheet("PDKS Rapor", 0);

                        Label label;
                        WritableCellFormat cFormat = new WritableCellFormat();
                        WritableFont font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
                        cFormat.setFont(font);

                        label = new Label(0, 0, "OTO", cFormat);
                        excelSheet.addCell(label);


                        label = new Label(1, 0, "RUHSAT PLAKA", cFormat);
                        excelSheet.addCell(label);

                        label = new Label(2, 0, "AKTİF PLAKA", cFormat);
                        excelSheet.addCell(label);

                        label = new Label(3, 0, "SÜRÜCÜ", cFormat);
                        excelSheet.addCell(label);

                        label = new Label(4, 0, "HAREKET", cFormat);
                        excelSheet.addCell(label);

                        label = new Label(5, 0, "SAAT", cFormat);
                        excelSheet.addCell(label);

                        label = new Label(6, 0, "TARİH", cFormat);
                        excelSheet.addCell(label);


                        WritableCellFormat cFormattbody_beyaz = new WritableCellFormat();
                        cFormattbody_beyaz.setBackground(Colour.WHITE);

                        WritableCellFormat cFormattbody_gri = new WritableCellFormat();
                        cFormattbody_gri.setBackground(Colour.GREY_25_PERCENT);

                        String son_oto = "";
                        WritableCellFormat son_stil = cFormattbody_beyaz;
                        WritableCellFormat aktif_stil;

                        JSONObject res;
                        JSONArray otobus_array;
                        for (int j = 0; j < data.length(); j++) {
                            otobus_array = data.getJSONArray(j);

                            for( int k = 0; k < otobus_array.length(); k++ ){
                                res = otobus_array.getJSONObject(k);

                                if( son_oto.equals(res.getString("oto") ) ){
                                    aktif_stil = son_stil;
                                } else {
                                    if( son_stil.equals( cFormattbody_beyaz  ) ){
                                        aktif_stil = cFormattbody_gri;
                                    } else {
                                        aktif_stil = cFormattbody_beyaz;
                                    }
                                }
                                label = new Label(0, row, res.getString("oto"), aktif_stil);
                                excelSheet.addCell(label);

                                label = new Label(1, row, res.getString("ruhsat_plaka"), aktif_stil);
                                excelSheet.addCell(label);

                                label = new Label(2, row, res.getString("plaka"), aktif_stil);
                                excelSheet.addCell(label);

                                label = new Label(3, row, res.getString("surucu"), aktif_stil);
                                excelSheet.addCell(label);

                                label = new Label(4, row, res.getString("tip"), aktif_stil);
                                excelSheet.addCell(label);

                                label = new Label(5, row, res.getString("kart_okutma_saati"), aktif_stil);
                                excelSheet.addCell(label);

                                label = new Label(6, row, res.getString("tarih"), aktif_stil);
                                excelSheet.addCell(label);

                                row++;
                                son_oto = res.getString("oto");
                                son_stil = aktif_stil;
                            }
                        }

                        // sürücü isimleri sıgmıyor, o yuzden genislet col u
                        CellView cv = excelSheet.getColumnView(3);
                        cv.setSize(8200);
                        excelSheet.setColumnView(3, cv);

                        cv = excelSheet.getColumnView(1);
                        cv.setSize(4000);
                        excelSheet.setColumnView(1, cv);

                        cv = excelSheet.getColumnView(2);
                        cv.setSize(4000);
                        excelSheet.setColumnView(2, cv);


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
                    listener.on_refresh();
                }
            }
        });
        th.setDaemon(true);
        th.start();

    }


}
