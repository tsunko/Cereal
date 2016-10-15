package ai.seitok.natsuba.cereal;

import java.util.HashMap;
import java.util.Map;

public class NoDupeHashMap<K, V> extends HashMap<K, V> {

    @Override
    public boolean replace(K key, V oldValue, V newValue){
        throw new UnsupportedOperationException("no duplicates allowed");
    }

    @Override
    public V put(K key, V value){
        if(containsKey(key)){
            throw new UnsupportedOperationException("no duplicates allowed");
        }

        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m){
        m.entrySet().stream().filter(entry -> containsKey(entry.getKey())).forEach(e ->
                put(e.getKey(), e.getValue())
        );
    }

}
