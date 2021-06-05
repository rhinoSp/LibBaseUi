package com.rhino.ui.demo;

import android.os.Build;

import com.rhino.ui.impl.IAppInfo;
import com.rhino.ui.utils.DeviceUtils;

/**
 * @author LuoLin
 * @since Create on 2021/6/5
 **/
public class AppInfo implements IAppInfo {

    @Override
    public String getVersionName() {
        return BuildConfig.VERSION_NAME;
    }

    @Override
    public int getVersionCode() {
        return BuildConfig.VERSION_CODE;
    }

    @Override
    public String getDeviceModel() {
        return Build.MODEL;
    }

    @Override
    public String getDeviceId() {
        return DeviceUtils.getUUID(App.getAppContext());
    }

    @Override
    public int getSDKVersion() {
        return Build.VERSION.SDK_INT;
    }

    @Override
    public boolean isDebug() {
        return BuildConfig.DEBUG;
    }
}
