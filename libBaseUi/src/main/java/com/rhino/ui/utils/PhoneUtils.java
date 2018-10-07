package com.rhino.ui.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

/**
 * <p>The utils of phone<p>
 *
 * @author LuoLin
 * @since Create on 2018/9/27.
 */
public class PhoneUtils {

    /**
     * Get the IMEI.
     *
     * @param context context
     * @return IMEI
     */
    @Nullable
    @SuppressLint("HardwareIds")
    public static String getImei(@NonNull Context context) {
        String imei = null;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            Object service = context.getSystemService(Context.TELEPHONY_SERVICE);
            if (service != null) {
                imei = ((TelephonyManager) service).getDeviceId();
            }
        }
        return imei;
    }


}
