package com.rhino.ui.view.tab;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * <p>This is a custom TabHost, support horizontal and vertical</p>
 *
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public class CustomTabHost extends LinearLayout implements OnClickListener {

    /**
     * The app context
     */
    private Context context;
    /**
     * The FragmentManager
     */
    private FragmentManager fragmentManager;
    /**
     * The id of content view
     */
    private int contentId;
    /**
     * The listener when tab changeed
     */
    private onTabsChangedListener listener;
    /**
     * The tabs data array
     */
    private ArrayList<Tabs> tabs;
    /**
     * The current tab index
     */
    private int currentTabIndex = -1;
    /**
     * The last fragment
     */
    private Fragment lastFragment;


    public CustomTabHost(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    /**
     * Do something of init
     **/
    private void init() {
        tabs = new ArrayList<>();
        setGravity(Gravity.CENTER);
    }

    /**
     * Init something about the content view
     *
     * @param context         the app context
     * @param fragmentManager the fragmentManager
     * @param contentId       the content id
     */
    public void setup(Context context, FragmentManager fragmentManager,
            int contentId) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.contentId = contentId;
    }

    /**
     * Init the listener when tab changed
     *
     * @param listener the listener
     */
    public void setOnTabsChangedListener(onTabsChangedListener listener) {
        this.listener = listener;
    }

    /**
     * Set current tab index and change the content view
     *
     * @param index the tab index
     */
    public void setCurrentTab(int index) {
        if (null == listener || currentTabIndex == index) {
            return;
        }
        currentTabIndex = index;

        if (0 <= currentTabIndex && currentTabIndex < tabs.size()) {
            listener.onTabsChanged(tabs.get(currentTabIndex).tabId);
            switchContent();
        } else {
            listener.onTabsChanged("" + currentTabIndex);
        }
    }

    /**
     * Add the tab data
     *
     * @param tabId    the string of tabId
     * @param view     the tab view
     * @param fragment the fragment of content view
     */
    public void addTab(String tabId, View view, Fragment fragment) {
        tabs.add(new Tabs(tabId, view, fragment));
        view.setTag(tabId);

        if (isPortrait()) {
            view.setLayoutParams(new LayoutParams(0,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        } else {
            view.setLayoutParams(new LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, 0, 1.0f));
        }

        view.setOnClickListener(this);
        addView(view);
    }

    /**
     * Whether horizontal or vertical about screen
     *
     * @return vertical return true, horizontal return false
     */
    private boolean isPortrait() {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    /**
     * Switch the content view by fragment
     **/
    public void switchContent() {
        Fragment toFragment = tabs.get(currentTabIndex).fragment;
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (null == toFragment) {
            return;
        }
        if (!toFragment.isAdded()) {
            if (null != lastFragment) {
                transaction.hide(lastFragment);
            }
            transaction.add(contentId, toFragment).commit();
        } else if (toFragment.isHidden()) {
            if (null != lastFragment) {
                transaction.hide(lastFragment);
            }
            transaction.show(toFragment).commit();
        }
        lastFragment = toFragment;
    }

    /**
     * The listener when tab changed
     **/
    public interface onTabsChangedListener {
        void onTabsChanged(String tabId);
    }

    @Override
    public void onClick(View v) {
        if (null == listener) {
            return;
        }
        String tabId = (String) v.getTag();
        if ((0 <= currentTabIndex && currentTabIndex < tabs.size()
                && !tabs.get(currentTabIndex).tabId.equals(tabId)) || 0 > currentTabIndex) {

            for (int index = 0; index < tabs.size(); index++) {
                if (tabs.get(index).tabId.equals(tabId)) {
                    currentTabIndex = index;
                    break;
                }
            }
            listener.onTabsChanged(tabId);
            switchContent();
        }
    }

    public class Tabs {

        /**
         * the string of tabId
         **/
        public String tabId;
        /**
         * the tab view
         **/
        public View view;
        /**
         * the fragment of content view
         **/
        public Fragment fragment;

        public Tabs(String tabId, View view, Fragment fragment) {
            this.tabId = tabId;
            this.view = view;
            this.fragment = fragment;
        }
    }
}
