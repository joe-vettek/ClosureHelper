package com.xueluoanping.arknights.api;

import java.util.Map;

public class BetterEntry<K, V> implements Map.Entry<K, V> {
    private K key;
    private V value;

    public BetterEntry(K key, V value) {
        this.value = value;
        this.key = key;
    }

    public BetterEntry() {
    }

    @Override
    public K getKey() {
        return this.key;
    }

    @Override
    public V getValue() {
        return this.value;
    }


    public K setKey(K key) {
        this.key = key;
        return this.key;
    }

    @Override
    public V setValue(V value) {
        this.value = value;
        return this.value;
    }
}
