package com.rhino.ui.utils.crash;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;

/**
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public class CrashService extends Service {

    public static final String KEY_CRASH_HANDLE = "Crash.Handler";
    public static final String KEY_DEBUG_TEXT = "Crash.debug.text";
    private static final String TAG = "CrashService";

    private ICrashHandler mICrashHandler;
    private String mDebugText;

    public static void startThisService(Context context, @NonNull ICrashHandler crashHandler, String debugText) {
        Intent intent = new Intent(context, CrashService.class);
        intent.putExtra(KEY_CRASH_HANDLE, crashHandler);
        intent.putExtra(KEY_DEBUG_TEXT, debugText);
        context.startService(intent);
        Log.d(TAG, "startThisService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        if (null != mICrashHandler) {
            mICrashHandler.onCrashServerCreate();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        mICrashHandler = (ICrashHandler) intent.getSerializableExtra(KEY_CRASH_HANDLE);
        mDebugText = intent.getStringExtra(KEY_DEBUG_TEXT);
        if (null != mICrashHandler) {
            mICrashHandler.onCrashServerStart(mICrashHandler.getDebugDirectory()
                    + File.separator + mICrashHandler.getDebugFileName(), mDebugText);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (null != mICrashHandler) {
            mICrashHandler.onCrashServerDestroy();
        }
    }
}