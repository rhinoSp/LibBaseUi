package com.rhino.ui.utils.okhttp;

import java.io.File;


/**
 * @author LuoLin
 * @since Create on 2018/10/8.
 */
public abstract class Callback implements okhttp3.Callback {

    abstract void onProgressChanged(int progress);

    abstract void onSuccess(File file);

}
