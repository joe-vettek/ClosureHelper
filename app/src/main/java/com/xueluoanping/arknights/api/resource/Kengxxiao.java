package com.xueluoanping.arknights.api.resource;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.xueluoanping.arknights.SimpleApplication;
import com.xueluoanping.arknights.api.tool.ToolFile;
import com.xueluoanping.arknights.api.tool.ToolTable;
import com.xueluoanping.arknights.api.tool.ToolTime;
import com.xueluoanping.arknights.pro.HttpConnectionUtil;
import com.xueluoanping.arknights.pro.SimpleTool;
import com.xueluoanping.arknights.pro.spTool;

import java.io.File;

public class Kengxxiao {
    private static final String TAG = Kengxxiao.class.getSimpleName();

    // 注意这里采用了线程的手法，如果资源有变动，需要进行更新
    public static void checkforResource(Activity context) {
        switch (spTool.getResourceSelect()) {
            case 0:
                new Thread(() -> {
                    if (closure.checkUpdate()) closure.updateFromArknights(context,true);
                }).start();
                break;
            case 1:
                new Thread(() -> {
                    try {
                        String base = context.getExternalCacheDir().getAbsolutePath() + "/";
                        String vText = SimpleTool.getText(base + "data/data_version.txt");
                        Log.d(TAG, "run: 更新检查 " + vText);
                        int[] v = stringArrayToIntegerArray(
                                vText.split("\n")[2]
                                        .replace("VersionControl:", "")
                                        // split特殊字符要转义
                                        .split("\\."));
                        String url = "https://gcore.jsdelivr.net/gh/Kengxxiao/ArknightsGameData@master/zh_CN/gamedata/excel/data_version.txt";
                        String vGitText = HttpConnectionUtil.DownLoadTextPages(url, null, false);
                        try {
                            Log.d(TAG, vText + "run: 更新检查" + vGitText);
                            String versionRemote = vGitText.split("\n")[2].replace("VersionControl:", "");
                            int[] v2 = stringArrayToIntegerArray(versionRemote.split("\\."));

                            boolean needUpdate = false;
                            if (v2[0] > v[0]) needUpdate = true;
                            else if (v2[1] > v[1]) needUpdate = true;
                            else if (v2[2] > v[2]) needUpdate = true;

                            if (needUpdate) {
                                SimpleTool.toastInThread(context, "需要更新至" + versionRemote);
                                updateArknightsDataVersion(context);
                                ToolTable.initInstance();
                            } else {
                                // SimpleTool.toastInThread(context, "当前已经是最新版本（" + vText.split("\n")[1].split(" on ")[1] + "）！");
                            }

                        } catch (Exception e) {
                            SimpleTool.toastInThread(context, "更新检查出错！");
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        SimpleTool.toastInThread(context, "更新检查出错！");
                        e.printStackTrace();
                    }

                }).start();
                break;
            default:
                break;
        }

        new Thread(() -> {
            try {
                if (penguin_stats.checkUpdate()) {
                    penguin_stats.refreshDropsData();
                    // SimpleTool.toastInThread(context, "企鹅物流数据已更新！");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static int[] stringArrayToIntegerArray(String[] a) {
        int[] i = new int[a.length];
        for (int j = 0; j < a.length; j++) {
            i[j] = Integer.parseInt(a[j]);
        }
        return i;
    }

    // https://raw.fastgit.org/Kengxxiao/ArknightsGameData/master/zh_CN/gamedata/excel/item_table.json
    public static void updateArknightsDataVersion(Activity context) {
        String baseUrl = ToolFile.getBaseUrl();
        String versionFileName = "data/data_version.txt";
        String itemTableFileName = "data/item_table.json";
        String stageTableFileName = "data/stage_table.json";
        String characterTableFileName = "data/character_table.json";
        String itemNameTableFileName = "data/item_name_table.json";
        String baseUrlRemote = "https://gcore.jsdelivr.net/gh/Kengxxiao/ArknightsGameData@master/zh_CN/gamedata/excel/";
        // 用不了
        // baseUrlRemote="https://raw.fastgit.org/gh/Kengxxiao/ArknightsGameData/zh_CN/gamedata/excel/";
        File file;
        SimpleTool.toastInThread(context, "正在更新物品清单！");
        file = new File(baseUrl + itemTableFileName);
        try {
            String vText = HttpConnectionUtil.DownLoadTextPages(baseUrlRemote + itemTableFileName.replace("data/", ""), null, false);
            ToolFile.saveTextFile(context, vText, itemTableFileName);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "updateArknightsDataVersion: " + file.getAbsolutePath());
            SimpleTool.toastInThread(context, "物品清单更新出错！");
            return;
        }
        SimpleTool.toastInThread(context, "正在更新关卡清单！");
        file = new File(baseUrl + stageTableFileName);
        try {
            String vText = HttpConnectionUtil.DownLoadTextPages(baseUrlRemote + stageTableFileName.replace("data/", ""), null, false);
            ToolFile.saveTextFile(context, vText, stageTableFileName);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "updateArknightsDataVersion: " + file.getAbsolutePath());
            SimpleTool.toastInThread(context, "关卡清单更新出错！");
            return;
        }
        SimpleTool.toastInThread(context, "正在更新角色清单！");
        file = new File(baseUrl + characterTableFileName);
        try {
            String vText = HttpConnectionUtil.DownLoadTextPages(baseUrlRemote + characterTableFileName.replace("data/", ""), null, false);
            ToolFile.saveTextFile(context, vText, characterTableFileName);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "updateArknightsDataVersion: " + file.getAbsolutePath());
            SimpleTool.toastInThread(context, "角色清单更新出错！");
            return;
        }
        SimpleTool.toastInThread(context, "正在更新版本控制文件！");
        file = new File(baseUrl + versionFileName);
        try {
            String vText = HttpConnectionUtil.DownLoadTextPages(baseUrlRemote + versionFileName.replace("data/", ""), null, false);
            ToolFile.saveTextFile(context, vText, versionFileName);
            SimpleTool.toastInThread(context, "当前已经是最新版本！");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "updateArknightsDataVersion: " + file.getAbsolutePath());
            SimpleTool.toastInThread(context, "版本控制文件更新出错！");
            file.delete();
        }

        file = new File(baseUrl + itemNameTableFileName);
        exportNameUUIDToIconId(file, new File(baseUrl + itemTableFileName));

    }

    public static boolean exportNameUUIDToIconId(File file, File old) {
        String itemNameTableFileName = "data/item_name_table.json";
        boolean result = false;
        try {
            JsonObject j0 = SimpleTool.getGsonObject(SimpleTool.getText(old.getAbsolutePath())).getAsJsonObject("items");
            JsonObject j1 = new JsonObject();

            j0.keySet().forEach((s -> {
                JsonObject jtmp = j0.getAsJsonObject(s);
                if (jtmp.has("name")) {
                    j1.addProperty(SimpleTool.getUUID(jtmp.get("name").getAsString()).toString(),
                            jtmp.get("iconId").getAsString());
                }
            }));
            ToolFile.saveTextFile(SimpleApplication.getContext(), j1.toString(), itemNameTableFileName);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "checkArknightsDataVersion: " + file.getAbsolutePath());
        }
        return result;
    }


    // -1代表未知，0代表关闭
    public static int isStageOpen(String stageId) {
        JsonObject j = ToolTable.getInstance().getStageValidInfoTable();
        if (j != null) {
            if (j.has(stageId)) {
                JsonObject jo = j.getAsJsonObject(stageId);
                // try {
                long s0 = jo.get("startTs").getAsLong() * 1000;
                long s2 = jo.get("endTs").getAsLong() * 1000;
                long now = ToolTime.getTimeShanghai();
                if (s2 > 0) {
                    if (now > s0 && now < s2) return 1;
                    else return 0;
                } else return 1;
                // } catch (Exception e) {
                //     e.printStackTrace();
                // }

            }
        }
        return -1;
    }
}
