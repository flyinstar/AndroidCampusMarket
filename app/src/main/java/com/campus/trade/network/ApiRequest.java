package com.campus.trade.network;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.campus.trade.app.TradeApplication;
import com.campus.trade.network.callback.ApiCallback;
import com.campus.trade.ui.activity.LoginActivity;
import com.campus.trade.utils.SharedPrefUtils;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 请求执行辅助：异步请求并统一切回主线程回调。
 * 收到 401（Token 缺失/失效）时自动清除本地登录态并回到登录页。
 */
public final class ApiRequest {

    private static final Handler MAIN = new Handler(Looper.getMainLooper());

    private ApiRequest() {
    }

    public static <T> void enqueue(Call<BaseResponse<T>> call, ApiCallback<T> callback) {
        call.enqueue(new Callback<BaseResponse<T>>() {
            @Override
            public void onResponse(Call<BaseResponse<T>> call, Response<BaseResponse<T>> response) {
                BaseResponse<T> body = response.body();
                if (body == null) {
                    if (response.code() == 401) {
                        forceRelogin();
                        postFail(callback, "登录已过期，请重新登录");
                        return;
                    }
                    String msg = response.isSuccessful()
                            ? "返回数据为空" : "服务器错误(" + response.code() + ")";
                    postFail(callback, msg);
                    return;
                }
                if (body.isUnauthorized()) {
                    forceRelogin();
                    postFail(callback, "登录已过期，请重新登录");
                    return;
                }
                if (body.isSuccess()) {
                    postSuccess(callback, body.getData());
                } else {
                    postFail(callback, body.safeMsg());
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<T>> call, Throwable t) {
                postFail(callback, friendlyError(t));
            }
        });
    }

    /**
     * 401 处理：清除本地 Token/记住密码之外的登录态并跳转登录页。
     * 多个并发请求同时 401 时也只触发一次导航。
     */
    private static boolean sReloginPending = false;

    private static void forceRelogin() {
        if (sReloginPending) {
            return;
        }
        sReloginPending = true;
        MAIN.post(() -> {
            try {
                SharedPrefUtils.clear(TradeApplication.getContext());
                Intent intent = new Intent(TradeApplication.getContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                TradeApplication.getContext().startActivity(intent);
            } finally {
                sReloginPending = false;
            }
        });
    }

    private static String friendlyError(Throwable t) {
        if (t instanceof UnknownHostException || t instanceof ConnectException) {
            return "无法连接服务器，请检查网络";
        }
        if (t instanceof SocketTimeoutException) {
            return "网络请求超时，请稍后重试";
        }
        if (t instanceof IOException) {
            return "网络连接失败: " + t.getMessage();
        }
        return "请求失败: " + t.getMessage();
    }

    private static <T> void postSuccess(final ApiCallback<T> cb, final T data) {
        MAIN.post(() -> cb.onSuccess(data));
    }

    private static <T> void postFail(final ApiCallback<T> cb, final String msg) {
        MAIN.post(() -> cb.onFailure(msg));
    }
}
