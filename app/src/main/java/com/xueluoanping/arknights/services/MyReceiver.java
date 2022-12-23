package com.xueluoanping.arknights.services;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.xueluoanping.arknights.api.Game;
import com.xueluoanping.arknights.global.Global;

import java.util.List;

import static com.xueluoanping.arknights.services.SimpleService.broadcastIntent;

/**
 * Created by xfkang on 2018/5/7.
 */

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "可露希尔的邮件已启用", Toast.LENGTH_SHORT).show();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Global.prepareBaseData(context);
        // if(!isRun(context,"com.xueluoanping.arknights")){
        //     Intent i = new Intent(context, SimpleService.class);
        //     i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //    context. startService(i);
        // }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Game.GameInfo info= Global.getSelectedGame().isInList2(Game.getGameStatue(context));
                    if (info==null)
                        return;
                    if (info.code != Game.WebGame_Status_Code_Running)
                    {
                        // Log.d(TAG, "run: 可露希尔未运行");
                        notificationManager.notify(10, broadcastIntent(context,info.status).build());
                    }
                } catch (Exception e) {
                    // Log.d(TAG, "run: 可露希尔未运行");
                    notificationManager.notify(10, broadcastIntent(context,"网页连接异常").build());
                    e.printStackTrace();
                }
            }
        }).start();
        Log.d("MyReceiver", "hello");

    }

    /**
     * 判断应用是否在运行
     *
     * @param context
     * @return
     */
    public boolean isRun(Context context, String packagename) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        boolean isAppRunning = false;
        //100表示取的最大的任务数，info.topActivity表示当前正在运行的Activity，info.baseActivity表系统后台有此进程在运行
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(packagename) || info.baseActivity.getPackageName().equals(packagename)) {
                isAppRunning = true;
                Log.d("ActivityService isRun()", info.topActivity.getPackageName() + " info.baseActivity.getPackageName()=" + info.baseActivity.getPackageName());
                break;
            }
        }
        Log.d("ActivityService isRun()", "com.ad 程序  ...isAppRunning......" + isAppRunning);
        return isAppRunning;
    }



}


