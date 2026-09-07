package com.campus.trade.presenter.user;

import com.campus.trade.base.BaseView;
import com.campus.trade.model.entity.User;

/**
 * “我的”契约
 */
public interface ProfileContract {

    interface View extends BaseView {
        void onProfileLoaded(User user);
    }

    interface Presenter {
        void loadProfile();

        void changePassword(String oldPassword, String newPassword);
    }
}
