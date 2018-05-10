package com.rhino.ui.view.tab;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public class CustomTabLayout extends LinearLayout implements View.OnClickListener {

    private ViewPager mViewPager;
    private PagerAdapter mAdapter;
    private List<CustomTabItemLayout> mTabLayouts;
    private OnTabChangedListener mTabListener;
    private TabLayoutOnPageChangeListener mPageChangeListener;

    private ColorStateList mColorStateList;

    private int mCurrentTabIndex;
    private CustomTabItemLayout mCurrentTab;

    private boolean mSmoothSwitch;

    public CustomTabLayout(Context context) {
        this(context, null);
    }

    public CustomTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
    }

    @Nullable
    private Fragment getItemContent(int pos) {
        if (mAdapter == null && mViewPager != null) {
            mAdapter = mViewPager.getAdapter();
        }
        if (mAdapter != null && mAdapter.getCount() > mCurrentTabIndex) {
            if (mAdapter instanceof FragmentStatePagerAdapter) {
                return ((FragmentStatePagerAdapter) mAdapter).getItem(pos);
            } else if (mAdapter instanceof FragmentPagerAdapter) {
                return ((FragmentPagerAdapter) mAdapter).getItem(pos);
            }
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        CustomTabItemLayout tab = (CustomTabItemLayout) v;
        if (tab != mCurrentTab) {
            int index = tab.getIndex();
            setCurrentTabSmooth(index);
            if (mTabListener != null) {
                mTabListener.onTabChanged(tab);
            }
        }
    }

    /**
     * Set the item color.
     *
     * @param defaultColor  the default color
     * @param selectedColor the select color
     */
    public void setColor(int defaultColor, int selectedColor) {
        mColorStateList = createColorStateList(defaultColor, selectedColor);
    }

    /**
     * Set the item color state list.
     *
     * @param stateList ColorStateList
     */
    public void setColor(ColorStateList stateList) {
        if (stateList != null) {
            mColorStateList = stateList;
        }
    }

    /**
     * Set the viewpager.
     *
     * @param viewPager ViewPager
     */
    public void setViewPager(@NonNull ViewPager viewPager) {
        if (mViewPager != null) {
            mViewPager.removeOnPageChangeListener(mPageChangeListener);
        }
        mViewPager = viewPager;
    }

    /**
     * Set the tab item list.
     *
     * @param tabs the tab item list
     */
    public void setTabs(@NonNull List<CustomTabItemLayout> tabs) {
        mTabLayouts = new ArrayList<>();
        mTabLayouts.addAll(tabs);
    }

    /**
     * Set the tab item change listener.
     *
     * @param listener IOnTabChangedListener
     */
    public void setOnTabChangedListener(OnTabChangedListener listener) {
        mTabListener = listener;
    }

    /**
     * Call when param is ok.
     *
     * @see #setViewPager(ViewPager)
     * @see #setTabs(List)
     */
    public void setUp() {
        if (mTabLayouts != null) {
            removeAllViews();
            if (mTabLayouts.size() > 0) {
                setWeightSum(mTabLayouts.size());
                int index = 0;
                for (CustomTabItemLayout tabLayout : mTabLayouts) {
                    tabLayout.setIndex(index);
                    tabLayout.setOnClickListener(this);
                    addView(tabLayout, new LinearLayout.LayoutParams(0, -1, 1));
                    if (mColorStateList != null) {
                        tabLayout.setColorStateList(mColorStateList);
                    }
                    index++;
                }
                switchTab(mCurrentTabIndex);
            }
        }
        if (mViewPager != null) {
            mPageChangeListener = new TabLayoutOnPageChangeListener();
            mViewPager.addOnPageChangeListener(mPageChangeListener);
            mAdapter = mViewPager.getAdapter();
            switchPage(mCurrentTabIndex, false);
        }
    }

    /**
     * Switch tab smoothly.
     *
     * @param index index
     */
    public void setCurrentTabSmooth(int index) {
        switchPage(index, mSmoothSwitch);
        switchTab(index);
    }

    /**
     * Switch tab not smoothly.
     *
     * @param index index
     */
    public void setCurrentTab(int index) {
        switchPage(index, false);
        switchTab(index);
    }

    /**
     * Switch tab not smoothly.
     *
     * @param tabText the tab text
     */
    public void setCurrentTab(@NonNull String tabText) {
        int index = findIndexByTabText(tabText);
        if (index != -1) {
            setCurrentTab(index);
        }
    }

    /**
     * Set the new flag.
     *
     * @param index index
     * @param count count
     */
    public void setNewFlag(int index, int count) {
        CustomTabItemLayout itemLayout = findByTabIndex(index);
        if (null != itemLayout) {
            if (0 < count) {
                if (count > 99) count = 99;
                itemLayout.setNewFlag(String.valueOf(count));
                itemLayout.setNewFlagVisible(true);
            } else {
                itemLayout.setNewFlag("");
                itemLayout.setNewFlagVisible(false);
            }
        }
    }

    /**
     * Find the tab item layout by index.
     *
     * @param index the tab item index
     * @return null not found
     */
    @Nullable
    public CustomTabItemLayout findByTabIndex(int index) {
        if (validIndex(index)) {
            return mTabLayouts.get(index);
        }
        return null;
    }

    /**
     * Find the tab item layout by text.
     *
     * @param text the tab item text
     * @return null not found
     */
    @Nullable
    public CustomTabItemLayout findByTabText(String text) {
        int index = findIndexByTabText(text);
        if (validIndex(index)) {
            return mTabLayouts.get(index);
        }
        return null;
    }

    /**
     * Find the tab index by text.
     *
     * @return -1 not found
     */
    public int findIndexByTabText(String text) {
        if (text != null && mTabLayouts != null) {
            for (int i = 0; i < mTabLayouts.size(); i++) {
                if (text.equals(mTabLayouts.get(i).getText())) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Set the smooth flag.
     *
     * @param smoothEnable true smoothly switch
     */
    public void setSmoothSwitch(boolean smoothEnable) {
        mSmoothSwitch = smoothEnable;
    }

    /**
     * Get current tab layout
     *
     * @return null not init
     * @see #setUp()
     */
    public CustomTabItemLayout getCurrentTab() {
        return mCurrentTab;
    }

    /**
     * Switch tab by index.
     *
     * @param index index
     */
    private void switchTab(int index) {
        if (!validIndex(index)) {
            return;
        }
        CustomTabItemLayout itemLayout = mTabLayouts.get(index);
        if (mCurrentTab == itemLayout) {
            return;
        }
        if (mCurrentTab != null) {
            mCurrentTab.setSelected(false);
        }
        itemLayout.setSelected(true);
        mCurrentTab = itemLayout;
        mCurrentTabIndex = index;
    }

    /**
     * Switch page by index
     *
     * @param index        index
     * @param smoothScroll smoothScroll
     */
    private void switchPage(int index, boolean smoothScroll) {
        if (!validIndex(index) || mViewPager == null || mAdapter == null) {
            return;
        }
        mViewPager.setCurrentItem(index, smoothScroll);
    }

    /**
     * Whether index valid.
     *
     * @param index index
     * @return true valid
     */
    private boolean validIndex(int index) {
        return mTabLayouts != null && index >= 0 && index < mTabLayouts.size();
    }

    /**
     * Create ColorStateList.
     *
     * @param defaultColor  the default color
     * @param selectedColor the selected color
     * @return ColorStateList
     */
    private ColorStateList createColorStateList(int defaultColor, int selectedColor) {
        final int[][] states = new int[2][];
        final int[] colors = new int[2];

        states[0] = SELECTED_STATE_SET;
        colors[0] = selectedColor;

        states[1] = EMPTY_STATE_SET;
        colors[1] = defaultColor;

        return new ColorStateList(states, colors);
    }

    public interface OnTabChangedListener {
        void onTabChanged(CustomTabItemLayout tab);
    }

    private class TabLayoutOnPageChangeListener implements ViewPager.OnPageChangeListener {
        private boolean mNeedNotify;

        @Override
        public void onPageScrollStateChanged(int state) {
            if (!mNeedNotify) {
                mNeedNotify = (state == ViewPager.SCROLL_STATE_DRAGGING);
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            Fragment curFragment = getItemContent(mCurrentTabIndex);
            if (curFragment != null && curFragment.isAdded()) {
                curFragment.onPause();
            }
            Fragment newFragment = getItemContent(position);
            if (newFragment != null && newFragment.isAdded()) {
                newFragment.onResume();
            }

            if (mNeedNotify && validIndex(position)) {
                switchTab(position);
                if (mTabListener != null) {
                    mTabListener.onTabChanged(mTabLayouts.get(position));
                }
            }
            mCurrentTabIndex = position;
            mNeedNotify = false;
        }
    }

}
