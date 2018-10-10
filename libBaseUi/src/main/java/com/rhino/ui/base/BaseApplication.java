package com.rhino.ui.base;

import android.app.Application;

import com.rhino.ui.utils.LogUtils;
import com.rhino.ui.utils.ToastUtils;
import com.rhino.ui.utils.crash.CrashHandlerUtils;
import com.rhino.ui.utils.crash.DefaultCrashHandler;


/**
 * @author LuoLin
 * @since Create on 2018/10/7.
 **/
public abstract class BaseApplication extends Application {

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

    public void baseInit() {
        LogUtils.init(getApplicationContext(), isDebug(), false);
        ToastUtils.init(getApplicationContext());
        CrashHandlerUtils.getInstance().init(getApplicationContext(), new DefaultCrashHandler());
    }

}
