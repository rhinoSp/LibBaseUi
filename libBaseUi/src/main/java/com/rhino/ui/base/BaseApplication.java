package com.rhino.ui.base;

import android.app.Application;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.rhino.ui.BuildConfig;
import com.rhino.ui.utils.LogUtils;
import com.rhino.ui.utils.TimeUtils;
import com.rhino.ui.utils.ToastUtils;
import com.rhino.ui.utils.crash.CrashHandlerUtils;
import com.rhino.ui.utils.crash.CrashService;
import com.rhino.ui.utils.crash.CrashTipsActivity;
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
        init();
    }

    public void init() {
        LogUtils.init(getApplicationContext(), BuildConfig.DEBUG, false);
        ToastUtils.init(getApplicationContext());
        CrashHandlerUtils.getInstance().init(getApplicationContext(), this);
    }

    @NonNull
    @Override
    public String getDebugDirectory() {
        if (getInstance() != null && getInstance().getApplicationContext() != null) {
            File file = getInstance().getApplicationContext().getExternalFilesDir(null);
            if (file != null) {
                return file.getParent() + File.separator + "log_crash";
            }
        }
        return "";
    }

    @NonNull
    @Override
    public String getDebugFileName() {
        return TimeUtils.formatTime(System.currentTimeMillis(), "yyyyMMddHHmmss") + ".txt";
    }

    @Override
    public void onCrashServerCreate() {
        LogUtils.d("onCrashServerCreate");
    }

    @Override
    public void onCrashServerStart(@Nullable String debugFilePath, @Nullable String debugText) {
        LogUtils.d("onCrashServerStart: debugFilePath = " + debugFilePath);
        Intent intent = new Intent(getInstance().getApplicationContext(), CrashTipsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(CrashService.KEY_CRASH_HANDLE, this);
        intent.putExtra(CrashService.KEY_DEBUG_TEXT, debugText);
        getInstance().startActivity(intent);
    }

    @Override
    public void onCrashServerDestroy() {
        LogUtils.d("onCrashServerDestroy");
    }

    @Override
    public Class<?> getRestartActivity() {
        return null;
    }
}
