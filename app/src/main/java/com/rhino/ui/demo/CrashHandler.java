package com.rhino.ui.demo;

import android.content.Context;

import androidx.annotation.Nullable;

import com.rhino.log.crash.CrashTipsActivity;
import com.rhino.log.crash.DefaultCrashHandler;
import com.rhino.ui.utils.ActivityUtils;

/**
 * @author LuoLin
 * @since Create on 2018/10/10.
 */
public class CrashHandler extends DefaultCrashHandler {

    @Override
    public Class<?> getRestartActivity() {
        return MainActivity.class;
    }

    @Override
    public void onCrashCaught(Context context, @Nullable @org.jetbrains.annotations.Nullable String debugFilePath, @Nullable @org.jetbrains.annotations.Nullable String debugText) {
        CrashTipsActivity.startThis(context, this, debugFilePath, debugText);
    }

    @Override
    public void uncaughtException(Throwable ex) {
        ActivityUtils.getInstance().exit();
    }
}
