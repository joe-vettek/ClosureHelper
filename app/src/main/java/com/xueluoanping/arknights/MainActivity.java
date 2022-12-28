package com.xueluoanping.arknights;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.xueluoanping.arknights.api.Data;
import com.xueluoanping.arknights.api.Game;
import com.xueluoanping.arknights.custom.Item.ItemModel;
import com.xueluoanping.arknights.custom.Item.itemAdapterSpecial;
import com.xueluoanping.arknights.global.Global;
import com.xueluoanping.arknights.pages.AccountMangerActivty;
import com.xueluoanping.arknights.pages.GameSettingsActivity;
import com.xueluoanping.arknights.pages.LoginActivity;
import com.xueluoanping.arknights.pages.SettingActivity;
import com.xueluoanping.arknights.pages.WebActivity;
import com.xueluoanping.arknights.pro.HttpConnectionUtil;
import com.xueluoanping.arknights.pro.SimpleTool;
import com.xueluoanping.arknights.pro.spTool;
import com.xueluoanping.arknights.services.MyReceiver;
import com.xueluoanping.arknights.services.SimpleService;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private RecyclerView recyclerView2;
    private ImageView imageView;
    private TextView textView;
    private itemAdapterSpecial adapter;
    private itemAdapterSpecial adapter2;
    private ArrayList<ItemModel> datas = new ArrayList<>();
    private ArrayList<ItemModel> datas2 = new ArrayList<>();



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestforPermissons();
        bindComponents();
        Global.prepareBaseData(getApplicationContext());
        startService(1);

        // 如果为空，登录
        if (spTool.getToken(getApplicationContext()).isEmpty()) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
        //不为空，加载游戏信息
        else {
            clearAndLoad();
        }
        // showAnnouncement();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void showAnnouncement() {
        AlertDialog.Builder dialogAnnouncementBuilder = new AlertDialog.Builder(MainActivity.this)
                .setIcon(R.mipmap.npc_007_closure)
                .setTitle("平台公告")
                .setMessage(R.string.AnnouncementText)
                .setPositiveButton("三秒后自动关闭", null);

        final AlertDialog dlg = dialogAnnouncementBuilder.create();
        dlg.show();
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                dlg.dismiss();
            }
        }, 3000);
    }


    private void requestforPermissons() {
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                } else {
                    //这里就是权限打开之后自己要操作的逻辑
                }
            }
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        registerReceiver(new MyReceiver(), intentFilter);
    }

    private void bindComponents() {
        recyclerView = findViewById(R.id.itemList);
        recyclerView2 = findViewById(R.id.itemList2);
        imageView = findViewById(R.id.iv_secretary);
        textView = findViewById(R.id.textView12);

        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (needLogin) {
                    Log.d(TAG, "onClick: 尝试登陆中");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            toastInThread("尝试登陆中");
                            if (Game.TryLogin(getApplicationContext(), currentAccount, currentPlatform)) {
                                alertTextView("正在登陆中！");
                                Timer timer = new Timer();
                                long cacheTime = System.currentTimeMillis();
                                timer.schedule(new TimerTask() {

                                    @Override
                                    public void run() {

                                        try {
                                            int code = Global.getSelectedGame().isInList2(Game.getGameStatue(getApplicationContext())).code;
                                            if (code == Game.WebGame_Status_Code_Running) {
                                                long nowTime = System.currentTimeMillis();
                                                double duration = (nowTime - cacheTime) / 1000.0d;
                                                Log.d(TAG, "run: " + "登录完成，共耗时" + duration + "s");
                                                toastInThread("登录完成，共耗时" + duration + "s");
                                                Thread.sleep(300);
                                                timer.cancel();
                                                timer.purge();
                                                clearAndLoad();
                                            } else if (code != Game.WebGame_Status_Code_Loginning) {
                                                toastInThread("正在访问可露希尔网页进行手动确认");
                                                alertTextView("完成手动确认后请点击头像重新加载数据");
                                                notifyUser("需要手动登录");
                                                // 之后回到正常登录流程
                                                // timer.cancel();
                                                // timer.purge();
                                                Intent ii = new Intent(getApplicationContext(), WebActivity.class);
                                                startActivity(ii);
                                            }

                                        } catch (IOException | JSONException | InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, 30000, 1000*180);

                            } else
                                toastInThread("请访问可露希尔工作室进行检查或者更换网络！");
                        }
                    }).start();

                } else {
                    ArrayList<String> items = new ArrayList<>();
                    items.add("• 重载当前账户数据");

                    ArrayList<Global.GlobalGameAccount> tempList = Global.getGamesList();
                    for (Global.GlobalGameAccount account : tempList) {
                        String s = "• ";
                        s += "【" + Game.getPlatform(account.platform) + "】";
                        s += SimpleTool.protectTelephoneNum(account.account);
                        if (account.equals(Global.getSelectedGame()))
                            s += "（点击调整）";
                        else s += "（点击切换）";
                        items.add(s);
                    }

                    items.add("• 账户管理面板");
                    items.add("• 设置");

                    String[] itemArray = items.toArray(new String[0]);
                    for (String s : itemArray) Log.d(TAG, "onClick: " + s);

                    AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("快捷设置")
                            .setIcon(R.mipmap.npc_007_closure)
                            .setItems(itemArray, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String name = items.get(which);
                                    Log.d(TAG, "onClick: " + name);

                                    if (name.contains("重载")) {
                                        toastInThread("重新加载！");
                                        // needLogin = false;
                                        clearAndLoad();
                                    } else if (name.contains("账户")) {
                                        Intent i1 = new Intent(MainActivity.this, AccountMangerActivty.class);
                                        startActivity(i1);
                                    } else if (name.contains("设置")) {
                                        Intent i1 = new Intent(MainActivity.this, SettingActivity.class);
                                        startActivity(i1);
                                    } else {
                                        if (!name.contains("点击调整")) {
                                            // if (which > 0)
                                            Global.setSelectedGameAndSave(getApplicationContext(), Global.getGamesList().get(which - 1));
                                            toastInThread("重新加载！");
                                            // needLogin = false;
                                            clearAndLoad();
                                        } else {
                                            Intent i = new Intent(MainActivity.this, GameSettingsActivity.class);

                                            startActivity(i);
                                        }
                                    }

                                }
                            }).create();
                    dialog.show();


                }

            }
        });
    }

    private void notifyUser(String x) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(10,
                SimpleService.broadcastIntent(getApplicationContext(), x).setAutoCancel(true).build());
    }

    public void clearAndLoad() {
        needLogin = false;
        datas.clear();
        datas2.clear();
        alertTextView(getText(R.string.timeoutText).toString());
        loadStatus();
    }

    public void toastInThread(String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void alertTextView(String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(s);
            }
        });
    }

    boolean needLogin = false;
    String currentAccount = HttpConnectionUtil.empty;
    int currentPlatform = 1;

    private void loadStatus() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {


                    Game.GameInfo[] games = Game.getGameStatue(getApplicationContext());
                    Global.updateGlobalGamesList(getApplicationContext(), games, true);

                    // 只有运行状态才进行
                    if (games.length == 0) {
                        alertTextView("未添加游戏账户！");
                        return;
                    }
                    Game.GameInfo game0 = Global.getSelectedGame().isInList2(games);

                    JSONObject stageJsonObject = Data.getStageTable().getJSONObject(game0.battleMaps);
                    String main = "状态：" + game0.status
                            + "\n战斗地图：" + stageJsonObject.getString("code") + "(" + stageJsonObject.getString("name") + ")";

                    // 只有运行状态才进行
                    if (game0.code != Game.WebGame_Status_Code_Running && game0.code != Game.WebGame_Status_Code_Loginning) {

                        currentPlatform = game0.platform;
                        currentAccount = game0.account;
                        main = main.replace("状态：-\n", "状态：游戏未运行\n");
                        if (spTool.getQuickLogin()) {
                            alertTextView(main + "\n\n如需要重启，可以点击头像尝试启动账户");
                            needLogin = true;

                        } else {
                            alertTextView(main + "\n\n点击头像获得更多信息");
                        }
                        return;
                    }


                    Data.AccountData data0 = Data.getBasicInfo(getApplicationContext(), game0);
                    Data.AccountData data0_back = new Data.AccountData(Data.getOldDataTable(getApplicationContext(), Global.getSelectedGame().getSimpleGameInfo()));



                    if (data0.inventory.toString().equals(HttpConnectionUtil.emptyJsonObject1.toString())) {
                        if (data0_back.inventory.length() > 50)
                            data0.inventory = data0_back.inventory;
                    }
                    // Log.d(TAG, "run: "+data0.inventory+firstLoginNeedAutoAskForOCR);
                    // 当前版本，物品栏的变动需要请求扫描
                    // 但是金币什么的更新是比较及时的
                    if (data0.lastFreshTs > data0_back.lastFreshTs)
                        data0.lastFreshTs_Inventory = System.currentTimeMillis();
                    else data0.lastFreshTs_Inventory = data0_back.lastFreshTs_Inventory;
                    data0.lastFreshTs_Base = System.currentTimeMillis();
                    Data.saveOldDataTable(getApplicationContext(), data0);


                    // 这处理账户重启服务器仓库不保存的问题，这时候自动第一次申请识别仓库
                    boolean firstLoginNeedAutoAskForOCR = false;
                    float intervalTime = Data.getDataIntervalTime(getApplicationContext(),  data0_back);
                    if(intervalTime>1){
                        if (data0.inventory.toString().equals(HttpConnectionUtil.emptyJsonObject1.toString())) {
                            firstLoginNeedAutoAskForOCR = true;
                        }
                    }
                    Log.d(TAG, "run: "+firstLoginNeedAutoAskForOCR);
                    final String[] tip = new String[]{""};
                    if (spTool.getAutoWarehouseIdentification() |  firstLoginNeedAutoAskForOCR) {
                        if (intervalTime > 1) {
                            tip[0] = "已经为您请求了最新的仓库内容，请过一段时间再来看看吧~\n";
                            Log.d(TAG, "run: "+tip[0]);
                            Data.requestForOCR(getApplicationContext(), game0);

                        } else {
                            tip[0] = "已经请求过了最新的仓库内容，" + (int) ((1 - intervalTime) * 60) + "分钟内无法重复请求~\n";
                        }
                    } else {

                    }

                    String name = Data.getCharacterTable().getJSONObject(data0.secretary).getString("name");
                    String sss = "等级：" + data0.level
                            + "\n源石数量：" + data0.androidDiamond
                            + "\n合成玉数量：" + data0.diamondShard
                            + "\n龙门币数量：" + data0.gold
                            + "\n助理：" + name;

                    // Log.d(TAG, "run: "+Data.getStageTable(getApplicationContext()));

                    JSONObject itemJsonObject = Data.getItemTable();

                    // 用于比对数额
                    Map<String, Integer> inventoryHistoryMap = new HashMap<>();
                    // forEachRemaining只能用一次
                    data0.inventory.keys().forEachRemaining(key -> {
                        try {
                            int amount = data0.inventory.getInt(key);
                            if (amount > 0) {
                                JSONObject itemSector = itemJsonObject.getJSONObject(key);
                                ItemModel item = new ItemModel();
                                item.key = itemSector.getString("iconId");
                                item.text = itemSector.getString("name") + "：" + amount + "\n";
                                datas.add(item);

                                inventoryHistoryMap.put(key, amount);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                    // forEachRemaining只能用一次
                    data0_back.inventory.keys().forEachRemaining(key -> {
                        try {
                            int amount = data0_back.inventory.getInt(key);
                            if (amount > 0) {
                                if (inventoryHistoryMap.containsKey(key))
                                    inventoryHistoryMap.put(key, inventoryHistoryMap.get(key) - amount);
                                else
                                    inventoryHistoryMap.put(key, amount);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });

                    StringBuilder stringBuilder = new StringBuilder();

                    int gold_amount = data0.gold - data0_back.gold;
                    int shard_amount = data0.diamondShard - data0_back.diamondShard;

                    @SuppressLint("DefaultLocale")

                    StringBuilder baseString = new StringBuilder();
                    if (gold_amount > 0)
                        baseString.append(String.format(Locale.CHINA, "龙门币：%+d；  ", gold_amount));
                    if (shard_amount > 0)
                        baseString.append(String.format(Locale.CHINA, "合成玉：%+d；  ", shard_amount));

                    // stringBuilder.append(baseString);
                    inventoryHistoryMap.forEach((key, amount) -> {
                        if (amount != 0) {
                            try {
                                String amountString = amount > 0 ? "+" + amount : "" + amount;
                                String itemString = "" + itemJsonObject.getJSONObject(key).getString("name") + ":" + amountString + "；  ";
                                stringBuilder.append(itemString);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });

                    String timeString1 = SimpleTool.getFormatDate(data0.lastFreshTs_Inventory, false);
                    String timeString0 = SimpleTool.getFormatDate(data0.lastFreshTs_Base, false);
                    // Log.d(TAG, "run: " + stringBuilder);


                    String finalMain = main;
                    boolean finalFirstLoginNeedAutoAskForOCR = firstLoginNeedAutoAskForOCR;
                    runOnUiThread(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            // Context context=getApplicationContext();
                            // 注意getApplicationContext()获取的是应用，对话框必须指向activity
                            if (spTool.getAutoWarehouseIdentification() | finalFirstLoginNeedAutoAskForOCR) {
                                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                                SpannableString sTip = new SpannableString(tip[0] + "\n\n");
                                ForegroundColorSpan colorSpan0 = new ForegroundColorSpan(getResources().getColor(R.color.colorPrimaryDark, null));
                                sTip.setSpan(colorSpan0, 0, sTip.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                                spannableStringBuilder.append(sTip);
                                SpannableString t1 = new SpannableString("【截止到" + timeString0 + "这些资源发生了变化】\n");
                                ForegroundColorSpan colorSpant1 = new ForegroundColorSpan(getResources().getColor(R.color.colorPrimaryDark, null));
                                sTip.setSpan(colorSpant1, 0, t1.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                                spannableStringBuilder.append(t1).append(String.valueOf(baseString)).append("\n\n");
                                SpannableString t2 = new SpannableString("【截止到" + timeString1 + "仓库发生了这些变化】\n");
                                ForegroundColorSpan colorSpant2 = new ForegroundColorSpan(getResources().getColor(R.color.colorPrimaryDark, null));
                                sTip.setSpan(colorSpant2, 0, t2.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                                spannableStringBuilder.append(t2).append(String.valueOf(stringBuilder)).append("\n\n");

                                new AlertDialog.Builder(MainActivity.this)
                                        .setIcon(R.mipmap.npc_007_closure)
                                        .setTitle("提示")
                                        .setMessage(spannableStringBuilder)
                                        .setPositiveButton("确认", null)
                                        .show();
                            }
                            // RoundedCorners roundedCorners = new RoundedCorners(20);//圆角为5

                            String secretarySkinIdDec = data0.secretarySkinId.replaceAll("[@#]", "_");
                            String PicUrl = "https://ak.dzp.me/dst/avatar/ASSISTANT/" + secretarySkinIdDec + ".webp";
                            final String PicUrl2 = PicUrl = "https://ak.dzp.me/dst/avatar/ASSISTANT/" + data0.secretary + ".webp";
                            RequestOptions options = RequestOptions.bitmapTransform(new CircleCrop());
                            Glide.with(getApplicationContext())
                                    .load(PicUrl)
                                    .apply(options)
                                    .into(new SimpleTarget<Drawable>() {
                                        @Override
                                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                            if (resource.getBounds().width() > 5)
                                                imageView.setImageDrawable(resource);
                                            else Glide.with(getApplicationContext())
                                                    .load(PicUrl2)
                                                    .apply(options)
                                                    .into(imageView);
                                        }
                                    });
                            textView.setText(finalMain + "\n\n" + sss);

                            if (datas.size() > 8) {
                                List<List<ItemModel>> lists = SimpleTool.splitList(datas, datas.size() / 2 + 1);
                                datas = new ArrayList<>(lists.get(0));
                                datas2 = new ArrayList<>(lists.get(1));


                            }
                            loadData();
                        }
                    });

                } catch (IOException ex) {
                    ex.printStackTrace();
                    toastInThread("网络有误，访问失败！");
                    notifyUser("无法连接上");
                } catch (Exception e) {
                    e.printStackTrace();
                    // toastInThread("正在维护");
                    // notifyUser("正在维护");
                }
            }
        }).start();
    }

    private void loadData() {
        //创建布局管理
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        //        View view = LayoutInflater.from(this).inflate(R.layout.item_hr_diary, null);
        //创建适配器
        adapter = new itemAdapterSpecial(R.layout.ic_item, datas);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getApplicationContext());
        layoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView2.setLayoutManager(layoutManager2);
        adapter2 = new itemAdapterSpecial(R.layout.ic_item, datas2);
        recyclerView2.setAdapter(adapter2);
    }

    public void startService(int actionCode) {

        // if (!SimpleService.isRunning)
        {
            Intent intent = new Intent(MainActivity.this, SimpleService.class);
            intent.setClass(getApplicationContext(), SimpleService.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("actionCode", actionCode);
            startService(intent);
        }
    }
}