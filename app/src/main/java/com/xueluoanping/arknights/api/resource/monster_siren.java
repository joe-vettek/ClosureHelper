package com.xueluoanping.arknights.api.resource;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xueluoanping.arknights.api.BetterEntry;
import com.xueluoanping.arknights.api.main.Data;
import com.xueluoanping.arknights.api.main.Game;
import com.xueluoanping.arknights.api.main.auth;
import com.xueluoanping.arknights.api.main.host;
import com.xueluoanping.arknights.api.tool.ToolFile;
import com.xueluoanping.arknights.pro.HttpConnectionUtil;
import com.xueluoanping.arknights.pro.SimpleTool;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class monster_siren {
    private static final String TAG = monster_siren.class.getSimpleName();

    private static String getAlbumDetailUrl(int id) {
        return String.format(albumDetailUrl, id);
    }

    private static String getSongUrl(int id) {
        return String.format(songDetailUrl, id);
    }

    private static final String base = "https://monster-siren.hypergryph.com/api/";
    private static final String albumUrl = base + "albums";
    private static final String albumDetailUrl = base + "/album/%s/detail";
    private static final String songDetailUrl = base + "song/%s";

    public static BetterEntry<String, List<Integer>> getAllAlbum() throws IOException, NullPointerException {
        String urlStr = albumUrl;
        String GameJson = HttpConnectionUtil.DownLoadTextPages(urlStr, null, true);
        Log.d(TAG, "getAllAlbum: " + GameJson);
        JsonArray c = SimpleTool.getGsonObject(GameJson).getAsJsonArray("data");
        List<Integer> list = new ArrayList<>();
        if (c != null)
            c.forEach(jsonElement -> {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                String key = "cid";
                if (jsonObject.has(key))
                    list.add(jsonObject.get(key).getAsInt());
            });
        return new BetterEntry<>("塞壬唱片", list);
    }

    public static BetterEntry<String, List<Integer>> getAllSongInTheAlbum(int id) throws IOException, NullPointerException {
        String urlStr = getAlbumDetailUrl(id);
        String GameJson = HttpConnectionUtil.DownLoadTextPages(urlStr, null, true);
        Log.d(TAG, "getAllSongInTheAlbum: " + GameJson);
        JsonObject c = SimpleTool.getGsonObject(GameJson).getAsJsonObject("data");
        List<Integer> list = new ArrayList<>();
        String name = "";
        if (c != null) {
            JsonArray cc = c.getAsJsonArray("songs");
            if (cc != null)
                cc.forEach(jsonElement -> {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    String key = "cid";
                    if (jsonObject.has(key))
                        list.add(jsonObject.get(key).getAsInt());
                });
            name = c.get("name").getAsString();
        }
        return new BetterEntry<>(name, list);
    }

    public static BetterEntry<String, String> getSongSourceUrl(int id) throws IOException, NullPointerException {
        String urlStr = getSongUrl(id);
        Log.d(TAG, "getSongSourceUrl: " + urlStr);
        String GameJson = HttpConnectionUtil.DownLoadTextPages(urlStr, null, true);
        Log.d(TAG, "getSongSourceUrl: " + GameJson);
        JsonObject c = SimpleTool.getGsonObject(GameJson).getAsJsonObject("data");
        List<Integer> list = new ArrayList<>();
        if (c != null) {
            String cc = c.get("sourceUrl").getAsString();
            String name = c.get("name").getAsString();
            return new BetterEntry<>(name, cc);
        }
        return null;
    }

    public static boolean downloadFile(int id) {
        FileOutputStream out = null;
        InputStream inputStream = null;
        byte[] buffer = new byte[1024];
        int lenght = 0;
        boolean result = false;
        try {
            // 由于捕捉异常了，所以不处理空指针
            String url = getSongSourceUrl(id).getValue();
            inputStream = HttpConnectionUtil.DownLoadFile(url, null, true);
            File file = new File(getMusicFilePos(id));
            if (!file.exists()) {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }
            out = new FileOutputStream(file);
            while ((lenght = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, lenght);
            }
            out.flush();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
                if (out != null) out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return result;
    }

    public static String getMusicFilePos(int id) {
        return ToolFile.getBaseUrl() + "music/" + id + ".mp3";
    }

    public static BetterEntry<String, String> getRandomSong() throws IOException, NullPointerException {
        List<Integer> l1 = getAllAlbum().getValue();
        Random r = new Random();
        int albumId = l1.get(r.nextInt(l1.size()));
        Log.d(TAG, "getRandomSong: albumId：" + albumId);

        BetterEntry<String, List<Integer>> entry2 = getAllSongInTheAlbum(albumId);
        List<Integer> l2 = entry2.getValue();
        int songId = l2.get(r.nextInt(l2.size()));
        Log.d(TAG, "getRandomSong: songId：" + songId);

        BetterEntry<String, String> entry3 = getSongSourceUrl(songId);
        assert entry3 != null;
        String source = entry3.getValue();
        Log.d(TAG, "getRandomSong: " + source);
        return new BetterEntry<>(entry2.getKey() + " - " + entry3.getKey(), source);
    }
}
