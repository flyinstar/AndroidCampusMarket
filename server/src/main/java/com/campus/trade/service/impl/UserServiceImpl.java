package com.campus.trade.service.impl;

import com.campus.trade.common.BusinessException;
import com.campus.trade.dto.request.PasswordChangeRequest;
import com.campus.trade.dto.request.RegisterRequest;
import com.campus.trade.dto.request.UserUpdateRequest;
import com.campus.trade.entity.User;
import com.campus.trade.mapper.UserMapper;
import com.campus.trade.service.IUserService;
import com.campus.trade.utils.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 用户服务实现
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User findByEmail(String email) {
        if (email == null) {
            return null;
        }
        return userMapper.findByEmail(email.trim().toLowerCase());
    }

    @Override
    public User findById(Integer id) {
        return userMapper.findById(id);
    }

    @Override
    public User register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (findByEmail(email) != null) {
            throw new BusinessException("该邮箱已注册");
        }
        String salt = PasswordUtil.generateSalt();
        String passwordHash = PasswordUtil.encryptPassword(request.getPassword(), salt);

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordHash);
        user.setSalt(salt);
        String nickname = request.getNickname();
        user.setNickname(StringUtils.hasText(nickname) ? nickname.trim() : "校园用户");
        user.setAvatar(null);
        userMapper.insert(user);
        return user;
    }

    @Override
    public User getProfile(Integer userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    @Override
    public User updateProfile(Integer userId, UserUpdateRequest request) {
        getProfile(userId);
        User update = new User();
        update.setId(userId);
        update.setNickname(trimToNull(request.getNickname()));
        update.setAvatar(trimToNull(request.getAvatar()));
        update.setPhone(trimToNull(request.getPhone()));
        update.setGrade(trimToNull(request.getGrade()));
        update.setMajor(trimToNull(request.getMajor()));
        userMapper.updateProfile(update);
        return getProfile(userId);
    }

    @Override
    public void changePassword(Integer userId, PasswordChangeRequest request) {
        User user = getProfile(userId);
        String inputHash = PasswordUtil.encryptPassword(request.getOldPassword(), user.getSalt());
        if (!inputHash.equals(user.getPasswordHash())) {
            throw new BusinessException("原密码错误");
        }
        String newSalt = PasswordUtil.generateSalt();
        String newHash = PasswordUtil.encryptPassword(request.getNewPassword(), newSalt);
        userMapper.updatePassword(userId, newHash, newSalt);
    }

    private String trimToNull(String s) {
        return StringUtils.hasText(s) ? s.trim() : null;
    }
}
