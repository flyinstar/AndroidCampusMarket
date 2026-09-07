package com.campus.trade.presenter.user;

import com.campus.trade.base.BaseView;
import com.campus.trade.model.entity.Product;

import java.util.List;

/**
 * 我的收藏契约
 */
public interface FavoritesContract {

    interface View extends BaseView {
        void onFavorites(List<Product> products);
    }

    interface Presenter {
        void load();
    }
}
