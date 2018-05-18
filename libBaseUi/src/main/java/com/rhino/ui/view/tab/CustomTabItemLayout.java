package com.rhino.ui.view.tab;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
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
    private FreeTintImageView mIcon;
    private TextView mTextNewFlag;
    private TextView mText;

    private CustomTabItemLayout(Context context) {
        super(context);
        inflate(getContext(), R.layout.widget_tab_item, this);
        mTabItem = findViewById(R.id.tab_item);
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
     * Set the tab item text.
     *
     * @param text string
     */
    public void setText(@NonNull String text) {
        mText.setText(text);
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
        mIcon.setColorStateList(list);
        mText.setTextColor(list);
    }

    public static CustomTabItemLayout build(Context context, @DrawableRes int iconRid, String text) {
        CustomTabItemLayout tabItem = new CustomTabItemLayout(context);
        tabItem.setIcon(iconRid);
        tabItem.setText(text);
        return tabItem;
    }

}
