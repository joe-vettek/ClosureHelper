package com.xueluoanping.arknights.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.xueluoanping.arknights.MainActivity;
import com.xueluoanping.arknights.R;
import com.xueluoanping.arknights.api.Game;
import com.xueluoanping.arknights.global.Global;

import java.util.Timer;
import java.util.TimerTask;

public class SimpleService extends Service {
    private static final String TAG = SimpleService.class.getSimpleName();
    public static boolean isRunning = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
        Log.d(TAG, "onCreate: ");


    }

    @Override
    public void onDestroy() {
        isRunning = false;
        Log.d(TAG, "onDestroy: ");

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Game.GameInfo info=Global.getSelectedGame().isInList2(Game.getGameStatue(getApplicationContext()));
                            if (info==null)
                                return;
                            if (info.code != Game.WebGame_Status_Code_Running) {
                                Log.d(TAG, "run: 可露希尔未运行");
                                notificationManager.notify(10, broadcastIntent(getApplicationContext(), info.status).build());
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "run: 可露希尔未运行");
                            notificationManager.notify(10, broadcastIntent(getApplicationContext(), "网页连接异常").build());
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }, 0, 3600*1000);
        return START_STICKY;
    }

    public static NotificationCompat.Builder broadcastIntent(Context context, String status) {

        if(status.equals("-"))
            status="未运行";

        //初始化通知管理器
        final String CHANNEL_ID = "channel_id_1";
        final String CHANNEL_NAME = "可露希尔的邮件";

        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //只在Android O之上需要渠道
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            //如果这里用IMPORTANCE_NOENE就需要在系统的设置里面开启渠道，
            //通知才能正常弹出
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);

        Intent launcher = new Intent(context, MainActivity.class);
        // 这两行设置是否以桌面应用启动方式去启动
        // CATEGORY_LAUNCHER有了这个,程序就会出现在桌面上
        launcher.setAction(Intent.ACTION_MAIN);
        launcher.addCategory(Intent.CATEGORY_LAUNCHER);
        launcher.setFlags(
                // Intent.FLAG_ACTIVITY_NEW_TASK
                Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED

        );
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, 0, launcher, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        //PendingIntent.FLAG_UPDATE_CURREN
        builder
                .setSmallIcon(R.mipmap.npc_007_closure)
                .setContentTitle("可露希尔小助手")
                .setContentText("您的可露希尔账户似乎" + status + "呢，请点击查看具体信息~")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent);
        int notificationId = 10;
        return builder;
    }

    public static void notifyUser(Context context,String x) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(10,
                SimpleService.broadcastIntent(context, x).setAutoCancel(true).build());
    }
}
