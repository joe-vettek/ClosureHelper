package com.xueluoanping.arknights.pages;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.JsonObject;
import com.xueluoanping.arknights.R;
import com.xueluoanping.arknights.SimpleApplication;
import com.xueluoanping.arknights.api.BetterEntry;
import com.xueluoanping.arknights.api.main.Data;
import com.xueluoanping.arknights.api.main.Game;
import com.xueluoanping.arknights.api.resource.dzp;
import com.xueluoanping.arknights.api.tool.ToolTable;
import com.xueluoanping.arknights.api.tool.ToolTheme;
import com.xueluoanping.arknights.base.BaseActivity;
import com.xueluoanping.arknights.custom.Item.ItemModel;
import com.xueluoanping.arknights.custom.TextImageTransformation;
import com.xueluoanping.arknights.custom.UrlImageSpan;
import com.xueluoanping.arknights.pages.fragment.GamesFragment;
import com.xueluoanping.arknights.pro.HttpConnectionUtil;
import com.xueluoanping.arknights.pro.SimpleTool;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InventoryActivity extends BaseActivity {
    private static final String TAG = InventoryActivity.class.getSimpleName();
    private TextView textView;
    private Game.GameInfo gameInstanceInfo;
    private Button bt_back;
    private Button bt_ocr;
    private ArrayList<BetterEntry<String, BetterEntry<String, Integer>>> datas = new ArrayList<>();
    private NestedScrollView scrollView;
    private TableLayout tableLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameInstanceInfo = new Game.GameInfo().backupFromIntentBundle(getIntent());
        setContentView(R.layout.activity_inventory);
        bindComponents();
        setListeners();
        loadData();
    }

    private void loadData() {
        datas.clear();
        new Thread(() -> {
            try {
                Data.AccountData data0 = Data.getBasicInfo(SimpleApplication.getContext(), gameInstanceInfo);
                JsonObject item = ToolTable.getInstance().getItemTable();
                new Thread(() -> {
                    if(data0.inventory==null||data0.inventory== HttpConnectionUtil.emptyJsonObject1){
                        toastInThread("仓库为空，请申请识别！");
                    }
                    // forEachRemaining只能用一次
                    Log.d(TAG, "loadData: " + data0.gold);
                    try {
                        datas.add(new BetterEntry<>(ToolTable.getInstance().getItemTable().getAsJsonObject("4001").get("iconId").getAsString(),
                                new BetterEntry<>("龙门币", data0.gold)));
                        datas.add(new BetterEntry<>(ToolTable.getInstance().getItemTable().getAsJsonObject("4003").get("iconId").getAsString(),
                                new BetterEntry<>("合成玉", data0.diamondShard)));
                        datas.add(new BetterEntry<>(ToolTable.getInstance().getItemTable().getAsJsonObject("4002").get("iconId").getAsString(),
                                new BetterEntry<>("至纯源石", data0.androidDiamond)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    data0.inventory.keys().forEachRemaining(key -> {
                        try {
                            int amount = data0.inventory.getInt(key);
                            if (amount > 0) {
                                // JSONObject itemSector = itemJsonObject.getJSONObject(key);
                                JsonObject itemSector = item.getAsJsonObject(key);
                                BetterEntry<String, BetterEntry<String, Integer>> iconCombination = new BetterEntry<>();
                                BetterEntry<String, Integer> nameWithAmount = new BetterEntry<>();
                                iconCombination.setKey(itemSector.get("iconId").getAsString());
                                nameWithAmount.setKey(itemSector.get("name").getAsString());
                                nameWithAmount.setValue(amount);
                                iconCombination.setValue(nameWithAmount);
                                datas.add(iconCombination);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });

                    SpannableStringBuilder ssb = new SpannableStringBuilder();
                    datas.forEach(entryBase -> {
                        String name = entryBase.getValue().getKey();
                        int amount = entryBase.getValue().getValue();
                        String text0 = name + "：" + amount + "；";
                        // ss.append(text0);
                        SpannableString spannableString = new SpannableString(text0);
                        String url = dzp.getItemIconUrlById(entryBase.getKey());
                        UrlImageSpan imageSpan = new UrlImageSpan(InventoryActivity.this, url, textView, amount);
                        spannableString.setSpan(imageSpan, 0, text0.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        String clickHint = name + "（共" + amount + "）";
                        ClickableSpan clickableSpan = new ClickableSpan() {
                            @Override
                            public void onClick(@NonNull View widget) {
                                toastInThread(clickHint);
                            }
                        };
                        spannableString.setSpan(clickableSpan, 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ssb.append(spannableString);
                    });
                    // if (datas.size() == 0) ssb.append("未申请仓库识别;");

                    if (ssb.length() > 5) {
                        // String ssString = ss.substring(0, ss.length() - 1);
                        runOnUiThread(() -> {
                            // t14.setText("今日收益：" + ssString);
                            // textView.setText((SpannableStringBuilder) ssb.subSequence(0, ssb.length() - 1));
                            // boolean a= ToolTheme.getBooleanValue(InventoryActivity.this,R.attr.isLightTheme);
                            int color=
                                    ToolTheme.getColorValue(InventoryActivity.this,R.attr.colorAccent);
                            int lines = datas.size() / 5;
                            int itemWidth=tableLayout.getWidth()/5-10;
                            int itemHeight=tableLayout.getWidth()/5-10;
                            for (int i = 0; i < lines; i++) {
                                TableRow tableRow = new TableRow(InventoryActivity.this);
                                tableRow.setGravity(Gravity.CENTER_HORIZONTAL);
                                tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                                for (int j = 0; j < 5; j++) {
                                    if (datas.size() > 0) {
                                        BetterEntry<String, BetterEntry<String, Integer>> entryBase=datas.get(0);
                                        String name = entryBase.getValue().getKey();
                                        int amount = entryBase.getValue().getValue();
                                        String clickHint = name + "（共" + amount + "个）";

                                        ImageView imageView = new ImageView(InventoryActivity.this);
                                        ViewGroup.LayoutParams params = new TableRow.LayoutParams() ;
                                        params.width =itemWidth;
                                        params.height =itemHeight;
                                        imageView.setLayoutParams(params);
                                        imageView.setOnClickListener(v -> {
                                            toastInThread(clickHint);
                                        });
                                        imageView.setOnLongClickListener(v -> {
                                          startWebActivity(WebActivity2.getPrtsUrl_base(name));
                                            return true;
                                        });
                                        // imageView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                                        String url = dzp.getItemIconUrlById(entryBase.getKey());
                                        // Log.d(TAG, "loadData: " + url);
                                        RequestOptions options = new RequestOptions();
                                        String shortAmountText=SimpleTool.getShortAmountDescriptionText(entryBase.getValue().getValue());
                                        options = options.transform(new TextImageTransformation(shortAmountText,color,false));
                                        Glide.with(getApplicationContext())
                                                .load(url)
                                                .apply(options )
                                                .into(imageView);
                                        tableRow.addView(imageView);
                                        datas.remove(0);
                                    }else break;
                                }
                                tableLayout.addView(tableRow);
                                // tableLayout.invalidate();

                            }

                        });

                    }
                }).start();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void setListeners() {
        bt_ocr.setOnClickListener(v -> {
            new Thread(() -> {
                try {
                    Data.requestForOCR(SimpleApplication.getContext(), InventoryActivity.this.gameInstanceInfo);
                    SimpleTool.toastInThread(InventoryActivity.this, "已经为您申请仓库识别，预计耗时十分钟，请选择合适的时间查看结果（需要重载）。");
                } catch (Exception e) {
                    e.printStackTrace();
                    SimpleTool.toastInThread(InventoryActivity.this, "申请失败，请确认网络通畅且账户正在托管中！");

                }
            }).start();
        });

        bt_back.setOnClickListener((v) -> {
            finish();
        });
    }

    private void bindComponents() {
        textView = findViewById(R.id.textView88);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setText("点击图标可显示内容，长按跳转PRTS Wiki");
        bt_ocr = findViewById(R.id.bt_requestOCR);
        bt_back = findViewById(R.id.bt_inventory_back);
        tableLayout = findViewById(R.id.tbl_inventory);
    }
}
