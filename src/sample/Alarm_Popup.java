package sample;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Obarey on 14.02.2017.
 */
public class Alarm_Popup {

    private Popup popup = null;
    private Node parent;
    private VBox alarm_ul = new VBox();
    private double last_timestamp = 0;
    private boolean run = true;

    private AnchorPane content_container;
    private Map<String, Alarm_Goruldu_Listener> listeners = new HashMap<>();

    private Map<String, ArrayList<Alarm_Data>> alarmlar = new HashMap<>();

    public Alarm_Popup( Node parent_node ){
        parent = parent_node;
    }

    public  Map<String, ArrayList<Alarm_Data>> get_son_alarmlar(){
        return alarmlar;
    }

    private void set_content(){
        alarm_ul.getChildren().clear();
        alarm_ul = new VBox();
        alarm_ul.setAlignment(Pos.TOP_CENTER);
        alarm_ul.setMaxHeight( Common.get_screen_res().get("H") - 100 );
        alarm_ul.setPrefHeight( Common.get_screen_res().get("H") - 100 );
        Alarm_Box alarm_box;
        int index = 0;
        for (Map.Entry<String, ArrayList<Alarm_Data>> entry : alarmlar.entrySet()) {
            try {

                alarm_box = new Alarm_Box(index, entry.getValue(), new Alarm_Goruldu_Listener() {
                    @Override
                    public void goruldu_yap(String key) {
                        System.out.println( key + " --- ALARM GÖRÜLDÜ YAP! 222");
                        listeners.get(entry.getKey()).goruldu_yap( key );
                    }
                });
                alarm_ul.getChildren().add( alarm_box );
                index++;
            } catch( NullPointerException e ){
                e.printStackTrace();
            }
        }
        content_container.getChildren().add( alarm_ul );
    }

    public void init(){

        content_container = new AnchorPane();
        content_container.getStyleClass().add("alarm-popup");
        content_container.getStylesheets().add(Obarey_Popup.class.getResource("resources/css/common.css").toExternalForm());

        popup = new Popup();
        popup.getContent().add( content_container );
        popup.setAutoHide(false);
        popup.setHideOnEscape(true);
        popup.setHeight( Common.get_screen_res().get("H") - 100 );

        show_interval_start();
    }

    public synchronized void alarm_ekle( String otobus_kod, ArrayList<Alarm_Data> yeni_alarmlar, Alarm_Goruldu_Listener listener ){
        alarmlar.put( otobus_kod, yeni_alarmlar );
        if( !listeners.containsKey(otobus_kod)) listeners.put( otobus_kod, listener );
    }

    public void show_interval_start(){
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                while( run ){
                    if( !alarmlar.isEmpty() ){
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                set_content();
                                popup.show( parent, 20, 20 );
                                if( User_Config.app_ayarlar.getInt("alarm_kaybolma_frekans") > 0 ){
                                    final ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
                                    exec.schedule(new Runnable() {
                                        @Override
                                        public void run() {
                                            Platform.runLater(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        popup.hide();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                        }
                                    }, User_Config.app_ayarlar.getInt("alarm_kaybolma_frekans"), TimeUnit.SECONDS);
                                    exec.shutdown();
                                }
                            }
                        });
                    }
                    try {
                        Thread.sleep(User_Config.app_ayarlar.getInt("alarm_frekans") * 1000 );
                    } catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        th.setDaemon(true);
        th.start();

    }



    /*public void show_old( Map<String, ArrayList<Alarm_Data>> alarmlar ){
        this.alarmlar = alarmlar;
        Thread alarm_popup_th = new Thread( new Task<String>(){
            @Override
            protected void succeeded() {
                popup.show( parent, 20, 20 );
                final ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
                exec.schedule(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        popup.hide();
                                        popup = null;
                                    } catch( Exception e ){
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 20, TimeUnit.SECONDS);
                exec.shutdown();
            }

            @Override
            protected String call(){
                init();
                return null;
            }

        });
        alarm_popup_th.setDaemon(true);
        alarm_popup_th.start();
    }*/


    /*private void set_content( Map<String, ArrayList<Alarm_Data>> alarmlar ){
        alarm_ul.getChildren().clear();
        alarm_ul = new VBox();
        alarm_ul.setAlignment(Pos.TOP_CENTER);
        HBox alarm_li;
        String style_class;
        for( Alarm_Data alarm : alarmlar ){
            alarm_li = new HBox();
            alarm_li.getStyleClass().add("alarm-li");
            style_class = "mavi";

            if( alarm.get_type() == Alarm_Data.GEC_KALMA ){
                style_class = "turuncu";
            } else if( alarm.get_type() == Alarm_Data.AMIR_SAAT_ATADI ){
                style_class = "mavi";
            } else if( alarm.get_type() == Alarm_Data.SEFER_BASLAMADI ){
                style_class = "turuncu";
            } else if( alarm.get_type() == Alarm_Data.SEFER_IPTAL ){
                style_class = "kirmizi";
            } else if( alarm.get_type() == Alarm_Data.SEFER_YARIM ){
                style_class = "kirmizi";
            } else if( alarm.get_type() == Alarm_Data.SEFERLER_DUZELTILDI ){
                style_class = "yesil";
            }

            Label lbl_mesaj = new Label( alarm.get_kod() + " " + alarm.get_mesaj() + " [ "+alarm.get_tarih()+" ] ");
            lbl_mesaj.getStyleClass().addAll("alarm-label", "alarm-label-" + style_class );
            ImageView img = new ImageView( new Image( getClass().getResource("resources/img/ico_alarm_"+style_class+".png").toExternalForm() ));
            alarm_li.getChildren().addAll( img, lbl_mesaj );
            try {
                alarm_ul.getChildren().add( alarm_li );
            } catch( Exception e ){
                e.printStackTrace();
            }
        }


    }*/



    /*public boolean is_on(){
        return popup != null && popup.ison();
    }*/



    /*public void show(  Map<String, ArrayList<Alarm_Data>> data  ){
        if( data.size() == 0 ){
            //System.out.println("veri yok alarm");
            return;
        } else {
            //System.out.println("alarm size: " + data.size());
        }
        // @todo bu thread i bitirecez kaliyo mal gibi u bitirecez
        Thread alarm_popup_th = new Thread( new Task<String>(){
            @Override
            protected void succeeded() {
                if (popup == null) {
                    popup = new Obarey_Popup(title, parent);
                    popup.init(draggable);
                }
                popup.set_content(alarm_ul);
                popup.show(x, y);

                // otobus boxtan alarm secildiyse oto kapatmayi kullanmiyoruz
                if (!tekli){
                    final ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
                    exec.schedule(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            popup.hide();
                                            popup = null;
                                        } catch( Exception e ){
                                            e.printStackTrace();
                                        }


                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 20, TimeUnit.SECONDS);
                    exec.shutdown();
                }
            }

            @Override
            protected String call(){

                set_content( data );
                return null;
            }

        });
        alarm_popup_th.setDaemon(true);
        alarm_popup_th.start();

    }*/



}

