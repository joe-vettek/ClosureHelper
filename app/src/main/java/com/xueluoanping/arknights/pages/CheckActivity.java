package com.xueluoanping.arknights.pages;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.geetest.sdk.GT3ConfigBean;
import com.geetest.sdk.GT3ErrorBean;
import com.geetest.sdk.GT3GeetestUtils;
import com.geetest.sdk.GT3Listener;
import com.xueluoanping.arknights.SimpleApplication;
import com.xueluoanping.arknights.api.main.Game;
import com.xueluoanping.arknights.base.BaseActivity;
import com.xueluoanping.arknights.pro.SimpleTool;

import org.json.JSONException;
import org.json.JSONObject;

public class CheckActivity extends BaseActivity {

    private static final String TAG = CheckActivity.class.getSimpleName();
    private GT3ConfigBean gt3ConfigBean;
    private GT3GeetestUtils gt3GeetestUtils;
    private String challenge = "";
    private String gt = "";
    private String account = "";
    private int platform = Game.Platform_Unknown;

    // private  GT3GeetestButton geetestButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 请在oncreate方法里初始化以获取足够手势数据来保证第一轮验证成功率
        // setContentView(R.layout.activity_check);
        //  geetestButton = (GT3GeetestButton) findViewById(R.id.btn_geetest);

        getExtra();


