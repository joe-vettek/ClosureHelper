package com.xueluoanping.arknights.custom;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

public class TextImageTransformation extends BitmapTransformation {

    private static final String TAG = TextImageTransformation.class.getSimpleName();
    private String text;
    private final int paintColor;
    private boolean small = false;

    public TextImageTransformation(String a, int paint1, boolean small) {
        this.text = a;
        this.paintColor = paint1;
        this.small = small;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        Canvas canvas = new Canvas(toTransform);
        // canvas.scale(canvas.getWidth() * 1.5f, canvas.getHeight() * 1.5f);
        // canvas.setBitmap(toTransform);

        // Log.d(TAG, "transform: "+paintColor);
        Paint paint = new Paint();
        paint.setColor(paintColor);
        float textSize = (toTransform.getHeight() / 5f);
        if (small)
           textSize=textSize / 1.5f;

        paint.setTextSize(textSize);
        float length_text = paint.measureText(text);
        float posx = toTransform.getWidth() - length_text - 10;
        float posy = toTransform.getHeight() - 10;

        TextPaint mTextStrokePaint = new TextPaint();
        mTextStrokePaint.setTextSize(textSize);

        mTextStrokePaint.setColor(Color.WHITE);
        mTextStrokePaint.setStyle(Paint.Style.STROKE);
        if (small) mTextStrokePaint.setStrokeWidth(6);
        else {
            mTextStrokePaint.setStrokeWidth(12);
        }
        mTextStrokePaint.setDither(true);
        mTextStrokePaint.setAntiAlias(true);

        // mTextStrokePaint.setTextAlign(Paint.Align.CENTER);

        canvas.save();
        canvas.drawText(text, posx, posy, mTextStrokePaint);
        // canvas.translate(x + 45, transY);
        // paint.setColor(Color.DKGRAY);
        // canvas.drawRoundRect(posx - 5, posy - 50, toTransform.getWidth(), toTransform.getHeight(), 5f, 5f, paint);
        // paint.setColor(paintColor);
        canvas.drawText(text, posx, posy, paint);
        canvas.restore();


        // canvas.
        // Bitmap whiteBgBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);

        // Canvas canvas = new Canvas(whiteBgBitmap);

        // canvas.drawColor(Color.WHITE);

        // canvas.drawBitmap(toTransform, 0, 0, null);


        // Bitmap.cre
        return toTransform;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

    }
}
