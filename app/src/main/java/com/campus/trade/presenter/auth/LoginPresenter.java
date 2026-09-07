package com.campus.trade.presenter.auth;

import android.content.Context;
import android.text.TextUtils;
import android.util.Patterns;

import com.campus.trade.base.BasePresenter;
import com.campus.trade.model.repository.UserRepository;
import com.campus.trade.network.callback.ApiCallback;
import com.campus.trade.utils.SharedPrefUtils;

/**
 * 登录 Presenter
 */
public class LoginPresenter extends BasePresenter<LoginContract.View> implements LoginContract.Presenter {

    private final Context mContext;
    private final UserRepository mRepository;

    public LoginPresenter(Context context) {
        this.mContext = context.getApplicationContext();
        this.mRepository = new UserRepository();
    }

    @Override
    public void login(String email, String password) {
        LoginContract.View view = getView();
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (view != null) {
                view.setEmailError("请输入有效的邮箱地址");
            }
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            if (view != null) {
                view.setPasswordError("密码不能少于6位");
            }
            return;
        }
        if (view != null) {
            view.showLoading();
        }
        mRepository.login(email, password, new ApiCallback<String>() {
            @Override
            public void onSuccess(String token) {
                LoginContract.View v = getView();
                if (v != null) {
                    v.hideLoading();
                    SharedPrefUtils.saveToken(mContext, token);
                    SharedPrefUtils.saveUserEmail(mContext, email);
                    v.onLoginSuccess();
                }
            }

            @Override
            public void onFailure(String msg) {
                LoginContract.View v = getView();
                if (v != null) {
                    v.hideLoading();
                    v.showError(msg);
                }
            }
        });
    }

    @Override
    public void saveLoginInfo(String email, String password, boolean remember) {
        SharedPrefUtils.saveLoginInfo(mContext, email, password, remember);
    }

    @Override
    public String getSavedEmail() {
        return SharedPrefUtils.getSavedEmail(mContext);
    }

    @Override
    public String getSavedPassword() {
        return SharedPrefUtils.getSavedPassword(mContext);
    }

    @Override
    public boolean getRememberStatus() {
        return SharedPrefUtils.getRememberStatus(mContext);
    }
}
