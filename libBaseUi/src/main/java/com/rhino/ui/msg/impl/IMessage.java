package com.rhino.ui.msg.impl;

import android.support.annotation.NonNull;

import com.rhino.ui.msg.Message;

/**
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public interface IMessage {

    /**
     * Send the message.
     *
     * @param data the message.
     * @return True: handled, false: not handle.
     */
    boolean sendMessage(@NonNull Message data);

    /**
     * Handle the message.
     *
     * @param data the message
     * @return True: handled, false: not handle.
     */
    boolean handleMessage(@NonNull Message data);

}
