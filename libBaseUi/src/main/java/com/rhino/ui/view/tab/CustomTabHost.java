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
    private int currentTab = -1;
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
     * @param id the tab index
     */
    public void setCurrentTab(int id) {
        if (null == listener || currentTab == id) {
            return;
        }
        currentTab = id;

        if (0 <= currentTab && currentTab < tabs.size()) {
            listener.onTabsChanged(tabs.get(currentTab).tabId);
            switchContent();
        } else {
            listener.onTabsChanged("" + currentTab);
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
        Fragment toFragment = tabs.get(currentTab).fragment;
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
        if ((0 <= currentTab && currentTab < tabs.size() && !tabs
                .get(currentTab).tabId.equals(tabId)) || 0 > currentTab) {

            for (int id = 0; id < tabs.size(); id++) {
                if (tabs.get(id).tabId.equals(tabId)) {
                    currentTab = id;
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
