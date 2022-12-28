package com.xueluoanping.arknights.pages;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xueluoanping.arknights.R;
import com.xueluoanping.arknights.pro.HttpConnectionUtil;
import com.xueluoanping.arknights.pro.SimpleTool;
import com.xueluoanping.arknights.pro.spTool;

import java.io.File;
import java.io.IOException;

public class SettingActivity extends AppCompatActivity {
    private static final String TAG = SettingActivity.class.getSimpleName();

    private Switch se_sw_autoWarehouseIdentification;
    private Switch se_sw_autoLogin;
    private Button bt_updateDetect;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        bindComponents();
        initStatus();
        setListeners();
    }

    private void initStatus() {

        se_sw_autoWarehouseIdentification.setChecked(spTool.getAutoWarehouseIdentification());
        se_sw_autoLogin.setChecked(spTool.getQuickLogin());

    }

    private void setListeners() {

        se_sw_autoWarehouseIdentification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                spTool.saveAutoWarehouseIdentification(b);
            }
        });
        se_sw_autoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                spTool.saveQuickLogin(b);
            }
        });

        bt_updateDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String base = getExternalCacheDir().getAbsolutePath() + "/";
                            String vText = SimpleTool.getText(base + "data/data_version.txt");
                            Log.d(TAG, "run: 更新检查 " + vText);
                            int[] v = stringArrayToIntegerArray(
                                    vText.split("\n")[2]
                                            .replace("VersionControl:", "")
                                            // split特殊字符要转义
                                            .split("\\."));
                            String url = "https://cdn.jsdelivr.net/gh/Kengxxiao/ArknightsGameData@master/zh_CN/gamedata/excel/data_version.txt";
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
                                    SimpleTool.toastInThread(SettingActivity.this, "需要更新至" + versionRemote);
                                    updateArknightsDataVersion();
                                } else SimpleTool.toastInThread(SettingActivity.this, "当前已经是最新版本！");

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    private void bindComponents() {
        se_sw_autoWarehouseIdentification = findViewById(R.id.se_sw_autoWarehouseIdentification);
        se_sw_autoLogin = findViewById(R.id.se_sw_autoLogin);
        bt_updateDetect = findViewById(R.id.bt_updateDetect);
    }

    private int[] stringArrayToIntegerArray(String[] a) {
        int[] i = new int[a.length];
        for (int j = 0; j < a.length; j++) {
            i[j] = Integer.parseInt(a[j]);
        }
        return i;
    }

    // https://raw.fastgit.org/Kengxxiao/ArknightsGameData/master/zh_CN/gamedata/excel/item_table.json
    private void updateArknightsDataVersion() {
        String baseUrl = getExternalCacheDir().getAbsolutePath() + "/";
        String versionFileName = "data/data_version.txt";
        String itemTableFileName = "data/item_table.json";
        String stageTableFileName = "data/stage_table.json";
        String characterTableFileName = "data/character_table.json";
        String baseUrlRemote = "https://cdn.jsdelivr.net/gh/Kengxxiao/ArknightsGameData@master/zh_CN/gamedata/excel/";

        File file;
        SimpleTool.toastInThread(this, "正在更新物品清单！");
        file = new File(baseUrl + itemTableFileName);
        try {
            String vText = HttpConnectionUtil.DownLoadTextPages(baseUrlRemote + itemTableFileName.replace("data/", ""), null, false);
            SimpleTool.saveTextFile(this, vText, itemTableFileName);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "updateArknightsDataVersion: " + file.getAbsolutePath());
            SimpleTool.toastInThread(this, "物品清单更新出错！");
            return;
        }
        SimpleTool.toastInThread(this, "正在更新关卡清单！");
        file = new File(baseUrl + stageTableFileName);
        try {
            String vText = HttpConnectionUtil.DownLoadTextPages(baseUrlRemote + stageTableFileName.replace("data/", ""), null, false);
            SimpleTool.saveTextFile(this, vText, stageTableFileName);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "updateArknightsDataVersion: " + file.getAbsolutePath());
            SimpleTool.toastInThread(this, "关卡清单更新出错！");
            return;
        }
        SimpleTool.toastInThread(this, "正在更新角色清单！");
        file = new File(baseUrl + characterTableFileName);
        try {
            String vText = HttpConnectionUtil.DownLoadTextPages(baseUrlRemote + characterTableFileName.replace("data/", ""), null, false);
            SimpleTool.saveTextFile(this, vText, characterTableFileName);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "updateArknightsDataVersion: " + file.getAbsolutePath());
            SimpleTool.toastInThread(this, "角色清单更新出错！");
            return;
        }
        SimpleTool.toastInThread(this, "正在更新版本控制文件！");
        file = new File(baseUrl + versionFileName);
        try {
            String vText = HttpConnectionUtil.DownLoadTextPages(baseUrlRemote + versionFileName.replace("data/", ""), null, false);
            SimpleTool.saveTextFile(this, vText, versionFileName);
            SimpleTool.toastInThread(this, "当前已经是最新版本！");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "updateArknightsDataVersion: " + file.getAbsolutePath());
            SimpleTool.toastInThread(this, "版本控制文件更新出错！");
            file.delete();
        }

    }

}
