package com.rhino.ui;

import com.rhino.ui.base.BaseApplication;
import com.rhino.ui.utils.crash.CrashHandlerUtils;

/**
 * @author LuoLin
 * @since Create on 2018/10/7.
 **/
public class AppApplication extends BaseApplication {

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
