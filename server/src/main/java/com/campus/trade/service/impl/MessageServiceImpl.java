package com.campus.trade.service.impl;

import com.campus.trade.common.BusinessException;
import com.campus.trade.dto.request.MessageSendRequest;
import com.campus.trade.dto.response.ConversationVO;
import com.campus.trade.entity.ChatMessage;
import com.campus.trade.entity.User;
import com.campus.trade.mapper.MessageMapper;
import com.campus.trade.mapper.UserMapper;
import com.campus.trade.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息服务实现（轮询）
 */
@Service
public class MessageServiceImpl implements IMessageService {

    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public ChatMessage send(Integer fromUserId, MessageSendRequest request) {
        if (request.getToUserId() == null) {
            throw new BusinessException("接收者不能为空");
        }
        if (request.getToUserId().equals(fromUserId)) {
            throw new BusinessException("不能给自己发送消息");
        }
        if (!StringUtils.hasText(request.getContent())) {
            throw new BusinessException("消息内容不能为空");
        }
        User target = userMapper.findById(request.getToUserId());
        if (target == null) {
            throw new BusinessException("接收用户不存在");
        }
        int msgType = request.getMsgType() == null ? ChatMessage.TYPE_TEXT : request.getMsgType();
        if (msgType != ChatMessage.TYPE_TEXT && msgType != ChatMessage.TYPE_IMAGE) {
            msgType = ChatMessage.TYPE_TEXT;
        }

        ChatMessage message = new ChatMessage();
        message.setFromUserId(fromUserId);
        message.setToUserId(request.getToUserId());
        message.setContent(request.getContent().trim());
        message.setMsgType(msgType);
        message.setIsRead(false);
        message.setCreatedAt(LocalDateTime.now());
        messageMapper.insert(message);
        return message;
    }

    @Override
    public List<ChatMessage> history(Integer me, Integer targetUserId, int page, int size) {
        int p = Math.max(page, 1);
        int s = Math.min(Math.max(size, 1), 50);
        int offset = (p - 1) * s;
        List<ChatMessage> list = messageMapper.selectHistory(me, targetUserId, offset, s);
        return list == null ? Collections.emptyList() : list;
    }

    @Override
    public List<ChatMessage> poll(Integer me, Integer targetUserId, long sinceId) {
        List<ChatMessage> list = messageMapper.pollNewMessages(me, targetUserId, sinceId);
        return list == null ? Collections.emptyList() : list;
    }

    @Override
    public int unreadCount(Integer userId) {
        return messageMapper.countUnread(userId);
    }

    @Override
    public void markAsRead(Integer fromUserId, Integer toUserId) {
        messageMapper.markRead(fromUserId, toUserId);
    }

    @Override
    public List<ConversationVO> conversations(Integer userId) {
        List<ConversationVO> list = messageMapper.selectConversations(userId);
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> unreadRows = messageMapper.selectUnreadGroup(userId);
        Map<Integer, Integer> unreadMap = new HashMap<>();
        if (unreadRows != null) {
            for (Map<String, Object> row : unreadRows) {
                Object uid = row.get("fromUserId");
                Object cnt = row.get("cnt");
                if (uid != null && cnt != null) {
                    unreadMap.put(Integer.valueOf(String.valueOf(uid)),
                            ((Number) cnt).intValue());
                }
            }
        }
        for (ConversationVO vo : list) {
            Integer u = unreadMap.get(vo.getTargetUserId());
            vo.setUnreadCount(u == null ? 0 : u);
        }
        return list;
    }
}
