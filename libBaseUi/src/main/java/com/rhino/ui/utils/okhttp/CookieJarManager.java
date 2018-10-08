package com.rhino.ui.utils.okhttp;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;


/**
 * @author LuoLin
 * @since Create on 2018/8/20.
 */
public class CookieJarManager implements CookieJar {

    private List<Cookie> mCookieList = new ArrayList<>();
    private String[] cookieUrl;

    public CookieJarManager(String[] cookieUrl) {
        this.cookieUrl = cookieUrl;
    }

    private boolean isSaveCookie(String url) {
        for (String cookieUrl : cookieUrl) {
            if (url.contains(cookieUrl)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void saveFromResponse(okhttp3.HttpUrl httpUrl, List<Cookie> list) {
        String url = httpUrl.uri().toString();
        if (isSaveCookie(url)) {
//    		mCookieList.clear(); // 这里只保存一次cookie
            mCookieList.addAll(list);
        }
    }

    @Override
    public List<Cookie> loadForRequest(okhttp3.HttpUrl httpUrl) {
        return mCookieList;
    }
}
