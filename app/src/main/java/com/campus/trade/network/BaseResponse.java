package com.campus.trade.network;

import android.text.TextUtils;

/**
 * 统一响应实体（与服务端 BaseResponse 对应）
 */
public class BaseResponse<T> {

    public static final int CODE_SUCCESS = 0;
    public static final int CODE_UNAUTHORIZED = 401;

    private int code;
    private String msg;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return code == CODE_SUCCESS;
    }

    public boolean isUnauthorized() {
        return code == CODE_UNAUTHORIZED;
    }

    public String safeMsg() {
        return TextUtils.isEmpty(msg) ? "操作失败" : msg;
    }
}
