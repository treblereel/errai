package org.jboss.errai.polymer.shared;

import java.util.Map;

/**
 * Created by treblereel on 3/13/17.
 */
public class Tuple<K, V> implements Map.Entry<K, V> {
    private K key;

    private V value;

    public Tuple(K key, V value){
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
    public V setValue(V value) {
        V old = value;
        this.value = value;
        return old;
    }

}
