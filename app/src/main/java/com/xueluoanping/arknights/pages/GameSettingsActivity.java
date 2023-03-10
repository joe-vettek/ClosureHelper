package com.xueluoanping.arknights.pages;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
                        Log.d(TAG, "run: ??????????????????500ms");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                stagesListAll.clear();
                // forEachRemaining???????????????
                if (stageTable == null) {
                    toastInThread("??????????????????????????????????????????????????????");
                    ToolTable.initInstance();
                    return;
                }
                Set<Map.Entry<String, JsonElement>> es = stageTable.getAsJsonObject().entrySet();
                JsonObject jsonObject = stageTable.getAsJsonObject();

                // ??????????????????
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
                        stageModel.setOpen(Kengxxiao.isStageOpen(key.getKey())!=0);
                        JsonArray drops = stageJsonObject.get("stageDropInfo").getAsJsonObject().get("displayDetailRewards").getAsJsonArray();
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
                                            stringStringEntry.setOccPercent(penguin_stats.getRealOccPercent(key.getKey(),id),OccPercent);
                                        }

                                        stageModel.displayRewards.add(stringStringEntry);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        AtomicBoolean isSelected = new AtomicBoolean(false);
                        stageStringsList.forEach(s -> {
                            if (stageModel.getStageId().equals(s))
                                isSelected.set(true);
                        });
                        if (isSelected.get())
                            stageModel.setSelected(true);
                        if (stageModel.displayRewards.size() > 0)
                            stagesListAll.add(stageModel);

                    } catch (Exception e) {
                        Log.d(TAG, "run: ????????????????????????" + key.getKey());
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
                        toastInThread("?????????????????????????????????");
                        runOnUiThread(() -> {finish();});
                        // ????????????????????????????????????????????????????????????????????????????????????
                        try {
                            gameSettings[0] = Game.getGameSettings_Json(SimpleTool.getJson(url));
                        } catch (JSONException | IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    oldSetting = gameSettings[0];
                    final StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("?????????????????????");


                    gameSettings[0].battleMaps.forEach(map -> {
                        try {
                            JsonObject stageJsonObject = stageTable.getAsJsonObject().get(map).getAsJsonObject();
                            stringBuilder.append(stageJsonObject.get("code").getAsString() + "(" + stageJsonObject.get("name").getAsString() + ")???");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                    stageStringsList = gameSettings[0].battleMaps;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
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

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (Game.updateGameSettings(
                                    gameInstanceInfo, gameSettings)) {
                                finish();
                                toastInThread("???????????????");
                            } else {
                                toastInThread("???????????????????????????");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            toastInThread("???????????????????????????");
                        }
                    }
                }).start();

            }
        });
        bt_search.setOnLongClickListener((v -> {
            hideInputMethod();
            // ????????????
            tv_battleMaps.setText("?????????????????????");
            et_search.setText("");
            stagesList.clear();
            ad_battleSelect.setList(stagesList);
            ad_battleSelect.notifyDataSetChanged();
            // ????????????
            stagesListAll.forEach(stageModel -> {
                stageModel.setSelected(false);
            });
            stageStringsList.clear();
            Toast.makeText(this, "?????????????????????", Toast.LENGTH_SHORT).show();
            return true;
        }));
        bt_search.setOnClickListener(new View.OnClickListener() {
            String cache = "***";
            boolean hasHint = false;

            @Override
            public void onClick(View view) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                // ???????????????
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

                // ??????????????? (?i)?????????????????????
                String regex = et_search.getText().toString();
                if (regex.isEmpty()) {
                    // getsong();
                    // adapter.setList(datas);
                    stagesList.clear();
                    ad_battleSelect.setList(stagesList);
                    toastInThread("??????????????????");
                    return;
                }
                if (!hasHint) {
                    // toastInThread("???????????????????????????????????????");
                    hasHint = true;
                } else {
                    // toastInThread("???????????????");
                }
                // ??????????????????????????????????????????????????????
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
                // ????????????????????????????????????????????????????????????lowercase
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
        tv_battleMaps = findViewById(R.id.tv_battleMaps);
        rc_battleSelect = findViewById(R.id.rc_battleSelect);
    }

    private void loadData() {
        //??????????????????
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rc_battleSelect.setLayoutManager(layoutManager);
        //        View view = LayoutInflater.from(this).inflate(R.layout.item_hr_diary, null);
        //???????????????
        ad_battleSelect = new StageAdapter(R.layout.ic_battle, stagesList);
        rc_battleSelect.setAdapter(ad_battleSelect);

        //    ????????????????????????
        gameInstanceInfo = new Game.GameInfo().backupFromIntentBundle(getIntent());
    }

    public void toastInThread(String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
