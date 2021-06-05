package com.rhino.ui.msg;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.rhino.ui.msg.impl.ILocalMessage;

import java.util.List;

/**
 * <p>The overall local message</p>
 *
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public class OverallLevelLocalMessage extends SameLevelLocalMessage {

    public OverallLevelLocalMessage() {
        type = MSG_TYPE_OVER_ALL;
    }

    @Override
    public boolean dispatchLocalMessage(@NonNull ILocalMessage iLocalMessage) {
        if (iLocalMessage instanceof Fragment) {
            if (dispatchSameLevelFragment((Fragment) iLocalMessage)
                    || dispatchTopFragment((Fragment) iLocalMessage)
                    || dispatchBottomFragment((Fragment) iLocalMessage)) {
                return true;
            } else {
                FragmentActivity activity = ((Fragment) iLocalMessage).getActivity();
                if (activity instanceof ILocalMessage && ((ILocalMessage) activity).handleLocalMessage(this)) {
                    return true;
                }
            }
        } else if (iLocalMessage instanceof FragmentActivity) {
            List<Fragment> fragments = getAttachedFragments(iLocalMessage);
            if (null != fragments) {
                for (Fragment f : fragments) {
                    if (f instanceof ILocalMessage && ((ILocalMessage) f).handleLocalMessage(this)) {
                        return true;
                    } else if (dispatchTopFragment(f)) {
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
            if (parentFragment instanceof ILocalMessage && ((ILocalMessage) parentFragment).handleLocalMessage(this)) {
                return true;
            } else {
                List<Fragment> fragments = getAttachedFragments(fragment.getParentFragment());
                if (null != fragments) {
                    for (Fragment f : fragments) {
                        if (f instanceof ILocalMessage && ((ILocalMessage) f).handleLocalMessage(this)) {
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
                if (f instanceof ILocalMessage && ((ILocalMessage) f).handleLocalMessage(this)) {
                    return true;
                } else if (dispatchTopFragment(f)) {
                    return true;
                }
            }
        }
        return false;
    }

}
