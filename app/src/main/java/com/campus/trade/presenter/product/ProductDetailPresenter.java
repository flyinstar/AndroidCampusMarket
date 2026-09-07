package com.campus.trade.presenter.product;

import com.campus.trade.base.BasePresenter;
import com.campus.trade.model.entity.Comment;
import com.campus.trade.model.entity.Product;
import com.campus.trade.model.repository.OrderRepository;
import com.campus.trade.model.repository.ProductRepository;
import com.campus.trade.network.callback.ApiCallback;

import java.util.List;

/**
 * 商品详情 Presenter
 */
public class ProductDetailPresenter extends BasePresenter<ProductDetailContract.View>
        implements ProductDetailContract.Presenter {

    private final ProductRepository mProductRepository;
    private final OrderRepository mOrderRepository;
    private Product mProduct;

    public ProductDetailPresenter() {
        this.mProductRepository = new ProductRepository();
        this.mOrderRepository = new OrderRepository();
    }

    @Override
    public void loadDetail(int productId) {
        mProductRepository.detail(productId, new ApiCallback<Product>() {
            @Override
            public void onSuccess(Product data) {
                mProduct = data;
                ProductDetailContract.View v = getView();
                if (v != null) {
                    v.onDetailLoaded(data);
                }
            }

            @Override
            public void onFailure(String msg) {
                ProductDetailContract.View v = getView();
                if (v != null) {
                    v.showError(msg);
                }
            }
        });
    }

    @Override
    public void loadComments(int productId) {
        mProductRepository.comments(productId, 1, 50, new ApiCallback<List<Comment>>() {
            @Override
            public void onSuccess(List<Comment> data) {
                ProductDetailContract.View v = getView();
                if (v != null) {
                    v.onCommentsLoaded(data);
                }
            }

            @Override
            public void onFailure(String msg) {
                // 评论加载失败不打断浏览
            }
        });
    }

    @Override
    public void toggleFavorite(int productId) {
        mProductRepository.toggleFavorite(productId, new ApiCallback<String>() {
            @Override
            public void onSuccess(String data) {
                boolean favorited = data != null && data.contains("已收藏");
                ProductDetailContract.View v = getView();
                if (v != null) {
                    v.onFavoriteChanged(favorited);
                }
            }

            @Override
            public void onFailure(String msg) {
                ProductDetailContract.View v = getView();
                if (v != null) {
                    v.showError(msg);
                }
            }
        });
    }

    @Override
    public void addComment(int productId, String content) {
        mProductRepository.addComment(productId, content, new ApiCallback<Comment>() {
            @Override
            public void onSuccess(Comment data) {
                ProductDetailContract.View v = getView();
                if (v != null) {
                    v.onCommentAdded(data);
                }
            }

            @Override
            public void onFailure(String msg) {
                ProductDetailContract.View v = getView();
                if (v != null) {
                    v.showError(msg);
                }
            }
        });
    }

    @Override
    public void createOrder(int productId, String remark) {
        ProductDetailContract.View v = getView();
        if (v != null) {
            v.showLoading();
        }
        mOrderRepository.create(productId, remark, new ApiCallback<com.campus.trade.model.entity.OrderInfo>() {
            @Override
            public void onSuccess(com.campus.trade.model.entity.OrderInfo data) {
                ProductDetailContract.View view = getView();
                if (view != null) {
                    view.hideLoading();
                    view.onOrderCreated();
                }
            }

            @Override
            public void onFailure(String msg) {
                ProductDetailContract.View view = getView();
                if (view != null) {
                    view.hideLoading();
                    view.showError(msg);
                }
            }
        });
    }
}
