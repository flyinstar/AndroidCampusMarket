package com.campus.trade.common;

/**
 * 统一响应体
 */
public class BaseResponse<T> {

    /** 0 成功；非 0 失败 */
    private int code;
    private String msg;
    private T data;

    public BaseResponse() {
    }

    public BaseResponse(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(ResultCode.SUCCESS.getCode(), "success", data);
    }

    public static <T> BaseResponse<T> success(String msg, T data) {
        return new BaseResponse<>(ResultCode.SUCCESS.getCode(), msg, data);
    }

    public static <T> BaseResponse<T> error(String msg) {
        return new BaseResponse<>(ResultCode.ERROR.getCode(), msg, null);
    }

    public static <T> BaseResponse<T> error(int code, String msg) {
        return new BaseResponse<>(code, msg, null);
    }

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
}
