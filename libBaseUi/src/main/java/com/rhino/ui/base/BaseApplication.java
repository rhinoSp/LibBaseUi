package com.rhino.ui.base;

import android.app.Application;
import android.content.Context;

import com.rhino.log.LogUtils;
import com.rhino.log.crash.CrashHandlerUtils;
import com.rhino.ui.utils.AppBackgroundUtils;
import com.rhino.ui.utils.FileConfig;
import com.rhino.ui.utils.SharedPreferencesUtils;
import com.rhino.ui.utils.crash.CrashHandler;
import com.rhino.ui.utils.ui.ToastUtils;

/**
 * @author LuoLin
 * @since Create on 2018/10/7.
 **/
public abstract class BaseApplication extends Application implements AppBackgroundUtils.OnAppBackgroundRunListener {

    private static BaseApplication instance;

    public static BaseApplication getInstance() {
        return instance;
    }

    public abstract boolean isDebug();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        baseInit();
    }

    @Override
    public void onAppBackgroundRun(boolean backgroundRun) {
        if (backgroundRun) {
            LogUtils.i("正在后台运行");
        } else {
            LogUtils.i("正在前台运行");
        }
    }

    public void baseInit() {
        FileConfig.initFolderPath();
        LogUtils.init(getApplicationContext(), isDebug(), false);
        ToastUtils.init(getApplicationContext());
        CrashHandlerUtils.getInstance().init(getApplicationContext(), new CrashHandler());
        SharedPreferencesUtils.getInstance().init(getApplicationContext());
        AppBackgroundUtils.registerActivityLifecycleCallbacks(this, this);
    }

    public static Context getAppContext() {
        return getInstance().getApplicationContext();
    }

}
