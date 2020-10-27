package com.rhino.ui.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.rhino.log.LogUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;


/**
 * <p>The utils of device<p>
 *
 * @author LuoLin
 * @since Create on 2018/9/27.
 */
public class DeviceUtils {

    private static String uuid;

    /**
     * 获取手机的唯一标识
     *
     * @param context Context
     * @return 唯一标识
     */
    public static String getDeviceId(Context context) {
        StringBuilder deviceSign = new StringBuilder();
        // IMEI
        String imei = getIMEI(context);
        if (!TextUtils.isEmpty(imei)) {
            deviceSign.append(imei);
            return deviceSign.toString();
        }
        // SN
        String sn = getSN(context);
        if (!TextUtils.isEmpty(sn)) {
            deviceSign.append(sn);
            return deviceSign.toString();
        }
        // UUID
        String uuid = getUUID(context);
        if (!TextUtils.isEmpty(uuid)) {
            deviceSign.append(uuid);
            return deviceSign.toString();
        }
        return deviceSign.toString();
    }

    /**
     * 得到全局唯一UUID
     *
     * @param context Context
     * @return UUID
     */
    public static String getUUID(Context context) {
        SharedPreferences mShare = context.getSharedPreferences("uuid", MODE_PRIVATE);
        if (mShare != null) {
            uuid = mShare.getString("uuid", "");
        }
        if (TextUtils.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString();
            mShare.edit().putString("uuid", uuid).apply();
        }
        return uuid;
    }

