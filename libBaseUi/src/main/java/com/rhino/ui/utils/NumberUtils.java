package com.rhino.ui.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.math.BigDecimal;

/**
 * <p>The utils of number<p>
 *
 * @author LuoLin
 * @since Create on 2018/9/27.
 */
public class NumberUtils {

    /**
     * 保留有效数字
     * @param val 初始值
     * @param newScale 小数有效位数
     * @return 结果
     */
    public static float formatMaximumFractionDigits(double val, int newScale) {
        BigDecimal bg = new BigDecimal(val);
        return bg.setScale(newScale, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    /**
     * 保留有效数字
     * @param val 初始值
     * @param maximumFractionDigits 小数有效位数
     * @return 结果
     */
    public static float formatMaximumFractionDigits(@Nullable String val, int maximumFractionDigits) {
        double d = 0;
        try {
            d = Double.valueOf(val);
        } catch (Exception e) {
            d = 0;
        }
        return formatMaximumFractionDigits(d, maximumFractionDigits);
    }

    /**
     * 清除末尾的0
     * @param val 初始值
     * @return 结果
     */
    @NonNull
    public static String removeEndZero(@Nullable String val) {
        if (val != null && val.indexOf(".") > 0) {
            //去掉多余的0
            val = val.replaceAll("0+?$", "");
            //如最后一位是.则去掉
            val = val.replaceAll("[.]$", "");
            return val;
        }
        return "";
    }

}
