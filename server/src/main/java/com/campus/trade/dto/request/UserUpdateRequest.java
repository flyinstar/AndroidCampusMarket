package com.campus.trade.dto.request;

import javax.validation.constraints.Size;

/**
 * 用户资料更新请求（字段为空则保持不变）
 */
public class UserUpdateRequest {

    @Size(max = 50, message = "昵称最长50字")
    private String nickname;

    private String avatar;

    @Size(max = 15, message = "手机号过长")
    private String phone;

    @Size(max = 20, message = "年级格式如：2023级")
    private String grade;

    @Size(max = 100, message = "专业最长100字")
    private String major;

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }
}
