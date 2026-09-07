package com.campus.trade.mapper;

import com.campus.trade.entity.Comment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论 Mapper
 */
public interface CommentMapper {

    int insert(Comment comment);

    /** 商品评论列表（新→旧），联表带出昵称头像 */
    List<Comment> selectByProduct(@Param("productId") Integer productId,
                                  @Param("offset") int offset,
                                  @Param("size") int size);

    long countByProduct(@Param("productId") Integer productId);
}
