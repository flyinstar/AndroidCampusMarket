package com.campus.trade.model.entity;

/**
 * 订单实体
 */
public class OrderInfo {

    public static final int STATUS_PENDING = 0;  // 待确认
    public static final int STATUS_TRADING = 1;  // 交易中
    public static final int STATUS_DONE = 2;     // 已完成
    public static final int STATUS_CANCELED = 3; // 已取消

    private int id;
    private String orderNo;
    private int productId;
    private int buyerId;
    private int sellerId;
    private double amount;
    private int status;
    private String remark;
    private String meetingTime;
    private String meetingPlace;
    private String createdAt;
    private String updatedAt;

    // 展示字段
    private String productTitle;
    private String productCover;
    private String counterpartName;
    private String counterpartAvatar;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(int buyerId) {
        this.buyerId = buyerId;
    }

    public int getSellerId() {
        return sellerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getMeetingTime() {
        return meetingTime;
    }

    public void setMeetingTime(String meetingTime) {
        this.meetingTime = meetingTime;
    }

    public String getMeetingPlace() {
        return meetingPlace;
    }

    public void setMeetingPlace(String meetingPlace) {
        this.meetingPlace = meetingPlace;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
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

    public String priceText() {
        return String.format(java.util.Locale.US, "%.2f", amount);
    }

    public static String statusText(int status) {
        switch (status) {
            case STATUS_PENDING:
                return "待卖家确认";
            case STATUS_TRADING:
                return "交易中";
            case STATUS_DONE:
                return "已完成";
            case STATUS_CANCELED:
                return "已取消";
            default:
                return "未知";
        }
    }
}
