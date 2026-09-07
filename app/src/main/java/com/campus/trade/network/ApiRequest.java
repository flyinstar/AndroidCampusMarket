package com.campus.trade.network;

import android.os.Handler;
import android.os.Looper;

import com.campus.trade.network.callback.ApiCallback;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 请求执行辅助：异步请求并统一切回主线程回调
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
                    String msg = response.isSuccessful()
                            ? "返回数据为空" : "服务器错误(" + response.code() + ")";
                    postFail(callback, msg);
                    return;
                }
                if (body.isUnauthorized()) {
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
