package com.campus.trade.common;

/**
 * 业务异常：抛出后由 GlobalExceptionHandler 统一转为 BaseResponse.error(msg)
 */
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.ERROR.getCode();
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
