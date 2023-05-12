package com.xueluoanping.arknights.api.tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.xueluoanping.arknights.R;
import com.xueluoanping.arknights.api.resource.prts;

public class ToolGlide {
    public static RequestBuilder<Drawable> pic(Context context) {
        return Glide.with(context).load(R.mipmap.pic_holder);
    }

    public static RequestBuilder<Bitmap> pic2(Context context) {
        return Glide.with(context).asBitmap().load(R.mipmap.pic_holder);
    }

    public static RequestBuilder<Drawable> errorNoImage(Context context, RequestOptions options) {
       return Glide.with(context).load(prts.noImaginePngUrl).error(ToolGlide.pic(context)).apply(options);
    }

    public static RequestBuilder<Bitmap> errorNoImage2(Context context, RequestOptions options) {
        return Glide.with(context).asBitmap().load(prts.noImaginePngUrl).error(ToolGlide.pic2(context)).apply(options);
    }
}
