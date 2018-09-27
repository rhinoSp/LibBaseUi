package com.rhino.ui.base;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import com.rhino.ui.msg.Message;
import com.rhino.ui.msg.impl.IMessage;
import com.rhino.ui.utils.ActivityUtils;
import com.rhino.ui.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>The base Activity</p>
 *
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public abstract class BaseActivity extends FragmentActivity implements IMessage,
        IFragment {

    public String CLASS_NAME = getClass().getName();
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
     * The OnClickListener.
     */
    public View.OnClickListener mBaseOnClickListener;
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
     * Init the data
     *
     * @return true success false failed
     */
    public abstract boolean initData();

    /**
     * Init the view.
     */
    public abstract void initView();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.i(CLASS_NAME);
        initExtraData();
        mCreateTime = System.currentTimeMillis();
        registerFragmentLifecycleCallbacks();
        ActivityUtils.getInstance().addActivity(this, mExtras, mCreateTime);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
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
    public void onPause() {
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
     * @param resultCode resultCode
     * @param data data
     */
    public void dispatchOnActivityResult(int requestCode, int resultCode, Intent data) {
        List<Fragment> fragments = getAttachedFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof IOnBackPressed) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
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

    @Override
    public boolean handleMessage(@NonNull Message data) {
        LogUtils.i(CLASS_NAME + ", Received message: " + data.toString());
        return false;
    }

    @Override
    public boolean sendMessage(@NonNull Message data) {
        LogUtils.i(CLASS_NAME + ", Send message: " + data.toString());
        return data.dispatchMessage(this);
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
        if (mBaseOnClickListener == null) {
            mBaseOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    baseOnClickListener(v);
                }
            };
        }
        return mBaseOnClickListener;
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
     * @param v the click view
     */
    public void baseOnClickListener(View v) {
    }

    /**
     * Deal the long click listener.
     *
     * @param v the long click view
     */
    public boolean baseOnLongClickListener(View v) {
        return false;
    }
}