        gt3GeetestUtils = new GT3GeetestUtils(this);
        initConfigBean();
        startCheck();
    }

    private void getExtra() {
        Bundle extras = getIntent().getExtras();
        this.challenge = extras.getString("challenge", "");
        this.gt = extras.getString("gt", "");
        this.account = extras.getString("account", "");
        this.platform = extras.getInt("platform", Game.Platform_Unknown);
    }

    // {
    //     "success" : 1,
    //         "challenge" : "4a5cef77243baa51b2090f7258bf1368",
    //         "gt" : "019924a82c70bb123aae90d483087f94"
    // }
    private void startCheck() {


    }

    private void initConfigBean() {
        // 配置bean文件，也可在oncreate初始化
        gt3ConfigBean = new GT3ConfigBean();
        // 设置验证模式，1：bind，2：unbind
        gt3ConfigBean.setPattern(1);
        // 设置点击灰色区域是否消失，默认不消息
        gt3ConfigBean.setCanceledOnTouchOutside(false);
        // 设置语言，如果为null则使用系统默认语言
        gt3ConfigBean.setLang(null);
        // 设置加载webview超时时间，单位毫秒，默认10000，仅且webview加载静态文件超时，不包括之前的http请求
        gt3ConfigBean.setTimeout(5000);
        // 设置webview请求超时(用户点选或滑动完成，前端请求后端接口)，单位毫秒，默认10000
        gt3ConfigBean.setWebviewTimeout(10000);
        // 设置回调监听
        gt3ConfigBean.setListener(new SimpleListener());
        gt3GeetestUtils.init(gt3ConfigBean);
        gt3GeetestUtils.startCustomFlow();
        // geetestButton.setGeetestUtils(gt3GeetestUtils);
    }

    public void onDestroy() {
        super.onDestroy();
        // TODO 销毁资源，务必添加
        if (gt3GeetestUtils != null) {
            gt3GeetestUtils.destory();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // TODO 横竖屏切换
        if (gt3GeetestUtils != null) {
            gt3GeetestUtils.changeDialogLayout();
        }
    }


    public class SimpleListener extends GT3Listener {

        /**
         * 验证码加载完成
         *
         * @param duration 加载时间和版本等信息，为json格式
         */
        @Override
        public void onDialogReady(String duration) {
            Log.e(TAG, "GT3BaseListener-->onDialogReady-->" + duration);
        }

        /**
         * 图形验证结果回调
         *
         * @param code 1为正常 0为失败
         */
        @Override
        public void onReceiveCaptchaCode(int code) {
            Log.e(TAG, "GT3BaseListener-->onReceiveCaptchaCode-->" + code);
        }

        /**
         * 自定义api2回调
         *
         * @param result，api2请求上传参数
         */

        // {
        //     "challenge": "string",
        //         "geetest_challenge": "string",
        //         "geetest_seccode": "string",
        //         "geetest_validate": "string",
        //         "success": true
        // }
        // GT3BaseListener-->onDialogResult-->
        // {"geetest_challenge":"d962406811e83d49c6d34b498744d378e0",
        // "geetest_seccode":"73180685879378f89daf5b00e728013d|jordan",
        // "geetest_validate":"73180685879378f89daf5b00e728013d"}
        @Override
        public void onDialogResult(String result) {
            Log.e(TAG, "GT3BaseListener-->onDialogResult-->" + result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                jsonObject.put("challenge", CheckActivity.this.challenge);
                jsonObject.put("success", true);
                gt3GeetestUtils.destory();
                finish();
                new Thread(() -> {
                    if (!Game.TryCaptcha(CheckActivity.this.account, CheckActivity.this.platform, jsonObject)) {
                        SimpleTool.toastInThread(CheckActivity.this, "验证失败，请重新尝试！");
                    } else {
                        SimpleTool.toastInThread(CheckActivity.this, "验证通过，正在登录中！");
                        // ((SimpleApplication) SimpleApplication.getContext()).restart();
                    }

                    // ((SimpleApplication) SimpleApplication.getContext()).restart();
                }).start();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // 开启api2逻辑
            // new RequestAPI2().execute(result);
            // gt3GeetestUtils.destory();
        }

        /**
         * 统计信息，参考接入文档
         *
         * @param result
         */
        @Override
        public void onStatistics(String result) {
            Log.e(TAG, "GT3BaseListener-->onStatistics-->" + result);
        }

        /**
         * 验证码被关闭
         *
         * @param num 1 点击验证码的关闭按钮来关闭验证码, 2 点击屏幕关闭验证码, 3 点击返回键关闭验证码
         */
        @Override
        public void onClosed(int num) {
            Log.e(TAG, "GT3BaseListener-->onClosed-->" + num);
        }

        /**
         * 验证成功回调
         *
         * @param result
         */
        // 不会到达这里不需要考虑
        @Override
        public void onSuccess(String result) {
            // Log.e(TAG, "GT3BaseListener-->onSuccess-->" + result);
        }

        /**
         * 验证失败回调
         *
         * @param errorBean 版本号，错误码，错误描述等信息
         */
        @Override
        public void onFailed(GT3ErrorBean errorBean) {
            SimpleTool.toastInThread(CheckActivity.this, errorBean.errorDesc + "，请重新登录再试");
            gt3GeetestUtils.destory();
            finish();
            Log.e(TAG, "GT3BaseListener-->onFailed-->" + errorBean.toString());
        }

        /**
         * 自定义api1回调
         */
        @Override
        public void onButtonClick() {
            // new RequestAPI1().execute();
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("success", 1);
                jsonObject.put("challenge", CheckActivity.this.challenge);
                jsonObject.put("gt", CheckActivity.this.gt);
            } catch (Exception e) {
                e.printStackTrace();
            }
            gt3ConfigBean.setApi1Json(jsonObject);
            // 继续验证
            gt3GeetestUtils.getGeetest();
        }

    }

    /**
     * 请求api1
     */
    class RequestAPI1 extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... params) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("success", 1);
                jsonObject.put("challenge", CheckActivity.this.challenge);
                jsonObject.put("gt", CheckActivity.this.gt);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject params) {
            // SDK可识别格式为
            // {"success":1,"challenge":"06fbb267def3c3c9530d62aa2d56d018","gt":"019924a82c70bb123aae90d483087f94","new_captcha":true}
            // TODO 设置返回api1数据，即使为 null 也要设置，SDK内部已处理
            gt3ConfigBean.setApi1Json(params);
            // 继续验证
            gt3GeetestUtils.getGeetest();
        }
    }
}
