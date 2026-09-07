package com.campus.trade.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.campus.trade.R;
import com.campus.trade.utils.SharedPrefUtils;

/**
 * 启动页：根据登录态跳转。
 * 自绘启动画面为有意的产品设计（minSdk 23，兼容 Android 12 以下系统）。
 */
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = SharedPrefUtils.isLoggedIn(this)
                    ? new Intent(this, MainActivity.class)
                    : new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, 900);
    }
}
