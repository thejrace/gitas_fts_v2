package sample;

import jxl.Workbook;
import jxl.format.*;
import jxl.write.*;
import jxl.write.Border;
import jxl.write.BorderLineStyle;
import jxl.write.Colour;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Obarey on 22.02.2017.
 */
public class Excel_Eksefer_Onerileri {

    private ArrayList<Oneri_Sefer_Data> d1, d2, d3;
    private WritableWorkbook myFirstWbook = null;
    private WritableSheet excelSheet = null;
    public Excel_Eksefer_Onerileri(ArrayList<Oneri_Sefer_Data> a_data, ArrayList<Oneri_Sefer_Data> b_data, ArrayList<Oneri_Sefer_Data> c_data ){
        d1 = a_data;
        d2 = b_data;
        d3 = c_data;
    }

    public void bolge_tema( int c, String bolge, ArrayList<Oneri_Sefer_Data> data ){

        try {
            Label label;
            WritableCellFormat cFormat = new WritableCellFormat();
            WritableFont font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
            cFormat.setFont(font);
            cFormat.setAlignment(jxl.format.Alignment.CENTRE);


            label = new Label(c+1, 0, bolge + " BÖLGESİ", cFormat);
            excelSheet.addCell(label);

            label = new Label(c, 1, "OTO", cFormat);
            excelSheet.addCell(label);

            label = new Label(c+1, 1, "HAT - GÜZ.", cFormat);
            excelSheet.addCell(label);

            label = new Label(c+2, 1, "ORER", cFormat);
            excelSheet.addCell(label);

            label = new Label(c+3, 1, "DKODU", cFormat);
            excelSheet.addCell(label);

            WritableFont fontbody = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD);
            WritableFont fontbody_bold = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
            int row = 2;
            WritableCellFormat cFormattbody, cFormatotobus_body;
            cFormattbody = new WritableCellFormat();
            cFormattbody.setBorder(Border.BOTTOM, BorderLineStyle.THIN, Colour.GRAY_50);
            cFormattbody.setFont(fontbody_bold);
            cFormattbody.setAlignment(jxl.format.Alignment.CENTRE);
            cFormattbody.setBackground(Colour.YELLOW);

            cFormatotobus_body = new WritableCellFormat();
            //cFormatotobus_body.setBorder(Border.BOTTOM, BorderLineStyle.THIN, Colour.GRAY_50);
            cFormatotobus_body.setFont(fontbody);
            cFormatotobus_body.setAlignment(jxl.format.Alignment.CENTRE);
            cFormatotobus_body.setBackground(Colour.WHITE);

            WritableCellFormat cFormatotobus_bold_body = new WritableCellFormat();
            //cFormatotobus_body.setBorder(Border.BOTTOM, BorderLineStyle.THIN, Colour.GRAY_50);
            cFormatotobus_bold_body.setFont(fontbody_bold);
            cFormatotobus_bold_body.setAlignment(jxl.format.Alignment.CENTRE);
            cFormatotobus_bold_body.setBackground(Colour.WHITE);

            for( Oneri_Sefer_Data sefer_data : data ){

                label = new Label(c, row, sefer_data.get_oto(), cFormattbody);
                excelSheet.addCell(label);

                label = new Label(c+1, row, sefer_data.get_guzergah(), cFormattbody);
                excelSheet.addCell(label);

                label = new Label(c+2, row, sefer_data.get_orer(), cFormattbody);
                excelSheet.addCell(label);

                label = new Label(c+3, row, sefer_data.get_dkodu(), cFormattbody);
                excelSheet.addCell(label);

                row++;

                for( Otobus_Data otobus_data : sefer_data.get_otolar() ){
                    WritableCellFormat dn_format;
                    if( sefer_data.get_guzergah().equals( otobus_data.get_guz() ) ){
                        dn_format = cFormatotobus_bold_body;
                    } else {
                        dn_format = cFormatotobus_body;
                    }

                    label = new Label(c, row, otobus_data.get_oto(), dn_format);
                    excelSheet.addCell(label);

                    label = new Label(c+1, row, otobus_data.get_guz(), dn_format);
                    excelSheet.addCell(label);

                    label = new Label(c+2, row, otobus_data.get_son_orer(), dn_format);
                    excelSheet.addCell(label);

                    label = new Label(c+3, row, "", dn_format);
                    excelSheet.addCell(label);

                    row++;
                }
                row++;
            }



        }  catch (WriteException e) {
            e.printStackTrace();
        }

    }

    public void action(){



        try {

            String tarih = Common.get_current_date();

            myFirstWbook = Workbook.createWorkbook(new File("C:\\temp\\EK_SEFER_ONERI_Tablo_"+tarih+".xls"));
            excelSheet = myFirstWbook.createSheet("EK Sefer Öneri Tablosu", 0);

            myFirstWbook.setColourRGB(Colour.YELLOW, 242, 229, 183);


            bolge_tema(0, "A", d1 );
            bolge_tema(5, "B", d2 );
            bolge_tema(10, "C", d3 );

            myFirstWbook.write();

        }  catch (IOException e) {
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

    }

}
