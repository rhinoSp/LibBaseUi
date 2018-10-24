package com.rhino.ui.view.viewpager.adapter;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.rhino.ui.view.viewpager.base.BaseHolder;
import com.rhino.ui.view.viewpager.base.BaseHolderData;
import com.rhino.ui.view.viewpager.base.BaseHolderFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by LuoLin on 2017/06/06
 */
public class BasePageAdapter extends RecyclingPagerAdapter {

    protected List<BaseHolderData> mDataList;
    protected BaseHolderFactory mBaseHolderFactory;

    public BasePageAdapter(BaseHolderFactory holderFactory, List<BaseHolderData> dataList) {
        this.mBaseHolderFactory = holderFactory;
        this.mDataList = new ArrayList<>();
        this.mDataList.addAll(dataList);
    }

    @SuppressWarnings("unchecked")
    @Override
    public View getView(int position, View view, ViewGroup container) {
        BaseHolder holder;
        if (view == null) {
            holder = mBaseHolderFactory.buildHolder(container, mDataList.get(position).getLayoutRes());
            view = holder.itemView;
            view.setTag(holder);
        } else {
            Object tag = view.getTag(mDataList.get(position).getLayoutRes());
            if(null == tag){
                holder = mBaseHolderFactory.buildHolder(container, mDataList.get(position).getLayoutRes());
                view = holder.itemView;
                view.setTag(holder);
            } else {
                holder = (BaseHolder)tag;
            }
        }
        holder.onBindView(position, mDataList.get(position));
        return holder.itemView;
    }

    @Override
    public int getCount() {
        return null != mDataList ? mDataList.size() : 0;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    /**
     * Get the data list.
     * @return list
     */
    public List<? extends BaseHolderData> getDataList(){
        return mDataList;
    }

    /**
     * Update the data.
     * @param mDataList list
     */
    public void updateData(@NonNull List<? extends BaseHolderData> mDataList){
        this.mDataList.clear();
        this.mDataList.addAll(mDataList);
    }

}
