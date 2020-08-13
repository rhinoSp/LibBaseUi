package com.rhino.ui.utils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * @author rhino
 * @since Create on 2019/12/6.
 **/
public class BatteryUtils {

    /**
     * 实时获取电量
     */
    public static int getSystemBattery(Context context) {
        Intent batteryInfoIntent = context.registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryInfoIntent.getIntExtra("level", 0);
        int batterySum = batteryInfoIntent.getIntExtra("scale", 100);
        int percentBattery = 100 * level / batterySum;
        return percentBattery;
    }

}
