package com.campus.trade.network;

import com.campus.trade.model.entity.Category;
import com.campus.trade.model.entity.ChatMessage;
import com.campus.trade.model.entity.Comment;
import com.campus.trade.model.entity.Conversation;
import com.campus.trade.model.entity.OrderInfo;
import com.campus.trade.model.entity.Product;
import com.campus.trade.model.entity.ProductPage;
import com.campus.trade.model.entity.User;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit 接口定义
 */
public interface ApiService {

    // ========== 认证 ==========
    @POST("v1/auth/login")
    Call<BaseResponse<String>> login(@Body Map<String, Object> params);

    @POST("v1/auth/register")
    Call<BaseResponse<String>> register(@Body Map<String, Object> params);

    // ========== 分类 ==========
    @GET("v1/categories")
    Call<BaseResponse<List<Category>>> getCategories();

    // ========== 商品 ==========
    @GET("v1/products")
    Call<BaseResponse<ProductPage>> getProducts(@Query("categoryId") Integer categoryId,
                                                @Query("keyword") String keyword,
                                                @Query("page") int page,
                                                @Query("size") int size,
                                                @Query("sort") String sort);

    @GET("v1/products/{id}")
    Call<BaseResponse<Product>> getProductDetail(@Path("id") int id);

    @POST("v1/products")
    Call<BaseResponse<Product>> publishProduct(@Body Map<String, Object> params);

    @PUT("v1/products/{id}/status")
    Call<BaseResponse<String>> updateProductStatus(@Path("id") int id,
                                                   @Query("status") int status);

    @DELETE("v1/products/{id}")
    Call<BaseResponse<String>> deleteProduct(@Path("id") int id);

    @POST("v1/products/{id}/favorite")
    Call<BaseResponse<String>> toggleFavorite(@Path("id") int id);

    @GET("v1/products/{id}/favorited")
    Call<BaseResponse<Boolean>> isProductFavorited(@Path("id") int id);

    @GET("v1/products/{id}/comments")
    Call<BaseResponse<List<Comment>>> getComments(@Path("id") int id,
                                                  @Query("page") int page,
                                                  @Query("size") int size);

    @POST("v1/products/{id}/comments")
    Call<BaseResponse<Comment>> addComment(@Path("id") int id, @Body Map<String, Object> params);

    // ========== 图片上传 ==========
    @Multipart
    @POST("v1/upload/image")
    Call<BaseResponse<String>> uploadImage(@Part MultipartBody.Part file);

    // ========== 用户 ==========
    @GET("v1/user/profile")
    Call<BaseResponse<User>> getProfile();

    @PUT("v1/user/profile")
    Call<BaseResponse<User>> updateProfile(@Body Map<String, Object> params);

    @PUT("v1/user/password")
    Call<BaseResponse<String>> changePassword(@Body Map<String, Object> params);

    @GET("v1/user/products")
    Call<BaseResponse<List<Product>>> getMyProducts(@Query("status") Integer status);

    @GET("v1/user/favorites")
    Call<BaseResponse<List<Product>>> getFavorites();

    // ========== 订单 ==========
    @GET("v1/orders")
    Call<BaseResponse<List<OrderInfo>>> getOrders(@Query("status") Integer status,
                                                  @Query("role") String role);

    @POST("v1/orders")
    Call<BaseResponse<OrderInfo>> createOrder(@Body Map<String, Object> params);

    @PUT("v1/orders/{id}/status")
    Call<BaseResponse<String>> updateOrderStatus(@Path("id") int id, @Body Map<String, Object> params);

    // ========== 消息（轮询） ==========
    @POST("v1/messages")
    Call<BaseResponse<ChatMessage>> sendMessage(@Body Map<String, Object> params);

    @GET("v1/messages/conversations")
    Call<BaseResponse<List<Conversation>>> getConversations();

    @GET("v1/messages/poll")
    Call<BaseResponse<List<ChatMessage>>> pollMessages(@Query("targetUserId") int targetUserId,
                                                       @Query("sinceId") long sinceId);

    @GET("v1/messages/history/{targetUserId}")
    Call<BaseResponse<List<ChatMessage>>> getMessageHistory(@Path("targetUserId") int targetUserId,
                                                            @Query("page") int page,
                                                            @Query("size") int size);

    @GET("v1/messages/unread/count")
    Call<BaseResponse<Integer>> getUnreadCount();

    @PUT("v1/messages/read")
    Call<BaseResponse<String>> markAsRead(@Query("fromUserId") int fromUserId);
}
