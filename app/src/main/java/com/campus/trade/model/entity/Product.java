package com.campus.trade.model.entity;

import java.util.List;

/**
 * 商品实体（与后端 product 表 + 联表字段对应）
 */
public class Product {

    public static final int STATUS_OFF = 0;      // 下架
    public static final int STATUS_ON = 1;       // 上架中
    public static final int STATUS_RESERVED = 2; // 已预约
    public static final int STATUS_SOLD = 3;     // 已售出

    private int id;
    private String title;
    private String description;
    private double price;
    private int userId;
    private String sellerName;
    private String sellerAvatar;
    private int sellerCredit;
    private int categoryId;
    private String categoryName;
    private int condition;
    private int status;
    private int viewCount;
    private int favoriteCount;
    private boolean isFavorite;
    private String firstImage;
    private List<String> imageUrls;
    private String createdAt;
    private String updatedAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerAvatar() {
        return sellerAvatar;
    }

    public void setSellerAvatar(String sellerAvatar) {
        this.sellerAvatar = sellerAvatar;
    }

    public int getSellerCredit() {
        return sellerCredit;
    }

    public void setSellerCredit(int sellerCredit) {
        this.sellerCredit = sellerCredit;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getFirstImage() {
        return firstImage;
    }

    public void setFirstImage(String firstImage) {
        this.firstImage = firstImage;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
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

    /** 显示用价格 */
    public String priceText() {
        return String.format(java.util.Locale.US, "%.2f", price);
    }

    /** 新旧程度文案 */
    public static String conditionText(int condition) {
        switch (condition) {
            case 1:
                return "全新";
            case 2:
                return "几乎全新";
            case 3:
                return "有使用痕迹";
            case 4:
                return "老旧";
            default:
                return "未知";
        }
    }

    /** 商品状态文案 */
    public static String statusText(int status) {
        switch (status) {
            case STATUS_OFF:
                return "已下架";
            case STATUS_ON:
                return "出售中";
            case STATUS_RESERVED:
                return "已预约";
            case STATUS_SOLD:
                return "已售出";
            default:
                return "未知";
        }
    }
}
