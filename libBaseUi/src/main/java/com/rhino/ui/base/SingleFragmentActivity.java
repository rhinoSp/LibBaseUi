package com.rhino.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.rhino.ui.R;


/**
 * @author LuoLin
 * @since Create on 2018/5/16.
 **/
public class SingleFragmentActivity extends BaseSimpleTitleActivity {

    public static final String KEY_FRAGMENT_CLASS_NAME = "key.fragment.class.name";
    public BaseFragment mFragment;

    public static class Build {
        Activity activity;
        Bundle bundle;
        String fragmentClassName;
        Class<?> activityClass;
        int requestCode;

        public void start() {
            Intent intent = new Intent(activity, activityClass);
            if (null == bundle) {
                bundle = new Bundle();
            }
            bundle.putString(KEY_FRAGMENT_CLASS_NAME, fragmentClassName);
            intent.putExtras(bundle);
            activity.startActivityForResult(intent, requestCode);
        }

        public Build setActivity(Activity activity) {
            this.activity = activity;
            return this;
        }

        public Build setBundle(Bundle bundle) {
            this.bundle = bundle;
            return this;
        }

        public Build setFragmentClassName(String fragmentClassName) {
            this.fragmentClassName = fragmentClassName;
            return this;
        }

        public Build setActivityClass(Class<?> activityClass) {
            this.activityClass = activityClass;
            return this;
        }

        public Build setRequestCode(int requestCode) {
            this.requestCode = requestCode;
            return this;
        }
    }

    public static Build with(Activity activity) {
        return new Build()
                .setActivity(activity)
                .setActivityClass(SingleFragmentActivity.class);
    }

    @Override
    public void setContent() {
        FrameLayout mContainer = new FrameLayout(this);
        mContainer.setId(R.id.single_fragment_activity_container);
        setContentView(mContainer);
    }

    @Override
    public boolean init() {
        String fragmentClassName = mExtras.getString(KEY_FRAGMENT_CLASS_NAME);
        if (!TextUtils.isEmpty(fragmentClassName)) {
            mFragment = BaseFragment.newInstance(fragmentClassName);
            if (null != mFragment) {
                mFragment.setArguments(mExtras);
            }
            return mFragment != null;
        }
        return false;
    }

    @Override
    public void initView() {
        mActionBarHelper.setStatusBarVisible(false);
        mActionBarHelper.setTitleVisible(false);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.single_fragment_activity_container);
        if (fragment == null) {
            mFragment.setArguments(mExtras);
            fm.beginTransaction().add(R.id.single_fragment_activity_container, mFragment).commit();
        } else {
            mFragment = (BaseFragment) fragment;
        }
    }

}
