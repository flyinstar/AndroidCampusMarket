package com.campus.trade.app;

import android.app.Application;
import android.content.Context;

/**
 * 全局 Application：持有 Context 供网络拦截器/工具类使用
 */
public class TradeApplication extends Application {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
    }

    public static Context getContext() {
        return sContext;
    }
}
