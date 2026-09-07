package com.campus.trade.mapper;

import com.campus.trade.entity.OrderInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单 Mapper
 */
public interface OrderMapper {

    int insert(OrderInfo order);

    OrderInfo findById(@Param("id") Integer id);

    /** 该商品下进行中的订单数量（status in 0,1） */
    int countActiveByProduct(@Param("productId") Integer productId, @Param("excludeId") Integer excludeId);

    /** 该商品是否已存在任何订单（含历史），用于限制删除 */
    int countByProduct(@Param("productId") Integer productId);

    /** 我的订单列表；role: buyer / seller / null-全部；status 可为空 */
    List<OrderInfo> selectList(@Param("userId") Integer userId,
                               @Param("status") Integer status,
                               @Param("role") String role,
                               @Param("offset") int offset,
                               @Param("size") int size);

    long countList(@Param("userId") Integer userId,
                   @Param("status") Integer status,
                   @Param("role") String role);

    /** 更新状态与见面信息（接单时携带 meetingTime/meetingPlace） */
    int updateStatus(@Param("id") Integer id,
                     @Param("status") Integer status,
                     @Param("meetingTime") java.time.LocalDateTime meetingTime,
                     @Param("meetingPlace") String meetingPlace);

    /** 取消该商品除 excludeId 外的其他进行中订单（商品成交时触发） */
    int cancelActiveByProduct(@Param("productId") Integer productId, @Param("excludeId") Integer excludeId);
}