    /**
     * SN
     */
    @Nullable
    @SuppressLint({"HardwareIds", "MissingPermission"})
    public static String getSN(@NonNull Context context) {
        String imei = null;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            Object service = context.getSystemService(Context.TELEPHONY_SERVICE);
            if (service != null) {
                imei = ((TelephonyManager) service).getSimSerialNumber();
            }
        }
        return imei;
    }

    /**
     * IMEI
     */
    @Nullable
    @SuppressLint({"HardwareIds", "MissingPermission"})
    public static String getIMEI(@NonNull Context context) {
        String imei = null;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            Object service = context.getSystemService(Context.TELEPHONY_SERVICE);
            if (service != null) {
                imei = ((TelephonyManager) service).getDeviceId();
            }
        }
        return imei;
    }

    /**
     * 获取设备信息
     */
    public static String getDeviceInfo(String spit) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("主板：").append(BOARD());
        stringBuilder.append(spit).append("系统启动程序版本号：").append(BOOTLOADER());
        stringBuilder.append(spit).append("系统定制商：").append(BRAND());
        stringBuilder.append(spit).append("cpu指令集：").append(CPU_ABI());
        stringBuilder.append(spit).append("cpu指令集2：").append(CPU_ABI2());
        stringBuilder.append(spit).append("设备驱动名称：").append(DEVICE());
        stringBuilder.append(spit).append("显示屏参数：").append(DISPLAY());
        stringBuilder.append(spit).append("硬件识别码：").append(FINGERPRINT());
        stringBuilder.append(spit).append("硬件名称：").append(HARDWARE());
        stringBuilder.append(spit).append("设备主机地址: ").append(HOST());
        stringBuilder.append(spit).append("设备版本号：").append(ID());
        stringBuilder.append(spit).append("硬件制造商：").append(MANUFACTURER());
        stringBuilder.append(spit).append("型号：").append(MODEL());
        stringBuilder.append(spit).append("硬件序列号：").append(SERIAL());
        stringBuilder.append(spit).append("手机制造商：").append(PRODUCT());
        stringBuilder.append(spit).append("设备标签：").append(TAGS());
        stringBuilder.append(spit).append("时间: ").append(TIME());
        stringBuilder.append(spit).append("设备版本类型：").append(TYPE());
        stringBuilder.append(spit).append("设备用户名: ").append(USER());
        stringBuilder.append(spit).append("Android版本: ").append(VERSION_RELEASE());
        return stringBuilder.toString();
    }

    /**
     * 设备主板
     */
    public static String BOARD() {
        return android.os.Build.BOARD;
    }

    /**
     * 系统启动程序版本号
     */
    public static String BOOTLOADER() {
        return android.os.Build.BOOTLOADER;
    }

    /**
     * 系统定制商
     */
    public static String BRAND() {
        return android.os.Build.BRAND;
    }

    /**
     * cpu指令集
     */
    public static String CPU_ABI() {
        return android.os.Build.CPU_ABI;
    }

    /**
     * cpu指令集2
     */
    public static String CPU_ABI2() {
        return android.os.Build.CPU_ABI2;
    }

    /**
     * 设备驱动名称
     */
    public static String DEVICE() {
        return android.os.Build.DEVICE;
    }


    /**
     * 显示屏参数，设备显示的版本包（在系统设置中显示为版本号）和ID一样
     */
    public static String DISPLAY() {
        return android.os.Build.DISPLAY;
    }

    /**
     * 硬件识别码，设备的唯一标识，由设备的多个信息拼接合成
     */
    public static String FINGERPRINT() {
        return android.os.Build.FINGERPRINT;
    }

    /**
     * 设备硬件名称,一般和基板名称一样（BOARD）
     */
    public static String HARDWARE() {
        return android.os.Build.HARDWARE;
    }

    /**
     * 设备主机地址
     */
    public static String HOST() {
        return android.os.Build.HOST;
    }

    /**
     * 设备版本号
     */
    public static String ID() {
        return android.os.Build.ID;
    }

    /**
     * 型号 设备名称
     */
    public static String MODEL() {
        return android.os.Build.MODEL;
    }

    /**
     * 设备制造商
     */
    public static String MANUFACTURER() {
        return android.os.Build.MANUFACTURER;
    }

    /**
     * 整个产品的名称
     */
    public static String PRODUCT() {
        return android.os.Build.PRODUCT;
    }

    /**
     * 无线电固件版本号，通常是不可用的 显示unknown
     */
    public static String RADIO() {
        return android.os.Build.RADIO;
    }

    /**
     * 硬件序列号
     */
    public static String SERIAL() {
        return android.os.Build.SERIAL;
    }

    /**
     * SUPPORTED_32_BIT_ABIS
     */
    public static String[] SUPPORTED_32_BIT_ABIS() {
        return android.os.Build.SUPPORTED_32_BIT_ABIS;
    }

    /**
     * SUPPORTED_64_BIT_ABIS
     */
    public static String[] SUPPORTED_64_BIT_ABIS() {
        return android.os.Build.SUPPORTED_64_BIT_ABIS;
    }

    /**
     * SUPPORTED_ABIS
     */
    public static String[] SUPPORTED_ABIS() {
        return android.os.Build.SUPPORTED_ABIS;
    }

    /**
     * 设备标签。如release-keys 或测试的 test-keys
     */
    public static String TAGS() {
        return android.os.Build.TAGS;
    }

    /**
     * 时间
     */
    public static long TIME() {
        return android.os.Build.TIME;
    }

    /**
     * 设备版本类型  主要为"user" 或"eng"
     */
    public static String TYPE() {
        return android.os.Build.TYPE;
    }

    /**
     * 设备用户名 基本上都为android-build
     */
    public static String USER() {
        return android.os.Build.USER;
    }

    /**
     * BASE_OS
     */
    public static String VERSION_BASE_OS() {
        return android.os.Build.VERSION.BASE_OS;
    }

    /**
     * 设备当前的系统开发代号，一般使用REL代替
     */
    public static String VERSION_CODENAME() {
        return android.os.Build.VERSION.CODENAME;
    }

    /**
     * 系统源代码控制值，一个数字或者git hash值
     */
    public static String VERSION_INCREMENTAL() {
        return android.os.Build.VERSION.INCREMENTAL;
    }

    /**
     * PREVIEW_SDK_INT
     */
    public static int VERSION_PREVIEW_SDK_INT() {
        return android.os.Build.VERSION.PREVIEW_SDK_INT;
    }

    /**
     * Android版本。如4.1.2 或2.2 或2.3等
     */
    public static String VERSION_RELEASE() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 系统的API级别 一般使用下面大的SDK_INT 来查看
     */
    public static String VERSION_SDK() {
        return android.os.Build.VERSION.SDK;
    }

    /**
     * 系统的API级别 数字表示
     */
    public static int VERSION_SDK_INT() {
        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * SECURITY_PATCH
     */
    public static String VERSION_SECURITY_PATCH() {
        return android.os.Build.VERSION.SECURITY_PATCH;
    }

    /**
     * get system property
     */
    public static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            java.lang.Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            LogUtils.e("Unable to read sysprop " + propName, ex);
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    LogUtils.e("Exception while closing InputStream", e);
                }
            }
        }
        return line;
    }

    /**
     * 获取emui版本号
     */
    public static String getVersionEMUI() {
        Class<?> classType = null;
        String buildVersion = null;
        try {
            classType = Class.forName("android.os.SystemProperties");
            Method getMethod = classType.getDeclaredMethod("get", new Class<?>[]{String.class});
            buildVersion = (String) getMethod.invoke(classType, new Object[]{"ro.build.version.emui"});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buildVersion;
    }


}
