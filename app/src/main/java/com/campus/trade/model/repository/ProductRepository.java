package com.campus.trade.model.repository;

import com.campus.trade.model.entity.Category;
import com.campus.trade.model.entity.Comment;
import com.campus.trade.model.entity.Product;
import com.campus.trade.model.entity.ProductPage;
import com.campus.trade.network.ApiClient;
import com.campus.trade.network.ApiRequest;
import com.campus.trade.network.callback.ApiCallback;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

/**
 * 商品数据仓库
 */
public class ProductRepository {

    // ========== 分类 ==========
    public void categories(ApiCallback<List<Category>> callback) {
        ApiRequest.enqueue(ApiClient.getApiService().getCategories(), callback);
    }

    // ========== 商品 ==========
    public void products(Integer categoryId, String keyword, int page, int size, String sort,
                         ApiCallback<ProductPage> callback) {
        ApiRequest.enqueue(ApiClient.getApiService()
                .getProducts(categoryId, keyword, page, size, sort), callback);
    }

    public void detail(int id, ApiCallback<Product> callback) {
        ApiRequest.enqueue(ApiClient.getApiService().getProductDetail(id), callback);
    }

    public void publish(String title, String description, double price, int categoryId, int condition,
                        List<String> imageUrls, ApiCallback<Product> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("title", title);
        params.put("description", description);
        params.put("price", price);
        params.put("categoryId", categoryId);
        params.put("condition", condition);
        params.put("imageUrls", imageUrls);
        ApiRequest.enqueue(ApiClient.getApiService().publishProduct(params), callback);
    }

    public void updateStatus(int id, int status, ApiCallback<String> callback) {
        ApiRequest.enqueue(ApiClient.getApiService().updateProductStatus(id, status), callback);
    }

    public void delete(int id, ApiCallback<String> callback) {
        ApiRequest.enqueue(ApiClient.getApiService().deleteProduct(id), callback);
    }

    public void toggleFavorite(int id, ApiCallback<String> callback) {
        ApiRequest.enqueue(ApiClient.getApiService().toggleFavorite(id), callback);
    }

    public void isFavorited(int id, ApiCallback<Boolean> callback) {
        ApiRequest.enqueue(ApiClient.getApiService().isProductFavorited(id), callback);
    }

    // ========== 评论 ==========
    public void comments(int productId, int page, int size, ApiCallback<List<Comment>> callback) {
        ApiRequest.enqueue(ApiClient.getApiService().getComments(productId, page, size), callback);
    }

    public void addComment(int productId, String content, ApiCallback<Comment> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("content", content);
        ApiRequest.enqueue(ApiClient.getApiService().addComment(productId, params), callback);
    }

    // ========== 图片上传 ==========
    public void uploadImage(File file, ApiCallback<String> callback) {
        RequestBody body = RequestBody.create(MediaType.parse("image/jpeg"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), body);
        Call<com.campus.trade.network.BaseResponse<String>> call =
                ApiClient.getApiService().uploadImage(part);
        ApiRequest.enqueue(call, callback);
    }
}
