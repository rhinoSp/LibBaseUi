package com.rhino.ui.base;

import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.rhino.ui.ActionBarHelper;
import com.rhino.ui.R;


/**
 * <p>The base activity extends BaseActivity, custom the tile, support add title button.<p>
 *
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public abstract class BaseSimpleTitleActivity extends BaseActivity {

    /**
     * The action bar helper.
     */
    protected ActionBarHelper mActionBarHelper;
    /**
     * The action bar container.
     */
    protected FrameLayout mActionBarContainer;
    /**
     * The content container.
     */
    protected FrameLayout mContentContainer;
    /**
     * The content view.
     */
    protected View mContentView;
    /**
     * The color of theme and theme light
     */
    protected int mThemeColor, mThemeColorLight;


    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(R.layout.layout_page_base);
        initResources();
        initBaseView(getLayoutInflater().inflate(layoutResID, null, false));
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(R.layout.layout_page_base);
        initResources();
        initBaseView(view);
    }

    /**
     * Init resources.
     */
    private void initResources() {
        mThemeColor = getResources().getColor(R.color.theme_color);
        mThemeColorLight = getResources().getColor(R.color.theme_color_light);
    }

    /**
     * Init the base view.
     *
     * @param contentView the content view
     */
    private void initBaseView(View contentView) {
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
    protected void onTitleBackPressed() {
        onBackPressed();
    }

}
