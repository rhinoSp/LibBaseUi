package com.rhino.ui.impl;

import android.view.View;

/**
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public abstract class IOnCountClickListener implements View.OnClickListener {

    private int mClickCount = 0;
    private int mValidClickCount;

    public abstract void onCountClick(View v);
    public void onCountClickChanged(View v, int clickCount) {
    }

    public IOnCountClickListener(int validClickCount){
        if (validClickCount > 0) {
            this.mValidClickCount = validClickCount;
        }
    }
    @Override
    public void onClick(View v) {
        mClickCount ++;
        if (mValidClickCount == mClickCount) {
            onCountClick(v);
            mClickCount = 0;
        }
        onCountClickChanged(v, mClickCount);
    }
}
