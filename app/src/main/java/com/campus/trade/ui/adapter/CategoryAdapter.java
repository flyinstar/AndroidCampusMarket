package com.campus.trade.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.campus.trade.R;
import com.campus.trade.model.entity.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类列表适配器
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.VH> {

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    private final List<Category> mData = new ArrayList<>();
    private OnCategoryClickListener mListener;

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.mListener = listener;
    }

    public void setData(List<Category> categories) {
        mData.clear();
        if (categories != null) {
            mData.addAll(categories);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_row, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Category category = mData.get(position);
        String name = category.getName();
        holder.tvCircle.setText(name == null || name.isEmpty() ? "?" : name.substring(0, 1));
        holder.tvName.setText(name);
        holder.itemView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onCategoryClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvCircle;
        TextView tvName;

        VH(@NonNull View itemView) {
            super(itemView);
            tvCircle = itemView.findViewById(R.id.tv_circle);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }
}
