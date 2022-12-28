package com.xueluoanping.arknights.pages;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xueluoanping.arknights.R;
import com.xueluoanping.arknights.api.Game;
import com.xueluoanping.arknights.custom.GameInfo.InfoAdapter;
import com.xueluoanping.arknights.custom.stage.StageAdapter;
import com.xueluoanping.arknights.custom.stage.StageModel;
import com.xueluoanping.arknights.pro.SimpleTool;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AccountMangerActivty extends AppCompatActivity {
    private Button bt_newAccount;
    private RecyclerView rc_accountList;
    private InfoAdapter ad_accountList;
    private List<Game.GameInfo> infoList = new ArrayList<>();

    private static final int AccountLimit = 5;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountmanager);
        bindComponents();
        setListeners();

        getData();
        loadData();

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

                    infoList = new ArrayList<>(Arrays.asList(Game.getGameStatue(getApplicationContext())));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ad_accountList.setList(infoList);
                            ad_accountList.notifyDataSetChanged();
                        }
                    });

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }


    private void setListeners() {
        bt_newAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(infoList.size()<5)
                {
                    View root = LayoutInflater.from(AccountMangerActivty.this).inflate(R.layout.dg_addaccount, null);
                    DialogInterface dialogInterface = new AlertDialog.Builder(AccountMangerActivty.this)
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
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ad_accountList.setList(infoList);
                                                    // ad_accountList.notifyDataSetChanged();
                                                    dialogInterface.dismiss();
                                                }
                                            });
                                            SimpleTool.toastInThread(AccountMangerActivty.this, "添加成功");

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            SimpleTool.toastInThread(AccountMangerActivty.this,"添加失败");
                                        }


                                    } else
                                        SimpleTool.toastInThread(AccountMangerActivty.this, "账号密码或网络错误");
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
                }
                else {
                    new AlertDialog.Builder(AccountMangerActivty.this)
                            .setIcon(R.mipmap.npc_007_closure)
                            .setTitle("警告")
                            .setMessage("最多创建五个用户")
                            // .setMessage()
                            .show();
                }

            }
        });
    }

    private void bindComponents() {
        bt_newAccount = findViewById(R.id.bt_newAccount);
        rc_accountList = findViewById(R.id.rc_accountList);
    }

    private void loadData() {
        //创建布局管理
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rc_accountList.setLayoutManager(layoutManager);
        //        View view = LayoutInflater.from(this).inflate(R.layout.item_hr_diary, null);
        //创建适配器
        ad_accountList = new InfoAdapter(R.layout.ic_account, infoList);
        rc_accountList.setAdapter(ad_accountList);
    }
}
