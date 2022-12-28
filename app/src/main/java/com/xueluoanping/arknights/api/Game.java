package com.xueluoanping.arknights.api;

import android.content.Context;
import android.util.JsonWriter;
import android.util.Log;

import com.google.gson.Gson;
import com.xueluoanping.arknights.SimpleApplication;
import com.xueluoanping.arknights.pro.HttpConnectionUtil;
import com.xueluoanping.arknights.services.SimpleService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Game {
    private static final String TAG = Game.class.getSimpleName();

    // [{"config":{"account":"******","platform":1,"userEmail":"********","isPause":false}
    // ,"status":{"code":2,"text":"托管中"}
    // ,"captcha_info":{"challenge":"","gt":"","created":0,"captcha_type":""}
    // ,"game_config":{"isAutoBattle":true,"mapId":"act22side_05","battleMaps":["act22side_05"]
    //             ,"keepingAP":50,"recruitReserve":1,"recruitIgnoreRobot":true,"isStopped":false,"enableBuildingArrange":true}}]
    public static final int WebGame_Status_Code_ErrorLogin = -1;
    public static final int WebGame_Status_Code_NeedLogin = 0;
    public static final int WebGame_Status_Code_Loginning = 1;
    public static final int WebGame_Status_Code_Running = 2;
    public static final int WebGame_Status_Code_ErrorGame = 3;
    public static final int WebGame_Status_Code_NeedCheck = 999;

    public static final int Platform_Unknown = -1;
    public static final int Platform_IOS = 0;
    public static final int Platform_Android = 1;
    public static final int Platform_Bilibili = 2;

    public static class GameInfo {
        public String account = HttpConnectionUtil.empty;
        public int platform = Platform_Unknown;
        public int code = WebGame_Status_Code_NeedLogin;
        public String status = HttpConnectionUtil.empty;
        public String battleMaps = HttpConnectionUtil.empty;
    }

    // {
    //     "code": 1,
    //         "data": {
    //     "isAutoBattle": true,
    //             "mapId": "act22side_06",
    //             "battleMaps": [
    //     "act22side_06"
    // ],
    //     "keepingAP": 50,
    //             "recruitReserve": 1,
    //             "recruitIgnoreRobot": true,
    //             "isStopped": false,
    //             "enableBuildingArrange": true
    // },
    //     "message": "successful!"
    // }
    public static class GameSettings implements Serializable {
        public int keepingAP = 0;
        public int recruitReserve = 0;
        public boolean isAutoBattle = false;
        public boolean enableBuildingArrange = false;
        public boolean recruitIgnoreRobot = false;
        public List<String> battleMaps = new ArrayList<>();
        public boolean isStopped=false;


        public String getJson() {
            Gson gson = new Gson();
            return gson.toJson(GameSettings.this);
        }

    }

    public static GameInfo[] getGameStatue(Context context) throws IOException, JSONException {
        String urlStr = host.baseApi + "/Game";

        String GameJson = HttpConnectionUtil.DownLoadTextPages(urlStr, auth.getTokenMap(context),true);
        Log.d(TAG, "getGameStatue: " + GameJson);
        JSONArray jsonArray = new JSONObject(GameJson).getJSONArray("data");
        GameInfo[] infoList = new GameInfo[jsonArray.length()];

        for (int i = 0; i < jsonArray.length(); i++) {
            GameInfo info = new GameInfo();
            JSONObject sector = jsonArray.getJSONObject(i);
            JSONObject config = sector.getJSONObject("config");
            JSONObject status = sector.getJSONObject("status");
            JSONObject captcha_info = sector.getJSONObject("captcha_info");
            JSONObject game_config = sector.getJSONObject("game_config");
            info.account = config.getString("account");
            info.platform = config.getInt("platform");
            info.code = status.getInt("code");
            info.status = status.getString("text");
            try {
                info.battleMaps = game_config.getJSONArray("battleMaps").getString(0);
            } catch (Exception e) {
                // e.printStackTrace();
                Log.d(TAG, "getGameStatue: 战斗地图为空");
            }

            infoList[i] = info;
        }


        return infoList;
    }

    public static GameSettings getGameSettings_Json(JSONObject jsonArray) throws JSONException {
        GameSettings gameSettings = new GameSettings();
        gameSettings.isAutoBattle = jsonArray.getBoolean("isAutoBattle");
        gameSettings.enableBuildingArrange = jsonArray.getBoolean("enableBuildingArrange");
        gameSettings.recruitIgnoreRobot = jsonArray.getBoolean("recruitIgnoreRobot");
        gameSettings.keepingAP = jsonArray.getInt("keepingAP");
        gameSettings.recruitReserve = jsonArray.getInt("recruitReserve");
        gameSettings.battleMaps.clear();
        gameSettings.isStopped=jsonArray.getBoolean("isStopped");
        for (int i = 0; i < jsonArray.getJSONArray("battleMaps").length(); i++) {
            gameSettings.battleMaps.add(jsonArray.getJSONArray("battleMaps").getString(i));
        }
        return gameSettings;
    }

    public static GameSettings getGameSettings(GameInfo info) throws JSONException, IOException {
        String urlStr = host.baseApi + "/Game/Config/" + info.account + "/" + info.platform;

        String GameJson = HttpConnectionUtil.DownLoadTextPages(urlStr, auth.getTokenMap(SimpleApplication.getContext()),true);
        Log.d(TAG, "getGameStatue: " + GameJson);
        JSONObject jsonArray = new JSONObject(GameJson).getJSONObject("data");

        return getGameSettings_Json(jsonArray);
    }

    public static boolean updateGameSettings(GameInfo info, GameSettings gameSettings) throws IOException {
        Log.d(TAG, "updateGameSettings: " + gameSettings.getJson());
        String urlStr = host.baseApi + "/Game/Config/" + info.account + "/" + info.platform;
        String GameJson = HttpConnectionUtil.post(urlStr, gameSettings.getJson(), auth.getTokenMap(SimpleApplication.getContext()));
        Log.d(TAG, "updateGameSettings: " + GameJson);
        // 以后再处理报文
        return GameJson.contains("successful");
    }

    public static boolean TryLogin(Context context, String account, int platform) {
        String urlStr = host.baseApi + "/Game/Login";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("account", account);
            jsonObject.put("platform", platform);
            String s = HttpConnectionUtil.post(urlStr, jsonObject.toString(), auth.getTokenMap(context));
            JSONObject message = new JSONObject(s);
            Log.d(TAG, "TryLogin: " + s);
            if (message.getInt("code") == 1) return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean TryDelete( String account, int platform) {
        String urlStr = host.baseApi + "/Game";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("account", account);
            jsonObject.put("platform", platform);
            String s = HttpConnectionUtil.delete(urlStr, jsonObject.toString(), auth.getTokenMap(SimpleApplication.getContext()));
            JSONObject message = new JSONObject(s);
            Log.d(TAG, "TryDelete: " + s);
            if (message.getInt("code") == 1) return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean TryAdd( String account, String password,int platform) {
        String urlStr = host.baseApi + "/Game";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("account", account);
            jsonObject.put("password", password);
            jsonObject.put("platform", platform);
            Log.d(TAG, "TryAdd: "+jsonObject);
            String s = HttpConnectionUtil.post(urlStr, jsonObject.toString(), auth.getTokenMap(SimpleApplication.getContext()));
            JSONObject message = new JSONObject(s);
            Log.d(TAG, "TryAdd: " + s);
            if (message.getInt("code") == 1) return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getPlatform(int code) {
        switch (code) {
            case Platform_IOS:
                return "苹果";
            case Platform_Android:
                return "安卓";
            case Platform_Bilibili:
                return "B站";
            default:
                break;
        }
        return "未知平台";
    }
}
