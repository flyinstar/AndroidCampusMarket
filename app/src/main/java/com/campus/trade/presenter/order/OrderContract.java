package com.campus.trade.presenter.order;

import com.campus.trade.base.BaseView;
import com.campus.trade.model.entity.OrderInfo;

import java.util.List;

/**
 * 订单契约
 */
public interface OrderContract {

    interface View extends BaseView {
        void onOrders(List<OrderInfo> orders);

        /** 状态流转成功后刷新 */
        void onOrdersChanged();
    }

    interface Presenter {
        void load(Integer status, String role);

        /** 状态流转；接单时传 meetingTime/meetingPlace */
        void changeStatus(OrderInfo order, int targetStatus,
                          String meetingTime, String meetingPlace);
    }
}
