package sample;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.apache.commons.codec.digest.DigestUtils;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Obarey on 05.02.2017.
 */
public class Common {

    public static String mac_hash(){
        try {
            String command = "ipconfig /all";
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader inn = new BufferedReader(new InputStreamReader(p.getInputStream()));
            Pattern pattern = Pattern.compile(".*Physical Addres.*: (.*)");
            StringBuilder sb = new StringBuilder();
            while (true) {
                String line = inn.readLine();
                if (line == null)
                    break;
                Matcher mm = pattern.matcher(line);
                if (mm.matches()) {
                    //System.out.println(mm.group(1));
                    sb.append(mm.group(1));
                }
            }
            return DigestUtils.sha256Hex(sb.toString());
        } catch( IOException e ){
            e.printStackTrace();
            return null;
        }
    }

    public static LocalDate dp_placeholder(String dateString){
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.parse(dateString, formatter);
            return localDate;
        } catch( DateTimeException e ){
            e.printStackTrace();
        }
        return null;
    }


    public static String bilgisayar_adini_al(){
        String hostname = "Bilinmiyor";
        try {
            InetAddress addr;
            addr = InetAddress.getLocalHost();
            hostname = addr.getHostName();
        } catch (UnknownHostException ex)        {
            System.out.println("Bilgisayar adi alinamadi.");
        }
        return hostname;
    }

    public static boolean dt_gecmis( String d1, String d2 ){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date t1 = sdf.parse(d1);
            Date t2 = sdf.parse(d2);
            if( t1.compareTo(t2) == 0 || t1.compareTo(t2) == 1 ){
                return true;
            } else {
                return false;
            }
        } catch(ParseException e ){
            e.printStackTrace();
        }
        return false;
    }

    public static String rev_datetime( String dt ){
        String date = dt.substring(0, 10);
        String[] exp = date.split("-");
        return exp[2]+"-"+exp[1]+"-"+exp[0]+ " " + dt.substring(11);
    }

    public static String rev_date( String dt ){
        String[] exp = dt.split("-");
        return  exp[2]+"-"+exp[1]+"-"+exp[0];
    }

    public static String iys_to_date( String iys_tarih ){
        String[] exp = iys_tarih.split("\\.");
        return "20"+exp[2]+"-"+exp[1]+"-"+exp[0];
    }

    public static int result_count( ResultSet res ){
        try {
            res.last();
            int count = res.getRow();
            res.beforeFirst();
            return count;
        } catch( SQLException e ){
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean is_numeric( String val ){
        return val.matches("\\d+");
    }

    public static String hat_kod_sef( String hat_kodu ){
        if( hat_kodu.indexOf("Ç") > 0 ) hat_kodu = hat_kodu.replace("Ç", "C.");
        if( hat_kodu.indexOf("Ş") > 0 ) hat_kodu = hat_kodu.replace("Ş", "S.");
        if( hat_kodu.indexOf("Ü") > 0 ) hat_kodu = hat_kodu.replace("Ü", "U.");
        if( hat_kodu.indexOf("Ö") > 0 ) hat_kodu = hat_kodu.replace("Ö", "O.");
        if( hat_kodu.indexOf("İ") > 0 ) hat_kodu = hat_kodu.replace("İ", "I.");
        return hat_kodu;

    }

    public static String regex_trim( String str ){
        return str.replaceAll("\u00A0", "");
    }

    public static Map<String, Integer> get_screen_res(){
        Map<String, Integer> out = new HashMap<>();
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        out.put("W", width );
        out.put("H", height );
        return out;
    }

    public static long get_unix() {
        return (System.currentTimeMillis() / 1000L) - 3600;
    }

    public static String get_current_datetime(){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String get_current_date(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String get_current_hmin(){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        return dateFormat.format(date);

    }

    public static String get_yesterday_date() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return dateFormat.format(cal.getTime());
    }

    static class Delta { double x, y; }
    public static void make_draggable(Node node) {
        final Delta dragDelta = new Delta();
        node.setOnMousePressed(me -> {
            dragDelta.x = me.getX();
            dragDelta.y = me.getY();
        });
        node.setOnMouseDragged(me -> {
            node.setLayoutX(node.getLayoutX() + me.getX() - dragDelta.x);
            node.setLayoutY(node.getLayoutY() + me.getY() - dragDelta.y);
        });
    }

    public static void make_stage_draggable(final Stage stage, final Node byNode) {
        final Delta dragDelta = new Delta();
        byNode.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                // record a delta distance for the drag and drop operation.
                dragDelta.x = stage.getX() - mouseEvent.getScreenX();
                dragDelta.y = stage.getY() - mouseEvent.getScreenY();
            }
        });
        byNode.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                stage.setX(mouseEvent.getScreenX() + dragDelta.x);
                stage.setY(mouseEvent.getScreenY() + dragDelta.y);
            }
        });

    }

}
