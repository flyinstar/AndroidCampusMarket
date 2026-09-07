package com.campus.trade.controller;

import com.campus.trade.common.BaseResponse;
import com.campus.trade.dto.request.MessageSendRequest;
import com.campus.trade.dto.response.ConversationVO;
import com.campus.trade.entity.ChatMessage;
import com.campus.trade.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 消息接口（轮询）
 */
@RestController
@RequestMapping("/v1/messages")
public class MessageController {

    @Autowired
    private IMessageService messageService;

    // 发送消息
    @PostMapping
    public BaseResponse<ChatMessage> send(@Valid @RequestBody MessageSendRequest req,
                                          HttpServletRequest request) {
        Integer fromUserId = (Integer) request.getAttribute("currentUserId");
        return BaseResponse.success(messageService.send(fromUserId, req));
    }

    // 会话列表
    @GetMapping("/conversations")
    public BaseResponse<List<ConversationVO>> conversations(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("currentUserId");
        return BaseResponse.success(messageService.conversations(userId));
    }

    // 轮询新消息（核心）
    @GetMapping("/poll")
    public BaseResponse<List<ChatMessage>> poll(@RequestParam Integer targetUserId,
                                                @RequestParam(defaultValue = "0") Long sinceId,
                                                HttpServletRequest request) {
        Integer currentUserId = (Integer) request.getAttribute("currentUserId");
        List<ChatMessage> messages = messageService.poll(currentUserId, targetUserId, sinceId);
        return BaseResponse.success(messages);
    }

    // 聊天历史（首次加载 / 下拉加载更多）
    @GetMapping("/history/{targetUserId}")
    public BaseResponse<List<ChatMessage>> history(@PathVariable Integer targetUserId,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "20") Integer size,
                                                   HttpServletRequest request) {
        Integer currentUserId = (Integer) request.getAttribute("currentUserId");
        List<ChatMessage> messages = messageService.history(currentUserId, targetUserId, page, size);
        return BaseResponse.success(messages);
    }

    // 未读消息总数
    @GetMapping("/unread/count")
    public BaseResponse<Integer> getUnreadCount(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("currentUserId");
        return BaseResponse.success(messageService.unreadCount(userId));
    }

    // 标记某用户发来的消息已读
    @PutMapping("/read")
    public BaseResponse<String> markAsRead(@RequestParam Integer fromUserId,
                                           HttpServletRequest request) {
        Integer currentUserId = (Integer) request.getAttribute("currentUserId");
        messageService.markAsRead(fromUserId, currentUserId);
        return BaseResponse.success("已读", "已读");
    }
}
