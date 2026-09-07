package com.campus.trade.network;

import com.campus.trade.app.TradeApplication;
import com.campus.trade.network.interceptor.AuthInterceptor;
import com.campus.trade.utils.SharedPrefUtils;

import java.net.URI;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit 单例客户端。
 *
 * 服务器地址支持在登录页的「服务器设置」里自定义（保存在本机，
 * 切换后重建 Retrofit 单例），未自定义时使用内置默认地址 DEFAULT_BASE_URL。
 * 内置默认地址说明（见《开发文档.md》5.2）：
 *  - 模拟器调试：http://10.0.2.2:8080/
 *  - 真机调试：改为电脑局域网 IP，如 http://192.168.1.100:8080/
 */
public class ApiClient {

    public static final String DEFAULT_BASE_URL = "http://192.168.100.100:8080/";

    /** 当前生效的服务器地址（进程内缓存，末尾带 '/'） */
    private static String sBaseUrl;

    private static Retrofit retrofit;
    private static ApiService apiService;

    private ApiClient() {
    }

    public static synchronized ApiService getApiService() {
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
                    .baseUrl(getBaseUrl())
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }

    /** 当前生效的服务器地址（自定义优先，否则内置默认值），末尾带 '/' */
    public static synchronized String getBaseUrl() {
        if (sBaseUrl == null) {
            sBaseUrl = DEFAULT_BASE_URL;
            String saved = SharedPrefUtils.getServerUrl(TradeApplication.getContext());
            if (saved != null) {
                String normalized = normalize(saved);
                if (normalized != null) {
                    sBaseUrl = normalized;
                }
            }
        }
        return sBaseUrl;
    }

    /**
     * 切换服务器：保存新地址并使 Retrofit 单例失效，下次访问时按新地址重建。
     * 传入空/null 视为恢复默认。
     */
    public static synchronized void switchServer(String rawUrl) {
        String normalized = normalize(rawUrl);
        if (normalized == null) {
            // 恢复默认
            sBaseUrl = DEFAULT_BASE_URL;
            SharedPrefUtils.saveServerUrl(TradeApplication.getContext(), null);
        } else {
            sBaseUrl = normalized;
            SharedPrefUtils.saveServerUrl(TradeApplication.getContext(), normalized);
        }
        retrofit = null;
        apiService = null;
    }

    /**
     * 规范化服务器地址并校验：
     * 允许不带协议的输入（自动补 http://）、自动补末尾 '/'；
     * 必须是 http/https、含主机、且不带子路径（App 接口都在根路径下）。
     *
     * @return 规范化后的地址；非法输入返回 null
     */
    public static String normalize(String raw) {
        if (raw == null) {
            return null;
        }
        String s = raw.trim();
        if (s.isEmpty()) {
            return null;
        }
        if (!s.contains("://")) {
            s = "http://" + s;
        }
        if (!s.endsWith("/")) {
            s = s + "/";
        }
        try {
            URI uri = URI.create(s);
            String scheme = uri.getScheme();
            String host = uri.getHost();
            if (host == null || host.isEmpty()) {
                return null;
            }
            if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
                return null;
            }
            String path = uri.getPath();
            if (path != null && !path.isEmpty() && !"/".equals(path)) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
        return s;
    }

    /**
     * 服务器存库的图片 URL 常写死为 localhost 或旧服务器地址，
     * 此处统一替换为“当前生效服务器”的主机，保证图片可达：
     *  - 本来就是 localhost/127.0.0.1 的，一律换主机；
     *  - /images/ 开头的商品图若主机与当前服务器不一致也换主机；
     *  - 其余外部链接原样返回。
     */
    public static String toAccessibleImageUrl(String url) {
        if (url == null || url.isEmpty()) {
            return url;
        }
        int idx = url.indexOf("://");
        if (idx < 0) {
            return url;
        }
        int hostStart = idx + 3;
        int slash = url.indexOf('/', hostStart);
        String rest = (slash < 0) ? "/" : url.substring(slash);

        String authority = (slash < 0) ? url.substring(hostStart) : url.substring(hostStart, slash);
        String host = authority;
        int colon = host.indexOf(':');
        if (colon >= 0) {
            host = host.substring(0, colon);
        }
        host = host.toLowerCase(Locale.US);
        boolean loopback = "localhost".equals(host) || "127.0.0.1".equals(host)
                || "0.0.0.0".equals(host);

        if (!loopback) {
            // 非本机回环地址：仅当它是本服务的 /images/** 商品图且主机与当前服务器不同才替换
            if (!rest.startsWith("/images/")) {
                return url;
            }
            String currentAuthority = authorityOf(getBaseUrl());
            if (currentAuthority != null && currentAuthority.equalsIgnoreCase(authority)) {
                return url;
            }
        }
        String base = getBaseUrl();
        String baseNoTrail = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
        return baseNoTrail + rest;
    }

    private static String authorityOf(String url) {
        int idx = url.indexOf("://");
        if (idx < 0) {
            return null;
        }
        int start = idx + 3;
        int slash = url.indexOf('/', start);
        return (slash < 0) ? url.substring(start) : url.substring(start, slash);
    }
}
