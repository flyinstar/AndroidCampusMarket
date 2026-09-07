package com.campus.trade.service;

import com.campus.trade.dto.request.CommentCreateRequest;
import com.campus.trade.dto.request.ProductPublishRequest;
import com.campus.trade.dto.response.ProductListResponse;
import com.campus.trade.entity.Comment;
import com.campus.trade.entity.Product;

import java.util.List;

/**
 * 商品服务
 */
public interface IProductService {

    /** 发布商品（写入商品与图片） */
    Product publish(Integer userId, ProductPublishRequest request);

    /** 商品分页浏览 */
    ProductListResponse list(Integer categoryId, String keyword, int page, int size, String sort);

    /** 商品详情（浏览量+1）；currentUserId 非空时计算是否已收藏 */
    Product detail(Integer id, Integer currentUserId);

    /** 我的发布 */
    List<Product> myProducts(Integer userId, Integer status);

    /** 我的收藏 */
    List<Product> favorites(Integer userId);

    /** 收藏/取消收藏，返回操作后是否已收藏 */
    boolean toggleFavorite(Integer userId, Integer productId);

    boolean isFavorited(Integer userId, Integer productId);

    /** 卖家更新商品上下架状态 */
    void updateStatus(Integer userId, Integer productId, Integer status);

    /** 卖家删除商品（无历史订单时允许） */
    void delete(Integer userId, Integer productId);

    Comment addComment(Integer userId, Integer productId, CommentCreateRequest request);

    List<Comment> comments(Integer productId, int page, int size);
}
