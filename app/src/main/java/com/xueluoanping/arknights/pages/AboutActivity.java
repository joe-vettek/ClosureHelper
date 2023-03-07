package com.xueluoanping.arknights.pages;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.xueluoanping.arknights.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


public class AboutActivity extends AppCompatActivity {

    private String TAG = AboutActivity.class.getSimpleName();
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        textView = findViewById(R.id.textViewA);

        Html.ImageGetter imageGetter = source -> {
            final LevelListDrawable mDrawable = new LevelListDrawable();
            final double windowsWidth = 80;
            URL url;
            Glide.with(AboutActivity.this).load(source).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    mDrawable.addLevel(1, 1, resource);
                    double scale = (double) resource.getIntrinsicHeight() / (double) resource.getIntrinsicWidth();
                    int imgHeight = (int) (scale * windowsWidth);
                    int imgWidth = (int) windowsWidth;
                    mDrawable.setBounds(0, 0, imgWidth, imgHeight);
                    mDrawable.setLevel(1);
                    /**
                     * 图片下载完成之后重新赋值textView
                     *
                     */
                    textView.invalidate();
                    textView.setText(textView.getText());
                }

            });
            return mDrawable;
        };
        String s = "<div>\n" +
                "\t\t\t<h3>关于本APP的一些说明：</h3>\n" +
                "\t\t\t<br>\n" +
                "\t\t\t1.特别感谢<a href=\"https://arknights.host/\"><img src=\"https://ak.dzp.me/dst/avatar/ASSISTANT/npc_007_closure.webp\" alt=\"\" width=\"20\" height=\"20\">可露希尔工作室</a>和skd、<a\n" +
                "\t\t\t\thref=\"https://github.com/Kengxxiao/ArknightsGameData\"><img src=\"https://avatars.githubusercontent.com/u/11478651?v=4ages/ak.png\" alt=\"\" width=\"20\" height=\"20\">Kengxxiao</a>、<a\n" +
                "\t\t\t\thref=\"https://prts.wiki/w/%E9%A6%96%E9%A1%B5\"><img src=\"https://prts.wiki/images/ak.png\" alt=\"\" width=\"20\" height=\"20\">PRTS Wiki</a>、<a\n" +
                "\t\t\t\thref=\"https://penguin-stats.cn/?utm_source=penguin-stats&utm_medium=mirror-notification\"><img src=\"https://penguin.upyun.galvincdn.com/logos/penguin_stats_logo.png\" alt=\"\" width=\"20\" height=\"20\">企鹅物流</a>、<a\n" +
                "\t\t\t\thref=\"https://monster-siren.hypergryph.com/music\"><img src=\"https://web.hycdn.cn/siren/site/manifest/icon_192.png\" alt=\"\" width=\"20\" height=\"20\">塞壬唱片</a>，感谢以上大佬们的API共享和资源开放；\n" +
                "\t\t\t2.本APP源代码仓库地址：<a\n" +
                "\t\t\t\thref=\"https://github.com/joe-vettek/ClosureHelper\"><img src=\"https://avatars.githubusercontent.com/u/82761814?v=4\" alt=\"\" width=\"20\" height=\"20\">https://github.com/joe-vettek/ClosureHelper</a>\n" +
                "\t\t\t<br>\n" +
                "\t\t\t<br>\n" +
                "\t\t\t<h3>使用帮助：</h3>\n" +
                "\t\t\t<br>\n" +
                "\t\t\t1.理智分析、每日收益统计、下次运行时间等功能仅供参考且使用北京时间；\n" +
                "\t\t\t<br>\n" +
                "\t\t\t2.在实例界面点击人物头像可以进行更详细的托管配置；\n" +
                "\t\t\t<br>\n" +
                "\t\t\t3.登录和验证后如果未刷新数据可以在右下角按钮菜单进行全局刷新；\n" +
                "\t\t\t<br>\n" +
                "\t\t\t4.游戏配置界面长按查找按钮可以清空战斗序列；\n" +
                "\t\t\t<br>\n" +
                "\t\t\t5.如果遇到异常或者程序崩溃，优先清理数据，若仍未解决，则在设置中点击资源修复，若仍有问题请在群中联系作者。\n" +
                "\n" +
                "\t\t</div>";
        // flags
        // FROM_HTML_MODE_COMPACT：html块元素之间使用一个换行符分隔
        // FROM_HTML_MODE_LEGACY：html块元素之间使用两个换行符分隔
        textView.setText(Html.fromHtml(s, Html.FROM_HTML_MODE_LEGACY, imageGetter, null));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.exit);
    }


}