package com.rhino.ui.utils;

import android.text.TextUtils;

/**
 * <p>The utils of String</p>
 *
 * @author LuoLin
 * @since Create on 2018/9/27.
 **/
public class StringUtils {

    public static String notNull(String value) {
        return TextUtils.isEmpty(value) || value.equals("null") ? "" : value;
    }

}
