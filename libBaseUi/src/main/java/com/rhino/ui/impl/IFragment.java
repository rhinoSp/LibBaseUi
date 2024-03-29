package com.rhino.ui.impl;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.List;

/**
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public interface IFragment {

    @NonNull
    List<Fragment> getAttachedFragments();

    @NonNull
    List<Fragment> getChildAttachedFragments();

}
