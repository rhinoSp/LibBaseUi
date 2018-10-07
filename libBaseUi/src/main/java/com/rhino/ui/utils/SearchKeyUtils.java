package com.rhino.ui.utils;

import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.text.TextUtils;


/**
 * @author LuoLin
 * @since Create on 2018/8/24.
 */
public class SearchKeyUtils {

    /**
     * 返回带有检索关键字的字符串
     *
     * @param text      原字符串
     * @param searchKey 检索关键字
     * @return 新字符串
     */
    @Nullable
    public static String formatWithSearchKey(String text, String searchKey) {
        return formatWithSearchKey(text, searchKey, 0xFF0000);
    }

    /**
     * 返回带有检索关键字的字符串
     *
     * @param text      原字符串
     * @param searchKey 检索关键字
     * @param searchKeyColor 检索关键字颜色
     * @return 新字符串
     */
    @Nullable
    public static String formatWithSearchKey(String text, String searchKey, @ColorInt int searchKeyColor) {
        if (!TextUtils.isEmpty(text) && !TextUtils.isEmpty(searchKey)) {
            int index = text.indexOf(searchKey);
            if (-1 != index && index + searchKey.length() <= text.length()) {
                String pyName = text.substring(index, index + searchKey.length());
                text = text.replaceAll(pyName, "<font color=\"" + searchKeyColor + "\">" + pyName + "</font>");
            }
        }
        return text;
    }

}
