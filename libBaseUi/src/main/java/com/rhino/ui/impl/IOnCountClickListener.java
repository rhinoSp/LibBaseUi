package com.rhino.ui.impl;

import android.view.View;

/**
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public abstract class IOnCountClickListener implements View.OnClickListener {

    private int mClickCount = 0;
    private long mFirstClickTimestamp = -1;
    private long clickDurationMillis = -1;
    private int mValidClickCount;

    public abstract void onCountClick(View v);

    public void onCountClickChanged(View v, int clickCount) {
    }

    public IOnCountClickListener(int mClickCount) {
        this.mClickCount = mClickCount;
    }

    public IOnCountClickListener(int validClickCount, long clickDurationMillis) {
        if (validClickCount > 0) {
            this.mValidClickCount = validClickCount;
            this.clickDurationMillis = clickDurationMillis;
        }
    }

    @Override
    public void onClick(View v) {
        long timestamp = System.currentTimeMillis();
        if (mClickCount == 0) {
            mFirstClickTimestamp = timestamp;
        }
        mClickCount++;
        if (clickDurationMillis > 0 && timestamp > mFirstClickTimestamp + clickDurationMillis) {
            mClickCount = 0;
        }
        if (mValidClickCount == mClickCount) {
            onCountClick(v);
            mClickCount = 0;
        }
        onCountClickChanged(v, mClickCount);
    }
}
