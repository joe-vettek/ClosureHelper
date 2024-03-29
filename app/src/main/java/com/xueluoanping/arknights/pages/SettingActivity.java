package com.xueluoanping.arknights.pages;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.Nullable;

import com.xueluoanping.arknights.R;
import com.xueluoanping.arknights.SimpleApplication;
import com.xueluoanping.arknights.api.main.host;
import com.xueluoanping.arknights.api.resource.Kengxxiao;
import com.xueluoanping.arknights.api.resource.closure;
import com.xueluoanping.arknights.api.resource.monster_siren;
import com.xueluoanping.arknights.base.BaseActivity;
import com.xueluoanping.arknights.pro.spTool;

import java.io.File;

public class SettingActivity extends BaseActivity {
    private static final String TAG = SettingActivity.class.getSimpleName();

    private Switch se_sw_autoWarehouseIdentification;
    private Switch se_sw_autoLogin;
    private Switch se_sw_startMusic;
    private Button bt_updateDetect;
    private Spinner sp_lineSelect;
    // private Spinner sp_resourceSelect;
    private Spinner sp_musicSelect;
    private ImageView bt_exit;

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
        se_sw_startMusic.setChecked(spTool.getStartMusic());
        sp_lineSelect.setSelection(spTool.getLineSelect());
        // sp_resourceSelect.setSelection(spTool.getResourceSelect());
        sp_musicSelect.setSelection(spTool.getMusicSelect());
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

        se_sw_startMusic.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            spTool.saveStartMusic(isChecked);
        }));
        bt_updateDetect.setOnClickListener(view -> {
            // ((SimpleApplication) SimpleApplication.getContext()).restart();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // if (spTool.getResourceSelect() == 0)
                        closure.updateFromArknights(SettingActivity.this, false);
                    // else Kengxxiao.updateArknightsDataVersion(SettingActivity.this);
                }
            }).start();

        });

        sp_lineSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spTool.setLineSelect(position);
                host.setQuickestHost(host.baseHosts[position].getKey());
                // toastInThread("已经切换到"+getResources().getStringArray(R.array.line_type)[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // sp_resourceSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        //     @Override
        //     public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //         spTool.setResourceSelect(position);
        //     }
        //     @Override
        //     public void onNothingSelected(AdapterView<?> parent) {
        //     }
        // });

        sp_musicSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    int musicIdNew = Integer.parseInt(getResources().getTextArray(R.array.music_type_id)[position] + "");
                    // 这里存顺序，以免找起来麻烦
                    // if (spTool.getMusicSelect() != position)
                    {
                        spTool.setMusicSelect(position);
                        if (musicIdNew!=-100&&position > 0 && !new File(monster_siren.getMusicFilePos(musicIdNew)).exists()) {
                            new Thread(() -> {
                                toastInThread("开始下载");
                                boolean result = monster_siren.downloadFile(musicIdNew);
                                Log.d(TAG, "下载音乐: " + result);
                                if (result) toastInThread("下载成功");
                                else toastInThread("该曲目暂无设置");
                            }).start();
                        }
                    }
                } catch (Exception e) {
                    // e.printStackTrace();
                    toastInThread("该曲目暂无设置");
                }
                // toastInThread("已经切换到"+getResources().getStringArray(R.array.resource_type)[position]+"，正在重启！");
                // SimpleApplication.restartNow();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        bt_exit.setOnClickListener(v -> finish());

    }

    private void bindComponents() {
        se_sw_autoWarehouseIdentification = findViewById(R.id.se_sw_autoWarehouseIdentification);
        se_sw_autoLogin = findViewById(R.id.se_sw_autoLogin);
        se_sw_startMusic = findViewById(R.id.se_sw_startMusic);
        bt_updateDetect = findViewById(R.id.bt_updateDetect);
        sp_lineSelect = findViewById(R.id.sp_lineSelect);
        // sp_resourceSelect = findViewById(R.id.sp_resourceSelect);
        sp_musicSelect = findViewById(R.id.sp_musicSelect);
        bt_exit = findViewById(R.id.iv_back3);

    }


}
