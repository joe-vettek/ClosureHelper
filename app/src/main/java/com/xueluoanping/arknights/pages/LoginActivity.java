package com.xueluoanping.arknights.pages;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
    }


    private void bindListeners() {
        // if (passwordEditText != null
        //         && userEditText != null
        //         && loginButton != null)
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
                    spTool.saveUserName( userEditText.getText().toString());
                    // 因为问题不大所以不做加密
                    spTool.savePassword( passwordEditText.getText().toString());


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                auth.init(getApplicationContext());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });

                            } catch (Exception e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            });


        }
    }
}
