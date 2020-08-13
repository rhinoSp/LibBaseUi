package com.rhino.ui.base;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.rhino.log.LogUtils;
import com.rhino.ui.R;
import com.rhino.ui.utils.StatusBarUtils;


/**
 * <p>The base activity extends BaseActivity, custom the tile, support add title button.<p>
 *
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public abstract class BaseSimpleTitleActivity<T extends ViewDataBinding> extends BaseActivity {

    /**
     * dataBinding
     */
    public T dataBinding;
    /**
     * The action bar helper.
     */
    public ActionBarHelper mActionBarHelper;
    /**
     * The action bar container.
     */
    public FrameLayout mActionBarContainer;
    /**
     * The content container.
     */
    public FrameLayout mContentContainer;
    /**
     * The content view.
     */
    public View mContentView;
    /**
     * The color of theme and theme light
     */
    public int mThemeColor, mThemeColorLight;


    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(R.layout.layout_page_base);
        initResources();
        View contentView = getLayoutInflater().inflate(layoutResID, null, false);
        initDataBinding(contentView);
        initBaseView(contentView);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(R.layout.layout_page_base);
        initResources();
        initDataBinding(view);
        initBaseView(view);
    }

    /**
     * Init dataBinding
     */
    public void initDataBinding(View contentView) {
        try {
            dataBinding = DataBindingUtil.bind(contentView);
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    /**
     * Init resources.
     */
    public void initResources() {
        mThemeColor = getResources().getColor(R.color.theme_color);
        mThemeColorLight = getResources().getColor(R.color.theme_color_light);
    }

    /**
     * Init the base view.
     *
     * @param contentView the content view
     */
    public void initBaseView(View contentView) {
        StatusBarUtils.setTranslucentStatus(this);
        mContentView = contentView;
        mActionBarContainer = findSubViewById(R.id.base_action_bar);
        mContentContainer = findSubViewById(R.id.base_container);

        mActionBarHelper = new ActionBarHelper(getApplicationContext());
        mActionBarHelper.init(mActionBarContainer, mContentContainer);
        mActionBarHelper.setTitle(this.getClass().getSimpleName());
        mActionBarHelper.setBackgroundColor(mThemeColor);
        mActionBarHelper.setTitleBackKeyVisible(true);
        mActionBarHelper.setTitleBackKeyClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTitleBackPressed();
            }
        });

        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mContentContainer.addView(contentView, lp);
    }

    /**
     * The click listener of title back key.
     */
    public void onTitleBackPressed() {
        onBackPressed();
    }

}
