package com.campus.trade.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences 本地存储工具
 */
public final class SharedPrefUtils {

    private static final String SP_NAME = "campus_trade_pref";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_REMEMBER = "remember";
    private static final String KEY_USER_ID = "userId";

    private SharedPrefUtils() {
    }

    public static SharedPreferences getSP(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public static void saveToken(Context context, String token) {
        getSP(context).edit().putString(KEY_TOKEN, token).apply();
    }

    public static String getToken(Context context) {
        return getSP(context).getString(KEY_TOKEN, null);
    }

    public static boolean isLoggedIn(Context context) {
        String token = getToken(context);
        return token != null && !token.isEmpty();
    }

    public static void saveUserId(Context context, int userId) {
        getSP(context).edit().putInt(KEY_USER_ID, userId).apply();
    }

    public static int getUserId(Context context) {
        return getSP(context).getInt(KEY_USER_ID, -1);
    }

    public static void saveUserEmail(Context context, String email) {
        getSP(context).edit().putString(KEY_EMAIL, email).apply();
    }

    public static void saveLoginInfo(Context context, String email, String password, boolean remember) {
        getSP(context).edit()
                .putString(KEY_EMAIL, email)
                .putString(KEY_PASSWORD, password)
                .putBoolean(KEY_REMEMBER, remember)
                .apply();
    }

    public static String getSavedEmail(Context context) {
        return getSP(context).getString(KEY_EMAIL, "");
    }

    public static String getSavedPassword(Context context) {
        return getSP(context).getString(KEY_PASSWORD, "");
    }

    public static boolean getRememberStatus(Context context) {
        return getSP(context).getBoolean(KEY_REMEMBER, false);
    }

    public static void clear(Context context) {
        getSP(context).edit().clear().apply();
    }
}
