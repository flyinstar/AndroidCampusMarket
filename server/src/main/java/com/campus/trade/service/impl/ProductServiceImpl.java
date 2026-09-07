package com.campus.trade.service.impl;

import com.campus.trade.common.BusinessException;
import com.campus.trade.dto.request.CommentCreateRequest;
import com.campus.trade.dto.request.ProductPublishRequest;
import com.campus.trade.dto.response.ProductListResponse;
import com.campus.trade.entity.Comment;
import com.campus.trade.entity.Product;
import com.campus.trade.entity.ProductImage;
import com.campus.trade.entity.User;
import com.campus.trade.mapper.CommentMapper;
import com.campus.trade.mapper.FavoriteMapper;
import com.campus.trade.mapper.OrderMapper;
import com.campus.trade.mapper.ProductImageMapper;
import com.campus.trade.mapper.ProductMapper;
import com.campus.trade.mapper.UserMapper;
import com.campus.trade.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 商品服务实现
 */
@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductImageMapper productImageMapper;
    @Autowired
    private FavoriteMapper favoriteMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Product publish(Integer userId, ProductPublishRequest request) {
        if (CollectionUtils.isEmpty(request.getImageUrls())) {
            throw new BusinessException("请至少上传一张图片");
        }
        List<String> urls = new ArrayList<>();
        for (String url : request.getImageUrls()) {
            if (StringUtils.hasText(url)) {
                urls.add(url.trim());
            }
        }
        if (urls.isEmpty()) {
            throw new BusinessException("请至少上传一张图片");
        }

        Product product = new Product();
        product.setTitle(request.getTitle().trim());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setUserId(userId);
        product.setCategoryId(request.getCategoryId());
        int condition = request.getCondition() == null ? 3 : request.getCondition();
        if (condition < 1 || condition > 4) {
            condition = 3;
        }
        product.setCondition(condition);
        product.setStatus(Product.STATUS_ON);
        product.setViewCount(0);
        product.setFavoriteCount(0);
        product.setLatitude(request.getLatitude());
        product.setLongitude(request.getLongitude());
        productMapper.insert(product);

        List<ProductImage> images = new ArrayList<>();
        for (int i = 0; i < urls.size(); i++) {
            ProductImage img = new ProductImage();
            img.setProductId(product.getId());
            img.setImageUrl(urls.get(i));
            img.setSortOrder(i);
            images.add(img);
        }
        productImageMapper.insertBatch(images);
        product.setImageUrls(urls);
        return product;
    }

    @Override
    public ProductListResponse list(Integer categoryId, String keyword, int page, int size, String sort) {
        int p = Math.max(page, 1);
        int s = Math.min(Math.max(size, 1), 50);
        String kw = StringUtils.hasText(keyword) ? keyword.trim() : null;
        String order = "new".equalsIgnoreCase(sort) ? "new" : "hot";
        int offset = (p - 1) * s;

        long total = productMapper.countPage(categoryId, kw);
        List<Product> list = productMapper.selectPage(categoryId, kw, order, offset, s);
        return new ProductListResponse(total, p, s, list == null ? Collections.emptyList() : list);
    }

    @Override
    public Product detail(Integer id, Integer currentUserId) {
        Product product = productMapper.findById(id);
        if (product == null) {
            throw new BusinessException("商品不存在或已删除");
        }
        productMapper.incrementView(id);
        product.setViewCount(product.getViewCount() == null ? 1 : product.getViewCount() + 1);
        product.setImageUrls(loadImageUrls(id));
        if (currentUserId != null) {
            product.setIsFavorite(favoriteMapper.exists(currentUserId, id) > 0);
        }
        return product;
    }

    @Override
    public List<Product> myProducts(Integer userId, Integer status) {
        List<Product> list = productMapper.selectByUser(userId, status);
        fillImageUrls(list);
        return list;
    }

    @Override
    public List<Product> favorites(Integer userId) {
        List<Product> list = productMapper.selectFavorites(userId);
        fillImageUrls(list);
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleFavorite(Integer userId, Integer productId) {
        Product product = requireProduct(productId);
        if (product.getUserId().equals(userId)) {
            throw new BusinessException("不能收藏自己发布的商品");
        }
        boolean exists = favoriteMapper.exists(userId, productId) > 0;
        if (exists) {
            favoriteMapper.deleteByUserProduct(userId, productId);
            productMapper.subFavoriteCount(productId);
            return false;
        }
        favoriteMapper.insert(userId, productId);
        productMapper.addFavoriteCount(productId);
        return true;
    }

    @Override
    public boolean isFavorited(Integer userId, Integer productId) {
        requireProduct(productId);
        return favoriteMapper.exists(userId, productId) > 0;
    }

    @Override
    public void updateStatus(Integer userId, Integer productId, Integer status) {
        Product product = requireProduct(productId);
        requireOwner(userId, product);
        if (status == null || (status != Product.STATUS_OFF && status != Product.STATUS_ON)) {
            throw new BusinessException("仅支持上架(1)/下架(0)状态变更");
        }
        if (status == Product.STATUS_ON && product.getStatus() == Product.STATUS_RESERVED) {
            throw new BusinessException("商品已被预约，无法上架");
        }
        productMapper.updateStatus(productId, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Integer userId, Integer productId) {
        Product product = requireProduct(productId);
        requireOwner(userId, product);
        if (orderMapper.countByProduct(productId) > 0) {
            throw new BusinessException("该商品存在订单记录，无法删除，请使用下架");
        }
        productMapper.deleteById(productId);
    }

    @Override
    public Comment addComment(Integer userId, Integer productId, CommentCreateRequest request) {
        requireProduct(productId);
        Comment comment = new Comment();
        comment.setProductId(productId);
        comment.setUserId(userId);
        comment.setContent(request.getContent().trim());
        comment.setCreatedAt(LocalDateTime.now());
        commentMapper.insert(comment);

        User user = userMapper.findById(userId);
        if (user != null) {
            comment.setUserName(user.getNickname());
            comment.setUserAvatar(user.getAvatar());
        }
        return comment;
    }

    @Override
    public List<Comment> comments(Integer productId, int page, int size) {
        requireProduct(productId);
        int p = Math.max(page, 1);
        int s = Math.min(Math.max(size, 1), 50);
        int offset = (p - 1) * s;
        List<Comment> list = commentMapper.selectByProduct(productId, offset, s);
        return list == null ? Collections.emptyList() : list;
    }

    // ---------- 私有工具 ----------

    private Product requireProduct(Integer productId) {
        Product product = productMapper.findById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在或已删除");
        }
        return product;
    }

    private void requireOwner(Integer userId, Product product) {
        if (!product.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作该商品");
        }
    }

    private List<String> loadImageUrls(Integer productId) {
        List<ProductImage> images = productImageMapper.selectByProductId(productId);
        List<String> urls = new ArrayList<>();
        if (images != null) {
            for (ProductImage img : images) {
                urls.add(img.getImageUrl());
            }
        }
        return urls;
    }

    private void fillImageUrls(List<Product> list) {
        if (list == null) {
            return;
        }
        for (Product product : list) {
            product.setImageUrls(loadImageUrls(product.getId()));
        }
    }
}
