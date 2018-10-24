package com.rhino.ui.utils;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

/**
 * <p>The utils of Vibrator<p>
 *
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public class VibratorUtils {

    private Vibrator vibrator;

    public static VibratorUtils instance;

    public static VibratorUtils getInstense(Context context) {
        if (null == instance) {
            instance = new VibratorUtils(context);
        }
        return instance;
    }

    public VibratorUtils(Context context) {
        vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
    }


    /**
     * 检测当前硬件是否有vibrator
     *
     * @return true：有 false：没有
     */
    public boolean hasVibrator() {
        return vibrator.hasVibrator();
    }

    /**
     * 开始震动
     *
     * @param milliseconds 震动时间， 单位ms
     */
    public void startVibrator(long milliseconds) {
        vibrator.vibrate(milliseconds);
    }

    /**
     * 按照指定的模式去震动
     *
     * @param pattern 第一个参数为等待指定时间后开始震动，震动时间为第二个参数。后边的参数依次为等待震动和震动的时间
     * @param repeat  -1为不重复，0为一直震动
     */
    public void startVibrator(long[] pattern, int repeat) {
        vibrator.vibrate(pattern, repeat);
    }

    /**
     * 取消震动
     **/
    public void cancelVibrator() {
        vibrator.cancel();
    }
}
