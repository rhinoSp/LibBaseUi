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

    private static final String CRASH_HANDLE_KEY = "Crash.Handler";
    private static final String TAG = "CrashService";

    private ICrashHandler mICrashHandler;

    public static void startThisService(Context context, @NonNull ICrashHandler crashHandler) {
        Intent intent = new Intent(context, CrashService.class);
        intent.putExtra(CRASH_HANDLE_KEY, crashHandler);
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
        mICrashHandler = (ICrashHandler) intent.getSerializableExtra(CRASH_HANDLE_KEY);
        Log.d(TAG, "mICrashHandler = " + mICrashHandler);
        if (null != mICrashHandler) {
            mICrashHandler.onCrashServerStart(mICrashHandler.getDebugDirectory()
                    + File.separator + mICrashHandler.getDebugFileName());
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