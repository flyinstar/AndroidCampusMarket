package com.campus.trade.presenter.user;

import com.campus.trade.base.BasePresenter;
import com.campus.trade.model.entity.Product;
import com.campus.trade.model.repository.UserRepository;
import com.campus.trade.network.callback.ApiCallback;

import java.util.List;

/**
 * 我的收藏 Presenter
 */
public class FavoritesPresenter extends BasePresenter<FavoritesContract.View>
        implements FavoritesContract.Presenter {

    private final UserRepository mRepository;

    public FavoritesPresenter() {
        this.mRepository = new UserRepository();
    }

    @Override
    public void load() {
        mRepository.favorites(new ApiCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> data) {
                FavoritesContract.View v = getView();
                if (v != null) {
                    v.onFavorites(data);
                }
            }

            @Override
            public void onFailure(String msg) {
                FavoritesContract.View v = getView();
                if (v != null) {
                    v.showError(msg);
                }
            }
        });
    }
}
