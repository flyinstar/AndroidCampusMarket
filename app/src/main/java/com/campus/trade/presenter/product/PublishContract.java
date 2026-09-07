package com.campus.trade.presenter.product;

import com.campus.trade.base.BaseView;
import com.campus.trade.model.entity.Category;
import com.campus.trade.model.entity.Product;

import java.util.List;

/**
 * 发布商品契约
 */
public interface PublishContract {

    interface View extends BaseView {
        void onCategories(List<Category> categories);

        /** 上传进度提示 */
        void onUploadProgress(int current, int total);

        void onPublishSuccess(Product product);
    }

    interface Presenter {
        void loadCategories();

        /** 依次上传图片再发布；失败会通过 view.showError 反馈 */
        void publish(List<String> localImagePaths, String title, String description,
                     double price, int categoryId, int condition);
    }
}
