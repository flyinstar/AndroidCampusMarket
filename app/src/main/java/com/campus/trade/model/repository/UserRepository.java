package com.campus.trade.model.repository;

import com.campus.trade.model.entity.User;
import com.campus.trade.network.ApiClient;
import com.campus.trade.network.ApiRequest;
import com.campus.trade.network.callback.ApiCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户数据仓库：登录 / 注册 / 资料
 */
public class UserRepository {

    public void login(String email, String password, ApiCallback<String> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
        ApiRequest.enqueue(ApiClient.getApiService().login(params), callback);
    }

    public void register(String email, String password, String nickname, ApiCallback<String> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
        params.put("nickname", nickname);
        ApiRequest.enqueue(ApiClient.getApiService().register(params), callback);
    }

    public void getProfile(ApiCallback<User> callback) {
        ApiRequest.enqueue(ApiClient.getApiService().getProfile(), callback);
    }

    public void updateProfile(String nickname, String avatar, String phone, String grade, String major,
                              ApiCallback<User> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("nickname", nickname);
        params.put("avatar", avatar);
        params.put("phone", phone);
        params.put("grade", grade);
        params.put("major", major);
        ApiRequest.enqueue(ApiClient.getApiService().updateProfile(params), callback);
    }

    public void changePassword(String oldPassword, String newPassword, ApiCallback<String> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("oldPassword", oldPassword);
        params.put("newPassword", newPassword);
        ApiRequest.enqueue(ApiClient.getApiService().changePassword(params), callback);
    }

    public void myProducts(Integer status, ApiCallback<List<com.campus.trade.model.entity.Product>> callback) {
        ApiRequest.enqueue(ApiClient.getApiService().getMyProducts(status), callback);
    }

    public void favorites(ApiCallback<List<com.campus.trade.model.entity.Product>> callback) {
        ApiRequest.enqueue(ApiClient.getApiService().getFavorites(), callback);
    }
}
