package com.rhino.ui.impl;

import android.view.View;

/**
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public abstract class IOnNoMultiClickListener implements View.OnClickListener {

    private static final int DEFAULT_CLICK_SPACE = 500;
    private long mClickSpace;
    private long mLastClickTimStamp;

    public abstract void onNoMultiClick(View v);

    public IOnNoMultiClickListener(){
        this(DEFAULT_CLICK_SPACE);
    }

    public IOnNoMultiClickListener(int clickSpace){
        if (clickSpace > 0) {
            this.mClickSpace = clickSpace;
        }
    }
    @Override
    public void onClick(View v) {
        long currentTimestamp = System.currentTimeMillis();
        if (Math.abs(mLastClickTimStamp - currentTimestamp) >= mClickSpace) {
            onNoMultiClick(v);
            mLastClickTimStamp = System.currentTimeMillis();
        }
    }
}
