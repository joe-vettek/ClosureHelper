package com.xueluoanping.arknights;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xueluoanping.arknights.api.Data;
import com.xueluoanping.arknights.pro.HttpConnectionUtil;
import com.xueluoanping.arknights.pro.SimpleTool;

import java.io.File;
import java.io.IOException;


public class SimpleApplication extends Application {

    private static final String TAG = SimpleApplication.class.getSimpleName();

    public static Context getContext() {
        return context;
    }

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        checkArknightsDataVersion();
        // new Thread(new Runnable() {
        //     @Override
        //     public void run() {
        //         try {
        //             String aa = HttpConnectionUtil.DownLoadTextPages("https://raw.fastgit.org/Kengxxiao/ArknightsGameData/master/zh_CN/gamedata/excel/item_table.json", null);
        //             Gson gson = new Gson();
        //             gson.toJson(aa);
        //             Log.d(TAG, "onCreate: " + aa.toString());
        //         } catch (IOException e) {
        //             e.printStackTrace();
        //         }
        //     }
        // }).start();

    }
    // https://raw.fastgit.org/Kengxxiao/ArknightsGameData/master/zh_CN/gamedata/excel/item_table.json
    private void checkArknightsDataVersion() {
        String baseUrl = getExternalCacheDir().getAbsolutePath() + "/";
        String versionFileName = "data/data_version.txt";
        String itemTableFileName = "data/item_table.json";
        String stageTableFileName = "data/stage_table.json";
        String characterTableFileName = "data/character_table.json";
        File file = new File(baseUrl + versionFileName);
        if (!file.exists()) {
            try {
                String vText=SimpleTool.getStringAssents(versionFileName);
                SimpleTool.saveTextFile(getContext(),vText,versionFileName);
                checkArknightsDataVersion();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "checkArknightsDataVersion: "+file.getAbsolutePath());
            }
        }
        file = new File(baseUrl + itemTableFileName);
        if (!file.exists()) {
            try {
                String vText=SimpleTool.getStringAssents(itemTableFileName);
                SimpleTool.saveTextFile(getContext(),vText,itemTableFileName);
                checkArknightsDataVersion();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "checkArknightsDataVersion: "+file.getAbsolutePath());
            }
        }
        file = new File(baseUrl + stageTableFileName);
        if (!file.exists()) {
            try {
                String vText=SimpleTool.getStringAssents(stageTableFileName);
                SimpleTool.saveTextFile(getContext(),vText,stageTableFileName);
                checkArknightsDataVersion();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "checkArknightsDataVersion: "+file.getAbsolutePath());
            }
        }
        file = new File(baseUrl + characterTableFileName);
        if (!file.exists()) {
            try {
                String vText=SimpleTool.getStringAssents(characterTableFileName);
                SimpleTool.saveTextFile(getContext(),vText,characterTableFileName);
                checkArknightsDataVersion();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "checkArknightsDataVersion: "+file.getAbsolutePath());
            }
        }
    }


}
