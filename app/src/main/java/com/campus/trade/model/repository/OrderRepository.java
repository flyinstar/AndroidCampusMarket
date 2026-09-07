package com.campus.trade.model.repository;

import com.campus.trade.model.entity.OrderInfo;
import com.campus.trade.network.ApiClient;
import com.campus.trade.network.ApiRequest;
import com.campus.trade.network.callback.ApiCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单数据仓库
 */
public class OrderRepository {

    public void list(Integer status, String role, ApiCallback<List<OrderInfo>> callback) {
        ApiRequest.enqueue(ApiClient.getApiService().getOrders(status, role), callback);
    }

    public void create(int productId, String remark, ApiCallback<OrderInfo> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("productId", productId);
        params.put("remark", remark);
        ApiRequest.enqueue(ApiClient.getApiService().createOrder(params), callback);
    }

    /**
     * 状态流转：status=1(接单) 需带 meetingTime("yyyy-MM-dd'T'HH:mm:ss")/meetingPlace；
     * 完成/取消不带见面信息。
     */
    public void updateStatus(int orderId, int status, String meetingTime, String meetingPlace,
                             ApiCallback<String> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("status", status);
        if (meetingTime != null) {
            params.put("meetingTime", meetingTime);
        }
        if (meetingPlace != null) {
            params.put("meetingPlace", meetingPlace);
        }
        ApiRequest.enqueue(ApiClient.getApiService().updateOrderStatus(orderId, params), callback);
    }
}
