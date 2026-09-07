package com.campus.trade.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.campus.trade.R;
import com.campus.trade.model.entity.ChatMessage;
import com.campus.trade.model.entity.Conversation;
import com.campus.trade.network.ApiClient;
import com.campus.trade.utils.DateTimeUtil;
import com.campus.trade.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * 会话列表适配器
 */
public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.VH> {

    public interface OnConversationClickListener {
        void onConversationClick(Conversation conversation);
    }

    private final List<Conversation> mData = new ArrayList<>();
    private OnConversationClickListener mListener;

    public void setListener(OnConversationClickListener listener) {
        this.mListener = listener;
    }

    public void setData(List<Conversation> conversations) {
        mData.clear();
        if (conversations != null) {
            mData.addAll(conversations);
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
                .inflate(R.layout.item_conversation, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Conversation conversation = mData.get(position);
        ImageLoader.loadAvatar(holder.itemView.getContext(),
                ApiClient.toAccessibleImageUrl(conversation.getAvatar()), holder.ivAvatar);
        holder.tvName.setText(conversation.getNickname());
        holder.tvTime.setText(DateTimeUtil.friendlyTime(conversation.getLastTime()));
        String preview = conversation.getLastContent();
        if (conversation.getLastMsgType() == ChatMessage.TYPE_IMAGE) {
            preview = "[图片]";
        }
        holder.tvLast.setText(preview);
        if (conversation.getUnreadCount() > 0) {
            holder.tvUnread.setVisibility(View.VISIBLE);
            holder.tvUnread.setText(conversation.getUnreadCount() > 99 ? "99+" : String.valueOf(conversation.getUnreadCount()));
        } else {
            holder.tvUnread.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onConversationClick(conversation);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvName;
        TextView tvTime;
        TextView tvLast;
        TextView tvUnread;

        VH(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvName = itemView.findViewById(R.id.tv_name);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvLast = itemView.findViewById(R.id.tv_last);
            tvUnread = itemView.findViewById(R.id.tv_unread);
        }
    }
}
