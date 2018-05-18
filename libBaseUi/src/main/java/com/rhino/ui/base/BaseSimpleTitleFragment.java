package com.rhino.ui.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.rhino.ui.ActionBarHelper;
import com.rhino.ui.R;


/**
 * <p>The base fragment extends BaseFragment, custom the tile, support add title button.<p>
 *
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public abstract class BaseSimpleTitleFragment extends BaseFragment {

    /**
     * The content view layout id.
     */
    protected int mContentId;
    /**
     * The content view.
     */
    protected View mContentView;
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
     * The color of theme and theme light.
     */
    protected int mThemeColor, mThemeColorLight;


    @Override
    protected void setContentView(int layoutId) {
        mContentId = layoutId;
    }

    @Override
    protected void setContentView(@NonNull View contentView) {
        mContentView = contentView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initResources();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mParentView = inflater.inflate(R.layout.layout_page_base, container, false);
        if (0 != mContentId) {
            mContentView = inflater.inflate(mContentId, container, false);
        }
        initBaseView(mParentView);
        return mParentView;
    }

    /**
     * Init resources
     */
    private void initResources() {
        mThemeColor = getResources().getColor(R.color.theme_color);
        mThemeColorLight = getResources().getColor(R.color.theme_color_light);
    }

    /**
     * Init the base view
     *
     * @param parent view
     */
    private void initBaseView(View parent) {
        mActionBarContainer = findSubViewById(R.id.base_action_bar);
        mContentContainer = findSubViewById(R.id.base_container, parent);

        mActionBarHelper = new ActionBarHelper(getContext());
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
        mContentContainer.addView(mContentView, lp);
    }

    /**
     * The click listener of title back key.
     */
    protected void onTitleBackPressed() {
        Activity activity = getActivity();
        if (null != activity) {
            activity.onBackPressed();
        }
    }

}
