package com.xueluoanping.arknights.pages;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.bumptech.glide.Glide;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.XPopupImageLoader;
import com.xueluoanping.arknights.R;
import com.xueluoanping.arknights.api.main.Game;
import com.xueluoanping.arknights.base.BaseActivity;
import com.xueluoanping.arknights.pro.SimpleTool;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class GalleyActivity extends BaseActivity {
    private static final String TAG = GalleyActivity.class.getSimpleName();
    private TextView textView;
    private String account = "";
    private int platform = Game.Platform_Unknown;
    private NestedScrollView scrollView;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_galley);
        textView = findViewById(R.id.textView66);
        scrollView = findViewById(R.id.nsv_galley);
        linearLayout = findViewById(R.id.ll_galley);
        Bundle extras = getIntent().getExtras();
        this.account = extras.getString("account", "");
        this.platform = extras.getInt("platform", Game.Platform_Unknown);
        addPic();
    }

    private void addPic() {
        textView.setText("当前没有截图，请等待。");
        new Thread(() -> {
            List<Game.Screenshot> list = Game.getScreenshots(account, platform);
            if (list != null) {
                AtomicInteger amount = new AtomicInteger();
                list.forEach(shot -> {
                    shot.getFileName().forEach(s -> {
                        runOnUiThread(() -> {
                            ImageView imageView = new ImageView(GalleyActivity.this);
                            imageView.setPadding(0, 10, 0, 10);
                            imageView.setOnClickListener(v -> {
                                new XPopup.Builder(GalleyActivity.this)
                                        .autoDismiss(true)
                                        .asImageViewer(imageView, shot.getUrl() + s, new ImageLoader())
                                        .show();
                                updatePrimaryClip(s);
                                SimpleTool.toastInThread(GalleyActivity.this, "已经将截图编号复制到剪贴板！");
                            });
                            Glide.with(getApplicationContext())
                                    .load(shot.getUrl() + s)
                                    // .apply(options)
                                    .into(imageView);
                            linearLayout.addView(imageView);
                        });

                    });
                    amount.addAndGet(shot.getFileName().size());
                });
                // Game.Screenshot shot=list.get(0);

                textView.setText(String.format(Locale.CHINA, "一共有%s张截图", amount));
            }
        }).start();
    }

    // 图片加载器，我不负责加载图片，需要你实现一个图片加载器传给我，这里以 Glide 为例。
    class ImageLoader implements XPopupImageLoader {

        @Override
        public void loadImage(int position, @NonNull Object uri, @NonNull ImageView imageView) {
            Glide.with(imageView).load(uri).into(imageView);
        }

        //必须实现这个方法，返回 uri 对应的缓存文件，可参照下面的实现，内部保存图片会用到。
        @Override
        public File getImageFile(@NonNull Context context, @NonNull Object uri) {
            try {
                return Glide.with(context).downloadOnly().load(uri).submit().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    void updatePrimaryClip(String newText) {
        ClipData clip = ClipData.newPlainText(null, newText);
        ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(clip);
        Log.i(TAG, "updatePrimaryClip: newText=" + newText);
    }

}
