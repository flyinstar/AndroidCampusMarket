package com.campus.trade.dto.response;

import java.time.LocalDateTime;

/**
 * 会话列表项（消息 Tab）
 */
public class ConversationVO {

    private Integer targetUserId;
    private String nickname;
    private String avatar;
    private String lastContent;
    private Integer lastMsgType;
    private LocalDateTime lastTime;
    private Integer unreadCount;

    public Integer getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Integer targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLastContent() {
        return lastContent;
    }

    public void setLastContent(String lastContent) {
        this.lastContent = lastContent;
    }

    public Integer getLastMsgType() {
        return lastMsgType;
    }

    public void setLastMsgType(Integer lastMsgType) {
        this.lastMsgType = lastMsgType;
    }

    public LocalDateTime getLastTime() {
        return lastTime;
    }

    public void setLastTime(LocalDateTime lastTime) {
        this.lastTime = lastTime;
    }

    public Integer getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }
}
