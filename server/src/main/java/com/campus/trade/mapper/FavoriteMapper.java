package com.campus.trade.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * 收藏 Mapper
 */
public interface FavoriteMapper {

    int insert(@Param("userId") Integer userId, @Param("productId") Integer productId);

    int deleteByUserProduct(@Param("userId") Integer userId, @Param("productId") Integer productId);

    int exists(@Param("userId") Integer userId, @Param("productId") Integer productId);
}
