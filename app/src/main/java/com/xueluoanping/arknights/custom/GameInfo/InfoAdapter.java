package com.xueluoanping.arknights.custom.GameInfo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xueluoanping.arknights.R;
import com.xueluoanping.arknights.SimpleApplication;
import com.xueluoanping.arknights.api.main.Data;
import com.xueluoanping.arknights.api.main.Game;
import com.xueluoanping.arknights.api.resource.dzp;
import com.xueluoanping.arknights.api.tool.ToolTime;
import com.xueluoanping.arknights.pages.CheckActivity;
import com.xueluoanping.arknights.pro.SimpleTool;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class InfoAdapter extends BaseQuickAdapter<Game.GameInfo, BaseViewHolder> {
    private static String TAG = InfoAdapter.class.getSimpleName();

    public InfoAdapter(int layoutResId, @Nullable List<Game.GameInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, Game.GameInfo info) {
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch sw = baseViewHolder.findView(R.id.ic_sw_go);
        Button bt = baseViewHolder.findView(R.id.ic_bt_delete);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();

        // baseViewHolder.setTextColor(R.id.ic_bt_delete,)
        String l1 = Game.getPlatform(info.platform) + "：" + SimpleTool.protectTelephoneNum(info.account) + "\n";
        spannableStringBuilder.append(l1);

        String l2 = "当前状态：";
        SpannableString sTip = new SpannableString(info.status + "\n");
        ForegroundColorSpan colorSpan0 = new ForegroundColorSpan(getContext().getColor(R.color.lightgreen));
        ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(getContext().getColor(R.color.lightskyblue));
        ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(getContext().getColor(R.color.crimson));

        // 通过GameSetting才可以获取知道isPause
        switch (info.code) {
            case Game.WebGame_Status_Code_Running:
                sTip.setSpan(colorSpan0, 0, sTip.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                sw.setChecked(true);
                break;
            case Game.WebGame_Status_Code_Loginning:
                sw.setChecked(true);
                sTip.setSpan(colorSpan1, 0, sTip.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                break;
            case Game.WebGame_Status_Code_ErrorGame:
                sw.setChecked(true);
                sTip.setSpan(colorSpan2, 0, sTip.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                break;
            default:
                if (info.status.equals("-")) {
                    sTip = new SpannableString("未运行\n");
                }
                sw.setChecked(false);
                sTip.setSpan(colorSpan2, 0, sTip.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                break;
        }
        spannableStringBuilder.append(l2).append(sTip);

        baseViewHolder.setText(R.id.ic_tv_accountName, spannableStringBuilder);

        ImageView imageView = baseViewHolder.findView(R.id.ic_iv_icon);
        // Data.AccountData data0_back = new Data.AccountData(Data.getOldDataTable(getContext(), info));

        new Thread(() -> {
            Data.AccountData data0_back = null;
            try {
                data0_back = Data.getBasicInfo(getContext(), info);
            } catch (IOException | JSONException e) {
                // e.printStackTrace();
                Log.d(TAG, "run: getBasicInfo得不到进一步信息");
            }
            if (data0_back != null) {
                dzp.loadImageIntoImageview(data0_back, (Activity) getContext(), imageView);

            }
        }).start();


        sw.setOnCheckedChangeListener((compoundButton, b) -> {
            // 改变状态必然由启动到终止

            new Thread(() -> {
                if (b) {
                    Game.TryLogin((Activity) getContext(), info.account, info.platform);
                    SimpleTool.toastInThread((Activity) getContext(), "尝试登录中");
                    info.status = "尝试登陆中";
                    info.code = Game.WebGame_Status_Code_Loginning;

                    // getData().set(getData().indexOf(info), info);

                    safeRunOnUiThread(() -> {
                        notifyItemChanged(getItemPosition(info));
                    });
                    Timer timer = new Timer();
                    long cacheTime = ToolTime.getTimeShanghai();
                    timer.schedule(new TimerTask() {

                        @Override
                        public void run() {

                            try {
                                Game.GameInfo info1 = info.getEqualNewInstance(Game.getGameStatue(getContext()));

                                int code = info1.code;
                                // if(code==Game.WebGame_Status_Code_ErrorLogin)
                                // {
                                //
                                // }else
                                if (code == Game.WebGame_Status_Code_Running) {
                                    long nowTime = ToolTime.getTimeShanghai();
                                    double duration = (nowTime - cacheTime) / 1000.0d;
                                    Log.d(TAG, "run: " + "登录完成，共耗时" + duration + "s");
                                    SimpleTool.toastInThread((Activity) getContext(), "登录完成，共耗时" + duration + "s");
                                    synchronized (timer) {
                                        timer.cancel();
                                        timer.purge();
                                    }

                                    // setData(baseViewHolder.getLayoutPosition(), info1);
                                } else if (code == Game.WebGame_Status_Code_NeedCheck) {

                                    SimpleTool.toastInThread((Activity) getContext(), "暂时请访问可露希尔网页进行手动确认，也可以通知开发者提供信息帮助完成这一功能");
                                    SimpleTool.toastInThread((Activity) getContext(), "验证完成后请自行刷新。");
                                    if (!info1.challenge.equals(info.challenge)) {
                                        Intent ii = new Intent(getContext(), CheckActivity.class);
                                        ii.putExtra("challenge", info1.challenge);
                                        ii.putExtra("gt", info1.gt);
                                        ii.putExtra("account", info1.account);
                                        ii.putExtra("platform", info1.platform);
                                        getContext().startActivity(ii);
                                        synchronized (timer) {
                                            timer.cancel();
                                        }
                                    }


                                }
                                // (code == Game.WebGame_Status_Code_Loginning)
                                // else {
                                //     setData(baseViewHolder.getLayoutPosition(), info1);
                                // }
                                setData(getItemPosition(info), info1);
                                if (code == Game.WebGame_Status_Code_ErrorLogin || (info.code != info1.code && !info.status.equals(info1.status)))
                                    safeRunOnUiThread(() -> {
                                        notifyItemChanged(getItemPosition(info));
                                        Log.d(TAG, "run: getGameStatue");
                                    });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 5000, 5000);

                }
                // else
                // toastInThread("请访问可露希尔工作室进行检查或者更换网络！");

                else {
                    try {
                        Game.GameSettings gameSettings = Game.getGameSettings(info);
                        gameSettings.isStopped = true;
                        if (Game.updateGameSettings(
                                info, gameSettings)) {
                            SimpleTool.toastInThread((Activity) getContext(), "更新成功！");
                            Game.GameInfo[] infos = Game.getGameStatue(getContext());
                            for (int i = 0; i < infos.length; i++) {
                                if (info.account.equals(infos[i].account)
                                        && info.platform == infos[i].platform) {
                                    getData().set(i, infos[i]);
                                    int finalI = i;
                                    safeRunOnUiThread(() -> {notifyItemChanged(finalI);});
                                    break;
                                }
                            }


                        } else {
                            SimpleTool.toastInThread((Activity) getContext(), "网络有误，请重试！");
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        SimpleTool.toastInThread((Activity) getContext(), "网络有误，请重试！");
                    }
                    // try {
                    //     Game.getGameSettings(info).isStopped = true;
                    // } catch (JSONException | IOException e) {
                    //     e.printStackTrace();
                    // }
                }
            }).start();


        });

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setIcon(R.mipmap.npc_007_closure)
                        .setTitle("警告")
                        .setMessage("你确定要删除这个账户的记录吗")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new Thread(() -> {
                                    if (Game.TryDelete(info.account, info.platform)) {
                                        safeRunOnUiThread(() -> {
                                            getData().remove(getItemPosition(info));
                                            removeAt(getItemPosition(info));
                                            notifyItemChanged(getItemPosition(info));
                                            SimpleTool.toastInThread((Activity) getContext(), "删除成功");

                                            // 因为需要调整逻辑，所以暂时在此处插入重启
                                            ((SimpleApplication) SimpleApplication.getContext()).restart();
                                        });
                                    } else
                                        SimpleTool.toastInThread((Activity) getContext(), "删除失败");
                                }).start();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();

            }
        });

    }

    private void safeRunOnUiThread(Runnable runnable) {
        if (!((Activity) getContext()).isDestroyed()) {
            ((Activity) getContext()).runOnUiThread(runnable);
        } else {
            Log.e(TAG, "safeRunOnUiThread: Try run ui Thread on a nonexistent Activity.\n" + runnable);
        }
    }
}
