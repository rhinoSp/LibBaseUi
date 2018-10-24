package com.rhino.ui.utils.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * <p>The utils of network<p>
 * <p><uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" /><p>
 *
 * @author LuoLin
 * @since Create on 2016/12/17
 **/
public class NetWorkUtils {

    public static boolean isNetWorkAvailable(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

}
