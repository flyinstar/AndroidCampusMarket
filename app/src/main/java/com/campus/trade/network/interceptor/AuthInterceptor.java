package com.campus.trade.network.interceptor;

import android.text.TextUtils;

import com.campus.trade.app.TradeApplication;
import com.campus.trade.utils.SharedPrefUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Token 注入拦截器：为每个请求自动附加 Authorization: Bearer <token>
 */
public class AuthInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String token = SharedPrefUtils.getToken(TradeApplication.getContext());
        if (!TextUtils.isEmpty(token)) {
            Request.Builder builder = original.newBuilder()
                    .header("Authorization", "Bearer " + token);
            return chain.proceed(builder.build());
        }
        return chain.proceed(original);
    }
}
