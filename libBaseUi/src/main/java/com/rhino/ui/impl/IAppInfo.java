package com.rhino.ui.impl;

/**
 * @author LuoLin
 * @since Create on 2021/6/5
 **/
public interface IAppInfo {

    String getVersionName();

    int getVersionCode();

    String getDeviceModel();

    String getDeviceId();

    int getSDKVersion();

    boolean isDebug();

}
