package com.campus.trade.mapper;

import com.campus.trade.entity.Category;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分类 Mapper
 */
public interface CategoryMapper {

    List<Category> listAll();

    Category findById(@Param("id") Integer id);
}
