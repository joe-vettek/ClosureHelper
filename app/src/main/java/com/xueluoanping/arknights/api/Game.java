package com.xueluoanping.arknights.api;

import android.content.Context;
import android.util.Log;

import com.xueluoanping.arknights.pro.HttpConnectionUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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

    public static GameInfo[] getGameStatue(Context context) throws IOException, JSONException {
        String urlStr = host.baseApi + "/Game";

        String GameJson = HttpConnectionUtil.DownLoadTextPages(urlStr, auth.getTokenMap(context));
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
            info.battleMaps = game_config.getJSONArray("battleMaps").getString(0);
            infoList[i] = info;
        }


        return infoList;
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

    public static String getPlatform(int code)
    {
        switch (code){
            case Platform_IOS:
                return "苹果";
            case Platform_Android:
                return "安卓";
            case Platform_Bilibili:
                return "B站";
            default:break;
        }
        return "未知平台";
    }
}
