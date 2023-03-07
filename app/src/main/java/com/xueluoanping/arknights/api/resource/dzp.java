package com.xueluoanping.arknights.api.resource;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.xueluoanping.arknights.R;
import com.xueluoanping.arknights.SimpleApplication;
import com.xueluoanping.arknights.api.main.Data;
import com.xueluoanping.arknights.api.tool.ToolFile;
import com.xueluoanping.arknights.api.tool.ToolTable;
import com.xueluoanping.arknights.api.tool.ToolTheme;
import com.xueluoanping.arknights.api.tool.ToolTime;
import com.xueluoanping.arknights.custom.TextImageTransformation;
import com.xueluoanping.arknights.pro.SimpleTool;

import java.io.File;

public class dzp {
    private static final String TAG = dzp.class.getSimpleName();

    public static String getSecretarySkinUrl(String id) {
        return String.format(url_secretarySkin, id);
    }

    private static final String url_secretarySkin = "https://ak.dzp.me/dst/avatar/ASSISTANT/%s.webp";
    private static final String url_ItemIcon = "http://ak.dzp.me/dst/items/%s.webp";

    public static String replaceNetworkDivide(String x) {
        return x.replaceAll("[@#]", "_");
    }

    public static void loadImageIntoImageview(Data.AccountData data0, Activity context, ImageView imageView) {
        if (context != null && !context.isDestroyed()) {
            context.runOnUiThread(() -> {
                String secretarySkinIdDec = dzp.replaceNetworkDivide(data0.secretarySkinId);
                String PicUrl = dzp.getSecretarySkinUrl(secretarySkinIdDec);
                // Log.d(TAG, "loadImageIntoImageview: "+PicUrl);
                final String PicUrl2 = dzp.getSecretarySkinUrl(data0.secretary);
                // options= options.apply();

                RequestOptions finalOptions = RequestOptions.bitmapTransform(new MultiTransformation<>(
                        new CircleCrop(),
                        new TextImageTransformation("Lv." + data0.level, ToolTheme.getColorValue(context, R.attr.colorAccent), false)));
                Glide.with(SimpleApplication.getContext())
                        .load(PicUrl)
                        .transition(withCrossFade())
                        .apply(finalOptions)
                        // .apply(new RequestOptions().transform(new TextImageTransformation("Lv."+data0.level, ToolTheme.getColorValue(context, R.attr.colorAccent))))
                        .into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                if (resource.getIntrinsicWidth() > 5)
                                    imageView.setImageDrawable(resource);
                                else Glide.with(SimpleApplication.getContext())
                                        .load(PicUrl2)
                                        .transition(withCrossFade())
                                        .error(Glide.with(context).load(R.mipmap.npc_007_closure))
                                        .apply(finalOptions)
                                        // .apply
                                        .into(imageView);
                            }
                        });
            });

        }
    }

    public static String getItemIconUrl(String name) {
        try {
            return String.format(url_ItemIcon, ToolTable.getInstance().getItemNameTable().get(SimpleTool.getUUID(name).toString()).getAsString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getItemIconUrlById(String id) {
        return String.format(url_ItemIcon, id);

    }


}
