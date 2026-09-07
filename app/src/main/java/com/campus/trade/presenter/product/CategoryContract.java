package com.campus.trade.presenter.product;

import com.campus.trade.base.BaseView;
import com.campus.trade.model.entity.Category;

import java.util.List;

/**
 * 分类页契约
 */
public interface CategoryContract {

    interface View extends BaseView {
        void onCategories(List<Category> categories);
    }

    interface Presenter {
        void loadCategories();
    }
}
