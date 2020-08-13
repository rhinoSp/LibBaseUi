package com.rhino.ui.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.rhino.ui.impl.IFragment;
import com.rhino.ui.impl.IOnBackPressed;
import com.rhino.ui.impl.IOnKeyDown;
import com.rhino.ui.impl.IOnNoMultiClickListener;
import com.rhino.ui.msg.LocalMessage;
import com.rhino.ui.msg.impl.ILocalMessage;
import com.rhino.ui.utils.ActivityUtils;
import com.rhino.log.LogUtils;
import com.rhino.ui.utils.ui.ToastUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>The base Activity</p>
 *
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public abstract class BaseActivity extends FragmentActivity implements ILocalMessage,
        IFragment {

    public String CLASS_NAME = getClass().getName();
    /**
     * This
     */
    public BaseActivity mActivity;
    /**
     * The Handler.
     */
    public MyHandler mHandler;
    /**
     * The create time of activity.
     */
    public long mCreateTime;
    /**
     * The bundle data.
     */
    public Bundle mExtras;
    /**
     * Whether the activity is alive.
     */
    public boolean mIsPageAlive;
    /**
     * Whether the activity is visible.
     */
    public boolean mIsPageVisible;
    /**
     * The IOnNoMultiClickListener.
     */
    public IOnNoMultiClickListener mBaseOnNoMultiClickListener;
    /**
     * The OnLongClickListener.
     */
    public View.OnLongClickListener mBaseOnLongClickListener;
    /**
     * The FragmentLifecycleCallbacks.
     */
    public FragmentManager.FragmentLifecycleCallbacks mFragmentLifecycleCallbacks;
    /**
     * The list of attached fragment.
     */
    public List<Fragment> mFragmentList = new ArrayList<>();

    /**
     * Set the parent view.
     * {@link #setContentView(int)}
     * {@link #setContentView(View)}}
     */
    public abstract void setContent();

    /**
     * Init the view.
     */
    public abstract void initView();

    /**
     * Init the data
     *
     * @return true success false failed
     */
    public boolean initData() {
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.i(CLASS_NAME);
        mActivity = this;
        mCreateTime = System.currentTimeMillis();
        mHandler = new MyHandler(this);
        initExtraData();
        registerFragmentLifecycleCallbacks();


        ActivityUtils.getInstance().addActivity(this, mExtras, mCreateTime);
        if (!initData()) {
            finish();
        } else {
            mIsPageAlive = true;
            setContent();
            initView();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtils.i(CLASS_NAME);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        LogUtils.i(CLASS_NAME);
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.i(CLASS_NAME);
    }

    @Override
    protected void onPostResume() {
        mIsPageVisible = true;
        super.onPostResume();
    }

    @Override
    public void onPause() {
        mIsPageVisible = false;
        super.onPause();
        LogUtils.i(CLASS_NAME);
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.i(CLASS_NAME);
    }

    @Override
    public void onDestroy() {
        LogUtils.i(CLASS_NAME);
        mIsPageAlive = false;
        unregisterFragmentLifecycleCallbacks();
        ActivityUtils.getInstance().removeActivity(this, mCreateTime);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        dispatchOnActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return dispatchKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (!dispatchBackPressed()) {
            super.onBackPressed();
        }
    }

    /**
     * Dispatch the back activity result.
     *
     * @param requestCode requestCode
     * @param resultCode  resultCode
     * @param data        data
     */
    public void dispatchOnActivityResult(int requestCode, int resultCode, Intent data) {
        List<Fragment> fragments = getAttachedFragments();
        for (Fragment fragment : fragments) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Dispatch the key down event.
     *
     * @param keyCode the key code
     * @param event   the event
     * @return True deal.
     */
    public boolean dispatchKeyDown(int keyCode, KeyEvent event) {
        List<Fragment> fragments = getAttachedFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof IOnKeyDown) {
                if (((IOnKeyDown) fragment).onKeyDown(keyCode, event)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Dispatch the back pressed event.
     *
     * @return True deal.
     */
    public boolean dispatchBackPressed() {
        List<Fragment> fragments = getAttachedFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof IOnBackPressed) {
                if (((IOnBackPressed) fragment).onBackPressed()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void handleOsMessage(@NonNull android.os.Message data) {
        LogUtils.i(CLASS_NAME + ", Received os message: " + data.toString());
    }

    @Override
    public boolean handleLocalMessage(@NonNull LocalMessage data) {
        LogUtils.i(CLASS_NAME + ", Received local message: " + data.toString());
        return false;
    }

    @Override
    public boolean sendLocalMessage(@NonNull LocalMessage data) {
        LogUtils.i(CLASS_NAME + ", Send local message: " + data.toString());
        return data.dispatchLocalMessage(this);
    }

    /**
     * Get the list of attached fragment
     *
     * @return list
     */
    @NonNull
    @Override
    public List<Fragment> getAttachedFragments() {
        return mFragmentList;
    }

    @NonNull
    @Override
    public List<Fragment> getChildAttachedFragments() {
        return null;
    }

    /**
     * Registers a {@link FragmentManager.FragmentLifecycleCallbacks} to listen to fragment lifecycle events
     * happening in this FragmentManager. All registered callbacks will be automatically
     * unregistered when this FragmentManager is destroyed.
     */
    public void registerFragmentLifecycleCallbacks() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (null != fragmentManager) {
            mFragmentLifecycleCallbacks = new FragmentManager.FragmentLifecycleCallbacks() {
                @Override
                public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {
                    super.onFragmentAttached(fm, f, context);
                    LogUtils.i(CLASS_NAME + ", onFragmentAttached: " + f.getClass().getName());
                    mFragmentList.add(f);
                }

                @Override
                public void onFragmentDetached(FragmentManager fm, Fragment f) {
                    super.onFragmentDetached(fm, f);
                    LogUtils.i(CLASS_NAME + ", onFragmentDetached: " + f.getClass().getName());
                    mFragmentList.remove(f);
                }
            };
            fragmentManager.registerFragmentLifecycleCallbacks(mFragmentLifecycleCallbacks, false);
        }
    }

    /**
     * Unregisters a previously registered {@link FragmentManager.FragmentLifecycleCallbacks}. If the callback
     * was not previously registered this call has no effect. All registered callbacks will be
     * automatically unregistered when this FragmentManager is destroyed.
     */
    public void unregisterFragmentLifecycleCallbacks() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (null != fragmentManager) {
            fragmentManager.unregisterFragmentLifecycleCallbacks(mFragmentLifecycleCallbacks);
        }
    }

    /**
     * Set the activity full screen.
     */
    public void setFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * Whether the activity is alive.
     *
     * @return true activity is alive
     */
    public boolean isPageAlive() {
        return mIsPageAlive;
    }

    /**
     * Whether the activity is visible.
     *
     * @return true activity is visible
     */
    public boolean isPageVisible() {
        return mIsPageVisible;
    }

    /**
     * Find the view by view id
     *
     * @param id view id
     * @return the view
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T findSubViewById(@IdRes int id) {
        return (T) findViewById(id);
    }

    /**
     * Find the view by id.
     *
     * @param id     view id
     * @param parent the parent view
     * @return the view
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T findSubViewById(@IdRes int id, View parent) {
        return (T) parent.findViewById(id);
    }

    /**
     * Show toast
     *
     * @param message message
     */
    public void showToast(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.show(message);
            }
        });
    }

    /**
     * Get the bundle data
     */
    public void initExtraData() {
        mExtras = getIntent().getExtras();
        if (mExtras == null) {
            mExtras = new Bundle();
        }
    }

    /**
     * Register a callback to be invoked when this view is clicked. If this view is not
     * clickable, it becomes clickable.
     *
     * @param views the view
     */
    public void setBaseOnClickListener(View... views) {
        if (views != null) {
            for (View v : views) {
                if (null != v) {
                    v.setOnClickListener(getBaseOnClickListener());
                }
            }
        }
    }

    /**
     * Register a callback to be invoked when this view is clicked and held. If this view is not
     * long clickable, it becomes long clickable.
     *
     * @param views the view
     */
    public void setBaseOnLongClickListener(View... views) {
        if (views != null) {
            for (View v : views) {
                if (null != v) {
                    v.setOnLongClickListener(getBaseOnLongClickListener());
                }
            }
        }
    }

    /**
     * Get the OnClickListener
     *
     * @return the OnClickListener
     */
    public View.OnClickListener getBaseOnClickListener() {
        if (mBaseOnNoMultiClickListener == null) {
            mBaseOnNoMultiClickListener = new IOnNoMultiClickListener() {
                @Override
                public void onNoMultiClick(View v) {
                    baseNoMultiClickListener(v);
                }

                @Override
                public void onClick(View v) {
                    super.onClick(v);
                    baseOnClickListener(v);
                }
            };
        }
        return mBaseOnNoMultiClickListener;
    }

    /**
     * Get the OnLongClickListener
     *
     * @return the OnLongClickListener
     */
    public View.OnLongClickListener getBaseOnLongClickListener() {
        if (mBaseOnLongClickListener == null) {
            mBaseOnLongClickListener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return baseOnLongClickListener(v);
                }
            };
        }
        return mBaseOnLongClickListener;
    }

    /**
     * Deal the click listener.
     *
     * @param view the click view
     */
    public void baseOnClickListener(View view) {
    }

    /**
     * Deal the click listener.
     *
     * @param view the click view
     */
    public void baseNoMultiClickListener(View view) {
    }

    /**
     * Deal the long click listener.
     *
     * @param view the long click view
     */
    public boolean baseOnLongClickListener(View view) {
        return false;
    }

    public static class MyHandler extends Handler {

        private WeakReference<BaseActivity> reference;

        public MyHandler(BaseActivity o) {
            reference = new WeakReference<>(o);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            BaseActivity o = reference.get();
            if (null != o) {
                o.handleOsMessage(msg);
            }
        }
    }
}
