package samurai.database;

import java.util.Map;

/**
 * @author TonTL
 * @version 4/5/2017
 */
public class Entry<K, V> implements Map.Entry {

    private K key;
    private V value;

    public Entry(K key, V value) {
        this.key = key;
        this.value = value;
    }


    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public Object setValue(Object value) {
        throw new UnsupportedOperationException();
    }
}
