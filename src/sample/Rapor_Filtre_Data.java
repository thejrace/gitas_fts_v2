package sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jeppe on 15.03.2017.
 */
public class Rapor_Filtre_Data {

    private Map<String, ArrayList<String>> data = new HashMap<>();

    // clone
    public Rapor_Filtre_Data( Rapor_Filtre_Data eski ){
        for (Map.Entry<String, ArrayList<String>> entry : eski.get().entrySet()) {
           ekle( entry.getKey(), entry.getValue());
        }
    }

    public Rapor_Filtre_Data(){

    }

    public void ekle( String key, ArrayList<String> array ){
        if( !data.containsKey(key) ) data.put( key, array );
    }

    public void ekle( String key, String[] vals ){
        if( !data.containsKey(key) ) data.put( key, new ArrayList<String>() );
        for( int x = 0; x < vals.length; x++ ) {
            data.get(key).add( vals[x] );
        }
    }

    public Map<String, ArrayList<String>> get(){
        return data;
    }

}
