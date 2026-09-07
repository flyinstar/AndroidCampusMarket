package com.campus.trade.presenter.product;

import com.campus.trade.base.BaseView;
import com.campus.trade.model.entity.Category;
import com.campus.trade.model.entity.Product;

import java.util.List;

/**
 * 首页/商品列表契约
 */
public interface HomeContract {

    interface View extends BaseView {
        void onCategoriesLoaded(List<Category> categories);

        /** @param products 本次数据（refresh=true 覆盖列表，false 追加）
         *  @param hasMore 是否还有下一页 */
        void onProducts(List<Product> products, boolean refresh, boolean hasMore);
    }

    interface Presenter {
        void loadCategories();

        /** 分类筛选：categoryId 为 null 表示全部 */
        void setCategory(Integer categoryId);

        void setKeyword(String keyword);

        void setSort(String sort);

        void refresh();

        void loadMore();
    }
}
