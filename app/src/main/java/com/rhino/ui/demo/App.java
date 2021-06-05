package com.rhino.ui.demo;

import com.rhino.ui.base.BaseApplication;
import com.rhino.log.crash.CrashHandlerUtils;

/**
 * @author LuoLin
 * @since Create on 2018/10/7.
 **/
public class App extends BaseApplication {

    @Override
    public boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    @Override
    public void baseInit() {
        super.baseInit();
        CrashHandlerUtils.getInstance().init(getApplicationContext(), new CrashHandler());
    }

}
