package com.campus.trade.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * 商品卡片适配器（首页/列表双列网格）
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.VH> {

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    private final List<Product> mData = new ArrayList<>();
    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public void replace(List<Product> list) {
        mData.clear();
        if (list != null) {
            mData.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void append(List<Product> list) {
        if (list == null) {
            return;
        }
        int start = mData.size();
        mData.addAll(list);
        notifyItemRangeInserted(start, list.size());
    }

    public boolean isEmpty() {
        return mData.isEmpty();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_card, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Product product = mData.get(position);
        ImageLoader.loadRound(holder.itemView.getContext(),
                ApiClient.toAccessibleImageUrl(product.getFirstImage()),
                holder.ivCover, 6);
        holder.tvTitle.setText(product.getTitle());
        holder.tvPrice.setText("¥" + product.priceText());
        holder.tvViews.setText(product.getViewCount() + " 浏览");
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
        TextView tvViews;

        VH(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvViews = itemView.findViewById(R.id.tv_views);
        }
    }
}
