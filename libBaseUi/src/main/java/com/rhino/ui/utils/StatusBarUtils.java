package com.rhino.ui.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.ColorInt;

import java.lang.reflect.Field;

/**
 * <p>The utils of StatusBar</p>
 *
 * @author LuoLin
 * @since Create on 2018/6/12.
 */
public class StatusBarUtils {

    /**
     * get the status height
     *
     * @param ctx the context
     * @return the status bar height
     */
    public static int getStatusBarHeight(Context ctx) {
        int result = 0;
        int resourceId = ctx.getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            result = ctx.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @SuppressWarnings("all")
    public static void setStatusBarColor(Activity activity, @ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                Class decorViewClazz = Class.forName("com.android.internal.policy.DecorView");
                Field field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor");
                field.setAccessible(true);
                field.setInt(activity.getWindow().getDecorView(), color);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
