package com.campus.trade.controller;

import com.campus.trade.common.BaseResponse;
import com.campus.trade.dto.request.LoginRequest;
import com.campus.trade.dto.request.RegisterRequest;
import com.campus.trade.entity.User;
import com.campus.trade.service.IUserService;
import com.campus.trade.utils.JwtUtil;
import com.campus.trade.utils.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 认证接口：注册 / 登录
 */
@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    @Autowired
    private IUserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public BaseResponse<String> register(@Valid @RequestBody RegisterRequest req) {
        userService.register(req);
        return BaseResponse.success("注册成功", null);
    }

    @PostMapping("/login")
    public BaseResponse<String> login(@Valid @RequestBody LoginRequest req) {
        User user = userService.findByEmail(req.getEmail());
        if (user == null) {
            return BaseResponse.error("用户不存在");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            return BaseResponse.error("账号已被禁用");
        }
        String inputHash = PasswordUtil.encryptPassword(req.getPassword(), user.getSalt());
        if (!inputHash.equals(user.getPasswordHash())) {
            return BaseResponse.error("密码错误");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        return BaseResponse.success(token);
    }
}
