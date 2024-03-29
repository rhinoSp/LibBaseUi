package com.rhino.ui.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.rhino.log.LogUtils;
import com.rhino.ui.impl.IFragment;
import com.rhino.ui.impl.IOnBackPressed;
import com.rhino.ui.impl.IOnKeyDown;
import com.rhino.ui.impl.IOnNoMultiClickListener;
import com.rhino.ui.msg.LocalMessage;
import com.rhino.ui.msg.impl.ILocalMessage;
import com.rhino.ui.utils.ui.ToastUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>The base Fragment</p>
 *
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public abstract class BaseFragment extends Fragment implements ILocalMessage,
        IFragment, IOnKeyDown, IOnBackPressed {

    public String CLASS_NAME = getClass().getName();
    /**
     * This
     */
    public FragmentActivity mActivity;
    /**
     * This
     */
    public Fragment mFragment;
    /**
     * The Handler.
     */
    public MyHandler mHandler;
    /**
     * The parent view.
     */
    public View mParentView;
    /**
     * The parent view layout id.
     */
    public int mParentLayoutId;
    /**
     * The bundle data.
     */
    public Bundle mExtras;
    /**
     * Whether the fragment is alive.
     */
    public boolean mIsPageAlive;
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
     * The child FragmentLifecycleCallbacks.
     */
    public FragmentManager.FragmentLifecycleCallbacks mChildFragmentLifecycleCallbacks;
    /**
     * The list of attached child fragment.
     */
    public List<Fragment> mChildFragmentList = new ArrayList<>();


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
    public boolean init() {
        return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
        mFragment = this;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.i(CLASS_NAME);
        mHandler = new MyHandler(this);
        initExtraData();
        registerFragmentLifecycleCallbacks();
        registerChildFragmentLifecycleCallbacks();
        if (!init()) {
            finish();
        } else {
            mIsPageAlive = true;
            setContent();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.i(CLASS_NAME);
        if (mParentLayoutId != 0) {
            mParentView = inflater.inflate(mParentLayoutId, container, false);
        }
        return mParentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtils.i(CLASS_NAME);
        initView();
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtils.i(CLASS_NAME);
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.i(CLASS_NAME);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtils.i(CLASS_NAME + ", hidden = " + hidden);
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
        unregisterFragmentLifecycleCallbacks();
        unregisterChildFragmentLifecycleCallbacks();
        mIsPageAlive = false;
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        dispatchOnActivityResult(requestCode, resultCode, data);
    }

    /**
     * Call back when key down event.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return this.dispatchKeyDown(keyCode, event);
    }

    @Override
    public boolean onBackPressed() {
        return this.dispatchBackPressed();
    }

    /**
     * Dispatch the back activity result.
     *
     * @param requestCode requestCode
     * @param resultCode  resultCode
     * @param data        data
     */
    public void dispatchOnActivityResult(int requestCode, int resultCode, Intent data) {
        List<Fragment> fragments = getChildAttachedFragments();
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
        List<Fragment> fragments = getChildAttachedFragments();
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
        List<Fragment> fragments = getChildAttachedFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof IOnBackPressed) {
                if (((IOnBackPressed) fragment).onBackPressed()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void handleOsMessage(@NonNull Message data) {
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

    /**
     * Get the list of attached child fragment
     *
     * @return list
     */
    @NonNull
    @Override
    public List<Fragment> getChildAttachedFragments() {
        return mChildFragmentList;
    }

    /**
     * Registers a {@link FragmentManager.FragmentLifecycleCallbacks} to listen to fragment lifecycle events
     * happening in this FragmentManager. All registered callbacks will be automatically
     * unregistered when this FragmentManager is destroyed.
     */
    public void registerFragmentLifecycleCallbacks() {
        FragmentManager fragmentManager = getFragmentManager();
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
        FragmentManager fragmentManager = getFragmentManager();
        if (null != fragmentManager) {
            fragmentManager.unregisterFragmentLifecycleCallbacks(mFragmentLifecycleCallbacks);
        }
    }

    /**
     * Registers a {@link FragmentManager.FragmentLifecycleCallbacks} to listen to fragment lifecycle events
     * happening in this FragmentManager. All registered callbacks will be automatically
     * unregistered when this FragmentManager is destroyed.
     */
    public void registerChildFragmentLifecycleCallbacks() {
        FragmentManager fragmentManager = getChildFragmentManager();
        mChildFragmentLifecycleCallbacks = new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {
                super.onFragmentAttached(fm, f, context);
                LogUtils.i(CLASS_NAME + ", child onFragmentAttached: " + f.getClass().getName());
                mChildFragmentList.add(f);
            }

            @Override
            public void onFragmentDetached(FragmentManager fm, Fragment f) {
                super.onFragmentDetached(fm, f);
                LogUtils.i(CLASS_NAME + ", child onFragmentDetached: " + f.getClass().getName());
                mChildFragmentList.remove(f);
            }
        };
        fragmentManager.registerFragmentLifecycleCallbacks(mChildFragmentLifecycleCallbacks, false);
    }

    /**
     * Unregisters a previously registered {@link FragmentManager.FragmentLifecycleCallbacks}. If the callback
     * was not previously registered this call has no effect. All registered callbacks will be
     * automatically unregistered when this FragmentManager is destroyed.
     */
    public void unregisterChildFragmentLifecycleCallbacks() {
        getChildFragmentManager().unregisterFragmentLifecycleCallbacks(mChildFragmentLifecycleCallbacks);
    }

    /**
     * Whether the fragment is alive.
     *
     * @return true fragment is alive
     * @see #getActivity()
     * @see #getView()
     */
    public boolean isPageAlive() {
        return mIsPageAlive && getActivity() != null && getView() != null;
    }

    /**
     * Whether the fragment is active.
     *
     * @return true fragment is active
     * @see #isPageAlive()
     * @see #isHidden()
     * @see #isResumed()
     */
    public boolean isPageActive() {
        return isPageAlive() && !isHidden() && isResumed();
    }

    /**
     * Find the view by view id.
     *
     * @param id view id
     * @return the view
     * @see View#findViewById(int)
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T findSubViewById(@IdRes int id) {
        return (T) mParentView.findViewById(id);
    }

    /**
     * Find the view by id.
     *
     * @param id     view id
     * @param parent the parent view
     * @return the view
     * @see View#findViewById(int)
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
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.show(message);
            }
        });
    }

    /**
     * Get the bundle data.
     */
    public void initExtraData() {
        mExtras = getArguments();
        if (mExtras == null) {
            mExtras = new Bundle();
        }
    }

    /**
     * Set the parent view.
     *
     * @param layoutId the layout id
     */
    public void setContentView(@LayoutRes int layoutId) {
        mParentLayoutId = layoutId;
    }

    /**
     * Set the parent view.
     *
     * @param contentView the view
     */
    public void setContentView(@NonNull View contentView) {
        mParentView = contentView;
    }

    /**
     * Call this when your activity is done and should be closed.
     */
    final public void finish() {
        Activity activity = getActivity();
        if (activity != null && !activity.isFinishing()) {
            activity.finish();
        }
    }

    /**
     * Register a callback to be invoked when this view is clicked. If this view is not
     * clickable, it becomes clickable.
     *
     * @param views the view
     */
    final public void setBaseOnClickListener(View... views) {
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
    final public void setBaseOnLongClickListener(View... views) {
        if (views != null) {
            for (View v : views) {
                if (null != v) {
                    v.setOnLongClickListener(getBaseOnLongClickListener());
                }
            }
        }
    }

    /**
     * Get the OnClickListener.
     *
     * @return the OnClickListener
     */
    final public View.OnClickListener getBaseOnClickListener() {
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
     * Get the OnLongClickListener.
     *
     * @return the OnLongClickListener
     */
    final public View.OnLongClickListener getBaseOnLongClickListener() {
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

    /**
     * Get ViewModel
     */
    @MainThread
    public <T extends ViewModel> T getViewModel(@NonNull Class<T> modelClass) {
        return new ViewModelProvider(this).get(modelClass);
    }

    /**
     * Build a fragment instance.
     *
     * @param name the fragment name
     * @return BaseFragment
     */
    @SuppressWarnings("all")
    @Nullable
    public static BaseFragment newInstance(String name) {
        try {
            Class c = Class.forName(name);
            return (BaseFragment) c.newInstance();
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return null;
    }

    /**
     * Build a fragment instance.
     *
     * @param name   the fragment name
     * @param bundle the Bundle
     * @return BaseFragment
     */
    @Nullable
    public static BaseFragment newInstance(@NonNull String name, @NonNull Bundle bundle) {
        BaseFragment baseFragment = newInstance(name);
        if (baseFragment != null) {
            baseFragment.setArguments(bundle);
        }
        return baseFragment;
    }

    public static class MyHandler extends Handler {

        private WeakReference<BaseFragment> reference;

        public MyHandler(BaseFragment o) {
            reference = new WeakReference<>(o);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BaseFragment o = reference.get();
            if (null != o) {
                o.handleOsMessage(msg);
            }
        }
    }

}
