package com.campus.trade.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品实体。
 * 下列字段由联表 / 附属查询填充，不属于 product 表：sellerName、sellerAvatar、categoryName、
 * firstImage、imageUrls、isFavorite
 */
public class Product implements Serializable {

    public static final int STATUS_OFF = 0;        // 下架
    public static final int STATUS_ON = 1;         // 上架中
    public static final int STATUS_RESERVED = 2;   // 已预约
    public static final int STATUS_SOLD = 3;       // 已售出

    private Integer id;
    private String title;
    private String description;
    private BigDecimal price;
    private Integer userId;
    private Integer categoryId;
    private Integer condition;
    private Integer status;
    private Integer viewCount;
    private Integer favoriteCount;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ---- 展示用扩展字段 ----
    private String sellerName;
    private String sellerAvatar;
    private Integer sellerCredit;
    private String categoryName;
    private String firstImage;
    private List<String> imageUrls;
    private Boolean isFavorite;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getCondition() {
        return condition;
    }

    public void setCondition(Integer condition) {
        this.condition = condition;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(Integer favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
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

    public Integer getSellerCredit() {
        return sellerCredit;
    }

    public void setSellerCredit(Integer sellerCredit) {
        this.sellerCredit = sellerCredit;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public Boolean getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(Boolean favorite) {
        isFavorite = favorite;
    }
}
