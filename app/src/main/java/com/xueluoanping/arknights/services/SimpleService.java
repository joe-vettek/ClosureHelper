package com.xueluoanping.arknights.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.xueluoanping.arknights.MainActivity;
import com.xueluoanping.arknights.R;
import com.xueluoanping.arknights.api.main.Game;
import com.xueluoanping.arknights.pro.SimpleTool;

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
                            Game.GameInfo[] infos = Game.getGameStatue(getApplicationContext());
                            for (int i = 0; i < infos.length; i++) {
                                Game.GameInfo info = infos[i];
                                if (info == null)
                                    return;
                                if (info.code != Game.WebGame_Status_Code_Running) {
                                    // Log.d(TAG, "run: ?????????????????????");
                                    notifyUser(getApplicationContext(), SimpleTool.protectTelephoneNum(info.account), info.status);
                                    // notificationManager.notify(10, broadcastIntent(getApplicationContext(), SimpleTool.protectTelephoneNum(info.account),info.status).build());
                                }
                            }


                        } catch (Exception e) {
                            Log.d(TAG, "run: ????????????");
                            // notifyUser(getApplicationContext(), "","??????????????????");
                            // e.printStackTrace();
                        }
                    }
                }).start();
            }
        }, 0, 3600 * 1000);
        return START_STICKY;
    }

    public static NotificationCompat.Builder broadcastIntent(Context context, String account, String status) {

        if (status.equals("-"))
            status = "?????????";

        //????????????????????????
        final String CHANNEL_ID = "channel_id_1";
        final String CHANNEL_NAME = "?????????????????????";

        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //??????Android O??????????????????
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            //???????????????IMPORTANCE_NOENE????????????????????????????????????????????????
            //????????????????????????
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);

        Intent launcher = new Intent(context, MainActivity.class);
        // ?????????????????????????????????????????????????????????
        // CATEGORY_LAUNCHER????????????,??????????????????????????????
        launcher.setAction(Intent.ACTION_MAIN);
        launcher.addCategory(Intent.CATEGORY_LAUNCHER);
        launcher.setFlags(
                // Intent.FLAG_ACTIVITY_NEW_TASK
                Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED

        );
        // |PendingIntent.FLAG_IMMUTABLE????????????????????????????????????????????????
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= 31)
            pendingIntent =
                    PendingIntent.getActivity(context, 0, launcher, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        else pendingIntent =
                PendingIntent.getActivity(context, 0, launcher, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);

        //PendingIntent.FLAG_UPDATE_CURREN
        builder
                .setSmallIcon(R.mipmap.npc_007_closure)
                .setContentTitle("?????????????????????")
                .setContentText("??????????????????" + account + "??????" + status + "?????????????????????????????????~")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
        ;
        int notificationId = 10;
        return builder;
    }

    public static void notifyUser(Context context, String account, String x) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(account.hashCode(),
                SimpleService.broadcastIntent(context, account, x).setAutoCancel(true).build());

    }
}
