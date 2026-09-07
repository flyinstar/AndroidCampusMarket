package com.campus.trade.presenter.order;

import com.campus.trade.base.BasePresenter;
import com.campus.trade.model.entity.OrderInfo;
import com.campus.trade.model.repository.OrderRepository;
import com.campus.trade.network.callback.ApiCallback;

import java.util.List;

/**
 * 订单 Presenter
 */
public class OrderPresenter extends BasePresenter<OrderContract.View> implements OrderContract.Presenter {

    private final OrderRepository mRepository;

    public OrderPresenter() {
        this.mRepository = new OrderRepository();
    }

    @Override
    public void load(Integer status, String role) {
        mRepository.list(status, role, new ApiCallback<List<OrderInfo>>() {
            @Override
            public void onSuccess(List<OrderInfo> data) {
                OrderContract.View v = getView();
                if (v != null) {
                    v.onOrders(data);
                }
            }

            @Override
            public void onFailure(String msg) {
                OrderContract.View v = getView();
                if (v != null) {
                    v.showError(msg);
                }
            }
        });
    }

    @Override
    public void changeStatus(OrderInfo order, int targetStatus,
                             String meetingTime, String meetingPlace) {
        mRepository.updateStatus(order.getId(), targetStatus, meetingTime, meetingPlace,
                new ApiCallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        OrderContract.View v = getView();
                        if (v != null) {
                            v.showToast("操作成功");
                            v.onOrdersChanged();
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        OrderContract.View v = getView();
                        if (v != null) {
                            v.showError(msg);
                        }
                    }
                });
    }
}
