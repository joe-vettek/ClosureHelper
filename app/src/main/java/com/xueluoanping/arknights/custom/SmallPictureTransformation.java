package com.xueluoanping.arknights.custom;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

public class SmallPictureTransformation extends BitmapTransformation {

    public static Bitmap transformFull(@NonNull Bitmap toTransform) {
        if (toTransform.getPixel(0, 0) != Color.TRANSPARENT)
      {
            int x = toTransform.getWidth();
            int y = toTransform.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(x + 20, y + 20, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            // paint.setColor(Color.TRANSPARENT);
            // canvas.drawRect(0f,0f,x+20f,y+20f,paint);
            canvas.drawBitmap(toTransform, 10, 10, paint);

            toTransform=bitmap;
        }
        return toTransform;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        //创建了一个 400*400 的 ARGB_8888 类型的空白位图对象
        // int x = toTransform.getWidth();
        // int y = toTransform.getHeight();
        // Bitmap bitmap = Bitmap.createBitmap(x + 20, y + 20, Bitmap.Config.ARGB_8888);
        // Canvas canvas = new Canvas(bitmap);
        // Paint paint = new Paint();
        // // paint.setColor(Color.TRANSPARENT);
        // // canvas.drawRect(0f,0f,x+20f,y+20f,paint);
        // canvas.drawBitmap(toTransform, 10, 10, paint);
        return transformFull(toTransform);
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

    }
}
