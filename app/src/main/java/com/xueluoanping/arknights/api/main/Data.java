package com.xueluoanping.arknights.api.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.xueluoanping.arknights.SimpleApplication;
import com.xueluoanping.arknights.api.tool.ToolTime;
import com.xueluoanping.arknights.global.Global;
import com.xueluoanping.arknights.pro.HttpConnectionUtil;
import com.xueluoanping.arknights.pro.SimpleTool;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Data {
    private static final String TAG = Data.class.getSimpleName();

    // http://ak.dzp.me/dst/items/SOCIAL_PT.webp
    // https://ak.dzp.me/dst/avatar/ASSISTANT/char_4043_erato.webp
    // https://assest.arknights.host/charpack/char_196_sunbr_summer_1.webp古米皮肤
    //    蒂蒂的图片网址
    //     https://github.com/Kengxxiao/ArknightsGameData/raw/master/en_US/gamedata/excel/item_table.json
    //    物品信息网址
    //     https://github.com/Kengxxiao/ArknightsGameData/raw/master/en_US/gamedata/excel/stage_table.json
    //    物品信息网址
    //     https://github.com/Kengxxiao/ArknightsGameData/raw/master/en_US/gamedata/excel/data_version.txt
    //    游戏数据版本地址

    // Czx
    //         获取announcement还要authority
    // @Czx https://ak.dzp.me/ann.json

    // https://api.arknights.host/Game/11/1
    public static class AccountData implements Serializable {
        public String nickName = HttpConnectionUtil.empty;
        public int level = 0;
        public int androidDiamond = 0;
        public int diamondShard = 0;
        public int gold = 0;
        public String secretary = HttpConnectionUtil.empty;
        public String secretarySkinId = HttpConnectionUtil.empty;
        public JSONObject inventory = HttpConnectionUtil.emptyJsonObject1;
        public long lastFreshTs=0;
        public long lastFreshTs_Inventory=0;
        public long lastFreshTs_Base=0;
        public long lastApAddTime=0;
        public int ap=0;
        public int maxAp=0;
        public AccountData() {
        }

        // public AccountData(JSONObject status) {
        //     try {
        //         this.nickName = status.getString("nickName");
        //         this.level = Integer.parseInt(status.getString("level"));
        //         this.androidDiamond = Integer.parseInt(status.getString("androidDiamond"));
        //         this.diamondShard = Integer.parseInt(status.getString("diamondShard"));
        //         this.gold = Integer.parseInt(status.getString("gold"));
        //         this.secretary = status.getString("secretary");
        //         this.secretarySkinId = status.getString("secretarySkinId");
        //         this.lastFreshTs = status.getLong("lastFreshTs");
        //         this.lastApAddTime=status.getLong("lastApAddTime")*1000L;
        //     } catch (Exception e) {
        //         e.printStackTrace();
        //         Log.d(TAG, "AccountData: "+status);
        //     }
        //
        //     try {
        //         this.inventory = status.getJSONObject("inventory");
        //     } catch (Exception e) {
        //         // e.printStackTrace();
        //         Log.d(TAG, "AccountData: 玩家容器为空");
        //     }
        //
        //     try {
        //         this.lastFreshTs_Inventory = status.getLong("lastFreshTs_Inventory");
        //         this.lastFreshTs_Base = status.getLong("lastFreshTs_Base");
        //     } catch (Exception e) {
        //         // e.printStackTrace();
        //     }
        // }

        // public JSONObject getJsonObject() {
        //     JSONObject storageInventory = new JSONObject();
        //     try {
        //         storageInventory.put("nickName", nickName);
        //         storageInventory.put("level", level);
        //         storageInventory.put("androidDiamond", androidDiamond);
        //         storageInventory.put("diamondShard", diamondShard);
        //         storageInventory.put("gold", gold);
        //         storageInventory.put("secretary", secretary);
        //         storageInventory.put("secretarySkinId", secretarySkinId);
        //         storageInventory.put("lastFreshTs", lastFreshTs);
        //         storageInventory.put("lastApAddTime", lastApAddTime);
        //     } catch (Exception e) {
        //         e.printStackTrace();
        //     }
        //     try {
        //         storageInventory.put("inventory", inventory);
        //     } catch (Exception e) {
        //         e.printStackTrace();
        //     }
        //     try {
        //         storageInventory.put("lastFreshTs_Inventory", lastFreshTs_Inventory);
        //         storageInventory.put("lastFreshTs_Base", lastFreshTs_Base);
        //     } catch (Exception e) {
        //         e.printStackTrace();
        //     }
        //     return storageInventory;
        // }


    }

    // {
    //     "code": 1,
    //         "data": null,
    //         "message": "请求成功，将开始识别仓库，请稍后.请勿滥用该API,谢谢"
    // }
    public static boolean requestForOCR(Context context,  final Game.GameInfo info) throws IOException {
        String urlStr = host.getQuickestHost() + "/Game/Ocr/" + info.account + "/" + info.platform;
        String GameJson = HttpConnectionUtil.post(urlStr,HttpConnectionUtil.emptyJsonObject, auth.getTokenMap(context));

        Timer ocrDetector =new Timer();
        ocrDetector.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    // Data.AccountData data0 = getBasicInfo(context, info);
                    // Data.AccountData data0_back = new Data.AccountData(Data.getOldDataTable(context,info));
                    // if (data0.lastFreshTs > data0_back.lastFreshTs)
                    {
                        // SimpleService.notifyUser(context,"已经仓库识别完成");
                        ocrDetector.cancel();
                        ocrDetector.purge();
                    }

                    Log.d(TAG, "run: 请求一次仓库");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },5*60*1000,5*60*1000);
        // 以后再处理报文
        return true;
    }

    public static AccountData getBasicInfo(Context context, Game.GameInfo info) throws IOException, JSONException {
        String urlStr = host.getQuickestHost() + "/Game/" + info.account + "/" + info.platform;
        String GameJson = HttpConnectionUtil.DownLoadTextPages(urlStr, auth.getTokenMap(context),true);

        Log.d(TAG, "getBasicInfo: " + GameJson);
        JSONObject jsonObject = new JSONObject(GameJson).getJSONObject("data");
        JSONObject status = jsonObject.getJSONObject("status");

        AccountData data = new AccountData();
        data.nickName = status.getString("nickName");
        data.level = Integer.parseInt(status.getString("level"));
        data.androidDiamond = Integer.parseInt(status.getString("androidDiamond"));
        data.diamondShard = Integer.parseInt(status.getString("diamondShard"));
        data.gold = Integer.parseInt(status.getString("gold"));
        data.secretary = status.getString("secretary");
        data.secretarySkinId = status.getString("secretarySkinId");
        data.lastApAddTime = status.getLong("lastApAddTime")*1000L;
        data.ap = status.getInt("ap");
        data.maxAp = status.getInt("maxAp");
        try {
            data.inventory = jsonObject.getJSONObject("inventory");
            data.lastFreshTs=jsonObject.getLong("lastFreshTs");

        } catch (Exception e) {
            // e.printStackTrace();
            Log.d(TAG, "getBasicInfo: 容器为空");
        }
        return data;

    }

    public static JSONObject getCharacterTable() throws JSONException, IOException {
        return getDataTable( "data/character_table.json");
    }

    public static JSONObject getItemTable() throws JSONException, IOException {
        return getDataTable( "data/item_table.json").getJSONObject("items");
    }

    public static JSONObject getStageTable() throws JSONException, IOException {
        return getDataTable("data/stage_table.json").getJSONObject("stages");
    }

    public static JSONObject getDataTable(String name) throws JSONException, IOException {
        // int size = (int) context.getAssets().openFd(name).getLength();
        Context context=SimpleApplication.getContext();
        File file = new File(context.getExternalCacheDir().getAbsolutePath()+"/" + name);
        int size = (int) file.length();
        byte[] cbuf = new byte[size];

        // InputStream is = context.getAssets().open(name);
        InputStream is = new FileInputStream(file);
        int len = is.read(cbuf);
        String text = new String(cbuf, 0, len);
        JSONTokener tokener = new JSONTokener(text);
        JSONObject object = new JSONObject(tokener);
        // Log.d(TAG, "getCharacterTable: ");
        return object;
    }

    // public static boolean saveOldDataTable(Context context, AccountData data) {
    //     SimpleTool.saveTextFile(context, data.getJsonObject().toString(), "user/" + SimpleTool.getUUID(Global.getSelectedGame().toString()) + ".json");
    //     return true;
    // }

    @SuppressLint("DefaultLocale")
    // hour
    public static float getDataIntervalTime(Context context,AccountData data0_back) {
        float interval = ((int) ((ToolTime.getTimeShanghai()- data0_back.lastFreshTs_Inventory)));
        Date date = new Date();
        date.setTime((long) interval);
        // Log.d(TAG, "getDataIntervalTime: " + String.format("%tT%n", date));
        // String[] times = String.format("%tT%n", date).split(":");
        float hour = date.getTime() / 3600000f;
        Log.d(TAG, "getDataIntervalTime: " + hour);
        // if (hour > 23) return "这段时间";
        // String.format("%.1f", hour) + "小时"
        return hour;
    }

    public static JSONObject getOldDataTable(Context context,Game.GameInfo account) throws JSONException, IOException {
        File file = new File(context.getExternalCacheDir().getAbsolutePath() + "/user/" + SimpleTool.getUUID(new Global.GlobalGameAccount(account).toString()) + ".json");
        int size = (int) file.length();
        if (size == 0) return HttpConnectionUtil.emptyJsonObject1;
        byte[] cbuf = new byte[size];
        InputStream is = new FileInputStream(file);
        int len = is.read(cbuf);
        String text = new String(cbuf, 0, len);
        JSONTokener tokener = new JSONTokener(text);
        JSONObject object = new JSONObject(tokener);
        // Log.d(TAG, "getCharacterTable: ");
        return object;
    }
}
