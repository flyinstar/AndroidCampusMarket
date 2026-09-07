package com.campus.trade.service.impl;

import com.campus.trade.common.BusinessException;
import com.campus.trade.dto.request.OrderCreateRequest;
import com.campus.trade.dto.request.OrderStatusRequest;
import com.campus.trade.entity.OrderInfo;
import com.campus.trade.entity.Product;
import com.campus.trade.mapper.OrderMapper;
import com.campus.trade.mapper.ProductMapper;
import com.campus.trade.service.IOrderService;
import com.campus.trade.utils.OrderNoGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * 订单服务实现
 */
@Service
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ProductMapper productMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderInfo create(Integer buyerId, OrderCreateRequest request) {
        Product product = productMapper.findById(request.getProductId());
        if (product == null) {
            throw new BusinessException("商品不存在或已删除");
        }
        if (product.getUserId().equals(buyerId)) {
            throw new BusinessException("不能购买自己发布的商品");
        }
        if (product.getStatus() != Product.STATUS_ON) {
            String tip = product.getStatus() == Product.STATUS_OFF ? "商品已下架"
                    : product.getStatus() == Product.STATUS_RESERVED ? "商品已被预约" : "商品已售出";
            throw new BusinessException(tip + "，暂时无法下单");
        }
        if (orderMapper.countActiveByProduct(product.getId(), null) > 0) {
            throw new BusinessException("商品已被其他用户预约");
        }

        OrderInfo order = new OrderInfo();
        order.setOrderNo(OrderNoGenerator.generate(buyerId));
        order.setProductId(product.getId());
        order.setBuyerId(buyerId);
        order.setSellerId(product.getUserId());
        order.setAmount(product.getPrice());
        order.setStatus(OrderInfo.STATUS_PENDING);
        order.setRemark(StringUtils.hasText(request.getRemark()) ? request.getRemark().trim() : null);
        orderMapper.insert(order);

        // 商品置为已预约，防止重复下单
        productMapper.updateStatus(product.getId(), Product.STATUS_RESERVED);
        return order;
    }

    @Override
    public List<OrderInfo> list(Integer userId, Integer status, String role) {
        String r = null;
        if ("buyer".equalsIgnoreCase(role)) {
            r = "buyer";
        } else if ("seller".equalsIgnoreCase(role)) {
            r = "seller";
        }
        List<OrderInfo> list = orderMapper.selectList(userId, status, r, 0, 200);
        return list == null ? Collections.emptyList() : list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Integer userId, Integer orderId, OrderStatusRequest request) {
        OrderInfo order = orderMapper.findById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        boolean isBuyer = order.getBuyerId().equals(userId);
        boolean isSeller = order.getSellerId().equals(userId);
        if (!isBuyer && !isSeller) {
            throw new BusinessException(403, "无权操作该订单");
        }
        Integer newStatus = request.getStatus();
        Integer cur = order.getStatus();
        if (newStatus == null || newStatus < 0 || newStatus > 3) {
            throw new BusinessException("非法的订单状态");
        }

        if (newStatus == OrderInfo.STATUS_TRADING) {
            // 0 -> 1 卖家接单
            if (!isSeller) {
                throw new BusinessException("只有卖家可以接单");
            }
            if (cur != OrderInfo.STATUS_PENDING) {
                throw new BusinessException("当前订单状态不允许接单");
            }
            if (!StringUtils.hasText(request.getMeetingPlace())) {
                throw new BusinessException("请填写约定见面地点");
            }
            if (request.getMeetingTime() == null) {
                throw new BusinessException("请选择约定见面时间");
            }
            orderMapper.updateStatus(orderId, OrderInfo.STATUS_TRADING,
                    request.getMeetingTime(), request.getMeetingPlace().trim());
            return;
        }

        if (newStatus == OrderInfo.STATUS_DONE) {
            // 0/1 -> 2 交易完成（买卖双方均可发起）
            if (cur != OrderInfo.STATUS_PENDING && cur != OrderInfo.STATUS_TRADING) {
                throw new BusinessException("当前订单状态不允许完成交易");
            }
            orderMapper.cancelActiveByProduct(order.getProductId(), orderId);
            orderMapper.updateStatus(orderId, OrderInfo.STATUS_DONE, null, null);
            productMapper.updateStatus(order.getProductId(), Product.STATUS_SOLD);
            return;
        }

        if (newStatus == OrderInfo.STATUS_CANCELED) {
            // 0/1 -> 3 取消订单（双方均可）
            if (cur != OrderInfo.STATUS_PENDING && cur != OrderInfo.STATUS_TRADING) {
                throw new BusinessException("当前订单状态不允许取消");
            }
            orderMapper.updateStatus(orderId, OrderInfo.STATUS_CANCELED, null, null);
            Product product = productMapper.findById(order.getProductId());
            if (product != null && product.getStatus() == Product.STATUS_RESERVED) {
                productMapper.updateStatus(product.getId(), Product.STATUS_ON);
            }
            return;
        }

        throw new BusinessException("非法的订单状态变更");
    }
}
