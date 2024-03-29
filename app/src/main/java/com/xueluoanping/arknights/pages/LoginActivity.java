package com.xueluoanping.arknights.pages;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.xueluoanping.arknights.R;
import com.xueluoanping.arknights.api.main.auth;
import com.xueluoanping.arknights.base.BaseActivity;
import com.xueluoanping.arknights.pro.spTool;


public class LoginActivity extends BaseActivity {

    private Button loginButton;
    private EditText userEditText;
    private EditText passwordEditText;
    private TextView textView5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bindControls();
        bindListeners();
    }


    private void bindControls() {
        loginButton = findViewById(R.id.bt_login);
        userEditText = findViewById(R.id.input_user);
        passwordEditText = findViewById(R.id.input_password);
        textView5 = findViewById(R.id.textView5);

    }


    private void bindListeners() {
        // if (passwordEditText != null
        //         && userEditText != null
        //         && loginButton != null)
        String s = "<div>本APP由可露希尔工作室提供API支持。<br>注册账号和绑定QQ请点击访问<a href=\"https://arknights.host/\">可露希尔工作室网页版本</a>。</div>";
        textView5.setText(Html.fromHtml(s, Html.FROM_HTML_MODE_COMPACT));
        textView5.setMovementMethod(LinkMovementMethod.getInstance());
        {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            // 隐藏软键盘
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
            userEditText.setText(spTool.getUserName(getApplicationContext()));
            passwordEditText.setText(spTool.getPassword(getApplicationContext()));
            // loginButton.requestFocus();
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spTool.saveUserName(userEditText.getText().toString());
                    // 因为问题不大所以不做加密
                    spTool.savePassword(passwordEditText.getText().toString());


                    new Thread(() -> {
                        try {
                            runOnUiThread(() -> {
                                Toast.makeText(LoginActivity.this, "正在登录", Toast.LENGTH_SHORT).show();
                            loginButton.setText("登陆中");
                            loginButton.setEnabled(false);
                            });

                            auth.initWithPassword(LoginActivity.this);
                            runOnUiThread(() -> {
                                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                loginButton.setText("登录");
                                loginButton.setEnabled(true);
                                finish();

                            });

                        } catch (Exception e) {
                            runOnUiThread(() -> {
                                auth.cancelCheck();
                                loginButton.setText("登录");
                                loginButton.setEnabled(true);
                                Toast.makeText(LoginActivity.this, "登录失败,"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                            e.printStackTrace();
                        }
                    }).start();
                }
            });


        }
    }

    @Override
    protected void onDestroy() {
        auth.destroyCheck();
        super.onDestroy();
    }
}
