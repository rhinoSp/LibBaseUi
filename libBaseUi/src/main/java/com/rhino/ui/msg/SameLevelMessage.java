package com.rhino.ui.msg;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.rhino.ui.impl.IFragment;
import com.rhino.ui.msg.impl.IMessage;

import java.util.List;

/**
 * <p>The same level message</p>
 *
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public class SameLevelMessage extends Message {

    public SameLevelMessage() {
        type = MSG_TYPE_SAME_LEVEL;
    }

    @Override
    public boolean dispatchMessage(@NonNull IMessage iMessage) {
        if (iMessage instanceof Fragment) {
            return dispatchSameLevelFragment((Fragment) iMessage);
        } else if (iMessage instanceof FragmentActivity) {
            List<Fragment> fragments = getAttachedFragments(iMessage);
            if (null != fragments) {
                for (Fragment f : fragments) {
                    if (f instanceof IMessage && ((IMessage) f).handleMessage(this)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected boolean dispatchSameLevelFragment(Fragment fragment) {
        List<Fragment> fragments = getAttachedFragments(fragment);
        if (null != fragments) {
            for (Fragment f : fragments) {
                if (f instanceof IMessage && ((IMessage) f).handleMessage(this)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Nullable
    protected List<Fragment> getAttachedFragments(Object iFragment) {
        if (iFragment instanceof IFragment) {
            return ((IFragment) iFragment).getAttachedFragments();
        }
        return null;
    }

    @Nullable
    protected List<Fragment> getChildAttachedFragments(Object iFragment) {
        if (iFragment instanceof IFragment) {
            return ((IFragment) iFragment).getChildAttachedFragments();
        }
        return null;
    }

}
