package com.rhino.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public class OverViewPager extends ViewPager {

    private boolean mScrollable;

    public OverViewPager(Context context) {
        super(context);
    }

    public OverViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!mScrollable) {
            return true;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mScrollable) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void setScrollable(boolean scrollable) {
        mScrollable = scrollable;
    }
}
