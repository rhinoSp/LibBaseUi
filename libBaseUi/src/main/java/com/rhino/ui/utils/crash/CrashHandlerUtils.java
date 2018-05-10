package com.rhino.ui.utils.crash;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.rhino.ui.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * <p>
 *     The handler about UncaughtException, will write the error info to file,
 *     need the follow permission.
 * 			<p>"android.permission.WRITE_EXTERNAL_STORAGE"</p>
 * 			<p>"android.permission.MOUNT_UNMOUNT_FILESYSTEMS"</p>
 * </p>
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public class CrashHandlerUtils implements UncaughtExceptionHandler {

    /**
     * The log tag.
     */
    private static final String TAG = "CrashHandlerUtils";

    /**
     * The default handler of UncaughtException.
     */
    private UncaughtExceptionHandler mDefaultHandler;
    /**
     * The context.
     */
    private Context mContext;
    /**
     * The ICrashHandler.
     */
    private ICrashHandler mICrashHandler;


    public CrashHandlerUtils(Context context, @NonNull ICrashHandler crashHandler) {
        this.mContext = context.getApplicationContext();
        this.mICrashHandler = crashHandler;
        // get the default handler of UncaughtException
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // set this CrashHandlerUtils to the default handler of UncaughtException
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * Called when UncaughtException happen
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            //If the user does not handle the exception handler to allow the
            //system to handle the default
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * Collecting device parameter information and save error information to file
     *
     * @param ex the throwable
     * @return true:handled; false:not handled
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        // save error information to file
        String path = saveCrashInfo2File(ex);
        if (!TextUtils.isEmpty(path)) {
            CrashService.startThisService(mContext, mICrashHandler);
        } else {
            try {
                new Thread() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        String errorDesc = mICrashHandler.getErrorDesc();
                        String desc = !TextUtils.isEmpty(errorDesc) ? errorDesc
                                : "很抱歉，程序出现异常，即将退出";
                        Toast.makeText(mContext, desc, Toast.LENGTH_LONG).show();
                        Looper.loop();
                    }
                }.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * Save error information to file
     *
     * @param ex the throwable
     * @return the file name
     */
    @Nullable
    private String saveCrashInfo2File(Throwable ex) {
        final StringBuilder sb = new StringBuilder();
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault());
        sb.append("DATE=").append(sdf.format(new Date(System.currentTimeMillis()))).append("\n");

        String path = getFilePath();
        if (TextUtils.isEmpty(path)) {
            Log.e(TAG, "File path is null");
            return null;
        }
        Log.e(TAG, "path = " + path);
        sb.append("FILE_PATH=").append(path).append("\n");

        ApplicationInfo info = mContext.getApplicationInfo();
        boolean isDebugVersion = (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        sb.append("IS_DEBUG_VERSION=").append(isDebugVersion).append("\n");

        String packageName = mContext.getPackageName();
        sb.append("PACKAGE_NAME=").append(packageName).append("\n");
        sb.append("APP_NAME=").append(mContext.getString(R.string.app_name)).append("\n");

        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                sb.append("VERSION_NAME=").append(pi.versionName).append("\n");
                sb.append("VERSION_CODE=").append(pi.versionCode).append("\n");
            }
        } catch (NameNotFoundException e) {
            Log.e(TAG, "Error when collect package info", e);
        }

        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                sb.append(field.getName()).append("=").append(field.get(null).toString()).append("\n");
                Log.e(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Log.e(TAG, "Error when collect crash info", e);
            }
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        Log.e(TAG, result, ex);

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(path);
            fos.write(sb.toString().getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error when write file.", e);
        }
        return path;
    }

    /**
     * Get the debug file path
     *
     * @return the debug file path
     */
    @Nullable
    private String getFilePath() {
        String dir = mICrashHandler.getDebugDirectory();
        if (!createDir(dir)) {
            return null;
        }

        String filePath = dir + File.separator + mICrashHandler.getDebugFileName();
        if (!createFile(filePath)) {
            return null;
        }
        return filePath;
    }

    /**
     * Create directory
     *
     * @param dirPath the directory path
     * @return true success, false failed
     */
    private boolean createDir(String dirPath) {
        if (TextUtils.isEmpty(dirPath)) {
            return false;
        }
        File file = new File(dirPath);
        if (!file.exists()) {
            return file.mkdirs();
        }
        return true;
    }

    /**
     * Create file
     *
     * @param filePath the file path
     * @return true success, false failed
     */
    private boolean createFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                return file.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "filePath" + filePath, e);
                return false;
            }
        }
        return true;
    }


}
