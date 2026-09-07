package com.campus.trade.presenter.product;

import com.campus.trade.base.BaseView;
import com.campus.trade.model.entity.Product;

import java.util.List;

/**
 * 我的发布契约
 */
public interface MyProductsContract {

    interface View extends BaseView {
        void onProducts(List<Product> products);

        /** 上下架/删除后刷新列表 */
        void onProductChanged();
    }

    interface Presenter {
        void load(Integer status);

        void changeStatus(Product product, int targetStatus);

        void delete(Product product);
    }
}
