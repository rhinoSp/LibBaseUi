package com.rhino.ui.view.viewpager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rhino.ui.view.viewpager.base.BaseHolder;
import com.rhino.ui.view.viewpager.base.BaseHolderFactory;


/**
 * Created by LuoLin on 2016/11/21.
 **/
public class SimpleHolderFactory extends BaseHolderFactory {
    @SuppressWarnings("all")
    @Override
    public BaseHolder buildHolder(ViewGroup parent, int viewLayout) {
        try {
            View view = LayoutInflater.from(parent.getContext()).inflate(viewLayout, parent, false);
            String className = (String) view.getTag();
            Class clazz = Class.forName(className);
            BaseHolder holder = (BaseHolder) clazz.getConstructor(View.class).newInstance(view);
            return holder;
        } catch (Exception e) {
            throw new IllegalArgumentException("Notice:You need to set the tag in the root view " +
                    "of the layout file, the tag string is full name of binded Holder class " +
                    "and will be used to reflect the object");
        }
    }
}
