package com.xueluoanping.arknights.api.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.xueluoanping.arknights.SimpleApplication;
import com.xueluoanping.arknights.api.tool.ToolTime;
import com.xueluoanping.arknights.custom.GameLog.GameLog;
import com.xueluoanping.arknights.pro.HttpConnectionUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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

    public static class GameInfo implements Serializable {
        public String account = HttpConnectionUtil.empty;
        public int platform = Platform_Unknown;
        public int code = WebGame_Status_Code_NeedLogin;
        public String status = HttpConnectionUtil.empty;
        public String mapId = HttpConnectionUtil.empty;
        public String challenge = "";
        public String gt = "";

        // 刷新数据
        public Game.GameInfo getEqualNewInstance(Game.GameInfo[] list) {
            for (Game.GameInfo info : list
            ) {
                if (info.platform == this.platform
                        && info.account.equals(this.account))
                    return info;
            }
            return null;
        }

        public Bundle createBundle() {
            Bundle bundle = new Bundle();
            bundle.putSerializable("info", GameInfo.this);
            return bundle;
        }

        // 需要接收
        public Game.GameInfo backupFromBundle(Bundle extras) {
            if (extras != null
                    && extras.containsKey("info")
            ) {
                Game.GameInfo info = (Game.GameInfo) extras.getSerializable("info");
                Log.d(TAG, "initData: " + info.account);
                return info;
            }
            return null;
        }

        public Intent addIntentBundle(Intent intent) {
            intent.putExtra("info", GameInfo.this);
            return intent;
        }

        // 需要接收
        public Game.GameInfo backupFromIntentBundle(Intent intent) {
            if (intent != null
                    && intent.hasExtra("info")
            ) {
                Game.GameInfo info = (Game.GameInfo) intent.getSerializableExtra("info");
                Log.d(TAG, "initData: " + info.account);
                return info;
            }
            return null;
        }

        @NotNull
        @Override
        public String toString() {
            return account + "#" + platform;
        }
    }

    public class GameLogs {

        private int code;
        private List<Detail> data;
        private String message;

        public void setCode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public void setData(List<Detail> data) {
            this.data = data;
        }

        public List<Detail> getData() {
            return data;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

    }

    public class Detail {

        private double ts;
        private String info;

        public void setTs(double ts) {
            this.ts = ts;
        }

        public double getTs() {
            return ts;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public String getInfo() {
            return info;
        }

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
        public boolean isStopped = false;


        public String getJson() {
            Gson gson = new Gson();
            return gson.toJson(GameSettings.this);
        }

    }

    public class ScreenshotListWrapper {
        private int code;
        private List<Screenshot> data;
        private String message;

        public void setCode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public void setData(List<Screenshot> data) {
            this.data = data;
        }

        public List<Screenshot> getData() {
            return data;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

    }

    public class Screenshot {

        private long UTCTime;
        private int type;
        private String host;
        private List<String> fileName;
        private String url;

        public void setUTCTime(long UTCTime) {
            this.UTCTime = UTCTime;
        }

        public long getUTCTime() {
            return UTCTime;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getHost() {
            return host;
        }

        public void setFileName(List<String> fileName) {
            this.fileName = fileName;
        }

        public List<String> getFileName() {
            return fileName;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

    }

    public static GameInfo[] getGameStatue(Context context) throws IOException, JSONException {
        String urlStr = host.getQuickestHost() + "/Game";

        String GameJson = HttpConnectionUtil.DownLoadTextPages(urlStr, auth.getTokenMap(context), true);
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
            info.challenge = captcha_info.getString("challenge");
            info.gt = captcha_info.getString("gt");
            try {
                info.mapId = game_config.getString("mapId");
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
        gameSettings.isStopped = jsonArray.getBoolean("isStopped");
        for (int i = 0; i < jsonArray.getJSONArray("battleMaps").length(); i++) {
            gameSettings.battleMaps.add(jsonArray.getJSONArray("battleMaps").getString(i));
        }
        return gameSettings;
    }

    public static GameSettings getGameSettings(GameInfo info) throws JSONException, IOException {
        String urlStr = host.getQuickestHost() + "/Game/Config/" + info.account + "/" + info.platform;

        String GameJson = HttpConnectionUtil.DownLoadTextPages(urlStr, auth.getTokenMap(SimpleApplication.getContext()), true);
        Log.d(TAG, "getGameStatue: " + GameJson);
        JSONObject jsonArray = new JSONObject(GameJson).getJSONObject("data");

        return getGameSettings_Json(jsonArray);
    }

    public static boolean updateGameSettings(GameInfo info, GameSettings gameSettings) throws IOException {
        Log.d(TAG, "updateGameSettings: " + gameSettings.getJson());
        String urlStr = host.getQuickestHost() + "/Game/Config/" + info.account + "/" + info.platform;
        String GameJson = HttpConnectionUtil.post(urlStr, gameSettings.getJson(), auth.getTokenMap(SimpleApplication.getContext()));
        Log.d(TAG, "updateGameSettings: " + GameJson);
        // 以后再处理报文
        return GameJson.contains("successful");
    }

    public static boolean TryLogin(Context context, String account, int platform) {
        String urlStr = host.getQuickestHost() + "/Game/Login";
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

    public static boolean TryDelete(String account, int platform) {
        String urlStr = host.getQuickestHost() + "/Game";
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

    public static boolean TryAdd(String account, String password, int platform) {
        String urlStr = host.getQuickestHost() + "/Game";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("account", account);
            jsonObject.put("password", password);
            jsonObject.put("platform", platform);
            Log.d(TAG, "TryAdd: " + jsonObject);
            String s = HttpConnectionUtil.post(urlStr, jsonObject.toString(), auth.getTokenMap(SimpleApplication.getContext()));
            JSONObject message = new JSONObject(s);
            Log.d(TAG, "TryAdd: " + s);
            if (message.getInt("code") == 1) return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean TryCaptcha(String account, int platform, JSONObject jsonObject) {
        String urlStr = host.getQuickestHost() + "/Game/Captcha/" + account + "/" + platform;
        try {
            String s = HttpConnectionUtil.post(urlStr, jsonObject.toString(), auth.getTokenMap(SimpleApplication.getContext()));
            Log.d(TAG, "TryCaptcha: " + s);
            JSONObject message = new JSONObject(s);

            if (message.getInt("code") == 1) return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<GameLog> getLog(String account, int platform) {
        // 0是时间戳，不给出
        String urlStr = host.getQuickestHost() + "/Game/Log/" + account + "/" + platform + "/0";
        try {
            String s = HttpConnectionUtil.DownLoadTextPages(urlStr, auth.getTokenMap(SimpleApplication.getContext()), true);
            Gson gson = new Gson();
            GameLogs z = gson.fromJson(s, GameLogs.class);

            List<GameLog> logList = new ArrayList<>();
            // 使用偏移纠正，注意获取的是负数，所以使用-号回正
            long offset= ToolTime.getTimeOffset();
            Log.d(TAG, "getLog: "+offset);
            for (int i = z.data.size() - 1; i > -1; i--) {
                // 不需要
                GameLog g = new GameLog();
                String date = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINA).format(new Date((long) (z.data.get(i).getTs() * 1000+offset)));
                g.setTs(date);
                g.setInfo(z.data.get(i).getInfo().replace("x","★"));
                g.ts0= (long) (z.data.get(i).getTs()*1000);
                logList.add(g);
            }
            return logList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static List<Screenshot> getScreenshots(String account, int platform) {
        String urlStr = host.getQuickestHost() + "/Game/Screenshots/" + account + "/" + platform;
        try {
            String s = HttpConnectionUtil.DownLoadTextPages(urlStr, auth.getTokenMap(SimpleApplication.getContext()), true);
            Log.d(TAG, "getScreenshots: " + s);
            Gson gson = new Gson();
            ScreenshotListWrapper z = gson.fromJson(s, ScreenshotListWrapper.class);
            return z.data;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return new ArrayList<>();
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
