package com.rhino.ui.demo.http;

import com.rhino.ui.demo.http.response.BaseResponse;
import com.rhino.ui.demo.http.response.UserInfo;

import io.reactivex.Observable;
import retrofit2.http.GET;


/**
 * @author LuoLin
 * @since Create on 2021/5/3.
 **/
public interface ApiService {

    String BASE_URL = "http://127.0.0.1:8888";

    @GET("getUserInfo")
    Observable<BaseResponse<UserInfo>> getUserInfo();


}