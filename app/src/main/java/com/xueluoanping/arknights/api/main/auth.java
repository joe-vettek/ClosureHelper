package com.xueluoanping.arknights.api.main;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

//import com.geetest.captcha.GTCaptcha4Client;
//import com.geetest.captcha.GTCaptcha4Config;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.xueluoanping.arknights.SimpleApplication;
import com.xueluoanping.arknights.pro.HttpConnectionUtil;
import com.xueluoanping.arknights.pro.TransCoding;
import com.xueluoanping.arknights.pro.spTool;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class auth {
    private static final String TAG = auth.class.getSimpleName();

    private static final String recaptchaKey = "6LfAKUQnAAAAAAt_QKAV5i-EdDPSL-pEtn589fQg";
//    private static GTCaptcha4Client gtCaptcha4Client_login;
    // private static GTCaptcha4Client gtCaptcha4Client_game;
    public static void initGTCaptcha4Client(Activity context)  {

//        GTCaptcha4Config config = new GTCaptcha4Config.Builder()
//                // .setDebug(true) // TODO 线上务必关闭
//                .setLanguage("zh")
//                .setTimeOut(10000)
//                // .setCanceledOnTouchOutside(true)
//
//                .build();
//        gtCaptcha4Client_login = GTCaptcha4Client.getClient(context)
//                .init("8d006e952aaee56c26f4efb82717dba8", config);

        // gtCaptcha4Client_game = GTCaptcha4Client.getClient(context)
        //         .init("8d006e952aaee56c26f4efb82717dba8", config);
        // new Thread(auth::click).start();
        Log.d(TAG, "initGTCaptcha4Client: 初始化验证成功");
    }

    public static void cancelCheck(){
        try {
//            gtCaptcha4Client_login.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void destroyCheck(){
        try {
//            gtCaptcha4Client_login.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String waitCheck(Activity context) throws TimeoutException, InterruptedException {
        AtomicReference<String> atomicString = new AtomicReference<>(null);
        context.runOnUiThread(()->{
            initGTCaptcha4Client(context);
            click(atomicString);
        });
        // Lock lock = new ReentrantLock();

        int time=0;
        while (atomicString.get()==null){
            Thread.sleep(1000);
            time++;
            if(time>30){
                throw new TimeoutException("验证超时");
            }
        }
        return atomicString.get();
    }
    public static void initWithPassword(Activity context) throws IOException, JSONException, InterruptedException, TimeoutException {
        String urlStr = host.getQuickestHost() + "/Auth/" + TransCoding.URlEncode(spTool.getUserName(context)) + "/" + TransCoding.URlEncode(spTool.getPassword(context));

        // lock.unlock();
        // String str = atomicString.get();
        //         Gson gson = new Gson();
        // Type type = new TypeToken<Map<String, String>>() {}.getType();
        // Map<String, String> map = gson.fromJson(str, type);
        Login(urlStr,waitCheck(context));
        Log.d(TAG, "init: ");
    }

    public static void click(AtomicReference<String> atomicString) {
//        gtCaptcha4Client_login.addOnSuccessListener(
//                        (status, response) -> {
//                            if (status) {
//                                // TODO 开启二次验证
//                                atomicString.set(response);
//                            } else {
//                                // TODO 用户答案验证错误
//                            }
//                            Log.d(TAG, "click: "+status+response);
//                        }).addOnFailureListener(error -> {
//
//                })
//                .verifyWithCaptcha();
    }

    public static Map<String, String> getTokenMap(Context context) {
        Map<String, String> extraRequestProperty = new HashMap<>();
        extraRequestProperty.put("authorization", spTool.getToken(context));
        extraRequestProperty.put("Referer", "Android");

        // Log.d(TAG, "getTokenMap: "+visitorId);
        // extraRequestProperty.put("visitorid", visitorId);
        // 加上时间戳，避免缓存机制
        return extraRequestProperty;
    }


    public static void refresh() throws IOException, JSONException {
        Context context = SimpleApplication.getContext();
        String urlStr = host.getQuickestHost() + "/Auth/" + spTool.getToken(context);
        Login(urlStr,null);
    }


    private static void Login(String url,String json) throws IOException, JSONException {
        String loginMessage = HttpConnectionUtil.post(url, json,null);
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
