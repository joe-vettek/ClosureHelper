package com.xueluoanping.arknights.pages.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class GamesFragment extends BaseFragment implements FragmentWithName {
    private static final String TAG = GamesFragment.class.getSimpleName();
    private ImageView imageView;
    private TextView textView;
    private ArrayList<ItemModel> datas = new ArrayList<>();
    private ArrayList<ItemModel> datas2 = new ArrayList<>();
    private RecyclerView recyclerView_log;
    private GameLogAdapter adapter_log;
    private List<GameLog> datas_log = new ArrayList<>();
    private Button bt_inventory;
    private Button bt_screenshot;
    private NestedScrollView nsv1;
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
        textView = root.findViewById(R.id.textView12);
        recyclerView_log = root.findViewById(R.id.logList);
        bt_inventory = root.findViewById(R.id.bt_inventory);
        bt_screenshot = root.findViewById(R.id.bt_screenshot);
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

        // ?????????????????????

        //??????????????????????????????
        // else {

        clearAndLoad();
        // }
    }

    private void initData() {
        gameInstanceInfo = new Game.GameInfo().backupFromBundle(getArguments());
    }


    private void bindComponents() {

        // ??????????????????
        t14.setMovementMethod(LinkMovementMethod.getInstance());

        // t14.setLongClickable(false);
        // ??????????????????????????????????????? Attempt to invoke virtual method 'int android.widget.Editor$SelectionModifierCursorController.getMinTouchOffset()' on a null object reference
        // https://blog.csdn.net/lxk_1993/article/details/78674324 ??????????????????
        // t14.setOnLongClickListener(v -> {
        //     t14.setSelected(true);
        //     return true;
        // });
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (needLogin) {
                    Log.d(TAG, "onClick: ???????????????");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            tryLogin();

                        }
                    }).start();

                } else {
                    ArrayList<String> items = new ArrayList<>();
                    items.add("??? ????????????????????????");
                    items.add("??? ??????????????????????????????????????????");
                    // items.add("??? ????????????????????????");
                    items.add("??? ????????????????????????");
                    // items.add("??? ??????????????????");
                    // items.add("??? ??????");

                    String[] itemArray = items.toArray(new String[0]);
                    for (String s : itemArray) Log.d(TAG, "onClick: " + s);

                    AlertDialog dialog = new AlertDialog.Builder(getActivity())
                            .setTitle("????????????")
                            .setIcon(R.mipmap.npc_007_closure)
                            .setItems(itemArray, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String name = items.get(which);
                                    Log.d(TAG, "onClick: " + name);

                                    if (name.contains("??????")) {
                                        SimpleTool.toastInThread(getActivity(), "???????????????");
                                        new Thread(() -> {
                                            try {
                                                refreshGameInfo();
                                            } catch (IOException | JSONException e) {
                                                e.printStackTrace();
                                            }
                                            clearAndLoad();
                                        }).start();
                                        // needLogin = false;

                                    } else if (name.contains("??????")) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                tryLogin();
                                            }
                                        }).start();

                                    } else if (name.contains("??????")) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Data.requestForOCR(SimpleApplication.getContext(), GamesFragment.this.gameInstanceInfo);
                                                    SimpleTool.toastInThread(getActivity(), "??????????????????????????????????????????????????????????????????????????????????????????????????????????????????");
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    SimpleTool.toastInThread(getActivity(), "???????????????????????????????????????????????????????????????");

                                                }
                                            }
                                        }).start();
                                    } else {
                                        if (name.contains("????????????")) {
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
            //     SimpleTool.toastInThread(getActivity(), "???????????????????????????");
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
        alertTextView("\n\n\n\n\n\n" + getText(R.string.timeoutText).toString());
        alertTextView2("");
        // if (!
        if (getActivity() != null && getActivity() instanceof BaseActivity)
            ((BaseActivity) getActivity()).queryArknightsIsMaintaining();
        // ) return;
        loadStatus();
    }


    public void toastInThread(String s) {
        safeRunOnUiThread(() -> Toast.makeText(SimpleApplication.getContext(), s, Toast.LENGTH_SHORT).show());
    }

    public void alertTextView(String s) {
        safeRunOnUiThread(() -> {
            textView.setText(s);
        });
    }

    public void alertTextView2(String s) {
        safeRunOnUiThread(() -> {
            t14.setText(s);
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

                    BetterEntry<String, String> main =
                            stageJsonObject == null ?
                                    new BetterEntry<>("?????????" + game0.status, "\n???????????????" + game0.mapId) :
                                    new BetterEntry<>("?????????" + game0.status,
                                            "\n???????????????" + stageJsonObject.get("code").getAsString() + "(" + stageJsonObject.get("name").getAsString() + ")");


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            datas_log = Game.getLog(game0.account, game0.platform);
                            // ????????????
                            safeRunOnUiThread(() -> {
                                alertButtonVisibility(false);
                                loadData_Log();
                            });
                            // ?????????????????????
                            new Thread(() -> {
                                datas_log.forEach((gameLog -> {
                                    String regex4 = ".*5???.*|.*6???.*";
                                    if (gameLog.getInfo().matches(regex4)) {
                                        String x = SimpleTool.protectTelephoneNum(GamesFragment.this.gameInstanceInfo.account) + "?????????????????????";
                                        toastInThread(x);
                                        notifyUser(SimpleTool.protectTelephoneNum(GamesFragment.this.gameInstanceInfo.account), "????????????????????????");
                                    }
                                }));
                            }).start();

                            // ??????????????????
                            new Thread(() -> {
                                if (datas_log.size() == 0) return;
                                BetterEntry<Long, Map<String, Integer>> entry = parsingLogsToInfo(datas_log);
                                Map<String, Integer> collectMaps = entry.getValue();

                                // StringBuilder ss = new StringBuilder();
                                if (ToolTime.getTimeShanghai() - datas_log.get(0).ts0 < 10 * 60 * 1000 ||
                                        entry.getKey() - ToolTime.getTimeShanghai() < 8 * 60 * 1000) {
                                    String t00 = textView.getText().toString();
                                    safeRunOnUiThread(() -> {
                                        textView.setText(t00.replace("??????", "??????"));
                                    });
                                }
                                float time = (ToolTime.getTimeShanghai() * 1.0f - datas_log.get(datas_log.size() - 1).ts0) / 1000f / 3600f;
                                Log.d(TAG, "run: ??????????????????" + time);
                                String c000 = (entry.getKey() > 0 ? "?????????????????????" + ToolTime.getFormatDate(entry.getKey(), false) : "???????????????????????????")
                                        + "\n???????????????%.1f??????\n";
                                c000 = String.format(Locale.CHINA, c000, time);
                                SpannableStringBuilder ssb = new SpannableStringBuilder(c000 + "???????????????");
                                collectMaps.forEach((name, amount) -> {
                                            // building_data ????????????????????????????????????????????????????????????
                                            boolean flag = name.matches(".*??????.*|.*?????????.*") && !name.contains("????????????");
                                            if (flag) {
                                                // name = name.replace("??????]", "");
                                                name = "[" + name;
                                            }
                                            String text0 = name + "???" + amount + "???";
                                            // ss.append(text0);
                                            SpannableString spannableString = new SpannableString(text0);
                                            if (!flag) {
                                                String url = dzp.getItemIconUrl(name);
                                                UrlImageSpan imageSpan = new UrlImageSpan(getContext(), url + "", t14, amount);
                                                spannableString.setSpan(imageSpan, 0, text0.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                                                String clickHint = name + "??????" + amount + "??????";
                                                ClickableSpan clickableSpan = new ClickableSpan() {
                                                    @Override
                                                    public void onClick(@NonNull View widget) {
                                                        toastInThread(clickHint);
                                                    }
                                                };
                                                spannableString.setSpan(clickableSpan, 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                            } else {
                                                UrlImageSpan imageSpan = new UrlImageSpan(getContext(), name, t14, amount);
                                                spannableString.setSpan(imageSpan, 0, text0.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                                                String clickHint = name + "??????" + amount + "??????";
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
                                if (collectMaps.size() == 0) ssb.append("??????;");

                                if (ssb.length() > 5) {
                                    // String ssString = ss.substring(0, ss.length() - 1);
                                    safeRunOnUiThread(() -> {
                                        // t14.setText("???????????????" + ssString);

                                        t14.setText((SpannableStringBuilder) ssb.subSequence(0, ssb.length() - 1));
                                    });
                                }

                            }).start();
                        }
                    }).start();

                    // ???????????????????????????
                    if (game0.code != Game.WebGame_Status_Code_Running && game0.code != Game.WebGame_Status_Code_Loginning) {
                        currentPlatform = game0.platform;
                        currentAccount = game0.account;
                        main.setKey(main.getKey().replace("?????????-", "??????????????????"));
                        if (spTool.getQuickLogin()) {
                            alertTextView(main.getKey() + main.getValue() + "\n\n??????????????????????????????????????????????????????");
                            needLogin = true;
                        } else {
                            alertTextView(main.getKey() + main.getValue() + "\n\n??????????????????????????????");
                        }
                        return;
                    }

                    Data.AccountData data0 = Data.getBasicInfo(SimpleApplication.getContext(), game0);
                    GamesFragment.this.name = "Dr." + data0.nickName;
                    ((MainActivity) getActivity()).alertTabText();
                    // String name = ToolTable.getInstance().getCharacterTable().get(data0.secretary).getAsJsonObject().get("name").getAsString();

                    // ??????????????????lastApAddTime?????????
                    int apCal = data0.ap + (int) (ToolTime.getTimeShanghai() - data0.lastApAddTime) / 1000 / 360;
                    if (apCal > data0.maxAp) {
                        // if(data0.ap>data0.maxAp)xpCal=data0.ap;
                        // else xpCal=data0.maxAp;
                        apCal = Math.max(data0.ap, data0.maxAp);
                    }
                    String sss =
                            // "???????????????" + data0.androidDiamond
                            // + "\n??????????????????" + data0.diamondShard
                            // + "\n??????????????????" + data0.gold
                            // + "\n???????????????" + apCal + "/" + data0.maxAp
                            // // ????????????
                            // +
                            "?????????????????????" + ToolTime.getFormatDate(data0.lastApAddTime + (data0.maxAp - data0.ap) * 6 * 60 * 1000, false);

                    Log.d(TAG, "run: " + (data0.maxAp - data0.ap));
                    // Log.d(TAG, "run: "+Data.getStageTable(SimpleApplication.getContext()));

                    // JSONObject itemJsonObject = Data.getItemTable();

                    boolean needOCR = (ToolTime.getTimeShanghai() / 1000f - data0.lastFreshTs) > 24 * 3600
                            && spTool.getAutoWarehouseIdentification();
                    Log.d(TAG, "??????????????????: " + (ToolTime.getTimeShanghai() / 1000f - data0.lastFreshTs) + "s");
                    if (needOCR) {
                        Data.requestForOCR(SimpleApplication.getContext(), game0);
                    }
                    int finalApCal = apCal;
                    safeRunOnUiThread(() -> {
                        // Context context=SimpleApplication.getContext();
                        // ??????getApplicationContext()??????????????????????????????????????????activity
                        // RoundedCorners roundedCorners = new RoundedCorners(20);//?????????5
                        if (needOCR)
                            SimpleTool.toastInThread(getActivity(), "????????????????????????????????????24??????????????????????????????????????????????????????????????????");

                        dzp.loadImageIntoImageview(data0, getActivity(), imageView);
                        // ?????????????????????????????????ToolTime.getTimeShanghai()???????????????
                        // Log.d(TAG, "run:???????????? "+ToolTime.getTimeShanghai());
                        // if (ToolTime.getTimeShanghai() - data0.lastApAddTime < 10 * 60 * 1000) {
                        //     main.setKey("??????????????????");
                        //
                        // }
                        alertTextView(main.getKey() + main.getValue());

                        t_gold.setText(SimpleTool.getShorterAmountDescriptionText(data0.gold));
                        t_diamond_shd.setText(SimpleTool.getShorterAmountDescriptionText(data0.diamondShard));
                        t_diamond.setText(SimpleTool.getShortestAmountDescriptionText(data0.androidDiamond));
                        t_ap.setText(finalApCal + "/" + data0.maxAp);

                        View.OnClickListener onClickListener = v -> {
                            BasePopupView builder = new XPopup.Builder(getContext())
                                    .atView(v)  // ?????????????????????View???????????????????????????????????????????????????
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
                    toastInThread("??????????????????????????????");
                    // notifyUser("", "???????????????");
                } catch (Exception e) {
                    e.printStackTrace();
                    // toastInThread("????????????");
                    // notifyUser("????????????");
                }
            }
        }).start();
    }

    private BetterEntry<Long, Map<String, Integer>> parsingLogsToInfo(List<GameLog> list) {
        Map<String, Integer> collectMaps = new LinkedHashMap<>();
        List<String> timeList = new ArrayList<>();

        //????????????????????????????????????
        long timeNow = ToolTime.getTimeShanghai();
        long hour4Today = ToolTime.get0HourTodayMills() + 4 * 3600 * 1000;
        // Log.d(TAG, "parsingLogsToInfo: "+SimpleTool.getFormatDate(ToolTime.getTimeShanghai(), false)+","
        //         +SimpleTool.getFormatDate(SimpleTool.get0HourTodayMills(), false)+","+ TimeZone.getDefault().getRawOffset());

        if (hour4Today > timeNow)
            hour4Today = hour4Today - 24 * 3600 * 1000;
        // Log.d(TAG, "parsingLogsToInfo: "+timeNow+","+hour4Today+","+( SimpleTool.get0HourTodayMills() + 4 * 3600 * 1000));
        for (int i = 0; i < list.size(); i++) {
            final String aa = list.get(i).getInfo();

            // ?????????????????????
            if (ToolTime.transferString2Date(list.get(i).getTs()) < hour4Today)
                break;
            // // ??????????????????????????????
            // if (!(flag1_battle || flag1_trade||flag2_battle||flag2_construct))
            //     continue;

            List<String> ddList = new ArrayList<>();
            if (aa.contains("????????????") || aa.contains("????????????")) {            // ??????????????????

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

                // ??????????????????
                ddList = Arrays.asList(sb.toString().split(", "));
            } else if (aa.contains("????????????")) {
                // ??????????????????
                try {
                    ddList = Arrays.asList(aa.split("???????????????")[1].split(", "));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // [??????] "????????????????????????" ?????????????????14???????????????~?????????????????????(1), ????????????(5)
            else if (aa.contains("??????")) {
                try {
                    ddList = Arrays.asList(aa.split("?????????")[1].split(", "));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (aa.contains("????????????")) {
                try {
                    String cc = aa.split("?????? ")[1];
                    ddList.add(cc);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // [??????] ????????????????????? ??????(20)
            // [??????] ?????????????????????????????????(230)
            // [??????] ?????????????????????300?????????????????????
            // ??????????????????????????????
            // else if (aa.contains("????????????")) {
            //     try {
            //         String cc = aa.split("?????? ")[1];
            //         ddList.add(cc);
            //     } catch (Exception e) {
            //         e.printStackTrace();
            //     }
            // }
            // Log.d(TAG, "parsingLogsToInfo: "+ddList.size());
            // ????????????
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
            if (aa.contains("??????????????????") && t_c == 0) {
                // [??????v2] ???????????????????????? 2023-02-04 01:56:53 ????????????
                String aaz = aa.replace("[??????v2] ???????????????????????? ", "")
                        .replace(" ????????????", "");
                t_c = ToolTime.transferString2Date2(aaz);
                if (t_c < timeNow) t_c = 0;
                Log.d(TAG, "parsingLogsToInfo: " + aaz + t_c);
            } else if (aa.contains("????????????") && t_b == 0) {
                // [??????v3] ????????? 2023-02-04 03:00:25 ?????????????????????
                String aaz = aa.replace("[??????v3] ????????? ", "")
                        .replace(" ?????????????????????", "");
                t_b = ToolTime.transferString2Date2(aaz);
                if (t_b < timeNow) t_b = 0;
            } else if (aa.contains("??????????????????") && t_clue == 0) {
                // [??????] ????????? 2023-01-25 04:05:50 ??????????????????
                String aaz = aa.replace("[??????] ????????? ", "")
                        .replace(" ??????????????????", "");
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
        LinearLayoutManager r = new LinearLayoutManager(SimpleApplication.getContext());
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
        toastInThread("???????????????");
        if (Game.TryLogin(SimpleApplication.getContext(), currentAccount, currentPlatform)) {
            alertTextView("??????????????????");
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
                            Log.d(TAG, "run: " + "????????????????????????" + duration + "s");
                            toastInThread("????????????????????????" + duration + "s");
                            Thread.sleep(300);
                            timer.cancel();
                            timer.purge();
                            clearAndLoad();
                        } else if (code != Game.WebGame_Status_Code_Loginning) {
                            toastInThread("??????????????????????????????????????????????????????????????????????????????????????????????????????????????????");
                            alertTextView("??????????????????????????????????????????????????????");
                            notifyUser(SimpleTool.protectTelephoneNum(info.account), "??????????????????");
                            // ??????????????????????????????
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
            toastInThread("???????????????????????????????????????????????????????????????");
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
