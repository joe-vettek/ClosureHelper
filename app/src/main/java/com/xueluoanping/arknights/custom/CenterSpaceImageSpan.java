package com.xueluoanping.arknights.custom;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;


/**
 * 垂直居中ImageSpan，支持margin间距设置
 * <p>
 *
 * @author yinxuming
 * @date 2020/4/2
 */
public class CenterSpaceImageSpan extends ImageSpan {
    private final int mMarginLeft;
    private final int mMarginRight;

    public CenterSpaceImageSpan(Drawable drawable) {
        this(drawable, 0, 0);

    }

    public CenterSpaceImageSpan(Drawable drawable,
                                int marginLeft, int marginRight) {
        super(drawable);
        mMarginLeft = marginLeft;
        mMarginRight = marginRight;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x,
                     int top, int y, int bottom,
                     @NonNull Paint paint) {

        Drawable b = getDrawable();
        Paint.FontMetricsInt fm = paint.getFontMetricsInt();
        x = mMarginLeft + x;
        int transY = (y + fm.descent + y + fm.ascent) / 2 - b.getBounds().bottom / 2;
        canvas.save();
        canvas.translate(x, transY);
        b.draw(canvas);
        canvas.restore();
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable
            Paint.FontMetricsInt fm) {
        //        Drawable d = getCachedDrawable();
        //        Rect rect = d.getBounds();
        //
        //        if (fm != null) {
        //            fm.ascent = -rect.bottom;
        //            fm.descent = 0;
        //
        //            fm.top = fm.ascent;
        //            fm.bottom = 0;
        //        }
        //
        //        return mMarginLeft + rect.right + mMarginRight;
        return mMarginLeft + super.getSize(paint, text, start, end, fm) + mMarginRight;
    }

    private Drawable getCachedDrawable() {
        WeakReference<Drawable> wr = mDrawableRef;
        Drawable d = null;

        if (wr != null) {
            d = wr.get();
        }

        if (d == null) {
            d = getDrawable();
            mDrawableRef = new WeakReference<Drawable>(d);
        }

        return d;
    }

    private WeakReference<Drawable> mDrawableRef;
}