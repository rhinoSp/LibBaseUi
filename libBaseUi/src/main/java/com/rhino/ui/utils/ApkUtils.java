package com.rhino.ui.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;

import com.rhino.log.LogUtils;
import com.rhino.ui.utils.ui.ToastUtils;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;


/**
 * <p>The utils of app</p>
 *
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public final class ApkUtils {

    public static String getAppPackageName(Context context) {
        if (null != context) {
            return context.getPackageName();
        }
        return null;
    }

    public static String getAppName(Context context) {
        PackageManager packageManager;
        try {
            packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(
                    context.getPackageName(), 0);
            return (String) packageManager.getApplicationLabel(applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e(e);
        }
        return "";
    }

    public static String getAppVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e(e);
        }
        return "1.0";
    }

    public static int getAppVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e(e);
        }
        return 0;
    }

    public static boolean isApkDebuggable(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return false;
    }

    public static void installApk(Context context, String apkFilePath) {
        File apkFile = new File(apkFilePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(
                    context, context.getApplicationContext().getPackageName() + ".fileprovider", apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }

    @Nullable
    public static String getRunningProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> rapi = mActivityManager.getRunningAppProcesses();
        if (null != rapi) {
            for (ActivityManager.RunningAppProcessInfo appProcess : rapi) {
                if (appProcess.pid == pid) {
                    return appProcess.processName;
                }
            }
        }
        return null;
    }

    /**
     * 判断本应用是否存活
     * 如果需要判断本应用是否在后台还是前台用getRunningTask
     */
    public static boolean isAPPAlive(Context mContext) {
        boolean isAPPRunning = false;
        // 获取activity管理对象
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        // 获取所有正在运行的app
        List<ActivityManager.RunningAppProcessInfo> appProcessInfoList = activityManager.getRunningAppProcesses();
        // 遍历，进程名即包名
        for (ActivityManager.RunningAppProcessInfo appInfo : appProcessInfoList) {
            if (mContext.getPackageName().equals(appInfo.processName)) {
                isAPPRunning = true;
                break;
            }
        }
        return isAPPRunning;
    }

    /**
     * 启动其他app
     */
    public static void startOtherApp(Activity activity, String otherAppPackageName) {
        try {
            Intent intent = activity.getPackageManager().getLaunchIntentForPackage(otherAppPackageName);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            activity.startActivity(intent);
        } catch (Exception e) {
            ToastUtils.show("应用未安装！");
            LogUtils.e("打开应用" + otherAppPackageName + "失败", e);
        }
    }

    /**
     * 获取apk文件版本号
     */
    public static int getApkFileVersionCode(Context context, String apkFilePath) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(apkFilePath, PackageManager.GET_ACTIVITIES);
        if (packageInfo != null) {
            return packageInfo.versionCode;
        }
        return 0;
    }

    /**
     * 获取签名SHA1
     */
    public static String getSHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length() - 1);
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e(e);
        } catch (NoSuchAlgorithmException e) {
            LogUtils.e(e);
        }
        return null;
    }

    public static boolean isInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List runningProcesses;
        if (Build.VERSION.SDK_INT > 20) {
            runningProcesses = am.getRunningAppProcesses();
            if (runningProcesses == null) {
                return true;
            }
            Iterator var4 = runningProcesses.iterator();

            while (true) {
                ActivityManager.RunningAppProcessInfo processInfo;
                do {
                    if (!var4.hasNext()) {
                        return isInBackground;
                    }

                    processInfo = (ActivityManager.RunningAppProcessInfo) var4.next();
                } while (processInfo.importance != 100);

                String[] var6 = processInfo.pkgList;
                int var7 = var6.length;

                for (int var8 = 0; var8 < var7; ++var8) {
                    String activeProcess = var6[var8];
                    if (activeProcess.equals(context.getPackageName())) {
                        return false;
                    }
                }
            }
        } else {
            runningProcesses = am.getRunningTasks(1);
            ComponentName componentInfo = ((ActivityManager.RunningTaskInfo) runningProcesses.get(0)).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }
        return isInBackground;
    }


}
