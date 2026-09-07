package com.campus.trade.ui.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.campus.trade.R;
import com.campus.trade.base.BaseActivity;
import com.campus.trade.model.entity.OrderInfo;
import com.campus.trade.presenter.order.OrderContract;
import com.campus.trade.presenter.order.OrderPresenter;
import com.campus.trade.ui.adapter.OrderAdapter;
import com.campus.trade.utils.SharedPrefUtils;

import java.util.List;

/**
 * 我的订单：全部 / 买到的 / 卖出的，支持接单/完成/取消
 */
public class OrdersActivity extends BaseActivity<OrderContract.View, OrderPresenter>
        implements OrderContract.View, OrderAdapter.Listener {

    private OrderAdapter mAdapter;
    private SwipeRefreshLayout srl;
    private View llEmpty;
    private String mRole; // null/buyer/seller
    private TextView tvAll;
    private TextView tvBuyer;
    private TextView tvSeller;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_orders;
    }

    @Override
    protected OrderPresenter createPresenter() {
        return new OrderPresenter();
    }

    @Override
    protected void initViews() {
        tvAll = findViewById(R.id.tv_role_all);
        tvBuyer = findViewById(R.id.tv_role_buyer);
        tvSeller = findViewById(R.id.tv_role_seller);
        srl = findViewById(R.id.srl_list);
        llEmpty = findViewById(R.id.ll_empty);
        RecyclerView rv = findViewById(R.id.rv_list);
        rv.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new OrderAdapter();
        mAdapter.setListener(this);
        mAdapter.setMyUserId(SharedPrefUtils.getUserId(this));
        rv.setAdapter(mAdapter);
    }

    @Override
    protected void initListeners() {
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        srl.setOnRefreshListener(() -> mPresenter.load(null, mRole));
        tvAll.setOnClickListener(v -> selectRole(null, tvAll));
        tvBuyer.setOnClickListener(v -> selectRole("buyer", tvBuyer));
        tvSeller.setOnClickListener(v -> selectRole("seller", tvSeller));
    }

    private void selectRole(String role, TextView selected) {
        mRole = role;
        setStyle(tvAll, role == null);
        setStyle(tvBuyer, "buyer".equals(role));
        setStyle(tvSeller, "seller".equals(role));
        mPresenter.load(null, role);
    }

    private void setStyle(TextView tv, boolean selected) {
        tv.setTextColor(androidx.core.content.ContextCompat.getColor(this, selected ? R.color.primary : R.color.text_second));
        tv.setTypeface(null, selected ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
    }

    @Override
    protected void initData() {
        mPresenter.load(null, mRole);
    }

    @Override
    public void onOrders(List<OrderInfo> orders) {
        srl.setRefreshing(false);
        mAdapter.setData(orders);
        llEmpty.setVisibility(mAdapter.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onOrdersChanged() {
        mPresenter.load(null, mRole);
    }

    @Override
    public void onAction(OrderInfo order, String action) {
        switch (action) {
            case "accept":
                showAcceptDialog(order);
                break;
            case "complete":
                confirm("确认完成交易", "请确认已与对方线下完成交易？", () ->
                        mPresenter.changeStatus(order, OrderInfo.STATUS_DONE, null, null));
                break;
            case "cancel":
                confirm("取消订单", "确定取消该订单吗？取消后商品将重新上架。", () ->
                        mPresenter.changeStatus(order, OrderInfo.STATUS_CANCELED, null, null));
                break;
            default:
                break;
        }
    }

    private void showAcceptDialog(OrderInfo order) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (20 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad, 12, pad, 0);

        final EditText etPlace = new EditText(this);
        etPlace.setHint("约定见面地点（必填），如：图书馆门口");
        final EditText etTime = new EditText(this);
        etTime.setHint("约定见面时间（必填），格式：2026-09-08 12:00");
        etTime.setInputType(InputType.TYPE_CLASS_DATETIME);
        layout.addView(etPlace);
        layout.addView(etTime);

        new AlertDialog.Builder(this)
                .setTitle("接单 · 约定线下交易")
                .setView(layout)
                .setNegativeButton("取消", null)
                .setPositiveButton("确认接单", (d, w) -> {
                    String place = etPlace.getText().toString().trim();
                    String timeText = etTime.getText().toString().trim();
                    if (place.isEmpty() || timeText.isEmpty()) {
                        showToast("请填写见面地点和时间");
                        return;
                    }
                    // 服务端期望 ISO 格式 yyyy-MM-dd'T'HH:mm:ss
                    String iso = timeText.replace(' ', 'T');
                    if (!iso.contains(":")) {
                        showToast("时间格式错误，示例：2026-09-08 12:00");
                        return;
                    }
                    int colonCount = iso.length() - iso.replace(":", "").length();
                    if (colonCount == 1) {
                        iso = iso + ":00";
                    }
                    mPresenter.changeStatus(order, OrderInfo.STATUS_TRADING, iso, place);
                })
                .show();
    }

    private void confirm(String title, String message, Runnable action) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", (d, w) -> action.run())
                .show();
    }
}
