package com.xueluoanping.arknights.custom;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class NoLongClickCrashTextView extends androidx.appcompat.widget.AppCompatTextView {
    public NoLongClickCrashTextView(Context context) {
        super(context);
    }

    public NoLongClickCrashTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NoLongClickCrashTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // 小米系统下不判断mEditor是否为空，而由于是私有变量，反射读取也需要catch异常因此直接做异常捕捉
    @Override
    public boolean performLongClick() {
        boolean result=false;
        try {
            result=super.performLongClick();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // @Override
    // public boolean performLongClick(float x, float y) {
    //     return super.performLongClick(x, y);
    // }
}
