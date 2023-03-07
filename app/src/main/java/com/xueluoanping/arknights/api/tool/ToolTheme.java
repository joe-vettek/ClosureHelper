package com.xueluoanping.arknights.api.tool;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.ColorInt;

import com.xueluoanping.arknights.R;
import com.xueluoanping.arknights.SimpleApplication;

public class ToolTheme {
    private static final String TAG = ToolTheme.class.getSimpleName() ;

    // 这里需要额外引入context，因为这个值是主题，需要设置的
    public static boolean getBooleanValue(Context context, int attrId) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme =context.getTheme();
        theme.resolveAttribute(attrId, typedValue, true);
        return !(typedValue.data == 0);
    }

    public static int getColorValue(Context context,int attrId) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(attrId, typedValue, true);
        @ColorInt int color = typedValue.data;
        // Log.d(TAG, "getColorValue: "+color);
        return color;
    }

    public static int getBackgroundColorValue(View view) {
        int color0 = Color.TRANSPARENT;
        Drawable background = view.getBackground();
        if (background instanceof ColorDrawable)
            color0 = ((ColorDrawable) background).getColor();
        return color0;
    }
}
