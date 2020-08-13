package com.rhino.ui.msg.impl;

import android.support.annotation.NonNull;

import com.rhino.ui.msg.LocalMessage;

/**
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public interface ILocalMessage {

    /**
     * Send the local message.
     *
     * @param data the local message.
     * @return True: handled, false: not handle.
     */
    boolean sendLocalMessage(@NonNull LocalMessage data);

    /**
     * Handle the local message.
     *
     * @param data the local message
     * @return True: handled, false: not handle.
     */
    boolean handleLocalMessage(@NonNull LocalMessage data);

}
