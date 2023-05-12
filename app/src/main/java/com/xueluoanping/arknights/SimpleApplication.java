package com.xueluoanping.arknights;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.lxj.xpopup.XPopup;
import com.xueluoanping.arknights.api.main.host;
import com.xueluoanping.arknights.api.resource.Kengxxiao;
import com.xueluoanping.arknights.api.tool.ToolFile;
import com.xueluoanping.arknights.api.tool.ToolTable;
import com.xueluoanping.arknights.api.tool.ToolTheme;
import com.xueluoanping.arknights.pro.SimpleTool;
import com.xueluoanping.arknights.pro.spTool;
import com.xueluoanping.arknights.services.MyReceiver;

import java.io.File;


public class SimpleApplication extends Application {

    private static final String TAG = SimpleApplication.class.getSimpleName();

    public static Context getContext() {
        return context;
    }

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    //暂时写写
    public static void checkAPPUpdate(MainActivity mainActivity) {

    }

    public static void restartNow() {
        if (SimpleApplication.getContext() != null)
            ((SimpleApplication) SimpleApplication.getContext()).restart();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        // 初始化地址
        int line = spTool.getLineSelect();
        if (line == 0)
            host.trySetQuickestAsyn();
        else host.setQuickestHost(host.baseHosts[line].getKey());
        // 注册动态接收器
        registerReceiverDynamic();

        //验证资源是否已经解压好到本地，没有则继续解压
        verifyResources();

        // 初始化
        ToolTable.initInstance();
        try {
            setTheme(spTool.getTheme());
        } catch (Exception e) {
            e.printStackTrace();
        }
        XPopup.setPrimaryColor(ToolTheme.getColorValue(context, R.attr.colorPrimary));
    }

    private void registerReceiverDynamic() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        registerReceiver(new MyReceiver(), intentFilter);
    }

    // https://raw.fastgit.org/Kengxxiao/ArknightsGameData/master/zh_CN/gamedata/excel/item_table.json
    private void verifyResources() {

        String baseUrl = getExternalCacheDir().getAbsolutePath() + "/";
        String versionFileName = "data/data_version.txt";
        String itemTableFileName = "data/item_table.json";
        String stageTableFileName = "data/stage_table.json";
        String itemNameTableFileName = "data/item_name_table.json";
        File file = new File(baseUrl + versionFileName);
        if (!file.exists()) {
            try {
                String vText = SimpleTool.getStringAssents(versionFileName);
                ToolFile.saveTextFile(getContext(), vText, versionFileName);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "checkArknightsDataVersion: " + file.getAbsolutePath());
            }
        }
        file = new File(baseUrl + itemTableFileName);
        if (!file.exists()) {
            try {
                String vText = SimpleTool.getStringAssents(itemTableFileName);
                ToolFile.saveTextFile(getContext(), vText, itemTableFileName);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "checkArknightsDataVersion: " + file.getAbsolutePath());
            }
        }
        file = new File(baseUrl + stageTableFileName);
        if (!file.exists()) {
            try {
                String vText = SimpleTool.getStringAssents(stageTableFileName);
                ToolFile.saveTextFile(getContext(), vText, stageTableFileName);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "checkArknightsDataVersion: " + file.getAbsolutePath());
            }
        }
        file = new File(baseUrl + itemNameTableFileName);
        if (!file.exists())
            Kengxxiao.exportNameUUIDToIconId(file, new File(baseUrl + itemTableFileName));
    }


    @SuppressLint("VisibleForTests")
    public void restart() {
        // ActivityManager manager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        // manager.restartPackage(getPackageName());
        //
        Glide.tearDown();
        Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


}
