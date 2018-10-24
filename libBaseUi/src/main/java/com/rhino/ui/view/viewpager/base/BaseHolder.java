package com.rhino.ui.view.viewpager.base;


import android.support.annotation.IdRes;
import android.view.View;

/**
 * Created by LuoLin on 2017/6/24
 */
public abstract class BaseHolder<T extends BaseHolderData> {

    public View itemView;

    abstract public void onBindView(int position, T data);

    public BaseHolder(View itemView){
        this.itemView = itemView;
    }

    /**
     * Look for a child view with the given id.  If this view has the given
     * id, return this view.
     * @param id The id to search for.
     * @see View#findViewById(int)
     * @return view
     */
    @SuppressWarnings("all")
    final protected <T extends View>T findSubViewById(@IdRes int id) {
        return (T) itemView.findViewById(id);
    }

}
