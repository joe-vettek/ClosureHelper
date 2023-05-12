package com.xueluoanping.arknights.pages;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xueluoanping.arknights.R;
import com.xueluoanping.arknights.api.main.Game;
import com.xueluoanping.arknights.api.resource.Kengxxiao;
import com.xueluoanping.arknights.api.resource.penguin_stats;
import com.xueluoanping.arknights.api.tool.ToolTable;
import com.xueluoanping.arknights.base.BaseActivity;
import com.xueluoanping.arknights.custom.stage.StageAdapter;
import com.xueluoanping.arknights.custom.stage.StageModel;
import com.xueluoanping.arknights.pro.SimpleTool;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameSettingsActivity extends BaseActivity {

    private static final String TAG = GameSettingsActivity.class.getSimpleName();
    private EditText et_ktkLimit;
    private EditText et_apLimit;
    private EditText et_search;
    private Switch sw_changeShift;
    private Switch sw_recruitBot;
    private Switch sw_hostBattle;
    private Spinner sp_accelerateSlot_CN;
    private TextView tv_battleMaps;
    private Button bt_submit;
    private RecyclerView rc_battleSelect;
    private StageAdapter ad_battleSelect;
    private JsonElement stageTable;
    private JsonObject itemTable;
    private List<StageModel> stagesListAll = new ArrayList<>();
    private List<StageModel> stagesList = new ArrayList<>();
    private List<String> stageStringsList;
    private Button bt_search;
    private Game.GameSettings oldSetting = new Game.GameSettings();
    private Game.GameInfo gameInstanceInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamesettings);

        bindComponents();
        loadData();

        initData();
        initStageData();
        setListeners();


    }

    public void initStageData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // try {
                //     stageTable = new JsonParser().parse(Data.getStageTable().toString());
                //     itemTable = Data.getItemTable();
                // } catch (JSONException | IOException e) {
                //     e.printStackTrace();
                // }
                try {
                    while (stageStringsList == null) {
                        Thread.sleep(500);
                        Log.d(TAG, "run: 等待数据下载500ms");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                stagesListAll.clear();
                // forEachRemaining只能用一次
                if (stageTable == null) {
                    toastInThread("资源加载异常，请稍后重试或尝试修复！");
                    ToolTable.initInstance();
                    return;
                }
                Set<Map.Entry<String, JsonElement>> es = stageTable.getAsJsonObject().entrySet();
                JsonObject jsonObject = stageTable.getAsJsonObject();

                // 加载掉落统计
                JsonArray jsonArray = ToolTable.getInstance().getMatrixTable();

                es.forEach(key -> {
                    try {
                        JsonObject stageJsonObject = jsonObject.get(key.getKey()).getAsJsonObject();
                        StageModel stageModel = new StageModel();
                        stageModel.setStageId(key.getKey().toString());

                        stageModel.setName0(stageJsonObject.get("name").getAsString());
                        stageModel.setCode(stageJsonObject.get("code").getAsString());
                        if (stageJsonObject.has("description"))
                            stageModel.setDescription(stageJsonObject.get("description").getAsString());
                        stageModel.setDiffGroup(stageJsonObject.get("diffGroup").getAsString());
                        stageModel.setApCost(stageJsonObject.get("apCost").getAsInt());
                        stageModel.setOpen(Kengxxiao.isStageOpen(key.getKey()) != 0);
                        JsonArray drops = stageJsonObject.get("stageDropInfo").getAsJsonObject().get("displayDetailRewards").getAsJsonArray();
                        if (drops.size() > 0)
                            for (int i = 0; i < drops.size(); i++) {
                                JsonObject drop = drops.get(i).getAsJsonObject();
                                try {
                                    if (drop.get("dropType").getAsInt() == 2 || drop.get("dropType").getAsInt() == 3) {
                                        String id = drop.get("id").getAsString();
                                        if (itemTable.has(id)) {
                                            String name = itemTable.get(id).getAsJsonObject().get("name").getAsString();
                                            String iconId = itemTable.get(id).getAsJsonObject().get("iconId").getAsString();
                                            int OccPercent = drop.get("occPercent").getAsInt();
                                            StageModel.Reward stringStringEntry = new StageModel.Reward();
                                            stringStringEntry.setItemId(id);
                                            stringStringEntry.setName0(name);
                                            stringStringEntry.setIconId(iconId);
                                            if (jsonArray == null)
                                                stringStringEntry.setOccPercent(OccPercent);
                                            else {
                                                stringStringEntry.setOccPercent(penguin_stats.getRealOccPercent(key.getKey(), id), OccPercent);
                                            }

                                            stageModel.displayRewards.add(stringStringEntry);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            // 可露希尔没有统计的时候求助企鹅物流
                        else {
                            if (key.getKey().contains("act24side"))
                                stageModel.displayRewards.addAll(penguin_stats.getDropsByStatics(key.getKey()));
                        }
                        AtomicBoolean isSelected = new AtomicBoolean(false);
                        stageStringsList.forEach(s -> {
                            if (stageModel.getStageId().equals(s))
                                isSelected.set(true);
                        });
                        // if(key.getKey().equals("act24side_08")){
                        //     Log.d(TAG, "run: "+key.getValue());
                        // };
                        if (isSelected.get())
                            stageModel.setSelected(true);
                        if (stageModel.displayRewards.size() > 0
                            // || key.getKey().contains("act24side")
                            // ||stageModel.isOpen()
                        )
                            stagesListAll.add(stageModel);

                    } catch (Exception e) {
                        Log.d(TAG, "run: 关卡数据读取错误" + key.getKey());
                        e.printStackTrace();
                    }
                });
                Collections.reverse(stagesListAll);
                Log.d(TAG, "initStageData: " + stagesListAll.size());

            }
        }
        ).start();
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    try {
                        stageTable = new JsonParser().parse(ToolTable.getInstance().getStageTable().toString());
                        // itemTable = Data.getItemTable();
                        itemTable = ToolTable.getInstance().getItemTable();
                        // initStageData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String url = getExternalCacheDir().getAbsolutePath()
                            + "/user/" + SimpleTool.getUUID(gameInstanceInfo.toString()) + "_Settings.json";
                    final Game.GameSettings[] gameSettings = new Game.GameSettings[]{new Game.GameSettings()};
                    try {
                        gameSettings[0] = Game.getGameSettings(gameInstanceInfo);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        toastInThread("网络异常或未启动托管！");
                        runOnUiThread(() -> {finish();});
                        // 后面这里好像暂时没用上，似乎是以前的可以离线时发送的写法
                        // try {
                        //     gameSettings[0] = Game.getGameSettings_Json(SimpleTool.getJson(url));
                        // } catch (JSONException | IOException ex) {
                        //     ex.printStackTrace();
                        // }
                    }
                    oldSetting = gameSettings[0];
                    final StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("当前战斗序列：");

                    int pos = 3;
                    String[] slotArray = getResources().getStringArray(R.array.accelerateSlot_CN_type);
                    for (int i = 0; i < slotArray.length; i++) {
                        if (slotArray[i].equals(oldSetting.accelerateSlot_CN)) {
                            pos = i;
                            break;
                        }
                    }
                    int finalPos = pos;
                    runOnUiThread(()-> sp_accelerateSlot_CN.setSelection(finalPos, false));

                    gameSettings[0].battleMaps.forEach(map -> {
                        try {
                            JsonObject stageJsonObject = stageTable.getAsJsonObject().get(map).getAsJsonObject();
                            stringBuilder.append(stageJsonObject.get("code").getAsString() + "(" + stageJsonObject.get("name").getAsString() + ")；");

                        } catch (Exception e) {
                            Log.d(TAG, "run: unknownMap" + map);
                            e.printStackTrace();
                        }
                    });

                    stageStringsList = gameSettings[0].battleMaps;
                    runOnUiThread(() -> {
                        try {
                            et_apLimit.setText(gameSettings[0].keepingAP + "");
                            et_ktkLimit.setText(gameSettings[0].recruitReserve + "");
                            sw_changeShift.setChecked(gameSettings[0].enableBuildingArrange);
                            sw_recruitBot.setChecked(gameSettings[0].recruitIgnoreRobot);
                            sw_hostBattle.setChecked(gameSettings[0].isAutoBattle);
                            tv_battleMaps.setText(stringBuilder);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d(TAG, "run: " + gameSettings[0].recruitIgnoreRobot);
                        }


                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    stageStringsList = new ArrayList<>();
                }

            }
        }).start();


    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners() {

        ad_battleSelect.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                StageModel stageModel = stagesList.get(position);
                stageModel.setSelected(!stageModel.isSelected());
                adapter.notifyItemChanged(position);
                Log.d(TAG, "onItemClick: " + stageModel + stageModel.isSelected());
                if (stageModel.isSelected()) {
                    stageStringsList.add(stageModel.getStageId());
                    tv_battleMaps.append(stageModel.toString());
                } else {
                    stageStringsList.remove(stageModel.getStageId());
                    tv_battleMaps.setText(tv_battleMaps.getText().toString().replace(stageModel.toString(), ""));
                }
            }
        });
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Game.GameSettings gameSettings = new Game.GameSettings();
                gameSettings.isAutoBattle = sw_hostBattle.isChecked();
                gameSettings.recruitIgnoreRobot = sw_recruitBot.isChecked();
                gameSettings.enableBuildingArrange = sw_changeShift.isChecked();
                gameSettings.keepingAP = Integer.parseInt(et_apLimit.getText().toString());
                gameSettings.recruitReserve = Integer.parseInt(et_ktkLimit.getText().toString());
                gameSettings.battleMaps = stageStringsList;
                gameSettings.isStopped = oldSetting.isStopped;

                gameSettings.accelerateSlot_CN = getResources().getStringArray(R.array.accelerateSlot_CN_type)[sp_accelerateSlot_CN.getSelectedItemPosition()];

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (Game.updateGameSettings(
                                    gameInstanceInfo, gameSettings)) {
                                finish();
                                toastInThread("更新成功！");
                            } else {
                                toastInThread("网络有误，请重试！");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            toastInThread("网络有误，请重试！");
                        }
                    }
                }).start();

            }
        });
        bt_search.setOnLongClickListener((v -> {
            hideInputMethod();
            // 清空显示
            tv_battleMaps.setText("当前战斗序列：");
            et_search.setText("");
            stagesList.clear();
            ad_battleSelect.setList(stagesList);
            ad_battleSelect.notifyDataSetChanged();
            // 清空记录
            stagesListAll.forEach(stageModel -> {
                stageModel.setSelected(false);
            });
            stageStringsList.clear();
            Toast.makeText(this, "已清空战斗序列", Toast.LENGTH_SHORT).show();
            return true;
        }));
        bt_search.setOnClickListener(new View.OnClickListener() {
            String cache = "***";
            boolean hasHint = false;

            @Override
            public void onClick(View view) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                // 隐藏软键盘
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

                // 正则前面的 (?i)表示无视大小写
                String regex = et_search.getText().toString();
                if (regex.isEmpty()) {
                    // getsong();
                    // adapter.setList(datas);
                    stagesList.clear();
                    ad_battleSelect.setList(stagesList);
                    toastInThread("请输入内容！");
                    return;
                }
                if (!hasHint) {
                    // toastInThread("长按可以清空全部战斗序列！");
                    hasHint = true;
                } else {
                    // toastInThread("正在查找！");
                }
                // 当字数在增加时避免重复搜索，增大压力
                if (!regex.contains(cache) || stagesList.size() == 0) {
                    try {
                        // stagesList = SimpleTool.deepCopy(stagesListAll);
                        stagesList.clear();
                        stagesList.addAll(stagesListAll);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // regex = "(?i)[\\s\\S]*" + regex + "[\\s\\S]*";
                regex = regex.toLowerCase();
                // 正则太卡了，还是不用正则，大小写问题统统lowercase
                ArrayList<StageModel> result = new ArrayList<>();
                for (int i = 0; i < stagesList.size(); i++) {
                    StageModel s = stagesList.get(i);
                    if (s.getCode().toLowerCase().contains(regex)
                            || s.getName0().toLowerCase().contains(regex)
                            || s.containsDrop(regex)) {
                        result.add(s);
                    }
                }

                cache = regex;
                Log.d(TAG, "onClick: " + result.size() + regex);
                try {
                    // stagesList = SimpleTool.deepCopy(result);
                    stagesList.clear();

                    stagesList.addAll(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                try {
                    ad_battleSelect.setList(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void bindComponents() {
        et_apLimit = findViewById(R.id.et_apLimit);
        et_ktkLimit = findViewById(R.id.et_ktkLimit);
        et_search = findViewById(R.id.et_search);
        bt_submit = findViewById(R.id.bt_submit);
        bt_search = findViewById(R.id.bt_search);
        sw_changeShift = findViewById(R.id.sw_changeShift);
        sw_recruitBot = findViewById(R.id.sw_recruitBot);
        sw_hostBattle = findViewById(R.id.sw_hostBattle);
        sp_accelerateSlot_CN = findViewById(R.id.sp_accelerateSlot_CN);
        tv_battleMaps = findViewById(R.id.tv_battleMaps);
        rc_battleSelect = findViewById(R.id.rc_battleSelect);
    }

    private void loadData() {
        //创建布局管理
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rc_battleSelect.setLayoutManager(layoutManager);
        //        View view = LayoutInflater.from(this).inflate(R.layout.item_hr_diary, null);
        //创建适配器
        ad_battleSelect = new StageAdapter(R.layout.ic_battle, stagesList);
        rc_battleSelect.setAdapter(ad_battleSelect);

        //    加载用户实例数据
        gameInstanceInfo = new Game.GameInfo().backupFromIntentBundle(getIntent());
    }

    // public void toastInThread(String s) {
    //     runOnUiThread(() -> Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show());
    // }
}
