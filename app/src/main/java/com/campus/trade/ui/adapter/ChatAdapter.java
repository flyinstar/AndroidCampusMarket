package com.campus.trade.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.campus.trade.R;
import com.campus.trade.model.entity.ChatMessage;
import com.campus.trade.network.ApiClient;
import com.campus.trade.utils.DateTimeUtil;
import com.campus.trade.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天气泡适配器（left=对方 right=自己），按时间正序展示
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.VH> {

    private final List<ChatMessage> mMessages = new ArrayList<>();

    private String mPeerAvatar;
    private int mMyUserId = Integer.MIN_VALUE;

    public void setPeerAvatar(String avatar) {
        this.mPeerAvatar = avatar;
    }

    public void setMyUserId(int myUserId) {
        this.mMyUserId = myUserId;
    }

    /** 服务端倒序返回首页数据，转成正序（旧→新） */
    public void replaceInitial(List<ChatMessage> descList) {
        mMessages.clear();
        if (descList != null) {
            for (int i = descList.size() - 1; i >= 0; i--) {
                mMessages.add(descList.get(i));
            }
        }
        notifyDataSetChanged();
    }

    /** 服务端倒序分页返回的更早消息，插入头部 */
    public void prependOlder(List<ChatMessage> descList) {
        if (descList == null || descList.isEmpty()) {
            return;
        }
        for (int i = descList.size() - 1; i >= 0; i--) {
            mMessages.add(0, descList.get(i));
        }
        notifyDataSetChanged();
    }

    public void append(List<ChatMessage> ascList) {
        if (ascList == null || ascList.isEmpty()) {
            return;
        }
        int start = mMessages.size();
        mMessages.addAll(ascList);
        notifyItemRangeInserted(start, ascList.size());
    }

    public void appendOne(ChatMessage message) {
        if (message == null) {
            return;
        }
        mMessages.add(message);
        notifyItemInserted(mMessages.size() - 1);
    }

    public int getSize() {
        return mMessages.size();
    }

    private boolean isMine(ChatMessage msg) {
        return msg.getFromUserId() == mMyUserId;
    }

    private String contentText(ChatMessage msg) {
        return msg.getMsgType() == ChatMessage.TYPE_IMAGE ? "[图片]" : msg.getContent();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        ChatMessage msg = mMessages.get(position);
        boolean mine = isMine(msg);
        holder.llLeft.setVisibility(mine ? View.GONE : View.VISIBLE);
        holder.llRight.setVisibility(mine ? View.VISIBLE : View.GONE);
        if (mine) {
            holder.tvBubbleRight.setText(contentText(msg));
        } else {
            holder.tvBubbleLeft.setText(contentText(msg));
            if (holder.itemView.getContext() != null) {
                ImageLoader.loadAvatar(holder.itemView.getContext(),
                        ApiClient.toAccessibleImageUrl(mPeerAvatar), holder.ivAvatarLeft);
            }
        }
        holder.tvTime.setText(DateTimeUtil.friendlyTime(msg.getCreatedAt()));
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTime;
        LinearLayout llLeft;
        LinearLayout llRight;
        ImageView ivAvatarLeft;
        TextView tvBubbleLeft;
        TextView tvBubbleRight;

        VH(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tv_time);
            llLeft = itemView.findViewById(R.id.ll_left);
            llRight = itemView.findViewById(R.id.ll_right);
            ivAvatarLeft = itemView.findViewById(R.id.iv_avatar_left);
            tvBubbleLeft = itemView.findViewById(R.id.tv_bubble_left);
            tvBubbleRight = itemView.findViewById(R.id.tv_bubble_right);
        }
    }
}
