package com.campus.trade.service;

import com.campus.trade.dto.request.OrderCreateRequest;
import com.campus.trade.dto.request.OrderStatusRequest;
import com.campus.trade.entity.OrderInfo;

import java.util.List;

/**
 * 订单服务
 */
public interface IOrderService {

    /** 买家下单：校验商品上架中且无进行中订单，成功后商品置为已预约 */
    OrderInfo create(Integer buyerId, OrderCreateRequest request);

    /** 我的订单（status/role 可空过滤） */
    List<OrderInfo> list(Integer userId, Integer status, String role);

    /** 订单状态流转 */
    void updateStatus(Integer userId, Integer orderId, OrderStatusRequest request);
}
