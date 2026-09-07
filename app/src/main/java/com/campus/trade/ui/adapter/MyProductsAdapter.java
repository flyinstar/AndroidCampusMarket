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
import com.campus.trade.model.entity.Product;
import com.campus.trade.network.ApiClient;
import com.campus.trade.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的发布列表适配器
 */
public class MyProductsAdapter extends RecyclerView.Adapter<MyProductsAdapter.VH> {

    public interface Listener {
        void onToggleStatus(Product product);

        void onDelete(Product product);

        void onItemClick(Product product);
    }

    private final List<Product> mData = new ArrayList<>();
    private Listener mListener;

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    public void setData(List<Product> list) {
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
                .inflate(R.layout.item_my_product, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Product product = mData.get(position);
        ImageLoader.loadRound(holder.itemView.getContext(),
                ApiClient.toAccessibleImageUrl(product.getFirstImage()), holder.ivCover, 6);
        holder.tvTitle.setText(product.getTitle());
        holder.tvPrice.setText("¥" + product.priceText());
        holder.tvStatus.setText(Product.statusText(product.getStatus()));

        if (product.getStatus() == Product.STATUS_ON) {
            holder.tvStatus.setTextColor(androidx.core.content.ContextCompat.getColor(holder.itemView.getContext(), R.color.status_green));
            holder.btnToggle.setText("下架");
        } else if (product.getStatus() == Product.STATUS_OFF) {
            holder.tvStatus.setTextColor(androidx.core.content.ContextCompat.getColor(holder.itemView.getContext(), R.color.status_gray));
            holder.btnToggle.setText("重新上架");
        } else {
            // 已预约/已售出：不可手动上下架
            holder.tvStatus.setTextColor(androidx.core.content.ContextCompat.getColor(holder.itemView.getContext(), R.color.status_orange));
            holder.btnToggle.setText("状态锁定");
            holder.btnToggle.setEnabled(false);
        }
        holder.btnToggle.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onToggleStatus(product);
            }
        });
        holder.btnDelete.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onDelete(product);
            }
        });
        holder.itemView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onItemClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle;
        TextView tvPrice;
        TextView tvStatus;
        Button btnToggle;
        Button btnDelete;

        VH(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnToggle = itemView.findViewById(R.id.btn_toggle);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
