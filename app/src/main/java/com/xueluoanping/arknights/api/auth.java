package com.xueluoanping.arknights.api;

import android.content.Context;
import android.util.Log;

import com.xueluoanping.arknights.pro.HttpConnectionUtil;
import com.xueluoanping.arknights.pro.TransCoding;
import com.xueluoanping.arknights.pro.spTool;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class auth {
    private static final String TAG = auth.class.getSimpleName();

    public static void init(Context context) throws IOException, JSONException {
        String urlStr = host.baseApi + "/Auth/" + TransCoding.URlEncode(spTool.getUserName(context)) + "/" + TransCoding.URlEncode(spTool.getPassword(context));
        // Log.d(TAG, "init: " + urlStr);
        String loginMessage = HttpConnectionUtil.DownLoadTextPages(urlStr, null);
        String token = HttpConnectionUtil.empty;
        boolean isAdmin = false;
        Log.d(TAG, "init: "+loginMessage);
        JSONObject jsonObject = new JSONObject( loginMessage).getJSONObject("data");
        isAdmin = jsonObject.getBoolean("isAdmin");
        token = jsonObject.getString("token");

        spTool.saveIsAdmin(context, isAdmin);
        spTool.saveToken(context, token);
    }


    public static Map<String, String> getTokenMap(Context context) {
        Map<String, String> extraRequestProperty = new HashMap<>();
        extraRequestProperty.put("authorization", spTool.getToken(context));
        // 加上时间戳，避免缓存机制
        return extraRequestProperty;
    }
}
