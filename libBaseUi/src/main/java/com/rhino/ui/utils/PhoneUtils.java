package com.rhino.ui.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.telephony.SmsManager;
import android.text.TextUtils;

import java.util.List;

/**
 * <p>The utils of phone<p>
 *
 * @author LuoLin
 * @since Create on 2018/9/27.
 */
public class PhoneUtils {

    public static String SMS_SEND_ACTION = "SMS_SEND_ACTION";
    public static String SMS_DELIVERED_ACTION = "SMS_DELIVERED_ACTION";

    /**
     * 打电话
     */
    public static boolean callPhone(Activity activity, String phoneNum) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !PermissionsUtils.checkSelfPermission(activity, new String[]{Manifest.permission.CALL_PHONE})) {
            PermissionsUtils.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE});
            return false;
        }
        if (TextUtils.isEmpty(phoneNum)) {
            return false;
        }
        //如果需要手动拨号将Intent.ACTION_CALL改为Intent.ACTION_DIAL（跳转到拨号界面，用户手动点击拨打）
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        activity.startActivity(intent);
        return true;
    }

    /**
     * 直接调用短信接口发短信
     */
    public static void sendSMS(Context context, String phoneNumber, String message) {
        // 获取短信管理器
        SmsManager smsManager = SmsManager.getDefault();
        // 拆分短信内容（手机短信长度限制）
        List<String> divideContents = smsManager.divideMessage(message);
        for (String text : divideContents) {
            /* 创建自定义Action常数的Intent(给PendingIntent参数之用) */
            Intent itSend = new Intent(SMS_SEND_ACTION);
            /* sentIntent参数为传送后接受的广播信息PendingIntent */
            PendingIntent mSendPI = PendingIntent.getBroadcast(context,
                    (int) System.currentTimeMillis(), itSend,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            /* deliveryIntent参数为送达后接受的广播信息PendingIntent */
            Intent itDeliver = new Intent(SMS_DELIVERED_ACTION);
            /* deliveryIntent参数为送达后接受的广播信息PendingIntent */
            PendingIntent mDeliverPI = PendingIntent.getBroadcast(context,
                    (int) System.currentTimeMillis(), itDeliver,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            smsManager.sendTextMessage(phoneNumber, null, text,
                    mSendPI, mDeliverPI);
        }
    }

    /**
     * Return run background.
     * @param context context
     * @return true or false
     */
    public static boolean isRunBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return false;
        }
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }


}
