package com.rhino.ui.utils.crash;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;

/**
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public interface ICrashHandler extends Serializable {

    @NonNull
    String getDebugDirectory();

    @NonNull
    String getDebugFileName();

    @Nullable
    String getErrorDesc();

    void onCrashServerCreate();

    void onCrashServerStart(@Nullable String debugFilePath);

    void onCrashServerDestroy();

}
