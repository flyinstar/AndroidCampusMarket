package com.campus.trade.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 发表评论请求
 */
public class CommentCreateRequest {

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 1000, message = "评论最长1000字")
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
