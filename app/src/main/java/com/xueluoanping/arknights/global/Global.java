package com.xueluoanping.arknights.global;

import android.content.Context;
import android.util.Log;

import com.xueluoanping.arknights.api.Game;
import com.xueluoanping.arknights.pro.SimpleTool;
import com.xueluoanping.arknights.pro.spTool;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Global {

    private static final String emptyString = "";
    private static final String TAG = Global.class.getSimpleName();

    private static ArrayList<GlobalGameAccount> gamesList = new ArrayList<>();

    private static GlobalGameAccount selectedGame = new GlobalGameAccount();

    public static class GlobalGameAccount implements Serializable {
        public String account = emptyString;
        public int platform = Game.Platform_Unknown;

        public boolean isEmpty() {
            return account.isEmpty();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GlobalGameAccount account1 = (GlobalGameAccount) o;
            return Objects.equals(account, account1.account) &&
                    Objects.equals(platform, account1.platform);
        }

        @Override
        public int hashCode() {
            return Objects.hash(account, platform);
        }

        public boolean isInList(List<GlobalGameAccount> list) {
            for (GlobalGameAccount account : list
            ) {
                if (account.platform == this.platform
                        && account.account.equals(this.account))
                    return true;
            }
            return false;
        }

        // 保证尽可能获得数据
        public Game.GameInfo isInList2(Game.GameInfo[] list) {
            for (Game.GameInfo info : list
            ) {
                if (info.platform == this.platform
                        && info.account.equals(this.account))
                    return info;
            }
            if (list.length > 0)
                return list[0];
            else
                return null;
        }


        @Override
        public String toString() {
            return account + "#" + platform;
        }

        public static GlobalGameAccount restoreFromString(String gString) {
            String[] game = gString.split("#");
            GlobalGameAccount account = new GlobalGameAccount();
            if (game.length == 2) {
                account.account = game[0];
                account.platform = Integer.parseInt(game[1]);
            }
            return account;
        }


        public GlobalGameAccount copy() {
            GlobalGameAccount account = new GlobalGameAccount();
            account.account = this.account;
            account.platform = this.platform;
            return account;
        }
    }

    public static void prepareBaseData(Context context) {
        loadGamesList(context);
        loadSelectedGame(context);
    }

    public static GlobalGameAccount getSelectedGame() {
        return selectedGame;
    }

    // 初始化时
    public static void loadSelectedGame(Context context) {
        Global.selectedGame = GlobalGameAccount.restoreFromString(spTool.getSelectedGame(context));
    }

    // 用于选择改变时
    public static void setSelectedGameAndSave(Context context, GlobalGameAccount selectedGame) {
        // 确保存在
        if (selectedGame.isInList(gamesList))
            Global.selectedGame = selectedGame;
        else if (gamesList.size() > 0) Global.selectedGame = gamesList.get(0).copy();
        else Global.selectedGame = new GlobalGameAccount();
        // 同步数据库
        spTool.saveSelectedGame(context);
    }

    // 初始化时
    public static void loadGamesList(Context context) {
        spTool.setGamesList(context);
    }

    public static ArrayList<GlobalGameAccount> getGamesList() {
        try {
            return (ArrayList<GlobalGameAccount>) SimpleTool.deepCopy(gamesList);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return new ArrayList<>();
    }

    public static ArrayList<String> getGamesStringList() {
        ArrayList<Global.GlobalGameAccount> list = Global.getGamesList();
        ArrayList<String> list2 = new ArrayList<>();
        for (GlobalGameAccount account : list
        ) {
            list2.add(account.toString());
        }
        for (String x : list2
        ) {
            Log.d(TAG, "getGamesStringList: " + SimpleTool.getUUID(x));
        }
        return list2;
    }

    public static void updateGlobalGamesList(Context context, Game.GameInfo[] infoList,boolean needSync) {
        gamesList.clear();
        for (Game.GameInfo info : infoList) {
            GlobalGameAccount account = new GlobalGameAccount();
            account.account = info.account;
            account.platform = info.platform;
            gamesList.add(account);
        }

        if (gamesList.size() > 0) {
            if (selectedGame.isEmpty()) {
                selectedGame = gamesList.get(0).copy();
            } else if (!selectedGame.isInList(gamesList)) {
                selectedGame = gamesList.get(0).copy();
            }
        } else {
            selectedGame = new GlobalGameAccount();
        }

        // 更新数据库
        if(needSync)
        {
            spTool.saveGamesList(context);
            spTool.saveSelectedGame(context);
        }
    }

    public static void updateGlobalGamesList(Context context, List<String> infoList,boolean needSync) {
        ArrayList<Game.GameInfo> list = new ArrayList<>();
        for (String info : infoList) {

            GlobalGameAccount account0_s = GlobalGameAccount.restoreFromString(info);

            if (!account0_s.isEmpty()) {
                Game.GameInfo account0 = new Game.GameInfo();
                account0.account = account0_s.account;
                account0.platform = account0_s.platform;
                list.add(account0);
            }

        }
        updateGlobalGamesList(context, list.toArray(new Game.GameInfo[0]),needSync);
    }
}
