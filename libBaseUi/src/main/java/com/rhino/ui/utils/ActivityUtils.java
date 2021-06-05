package com.rhino.ui.utils;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.Stack;

/**
 * <p>The utils of activity manager<p>
 *
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public class ActivityUtils {

    private Stack<ActivityData> activityStack;

    private static ActivityUtils instance;

    public static ActivityUtils getInstance() {
        if (instance == null) {
            instance = new ActivityUtils();
        }
        return instance;
    }

    private ActivityUtils() {
        activityStack = new Stack<>();
    }

    /**
     * Add activity to stack.
     *
     * @param activity the activity
     * @param bundle   the bundle data
     */
    public void addActivity(Activity activity, Bundle bundle, long createTime) {
        activityStack.add(new ActivityData(activity, bundle, createTime));
    }

    /**
     * remove activity to stack.
     *
     * @param activity   the activity
     * @param createTime the create time of activity
     */
    public boolean removeActivity(Activity activity, long createTime) {
        ActivityData data = findActivityData(activity, createTime);
        if (null != data) {
            activityStack.remove(data);
            return true;
        }
        return false;
    }

    /**
     * exit app and finish all activity.
     */
    public void exit() {
        if (activityStack.size() > 0) {
            finishAllActivity();
        }
    }

    /**
     * Finish all activity
     */
    public void finishAllActivity() {
        if (activityStack.empty()) {
            return;
        }
        for (int i = 0; i < activityStack.size(); i++) {
            ActivityData data = activityStack.get(i);
            if (null != data) {
                data.finish();
            }
        }
        activityStack.clear();
    }

    /**
     * Get current activity
     *
     * @return may be null
     */
    public Activity getCurrentActivity() {
        if (activityStack.empty()) {
            return null;
        }
        return activityStack.lastElement().activity;
    }

    /**
     * Finish current activity
     */
    public void finishCurrentActivity() {
        if (activityStack.empty()) {
            return;
        }
        Activity activity = activityStack.lastElement().activity;
        finishActivity(activity, 0);
    }

    /**
     * Finish activity
     *
     * @param cls        the activity class
     * @param createTime the create time of activity
     */
    public boolean finishActivity(Class<?> cls, long createTime) {
        if (null == cls) {
            return false;
        }
        ActivityData data = findActivityData(cls.getName(), createTime);
        if (null != data) {
            activityStack.remove(data);
            data.finish();
            return true;
        }
        return false;
    }

    /**
     * Finish the activity
     *
     * @param activity   the activity
     * @param createTime the create time of activity
     */
    public boolean finishActivity(Activity activity, long createTime) {
        return finishActivity(activity.getClass(), createTime);
    }

    /**
     * Finish other activity except this
     *
     * @param activity   this activity
     * @param createTime the create time of activity
     */
    public void finishOtherActivityExceptThis(Activity activity, long createTime) {
        if (null == activity || activityStack.empty()) {
            return;
        }
        ActivityData bak = null;
        for (ActivityData data : activityStack) {
            if (!data.activity.getClass().equals(activity.getClass()) || createTime != data.createTime) {
                data.finish();
            } else {
                bak = data;
            }
        }
        activityStack.clear();
        activityStack.add(bak);
    }

    /**
     * Find the activity in activityStack
     *
     * @param activity   the activity
     * @param createTime the create time of activity
     * @return ActivityData
     */
    public ActivityData findActivityData(Activity activity, long createTime) {
        return findActivityData(activity.getClass().getName(), createTime);
    }

    /**
     * Find the activity in activityStack
     *
     * @param activityClsName the activity class name
     * @param createTime      the create time of activity
     * @return ActivityData
     */
    public ActivityData findActivityData(String activityClsName, long createTime) {
        if (TextUtils.isEmpty(activityClsName) || activityStack.empty()) {
            return null;
        }
        for (int i = 0; i < activityStack.size(); i++) {
            ActivityData data = activityStack.get(i);
            if (null != data && null != data.activity
                    && data.activity.getClass().getName().equals(activityClsName)
                    && (createTime == 0 || createTime == data.createTime)) {
                return data;
            }
        }
        return null;
    }

    public static class ActivityData {
        public long createTime;
        public Activity activity;
        public Bundle bundle;

        public ActivityData(Activity activity, Bundle bundle, long createTime) {
            this.activity = activity;
            this.bundle = bundle;
            this.createTime = createTime;
        }

        public void finish() {
            if (null != activity) {
                activity.finish();
                activity = null;
            }
            bundle = null;
            createTime = -1;
        }
    }
}
