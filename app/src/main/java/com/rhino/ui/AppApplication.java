package com.rhino.ui;

import com.rhino.ui.base.BaseApplication;

/**
 * @author LuoLin
 * @since Create on 2018/10/7.
 **/
public class AppApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public Class<?> getRestartActivity() {
        return MainActivity.class;
    }
}
