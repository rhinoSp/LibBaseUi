package com.rhino.ui.demo.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.rhino.ui.base.BaseViewModel;
import com.rhino.log.LogUtils;
import com.rhino.ui.demo.http.ApiService;
import com.rhino.ui.demo.http.response.UserInfo;

/**
 * @author LuoLin
 * @since Create on 2021/6/5
 **/
public class UserViewModel extends BaseViewModel {

    public MutableLiveData<UserInfo> userInfoLiveData = new MutableLiveData<>();
    public ApiService apiService = getRetrofitService(ApiService.class);

    public void getUserInfo() {
        request(apiService.getUserInfo(), result -> {
            if (result != null && result.code == 0) {
                LogUtils.i("请求成功！" + result.toString());
                userInfoLiveData.postValue(result.data);
            } else {
                errorMsgLiveData.postValue(result == null ? "请求异常" : result.message);
                LogUtils.e(result == null ? "请求异常" : "请求异常！" + result.toString());
            }
        }, throwable -> {
            errorMsgLiveData.postValue(throwable.toString());
            LogUtils.e("请求异常！" + throwable.toString());
        });
    }

}
