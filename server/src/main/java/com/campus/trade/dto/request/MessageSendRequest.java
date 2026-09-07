package com.campus.trade.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 发送消息请求
 */
public class MessageSendRequest {

    @NotNull(message = "toUserId不能为空")
    private Integer toUserId;

    @NotBlank(message = "消息内容不能为空")
    private String content;

    /** 0文本 1图片 */
    private Integer msgType;

    public Integer getToUserId() {
        return toUserId;
    }

    public void setToUserId(Integer toUserId) {
        this.toUserId = toUserId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }
}
