package com.campus.trade.controller;

import com.campus.trade.common.BaseResponse;
import com.campus.trade.dto.request.OrderCreateRequest;
import com.campus.trade.dto.request.OrderStatusRequest;
import com.campus.trade.entity.OrderInfo;
import com.campus.trade.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 订单接口
 */
@RestController
@RequestMapping("/v1/orders")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    // 买家下单
    @PostMapping
    public BaseResponse<OrderInfo> create(@Valid @RequestBody OrderCreateRequest req,
                                          HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("currentUserId");
        return BaseResponse.success(orderService.create(userId, req));
    }

    // 我的订单（可按状态、角色过滤：buyer/seller）
    @GetMapping
    public BaseResponse<List<OrderInfo>> list(@RequestParam(required = false) Integer status,
                                              @RequestParam(required = false) String role,
                                              HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("currentUserId");
        return BaseResponse.success(orderService.list(userId, status, role));
    }

    // 订单状态流转（接单/完成/取消）
    @PutMapping("/{id}/status")
    public BaseResponse<String> updateStatus(@PathVariable Integer id,
                                             @Valid @RequestBody OrderStatusRequest req,
                                             HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("currentUserId");
        orderService.updateStatus(userId, id, req);
        return BaseResponse.success("操作成功", "操作成功");
    }
}
