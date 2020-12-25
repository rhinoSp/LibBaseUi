package com.rhino.ui.utils.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.rhino.log.LogUtils;

/**
 * <p>The utils of screen</p>
 *
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public class ScreenUtils {

    private static boolean initValid = false;
    private static int widthPixels;
    private static int heightPixels;

    /**
     * init widthPixels and heightPixels
     *
     * @param ctx the context
     */
    public static void init(Context ctx) {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        if (outMetrics.widthPixels > 0) {
            widthPixels = outMetrics.widthPixels;
            initValid = true;
        }
        if (outMetrics.heightPixels > 0) {
            heightPixels = outMetrics.heightPixels;
            initValid = true;
        }
    }

    /**
     * get screen width
     *
     * @param ctx the context
     * @return the screen width
     */
    public static int getScreenWidth(Context ctx) {
        if (!initValid) {
            init(ctx);
        }
        return widthPixels;
    }

    /**
     * get screen height
     *
     * @param ctx the context
     * @return the screen height
     */
    public static int getScreenHeight(Context ctx) {
        if (!initValid) {
            init(ctx);
        }
        return heightPixels;
    }

    /**
     * Get the screen brightness.
     *
     * @param activity the activity
     * @return the screen brightness
     */
    public static int getScreenBrightness(Activity activity) {
        int value = 0;
        ContentResolver cr = activity.getContentResolver();
        try {
            value = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            LogUtils.e(e);
        }
        return value;
    }

    /**
     * Set the screen brightness.
     *
     * @param activity the activity
     * @param value    the value of screen brightness
     */
    public static void setScreenBrightness(Activity activity, int value) {
        value = 0 >= value ? 0 : value;
        value = 255 <= value ? 255 : value;
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        params.screenBrightness = value / 255f;
        activity.getWindow().setAttributes(params);
    }

    /**
     * change dp to px
     *
     * @param ctx     the context
     * @param dpValue the dp value
     * @return the px value
     */
    public static int dip2px(Context ctx, float dpValue) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * change px to dp
     *
     * @param ctx     the context
     * @param pxValue the px value
     * @return the dp value
     */
    public static int px2dip(Context ctx, float pxValue) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * change px to sp
     *
     * @param ctx     the context
     * @param pxValue the px value
     * @return the sp value
     */
    public static int px2sp(Context ctx, float pxValue) {
        final float fontScale = ctx.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * change sp to px
     *
     * @param ctx     the context
     * @param spValue the sp value
     * @return the px value
     */
    public static int sp2px(Context ctx, float spValue) {
        final float fontScale = ctx.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

}
