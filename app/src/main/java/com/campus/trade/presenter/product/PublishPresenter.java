package com.campus.trade.presenter.product;

import com.campus.trade.base.BasePresenter;
import com.campus.trade.model.entity.Category;
import com.campus.trade.model.entity.Product;
import com.campus.trade.model.repository.ProductRepository;
import com.campus.trade.network.callback.ApiCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 发布商品 Presenter：图片逐张压缩上传 → 提交商品
 */
public class PublishPresenter extends BasePresenter<PublishContract.View> implements PublishContract.Presenter {

    private final ProductRepository mRepository;

    public PublishPresenter() {
        this.mRepository = new ProductRepository();
    }

    @Override
    public void loadCategories() {
        mRepository.categories(new ApiCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> data) {
                PublishContract.View v = getView();
                if (v != null) {
                    v.onCategories(data);
                }
            }

            @Override
            public void onFailure(String msg) {
                PublishContract.View v = getView();
                if (v != null) {
                    v.showError(msg);
                }
            }
        });
    }

    @Override
    public void publish(List<String> localImagePaths, String title, String description,
                        double price, int categoryId, int condition) {
        if (localImagePaths == null || localImagePaths.isEmpty()) {
            if (getView() != null) {
                getView().showError("请至少上传一张图片");
            }
            return;
        }
        PublishContract.View view = getView();
        if (view != null) {
            view.showLoading();
        }
        final List<String> uploadedUrls = new ArrayList<>();
        uploadSequentially(localImagePaths, 0, uploadedUrls, title, description, price, categoryId, condition);
    }

    private void uploadSequentially(List<String> paths, int index, List<String> uploadedUrls,
                                    String title, String description, double price,
                                    int categoryId, int condition) {
        if (index >= paths.size()) {
            doPublish(uploadedUrls, title, description, price, categoryId, condition);
            return;
        }
        final PublishContract.View view = getView();
        if (view != null) {
            view.onUploadProgress(index + 1, paths.size());
        }
        File file = new File(paths.get(index));
        if (!file.exists()) {
            fail("图片文件不存在: " + paths.get(index));
            return;
        }
        mRepository.uploadImage(file, new ApiCallback<String>() {
            @Override
            public void onSuccess(String url) {
                uploadedUrls.add(url);
                uploadSequentially(paths, index + 1, uploadedUrls,
                        title, description, price, categoryId, condition);
            }

            @Override
            public void onFailure(String msg) {
                fail("第" + (index + 1) + "张图片上传失败: " + msg);
            }
        });
    }

    private void doPublish(List<String> urls, String title, String description,
                           double price, int categoryId, int condition) {
        mRepository.publish(title, description, price, categoryId, condition, urls,
                new ApiCallback<Product>() {
                    @Override
                    public void onSuccess(Product data) {
                        PublishContract.View view = getView();
                        if (view != null) {
                            view.hideLoading();
                            view.onPublishSuccess(data);
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        fail(msg);
                    }
                });
    }

    private void fail(String msg) {
        PublishContract.View view = getView();
        if (view != null) {
            view.hideLoading();
            view.showError(msg);
        }
    }
}
