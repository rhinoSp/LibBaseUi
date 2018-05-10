package com.rhino.ui.msg;


import android.os.Bundle;
import android.support.annotation.NonNull;

import com.rhino.ui.msg.impl.IMessage;

/**
 * <p>The message</p>
 *
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public abstract class Message {

    public abstract boolean dispatchMessage(@NonNull IMessage iMessage);

    /**
     * Dispatch to same level.
     */
    public static final int MSG_TYPE_SAME_LEVEL = 1;
    /**
     * Dispatch to all.
     */
    public static final int MSG_TYPE_OVER_ALL = 2;

    /**
     * @see #MSG_TYPE_SAME_LEVEL
     */
    public int type;

    /**
     * User-defined message code so that the recipient can identify
     * what this message is about.
     */
    public int what;

    /**
     * arg1 and arg2 are lower-cost alternatives to using
     * {@link #setData(Bundle)} if you only need to store a
     * few integer values.
     */
    public int arg1;

    /**
     * arg1 and arg2 are lower-cost alternatives to using
     * {@link #setData(Bundle)} if you only need to store a
     * few integer values.
     */
    public int arg2;

    /**
     * An arbitrary object to send to the recipient.  When using
     * to send the message across processes this can only
     * be non-null if it contains a Parcelable of a framework class (not one
     * implemented by the application).   For other data transfer use
     *
     * <p>Note that Parcelable objects here are not supported prior to
     * the {@link android.os.Build.VERSION_CODES#FROYO} release.
     */
    public Object obj;

    /*package*/ long when;

    /*package*/ Bundle data;

    /**
     * Return the targeted delivery time of this message, in milliseconds.
     */
    public long getWhen() {
        return when;
    }

    /**
     * Obtains a Bundle of arbitrary data associated with this
     * event, lazily creating it if necessary. Set this value by calling
     * {@link #setData(Bundle)}.
     * @see #peekData()
     * @see #setData(Bundle)
     */
    public Bundle getData() {
        if (data == null) {
            data = new Bundle();
        }

        return data;
    }

    /**
     * Like getData(), but does not lazily create the Bundle.  A null
     * is returned if the Bundle does not already exist.  See
     * {@link #getData} for further information on this.
     * @see #getData()
     * @see #setData(Bundle)
     */
    public Bundle peekData() {
        return data;
    }

    /**
     * Sets a Bundle of arbitrary data values. Use arg1 and arg2 members
     * as a lower cost way to send a few simple integer values, if you can.
     * @see #getData()
     * @see #peekData()
     */
    public void setData(Bundle data) {
        this.data = data;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ type=").append(type);
        if (0 != what) {
            sb.append(" what=");
            sb.append(what);
        }
        if (0 != arg1) {
            sb.append(" arg1=");
            sb.append(arg1);
        }
        if (0 != arg2) {
            sb.append(" arg2=");
            sb.append(arg2);
        }
        if (null != obj) {
            sb.append(" obj=");
            sb.append(obj.toString());
        }
        sb.append(" }");
        return sb.toString();
    }
}
