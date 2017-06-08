package sample;

import org.json.JSONObject;

/**
 * Created by Jeppe on 18.05.2017.
 */
public class Filo_Download implements Runnable {

    private Thread thread;
    private boolean aktif = true;
    private Filo_Download_Listener listener;
    public void start(){
        if( thread == null ){
            thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();
        }
    }

    public void add_listener( Filo_Download_Listener listener ){
        this.listener = listener;
    }


    public void run(){

        while( aktif ){
            Web_Request request = new Web_Request(Web_Request.SERVIS_URL, "&req=filo_download");
            request.kullanici_pc_parametreleri_ekle(true);
            request.action();
            JSONObject output = new JSONObject( request.get_value() );
            listener.on_download_finish( output.getJSONObject("data") );

            try{
                Thread.sleep(60000);
            } catch( InterruptedException e ){
                e.printStackTrace();
            }
        }





    }


}
