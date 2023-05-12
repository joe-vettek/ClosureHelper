package com.xueluoanping.arknights.custom;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

public class TextImageTransformation extends BitmapTransformation {

    private static final String TAG = TextImageTransformation.class.getSimpleName();
    private String text = null;
    private String textInfo = null;
    private final int paintColor;
    private boolean smallInfo = false;

    public TextImageTransformation(String a, int paint1, boolean smallInfo) {
        this.text = a;
        this.paintColor = paint1;
        this.smallInfo = smallInfo;
    }

    public TextImageTransformation(String a, String b, int paint1, boolean smallInfo) {
        this.text = a;
        this.textInfo = b;
        this.paintColor = paint1;
        this.smallInfo = smallInfo;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {

        // boolean Smaller = false;
        // if (toTransform.getPixel(0, 0) != Color.TRANSPARENT) {
        //     toTransform = SmallPictureTransformation.transformFull(toTransform);
        //     // Smaller = true;
        // }


        Canvas canvas = new Canvas(toTransform);
        Paint paint = new Paint();
        paint.setColor(paintColor);
        float textSize = (toTransform.getWidth() / 5f);
        // if (small)
        //     textSize = textSize / 1.5f;

        paint.setTextSize(textSize);
        float length_text = paint.measureText(text);
        float posx = toTransform.getWidth() - length_text - 10;
        float posy = toTransform.getHeight() - 10;

        TextPaint mTextStrokePaint = new TextPaint();
        mTextStrokePaint.setTextSize(textSize);

        mTextStrokePaint.setColor(Color.WHITE);
        mTextStrokePaint.setStyle(Paint.Style.STROKE);
        // 这个要根据图片要算大小，不然随着分辨率的变化描边也会有问题
        mTextStrokePaint.setStrokeWidth(textSize/3);
        // if (Smaller) mTextStrokePaint.setStrokeWidth(6);
        // else {
        //     mTextStrokePaint.setStrokeWidth(12);
        // }

        // mTextStrokePaint.setDither(true);
        // mTextStrokePaint.setAntiAlias(true);

        // mTextStrokePaint.setTextAlign(Paint.Align.CENTER);

        canvas.save();
        canvas.drawText(text, posx, posy, mTextStrokePaint);
        canvas.drawText(text, posx, posy, paint);
        canvas.restore();

        canvas.save();
        if (this.textInfo != null) {
            if (smallInfo) {
                mTextStrokePaint.setTextSize(mTextStrokePaint.getTextSize() / 1.8f);
                paint.setTextSize(paint.getTextSize() / 1.8f);
            }
            posy = toTransform.getHeight() / 2f + 5;
            length_text = paint.measureText(textInfo);
            posx = (toTransform.getWidth() - length_text) / 2f;
            canvas.drawText(textInfo, posx, posy, mTextStrokePaint);
            canvas.drawText(textInfo, posx, posy, paint);
        }
        canvas.restore();

        return toTransform;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

    }
}
