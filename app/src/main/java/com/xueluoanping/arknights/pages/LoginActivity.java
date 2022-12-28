package com.xueluoanping.arknights.pages;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xueluoanping.arknights.R;
import com.xueluoanping.arknights.api.auth;
import com.xueluoanping.arknights.pro.spTool;

import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class LoginActivity extends AppCompatActivity {

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

    @SuppressLint("ClickableViewAccessibility")
    private void bindListeners() {
        if (passwordEditText != null
                && userEditText != null
                && loginButton != null) {
            userEditText.setText(spTool.getUserName(getApplicationContext()));
            passwordEditText.setText(spTool.getPassword(getApplicationContext()));
            loginButton.requestFocus();
            loginButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
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

                            } catch (JSONException | IOException e) {
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
                    return true;
                }
            });

        }
    }
}
