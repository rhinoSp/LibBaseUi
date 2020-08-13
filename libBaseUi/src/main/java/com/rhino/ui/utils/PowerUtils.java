package com.rhino.ui.utils;

import android.content.Context;
import android.os.PowerManager;

/**
 * @author rhino
 * @since Create on 2019/12/26.
 **/
public class PowerUtils {

    /**
     * 闹醒屏幕
     */
    public static void wake(Context context) {
        //获取电源管理器对象
        PowerManager pm=(PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.SCREEN_DIM_WAKE_LOCK,"bright");
        //点亮屏幕
        wl.acquire();
        //释放
        wl.release();
    }
}
