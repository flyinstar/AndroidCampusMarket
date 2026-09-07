package com.campus.trade.base;

/**
 * MVP - View 基类接口
 */
public interface BaseView {

    void showLoading();

    void hideLoading();

    void showError(String msg);

    void showToast(String msg);
}
