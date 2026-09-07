package com.campus.trade.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.campus.trade.R;
import com.campus.trade.model.entity.OrderInfo;
import com.campus.trade.network.ApiClient;
import com.campus.trade.utils.DateTimeUtil;
import com.campus.trade.utils.ImageLoader;
import com.campus.trade.utils.SharedPrefUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单列表适配器
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.VH> {

    public interface Listener {
        /** 按钮回调: action 取值 accept/complete/cancel */
        void onAction(OrderInfo order, String action);
    }

    private final List<OrderInfo> mData = new ArrayList<>();
    private Listener mListener;
    private int mMyUserId;

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    public void setMyUserId(int myUserId) {
        this.mMyUserId = myUserId;
    }

    public void setData(List<OrderInfo> list) {
        mData.clear();
        if (list != null) {
            mData.addAll(list);
        }
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return mData.isEmpty();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        OrderInfo order = mData.get(position);
        boolean isBuyer = order.getBuyerId() == mMyUserId;

        ImageLoader.loadRound(holder.itemView.getContext(),
                ApiClient.toAccessibleImageUrl(order.getProductCover()), holder.ivCover, 6);
        holder.tvTitle.setText(order.getProductTitle());
        holder.tvAmount.setText("¥" + order.priceText());
        holder.tvStatus.setText(OrderInfo.statusText(order.getStatus()));
        int statusColor = order.getStatus() == OrderInfo.STATUS_TRADING ? R.color.status_orange
                : order.getStatus() == OrderInfo.STATUS_DONE ? R.color.status_green
                : order.getStatus() == OrderInfo.STATUS_CANCELED ? R.color.status_gray
                : R.color.status_blue;
        holder.tvStatus.setTextColor(holder.itemView.getResources().getColor(statusColor));

        String role = isBuyer ? "我(买家)" : "我(卖家)";
        String counterpart = (isBuyer ? "卖家: " : "买家: ") + order.getCounterpartName();
        StringBuilder meta = new StringBuilder()
                .append(counterpart).append(" · ").append(role)
                .append(" · ").append(DateTimeUtil.friendlyTime(order.getCreatedAt()));
        if (order.getMeetingPlace() != null || order.getMeetingTime() != null) {
            meta.append("\n见面：")
                    .append(order.getMeetingPlace() == null ? "" : order.getMeetingPlace());
            if (order.getMeetingTime() != null) {
                meta.append(" ").append(DateTimeUtil.friendlyTime(order.getMeetingTime()));
            }
        }
        holder.tvMeta.setText(meta.toString());
        bindActions(holder, order, isBuyer);
    }

    private void bindActions(VH holder, OrderInfo order, boolean isBuyer) {
        holder.llActions.setVisibility(View.VISIBLE);
        holder.btnPrimary.setVisibility(View.VISIBLE);
        holder.btnSecondary.setVisibility(View.VISIBLE);
        switch (order.getStatus()) {
            case OrderInfo.STATUS_PENDING:
                if (isBuyer) {
                    holder.btnSecondary.setVisibility(View.GONE);
                    holder.btnPrimary.setText("取消订单");
                    holder.btnPrimary.setOnClickListener(v ->
                            fire(order, "cancel"));
                } else {
                    holder.btnSecondary.setText("取消订单");
                    holder.btnSecondary.setOnClickListener(v -> fire(order, "cancel"));
                    holder.btnPrimary.setText("接单");
                    holder.btnPrimary.setOnClickListener(v -> fire(order, "accept"));
                }
                break;
            case OrderInfo.STATUS_TRADING:
                holder.btnPrimary.setText("完成交易");
                holder.btnPrimary.setOnClickListener(v -> fire(order, "complete"));
                holder.btnSecondary.setText("取消订单");
                holder.btnSecondary.setOnClickListener(v -> fire(order, "cancel"));
                break;
            default:
                holder.llActions.setVisibility(View.GONE);
        }
    }

    private void fire(OrderInfo order, String action) {
        if (mListener != null) {
            mListener.onAction(order, action);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle;
        TextView tvAmount;
        TextView tvStatus;
        TextView tvMeta;
        View llActions;
        Button btnPrimary;
        Button btnSecondary;

        VH(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvMeta = itemView.findViewById(R.id.tv_meta);
            llActions = itemView.findViewById(R.id.ll_actions);
            btnPrimary = itemView.findViewById(R.id.btn_primary);
            btnSecondary = itemView.findViewById(R.id.btn_secondary);
        }
    }
}
