package com.campus.trade.presenter.product;

import com.campus.trade.base.BasePresenter;
import com.campus.trade.model.entity.Category;
import com.campus.trade.model.repository.ProductRepository;
import com.campus.trade.network.callback.ApiCallback;

import java.util.List;

/**
 * 分类页 Presenter
 */
public class CategoryPresenter extends BasePresenter<CategoryContract.View>
        implements CategoryContract.Presenter {

    private final ProductRepository mRepository;

    public CategoryPresenter() {
        this.mRepository = new ProductRepository();
    }

    @Override
    public void loadCategories() {
        mRepository.categories(new ApiCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> data) {
                CategoryContract.View v = getView();
                if (v != null) {
                    v.onCategories(data);
                }
            }

            @Override
            public void onFailure(String msg) {
                CategoryContract.View v = getView();
                if (v != null) {
                    v.showError(msg);
                }
            }
        });
    }
}
