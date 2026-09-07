package com.campus.trade.service;

import com.campus.trade.dto.request.PasswordChangeRequest;
import com.campus.trade.dto.request.RegisterRequest;
import com.campus.trade.dto.request.UserUpdateRequest;
import com.campus.trade.entity.User;

/**
 * 用户服务
 */
public interface IUserService {

    User findByEmail(String email);

    User findById(Integer id);

    /** 注册：邮箱唯一校验、加盐加密、保存 */
    User register(RegisterRequest request);

    User getProfile(Integer userId);

    User updateProfile(Integer userId, UserUpdateRequest request);

    void changePassword(Integer userId, PasswordChangeRequest request);
}
