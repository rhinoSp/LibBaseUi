package com.rhino.ui.utils;

import android.content.Context;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.rhino.log.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>The utils of String</p>
 *
 * @author LuoLin
 * @since Create on 2018/9/27.
 **/
public class StringUtils {

    /**
     * 返回非空，非"null"字符串
     *
     * @param value 原字符串
     * @return 结果
     */
    public static String notNullAndEmpty(String value) {
        return notNullAndEmpty(value, "未知");
    }

    /**
     * 返回非空，非"null"字符串
     *
     * @param value   原字符串
     * @param replace 替换字符串
     * @return 结果
     */
    public static String notNullAndEmpty(String value, String replace) {
        return TextUtils.isEmpty(value) || "null".equals(value) ? replace : value;
    }

    /**
     * 根据string关键字查找字符串
     *
     * @param context 上下文
     * @param key     string关键字
     * @return 字符串
     */
    public static String getStringByKey(Context context, String key) {
        if (null == key) {
            return null;
        }
        int id = context.getResources().getIdentifier(key, "string", context.getPackageName());
        String value = null;
        try {
            value = context.getString(id);
        } catch (Exception e) {
            System.out.println("Not found string resource ID #0x" + id);
        }
        return value;
    }

    /**
     * 根据string-array关键字和index查找字符串
     *
     * @param context  上下文
     * @param arrayKey string-array关键字
     * @param index    索引值
     * @return 字符串
     */
    public static String getStringByKey(Context context, String arrayKey, int index) {
        if (null == arrayKey || 0 > index) {
            return null;
        }
        int id = context.getResources().getIdentifier(arrayKey, "array", context.getPackageName());
        String[] value = null;
        try {
            value = context.getResources().getStringArray(id);
        } catch (Exception e) {
            System.out.println("Not found string resource ID #0x" + id);
        }
        if (null != value && index < value.length) {
            return value[index];
        }
        return null;
    }

    /**
     * 获取表达式的值，如：width="33"，这里将返回字符串"33"
     *
     * @param exp 表达式
     * @return value
     */
    public static String getValueInExp(String exp) {
        try {
            Pattern p = Pattern.compile("\"(.*?)\"");
            Matcher m = p.matcher(exp);
            if (m.find()) {
                return m.group(1);
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return "";
    }

    /**
     * 数字字符数组转Integer列表
     *
     * @param arr String[]
     * @return list
     */
    public static List<Integer> stringArray2Int(String[] arr) {
        if (null == arr) {
            return null;
        }
        List<Integer> ints = new ArrayList<>();
        try {
            for (int i = 0; i < arr.length; i++) {
                ints.add(Integer.parseInt(arr[i]));
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return ints;
    }

    /**
     * 百分比字符串转为0-100的整数
     *
     * @param floatStr 百分比字符串，如：0.32132
     * @return int
     */
    public static int string2PercentInt(String floatStr) {
        try {
            return (int) (100 * Float.valueOf(floatStr));
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return 0;
    }

    /**
     * 删除字符串中的空白符
     *
     * @param content String
     * @return String
     */
    public static String removeBlanks(String content) {
        if (content == null) {
            return null;
        }
        StringBuilder buff = new StringBuilder();
        buff.append(content);
        for (int i = buff.length() - 1; i >= 0; i--) {
            if (' ' == buff.charAt(i)
                    || ('\n' == buff.charAt(i))
                    || ('\t' == buff.charAt(i))
                    || ('\r' == buff.charAt(i))) {
                buff.deleteCharAt(i);
            }
        }
        return buff.toString();
    }

    /**
     * 比较两个字符串是否相同
     *
     * @param s1 串1
     * @param s2 串2
     * @return True 相同
     */
    public static boolean equalString(String s1, String s2) {
        if (null == s1) {
            return null == s2;
        }
        return s1.equals(s2);
    }

    /**
     * 是否为中文字符
     *
     * @param c char
     * @return True 是
     */
    public static boolean isChineseChar(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }


    /**
     * 字符串数组转都好分割的字符串
     */
    @NonNull
    public static String arrayToString(String[] arr) {
        StringBuilder sb = new StringBuilder();
        if (arr != null && arr.length > 0) {
            for (int i = 0; i < arr.length; i++) {
                if (i < arr.length - 1) {
                    sb.append(arr[i] + ",");
                } else {
                    sb.append(arr[i]);
                }
            }
        }
        return sb.toString();
    }

}
