package com.xueluoanping.arknights.api.tool;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
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

    public static SpannableString createColorText(CharSequence text,@ColorInt int color)
    {
        SpannableString spannableString0 = new SpannableString(text);

        ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(color);

        spannableString0.setSpan(colorSpan2, 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString0;
    }

    public static SpannableString createSizeText(CharSequence text,float relativeSize)
    {
        SpannableString spannableString0 = new SpannableString(text);

        spannableString0.setSpan(new RelativeSizeSpan(relativeSize),0, text.length(),  Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString0;
    }

    public static SpannableString createStyleText(CharSequence text,int fontStyle)
    {
        SpannableString spannableString0 = new SpannableString(text);

        spannableString0.setSpan(new StyleSpan(fontStyle),0, text.length(),  Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString0;
    }
}
