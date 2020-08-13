package com.rhino.ui.utils;

import android.content.Context;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;

import com.rhino.log.LogUtils;

/**
 * @author rhino
 * @since Create on 2019/12/21.
 **/
public class GPSUtils {

    /**
     * 打开或者关闭gps
     */
    public static void toggleGps(Context context, boolean open) {
        try {
            if (Build.VERSION.SDK_INT < 19) {
                Settings.Secure.setLocationProviderEnabled(context.getContentResolver(),
                        LocationManager.GPS_PROVIDER, open);
            } else {
                if (!open) {
                    Settings.Secure.putInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);
                } else {
                    Settings.Secure.putInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_HIGH_ACCURACY);
                }
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    /**
     * 判断gps是否处于打开状态
     */
    public static boolean isGpsOpen(Context context) {
        try {
            if (Build.VERSION.SDK_INT < 19) {
                LocationManager myLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                return myLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } else {
                int state = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);
                if (state == Settings.Secure.LOCATION_MODE_OFF) {
                    return false;
                } else {
                    return true;
                }
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return false;
    }
}
