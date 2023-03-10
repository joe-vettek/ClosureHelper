package com.xueluoanping.arknights.custom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.xueluoanping.arknights.R;
import com.xueluoanping.arknights.api.tool.ToolTheme;
import com.xueluoanping.arknights.pro.SimpleTool;
import com.xueluoanping.arknights.pro.spTool;

import java.lang.reflect.Field;

/**
 * 获取网络图片的ImageSpan
 * Created by Yomii on 2016/10/13.
 */
public class UrlImageSpan extends ImageSpan {

    private static final String TAG = UrlImageSpan.class.getSimpleName();
    private String url;
    private TextView tv;
    private boolean picShowed;
    private int amount;
    private int color;

    public UrlImageSpan(Context context, String url, TextView tv, int amount) {
        super(context, R.mipmap.pic_holder);
        this.url = url;
        this.tv = tv;
        this.amount = amount;

        // boolean a=ToolTheme.getBooleanValue(context,R.attr.isLightTheme);
        // 这无关紧要，不如考虑版本问题
        this.color =
                // Build.VERSION.SDK_INT>28
                // ?
                tv.getPaint().getColor();
        // :
        //            Color.BLACK;

        ;

    }

    @Override
    public Drawable getDrawable() {
        if (!picShowed) {
            RequestOptions options = new RequestOptions();

            String shortAmountText = SimpleTool.getShortAmountDescriptionText(amount);
            TextImageTransformation textImageTransformation=new TextImageTransformation(shortAmountText, color,false);
            options = options.transform(textImageTransformation);
            if (tv.getContext() instanceof Activity) {
                if (!((Activity) tv.getContext()).isDestroyed()) {
                    SimpleTarget<Drawable> target = new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            Resources resources = tv.getContext().getResources();
                            int targetWidth = (int) (resources.getDisplayMetrics().widthPixels * 0.8);

                            Bitmap zoom = zoom(SimpleTool.drawableToBitamp(resource), targetWidth);

                            BitmapDrawable b = new BitmapDrawable(resources, zoom);

                            b.setBounds(0, 0, 180, 180);
                            Field mDrawable;
                            Field mDrawableRef;
                            try {
                                mDrawable = ImageSpan.class.getDeclaredField("mDrawable");
                                mDrawable.setAccessible(true);
                                mDrawable.set(UrlImageSpan.this, b);

                                mDrawableRef = DynamicDrawableSpan.class.getDeclaredField("mDrawableRef");
                                mDrawableRef.setAccessible(true);
                                mDrawableRef.set(UrlImageSpan.this, null);

                                picShowed = true;
                                tv.setText(tv.getText());
                            } catch (IllegalAccessException | NoSuchFieldException e) {
                                e.printStackTrace();
                            }
                        }
                    };


                    RequestManager manager = Glide.with(tv.getContext());
                    RequestBuilder<Drawable> builder;

                    if (UrlImageSpan.this.url!=null&&UrlImageSpan.this.url.contains("http"))
                        builder = manager.load(url).apply(options);
                    else builder = manager.load("https://prts.wiki/images/f/fb/%E6%97%A0%E5%9B%BE%E7%89%87%E5%8D%A0%E4%BD%8D%E7%AC%A6.png")
                            .apply(new RequestOptions().transform(new MultiTransformation<>(new SmallPictureTransformation(),textImageTransformation)));
                    // 说明，加载本地图片似乎有严重的分辨率问题导致字体渲染效果不佳
                    // error处理不了也是因为处理不了drawable，这和Bitmap有区别，后续待改进一下
                    // apply只能apply一次
                    // 还有个问题是加载不了
                    // Log.d(TAG, "getDrawable: "+url);
                    builder
                            // error 部分需要单独设置requestOption
                            .error(Glide.with(tv.getContext()).load("https://prts.wiki/images/f/fb/%E6%97%A0%E5%9B%BE%E7%89%87%E5%8D%A0%E4%BD%8D%E7%AC%A6.png").error(Glide.with(tv.getContext()).load(R.mipmap.pic_holder)).apply(new RequestOptions().transform(new MultiTransformation<>(new SmallPictureTransformation(),textImageTransformation))))
                            .into(target);
            }

        }


    }
        return super.

    getDrawable();

}

    /**
     * 按宽度缩放图片
     *
     * @param bmp  需要缩放的图片源
     * @param newW 需要缩放成的图片宽度
     * @return 缩放后的图片
     */
    public static Bitmap zoom(@NonNull Bitmap bmp, int newW) {

        // 获得图片的宽高
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        // 计算缩放比例
        float scale = ((float) newW) / width;

        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);

        return newbm;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        super.draw(canvas, text, start, end, x, top, y, bottom, paint);
        canvas.save();
        paint.setTextSize(36);

        int transY = bottom;
        //碰到了再调试
        // if (mVerticalAlignment == ALIGN_BASELINE) {
        //     transY -= paint.getFontMetricsInt().descent;
        // } else if (mVerticalAlignment == ALIGN_CENTER) {
        //     transY = top + (bottom - top) / 2 - 120 / 2;
        // }

        canvas.translate(x + 45, transY);
        // canvas.drawText("" + amount, 0, 0, paint);
        canvas.restore();
    }
}

