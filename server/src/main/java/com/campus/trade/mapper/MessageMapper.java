package com.campus.trade.mapper;

import com.campus.trade.dto.response.ConversationVO;
import com.campus.trade.entity.ChatMessage;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 聊天消息 Mapper（轮询核心）
 */
public interface MessageMapper {

    int insert(ChatMessage message);

    /** 轮询：两人之间 id > sinceId 的消息（升序） */
    List<ChatMessage> pollNewMessages(@Param("userId1") Integer userId1,
                                      @Param("userId2") Integer userId2,
                                      @Param("sinceId") Long sinceId);

    /** 历史消息：两人之间按 id 倒序分页 */
    List<ChatMessage> selectHistory(@Param("userId1") Integer userId1,
                                    @Param("userId2") Integer userId2,
                                    @Param("offset") int offset,
                                    @Param("size") int size);

    int countUnread(@Param("userId") Integer userId);

    int markRead(@Param("fromUserId") Integer fromUserId,
                 @Param("toUserId") Integer toUserId);

    /** 会话列表：每人最后一条消息（含对方昵称头像） */
    List<ConversationVO> selectConversations(@Param("userId") Integer userId);

    /** 每个会话的未读数：fromUserId -> 未读计数 */
    List<Map<String, Object>> selectUnreadGroup(@Param("userId") Integer userId);
}
