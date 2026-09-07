package com.campus.trade.presenter.auth;

import com.campus.trade.base.BaseView;

/**
 * 注册模块契约
 */
public interface RegisterContract {

    interface View extends BaseView {
        void onRegisterSuccess();

        void setFieldError(String field, String error);
    }

    interface Presenter {
        void register(String nickname, String email, String password, String confirm);
    }
}
