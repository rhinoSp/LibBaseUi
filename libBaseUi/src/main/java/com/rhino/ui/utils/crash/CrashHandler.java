package com.rhino.ui.utils.crash;

import android.content.Context;
import android.support.annotation.NonNull;

import com.rhino.log.crash.DefaultCrashHandler;
import com.rhino.ui.utils.ActivityUtils;
import com.rhino.ui.utils.FileConfig;

/**
 * @author LuoLin
 * @since Create on 2019/7/3.
 **/
public class CrashHandler extends DefaultCrashHandler {

    @NonNull
    @Override
    public String getDebugDirectory(Context context) {
        return FileConfig.LOG_FOLDER_PATH;
    }

    @Override
    public void uncaughtException(Throwable ex) {
        ActivityUtils.getInstance().exit();
    }
}
