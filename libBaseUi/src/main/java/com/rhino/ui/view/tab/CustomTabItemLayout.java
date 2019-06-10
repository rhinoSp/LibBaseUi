package com.rhino.ui.view.tab;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rhino.ui.R;
import com.rhino.ui.view.image.FreeTintImageView;

/**
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public class CustomTabItemLayout extends LinearLayout {

    private int mIndex;

    private ViewGroup mTabItem;
    private ViewGroup mTabIconContainer;
    private FreeTintImageView mIcon;
    private TextView mTextNewFlag;
    private TextView mText;

    @DrawableRes
    private int[] mIconResIds;

    private CustomTabItemLayout(Context context) {
        super(context);
        inflate(getContext(), R.layout.widget_tab_item, this);
        mTabItem = findViewById(R.id.tab_item);
        mTabIconContainer = findViewById(R.id.tab_item_icon_container);
        mIcon = findViewById(R.id.tab_item_icon);
        mTextNewFlag = findViewById(R.id.tab_item_new_flag);
        mText = findViewById(R.id.tab_item_text);
    }

    /**
     * Set the select state.
     *
     * @param selected true selected
     */
    @Override
    public void setSelected(boolean selected) {
        mTabItem.setSelected(selected);
        if (mIconResIds != null && mIconResIds.length == 2) {
            mIcon.setImageResource(selected ? mIconResIds[1] : mIconResIds[0]);
        }
    }

    /**
     * Set the tab item icon.
     *
     * @param resId resId
     */
    public void setIcon(@DrawableRes int resId) {
        mIcon.setImageResource(resId);
    }

    /**
     * Set the tab item icon visible.
     *
     * @param visible visible
     */
    public void setIconVisible(boolean visible) {
        mTabIconContainer.setVisibility(visible ? VISIBLE : GONE);
    }

    /**
     * Set the tab item icon.
     *
     * @param mIconResIds mIconResIds
     */
    public void setIconResIds(@DrawableRes int[] mIconResIds) {
        this.mIconResIds = mIconResIds;
    }

    /**
     * Set the tab item text.
     *
     * @param text string
     */
    public void setText(@NonNull String text) {
        mText.setText(text);
    }

    /**
     * Set the tab item text visible.
     *
     * @param visible visible
     */
    public void setTextVisible(boolean visible) {
        mText.setVisibility(visible ? VISIBLE : GONE);
    }

    /**
     * Get the tab item text.
     *
     * @return text
     */
    public String getText() {
        return mText.getText().toString();
    }

    /**
     * Set the new flag text.
     *
     * @param text string
     */
    public void setNewFlag(String text) {
        mTextNewFlag.setText(text);
    }

    /**
     * Set the new flag visibility.
     *
     * @param visible visible
     */
    public void setNewFlagVisible(boolean visible) {
        mTextNewFlag.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     * Get the tab item index.
     *
     * @return index
     */
    public int getIndex() {
        return mIndex;
    }

    /**
     * Set the tab item index.
     *
     * @param index index
     */
    void setIndex(int index) {
        mIndex = index;
    }

    public void setColorStateList(@NonNull ColorStateList list) {
        if (mIconResIds != null && mIconResIds.length == 1) {
            mIcon.setColorStateList(list);
        }
        mText.setTextColor(list);
    }

    public static CustomTabItemLayout build(Context context, @DrawableRes int[] mIconRids, String text) {
        CustomTabItemLayout tabItem = new CustomTabItemLayout(context);
        tabItem.setIconResIds(mIconRids);
        if (mIconRids != null && mIconRids.length > 0) {
            tabItem.setIconVisible(true);
            tabItem.setIcon(mIconRids[0]);
        } else {
            tabItem.setIconVisible(false);
        }
        tabItem.setText(text);
        if (TextUtils.isEmpty(text)) {
            tabItem.setTextVisible(false);
        } else {
            tabItem.setTextVisible(true);
        }
        return tabItem;
    }

}
