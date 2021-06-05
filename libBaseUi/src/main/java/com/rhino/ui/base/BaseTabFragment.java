package com.rhino.ui.base;

import android.content.res.ColorStateList;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.rhino.ui.R;
import com.rhino.ui.view.ScrollAbleViewPager;
import com.rhino.ui.view.tab.CustomTabItemLayout;
import com.rhino.ui.view.tab.CustomTabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>The base tab Fragment</p>
 *
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public abstract class BaseTabFragment extends BaseFragment implements CustomTabLayout.OnTabChangedListener {

    /**
     * The default number of pages that should be retained to either side of the
     * current page in the view hierarchy in an idle state. Pages beyond this
     * limit will be recreated from the adapter when needed.
     */
    public static final int DF_SCREEN_PAGE_LIMIT = 5;
    /**
     * The number of pages that should be retained to either side of the
     * current page in the view hierarchy in an idle state. Pages beyond this
     * limit will be recreated from the adapter when needed.
     */
    public int mScreenPageLimit = DF_SCREEN_PAGE_LIMIT;
    /**
     * The tab layout.
     */
    public CustomTabLayout mTabLayout;
    /**
     * The tab line up tab layout.
     */
    public View mTabLine;
    /**
     * The viewpager of fragment container.
     */
    public ScrollAbleViewPager mViewPager;
    /**
     * The init select index.
     */
    public int mInitSelIndex;

    @NonNull
    public abstract List<TabItemData> getTabItemData();

    public ColorStateList getTabStateColorList() {
        return null;
    }

    @Override
    public void setContent() {
        setContentView(R.layout.layout_page_base_tab_viewpager);
    }

    @Override
    public void initView() {
        mTabLayout = findSubViewById(R.id.tab_layout);
        mTabLine = findSubViewById(R.id.tab_line);
        mViewPager = findSubViewById(R.id.tab_viewpager);
        mViewPager.setOffscreenPageLimit(mScreenPageLimit);
        mViewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        initTab();
    }

    /**
     * Set the number of pages that should be retained to either side of the
     * current page in the view hierarchy in an idle state. Pages beyond this
     * limit will be recreated from the adapter when needed.
     *
     * @param limit How many pages will be kept offscreen in an idle state.
     */
    public void setOffscreenPageLimit(int limit) {
        this.mScreenPageLimit = limit;
        if (null != mViewPager) {
            mViewPager.setOffscreenPageLimit(mScreenPageLimit);
        }
    }

    /**
     * Init the select index.
     *
     * @param index index
     */
    public void setInitSelIndex(int index) {
        this.mInitSelIndex = index;
    }

    /**
     * Set the tab background.
     *
     * @param background color or drawable
     */
    public void setTabBackground(@DrawableRes int background) {
        if (null != mTabLayout) {
            mTabLayout.setBackgroundResource(background);
        }
    }

    /**
     * Set the tab background.
     *
     * @param color color
     */
    public void setTabBackgroundColor(int color) {
        if (null != mTabLayout) {
            mTabLayout.setBackgroundColor(color);
        }
    }

    /**
     * Set the tab line color.
     *
     * @param color color
     */
    public void setTabLineColor(@ColorInt int color) {
        if (null != mTabLine) {
            mTabLine.setBackgroundColor(color);
        }
    }

    /**
     * Init the tab.
     */
    public void initTab() {
        List<TabItemData> list = getTabItemData();
        if (0 == list.size()) {
            throw new RuntimeException("BaseTabFragment getTabItemData is empty");
        }
        final int N = list.size();
        List<Fragment> listFragment = new ArrayList<>(N);
        List<CustomTabItemLayout> tabs = new ArrayList<>(N);
        for (TabItemData data : list) {
            if (data.invalidData()) {
                throw new RuntimeException("BaseTabFragment getTabItemData is invalid");
            }
            listFragment.add(newInstance(data.mFragmentName, mExtras));
            tabs.add(CustomTabItemLayout.build(getContext(), data.mIconResIds, data.mText));
        }

        mViewPager.setAdapter(new SimpleTabAdapter(getChildFragmentManager(), listFragment));

        mTabLayout.setOnTabChangedListener(this);
        mTabLayout.setViewPager(mViewPager);
        mTabLayout.setTabs(tabs);
        mTabLayout.setCurrentTab(mInitSelIndex);
        mTabLayout.setColor(getTabStateColorList());
        mTabLayout.setUp();

        onTabChanged(mTabLayout.getCurrentTab());
    }

    /**
     * Set the page scroll enable.
     *
     * @param scrollable scrollable
     */
    public void setPagerScrollable(boolean scrollable) {
        if (mViewPager != null) {
            mViewPager.setScrollable(scrollable);
        }
    }

    /**
     * Set current tab.
     *
     * @param index the tab index
     */
    public void setCurrentTab(int index) {
        if (null != mTabLayout) {
            mTabLayout.setCurrentTab(index);
        }
    }

    /**
     * Set current tab.
     *
     * @param tag the tab desc
     */
    public void setCurrentTab(String tag) {
        if (null != mTabLayout) {
            mTabLayout.setCurrentTab(tag);
        }
    }

    /**
     * Set the new flag.
     *
     * @param index index
     * @param count count
     */
    public void setNewFlag(int index, int count) {
        if (null != mTabLayout) {
            mTabLayout.setNewFlag(index, count);
        }
    }

    public final static class TabItemData {

        String mFragmentName;
        int[] mIconResIds;
        String mText;

        public TabItemData(@NonNull String fragment, @DrawableRes int[] resIds, String text) {
            mFragmentName = fragment;
            mIconResIds = resIds;
            mText = text;
        }

        public TabItemData(@NonNull Class<? extends BaseFragment> fragment, @DrawableRes int[] resIds, String text) {
            this(fragment.getName(), resIds, text);
        }

        boolean invalidData() {
            return TextUtils.isEmpty(mFragmentName);
        }
    }

    public class SimpleTabAdapter extends FragmentPagerAdapter {

        public List<? extends Fragment> fragments;

        public SimpleTabAdapter(@NonNull FragmentManager fm, @NonNull List<? extends Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }
    }
}
