package com.rhino.ui.utils.crash;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.rhino.ui.utils.LogUtils;
import com.rhino.ui.utils.TimeUtils;
import com.rhino.ui.utils.ToastUtils;

import java.io.File;
import java.io.Serializable;

/**
 * @author LuoLin
 * @since Create on 2018/10/10.
 */
public class DefaultCrashHandler implements Serializable {


    @NonNull
    public String getDebugDirectory(Context context) {
        File file = context.getExternalFilesDir(null);
        if (file != null) {
            return file.getParent() + File.separator + "log_crash";
        }
        return "";
    }

    @NonNull
    public String getDebugFileName() {
        return TimeUtils.formatTime(System.currentTimeMillis(), "yyyyMMddHHmmss") + ".txt";
    }

    public void onCrashServerCreate() {
        LogUtils.d("onCrashServerCreate");
    }

    public void onCrashServerStart(Context context, @Nullable String debugFilePath, @Nullable String debugText) {
        LogUtils.d("onCrashServerStart: debugFilePath = " + debugFilePath);
        ToastUtils.show("很抱歉，程序出现异常，即将退出");
        Intent intent = new Intent(context, CrashTipsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(CrashService.KEY_CRASH_HANDLE, this);
        intent.putExtra(CrashService.KEY_DEBUG_TEXT, debugText);
        context.startActivity(intent);
    }

    public void onCrashServerDestroy() {
        LogUtils.d("onCrashServerDestroy");
    }

    public Class<?> getRestartActivity() {
        return null;
    }
}
