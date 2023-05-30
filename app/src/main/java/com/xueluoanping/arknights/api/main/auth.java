package com.xueluoanping.arknights.api.main;

import android.content.Context;
import android.util.Log;

// import com.fingerprintjs.android.fpjs_pro.FingerprintJS;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xueluoanping.arknights.SimpleApplication;
import com.xueluoanping.arknights.api.BetterEntry;
import com.xueluoanping.arknights.pro.HttpConnectionUtil;
import com.xueluoanping.arknights.pro.TransCoding;
import com.xueluoanping.arknights.pro.spTool;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fingerprintjs.android.fpjs_pro.Configuration;
import com.fingerprintjs.android.fpjs_pro.FingerprintJS;
import com.fingerprintjs.android.fpjs_pro.FingerprintJSFactory;

public class auth {
    private static final String TAG = auth.class.getSimpleName();

    private static final String fingerprintKey = "7zPvpGlO9QLRlN14kjEQ";
    private static String visitorId=null;
    public static void initVisitorId() {
        FingerprintJSFactory factory = new FingerprintJSFactory(SimpleApplication.getContext());
        Configuration configuration = new Configuration(
                fingerprintKey
        );
        FingerprintJS fpjsClient = factory.createInstance(
                configuration

        );

        fpjsClient.getVisitorId(visitorIdResponse ->

        {
            // Use the ID
            visitorId = visitorIdResponse.getVisitorId();
            return null;
        });
    }

    public static void init(Context context) throws IOException, JSONException {
        String urlStr = host.getQuickestHost() + "/Auth/" + TransCoding.URlEncode(spTool.getUserName(context)) + "/" + TransCoding.URlEncode(spTool.getPassword(context));
        Login(urlStr);
    }


    public static Map<String, String> getTokenMap(Context context) {
        Map<String, String> extraRequestProperty = new HashMap<>();
        extraRequestProperty.put("authorization", spTool.getToken(context));
        while (visitorId==null) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }

        }
        Log.d(TAG, "getTokenMap: "+visitorId);
        extraRequestProperty.put("visitorid", visitorId);
        // 加上时间戳，避免缓存机制
        return extraRequestProperty;
    }
    public static Map<String, String> getVisitorIdMap() {
        Map<String, String> extraRequestProperty = new HashMap<>();
        while (visitorId==null) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }

        }
        extraRequestProperty.put("visitorid", visitorId);
        // 加上时间戳，避免缓存机制
        return extraRequestProperty;
    }
    public static void refresh() throws IOException, JSONException {
        Context context = SimpleApplication.getContext();
        String urlStr = host.getQuickestHost() + "/Auth/" + spTool.getToken(context);
        Login(urlStr);
    }


    private static void Login(String url) throws IOException, JSONException {
        String loginMessage = HttpConnectionUtil.DownLoadTextPages(url, getVisitorIdMap(), true);
        String token = HttpConnectionUtil.empty;
        boolean isAdmin = false;
        Log.d(TAG, "Login: " + loginMessage);
        JSONObject jsonObject = new JSONObject(loginMessage).getJSONObject("data");
        isAdmin = jsonObject.getBoolean("isAdmin");
        token = jsonObject.getString("token");

        spTool.saveIsAdmin(isAdmin);
        spTool.saveToken(token);
    }

    // https://ak.dzp.me/ann.json
    public static auth.announcement isMaintaining() throws IOException, NullPointerException {

        String urlStr = host.getQuickestHost() + "/Common/Announcement";
        String announcement = HttpConnectionUtil.DownLoadTextPages(urlStr, null, true);
        Gson g = new Gson();
        auth.announcement a = new Gson().fromJson(g.fromJson(announcement, JsonObject.class).getAsJsonObject("data"), auth.announcement.class);
        return a;
    }




    public static class announcement {
        private String announcement;
        private boolean allowLogin;
        private boolean allowGameLogin;
        private boolean isMaintain;

        public void setAnnouncement(String announcement) {
            this.announcement = announcement;
        }

        public String getAnnouncement() {
            return announcement;
        }

        public void setAllowLogin(boolean allowLogin) {
            this.allowLogin = allowLogin;
        }

        public boolean getAllowLogin() {
            return allowLogin;
        }

        public void setAllowGameLogin(boolean allowGameLogin) {
            this.allowGameLogin = allowGameLogin;
        }

        public boolean getAllowGameLogin() {
            return allowGameLogin;
        }

        public void setIsMaintain(boolean isMaintain) {
            this.isMaintain = isMaintain;
        }

        public boolean getIsMaintain() {
            return isMaintain;
        }

    }
}
