package com.campus.trade.dto.response;

import com.campus.trade.entity.Product;

import java.util.List;

/**
 * 商品分页列表响应
 */
public class ProductListResponse {

    private long total;
    private int page;
    private int size;
    private List<Product> list;

    public ProductListResponse() {
    }

    public ProductListResponse(long total, int page, int size, List<Product> list) {
        this.total = total;
        this.page = page;
        this.size = size;
        this.list = list;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<Product> getList() {
        return list;
    }

    public void setList(List<Product> list) {
        this.list = list;
    }
}
