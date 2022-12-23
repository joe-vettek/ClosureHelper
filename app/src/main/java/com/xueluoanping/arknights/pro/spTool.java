package com.xueluoanping.arknights.pro;

import android.content.Context;
import android.content.SharedPreferences;

import com.xueluoanping.arknights.global.Global;

import java.util.ArrayList;
import java.util.Objects;
import java.util.TreeSet;

public class spTool {
    private static final String defaultValue_String = "";
    private static final boolean defaultValue_Boolean = false;
    private static final String Pos_UserInfo = "User";
    private static final String Key_UserName = "name";
    private static final String Key_Password = "password";
    private static final String Key_Token = "token";
    private static final String Key_IsAdmin = "admin";
    private static final String Key_games = "games";
    private static final String Key_selectedGame = "selectedGame";
    public static String get(Context context, String location, String key, String defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(location, Context.MODE_PRIVATE);
        return sp.getString(key, defaultValue);
    }

    public static boolean get(Context context, String location, String key, boolean defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(location, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }

    public static void save(Context context, String location, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(location, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(key, value);
        ed.apply();
    }

    public static void save(Context context, String location, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(location, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(key, value);
        ed.apply();
    }

    public static void saveUserName(Context context, String name) {
        save(context, Pos_UserInfo, Key_UserName, name);
    }

    public static void savePassword(Context context, String password) {
        save(context, Pos_UserInfo, Key_Password, password);
    }

    public static String getUserName(Context context) {
        return get(context, Pos_UserInfo, Key_UserName, defaultValue_String);
    }

    public static String getPassword(Context context) {
        return get(context, Pos_UserInfo, Key_Password, defaultValue_String);
    }


    public static void saveToken(Context context, String token) {
        save(context, Pos_UserInfo, Key_Token, token);
    }

    public static void saveIsAdmin(Context context, boolean isAdmin) {
        save(context, Pos_UserInfo, Key_IsAdmin, isAdmin);
    }

    public static String getToken(Context context) {
        return get(context, Pos_UserInfo, Key_Token, defaultValue_String);
    }

    public static Boolean getIsAdmin(Context context) {
        return get(context, Pos_UserInfo, Key_IsAdmin, defaultValue_Boolean);
    }

    public static String getSelectedGame(Context context) {
        return get(context, Pos_UserInfo, Key_selectedGame, defaultValue_String);
    }

    public static void saveSelectedGame(Context context) {
        save(context, Pos_UserInfo, Key_selectedGame, Global.getSelectedGame().toString());
    }

    public static void setGamesList(Context context){
        SharedPreferences sp = context.getSharedPreferences(Pos_UserInfo, Context.MODE_PRIVATE);
        Global.updateGlobalGamesList(context,new ArrayList<>( new TreeSet<>(sp.getStringSet(Key_games, new TreeSet<>(new ArrayList<String>())))),false);
    }
    public static void saveGamesList(Context context){
       ArrayList<String> list = Global.getGamesStringList();
        SharedPreferences sp = context.getSharedPreferences(Pos_UserInfo, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putStringSet(Key_games, new TreeSet<>(list));
        ed.apply();
    }

}
