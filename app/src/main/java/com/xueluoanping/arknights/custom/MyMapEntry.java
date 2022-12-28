package com.xueluoanping.arknights.custom;

import java.util.Map;

public class MyMapEntry implements Map.Entry {
    private Object key;
    private Object value;



    @Override
    public Object getKey() {
        return this.key;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public Object setValue(Object o) {
        this.value = o;
        return this.value;
    }


    public Object setKey(Object o) {
        this.key = o;
        return this.key;
    }
}
