package com.campus.trade.mapper;

import com.campus.trade.entity.User;
import org.apache.ibatis.annotations.Param;

/**
 * 用户 Mapper
 */
public interface UserMapper {

    User findByEmail(@Param("email") String email);

    User findById(@Param("id") Integer id);

    int insert(User user);

    /** 动态更新资料 */
    int updateProfile(User user);

    /** 更新密码（需要重新换盐） */
    int updatePassword(@Param("id") Integer id,
                       @Param("passwordHash") String passwordHash,
                       @Param("salt") String salt);

    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);
}
