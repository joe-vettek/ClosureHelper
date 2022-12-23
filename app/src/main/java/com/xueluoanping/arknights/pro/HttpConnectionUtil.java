package com.xueluoanping.arknights.pro;

import android.content.Context;
import android.graphics.Bitmap;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpConnectionUtil {
    public static final String empty = "";
    public static final String emptyJsonObject = "{}";
    public static JSONObject emptyJsonObject1 = new JSONObject();
    // public static final JSONArray emptyJsonArray = new JSONArray("[]");

    public static boolean TryUrl(String urlStr) throws IOException {
        try {
            DownLoadTextPages(urlStr, null);
        } catch (UnknownHostException une){
            return false;
        }
        return true;
    }

    public static String DownLoadTextPages(String urlStr, Map<String, String> extraRequestProperty) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request.Builder builder = new Request.Builder()
                .url(urlStr);
        if (!(extraRequestProperty == null) && !extraRequestProperty.isEmpty()) {
            for (Map.Entry<String, String> entry : extraRequestProperty.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        Request request = builder.build();
        Response response = client.newCall(request).execute();

        assert response.body() != null;
        return response.body().string();
    }


    // post实例
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    public static String post(String url, String json, Map<String, String> extraRequestProperty) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request.Builder builder = new Request.Builder()
                .url(url);
        if (!(extraRequestProperty == null) && !extraRequestProperty.isEmpty()) {
            for (Map.Entry<String, String> entry : extraRequestProperty.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder
                .post(body)
                .build();
        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }


    // public static Bitmap getPic(Context context,String url){
    //
    //     //1.创建一个okhttpclient对象
    //     OkHttpClient okHttpClient = new OkHttpClient();
    //     //2.创建Request.Builder对象，设置参数，请求方式如果是Get，就不用设置，默认就是Get
    //     Request request = new Request.Builder()
    //             .url(url)
    //             .build();
    //     //3.创建一个Call对象，参数是request对象，发送请求
    //     Response response = okHttpClient.newCall(request).execute();
    //     response.body().bytes()
    //
    // }
}
