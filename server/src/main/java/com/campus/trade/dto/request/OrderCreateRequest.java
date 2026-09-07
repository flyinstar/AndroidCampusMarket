package com.campus.trade.dto.request;

import javax.validation.constraints.NotNull;

/**
 * 创建订单请求
 */
public class OrderCreateRequest {

    @NotNull(message = "productId不能为空")
    private Integer productId;

    private String remark;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
