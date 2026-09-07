package com.campus.trade.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<Void> handleBusiness(BusinessException e) {
        return BaseResponse.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<Void> handleValid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + " " + f.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return BaseResponse.error(ResultCode.PARAM_ERROR.getCode(), msg);
    }

    @ExceptionHandler({BindException.class, ConstraintViolationException.class})
    public BaseResponse<Void> handleBind(Exception e) {
        String msg = e.getMessage();
        if (e instanceof BindException) {
            msg = ((BindException) e).getBindingResult().getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("; "));
        }
        return BaseResponse.error(ResultCode.PARAM_ERROR.getCode(), msg == null ? "参数错误" : msg);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class})
    public BaseResponse<Void> handleBadRequest(Exception e) {
        return BaseResponse.error(ResultCode.PARAM_ERROR.getCode(), "参数错误或格式不正确: " + e.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public BaseResponse<Void> handleUpload(MaxUploadSizeExceededException e) {
        return BaseResponse.error("图片大小超出限制（单张最大5MB）");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse<Void> handleException(Exception e) {
        log.error("服务器异常", e);
        return BaseResponse.error("服务器内部错误: " + e.getMessage());
    }
}
