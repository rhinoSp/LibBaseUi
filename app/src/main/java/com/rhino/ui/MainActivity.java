package com.rhino.ui;

import com.rhino.ui.base.BaseSimpleTitleActivity;


public class MainActivity extends BaseSimpleTitleActivity {

    @Override
    protected void setContent() {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected boolean initData() {
        return true;
    }

    @Override
    protected void initView() {

    }
}
