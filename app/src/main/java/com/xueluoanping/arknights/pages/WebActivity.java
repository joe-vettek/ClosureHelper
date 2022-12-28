package com.xueluoanping.arknights.pages;

import android.content.Intent;
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
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xueluoanping.arknights.R;
import com.xueluoanping.arknights.api.host;
import com.xueluoanping.arknights.pro.spTool;

public class WebActivity extends AppCompatActivity {
    private static final String TAG = WebActivity.class.getSimpleName();
    WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        webView = findViewById(R.id.webViewer);

        initSetting();

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
                //hit.getExtra()为null或者hit.getType() == 0都表示即将加载的URL会发生重定向，需要做拦截处理
                if (hit.getExtra() != null)
                    if (hit.getExtra().isEmpty() || hit.getType() == 0) {
                        //通过判断开头协议就可解决大部分重定向问题了，有另外的需求可以在此判断下操作
                        Log.e("重定向", "重定向: " + hit.getType() + " && EXTRA（）" + hit.getExtra() + "------");
                        Log.e("重定向", "GetURL: " + view.getUrl() + "\n" + "getOriginalUrl()" + view.getOriginalUrl());
                        Log.d("重定向", "URL: " + request.getUrl().toString());
                    }

                Log.d(TAG, "shouldOverrideUrlLoading: " + request.getUrl());
                if (request.getUrl().toString().startsWith("http://") || request.getUrl().toString().startsWith("https://")) { //加载的url是http/https协议地址
                    view.loadUrl(request.getUrl().toString());
                    return false; //返回false表示此url默认由系统处理,url未加载完成，会继续往下走
                }
                return true;
            }


            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); //表示等待证书响应
                // handler.cancel(); //表示挂起连接，为默认方式
                // handler.handleMessage(null); //可做其他处理
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
                if (url.contains("login")) {

                    if (view.getProgress() == 100 && !hasLoad) {
                        hasLoad = true;
                        String jsCode = getLoginJS();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        view.evaluateJavascript(jsCode, new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                                Log.d(TAG, "onReceiveValue: " + value);
                                // 此处为JS返回的结果
                            }
                        });
                    }

                }

                // else   if (url.contains("home"))
                {
                    String jsCode = getHideJS();
                    view.evaluateJavascript(jsCode, null);

                }
            }
        });

        String url = host.baseApi + "/Auth/" + spTool.getToken(getApplicationContext());
        Log.d(TAG, "onCreate: " + url);
        // webView.loadUrl(url);

        url = "https://arknights.host/login";
        webView.loadUrl(url);
    }

    private String getLoginJS() {
        return "document.getElementsByClassName('ark-input mb-2')[0].value='" + spTool.getUserName(getApplicationContext()) + "' \n" +
                "document.getElementsByClassName('ark-input mb-2')[0].dispatchEvent(new Event('input')) \n" +
                "document.getElementsByClassName('ark-input mb-2')[1].value='" + spTool.getPassword(getApplicationContext()) + "' \n" +
                "document.getElementsByClassName('ark-input mb-2')[1].dispatchEvent(new Event('input')) \n" +
                "document.getElementsByClassName('btn px-8 py-3 btn-block btn-info')[0].click() \n" +
                // " window.open('https://arknights.host/home','_self') \n" +
                "1111";
    }

    private String getHideJS() {

        return "document.getElementsByClassName('btn btn-circle btn-ghost')[0].style.display='none' \n"
                + "document.getElementsByClassName('w-12 rounded-full')[0].style.display='none' \n"
                + "document.getElementsByClassName('ark-card relative')[0].style.display='none' \n"
                // +"document.getElementsByClassName('text-lg mt-2')[0].style.display='none' \n"
                + "document.getElementsByClassName('basis-full md:basis-1/3 md:mr-4 mb-4')[0].style.display='none' \n"
                + "var a = document.createElement('span');\n" +
                "a.innerHTML=\"<br><HR style=color=#212121><div align='center'>验证完成后请滑动返回</div><br>  \"\n" +
                "document.getElementsByClassName('mx-auto px-5 py-2')[0].appendChild(a)";
    }

    private void initSetting() {
        //系统默认会通过手机浏览器打开网页，为了能够直接通过WebView显示网页，则必须设置
        // webview 中设置允许缓存数据。
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

    // 返回键监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // if (webView.canGoBack())
            //     // 判断WebView是否能够返回，能-返回
            //     webView.goBack();
            // else
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
