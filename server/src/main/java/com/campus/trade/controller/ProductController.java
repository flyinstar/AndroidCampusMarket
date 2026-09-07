package com.campus.trade.controller;

import com.campus.trade.common.BaseResponse;
import com.campus.trade.dto.request.CommentCreateRequest;
import com.campus.trade.dto.request.ProductPublishRequest;
import com.campus.trade.dto.response.ProductListResponse;
import com.campus.trade.entity.Comment;
import com.campus.trade.entity.Product;
import com.campus.trade.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 商品核心接口
 */
@RestController
@RequestMapping("/v1/products")
public class ProductController {

    @Autowired
    private IProductService productService;

    // 发布商品
    @PostMapping
    public BaseResponse<Product> publish(@Valid @RequestBody ProductPublishRequest req,
                                         HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("currentUserId");
        return BaseResponse.success(productService.publish(userId, req));
    }

    // 商品列表（分页、分类、关键词、排序）
    @GetMapping
    public BaseResponse<ProductListResponse> list(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "hot") String sort) {
        return BaseResponse.success(productService.list(categoryId, keyword, page, size, sort));
    }

    // 商品详情
    @GetMapping("/{id}")
    public BaseResponse<Product> detail(@PathVariable Integer id, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("currentUserId");
        return BaseResponse.success(productService.detail(id, userId));
    }

    // 是否已收藏
    @GetMapping("/{id}/favorited")
    public BaseResponse<Boolean> favorited(@PathVariable Integer id, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("currentUserId");
        return BaseResponse.success(productService.isFavorited(userId, id));
    }

    // 收藏 / 取消收藏
    @PostMapping("/{id}/favorite")
    public BaseResponse<String> toggleFavorite(@PathVariable Integer id, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("currentUserId");
        boolean favorited = productService.toggleFavorite(userId, id);
        return BaseResponse.success(favorited ? "已收藏" : "已取消收藏", favorited ? "已收藏" : "已取消收藏");
    }

    // 更新商品状态（上架/下架，仅卖家）
    @PutMapping("/{id}/status")
    public BaseResponse<String> updateStatus(@PathVariable Integer id,
                                             @RequestParam Integer status,
                                             HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("currentUserId");
        productService.updateStatus(userId, id, status);
        return BaseResponse.success("更新成功", "更新成功");
    }

    // 删除商品（仅卖家，无订单记录时）
    @DeleteMapping("/{id}")
    public BaseResponse<String> delete(@PathVariable Integer id, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("currentUserId");
        productService.delete(userId, id);
        return BaseResponse.success("删除成功", "删除成功");
    }

    // 商品评论列表
    @GetMapping("/{id}/comments")
    public BaseResponse<List<Comment>> comments(@PathVariable Integer id,
                                                @RequestParam(defaultValue = "1") Integer page,
                                                @RequestParam(defaultValue = "20") Integer size) {
        return BaseResponse.success(productService.comments(id, page, size));
    }

    // 发表评论
    @PostMapping("/{id}/comments")
    public BaseResponse<Comment> addComment(@PathVariable Integer id,
                                            @Valid @RequestBody CommentCreateRequest req,
                                            HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("currentUserId");
        return BaseResponse.success(productService.addComment(userId, id, req));
    }
}
