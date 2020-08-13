package com.rhino.ui.msg;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.rhino.ui.impl.IFragment;
import com.rhino.ui.msg.impl.ILocalMessage;

import java.util.List;

/**
 * <p>The same level local message</p>
 *
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public class SameLevelLocalMessage extends LocalMessage {

    public SameLevelLocalMessage() {
        type = MSG_TYPE_SAME_LEVEL;
    }

    @Override
    public boolean dispatchLocalMessage(@NonNull ILocalMessage iLocalMessage) {
        if (iLocalMessage instanceof Fragment) {
            return dispatchSameLevelFragment((Fragment) iLocalMessage);
        } else if (iLocalMessage instanceof FragmentActivity) {
            List<Fragment> fragments = getAttachedFragments(iLocalMessage);
            if (null != fragments) {
                for (Fragment f : fragments) {
                    if (f instanceof ILocalMessage && ((ILocalMessage) f).handleLocalMessage(this)) {
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
                if (f instanceof ILocalMessage && ((ILocalMessage) f).handleLocalMessage(this)) {
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
