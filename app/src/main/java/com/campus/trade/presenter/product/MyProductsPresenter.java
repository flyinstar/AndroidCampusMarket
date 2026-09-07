package com.campus.trade.presenter.product;

import com.campus.trade.base.BasePresenter;
import com.campus.trade.model.entity.Product;
import com.campus.trade.model.repository.ProductRepository;
import com.campus.trade.model.repository.UserRepository;
import com.campus.trade.network.callback.ApiCallback;

import java.util.List;

/**
 * 我的发布 Presenter
 */
public class MyProductsPresenter extends BasePresenter<MyProductsContract.View>
        implements MyProductsContract.Presenter {

    private final UserRepository mUserRepository;
    private final ProductRepository mProductRepository;

    public MyProductsPresenter() {
        this.mUserRepository = new UserRepository();
        this.mProductRepository = new ProductRepository();
    }

    @Override
    public void load(Integer status) {
        mUserRepository.myProducts(status, new ApiCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> data) {
                MyProductsContract.View v = getView();
                if (v != null) {
                    v.onProducts(data);
                }
            }

            @Override
            public void onFailure(String msg) {
                MyProductsContract.View v = getView();
                if (v != null) {
                    v.showError(msg);
                }
            }
        });
    }

    @Override
    public void changeStatus(Product product, int targetStatus) {
        mProductRepository.updateStatus(product.getId(), targetStatus,
                new ApiCallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        MyProductsContract.View v = getView();
                        if (v != null) {
                            v.showToast(data == null ? "操作成功" : data);
                            v.onProductChanged();
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        MyProductsContract.View v = getView();
                        if (v != null) {
                            v.showError(msg);
                        }
                    }
                });
    }

    @Override
    public void delete(Product product) {
        mProductRepository.delete(product.getId(), new ApiCallback<String>() {
            @Override
            public void onSuccess(String data) {
                MyProductsContract.View v = getView();
                if (v != null) {
                    v.showToast("删除成功");
                    v.onProductChanged();
                }
            }

            @Override
            public void onFailure(String msg) {
                MyProductsContract.View v = getView();
                if (v != null) {
                    v.showError(msg);
                }
            }
        });
    }
}
