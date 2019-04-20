package com.rhino.ui.utils;

/**
 * @author LuoLin
 * @since Create on 2018/10/23.
 */
public class LauncherLimitUtils {

    public static void checkLauncherLimit(String buildTime, int dayAfter) {
        long limitTimestamp = TimeUtils.getTimeStampByTimeString(buildTime, "yyyy-MM-dd")
                + dayAfter * TimeUtils.MILLISECONDS_PER_DAY;
        if (System.currentTimeMillis() > limitTimestamp) {
            throw new RuntimeException("The apk is invalid.");
        }
    }

}
