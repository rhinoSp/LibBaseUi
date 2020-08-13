package com.rhino.ui.utils;

import com.rhino.log.LogUtils;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 自动生成dimens，生成格式如下：
 *
 * <dimen name="dp_m_100">-100dp</dimen>
 * <dimen name="dp_m_99_5">-99.5dp</dimen>
 * <dimen name="dp_7">7dp</dimen>
 * <dimen name="dp_7_5">7.5dp</dimen>
 *
 * <dimen name="sp_10">10sp</dimen>
 * <dimen name="sp_11">11sp</dimen>
 *
 * @author LuoLin
 * @since Create on 2019/4/12.
 */
public class EasyCreateDimen {

    /**
     * dimens输出路径
     */
    public static final String OUTPUT_PATH = "D:\\Workspace\\GitHub\\LibBaseUi\\libBaseUi\\src\\main\\res\\values";
    /**
     * dimens输出文件名dp
     */
    public static final String OUTPUT_FILE_NAME_DP = "dimens_dp.xml";
    /**
     * dimens输出文件名sp
     */
    public static final String OUTPUT_FILE_NAME_SP = "dimens_sp.xml";
    /**
     * 最小dp
     */
    public static final float DP_MIN_VALUE = -100;
    /**
     * 最大dp
     */
    public static final float DP_MAX_VALUE = 400;
    /**
     * 间隔dp
     */
    public static final float DP_SPACE_VALUE = 0.5f;
    /**
     * 最小sp
     */
    public static final float SP_MIN_VALUE = 0;
    /**
     * 最大sp
     */
    public static final float SP_MAX_VALUE = 100;
    /**
     * 间隔sp
     */
    public static final float SP_SPACE_VALUE = 1;

    /**
     * Main
     */
    public static void main(String[] args) {
        String dpContent = createDimens("dp", DP_MIN_VALUE, DP_MAX_VALUE, DP_SPACE_VALUE);
        writeFile(OUTPUT_PATH + File.separator + OUTPUT_FILE_NAME_DP, dpContent);

        String spContent = createDimens("sp", SP_MIN_VALUE, SP_MAX_VALUE, SP_SPACE_VALUE);
        writeFile(OUTPUT_PATH + File.separator + OUTPUT_FILE_NAME_SP, spContent);
    }

    /**
     * 创建dimens内容
     */
    public static String createDimens(String unit, float min, float max, float space) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<resources>\n\n");
        for (float i = min; i <= max; ) {
            stringBuilder.append(createDimen(unit, i));
            i += space;
        }
        stringBuilder.append("\n</resources>");
        return stringBuilder.toString();
    }

    /**
     * 创建dimen内容
     */
    public static String createDimen(String unit, float value) {
        String v = String.valueOf(value);
        if (v.endsWith(".0")) {
            // 去掉末尾的0
            v = v.replace(".0", "");
        }
        return new StringBuilder().append("    ")
                .append("<dimen name=\"")
                .append("_")
                .append(unit)
                .append("_")
                .append(String.valueOf(v).replace("-", "m_").replace(".", "_"))
                .append("\">")
                .append(v)
                .append(unit)
                .append("</dimen>")
                .append("\n")
                .toString();
    }

    /**
     * 写文件
     *
     * @param filePath    文件路径
     * @param fileContent 文件内容
     * @return true 写入成功
     */
    public static boolean writeFile(String filePath, String fileContent) {
        System.out.println("输出文件：" + filePath);
        FileOutputStream mFileOutputStream;
        try {
            mFileOutputStream = new FileOutputStream(filePath);
            mFileOutputStream.write(fileContent.getBytes());
            mFileOutputStream.close();
        } catch (Exception e) {
            LogUtils.e(e);
            return false;
        }
        return true;
    }

}
