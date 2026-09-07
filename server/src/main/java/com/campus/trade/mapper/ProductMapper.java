package com.campus.trade.mapper;

import com.campus.trade.entity.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品 Mapper
 */
public interface ProductMapper {

    int insert(Product product);

    Product findById(@Param("id") Integer id);

    /** 商品分页浏览：仅上架中(status=1)，支持分类/关键词筛选与排序 */
    List<Product> selectPage(@Param("categoryId") Integer categoryId,
                             @Param("keyword") String keyword,
                             @Param("sort") String sort,
                             @Param("offset") int offset,
                             @Param("size") int size);

    long countPage(@Param("categoryId") Integer categoryId,
                   @Param("keyword") String keyword);

    /** 我的发布（可按状态过滤） */
    List<Product> selectByUser(@Param("userId") Integer userId,
                               @Param("status") Integer status);

    /** 我的收藏（按收藏时间倒序） */
    List<Product> selectFavorites(@Param("userId") Integer userId);

    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    int deleteById(@Param("id") Integer id);

    int incrementView(@Param("id") Integer id);

    int addFavoriteCount(@Param("id") Integer id);

    int subFavoriteCount(@Param("id") Integer id);
}
