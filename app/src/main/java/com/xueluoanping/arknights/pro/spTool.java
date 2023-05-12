package com.xueluoanping.arknights.pro;

import android.content.Context;
import android.content.SharedPreferences;

import com.xueluoanping.arknights.R;
import com.xueluoanping.arknights.SimpleApplication;
import com.xueluoanping.arknights.global.Global;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

public class spTool {

    private static final String defaultValue_String = "";
    private static final boolean defaultValue_Boolean = false;
    private static final String Pos_UserInfo = "User";
    private static final String Pos_AppSetting = "Setting";
    private static final String Pos_App = "App";
    private static final String Key_UserName = "name";
    private static final String Key_Password = "password";
    private static final String Key_Token = "token";
    private static final String Key_IsAdmin = "admin";
    private static final String Key_games = "games";
    private static final String Key_selectedGame = "selectedGame";
    private static final String Key_autoWarehouseIdentification = "autoWarehouseIdentification";
    private static final String Key_autoLogin = "autoLogin";
    private static final String Key_startMusic = "startMusic";
    private static final String Key_Theme = "styleName";
    private static final String Key_LineSelect = "lineSelect";
    private static final String Key_ResourceSelect = "resourceSelect";
    private static final String Key_MusicSelect = "musicSelect";
    private static final String Key_AppVersion = "version";
    private static final String Key_NewOperator = "newOperator";

    public static String get(String location, String key, String defaultValue) {
        SharedPreferences sp = SimpleApplication.getContext().getSharedPreferences(location, Context.MODE_PRIVATE);
        return sp.getString(key, defaultValue);
    }

    public static boolean get(String location, String key, boolean defaultValue) {
        SharedPreferences sp = SimpleApplication.getContext().getSharedPreferences(location, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }

    public static int get(String location, String key, int defaultValue) {
        SharedPreferences sp = SimpleApplication.getContext().getSharedPreferences(location, Context.MODE_PRIVATE);
        return sp.getInt(key, defaultValue);
    }

    public static void save(String location, String key, String value) {
        SharedPreferences sp = SimpleApplication.getContext().getSharedPreferences(location, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(key, value);
        ed.apply();
    }

    public static void save(String location, String key, boolean value) {
        SharedPreferences sp = SimpleApplication.getContext().getSharedPreferences(location, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(key, value);
        ed.apply();
    }

    public static void save(String location, String key, int value) {
        SharedPreferences sp = SimpleApplication.getContext().getSharedPreferences(location, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt(key, value);
        ed.apply();
    }

    public static void saveUserName(String name) {
        save(Pos_UserInfo, Key_UserName, name);
    }

    public static void savePassword(String password) {
        save(Pos_UserInfo, Key_Password, password);
    }

    public static String getUserName(Context context) {
        return get(Pos_UserInfo, Key_UserName, defaultValue_String);
    }

    public static String getPassword(Context context) {
        return get(Pos_UserInfo, Key_Password, defaultValue_String);
    }


    public static void saveToken(String token) {
        save(Pos_UserInfo, Key_Token, token);
    }

    public static void saveIsAdmin(boolean isAdmin) {
        save(Pos_UserInfo, Key_IsAdmin, isAdmin);
    }

    public static String getToken(Context context) {
        return get(Pos_UserInfo, Key_Token, defaultValue_String);
    }

    public static Boolean getIsAdmin(Context context) {
        return get(Pos_UserInfo, Key_IsAdmin, defaultValue_Boolean);
    }

    public static String getSelectedGame(Context context) {
        return get(Pos_UserInfo, Key_selectedGame, defaultValue_String);
    }

    public static void saveSelectedGame(Context context) {
        save(Pos_UserInfo, Key_selectedGame, Global.getSelectedGame().toString());
    }

    public static void setGamesList(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Pos_UserInfo, Context.MODE_PRIVATE);
        Global.updateGlobalGamesList(SimpleApplication.getContext(), new ArrayList<>(new TreeSet<>(sp.getStringSet(Key_games, new TreeSet<>(new ArrayList<String>())))), false);
    }

    public static void saveGamesList(Context context) {
        ArrayList<String> list = Global.getGamesStringList();
        SharedPreferences sp = context.getSharedPreferences(Pos_UserInfo, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putStringSet(Key_games, new TreeSet<>(list));
        ed.apply();
    }

    public static boolean getAutoWarehouseIdentification() {
        return get(Pos_AppSetting, Key_autoWarehouseIdentification, defaultValue_Boolean);
    }


    public static void saveAutoWarehouseIdentification(Boolean token) {
        save(Pos_AppSetting, Key_autoWarehouseIdentification, token);
    }

    public static boolean getQuickLogin() {
        return get(Pos_AppSetting, Key_autoLogin, defaultValue_Boolean);
    }


    public static void saveQuickLogin(Boolean token) {
        save(Pos_AppSetting, Key_autoLogin, token);
    }

    public static boolean getStartMusic() {
        return get(Pos_AppSetting, Key_startMusic, defaultValue_Boolean);
    }


    public static void saveStartMusic(Boolean token) {
        save(Pos_AppSetting, Key_startMusic, token);
    }

    public static int getLineSelect() {
        return get(Pos_AppSetting, Key_LineSelect, 0);
    }

    public static void setLineSelect(int token) {
        save(Pos_AppSetting, Key_LineSelect, token);
    }

    public static int getResourceSelect() {
        return get(Pos_AppSetting, Key_ResourceSelect, 0);
    }

    public static void setResourceSelect(int token) {
        save(Pos_AppSetting, Key_ResourceSelect, token);
    }

    public static int getMusicSelect() {
        return get(Pos_AppSetting, Key_MusicSelect, 0);
    }

    public static void setMusicSelect(int token) {
        save(Pos_AppSetting, Key_MusicSelect, token);
    }

    public static int getTheme() {
        return get(Pos_AppSetting, Key_Theme, R.style.AppTheme_NoActionBar_AutumnDuck);
    }

    public static void setTheme(int token) {
        save(Pos_AppSetting, Key_Theme, token);
    }

    public static String getVersion() {
        return get(Pos_App, Key_AppVersion, defaultValue_String);
    }

    public static void setVersion(String token) {
        save(Pos_App, Key_AppVersion, token);
    }

    public static void addNewOperatorTime(String value) {
        SharedPreferences sp = SimpleApplication.getContext().getSharedPreferences(Pos_UserInfo, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        Set<String> set = new LinkedHashSet<>(sp.getStringSet(Key_NewOperator, new HashSet<>()));
        set.add(value);
        ed.putStringSet(Key_NewOperator,set);
        ed.apply();
    }

    public static void cleanNewOperatorTimeSet() {
        SharedPreferences sp = SimpleApplication.getContext().getSharedPreferences(Pos_UserInfo, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putStringSet(Key_NewOperator,new HashSet<>());
        ed.apply();
    }

    public static Set<String> getNewOperatorTimeSet() {
        SharedPreferences sp = SimpleApplication.getContext().getSharedPreferences(Pos_UserInfo, Context.MODE_PRIVATE);
        return sp.getStringSet(Key_NewOperator, new HashSet<>());
    }
}
