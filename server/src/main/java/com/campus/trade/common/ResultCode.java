package com.campus.trade.common;

/**
 * 响应码枚举
 */
public enum ResultCode {

    SUCCESS(0, "成功"),
    ERROR(500, "服务器内部错误"),
    UNAUTHORIZED(401, "未登录或Token已过期"),
    PARAM_ERROR(400, "参数错误"),
    NOT_FOUND(404, "资源不存在"),
    FORBIDDEN(403, "无权限操作");

    private final int code;
    private final String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
