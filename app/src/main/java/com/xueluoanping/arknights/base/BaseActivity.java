package com.xueluoanping.arknights.base;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xueluoanping.arknights.R;
import com.xueluoanping.arknights.SimpleApplication;
import com.xueluoanping.arknights.api.main.auth;
import com.xueluoanping.arknights.pages.WebActivity2;
import com.xueluoanping.arknights.pro.SimpleTool;
import com.xueluoanping.arknights.pro.spTool;
import com.xueluoanping.arknights.services.SimpleService;

import java.io.IOException;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setTheme(spTool.getTheme());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Intent getStartActivityIntent(Class<?> classA) {
        Intent i1 = new Intent(this, classA);
        return i1;
    }

    public void startActivity(Class<?> classA) {
        Intent i1 = getStartActivityIntent(classA);
        startActivity(i1);

    }

    public void startWebActivity(String url) {
        Intent i1 = getStartActivityIntent(WebActivity2.class);
        i1.putExtra("url",url);
        startActivity(i1);

    }


    public void startPackage(String packageName) {
        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent != null)
            startActivity(intent);
        else Toast.makeText(this, "未安装或权限不足", Toast.LENGTH_SHORT).show();
    }

    public void notifyUser(String account, String x) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(account.hashCode(),
                SimpleService.broadcastIntent(getApplicationContext(), account, x).setAutoCancel(true).build());
    }


    public void hideInputMethod() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void goNextTheme() {
        int oldTheme = spTool.getTheme();
        if (oldTheme == R.style.AppTheme_NoActionBar) {
            Toast.makeText(this, "切换到主题： 黑暗模式", Toast.LENGTH_SHORT).show();
            spTool.setTheme(R.style.AppTheme_NoActionBar_nightTheme);

        } else if (oldTheme == R.style.AppTheme_NoActionBar_nightTheme) {
            Toast.makeText(this, "切换到主题： 小鸭子的秋天", Toast.LENGTH_SHORT).show();
            spTool.setTheme(R.style.AppTheme_NoActionBar_AutumnDuck);
        }
        if (oldTheme == R.style.AppTheme_NoActionBar_AutumnDuck) {
            Toast.makeText(this, "切换到主题： 浮光跃金", Toast.LENGTH_SHORT).show();
            spTool.setTheme(R.style.AppTheme_NoActionBar_FloatingGold);

        }
        if (oldTheme == R.style.AppTheme_NoActionBar_FloatingGold) {
            Toast.makeText(this, "切换到主题： 云中梦", Toast.LENGTH_SHORT).show();
            spTool.setTheme(R.style.AppTheme_NoActionBar);

        }
        ((SimpleApplication) SimpleApplication.getContext()).restart();
    }

    public void queryArknightsIsMaintaining() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (auth.isMaintaining()) {
                        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        nMgr.cancel("".hashCode());
                        notifyUser("托管", "正在维护");
                        SimpleTool.toastInThread(BaseActivity.this, "当前可露希尔正在维护");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // 需要是活动
    public void toastInThread(String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BaseActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
