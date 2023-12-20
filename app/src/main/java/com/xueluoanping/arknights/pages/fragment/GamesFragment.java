package com.xueluoanping.arknights.pages.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.xueluoanping.arknights.MainActivity;
import com.xueluoanping.arknights.R;
import com.xueluoanping.arknights.SimpleApplication;
import com.xueluoanping.arknights.api.BetterEntry;
import com.xueluoanping.arknights.api.main.Data;
import com.xueluoanping.arknights.api.main.Game;
import com.xueluoanping.arknights.api.resource.dzp;
import com.xueluoanping.arknights.api.tool.ToolTable;
import com.xueluoanping.arknights.api.tool.ToolTheme;
import com.xueluoanping.arknights.api.tool.ToolTime;
import com.xueluoanping.arknights.base.BaseActivity;
import com.xueluoanping.arknights.base.BaseFragment;
import com.xueluoanping.arknights.custom.GameLog.GameLog;
import com.xueluoanping.arknights.custom.GameLog.GameLogAdapter;
import com.xueluoanping.arknights.custom.Item.ItemModel;
import com.xueluoanping.arknights.custom.UrlImageSpan;
import com.xueluoanping.arknights.pages.CheckActivity;
import com.xueluoanping.arknights.pages.GalleyActivity;
import com.xueluoanping.arknights.pages.GameSettingsActivity;
import com.xueluoanping.arknights.pages.InventoryActivity;
import com.xueluoanping.arknights.pro.HttpConnectionUtil;
import com.xueluoanping.arknights.pro.SimpleTool;
import com.xueluoanping.arknights.pro.spTool;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class GamesFragment extends BaseFragment implements FragmentWithName {
    private static final String TAG = GamesFragment.class.getSimpleName();
    private ImageView imageView;
    private ImageView imageView_state;
    private TextView textView;
    private ArrayList<ItemModel> datas = new ArrayList<>();
    private ArrayList<ItemModel> datas2 = new ArrayList<>();
    private RecyclerView recyclerView_log;
    private GameLogAdapter adapter_log;
    private List<GameLog> datas_log = new ArrayList<>();
    private Button bt_inventory;
    private Button bt_screenshot;
    private NestedScrollView nsv1;
    private TextView t13;
    private TextView t14;
    private String name;
    private Game.GameInfo gameInstanceInfo;

    private TextView t_gold;
    private TextView t_diamond_shd;
    private TextView t_diamond;
    private TextView t_ap;
    // private ImageView iv_gold;
    // private ImageView iv_diamond_shd;
    // private ImageView iv_diamond;
    private ImageView iv_ap;


    public static GamesFragment newInstance(Game.GameInfo info) {
        GamesFragment fragment = new GamesFragment();
        fragment.setArguments(info.createBundle());
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_main, container, false);
        nsv1 = root.findViewById(R.id.nsv1);
        imageView = root.findViewById(R.id.iv_secretary);
        imageView_state = root.findViewById(R.id.iv_secretary_status);
        textView = root.findViewById(R.id.textView12);
        recyclerView_log = root.findViewById(R.id.logList);
        bt_inventory = root.findViewById(R.id.bt_inventory);
        bt_screenshot = root.findViewById(R.id.bt_screenshot);
        t13 = root.findViewById(R.id.textView13);
        t14 = root.findViewById(R.id.textView14);

        t_gold = root.findViewById(R.id.t_gold);
        t_diamond_shd = root.findViewById(R.id.t_diamond_shd);
        t_diamond = root.findViewById(R.id.t_diamond);
        t_ap = root.findViewById(R.id.t_ap);
        //   iv_gold= root.findViewById(R.id.iv_gold);
        //   iv_diamond_shd= root.findViewById(R.id.iv_diamond_shd);
        // iv_diamond= root.findViewById(R.id.iv_diamond);
        iv_ap = root.findViewById(R.id.iv_ap);
        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        bindComponents();

        initData();

        // 如果为空，登录

        //不为空，加载游戏信息
        // else {

        clearAndLoad();
        // }
    }

    private void initData() {
        gameInstanceInfo = new Game.GameInfo().backupFromBundle(getArguments());
    }


    private void bindComponents() {

        // 让其可以点击
        t14.setMovementMethod(LinkMovementMethod.getInstance());

        // t14.setLongClickable(false);
        // 必需消费这个事件，否则报错 Attempt to invoke virtual method 'int android.widget.Editor$SelectionModifierCursorController.getMinTouchOffset()' on a null object reference
        // https://blog.csdn.net/lxk_1993/article/details/78674324 小米手机独有
        // t14.setOnLongClickListener(v -> {
        //     t14.setSelected(true);
        //     return true;
        // });
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (needLogin) {
                    Log.d(TAG, "onClick: 尝试登陆中");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            tryLogin();

                        }
                    }).start();

                } else {
                    ArrayList<String> items = new ArrayList<>();
                    items.add("• 重载当前账户数据");
                    items.add("• 登录（如已登录请勿重复选择）");
                    // items.add("• 获取当前仓库信息");
                    items.add("• 点击调整游戏配置");
                    // items.add("• 账户管理面板");
                    // items.add("• 设置");

                    String[] itemArray = items.toArray(new String[0]);
                    for (String s : itemArray) Log.d(TAG, "onClick: " + s);

                    AlertDialog dialog = new AlertDialog.Builder(getActivity())
                            .setTitle("快捷设置")
                            .setIcon(R.mipmap.npc_007_closure)
                            .setItems(itemArray, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String name = items.get(which);
                                    Log.d(TAG, "onClick: " + name);

                                    if (name.contains("重载")) {
                                        SimpleTool.toastInThread(getActivity(), "重新加载！");
                                        new Thread(() -> {
                                            try {
                                                refreshGameInfo();
                                            } catch (IOException | JSONException e) {
                                                e.printStackTrace();
                                            }
                                            clearAndLoad();
                                        }).start();
                                        // needLogin = false;

                                    } else if (name.contains("登录")) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                tryLogin();
                                            }
                                        }).start();

                                    } else if (name.contains("仓库")) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Data.requestForOCR(SimpleApplication.getContext(), GamesFragment.this.gameInstanceInfo);
                                                    SimpleTool.toastInThread(getActivity(), "已经为您申请仓库识别，预计耗时十分钟，请选择合适的时间查看结果（需要重载）。");
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    SimpleTool.toastInThread(getActivity(), "申请失败，请确认网络通畅且账户正在托管中！");

                                                }
                                            }
                                        }).start();
                                    } else {
                                        if (name.contains("点击调整")) {
                                            Intent i = new Intent(getActivity(), GameSettingsActivity.class);
                                            i = GamesFragment.this.gameInstanceInfo.addIntentBundle(i);
                                            startActivity(i);
                                        }
                                    }
                                    dialog.dismiss();
                                }
                            }).create();
                    dialog.show();


                }

            }
        });
        bt_screenshot.setOnClickListener((view0) -> {
            Intent ii = new Intent(SimpleApplication.getContext(), GalleyActivity.class);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Game.GameInfo info = null;
                    try {
                        info = GamesFragment.this.gameInstanceInfo;
                        ii.putExtra("account", info.account);
                        ii.putExtra("platform", info.platform);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    startActivity(ii);
                }
            }).start();

        });
        bt_inventory.setOnClickListener((view0) -> {
            // if (datas.size() > 0) {
            //     int distance = textView.getHeight() + 70 + recyclerView_log.getHeight();
            //     nsv1.smoothScrollTo(0, distance);
            // } else {
            //     SimpleTool.toastInThread(getActivity(), "请先申请仓库识别！");
            // }
            Intent ii = new Intent(SimpleApplication.getContext(), InventoryActivity.class);
            Game.GameInfo info = null;
            try {
                info = GamesFragment.this.gameInstanceInfo;
                ii = info.addIntentBundle(ii);
            } catch (Exception e) {
                e.printStackTrace();
            }

            startActivity(ii);
        });
    }

    public void clearAndLoad() {
        alertButtonVisibility(true);
        needLogin = false;
        datas.clear();
        datas2.clear();
        datas_log.clear();
        safeRunOnUiThread(() -> {
            alertTextView(getText(R.string.timeoutText).toString());
            alertTextView2("");
            t13.setVisibility(View.INVISIBLE);
            t14.setVisibility(View.INVISIBLE);
        });

        // if (!
        // if (getActivity() != null && getActivity() instanceof BaseActivity)
        //     ((BaseActivity) getActivity()).queryArknightsIsMaintaining();
        // ) return;
        loadStatus();
    }


    public void toastInThread(String s) {
        safeRunOnUiThread(() -> {
            SimpleTool.toastInThread(getActivity(), s);
            // try {
            //     Toast.makeText(SimpleApplication.getContext(), s, Toast.LENGTH_SHORT).show();
            // } catch (Exception e) {
            //     e.printStackTrace();
            // }
        });
    }

    public void alertTextView(String s) {
        safeRunOnUiThread(() -> {
            textView.setText(s);
        });
    }

    public void alertTextView2(String s) {
        safeRunOnUiThread(() -> {
            t13.setText(s);
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

                    Game.GameInfo game0 = GamesFragment.this.gameInstanceInfo;
                    JsonObject stageTable = ToolTable.getInstance().getStageTable();
                    JsonObject stageJsonObject = stageTable.has(game0.mapId) ? stageTable.getAsJsonObject(game0.mapId) : null;
                    Log.d(TAG, "run: " + game0.code);
                    safeRunOnUiThread(() -> {
                        safeRunOnUiThread(() -> {
                            t13.setVisibility(View.VISIBLE);
                            t14.setVisibility(View.VISIBLE);
                        });

                        try {
                            switch (game0.code) {
                                case Game.WebGame_Status_Code_Running:
                                    imageView_state.setImageDrawable(getContext().getDrawable(R.drawable.pot_green));
                                    break;
                                case Game.WebGame_Status_Code_NeedCheck:
                                    imageView_state.setImageDrawable(getContext().getDrawable(R.drawable.pot_orange));
                                    break;
                                case Game.WebGame_Status_Code_Loginning:
                                    imageView_state.setImageDrawable(getContext().getDrawable(R.drawable.pot_yellow));
                                    break;
                                // case Game.WebGame_Status_Code_Running:
                                //     imageView_state.setImageDrawable(getContext().getDrawable(R.drawable.pot_red));
                                //     break;
                                default:
                                    imageView_state.setImageDrawable(getContext().getDrawable(R.drawable.pot_red));
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                    BetterEntry<String, String> main =
                            stageJsonObject == null ?
                                    new BetterEntry<>(game0.status, game0.mapId) :
                                    new BetterEntry<>(game0.status, stageJsonObject.get("code").getAsString() + "(" + stageJsonObject.get("name").getAsString() + ")");


                    new Thread(() -> {
                        datas_log = Game.getLog(game0.account, game0.platform);
                        // 加载日志
                        safeRunOnUiThread(() -> {
                            alertButtonVisibility(false);
                            loadData_Log();
                        });
                        // 分析是否有高星
                        new Thread(() -> {
                            if (datas_log.size() == 0) return;
                            long tNew = datas_log.get(datas_log.size() - 1).ts0;
                            Set<String> set = spTool.getNewOperatorTimeSet();
                            // boolean needClean = false;
                            final long[] t00 = {0};
                            set.forEach(s -> {
                                try {
                                    long tOld = Long.parseLong(s);
                                    if (tOld > t00[0]) {
                                        t00[0] = tOld;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                            if (t00[0] < tNew) {
                                set = new HashSet<>();
                                spTool.cleanNewOperatorTimeSet();
                            }
                            Set<String> finalSet = set;
                            datas_log.forEach((gameLog -> {
                                String regex4 = ".*5★.*|.*6★.*";
                                if (gameLog.getInfo().matches(regex4)) {
                                    boolean hasInformed = finalSet.contains(gameLog.ts0 + "");
                                    if (hasInformed) return;

                                    String y = gameLog.getInfo().matches(".*5★.*") ? "五星" : "六星";
                                    String x = SimpleTool.protectTelephoneNum(GamesFragment.this.gameInstanceInfo.account) + "于" + ToolTime.getFormatDate(gameLog.ts0, false) + "出现" + y + "干员！";
                                    toastInThread(x);
                                    notifyUser(SimpleTool.protectTelephoneNum(GamesFragment.this.gameInstanceInfo.account), "于" + ToolTime.getFormatDate(gameLog.ts0, false) + "有" + y + "干员出现了");
                                    spTool.addNewOperatorTime(gameLog.ts0 + "");
                                }
                            }));
                        }).start();

                        // 分析作战情况
                        new Thread(() -> {
                            if (datas_log.size() == 0) return;
                            BetterEntry<Long, Map<String, Integer>> entry = parsingLogsToInfo(datas_log);
                            Map<String, Integer> collectMaps = entry.getValue();

                            // StringBuilder ss = new StringBuilder();
                            if (ToolTime.getTimeShanghai() - datas_log.get(0).ts0 < 10 * 60 * 1000 ||
                                    entry.getKey() - ToolTime.getTimeShanghai() < 8 * 60 * 1000) {
                                String t00 = textView.getText().toString();
                                safeRunOnUiThread(() -> {
                                    textView.setText(t00.replace("托管", "运行"));
                                });
                            }
                            float time = (ToolTime.getTimeShanghai() * 1.0f - datas_log.get(datas_log.size() - 1).ts0) / 1000f / 3600f;
                            Log.d(TAG, "run: 计算托管时间" + time + "," + ToolTime.getTimeShanghai());
                            String c000 = (entry.getKey() > 0 ? "下次运行时间：" + ToolTime.getFormatDate(entry.getKey(), false) : "下次运行时间：未知")
                                    + "\n托管时长：%.1f小时";
                            c000 = String.format(Locale.CHINA, c000, time);
                            //创建一个 SpannableStringBuilder
                            SpannableStringBuilder spannableStringBuilder0 = new SpannableStringBuilder();

                            SpannableString spannableString0 = new SpannableString("下次运行时间 \n");
                            //设置字体大小（相对值,单位：像素） 参数表示为默认字体宽度的多少倍
                            spannableString0.setSpan(new RelativeSizeSpan(0.67f), 0, spannableString0.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannableString0.setSpan(new StyleSpan(Typeface.NORMAL), 0, spannableString0.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                            //设置字体大小（绝对值,单位：像素）
                            // spannableString.setSpan(new TextAppearanceSpan(null, 0, 50, null, null), 2, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            //将SpannableStringBuilder设置给TextView
                            spannableStringBuilder0.append(spannableString0);
                            spannableStringBuilder0.append(ToolTheme.createColorText(entry.getKey() > 0 ? ToolTime.getFormatDate(entry.getKey(), false) : "未知", getContext().getColor(R.color.text_black)));


                            SpannableString spannableString1 = new SpannableString("\n\n");
                            //设置字体大小（相对值,单位：像素） 参数表示为默认字体宽度的多少倍
                            spannableString1.setSpan(new RelativeSizeSpan(0.5f), 0, spannableString1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannableString1.setSpan(new StyleSpan(Typeface.NORMAL), 0, spannableString1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannableStringBuilder0.append(spannableString1);
                            SpannableString spannableString2 = new SpannableString("托管时长 \n");
                            //设置字体大小（相对值,单位：像素） 参数表示为默认字体宽度的多少倍
                            spannableString2.setSpan(new RelativeSizeSpan(0.67f), 0, spannableString2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannableString2.setSpan(new StyleSpan(Typeface.NORMAL), 0, spannableString2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannableStringBuilder0.append(spannableString2);
                            spannableStringBuilder0.append(ToolTheme.createColorText(String.format(Locale.CHINA, "%.1f小时", time), getContext().getColor(R.color.text_black)));
                            safeRunOnUiThread(() -> {t13.setText(spannableStringBuilder0);});

                            // alertTextView2(c000);
                            //今日收益：
                            SpannableStringBuilder ssb = new SpannableStringBuilder("");
                            collectMaps.forEach((name, amount) -> {
                                        // building_data 家具数据在这里，暂时不引入，因为没有数据
                                        boolean flag = name.matches(".*家具.*|.*未识别.*") && !name.contains("家具零件");
                                        if (flag) {
                                            // name = name.replace("家具]", "");
                                            name = "[" + name;
                                        }
                                        String text0 = name + "：" + amount + "；";
                                        // ss.append(text0);
                                        SpannableString spannableString = new SpannableString(text0);
                                        if (!flag) {
                                            String url = dzp.getItemIconUrl(name);
                                            UrlImageSpan imageSpan = new UrlImageSpan(getContext(), url + "", name, t14, amount);
                                            spannableString.setSpan(imageSpan, 0, text0.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                                            String clickHint = name + "（共" + amount + "个）";
                                            ClickableSpan clickableSpan = new ClickableSpan() {
                                                @Override
                                                public void onClick(@NonNull View widget) {
                                                    toastInThread(clickHint);
                                                }
                                            };
                                            spannableString.setSpan(clickableSpan, 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        } else {
                                            UrlImageSpan imageSpan = new UrlImageSpan(getContext(), name, name, t14, amount);
                                            spannableString.setSpan(imageSpan, 0, text0.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                                            String clickHint = name + "（共" + amount + "个）";
                                            ClickableSpan clickableSpan = new ClickableSpan() {
                                                @Override
                                                public void onClick(@NonNull View widget) {
                                                    toastInThread(clickHint);
                                                }
                                            };
                                            spannableString.setSpan(clickableSpan, 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        }
                                        ssb.append(spannableString);
                                    }
                            );
                            if (collectMaps.size() == 0) ssb.append("暂无;");

                            if (ssb.length() > 5) {
                                // String ssString = ss.substring(0, ss.length() - 1);
                                safeRunOnUiThread(() -> {
                                    // t14.setText("今日收益：" + ssString);
                                    // t14.setVisibility(View.VISIBLE);
                                    t14.setText(ssb.subSequence(0, ssb.length() - 1));

                                });
                            }

                        }).start();
                    }).start();

                    // 只有运行状态才进行
                    if (game0.code != Game.WebGame_Status_Code_Running && game0.code != Game.WebGame_Status_Code_Loginning) {
                        currentPlatform = game0.platform;
                        currentAccount = game0.account;
                        main.setKey(main.getKey().replace("-", "未运行"));
                        if (spTool.getQuickLogin()) {
                            alertTextView("状态：" + main.getKey() + "\n战斗地图:" + main.getValue() + "\n\n如需要重启，可以点击头像尝试启动账户");
                            needLogin = true;
                        } else {
                            alertTextView("状态：" + main.getKey() + "\n战斗地图:" + main.getValue() + "\n\n点击头像获得更多信息");
                        }
                        return;
                    }

                    Data.AccountData data0 = Data.getBasicInfo(SimpleApplication.getContext(), game0);
                    GamesFragment.this.name = "Dr." + data0.nickName;
                    ((MainActivity) getActivity()).alertTabText();
                    // String name = ToolTable.getInstance().getCharacterTable().get(data0.secretary).getAsJsonObject().get("name").getAsString();

                    // 也许可以通过lastApAddTime来判断
                    int apCal = data0.ap + (int) (ToolTime.getTimeShanghai() - data0.lastApAddTime) / 1000 / 360;
                    if (apCal > data0.maxAp) {
                        // if(data0.ap>data0.maxAp)xpCal=data0.ap;
                        // else xpCal=data0.maxAp;
                        apCal = Math.max(data0.ap, data0.maxAp);
                    }
                    String sss =
                            // "源石数量：" + data0.androidDiamond
                            // + "\n合成玉数量：" + data0.diamondShard
                            // + "\n龙门币数量：" + data0.gold
                            // + "\n当前理智：" + apCal + "/" + data0.maxAp
                            // // 独立计算
                            // +
                            "理智溢出时间：" + ToolTime.getFormatDate(data0.lastApAddTime + (data0.maxAp - data0.ap) * 6 * 60 * 1000, false);

                    Log.d(TAG, "run: " + (data0.maxAp - data0.ap));
                    // Log.d(TAG, "run: "+Data.getStageTable(SimpleApplication.getContext()));

                    // JSONObject itemJsonObject = Data.getItemTable();

                    boolean needOCR = (ToolTime.getTimeShanghai() / 1000f - data0.lastFreshTs) > 24 * 3600
                            && spTool.getAutoWarehouseIdentification();
                    Log.d(TAG, "计算上次时间: " + (ToolTime.getTimeShanghai() / 1000f - data0.lastFreshTs) + "s");
                    if (needOCR) {
                        Data.requestForOCR(SimpleApplication.getContext(), game0);
                    }
                    int finalApCal = apCal;
                    safeRunOnUiThread(() -> {
                        // Context context=SimpleApplication.getContext();
                        // 注意getApplicationContext()获取的是应用，对话框必须指向activity
                        // RoundedCorners roundedCorners = new RoundedCorners(20);//圆角为5
                        if (needOCR)
                            SimpleTool.toastInThread(getActivity(), "距离上次仓库识别已经超过24小时或您已经重新登录，已为您重新申请新的识别");

                        dzp.loadImageIntoImageview(data0, getActivity(), imageView);
                        // 这里看看怎么优化，还有ToolTime.getTimeShanghai()强制走时区
                        // Log.d(TAG, "run:计算时间 "+ToolTime.getTimeShanghai());
                        if (ToolTime.getTimeShanghai() - data0.lastApAddTime < 10 * 60 * 1000)
                        {
                            main.setKey("运行中");
                            game0.code = 100;
                            safeRunOnUiThread(() -> {
                                try {
                                    imageView_state.setImageDrawable(getContext().getDrawable(R.drawable.pot_blue));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                        safeRunOnUiThread(() -> {
                            SpannableStringBuilder spannableStringBuilder0 = new SpannableStringBuilder();
                            String[] status = new String[]{"状态", main.getKey()};
                            spannableStringBuilder0.append(ToolTheme.createStyleText(ToolTheme.createSizeText(status[0] + "\n", 0.67f), Typeface.NORMAL));

                            switch (game0.code) {
                                case Game.WebGame_Status_Code_Running:
                                    spannableStringBuilder0.append(ToolTheme.createColorText(status[1], getContext().getColor(R.color.lightgreen)));
                                    break;
                                case Game.WebGame_Status_Code_Loginning:
                                    spannableStringBuilder0.append(ToolTheme.createColorText(status[1], getContext().getColor(R.color.yellow)));
                                    break;
                                case Game.WebGame_Status_Code_NeedCheck:
                                    spannableStringBuilder0.append(ToolTheme.createColorText(status[1], getContext().getColor(R.color.orange)));
                                    break;
                                case 100:
                                    spannableStringBuilder0.append(ToolTheme.createColorText(status[1], getContext().getColor(R.color.lightskyblue)));
                                    break;
                                default:
                                    // if (game0.status.equals("-")) {
                                    //     sTip = new SpannableString("未运行\n");
                                    // }
                                    spannableStringBuilder0.append(ToolTheme.createColorText(status[1], getContext().getColor(R.color.crimson)));
                                    break;

                            }

                            spannableStringBuilder0.append(ToolTheme.createSizeText("\n\n", 0.5f));
                            String[] battleMap = new String[]{"战斗地图", main.getValue()};
                            // String[] battleMap = main.getValue().replace("\n", "").split("：");
                            spannableStringBuilder0.append(ToolTheme.createStyleText(ToolTheme.createSizeText(battleMap[0] + "\n", 0.67f), Typeface.NORMAL));
                            spannableStringBuilder0.append(ToolTheme.createColorText(battleMap[1], getContext().getColor(R.color.text_black)));
                            // spannableStringBuilder0.append("\n");
                            safeRunOnUiThread(() -> {textView.setText(spannableStringBuilder0);});
                        });
                        // alertTextView(main.getKey() + main.getValue());

                        t_gold.setText(SimpleTool.getShorterAmountDescriptionText(data0.gold));
                        t_diamond_shd.setText(SimpleTool.getShorterAmountDescriptionText(data0.diamondShard));
                        t_diamond.setText(SimpleTool.getShortestAmountDescriptionText(data0.androidDiamond));
                        t_ap.setText(finalApCal + "/" + data0.maxAp);

                        View.OnClickListener onClickListener = v -> {
                            BasePopupView builder = new XPopup.Builder(getContext())
                                    .atView(v)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
                                    .asAttachList(new String[]{sss},
                                            new int[]{},
                                            (position, text) -> {
                                                // toast("click " + text);
                                            });
                            // builder.setText(new ColorDrawable(ToolTheme.getColorValue(getActivity(),R.attr.colorAccent)));
                            builder.show();
                        };
                        iv_ap.setOnClickListener(onClickListener);
                        t_ap.setOnClickListener(onClickListener);
                    });
                } catch (IOException ex) {
                    ex.printStackTrace();
                    toastInThread("网络有误，访问失败！");
                    // notifyUser("", "无法连接上");
                } catch (Exception e) {
                    e.printStackTrace();
                    // toastInThread("正在维护");
                    // notifyUser("正在维护");
                }
            }
        }).start();
    }

    private BetterEntry<Long, Map<String, Integer>> parsingLogsToInfo(List<GameLog> list) {
        Map<String, Integer> collectMaps = new LinkedHashMap<>();
        List<String> timeList = new ArrayList<>();

        //计算到游戏上一次刷新时间
        long timeNow = ToolTime.getTimeShanghai();
        long hour4Today = ToolTime.get0HourTodayMills() + 4 * 3600 * 1000;
        // Log.d(TAG, "parsingLogsToInfo: "+SimpleTool.getFormatDate(ToolTime.getTimeShanghai(), false)+","
        //         +SimpleTool.getFormatDate(SimpleTool.get0HourTodayMills(), false)+","+ TimeZone.getDefault().getRawOffset());

        if (hour4Today > timeNow)
            hour4Today = hour4Today - 24 * 3600 * 1000;
        // Log.d(TAG, "parsingLogsToInfo: "+timeNow+","+hour4Today+","+( SimpleTool.get0HourTodayMills() + 4 * 3600 * 1000));
        for (int i = 0; i < list.size(); i++) {
            final String aa = list.get(i).getInfo();

            // 计算到四点截止
            if (ToolTime.transferString2Date(list.get(i).getTs()) < hour4Today)
                break;
            // // 不计算无内容的字符串
            // if (!(flag1_battle || flag1_trade||flag2_battle||flag2_construct))
            //     continue;

            List<String> ddList = new ArrayList<>();
            if (aa.contains("战斗结束") || aa.contains("扫荡完成")) {            // 计算分割位置

                List<Integer> startPos = SimpleTool.getSignPoss(aa, "[".charAt(0));
                List<Integer> endPos = SimpleTool.getSignPoss(aa, "]".charAt(0));
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < startPos.size(); j++) {
                    Integer a = startPos.get(j);
                    Integer b = endPos.get(j);
                    String ccString = aa.substring(a + 1, b) + ", ";
                    if (ccString.contains("(")) {
                        sb.append(ccString);
                    }
                }

                // 解析具体物品
                ddList = Arrays.asList(sb.toString().split(", "));
            } else if (aa.contains("完成公招")) {
                // 解析具体物品
                try {
                    ddList = Arrays.asList(aa.split("获得物品：")[1].split(", "));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (aa.contains("单抽获得干员")) {
                // 解析具体物品
                try {
                    ddList = Arrays.asList(aa.split("获得物品：")[1].split(", "));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // [签到] "新春限时签到活动" 签到活动·第14天签到完成~获得：芯片助剂(1), 加急许可(5)
            else if (aa.contains("签到")) {
                try {
                    String[] getList = aa.split("获得：");
                    if (getList.length > 1)
                        ddList = Arrays.asList(aa.split("获得：")[1].split(", "));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "parsingLogsToInfo: " + aa);
                }
            } else if (aa.contains("收取来自")) {
                try {
                    String cc = aa.split("的： ")[1];
                    ddList.add(cc);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // [基建] 收取信用，获得 信用(20)
            // [信用] 获取每日信用奖励：信用(230)
            // [基建] 今日访问好友的300信用点数已获得
            // 不是重点，因此不统计
            // else if (aa.contains("收取信用")) {
            //     try {
            //         String cc = aa.split("获得 ")[1];
            //         ddList.add(cc);
            //     } catch (Exception e) {
            //         e.printStackTrace();
            //     }
            // }
            // Log.d(TAG, "parsingLogsToInfo: "+ddList.size());
            // 解析物品
            for (int j = 0; j < ddList.size(); j++) {
                String get = ddList.get(j);
                int p1 = get.indexOf("(");
                int p2 = get.indexOf(")");
                try {
                    String gg = get.substring(0, p1);
                    int amount = Integer.parseInt(get.substring(p1 + 1, p2));
                    if (collectMaps.containsKey(gg))
                        amount += collectMaps.get(gg);
                    collectMaps.put(gg, amount);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "parsingLogsToInfo: " + get);
                }
            }

        }

        long t_b = 0;
        long t_c = 0;
        long t_clue = 0;
        for (int i = 0; i < list.size(); i++) {
            if (t_clue != 0 && t_b != 0 && t_c != 0)
                break;
            final String aa = list.get(i).getInfo();
            if (aa.contains("下次基建排班") && t_c == 0) {
                // [泠夭v2] 下次基建排班将在 2023-02-04 01:56:53 自动进行
                String aaz = aa.replace("[泠夭v2] 下次基建排班将在 ", "")
                        .replace(" 自动进行", "");
                t_c = ToolTime.transferString2Date2(aaz);
                if (t_c < timeNow) t_c = 0;
                Log.d(TAG, "parsingLogsToInfo: " + aaz + t_c);
            } else if (aa.contains("下次战斗") && t_b == 0) {
                // [幽岚v3] 计划在 2023-02-04 03:00:25 开启下次战斗。
                String aaz = aa.replace("[幽岚v3] 计划在 ", "")
                        .replace(" 开启下次战斗。", "");
                t_b = ToolTime.transferString2Date2(aaz);
                if (t_b < timeNow) t_b = 0;
            } else if (aa.contains("收取线索奖励") && t_clue == 0) {
                // [基建] 计划在 2023-01-25 04:05:50 收取线索奖励
                String aaz = aa.replace("[基建] 计划在 ", "")
                        .replace(" 收取线索奖励", "");
                t_clue = ToolTime.transferString2Date2(aaz);
                if (t_clue < timeNow) t_clue = 0;
            }
        }
        final long[] tt = {0};

        List<Long> intList = new ArrayList<>();
        if (t_b > 0) intList.add(t_b);
        if (t_c > 0) intList.add(t_c);
        if (t_clue > 0) intList.add(t_clue);
        intList.forEach(i -> {
            if (i > timeNow && (i < tt[0] || tt[0] == 0)) tt[0] = i;
        });


        return new BetterEntry<>(tt[0], collectMaps);
    }


    private void loadData_Log() {
        LinearLayoutManager r = new LinearLayoutManager(getContext());
        r.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView_log.setLayoutManager(r);
        adapter_log = new GameLogAdapter(R.layout.ic_logrecord, datas_log);
        recyclerView_log.setAdapter(adapter_log);
    }


    private void notifyUser(String account, String x) {
        if (getActivity() != null && getActivity() instanceof BaseActivity)
            ((BaseActivity) getActivity()).notifyUser(account, x);
    }

    private void alertButtonVisibility(boolean hide) {
        safeRunOnUiThread(() -> {
            if (hide) {
                if (bt_inventory != null) bt_inventory.setVisibility(View.INVISIBLE);
                if (bt_inventory != null) bt_screenshot.setVisibility(View.INVISIBLE);
            } else {
                if (bt_inventory != null) bt_inventory.setVisibility(View.VISIBLE);
                if (bt_inventory != null) bt_screenshot.setVisibility(View.VISIBLE);
            }
        });
    }

    private void tryLogin() {
        toastInThread("尝试登陆中");
        if (Game.TryLogin(getActivity(), currentAccount, currentPlatform)) {
            alertTextView("正在登陆中！");
            Timer timer = new Timer();
            long cacheTime = ToolTime.getTimeShanghai();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {

                    try {
                        refreshGameInfo();
                        Game.GameInfo info = GamesFragment.this.gameInstanceInfo;
                        int code = info.code;
                        if (code == Game.WebGame_Status_Code_Running) {
                            long nowTime = ToolTime.getTimeShanghai();
                            double duration = (nowTime - cacheTime) / 1000.0d;
                            Log.d(TAG, "run: " + "登录完成，共耗时" + duration + "s");
                            toastInThread("登录完成，共耗时" + duration + "s");
                            Thread.sleep(300);
                            timer.cancel();
                            timer.purge();
                            clearAndLoad();
                        } else if (code != Game.WebGame_Status_Code_Loginning) {
                            toastInThread("暂时请访问可露希尔网页进行手动确认，也可以通知开发者提供信息帮助完成这一功能");
                            alertTextView("完成手动确认后请点击头像重新加载数据");
                            notifyUser(SimpleTool.protectTelephoneNum(info.account), "需要手动登录");
                            // 之后回到正常登录流程
                            // timer.cancel();
                            // timer.purge();
                            Intent ii = new Intent(SimpleApplication.getContext(), CheckActivity.class);
                            ii.putExtra("challenge", info.challenge);
                            ii.putExtra("gt", info.gt);
                            ii.putExtra("account", info.account);
                            ii.putExtra("platform", info.platform);
                            startActivity(ii);

                        }

                    } catch (IOException | JSONException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, 10000, 10000);

        } else
            toastInThread("请访问可露希尔工作室进行检查或者更换网络！");
    }

    private void refreshGameInfo() throws IOException, JSONException {
        this.gameInstanceInfo = GamesFragment.this.gameInstanceInfo.getEqualNewInstance(Game.getGameStatue(SimpleApplication.getContext()));
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void scrollTo(int i, int i1) {
        if (nsv1 != null) {
            nsv1.smoothScrollTo(0, 0);

        }
    }


    // public class CustomAttachPopup extends AttachPopupView {
    // public CustomAttachPopup(@NonNull Context context) {
    //         super(context);
    //     }
    //     @Override
    //     protected int getImplLayoutId() {
    //         return R.layout.custom_attach_popup2;
    //     }
    // }
}
