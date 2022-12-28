package com.xueluoanping.arknights.custom.GameInfo;

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
import com.xueluoanping.arknights.MainActivity;
import com.xueluoanping.arknights.R;
import com.xueluoanping.arknights.api.Data;
import com.xueluoanping.arknights.api.Game;
import com.xueluoanping.arknights.custom.Item.ItemModel;
import com.xueluoanping.arknights.global.Global;
import com.xueluoanping.arknights.pages.AccountMangerActivty;
import com.xueluoanping.arknights.pages.WebActivity;
import com.xueluoanping.arknights.pro.SimpleTool;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;

import java.io.IOException;
import java.util.Arrays;
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
        Switch sw = baseViewHolder.itemView.findViewById(R.id.ic_sw_go);
        Button bt = baseViewHolder.itemView.findViewById(R.id.ic_bt_delete);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();

        String l1 = Game.getPlatform(info.platform) + "：" + SimpleTool.protectTelephoneNum(info.account) + "\n";
        spannableStringBuilder.append(l1);

        String l2 = "当前状态：";
        SpannableString sTip = new SpannableString(info.status + "\n");
        ForegroundColorSpan colorSpan0 = new ForegroundColorSpan(getContext().getColor(R.color.lightgreen));
        ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(getContext().getColor(R.color.lightskyblue));
        ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(getContext().getColor(R.color.crimson));
        switch (info.code) {
            case Game.WebGame_Status_Code_Running:
                sTip.setSpan(colorSpan0, 0, sTip.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                sw.setChecked(true);
                break;
            case Game.WebGame_Status_Code_Loginning:
                sw.setChecked(true);
                sTip.setSpan(colorSpan1, 0, sTip.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                break;
            default:
                if (info.status.equals("-"))
                    sTip = new SpannableString("未运行\n");
                sTip.setSpan(colorSpan2, 0, sTip.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                break;
        }
        spannableStringBuilder.append(l2).append(sTip);

        baseViewHolder.setText(R.id.ic_tv_accountName, spannableStringBuilder);

        ImageView imageView = baseViewHolder.itemView.findViewById(R.id.ic_iv_icon);
        try {
            Data.AccountData data0_back = new Data.AccountData(Data.getOldDataTable(getContext(), info));
            String secretarySkinIdDec = data0_back.secretarySkinId.replaceAll("[@#]", "_");
            String PicUrl = "https://ak.dzp.me/dst/avatar/ASSISTANT/" + secretarySkinIdDec + ".webp";
            final String PicUrl2 = PicUrl = "https://ak.dzp.me/dst/avatar/ASSISTANT/" + data0_back.secretary + ".webp";
            RequestOptions options = RequestOptions.bitmapTransform(new CircleCrop());
            Glide.with(getContext())
                    .load(PicUrl)
                    .apply(options)
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @androidx.annotation.Nullable Transition<? super Drawable> transition) {
                            if (resource.getBounds().width() > 5)
                                imageView.setImageDrawable(resource);
                            else Glide.with(getContext())
                                    .load(PicUrl2)
                                    .apply(options)
                                    .into(imageView);
                        }
                    });
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // 改变状态必然由启动到终止

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (b) {
                            Game.TryLogin(getContext(), info.account, info.platform);
                            SimpleTool.toastInThread((Activity) getContext(), "尝试登录中");
                            info.status = "尝试登陆中";
                            info.code = Game.WebGame_Status_Code_Loginning;
                            getData().set(baseViewHolder.getLayoutPosition(), info);
                            ((Activity) getContext()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    notifyItemChanged(baseViewHolder.getLayoutPosition());
                                }
                            });
                            Timer timer = new Timer();
                            long cacheTime = System.currentTimeMillis();
                            timer.schedule(new TimerTask() {

                                @Override
                                public void run() {

                                    try {
                                        Game.GameInfo[] infos = Game.getGameStatue(getContext());
                                        for (int i = 0; i < infos.length; i++) {
                                            if (info.account.equals(infos[i].account)
                                                    && info.platform == infos[i].platform) {
                                                int code = infos[i].code;
                                                if (code == Game.WebGame_Status_Code_Running) {
                                                    long nowTime = System.currentTimeMillis();
                                                    double duration = (nowTime - cacheTime) / 1000.0d;
                                                    Log.d(TAG, "run: " + "登录完成，共耗时" + duration + "s");
                                                    SimpleTool.toastInThread((Activity) getContext(), "登录完成，共耗时" + duration + "s");
                                                    synchronized (timer) {
                                                        timer.cancel();
                                                        timer.purge();
                                                    }

                                                    getData().set(baseViewHolder.getLayoutPosition(), infos[i]);
                                                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            notifyItemChanged(baseViewHolder.getLayoutPosition());
                                                        }
                                                    });
                                                }
                                                else if (code != Game.WebGame_Status_Code_Loginning)
                                                {
                                                    synchronized (timer) {
                                                        SimpleTool.toastInThread((Activity) getContext(), "正在访问可露希尔网页进行手动确认");
                                                        // notifyUser("需要手动登录");
                                                        // timer.cancel();
                                                        // timer.purge();
                                                        // 之后回到正常登录请求和刷新流程
                                                        Intent ii = new Intent(getContext(), WebActivity.class);
                                                        getContext().startActivity(ii);
                                                    }
                                                    getData().set(baseViewHolder.getLayoutPosition(), infos[i]);
                                                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            notifyItemChanged(baseViewHolder.getLayoutPosition());
                                                        }
                                                    });
                                                }
                                                ((Activity) getContext()).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        notifyItemChanged(baseViewHolder.getLayoutPosition());
                                                    }
                                                });
                                                break;
                                            }
                                        }


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, 30000, 1000*180);

                        }
                        // else
                        // toastInThread("请访问可露希尔工作室进行检查或者更换网络！");

                        else {
                            try {
                                Game.GameSettings gameSettings = Game.getGameSettings(info);
                                gameSettings.isStopped = true;
                                if (Game.updateGameSettings(
                                        Global.getSelectedGame().getSimpleGameInfo(), gameSettings)) {
                                    SimpleTool.toastInThread((Activity) getContext(), "更新成功！");
                                    Game.GameInfo[] infos = Game.getGameStatue(getContext());
                                    for (int i = 0; i < infos.length; i++) {
                                        if (info.account.equals(infos[i].account)
                                                && info.platform == infos[i].platform) {
                                            getData().set(baseViewHolder.getLayoutPosition(), infos[i]);
                                            ((Activity) getContext()).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    notifyItemChanged(baseViewHolder.getLayoutPosition());
                                                }
                                            });
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
                    }
                }).start();


            }
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
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (Game.TryDelete(info.account, info.platform)) {
                                            ((Activity) getContext()).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    getData().remove(baseViewHolder.getLayoutPosition());
                                                    removeAt(baseViewHolder.getLayoutPosition());
                                                    notifyItemChanged(baseViewHolder.getLayoutPosition());
                                                    SimpleTool.toastInThread((Activity) getContext(), "删除成功");
                                                }
                                            });
                                        } else
                                            SimpleTool.toastInThread((Activity) getContext(), "删除失败");
                                    }
                                }).start();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();

            }
        });

    }
}
