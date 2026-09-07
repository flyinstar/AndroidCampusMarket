package com.campus.trade.presenter.product;

import com.campus.trade.base.BaseView;
import com.campus.trade.model.entity.Comment;
import com.campus.trade.model.entity.Product;

import java.util.List;

/**
 * 商品详情契约（含收藏/评论/下单触发）
 */
public interface ProductDetailContract {

    interface View extends BaseView {
        void onDetailLoaded(Product product);

        void onCommentsLoaded(List<Comment> comments);

        void onCommentAdded(Comment comment);

        void onFavoriteChanged(boolean favorited);

        /** 下单成功 */
        void onOrderCreated();
    }

    interface Presenter {
        void loadDetail(int productId);

        void loadComments(int productId);

        void toggleFavorite(int productId);

        void addComment(int productId, String content);

        /** 下单成功回调由 View.onOrderCreated 完成 */
        void createOrder(int productId, String remark);
    }
}
