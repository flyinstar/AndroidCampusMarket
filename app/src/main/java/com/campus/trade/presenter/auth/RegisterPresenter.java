package com.campus.trade.presenter.auth;

import android.text.TextUtils;
import android.util.Patterns;

import com.campus.trade.base.BasePresenter;
import com.campus.trade.model.repository.UserRepository;
import com.campus.trade.network.callback.ApiCallback;

/**
 * 注册 Presenter
 */
public class RegisterPresenter extends BasePresenter<RegisterContract.View>
        implements RegisterContract.Presenter {

    private final UserRepository mRepository;

    public RegisterPresenter() {
        this.mRepository = new UserRepository();
    }

    @Override
    public void register(String nickname, String email, String password, String confirm) {
        RegisterContract.View view = getView();
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (view != null) {
                view.setFieldError("email", "请输入有效的邮箱地址");
            }
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            if (view != null) {
                view.setFieldError("password", "密码不能少于6位");
            }
            return;
        }
        if (!password.equals(confirm)) {
            if (view != null) {
                view.setFieldError("confirm", "两次输入的密码不一致");
            }
            return;
        }
        if (view != null) {
            view.showLoading();
        }
        mRepository.register(email, password, nickname, new ApiCallback<String>() {
            @Override
            public void onSuccess(String data) {
                RegisterContract.View v = getView();
                if (v != null) {
                    v.hideLoading();
                    v.onRegisterSuccess();
                }
            }

            @Override
            public void onFailure(String msg) {
                RegisterContract.View v = getView();
                if (v != null) {
                    v.hideLoading();
                    v.showError(msg);
                }
            }
        });
    }
}
