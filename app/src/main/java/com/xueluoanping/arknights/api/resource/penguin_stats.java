package com.xueluoanping.arknights.api.resource;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.xueluoanping.arknights.SimpleApplication;
import com.xueluoanping.arknights.api.tool.ToolFile;
import com.xueluoanping.arknights.api.tool.ToolTable;
import com.xueluoanping.arknights.api.tool.ToolTime;
import com.xueluoanping.arknights.pro.HttpConnectionUtil;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class penguin_stats {
    private static final String TAG = penguin_stats.class.getSimpleName();

    private static final String url_matrixAll = "https://penguin-stats.cn/PenguinStats/api/v2/_private/result/matrix/CN/global/all";
    public static final String fileName_matrixAll = "data/penguin/matrix/all.json";
    private static final String fileName_version = "data/penguin/version.txt";

    // 同步
    public static void refreshDropsData() throws IOException, JsonSyntaxException {
        String t = HttpConnectionUtil.DownLoadTextPages(url_matrixAll, null, true);
        ToolFile.saveTextFile(SimpleApplication.getContext(), t, fileName_matrixAll);
        ToolFile.saveTextFile(SimpleApplication.getContext(), ToolTime.getTimeShanghai() + "", fileName_version);
        ToolTable.getInstance().updateMatrixTable();
        Log.d(TAG, "refreshDropsData: 刷新成功");
    }

    public static boolean checkUpdate()  {
        try {
            String filePath = ToolFile.getBaseUrl() + fileName_matrixAll;
            String versionFilePath = ToolFile.getBaseUrl() + fileName_version;
            File file = new File(filePath);
            File file2 = new File(versionFilePath);
            if (file.exists()&&file2.exists()) {
                long time = Long.parseLong(ToolFile.getTextFile(fileName_version));
                if (ToolTime.getTimeShanghai() - time < 24 * 60 * 1000) return false;
                else  return true;
            } else return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
       return true;
    }

    public static String getRealOccPercent(String stageId, String itemId) {
        String result = "";
        if (!ToolTable.getInstance().hasCompleteInit()) result = "";
        else {
            JsonArray a = ToolTable.getInstance().getMatrixTable();
            int size = a.size();
            for (int i = 0; i < size; i++) {
                JsonObject j = a.get(i).getAsJsonObject();
                String s0 = j.get("stageId").getAsString();
                // act17side_04 企鹅物流后面加了后缀
                if (!s0.contains(stageId)) continue;
                String i0 = j.get("itemId").getAsString();
                if (!i0.contains(itemId)) continue;
                int times = j.get("times").getAsInt();
                int quantity = j.get("quantity").getAsInt();
                if (times > 0)
                    result = String.format(Locale.CHINA, "%.1f", quantity * 100f / (float) times) + "%";
                else result = "0%";
            }
        }
        return result;
    }

}
