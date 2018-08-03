package com.rhino.ui.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.rhino.ui.decoration.SimpleItemDecoration;


/**
 * <p>The list fragment of RecyclerView</p>
 *
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public abstract class BaseListFragment extends BaseFragment {

    /**
     * The RecyclerView.
     */
    private RecyclerView mRecyclerView;
    /**
     * The Adapter of RecyclerView.
     */
    private RecyclerView.Adapter mRecyclerAdapter;
    /**
     * The LayoutManager of RecyclerView.
     */
    private RecyclerView.LayoutManager mLayoutManager;
    /**
     * The ItemDecoration of RecyclerView.
     */
    private RecyclerView.ItemDecoration mItemDecoration;


    @Override
    protected void setContent() {
        mRecyclerView = new RecyclerView(getActivity());
        setContentView(mRecyclerView);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initRecycler();
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * Do something init.
     */
    protected void initRecycler() {
        setLayoutManager(mLayoutManager);
        setItemDecoration(mItemDecoration);
        setAdapter(mRecyclerAdapter);
        notifyDataSetChanged();
    }

    /**
     * Notify any registered observers that the data set has changed.
     */
    final protected void notifyDataSetChanged() {
        if (mRecyclerAdapter != null) {
            mRecyclerAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Set the ItemDecoration.
     *
     * @param itemDecoration the ItemDecoration
     */
    final protected void setItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        if (null == mRecyclerView) {
            return;
        }
        mItemDecoration = itemDecoration;
        if (null == mItemDecoration) {
            mItemDecoration = new SimpleItemDecoration(getActivity());
        }
        mRecyclerView.addItemDecoration(mItemDecoration);
    }

    /**
     * Set the LayoutManager.
     *
     * @param layoutManager the LayoutManager
     */
    final protected void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        if (null == mRecyclerView) {
            return;
        }
        mLayoutManager = layoutManager;
        if (null == mLayoutManager) {
            mLayoutManager = new LinearLayoutManager(getActivity());
        }
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    /**
     * Set the Adapter.
     *
     * @param adapter the Adapter
     */
    final protected void setAdapter(RecyclerView.Adapter adapter) {
        if (null == mRecyclerView || null == adapter) {
            return;
        }
        mRecyclerAdapter = adapter;
        mRecyclerView.setAdapter(mRecyclerAdapter);
    }
}
