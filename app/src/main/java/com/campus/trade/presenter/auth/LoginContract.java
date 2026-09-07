package com.campus.trade.presenter.auth;

import com.campus.trade.base.BaseView;

/**
 * 登录模块契约（MVP 范例）
 */
public interface LoginContract {

    interface View extends BaseView {
        void onLoginSuccess();

        void setEmailError(String error);

        void setPasswordError(String error);
    }

    interface Presenter {
        void login(String email, String password);

        void saveLoginInfo(String email, String password, boolean remember);

        String getSavedEmail();

        String getSavedPassword();

        boolean getRememberStatus();
    }
}
