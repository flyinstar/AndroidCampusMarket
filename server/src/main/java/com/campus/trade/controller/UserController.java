package com.campus.trade.controller;

import com.campus.trade.common.BaseResponse;
import com.campus.trade.dto.request.PasswordChangeRequest;
import com.campus.trade.dto.request.UserUpdateRequest;
import com.campus.trade.entity.Product;
import com.campus.trade.entity.User;
import com.campus.trade.service.IProductService;
import com.campus.trade.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 用户中心：资料 / 我的发布 / 我的收藏 / 改密码
 */
@RestController
@RequestMapping("/v1/user")
public class UserController {

    @Autowired
    private IUserService userService;
    @Autowired
    private IProductService productService;

    @GetMapping("/profile")
    public BaseResponse<User> profile(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("currentUserId");
        return BaseResponse.success(userService.getProfile(userId));
    }

    @PutMapping("/profile")
    public BaseResponse<User> updateProfile(@Valid @RequestBody UserUpdateRequest req,
                                            HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("currentUserId");
        return BaseResponse.success(userService.updateProfile(userId, req));
    }

    @PutMapping("/password")
    public BaseResponse<String> changePassword(@Valid @RequestBody PasswordChangeRequest req,
                                               HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("currentUserId");
        userService.changePassword(userId, req);
        return BaseResponse.success("密码修改成功", null);
    }

    @GetMapping("/products")
    public BaseResponse<List<Product>> myProducts(@RequestParam(required = false) Integer status,
                                                  HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("currentUserId");
        return BaseResponse.success(productService.myProducts(userId, status));
    }

    @GetMapping("/favorites")
    public BaseResponse<List<Product>> favorites(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("currentUserId");
        return BaseResponse.success(productService.favorites(userId));
    }
}
