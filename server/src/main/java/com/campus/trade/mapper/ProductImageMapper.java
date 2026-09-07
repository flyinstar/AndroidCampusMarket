package com.campus.trade.mapper;

import com.campus.trade.entity.ProductImage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品图片 Mapper
 */
public interface ProductImageMapper {

    int insertBatch(@Param("list") List<ProductImage> list);

    List<ProductImage> selectByProductId(@Param("productId") Integer productId);

    int deleteByProductId(@Param("productId") Integer productId);
}
