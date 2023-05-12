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
            return getItemIconUrlById(ToolTable.getInstance().getItemNameTable().get(SimpleTool.getUUID(name).toString()).getAsString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getItemIconUrlById(String id) {
        switch (id) {
            case "act24side_melding_5":
                return "https://prts.wiki/images/6/67/%E9%81%93%E5%85%B7_%E5%B8%A6%E6%A1%86_%E2%80%9C%E5%85%BD%E4%B9%8B%E6%B3%AA%E2%80%9D.png";
            case "act24side_melding_4":
                return "https://prts.wiki/images/3/3a/%E9%81%93%E5%85%B7_%E5%B8%A6%E6%A1%86_%E5%87%B6%E8%B1%95%E5%85%BD%E7%9A%84%E5%8E%9A%E5%AE%9E%E7%9A%AE.png";
            case "act24side_melding_3":
                return "https://prts.wiki/images/1/1e/%E9%81%93%E5%85%B7_%E5%B8%A6%E6%A1%86_%E9%AC%A3%E7%8A%84%E5%85%BD%E7%9A%84%E5%B0%96%E9%94%90%E9%BD%BF.png";
            case "act24side_melding_2":
                return "https://prts.wiki/images/5/5a/%E9%81%93%E5%85%B7_%E5%B8%A6%E6%A1%86_%E6%BA%90%E7%9F%B3%E8%99%AB%E7%9A%84%E7%A1%AC%E5%A3%B3.png";
            case "act24side_melding_1":
                return "https://prts.wiki/images/8/82/%E9%81%93%E5%85%B7_%E5%B8%A6%E6%A1%86_%E7%A0%B4%E7%A2%8E%E7%9A%84%E9%AA%A8%E7%89%87.png";
            default:  return String.format(url_ItemIcon, id);
        }

        // return String.format(url_ItemIcon, id);

    }


}
