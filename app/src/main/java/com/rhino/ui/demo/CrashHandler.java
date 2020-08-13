package com.rhino.ui.demo;

import com.rhino.log.crash.DefaultCrashHandler;

/**
 * @author LuoLin
 * @since Create on 2018/10/10.
 */
public class CrashHandler extends DefaultCrashHandler {

    @Override
    public Class<?> getRestartActivity() {
        return MainActivity.class;
    }
}
