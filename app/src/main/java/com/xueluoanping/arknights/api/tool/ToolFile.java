package com.xueluoanping.arknights.api.tool;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xueluoanping.arknights.SimpleApplication;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ToolFile {
    public static String getTextFile(String name) throws IOException, NullPointerException {
        // int size = (int) context.getAssets().openFd(name).getLength();
        Context context = SimpleApplication.getContext();
        File file = new File(context.getExternalCacheDir().getAbsolutePath() + "/" + name);
        int size = (int) file.length();
        byte[] cbuf = new byte[size];

        // InputStream is = context.getAssets().open(name);
        InputStream is = new FileInputStream(file);
        int len = is.read(cbuf);

        return new String(cbuf, 0, len);
    }

    public static String getBaseUrl() {
        if (SimpleApplication.getContext() != null)
            return SimpleApplication.getContext().getExternalCacheDir().getAbsolutePath() + "/";
        else return null;
    }

    public static JsonObject textToJsonObject(String t) {  Gson g=new Gson();
        return g.fromJson(t,JsonObject.class);
    }

    public static void saveTextFile(Context context, String text, String simpleFileName) {
        try {
            // 存在cache目录
            File file = new File(context.getExternalCacheDir().getAbsolutePath() + "/" + simpleFileName);
            if (!file.exists()) {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }
            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(text.getBytes());
            outStream.close();
            Log.e("save", file.getName() + "" + file.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
