package com.rhino.ui.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.rhino.log.LogUtils;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.*;

/**
 * <p>The utils of number<p>
 *
 * @author LuoLin
 * @since Create on 2018/9/27.
 */
public class NumberUtils {

    /**
     * 保留有效数字
     *
     * @param val      初始值
     * @param newScale 小数有效位数
     * @return 结果
     */
    public static float formatMaximumFractionDigits(double val, int newScale) {
        BigDecimal bg = new BigDecimal(val);
        return bg.setScale(newScale, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    /**
     * 保留有效数字
     *
     * @param val                   初始值
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
     * 隐藏手机号码中间4位，用*代替
     *
     * @param phoneNumber 手机号码
     * @return 结果
     */
    public static String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() != 11) {
            return phoneNumber;
        }
        return phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(7);
    }

    /**
     * 强转int
     *
     * @param s int字符串
     * @return 结果
     */
    public static int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return 0;
    }

    /**
     * 强转float
     *
     * @param s float字符串
     * @return 结果
     */
    public static float parseFloat(String s) {
        try {
            return Float.parseFloat(s);
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return 0f;
    }

    /**
     * 清除末尾的0
     *
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

    /**
     * 是否为手机号码
     *
     * @param phone 手机号码，多个手机号码用“,”隔开
     * @return true 是
     */
    public static boolean isPhoneNO(String phone) {
        boolean flag = false;
        if (TextUtils.isEmpty(phone)) {
            return false;
        }
        String[] mobiles = phone.split(",");
        int len = mobiles.length;
        if (len == 1) {
            return matches("^((13[0-9])|(14[5,7,9])|(15[^4,\\D])|(17[0,1,3,5-8])|(18[0-9]))\\d{8}$", phone);
        } else {
            for (int i = 0; i < len; i++) {
                if (isPhoneNO(mobiles[i])) {
                    flag = true;
                } else {
                    flag = false;
                }
            }
        }
        return flag;
    }

    /**
     * 是否为数字字符串
     *
     * @param str 字符串
     * @return true 是
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

}
