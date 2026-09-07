package com.campus.trade.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 修改密码请求
 */
public class PasswordChangeRequest {

    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 50, message = "新密码长度需在6-50位之间")
    private String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
