package com.xueluoanping.arknights.pages;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.xueluoanping.arknights.R;
import com.xueluoanping.arknights.base.BaseActivity;

import java.net.URLDecoder;

public class WebActivity2 extends BaseActivity {
    private static final String TAG = WebActivity2.class.getSimpleName();
    WebView webView;
    FloatingActionButton floatingActionButton;
    public static String url_b = "https://wiki.biligame.com/arknights/%E9%A6%96%E9%A1%B5";
    public static String url = "https://m.prts.wiki/w/%E9%A6%96%E9%A1%B5";

    public static String getPrtsUrl_base(String name) {
        return prtsUrl_base+ URLDecoder.decode(name);
    }

    public static String getsUrl_prtsSearch(String name) {
        return url_prtsSearch+ URLDecoder.decode(name);
    }

    public static String url_prtsSearch="https://m.prts.wiki/index.php?search=";
    public static String prtsUrl_base = "https://m.prts.wiki/w/";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        webView = findViewById(R.id.webViewer);
        floatingActionButton = findViewById(R.id.fab_web);
        initSetting();

        setWebView();


        // String url = host.baseApi + "/Auth/" + spTool.getToken(getApplicationContext());
        // Log.d(TAG, "onCreate: " + url);
        // webView.loadUrl(url);
        //

        try {
            webView.loadUrl(getIntent().getStringExtra("url"));
        } catch (Exception e) {
            e.printStackTrace();
            finish();
            toastInThread("??????????????????");
        }


        floatingActionButton.setOnClickListener(v -> finish());
    }

    private void setWebView() {
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Log.d(TAG, "onJsAlert: " + url + message + result);
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d(TAG, "onConsoleMessage: " + consoleMessage.message());
                return super.onConsoleMessage(consoleMessage);
            }

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                Log.d(TAG, "onCreateWindow: " + resultMsg.toString());
                return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);

            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                Log.d(TAG, "onShowCustomView: " + callback.toString());
                super.onShowCustomView(view, callback);
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                WebView.HitTestResult hit = view.getHitTestResult();
                //hit.getExtra()???null??????hit.getType() == 0????????????????????????URL??????????????????????????????????????????
                if (hit.getExtra() != null)
                    if (hit.getExtra().isEmpty() || hit.getType() == 0) {
                        //???????????????????????????????????????????????????????????????????????????????????????????????????????????????
                        Log.e("?????????", "?????????: " + hit.getType() + " && EXTRA??????" + hit.getExtra() + "------");
                        Log.e("?????????", "GetURL: " + view.getUrl() + "\n" + "getOriginalUrl()" + view.getOriginalUrl());
                        Log.d("?????????", "URL: " + request.getUrl().toString());
                    }

                Log.d(TAG, "shouldOverrideUrlLoading: " + request.getUrl());
                if (request.getUrl().toString().startsWith("http://") || request.getUrl().toString().startsWith("https://")) { //?????????url???http/https????????????
                    view.loadUrl(request.getUrl().toString());
                    return false; //??????false?????????url?????????????????????,url????????????????????????????????????
                }
                return true;
            }


            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); //????????????????????????
                // handler.cancel(); //????????????????????????????????????
                // handler.handleMessage(null); //??????????????????
            }

            // public void onPageFinished(WebView view, String url) {
            //     super.onPageFinished(view, url);
            //     if (m_webView.getProgress() == 100) {
            //         progressBar.setVisibility(View.GONE);
            //         m_webView.setVisibility(View.VISIBLE);
            //     }
            // }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                hasLoad = false;
            }

            boolean hasLoad = false;

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "onPageFinished: " + url);
            }
        });
    }


    private void initSetting() {
        //???????????????????????????????????????????????????????????????????????????WebView??????????????????????????????
        // webview ??????????????????????????????
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

    }

    @Override
    protected void onDestroy() {

        deleteDatabase("webview.db");
        deleteDatabase("webviewCache.db");

        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();
        webView.clearHistory();
        super.onDestroy();
    }

    // ???????????????
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack())
                // ??????WebView????????????????????????-??????
                webView.goBack();
            else
                finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
