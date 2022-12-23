package com.xueluoanping.arknights.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.xueluoanping.arknights.R;

import java.lang.ref.WeakReference;
import java.security.MessageDigest;

public class NoBitmapTransformation extends BitmapTransformation {

    private WeakReference<Context> contextWeakReference;
    public NoBitmapTransformation(Context context){
        contextWeakReference=new WeakReference<>(context);
    }


    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        if(toTransform.getByteCount() < 10&&contextWeakReference.get()!=null)
        {
            return drawableToBitamp(contextWeakReference.get().getResources().getDrawable(R.mipmap.npc_007_closure));
        }
        return toTransform;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

    }

    private Bitmap drawableToBitamp(Drawable drawable)
    {
        //声明将要创建的bitmap
        Bitmap bitmap = null;
        //获取图片宽度
        int width = drawable.getIntrinsicWidth();
        //获取图片高度
        int height = drawable.getIntrinsicHeight();
        //图片位深，PixelFormat.OPAQUE代表没有透明度，RGB_565就是没有透明度的位深，否则就用ARGB_8888。详细见下面图片编码知识。
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        //创建一个空的Bitmap
        bitmap = Bitmap.createBitmap(width,height,config);
        //在bitmap上创建一个画布
        Canvas canvas = new Canvas(bitmap);
        //设置画布的范围
        drawable.setBounds(0, 0, width, height);
        //将drawable绘制在canvas上
        drawable.draw(canvas);
        return bitmap;
    }

}
