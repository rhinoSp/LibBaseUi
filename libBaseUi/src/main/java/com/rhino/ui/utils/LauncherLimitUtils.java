package com.rhino.ui.utils;


import com.rhino.ui.BuildConfig;


/**
 * @author LuoLin
 * @since Create on 2018/10/23.
 */
public class LauncherLimitUtils {

    public static void checkLauncherLimit(int dayAfter) {
        long limitTimestamp = TimeUtils.getTimeStampByTimeString(BuildConfig.BUILD_TIME, "yyyy-MM-dd")
                + dayAfter * TimeUtils.MILLISECONDS_PER_DAY;
        if (System.currentTimeMillis() > limitTimestamp) {
            throw new RuntimeException("The apk is invalid.");
        }
    }

}
