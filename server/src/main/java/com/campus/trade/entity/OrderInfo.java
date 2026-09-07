package com.campus.trade.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体。
 * 展示用扩展字段：productTitle、productCover、counterpartName、counterpartAvatar
 */
public class OrderInfo implements Serializable {

    public static final int STATUS_PENDING = 0;  // 待确认
    public static final int STATUS_TRADING = 1;  // 交易中
    public static final int STATUS_DONE = 2;     // 已完成
    public static final int STATUS_CANCELED = 3; // 已取消

    private Integer id;
    private String orderNo;
    private Integer productId;
    private Integer buyerId;
    private Integer sellerId;
    private BigDecimal amount;
    private Integer status;
    private String remark;
    private LocalDateTime meetingTime;
    private String meetingPlace;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ---- 展示用扩展字段 ----
    private String productTitle;
    private String productCover;
    private String counterpartName;
    private String counterpartAvatar;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Integer buyerId) {
        this.buyerId = buyerId;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductCover() {
        return productCover;
    }

    public void setProductCover(String productCover) {
        this.productCover = productCover;
    }

    public String getCounterpartName() {
        return counterpartName;
    }

    public void setCounterpartName(String counterpartName) {
        this.counterpartName = counterpartName;
    }

    public String getCounterpartAvatar() {
        return counterpartAvatar;
    }

    public void setCounterpartAvatar(String counterpartAvatar) {
        this.counterpartAvatar = counterpartAvatar;
    }
}
