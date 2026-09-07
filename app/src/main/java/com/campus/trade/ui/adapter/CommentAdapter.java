package com.campus.trade.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.campus.trade.R;
import com.campus.trade.model.entity.Comment;
import com.campus.trade.network.ApiClient;
import com.campus.trade.utils.DateTimeUtil;
import com.campus.trade.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * 评论适配器
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.VH> {

    private final List<Comment> mData = new ArrayList<>();

    public void replace(List<Comment> list) {
        mData.clear();
        if (list != null) {
            mData.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void addFirst(Comment comment) {
        if (comment == null) {
            return;
        }
        mData.add(0, comment);
        notifyItemInserted(0);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Comment comment = mData.get(position);
        ImageLoader.loadAvatar(holder.itemView.getContext(),
                ApiClient.toAccessibleImageUrl(comment.getUserAvatar()), holder.ivAvatar);
        holder.tvName.setText(comment.getUserName());
        holder.tvTime.setText(DateTimeUtil.friendlyTime(comment.getCreatedAt()));
        holder.tvContent.setText(comment.getContent());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvName;
        TextView tvTime;
        TextView tvContent;

        VH(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvName = itemView.findViewById(R.id.tv_name);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvContent = itemView.findViewById(R.id.tv_content);
        }
    }
}
