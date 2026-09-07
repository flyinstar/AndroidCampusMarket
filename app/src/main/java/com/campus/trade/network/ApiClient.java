package com.campus.trade.network;

import com.campus.trade.network.interceptor.AuthInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit 单例客户端
 *
 * BASE_URL 说明（见《开发文档.md》5.2）：
 *  - 模拟器调试：http://10.0.2.2:8080/
 *  - 真机调试：改为电脑局域网 IP，如 http://192.168.1.100:8080/
 */
public class ApiClient {

    public static final String BASE_URL = "http://192.168.100.100:8080/";

    private static Retrofit retrofit;
    private static ApiService apiService;

    private ApiClient() {
    }

    public static ApiService getApiService() {
        if (apiService == null) {
            synchronized (ApiClient.class) {
                if (apiService == null) {
                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY);

                    OkHttpClient client = new OkHttpClient.Builder()
                            .addInterceptor(new AuthInterceptor())
                            .addInterceptor(logging)
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(30, TimeUnit.SECONDS)
                            .build();

                    retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    apiService = retrofit.create(ApiService.class);
                }
            }
        }
        return apiService;
    }

    /**
     * 服务器绝对图片URL的主机替换为当前 BASE_URL 主机
     * （服务器配置 ACCESS_URL=http://localhost:8080/images/，客户端需换成本机可达地址）
     */
    public static String toAccessibleImageUrl(String url) {
        if (url == null || url.isEmpty()) {
            return url;
        }
        if (url.contains("://localhost") || url.contains("://127.0.0.1")) {
            int idx = url.indexOf("://");
            int slash = url.indexOf('/', idx + 3);
            String hostPart = (slash < 0) ? url : url.substring(0, slash);
            return BASE_URL.substring(0, BASE_URL.length() - 1) + url.substring(hostPart.length());
        }
        return url;
    }
}
