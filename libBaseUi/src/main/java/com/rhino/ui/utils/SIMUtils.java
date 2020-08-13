package com.rhino.ui.utils;

import android.content.Context;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

import com.rhino.log.LogUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author rhino
 * @since Create on 2019/12/23.
 **/
public class SIMUtils {

    public static boolean hasSIMCard(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String simSer = tm.getSimSerialNumber();
        if (simSer == null || simSer.equals("")) {
            return false;
        }
        int simState = tm.getSimState();
        if (simState == TelephonyManager.SIM_STATE_ABSENT
                || simState == TelephonyManager.SIM_STATE_UNKNOWN) {
            return false;
        }
        String simOperator = tm.getSimOperator();
        if (simOperator == null || simOperator.equals("")) {
            return false;
        }
        return true;
    }

    /**
     * Returns the number of phones available.
     * Returns 1 for Single standby mode (Single SIM functionality)
     * Returns 2 for Dual standby mode.(Dual SIM functionality)
     */
    public static int getSimCardCount(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Class cls = mTelephonyManager.getClass();
        try {
            Method mMethod = cls.getMethod("getSimCount");
            mMethod.setAccessible(true);
            return (int) mMethod.invoke(mTelephonyManager);
        } catch (NoSuchMethodException e) {
            LogUtils.e(e);
        } catch (InvocationTargetException e) {
            LogUtils.e(e);
        } catch (IllegalAccessException e) {
            LogUtils.e(e);
        }
        return -1;
    }

    public static int getAvailableSimCardCount(Context context) {
        int count = 0;
        SubscriptionManager mSubscriptionManager = SubscriptionManager.from(context);
        for (int i = 0; i < getSimCardCount(context); i++) {
            SubscriptionInfo sir = mSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(i);
            if (sir != null) {
                count++;
            }
        }
        return count;
    }

}
