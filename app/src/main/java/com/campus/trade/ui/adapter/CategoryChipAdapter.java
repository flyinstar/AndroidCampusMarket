package com.campus.trade.ui.adapter;

import android.graphics.Color;
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
 * 首页分类横向快捷入口（首位为“全部”，id=0 表示不过滤）
 */
public class CategoryChipAdapter extends RecyclerView.Adapter<CategoryChipAdapter.VH> {

    public interface OnCategoryClickListener {
        void onCategoryClick(Integer categoryId, String name);
    }

    private static final String ALL_NAME = "全部";

    private final List<Category> mData = new ArrayList<>();
    private OnCategoryClickListener mListener;
    private int mSelectedId; // 0 = 全部

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.mListener = listener;
    }

    public void setCategories(List<Category> categories) {
        mData.clear();
        Category all = new Category();
        all.setId(0);
        all.setName(ALL_NAME);
        mData.add(all);
        if (categories != null) {
            mData.addAll(categories);
        }
        notifyDataSetChanged();
    }

    public void setSelected(int categoryId) {
        mSelectedId = categoryId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_chip, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Category category = mData.get(position);
        String name = category.getName();
        String first = name == null || name.isEmpty() ? "?" : name.substring(0, 1);
        holder.tvCircle.setText(first);
        holder.tvName.setText(name);
        boolean selected = mSelectedId == category.getId();
        holder.tvName.setTextColor(selected
                ? androidx.core.content.ContextCompat.getColor(holder.itemView.getContext(), R.color.primary)
                : androidx.core.content.ContextCompat.getColor(holder.itemView.getContext(), R.color.text_second));
        holder.tvCircle.setTextColor(selected ? Color.WHITE
                : androidx.core.content.ContextCompat.getColor(holder.itemView.getContext(), R.color.primary));
        holder.tvCircle.setBackgroundResource(selected ? R.drawable.bg_category_circle_selected
                : R.drawable.bg_category_circle);
        holder.itemView.setOnClickListener(v -> {
            setSelected(category.getId());
            if (mListener != null) {
                mListener.onCategoryClick(category.getId(), name);
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
