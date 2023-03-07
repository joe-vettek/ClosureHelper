package com.xueluoanping.arknights.api.main;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.xueluoanping.arknights.SimpleApplication;
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
        String urlStr = host.getQuickestHost() + "/Auth/" + TransCoding.URlEncode(spTool.getUserName(context)) + "/" + TransCoding.URlEncode(spTool.getPassword(context));
        Login(urlStr);
    }


    public static Map<String, String> getTokenMap(Context context) {
        Map<String, String> extraRequestProperty = new HashMap<>();
        extraRequestProperty.put("authorization", spTool.getToken(context));
        // 加上时间戳，避免缓存机制
        return extraRequestProperty;
    }


    public static void refresh() throws IOException, JSONException {
        Context context = SimpleApplication.getContext();
        String urlStr = host.getQuickestHost() + "/Auth/" + spTool.getToken(context);
        Login(urlStr);
    }


    private static void Login(String url) throws IOException, JSONException {
        String loginMessage = HttpConnectionUtil.DownLoadTextPages(url, null, true);
        String token = HttpConnectionUtil.empty;
        boolean isAdmin = false;
        Log.d(TAG, "Login: " + loginMessage);
        JSONObject jsonObject = new JSONObject(loginMessage).getJSONObject("data");
        isAdmin = jsonObject.getBoolean("isAdmin");
        token = jsonObject.getString("token");

        spTool.saveIsAdmin(isAdmin);
        spTool.saveToken(token);
    }

    public static boolean isMaintaining() throws IOException {
        String urlStr = "https://ak.dzp.me/ann.json";
        String announcement=HttpConnectionUtil.DownLoadTextPages(urlStr,null,true);
        return  new Gson().fromJson(announcement, auth.announcement.class).isMaintain;
    }


    public static class announcement {

        private double updated_at;
        private String announcement;
        private boolean allowgamelogin;
        private boolean allowlogin;
        private boolean isMaintain;
        public void setUpdated_at(double updated_at) {
            this.updated_at = updated_at;
        }
        public double getUpdated_at() {
            return updated_at;
        }

        public void setAnnouncement(String announcement) {
            this.announcement = announcement;
        }
        public String getAnnouncement() {
            return announcement;
        }

        public void setAllowgamelogin(boolean allowgamelogin) {
            this.allowgamelogin = allowgamelogin;
        }
        public boolean getAllowgamelogin() {
            return allowgamelogin;
        }

        public void setAllowlogin(boolean allowlogin) {
            this.allowlogin = allowlogin;
        }
        public boolean getAllowlogin() {
            return allowlogin;
        }

        public void setIsMaintain(boolean isMaintain) {
            this.isMaintain = isMaintain;
        }
        public boolean getIsMaintain() {
            return isMaintain;
        }

    }
}
