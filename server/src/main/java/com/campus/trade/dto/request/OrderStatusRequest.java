package com.campus.trade.dto.request;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 订单状态流转请求：seller 接单(0->1)时携带 meetingTime/meetingPlace
 */
public class OrderStatusRequest {

    @NotNull(message = "status不能为空")
    private Integer status;

    private LocalDateTime meetingTime;

    private String meetingPlace;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getMeetingTime() {
        return meetingTime;
    }

    public void setMeetingTime(LocalDateTime meetingTime) {
        this.meetingTime = meetingTime;
    }

    public String getMeetingPlace() {
        return meetingPlace;
    }

    public void setMeetingPlace(String meetingPlace) {
        this.meetingPlace = meetingPlace;
    }
}
