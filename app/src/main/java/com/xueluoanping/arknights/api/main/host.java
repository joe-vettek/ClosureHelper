package com.xueluoanping.arknights.api.main;

import android.util.Log;

import com.xueluoanping.arknights.api.BetterEntry;
import com.xueluoanping.arknights.api.tool.ToolTime;
import com.xueluoanping.arknights.pro.HttpConnectionUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class host {
    public static final String baseApi = "https://devapi.arknights.host";
    private static final String TAG = host.class.getSimpleName();
    private static String sign = "/Nodes";
    private static final int TIMEMOUT_MAX = 999999;
    @SuppressWarnings("unchecked")
    public final static BetterEntry<String, Integer>[] baseHosts = (BetterEntry<String, Integer>[]) new BetterEntry<?, ?>[]{
            new BetterEntry<>(baseApi, TIMEMOUT_MAX),
            new BetterEntry<>("https://api-a.arknights.host", TIMEMOUT_MAX),
            new BetterEntry<>("https://api-b.arknights.host", TIMEMOUT_MAX),
            new BetterEntry<>("https://api-c.arknights.host", TIMEMOUT_MAX)};

    private static String quickestHost;


    public static void setQuickestHost(String quickestHost) {
        host.quickestHost = quickestHost;
    }


    public static void trySetQuickestAsyn() {
        new Thread(() -> trySetQuickestHost(5000)).start();
    }

    public static BetterEntry<Integer,Integer> trySetQuickestHost(int timeout) {
        quickestHost = null;
        // int result = -1;
        List<Thread> lists = new ArrayList<>();

        // 依次请求服务器并计算ping时间
        List<BetterEntry<String, Integer>> list2 = new ArrayList<>(Arrays.asList(baseHosts));
        list2.forEach(betterEntry -> {
            Thread d = new Thread(() -> {
                // 初始化数值
                betterEntry.setValue(TIMEMOUT_MAX);
                try {
                    long c =  ToolTime.getTimeShanghai();
                    HttpConnectionUtil.DownLoadTextPages(betterEntry.getKey() + sign, null, true);
                    long n =  ToolTime.getTimeShanghai();
                    int cn = (int) (n - c);
                    if (cn > 0)
                        betterEntry.setValue(cn);
                } catch (IOException e) {
                    Log.d(TAG, "trySetQuickestHost: 连接超时" + betterEntry.getKey());
                }

            });
            lists.add(d);
            d.start();
        });

        // 等待响应
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        lists.forEach(thread -> {
            try {
                thread.interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // 选取最佳值
        int choose = 0;
        int time = -1;
        for (int i = 1; i < baseHosts.length; i++) {
            if (baseHosts[i].getValue() < baseHosts[choose].getValue())
                choose = i;
        }
        //判断结果是否符合条件
        if (baseHosts[choose].getValue() < 10000) {
            quickestHost = baseHosts[choose].getKey();
            time = baseHosts[choose].getValue();
            // result = baseHosts[choose].getValue();
        }

        Log.d(TAG, "trySetQuickestHost: " + quickestHost + "," + time);
        //返回结果
        // if (quickestHost != null) ;
        return new BetterEntry<>(time,choose);
    }

    public static String getQuickestHost() {
        if (quickestHost == null) return baseApi;
        else return quickestHost;
    }
}
