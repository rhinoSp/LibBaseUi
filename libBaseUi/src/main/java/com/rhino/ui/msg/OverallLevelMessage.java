package com.rhino.ui.msg;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.rhino.ui.msg.impl.IMessage;

import java.util.List;

/**
 * <p>The overall message</p>
 *
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public class OverallLevelMessage extends SameLevelMessage {

    public OverallLevelMessage() {
        type = MSG_TYPE_OVER_ALL;
    }

    @Override
    public boolean dispatchMessage(@NonNull IMessage iMessage) {
        if (iMessage instanceof Fragment) {
            if (dispatchSameLevelFragment((Fragment) iMessage)
                    || dispatchTopFragment((Fragment) iMessage)
                    || dispatchBottomFragment((Fragment) iMessage)) {
                return true;
            } else {
                FragmentActivity activity = ((Fragment) iMessage).getActivity();
                if (activity instanceof IMessage && ((IMessage) activity).handleMessage(this)) {
                    return true;
                }
            }
        } else if (iMessage instanceof FragmentActivity) {
            List<Fragment> fragments = getAttachedFragments(iMessage);
            if (null != fragments) {
                for (Fragment f : fragments) {
                    if (dispatchTopFragment(f)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    private boolean dispatchBottomFragment(@NonNull Fragment fragment) {
        Fragment parentFragment = fragment.getParentFragment();
        if (null != parentFragment) {
            if (parentFragment instanceof IMessage && ((IMessage) parentFragment).handleMessage(this)) {
                return true;
            } else {
                List<Fragment> fragments = getAttachedFragments(fragment.getParentFragment());
                if (null != fragments) {
                    for (Fragment f : fragments) {
                        if (f instanceof IMessage && ((IMessage) f).handleMessage(this)) {
                            return true;
                        } else if (dispatchBottomFragment(f)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    private boolean dispatchTopFragment(@NonNull Fragment fragment) {
        List<Fragment> fragments = getChildAttachedFragments(fragment);
        if (null != fragments) {
            for (Fragment f : fragments) {
                if (f instanceof IMessage && ((IMessage) f).handleMessage(this)) {
                    return true;
                } else if (dispatchTopFragment(f)) {
                    return true;
                }
            }
        }
        return false;
    }


}
