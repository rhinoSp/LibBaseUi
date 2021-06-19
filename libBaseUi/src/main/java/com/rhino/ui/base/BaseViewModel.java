package com.rhino.ui.base;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rhino.log.LogUtils;
import com.rhino.ui.utils.net.HttpUtils;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author LuoLin
 * @since Create on 2021/6/5
 **/
public class BaseViewModel extends ViewModel {

    public MutableLiveData<Integer> statusLiveData = new MutableLiveData<>();
    public MutableLiveData<String> errorMsgLiveData = new MutableLiveData<>();

    protected <T> T getRetrofitService(Class<T> c) {
        return HttpUtils.getRetrofitService(c);
    }

    protected <T> Disposable request(
            Observable<T> observable,
            Consumer<? super T> onNext,
            Consumer<? super Throwable> onError
    ) {
        return HttpUtils.request(observable, disposable -> {
            LogUtils.i("请求开始！");
            statusLiveData.postValue(1);
        }, () -> {
            LogUtils.i("请求完成！");
            statusLiveData.postValue(2);
        }, onNext, onError);
    }


}
