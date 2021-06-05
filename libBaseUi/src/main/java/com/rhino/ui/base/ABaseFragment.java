package com.rhino.ui.base;

import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModel;

import com.rhino.log.LogUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author LuoLin
 * @since Create on 2021/6/4
 **/
public abstract class ABaseFragment<T extends ViewDataBinding, K extends ViewModel> extends BaseSimpleTitleFragment {

    /**
     * ViewDataBinding
     */
    public T dataBinding;
    /**
     * ViewModel
     */
    public K viewModel;

    @LayoutRes
    public abstract int getLayoutResId();

    public abstract void initData();

    @Override
    public void setContent() {
        setContentView(getLayoutResId());
    }

    @Override
    public void initBaseView(View contentView) {
        initDataBinding(contentView);
        initViewModel();
        initData();
        super.initBaseView(contentView);
    }

    /**
     * Init ViewDataBinding
     */
    public void initDataBinding(View contentView) {
        try {
            dataBinding = DataBindingUtil.bind(contentView);
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    /**
     * Init ViewModel
     */
    public void initViewModel() {
        Type genericSuperclass = getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            try {
                viewModel = getViewModel((Class<K>) ((ParameterizedType) genericSuperclass).getActualTypeArguments()[1]);
            } catch (Exception e) {
                LogUtils.e(e);
            }
        }
    }
}
