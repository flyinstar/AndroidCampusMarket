package com.campus.trade.ui.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.campus.trade.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 发布商品图片选择适配器（末尾自动补“+”添加项）
 */
public class PublishImageAdapter extends RecyclerView.Adapter<PublishImageAdapter.VH> {

    public interface Listener {
        void onAddImage();

        void onRemoveImage(int index);
    }

    public static final int MAX_COUNT = 6;

    private final List<String> mPaths = new ArrayList<>();
    private Listener mListener;

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    public List<String> getPaths() {
        return mPaths;
    }

    public void addPath(String path) {
        mPaths.add(path);
        notifyDataSetChanged();
    }

    public void removeAt(int index) {
        if (index >= 0 && index < mPaths.size()) {
            mPaths.remove(index);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mPaths.size() + (mPaths.size() < MAX_COUNT ? 1 : 0);
    }

    private boolean isAddTile(int position) {
        return position == mPaths.size();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_publish_image, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        if (isAddTile(position)) {
            holder.ivThumb.setImageResource(R.drawable.ic_add_photo);
            holder.btnRemove.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onAddImage();
                }
            });
            return;
        }
        String path = mPaths.get(position);
        holder.ivThumb.setImageResource(R.drawable.bg_img_placeholder);
        Glide.with(holder.itemView.getContext())
                .load(Uri.fromFile(new File(path)))
                .centerCrop()
                .into(holder.ivThumb);
        holder.btnRemove.setVisibility(View.VISIBLE);
        holder.itemView.setOnClickListener(null);
        holder.btnRemove.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onRemoveImage(position);
            }
        });
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivThumb;
        ImageButton btnRemove;

        VH(@NonNull View itemView) {
            super(itemView);
            ivThumb = itemView.findViewById(R.id.iv_thumb);
            btnRemove = itemView.findViewById(R.id.btn_remove);
        }
    }
}
