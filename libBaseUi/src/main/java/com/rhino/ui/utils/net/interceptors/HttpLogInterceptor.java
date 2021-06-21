package com.rhino.ui.utils.net.interceptors;

import com.rhino.log.LogUtils;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * @author LuoLin
 * @since Create on 2021/6/19
 **/
public class HttpLogInterceptor implements Interceptor {

    private String tag = "HTTP";
    private static Charset UTF8 = Charset.forName("UTF-8");

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    public static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 15; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            LogUtils.e(e);
            return false; // Truncated UTF-8 sequence.
        }
    }

    public HttpLogInterceptor(String tag) {
        this.tag = tag;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        ArrayList<String> logList = new ArrayList<>();

        Request request = chain.request();
        RequestBody requestBody = request.body();
        logList.add(String.format("【%s】 【%s】", request.method(), request.url().toString()));

        appendHeaders(logList, request.headers());

        if (requestBody != null) {
            StringBuilder sb = new StringBuilder();
            if (requestBody instanceof MultipartBody) {
                MultipartBody multipartBody = (MultipartBody) requestBody;
                for (MultipartBody.Part part : multipartBody.parts()) {
                    for (int i = 0; i < part.headers().size(); i++) {
                        sb.append(String.format("【%s = %s】\n", part.headers().name(i), part.headers().value(i)));
                    }
                    MediaType partBody = part.body().contentType();
                    String type = partBody != null ? partBody.type().toLowerCase() : "";
                    if (partBody != null
                            && (type.equals("video") || type.equals("image") || type.equals("file"))) {
                        sb.append("*******************************************************************\n");
                        sb.append("***************这里是文件的数据，省略输出**************************\n");
                        sb.append("*******************************************************************\n\n");
                    } else {
                        sb.append(String.format("【MultipartBody = %s】", formatBodyToString(part.body())));
                    }
                    sb.append("\n\n");
                }
            } else {
                sb.append(formatBodyToString(requestBody));
            }
            logList.add("↓↓↓↓↓↓↓↓↓↓↓↓【Request Body】↓↓↓↓↓↓↓↓↓↓↓");
            logList.add(sb.toString());
        }
        logList.add("↓↓↓↓↓↓↓↓↓↓↓↓【END Request】↓↓↓↓↓↓↓↓↓↓↓");

        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            logList.add("HTTP FAILED: " + e.toString());
            httpLog(logList);
            throw e;
        }

        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();

        logList.add(String.format("【code = %d, %dms】", response.code(), tookMs));
        appendHeaders(logList, response.headers());
        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE); // Buffer the entire body.
        Buffer buffer = source.buffer();
        Charset charset = UTF8;
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            try {
                charset = contentType.charset(UTF8);
            } catch (UnsupportedCharsetException e) {
                logList.add("Couldn't decode the response body; charset is likely malformed.");
                logList.add("END HTTP");
                return response;
            }
        }
        if (!isPlaintext(buffer)) {
            logList.add("END HTTP (binary " + buffer.size() + "-byte body omitted)");
            return response;
        }
        if (contentLength != 0L) {
            logList.add("↓↓↓↓↓↓↓↓↓↓↓↓【Response Data】↓↓↓↓↓↓↓↓↓↓↓");
            logList.add(buffer.clone().readString(charset));
        }
        logList.add("END HTTP (" + buffer.size() + " -byte body)");
        httpLog(logList);
        return response;
    }

    private void httpLog(ArrayList<String> logList) {
        LogUtils.i(tag, "┌──────────────────────────────────────────────────────────────────────");
        for (String log : logList) {
            LogUtils.i(tag, "│" + log);
        }
        LogUtils.i(tag, "└──────────────────────────────────────────────────────────────────────");
    }

    private String formatBodyToString(RequestBody requestBody) {
        String result = "";
        try {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }
            if (charset != null && isPlaintext(buffer)) {
                result = buffer.readString(charset);
            }
        } catch (IOException e) {
            result = "解析异常" + e.toString();
        }
        return result;
    }

    private void appendHeaders(ArrayList<String> logList, Headers headers) {
        for (int i = 0; i < headers.size(); i++) {
            logList.add(String.format("【%s = %s】", headers.name(i), headers.value(i)));
        }
    }

}
