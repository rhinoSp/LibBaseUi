package com.rhino.ui.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rhino.ui.impl.IFragment;
import com.rhino.ui.impl.IOnBackPressed;
import com.rhino.ui.impl.IOnKeyDown;
import com.rhino.ui.msg.Message;
import com.rhino.ui.msg.impl.IMessage;
import com.rhino.ui.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>The base Fragment</p>
 *
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public abstract class BaseFragment extends Fragment implements IOnKeyDown, IOnBackPressed, IMessage,
        IFragment {

    protected String CLASS_NAME = getClass().getName();
    /**
     * The parent view.
     */
    protected View mParentView;
    /**
     * The parent view layout id.
     */
    protected int mParentLayoutId;
    /**
     * The bundle data.
     */
    protected Bundle mExtras;
    /**
     * Whether the fragment is alive.
     */
    private boolean mIsPageAlive;
    /**
     * The OnClickListener.
     */
    private View.OnClickListener mBaseOnClickListener;
    /**
     * The OnLongClickListener.
     */
    private View.OnLongClickListener mBaseOnLongClickListener;
    /**
     * The FragmentLifecycleCallbacks.
     */
    private FragmentManager.FragmentLifecycleCallbacks mFragmentLifecycleCallbacks;
    /**
     * The list of attached fragment.
     */
    private List<Fragment> mFragmentList = new ArrayList<>();
    /**
     * The child FragmentLifecycleCallbacks.
     */
    private FragmentManager.FragmentLifecycleCallbacks mChildFragmentLifecycleCallbacks;
    /**
     * The list of attached child fragment.
     */
    private List<Fragment> mChildFragmentList = new ArrayList<>();


    /**
     * Set the parent view.
     * {@link #setContentView(int)}
     * {@link #setContentView(View)}}
     */
    protected abstract void setContent();

    /**
     * Init the data.
     *
     * @return true success, false failed
     */
    protected abstract boolean initData();

    /**
     * Init the view.
     */
    protected abstract void initView();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.i(CLASS_NAME);
        initExtraData();
        registerFragmentLifecycleCallbacks();
        registerChildFragmentLifecycleCallbacks();
        if (!initData()) {
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
        super.onDestroy();
    }

    /**
     * Call back when key down event.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onBackPressed() {
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
        if (null != fragmentManager) {
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
    }

    /**
     * Unregisters a previously registered {@link FragmentManager.FragmentLifecycleCallbacks}. If the callback
     * was not previously registered this call has no effect. All registered callbacks will be
     * automatically unregistered when this FragmentManager is destroyed.
     */
    public void unregisterChildFragmentLifecycleCallbacks() {
        FragmentManager fragmentManager = getChildFragmentManager();
        if (null != fragmentManager) {
            fragmentManager.unregisterFragmentLifecycleCallbacks(mChildFragmentLifecycleCallbacks);
        }
    }

    /**
     * Whether the fragment is alive.
     *
     * @return true fragment is alive
     * @see #getActivity()
     * @see #getView()
     */
    protected boolean isPageAlive() {
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
    protected boolean isPageActive() {
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
    protected <T extends View> T findSubViewById(@IdRes int id) {
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
    protected <T extends View> T findSubViewById(@IdRes int id, View parent) {
        return (T) parent.findViewById(id);
    }

    /**
     * Get the bundle data.
     */
    protected void initExtraData() {
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
    protected void setContentView(@LayoutRes int layoutId) {
        mParentLayoutId = layoutId;
    }

    /**
     * Set the parent view.
     *
     * @param contentView the view
     */
    protected void setContentView(@NonNull View contentView) {
        mParentView = contentView;
    }

    /**
     * Call this when your activity is done and should be closed.
     */
    final protected void finish() {
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
    final protected void setBaseOnClickListener(View... views) {
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
    final protected void setBaseOnLongClickListener(View... views) {
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
    final protected View.OnClickListener getBaseOnClickListener() {
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
     * Get the OnLongClickListener.
     *
     * @return the OnLongClickListener
     */
    final protected View.OnLongClickListener getBaseOnLongClickListener() {
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
    protected void baseOnClickListener(View v) {
    }

    /**
     * Deal the long click listener.
     *
     * @param v the long click view
     */
    protected boolean baseOnLongClickListener(View v) {
        return false;
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
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
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

}
