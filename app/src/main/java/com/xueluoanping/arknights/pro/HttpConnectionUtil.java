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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
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
            DownLoadTextPages(urlStr, null, true);
        } catch (UnknownHostException une) {
            return false;
        }
        return true;
    }

    public static String DownLoadTextPages(String urlStr, Map<String, String> extraRequestProperty, boolean useSSL) throws IOException {
        OkHttpClient client = new OkHttpClient();
        if (!useSSL) client = getUnsafeOkHttpClient();
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

    public static String DownLoadTextPagesNoOk(String urlStr, Map<String, String> extraRequestProperty) {
        URL url = null;
        HttpURLConnection httpConn = null;

        try {
            url = new URL(urlStr);
            httpConn = (HttpURLConnection) url.openConnection();
            HttpURLConnection.setFollowRedirects(true);
            httpConn.setRequestMethod("GET");

            httpConn.setRequestProperty("accept", "*/*");
            httpConn.setRequestProperty("accept-encoding", "gzip, deflate, br");
            httpConn.setRequestProperty("accept-language", "zh-CN,zh;q=0.9,ja;q=0.8");


            httpConn.setRequestProperty("sec-ch-ua", "\"Chromium\";v=\"104\", \" Not A;Brand\";v=\"99\", \"Google Chrome\";v=\"104\"");
            httpConn.setRequestProperty("sec-ch-ua-mobile", "?1");
            httpConn.setRequestProperty("sec-ch-ua-platform", "\"Android\"");
            httpConn.setRequestProperty("sec-fetch-dest", " script");
            httpConn.setRequestProperty("sec-fetch-mode", "no-cors");

            httpConn.setRequestProperty("sec-fetch-site", "same-site");

            // httpConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 11; Pixel 5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.91 Mobile Safari/537.36");
            httpConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; U; Android 9; zh-cn; Redmi Note 8 Build/PKQ1.190616.001) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/71.0.3578.141 Mobile Safari/537.36 XiaoMi/MiuiBrowser/12.5.22");
            if (!(extraRequestProperty == null) && !extraRequestProperty.isEmpty()) {
                for (Map.Entry<String, String> entry : extraRequestProperty.entrySet()) {
                    httpConn.setRequestProperty(entry.getKey(), entry.getValue());
                }
                // extraRequestProperty.forEach((String key, String value) -> {});
            }
            // logger.info(httpConn.getResponseMessage());

            //            ??????????????????
            httpConn.connect();
            int resultCode = httpConn.getResponseCode();

            //?????????????????????
            if (HttpURLConnection.HTTP_OK == resultCode) {

                if ((httpConn.getContentEncoding() + "").matches("gzip")) {
                    return TransCoding.GZIP_to_String(httpConn.getInputStream());
                } else {
                    StringBuffer sb = new StringBuffer();
                    String readLine = new String();
                    BufferedReader responseReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
                    while ((readLine = responseReader.readLine()) != null) {
                        sb.append(readLine).append("\n");
                    }
                    responseReader.close();
                    return sb.toString();
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            try {

                httpConn.disconnect();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return "";
    }

    // post??????
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

    public static String delete(String url, String json, Map<String, String> extraRequestProperty) throws IOException {

        Request.Builder builder = new Request.Builder()
                .url(url);
        if (!(extraRequestProperty == null) && !extraRequestProperty.isEmpty()) {
            for (Map.Entry<String, String> entry : extraRequestProperty.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder
                .delete(FormBody.create(JSON, json))
                .build();
        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            return response.body().string();
        }
    }
    // public static Bitmap getPic(Context context,String url){
    //
    //     //1.????????????okhttpclient??????
    //     OkHttpClient okHttpClient = new OkHttpClient();
    //     //2.??????Request.Builder?????????????????????????????????????????????Get?????????????????????????????????Get
    //     Request request = new Request.Builder()
    //             .url(url)
    //             .build();
    //     //3.????????????Call??????????????????request?????????????????????
    //     Response response = okHttpClient.newCall(request).execute();
    //     response.body().bytes()
    //
    // }

    //okHttp3????????????????????????

    public static OkHttpClient getUnsafeOkHttpClient() {

        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final javax.net.ssl.SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory);

            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
