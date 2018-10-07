package com.rhino.ui.base;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.rhino.ui.BuildConfig;
import com.rhino.ui.utils.LogUtils;
import com.rhino.ui.utils.TimeUtils;
import com.rhino.ui.utils.ToastUtils;
import com.rhino.ui.utils.crash.CrashHandlerUtils;
import com.rhino.ui.utils.crash.ICrashHandler;

import java.io.File;

/**
 * @author LuoLin
 * @since Create on 2018/10/7.
 **/
public class BaseApplication extends Application implements ICrashHandler {

    private static BaseApplication instance;
    public static BaseApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        LogUtils.init(getApplicationContext(), BuildConfig.DEBUG, false);
        ToastUtils.init(getApplicationContext());
        new CrashHandlerUtils(getApplicationContext(), this);
    }

    @NonNull
    @Override
    public String getDebugDirectory() {
        if (getInstance() != null && getInstance().getApplicationContext() != null) {
            File file = getInstance().getApplicationContext().getExternalFilesDir(null);
            if (file != null) {
                return file.getParent() + File.separator + "log";
            }
        }
        return "";
    }

    @NonNull
    @Override
    public String getDebugFileName() {
        return TimeUtils.formatTime(System.currentTimeMillis(), "yyyyMMdd_HH_mm_sss") + ".txt";
    }

    @Nullable
    @Override
    public String getErrorDesc() {
        return "很抱歉，程序出现异常，即将退出";
    }

    @Override
    public void onCrashServerCreate() {
        LogUtils.d("onCrashServerCreate");
    }

    @Override
    public void onCrashServerStart(@Nullable String debugFilePath) {
        LogUtils.d("onCrashServerStart: debugFilePath = " + debugFilePath);

        ToastUtils.show(getErrorDesc());
    }

    @Override
    public void onCrashServerDestroy() {
        LogUtils.d("onCrashServerDestroy");
    }

}
