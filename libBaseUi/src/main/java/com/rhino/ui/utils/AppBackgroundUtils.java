package com.rhino.ui.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.widget.Toast;

/**
 * 判断APP是否进入后台的工具类
 * 当APP进入后台时，显示提示，并通知栏展示
 * 当APP从后台回来进入前台时，重启APP
 * 360Replugin之类的插件一样支持，插件中Application无需再注册registerActivityLifecycleCallbacks
 * <p>
 * 通过ActivityLifecycleCallbacks来批量统计Activity的生命周期，来做判断，此方法在API 14以上均有效，但是需要在Application中注册此回调接口
 * 必须：
 * 1. 自定义Application并且注册ActivityLifecycleCallbacks接口
 * 2. AndroidManifest.xml中更改默认的Application为自定义
 * 3. 当Application因为内存不足而被Kill掉时，这个方法仍然能正常使用。虽然全局变量的值会因此丢失，但是再次进入App时候会重新统计一次的
 *
 * @author LuoLin
 * @since Create on 2018/11/27.
 */
public class AppBackgroundUtils {

    private static int appCount = 0;
    private static OnAppBackgroundRunListener onAppBackgroundRunListener = null;

    /**
     * 注册
     */
    public static void registerActivityLifecycleCallbacks(Application application, OnAppBackgroundRunListener backgroundRunListener) {
        onAppBackgroundRunListener = backgroundRunListener;
        application.registerActivityLifecycleCallbacks(getActivityLifecycleCallbacks());
    }

    /**
     * 当前应用是否在后台运行
     *
     * @return true 表示后台运行
     */
    public static boolean isAppBackground() {
        return appCount <= 0;
    }

    /**
     * 返回生命周期方法回调
     */
    public static Application.ActivityLifecycleCallbacks getActivityLifecycleCallbacks() {

        return new Application.ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (appCount == 0) {
                    if (onAppBackgroundRunListener != null) {
                        onAppBackgroundRunListener.onAppBackgroundRun(false);
                    }
                }
                appCount++;
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
                appCount--;
                if (appCount == 0) {
                    if (onAppBackgroundRunListener != null) {
                        onAppBackgroundRunListener.onAppBackgroundRun(true);
                    }
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        };
    }

    public interface OnAppBackgroundRunListener {
        void onAppBackgroundRun(boolean backgroundRun);
    }

}
