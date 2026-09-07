package com.campus.trade.controller;

import com.campus.trade.common.BaseResponse;
import com.campus.trade.entity.Category;
import com.campus.trade.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类接口
 */
@RestController
@RequestMapping("/v1/categories")
public class CategoryController {

    @Autowired
    private CategoryMapper categoryMapper;

    @GetMapping
    public BaseResponse<List<Category>> list() {
        return BaseResponse.success(categoryMapper.listAll());
    }
}
