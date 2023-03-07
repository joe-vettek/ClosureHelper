package com.xueluoanping.arknights.pages.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xueluoanping.arknights.MainActivity;
import com.xueluoanping.arknights.R;
import com.xueluoanping.arknights.SimpleApplication;
import com.xueluoanping.arknights.api.BetterEntry;
import com.xueluoanping.arknights.api.main.Game;
import com.xueluoanping.arknights.api.main.host;
import com.xueluoanping.arknights.base.BaseFragment;
import com.xueluoanping.arknights.custom.GameInfo.InfoAdapter;
import com.xueluoanping.arknights.internal.SplashView;
import com.xueluoanping.arknights.pro.SimpleTool;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AccountMangerFragment extends BaseFragment implements FragmentWithName {
    private Button bt_newAccount;
    private RecyclerView rc_accountList;
    private InfoAdapter ad_accountList;
    private SplashView ac_splash;
    private TextView tv_errorNet;
    private List<Game.GameInfo> infoList = new ArrayList<>();

    private boolean needRefresh = false;
    private boolean isMaintaining=false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_accountmanager, container, false);
        bt_newAccount = root.findViewById(R.id.bt_newAccount);
        rc_accountList = root.findViewById(R.id.rc_accountList);
        ac_splash = root.findViewById(R.id.ac_splash);
        tv_errorNet = root.findViewById(R.id.tv_errorNet);

        return root;
    }


    @Override
    public void onStart() {
        super.onStart();
        // bindComponents();
        setListeners();
        getData();
        loadData();

    }

    private void showAll() {
        ac_splash.finshSplash();
        bt_newAccount.setVisibility(View.VISIBLE);
        rc_accountList.setVisibility(View.VISIBLE);

    }

    private void hideAll() {
        bt_newAccount.setVisibility(View.INVISIBLE);
        rc_accountList.setVisibility(View.INVISIBLE);
    }

    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 调用Arrays.asList()生产的List的add、remove方法时报异常，这是由Arrays.asList() 返回的是Arrays的内部类ArrayList，
                    // 而不是java.util.ArrayList。
                    // Arrays的内部类ArrayList和java.util.ArrayList都是继承AbstractList，
                    // remove、add等方法AbstractList中是默认throw UnsupportedOperationException而且不作任何操作。
                    // java.util.ArrayList重新了这些方法而Arrays的内部类ArrayList没有重新，所以会抛出异常。

                    infoList = new ArrayList<>(Arrays.asList(Game.getGameStatue(SimpleApplication.getContext())));
                    safeRunOnUiThread(() -> {
                        ad_accountList.setList(infoList);
                        ad_accountList.notifyDataSetChanged();
                        showAll();
                        if (needRefresh) {
                            if (getActivity() != null && !getActivity().isDestroyed() && getActivity() instanceof MainActivity) {
                                ((MainActivity) getActivity()).loadAllGames();
                            }
                            needRefresh = false;
                        }
                    });

                } catch (IOException | JSONException e) {
                    safeRunOnUiThread(() -> {
                        showNetWorkError();
                    });
                    e.printStackTrace();
                }

            }
        }).start();
    }


    public void showNetWorkError() {
        tv_errorNet.setVisibility(View.VISIBLE);
        ac_splash.pauseSplash();
        needRefresh = true;
        SimpleTool.toastInThread(getActivity(), "网络故障，请重试");
    }

    public void hideNetWorkError() {
        tv_errorNet.setVisibility(View.INVISIBLE);
        ac_splash.continueSplash();
        SimpleTool.toastInThread(getActivity(), "正在重新选择最优网络通道，请耐心等待");
    }

    private void setListeners() {
        tv_errorNet.setMovementMethod(LinkMovementMethod.getInstance());
        tv_errorNet.setOnClickListener(v -> {
            hideNetWorkError();
            // 等待设置好之后再加载数据
            new Thread(() -> {
                BetterEntry<Integer,Integer> t = host.trySetQuickestHost(3000);
                if (t.getKey() > 0) {
                    SimpleTool.toastInThread(getActivity(), String.format("连接成功，当前最小延迟为%sms，使用线路-%s",t.getKey(),getResources().getStringArray(R.array.line_type)[t.getValue()]));
                    getData();
                } else safeRunOnUiThread(this::showNetWorkError);
            }).start();
        });
        bt_newAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (infoList.size() < 5) {
                    View root = LayoutInflater.from(getActivity()).inflate(R.layout.dg_addaccount, null);
                    DialogInterface dialogInterface = new AlertDialog.Builder(getActivity())
                            .setView(root)
                            .setIcon(R.mipmap.npc_007_closure)
                            .setTitle("添加一个账户")
                            // .setMessage()
                            .show();
                    ((Button) root.findViewById(R.id.dg_bt_submit)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            int platform = ((Spinner) root.findViewById(R.id.dg_sp_platform)).getSelectedItemPosition();
                            if (platform == Game.Platform_IOS) platform = Game.Platform_Android;
                            String account = ((EditText) root.findViewById(R.id.dg_et_user)).getText().toString();
                            String password = ((EditText) root.findViewById(R.id.dg_et_password)).getText().toString();
                            int finalPlatform = platform;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    if (Game.TryAdd(account, password, finalPlatform)) {
                                        Game.GameInfo info = new Game.GameInfo();
                                        info.account = account;
                                        info.platform = finalPlatform;
                                        info.code = Game.WebGame_Status_Code_NeedLogin;
                                        info.status = "游戏未启动";
                                        try {
                                            infoList.add(info);
                                            safeRunOnUiThread(() -> {
                                                ad_accountList.setList(infoList);
                                                // ad_accountList.notifyDataSetChanged();
                                                dialogInterface.dismiss();

                                                // 因为需要调整逻辑，所以暂时在此处插入重启
                                                ((SimpleApplication) SimpleApplication.getContext()).restart();
                                            });

                                            SimpleTool.toastInThread(getActivity(), "添加成功");

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            SimpleTool.toastInThread(getActivity(), "添加失败");
                                        }


                                    } else
                                        SimpleTool.toastInThread(getActivity(), "账号密码或网络错误");
                                }
                            }).start();
                        }
                    });
                    ((Button) root.findViewById(R.id.dg_bt_cancel)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogInterface.dismiss();
                        }
                    });
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setIcon(R.mipmap.npc_007_closure)
                            .setTitle("警告")
                            .setMessage("最多创建五个用户")
                            // .setMessage()
                            .show();
                }

            }
        });
    }

    // private void bindComponents() {
    //     bt_newAccount = getActivity().findViewById(R.id.bt_newAccount);
    //     rc_accountList = getActivity().findViewById(R.id.rc_accountList);
    // }

    private void loadData() {
        //创建布局管理
        LinearLayoutManager layoutManager = new LinearLayoutManager(SimpleApplication.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rc_accountList.setLayoutManager(layoutManager);
        //        View view = LayoutInflater.from(this).inflate(R.layout.item_hr_diary, null);
        //创建适配器
        ad_accountList = new InfoAdapter(R.layout.ic_account, infoList);
        rc_accountList.setAdapter(ad_accountList);


    }

    @Override
    public String getName() {
        return "账户面板";
    }

    @Override
    public void scrollTo(int x, int y) {
        if (rc_accountList != null)
            rc_accountList.scrollTo(x, y);
    }
}
