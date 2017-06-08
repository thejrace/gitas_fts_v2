package sample;

/**
 * Created by Jeppe on 17.03.2017.
 */
public class KeyValue_Pair {
    private final String key;
    private final String value;
    public KeyValue_Pair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey()   {    return key;    }

    public String toString() {    return value;  }
}
