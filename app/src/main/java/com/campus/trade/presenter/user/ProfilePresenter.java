package com.campus.trade.presenter.user;

import com.campus.trade.base.BasePresenter;
import com.campus.trade.model.entity.User;
import com.campus.trade.model.repository.UserRepository;
import com.campus.trade.network.callback.ApiCallback;

/**
 * “我的” Presenter
 */
public class ProfilePresenter extends BasePresenter<ProfileContract.View>
        implements ProfileContract.Presenter {

    private final UserRepository mRepository;

    public ProfilePresenter() {
        this.mRepository = new UserRepository();
    }

    @Override
    public void loadProfile() {
        mRepository.getProfile(new ApiCallback<User>() {
            @Override
            public void onSuccess(User data) {
                ProfileContract.View v = getView();
                if (v != null) {
                    v.onProfileLoaded(data);
                }
            }

            @Override
            public void onFailure(String msg) {
                ProfileContract.View v = getView();
                if (v != null) {
                    v.showError(msg);
                }
            }
        });
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        mRepository.changePassword(oldPassword, newPassword, new ApiCallback<String>() {
            @Override
            public void onSuccess(String data) {
                ProfileContract.View v = getView();
                if (v != null) {
                    v.showToast(data == null ? "密码修改成功" : data);
                }
            }

            @Override
            public void onFailure(String msg) {
                ProfileContract.View v = getView();
                if (v != null) {
                    v.showError(msg);
                }
            }
        });
    }
}
