package com.rhino.ui.utils.okhttp;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.rhino.ui.utils.LogUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * <p>The utils of HTTP.</p>
 *
 * @author LuoLin
 * @since Create on 2018/8/20.
 */
public class OkHttpRequestUtils {

    public static final String TAG = OkHttpRequestUtils.class.getSimpleName();
    public static final int DEFAULT_TIMEOUT_TIME = 60;

    public OkHttpClient mOkHttpClient;

    public OkHttpRequestUtils(String[] cookieUrl) {
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT_TIME, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT_TIME, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT_TIME, TimeUnit.SECONDS)
                .cookieJar(new CookieJarManager(cookieUrl))
                .build();
    }

    public void doPost(String url, Callback callback) {
        doPost(url, null, callback);
    }

    public void doPost(String url, HttpParams params, okhttp3.Callback callback) {
        Request.Builder requestBuilder = new Request.Builder().url(url);
        FormBody.Builder bodyBuilder = new FormBody.Builder();

        StringBuilder httpUrl = new StringBuilder(url);
        // 是否带有参数
        if (params != null) {
            // 反射得到参数对象
            Class<? extends HttpParams> clazz = params.getClass();
            // 获取参数对象所有属性
            Field fields[] = clazz.getDeclaredFields();
            httpUrl.append("?");
            for (Field field : fields) {
                // 突破private属性
                field.setAccessible(true);
                // 获取该字段的注解
                ParamField json = field.getAnnotation(ParamField.class);
                if (json != null && !TextUtils.isEmpty(json.value())) {
                    try {
                        httpUrl.append(json.value() + "="
                                + String.valueOf(field.get(params)) + "&");
                        bodyBuilder.add(json.value(), String.valueOf(field.get(params)));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (httpUrl.toString().endsWith("&")) {
            LogUtils.i(TAG, "httpUrl = " + httpUrl.delete(httpUrl.length() - 1, httpUrl.length()).toString());
        } else {
            LogUtils.i(TAG, "httpUrl = " + httpUrl.toString());
        }

        Request mRequest = requestBuilder.post(bodyBuilder.build()).build();
        mOkHttpClient.newCall(mRequest).enqueue(callback);
    }

    public void doPostByMap(String url, Map<String, String> paramsMap, Callback callback) {
        Request.Builder requestBuilder = new Request.Builder().url(url);
        FormBody.Builder bodyBuilder = new FormBody.Builder();

        StringBuilder httpUrl = new StringBuilder(url);
        if (paramsMap != null) {
            for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
                if (httpUrl.toString().equals(url)) {
                    httpUrl.append("?");
                }
                httpUrl.append(entry.getKey() + "=" + entry.getValue() + "&");
                bodyBuilder.add(entry.getKey(), entry.getValue());
            }
        }

        if (httpUrl.toString().endsWith("&")) {
            LogUtils.i(TAG, "httpUrl = " + httpUrl.delete(httpUrl.length() - 1, httpUrl.length()).toString());
        } else {
            LogUtils.i(TAG, "httpUrl = " + httpUrl.toString());
        }

        Request mRequest = requestBuilder.post(bodyBuilder.build()).build();
        mOkHttpClient.newCall(mRequest).enqueue(callback);
    }

    public void doGet(String url, Callback callback) {
        doGet(url, null, callback);
    }

    public void doGet(String url, HttpParams params, Callback callback) {
        String httpUrl = buildHttpUrl(url, params, null);
        LogUtils.i(TAG, "httpUrl = " + httpUrl);

        Request.Builder requestBuilder = new Request.Builder().url(httpUrl);
        Request request = requestBuilder.build();
        mOkHttpClient.newCall(request).enqueue(callback);
    }

    public void doGetByMap(String url, Map<String, String> paramsMap, Callback callback) {
        String httpUrl = buildHttpUrl(url, null, paramsMap);
        LogUtils.i(TAG, "httpUrl = " + httpUrl);

        Request.Builder requestBuilder = new Request.Builder().url(httpUrl);
        Request request = requestBuilder.build();
        mOkHttpClient.newCall(request).enqueue(callback);
    }

    private String buildHttpUrl(String url, HttpParams params, Map<String, String> paramsMap) {
        StringBuilder httpUrl = new StringBuilder(url);
        if (params != null) {
            Class<? extends HttpParams> clazz = params.getClass();
            Field fields[] = clazz.getDeclaredFields();
            httpUrl.append("?");
            for (Field field : fields) {
                field.setAccessible(true);
                ParamField json = field.getAnnotation(ParamField.class);
                if (json != null && !TextUtils.isEmpty(json.value())) {
                    try {
                        httpUrl.append(json.value() + "="
                                + String.valueOf(field.get(params)) + "&");
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
            if (httpUrl.toString().equals(url)) {
                httpUrl.append("?");
            }
            httpUrl.append(entry.getKey() + "=" + entry.getValue() + "&");
        }
        if (httpUrl.toString().endsWith("&")) {
            return httpUrl.toString().substring(0, httpUrl.length() - 1);
        }
        return httpUrl.toString();
    }

    public void uploadFile(final String url, Map<String, String> paramsMap, final File file, Callback callback) {
        String httpUrl = buildHttpUrl(url, null, paramsMap);
        LogUtils.i(TAG, "httpUrl = " + httpUrl);
        Request.Builder requestBuilder = new Request.Builder().url(httpUrl);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(MediaType.parse("multipart/form-data"), file))
                .build();
        Request request = requestBuilder.header("Authorization", "Client-ID " + UUID.randomUUID())
                .url(httpUrl)
                .post(new FileProgressRequestBody(requestBody, file, callback)).build();
        mOkHttpClient.newCall(request).enqueue(callback);
    }

    public class FileProgressRequestBody extends RequestBody {

        private RequestBody mDelegate;
        private Callback mCallback;
        private File mFile;

        public FileProgressRequestBody(RequestBody delegate, File file, Callback callback) {
            mDelegate = delegate;
            mFile = file;
            mCallback = callback;
        }

        @Override
        public MediaType contentType() {
            return mDelegate.contentType();
        }

        @Override
        public long contentLength() {
            try {
                return mDelegate.contentLength();
            } catch (IOException e) {
                return -1;
            }
        }

        @Override
        public void writeTo(@NonNull BufferedSink sink) throws IOException {
            BufferedSink bufferedSink = null;
            try {
                bufferedSink = Okio.buffer(new MyForwardingSink(sink));
                bufferedSink.timeout().timeout(120, TimeUnit.SECONDS);
                mDelegate.writeTo(bufferedSink);
                bufferedSink.flush();
                LogUtils.d(TAG, "[upload success] " + mFile.toString() + mDelegate.toString());
                mCallback.onSuccess(mFile);
            } catch (IOException e) {
                LogUtils.e(TAG, e.getMessage());
                mCallback.onError(e.getMessage());
                bufferedSink.close();
                sink.close();
                throw e;
            }
        }

        protected final class MyForwardingSink extends ForwardingSink {
            private long bytesWritten = 0;
            private int progressTemp = 0;

            public MyForwardingSink(Sink delegate) {
                super(delegate);
            }

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                bytesWritten += byteCount;
                int progress = (int) (100F * bytesWritten / contentLength());
                if (progress > progressTemp) {
                    LogUtils.d(TAG, "[upload progress] " + progress);
                    mCallback.onProgressChanged(progress);
                    progressTemp = progress;
                }
            }
        }
    }

}
