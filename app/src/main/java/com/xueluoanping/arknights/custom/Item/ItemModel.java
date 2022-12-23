package com.xueluoanping.arknights.custom.Item;


import com.xueluoanping.arknights.pro.HttpConnectionUtil;

import java.io.Serializable;


public class ItemModel implements Serializable {
    private static final String TAG = ItemModel.class.getSimpleName();
    public String key= HttpConnectionUtil.empty;
    public String text= HttpConnectionUtil.empty;
}
