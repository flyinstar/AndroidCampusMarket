package com.campus.trade.network.callback;

/**
 * 统一网络回调（成功 data 非空由实现方自行判断）
 */
public interface ApiCallback<T> {

    void onSuccess(T data);

    void onFailure(String msg);
}
