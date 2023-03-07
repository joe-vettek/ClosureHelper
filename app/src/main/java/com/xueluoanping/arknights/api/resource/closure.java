package com.xueluoanping.arknights.api.resource;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xueluoanping.arknights.SimpleApplication;
import com.xueluoanping.arknights.api.tool.ToolFile;
import com.xueluoanping.arknights.api.tool.ToolTable;
import com.xueluoanping.arknights.api.tool.ToolTime;
import com.xueluoanping.arknights.pages.SettingActivity;
import com.xueluoanping.arknights.pro.HttpConnectionUtil;
import com.xueluoanping.arknights.pro.SimpleTool;

import java.io.File;

public class closure {
    private static final String TAG = closure.class.getSimpleName();

    public static boolean checkUpdate() {
        try {
            String baseUrl = ToolFile.getBaseUrl();
            String itemTableFileName = "data/item_table.json";
            String stageTableFileName = "data/stage_table.json";
            String characterTableFileName = "data/character_table.json";
            String itemNameTableFileName = "data/item_name_table.json";
            String versionFileName = "data/data_version_dzp.txt";
            boolean flag = new File(baseUrl + itemTableFileName).exists()
                    && new File(baseUrl + stageTableFileName).exists()
                    && new File(baseUrl + characterTableFileName).exists()
                    && new File(baseUrl + itemNameTableFileName).exists();
            File file2 = new File(ToolFile.getBaseUrl() + versionFileName);
            if (flag && file2.exists()) {
                long time = Long.parseLong(ToolFile.getTextFile(versionFileName));
                if (ToolTime.getTimeShanghai() - time < 60 * 1000) return false;
                else return true;
            } else return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void updateFromArknights(Activity context, boolean needQuiet) {
        String baseUrl = context.getExternalCacheDir().getAbsolutePath() + "/";
        String versionFileName = "data/data_version_dzp.txt";
        String itemTableFileName = "data/item_table.json";
        String stageTableFileName = "data/stage_table.json";
        String characterTableFileName = "data/character_table.json";
        String itemNameTableFileName = "data/item_name_table.json";

        String url1 = "https://arknights.host/data/Stage.json";
        String url2 = "https://arknights.host/data/Items.json";
        File file;
        if (!needQuiet) {
            SimpleTool.toastInThread(context, "正在通过可露希尔网页获取临时更新！");
            SimpleTool.toastInThread(context, "正在更新关卡清单！");
        }
        file = new File(baseUrl + stageTableFileName);
        try {
            String vText = HttpConnectionUtil.DownLoadTextPages(url1, null, false);
            // vText.replace("ap", "apCost");
            // vText = "{\"stages\":{" + vText + "}";
            Gson g = new Gson();
            JsonObject json = g.fromJson(vText, JsonObject.class);
            JsonObject jsonNew = new JsonObject();
            // json = json.getAsJsonObject("stages");
            json.entrySet().forEach(entry -> {
                String id = entry.getKey();
                StageConvert convert = new StageConvert(entry.getValue().getAsJsonObject(), id.contains("tough"));
                jsonNew.addProperty(id, g.toJson(convert));

            });
            String s2 = "{\"stages\":" + jsonNew.toString()
                    .replace("\\\"", "\"")
                    .replace("\"{", "{")
                    .replace("}\"", "}")
                    + "}";
            ToolFile.saveTextFile(context, s2, stageTableFileName);
            ToolTable.getInstance().updateStageTable();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "updateArknightsDataVersion: " + file.getAbsolutePath());
            SimpleTool.toastInThread(context, "关卡清单更新出错！");
            return;
        }
        if (!needQuiet) {
            SimpleTool.toastInThread(context, "正在更新物品清单！");
        }
        file = new File(baseUrl + itemTableFileName);
        try {
            String vText = HttpConnectionUtil.DownLoadTextPages(url2, null, false);
            // vText= vText.replace("name", "itemId");
            vText = vText.replace("icon", "iconId");
            vText = "{\"items\":" + vText + "}";
            ToolFile.saveTextFile(context, vText, itemTableFileName);
            ToolTable.getInstance().updateItemTable();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "updateArknightsDataVersion: " + file.getAbsolutePath());
            SimpleTool.toastInThread(context, "物品清单更新出错！");
            return;
        }
        file = new File(baseUrl + itemNameTableFileName);
        boolean r = Kengxxiao.exportNameUUIDToIconId(file, new File(baseUrl + itemTableFileName));
        if (r) ToolTable.getInstance().updateItemNameTable();

        ToolFile.saveTextFile(SimpleApplication.getContext(), ToolTime.getTimeShanghai() + "", versionFileName);
        if (!needQuiet) {
            SimpleTool.toastInThread(context, "资源刷新完成！");
        }
    }

    public static class StageConvert {
        public String name;
        public String code;
        public String description;
        public String diffGroup;
        public String apCost;
        public JsonObject stageDropInfo;

        public StageConvert(JsonObject jsonObject, boolean TOUGH) {

            this.name = jsonObject.get("name").getAsString();
            this.code = jsonObject.get("code").getAsString();
            this.description = "";
            if (TOUGH)
                this.diffGroup = "TOUGH";
            else this.diffGroup = "NORMAL";
            this.apCost = jsonObject.get("ap").getAsString();
            this.stageDropInfo = new JsonObject();
            JsonArray jsonArray = jsonObject.getAsJsonArray("items");
            JsonArray jsonArray0 = new JsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {

                String id = jsonArray.get(i).getAsString();
                JsonObject t0 = new JsonObject();
                t0.addProperty("id", id);
                t0.addProperty("dropType", 2);
                t0.addProperty("occPercent", -1);
                jsonArray0.add(t0);

            }
            this.stageDropInfo.add("displayDetailRewards", jsonArray0);
        }


    }
}
