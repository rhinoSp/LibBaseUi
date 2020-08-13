package com.rhino.ui.utils;

import android.content.Context;
import android.net.Uri;
import android.provider.Settings;

import com.rhino.log.LogUtils;

/**
 * android.permission.WRITE_SETTINGS
 *
 * @author rhino
 * @since Create on 2019/8/20.
 **/
public class ScreenLockTimeUtils {

    /**
     * 获取系统休眠时间
     */
    public static int getDormant(Context context) {
        int result = 0;
        try {
            result = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (Settings.SettingNotFoundException e) {
            LogUtils.e(e);
        }
        return result;
    }

    /**
     * 设置系统的休眠时间
     */
    public static void setDormant(Context context, int time) {
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT, time);
        Uri uri = Settings.System
                .getUriFor(Settings.System.SCREEN_OFF_TIMEOUT);
        context.getContentResolver().notifyChange(uri, null);
    }
}
