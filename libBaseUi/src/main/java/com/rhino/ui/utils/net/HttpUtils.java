package com.rhino.ui.utils.net;

import android.text.TextUtils;
import android.util.LruCache;

import com.rhino.log.LogUtils;
import com.rhino.ui.utils.ReflectUtils;
import com.rhino.ui.utils.net.interceptors.HttpLogInterceptor;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * @author LuoLin
 * @since Create on 2021/6/5
 **/
public class HttpUtils {

    public static String BASE_URL = "";

    public static int cacheServiceCount = 100;
    public static int TIMEOUT_SECONDS_CONNECT = 60;
    public static int TIMEOUT_SECONDS_WRITE = 60;
    public static int TIMEOUT_SECONDS_READ = 60;
    private static LruCache<String, Object> lruOverseasRetrofitCacheService = new LruCache<String, Object>(cacheServiceCount);

    public static OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SECONDS_CONNECT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS_WRITE, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS_READ, TimeUnit.SECONDS)
                .addInterceptor(new HttpLogInterceptor("HTTP_LOG"))
                .build();
    }

    public static Retrofit getRetrofit(String baseUrl) {
        if (TextUtils.isEmpty(baseUrl)) {
            throw new RuntimeException("BASE_URL is null!");
        }
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getOkHttpClient())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static <T> T getRetrofitService(Class<T> service, String baseUrl) {
        if (TextUtils.isEmpty(baseUrl)) {
            baseUrl = getDeclaredBaseUrl(service);
        }
        String key = service.getName() + "_" + baseUrl;
        Object retrofitService = lruOverseasRetrofitCacheService.get(key);
        if (retrofitService == null) {
            retrofitService = getRetrofit(baseUrl).create(service);
            lruOverseasRetrofitCacheService.put(key, retrofitService);
        }
        return (T) retrofitService;
    }

    public static <T> T getRetrofitService(Class<T> service) {
        return (T) getRetrofitService(service, null);
    }

    public static String getDeclaredBaseUrl(Class<?> service) {
        String baseUrl = BASE_URL;
        Field field = ReflectUtils.getField(service, "BASE_URL", false, false);
        if (field != null) {
            try {
                baseUrl = (String) field.get(field.getName());
            } catch (IllegalAccessException e) {
                LogUtils.e(e);
            }
        }
        return baseUrl;
    }

    public static <T> Disposable request(
            Observable<T> observable,
            Consumer<? super Disposable> onSubscribe,
            Action onFinally,
            Consumer<? super T> onNext,
            Consumer<? super Throwable> onError
    ) {
        return observable.subscribeOn(Schedulers.io())
                .doOnSubscribe(onSubscribe)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(onFinally)
                .subscribe(onNext, onError);
    }

}
