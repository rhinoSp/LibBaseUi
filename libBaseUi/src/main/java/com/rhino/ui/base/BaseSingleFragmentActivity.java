package com.rhino.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.widget.FrameLayout;

import com.rhino.ui.R;


/**
 * @author LuoLin
 * @since Create on 2018/5/16.
 **/
public class BaseSingleFragmentActivity extends BaseActivity {

    public static final String KEY_FRAGMENT_CLASS_NAME = "key.fragment.class.name";
    public BaseFragment mFragment;

    public static void showPage(@NonNull Activity activity, @NonNull String fragmentClassName,
            @NonNull Class<?> activityClass) {
        showPage(activity, null, fragmentClassName, activityClass);
    }

    public static void showPage(@NonNull Activity activity, @Nullable Bundle bundle,
            @NonNull String fragmentClassName, @NonNull Class<?> activityClass) {
        Intent intent = new Intent(activity, activityClass);
        if (null == bundle) {
            bundle = new Bundle();
        }
        bundle.putString(KEY_FRAGMENT_CLASS_NAME, fragmentClassName);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    public static void showPageForResult(@NonNull Activity activity, @NonNull String fragmentClassName,
            @NonNull Class<?> activityClass, int requestCode) {
        showPageForResult(activity, null, fragmentClassName, activityClass, requestCode);
    }

    public static void showPageForResult(@NonNull Activity activity, @Nullable Bundle bundle,
            @NonNull String fragmentClassName, @NonNull Class<?> activityClass, int requestCode) {
        Intent intent = new Intent(activity, activityClass);
        if (null == bundle) {
            bundle = new Bundle();
        }
        bundle.putString(KEY_FRAGMENT_CLASS_NAME, fragmentClassName);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void setContent() {
        FrameLayout mContainer = new FrameLayout(this);
        mContainer.setId(R.id.base_single_fragment_activity_container);
        setContentView(mContainer);
    }

    @Override
    public boolean initData() {
        String fragmentClassName = mExtras.getString(KEY_FRAGMENT_CLASS_NAME);
        if (!TextUtils.isEmpty(fragmentClassName)) {
            mFragment = BaseFragment.newInstance(fragmentClassName);
            if(null != mFragment){
                mFragment.setArguments(mExtras);
            }
            return mFragment != null;
        }
        return false;
    }

    @Override
    public void initView() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.base_single_fragment_activity_container);
        if (fragment == null) {
            mFragment.setArguments(mExtras);
            fm.beginTransaction().add(R.id.base_single_fragment_activity_container, mFragment).commit();
        } else {
            mFragment = (BaseFragment) fragment;
        }
    }

}
