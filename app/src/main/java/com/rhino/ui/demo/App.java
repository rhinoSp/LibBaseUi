package com.rhino.ui.demo;

import com.rhino.log.crash.DefaultCrashHandler;
import com.rhino.ui.base.BaseApplication;
import com.rhino.log.crash.CrashHandlerUtils;
import com.rhino.ui.impl.IAppInfo;

/**
 * @author LuoLin
 * @since Create on 2018/10/7.
 **/
public class App extends BaseApplication {

    @Override
    public IAppInfo getAppInfo() {
        return new AppInfo();
    }

    @Override
    public DefaultCrashHandler getCrashHandler() {
        return new CrashHandler();
    }

    @Override
    public void baseInit() {
        super.baseInit();
        CrashHandlerUtils.getInstance().init(getApplicationContext(), new CrashHandler());
    }

}
