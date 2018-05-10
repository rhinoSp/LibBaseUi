package com.rhino.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rhino.ui.utils.ColorUtils;
import com.rhino.ui.utils.DrawableUtils;
import com.rhino.ui.view.image.FreeTintImageView;


/**
 * <p>The action bar helper</p>
 *
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public class ActionBarHelper {

    /**
     * The context.
     */
    private Context mContext;
    /**
     * The action bar.
     */
    private FrameLayout mActionBar;
    /**
     * The content.
     */
    private FrameLayout mContent;
    /**
     * The status bar.
     */
    private View mStatusBar;
    /**
     * The title container.
     */
    private View mTitleContainer;
    /**
     * The left back key container of title.
     */
    private View mTitleBackContainer;
    /**
     * The title left container.
     */
    private ViewGroup mTitleLeftContainer;
    /**
     * The title right container.
     */
    private ViewGroup mTitleRightContainer;
    /**
     * The title TextView.
     */
    private TextView mTitleTextView;
    /**
     * The status bar height init in dimen.xml
     */
    private int mStatusBarHeight;
    /**
     * The title height init in dimen.xml
     */
    private int mTitleHeight;
    /**
     * The title key icon size init in dimen.xml
     */
    private int mTitleKeyIconSize;
    /**
     * The title key text size init in dimen.xml
     */
    private int mTitleKeyTextSize;
    /**
     * The title key text horizontal margin init in dimen.xml
     */
    private int mTitleKeyTextHorizontalMargin;
    /**
     * The flag of StatusBar float able.
     */
    private boolean mStatusBarFloatAble = false;
    /**
     * The flag of Title float able.
     */
    private boolean mTitleFloatAble = false;


    public ActionBarHelper(Context context) {
        this.mContext = context;
    }

    public void init(FrameLayout actionBar, FrameLayout content) {
        this.mActionBar = actionBar;
        this.mContent = content;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        inflater.inflate(R.layout.layout_action_bar, mActionBar, true);
        mStatusBar = findSubViewById(R.id.action_bar_status);
        mTitleContainer = findSubViewById(R.id.action_bar_title_container);
        mTitleBackContainer = findSubViewById(R.id.action_bar_back_key_container);
        mTitleLeftContainer = findSubViewById(R.id.action_bar_left_container);
        mTitleTextView = findSubViewById(R.id.action_bar_title);
        mTitleRightContainer = findSubViewById(R.id.action_bar_right_container);

        mTitleBackContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) mContext).onBackPressed();
            }
        });

        ViewTreeObserver vto = mActionBar.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                adjustTitlePosition();
            }
        });

        mStatusBarHeight = getStatusBarHeight(mContext);
        mTitleHeight = (int) mContext.getResources().getDimension(R.dimen.action_bar_height);
        mTitleKeyIconSize = (int) mContext.getResources().getDimension(R.dimen.action_bar_key_icon_size);
        mTitleKeyTextSize = (int) mContext.getResources().getDimension(R.dimen.action_bar_key_text_size);
        mTitleKeyTextHorizontalMargin = (int) mContext.getResources().getDimension(R.dimen.action_bar_key_text_horizontal_margin);

        notifyStatusBarHeight();
        notifyContentTopMargin();
    }

    /**
     * Find the view by view id
     *
     * @param id view id
     * @return the view
     */
    @SuppressWarnings("unchecked")
    protected <T extends View> T findSubViewById(@IdRes int id) {
        return (T) mActionBar.findViewById(id);
    }

    /**
     * Get the status bar height.
     *
     * @param ctx the context
     * @return the status bar height
     */
    private int getStatusBarHeight(Context ctx) {
        int result = 0;
        int resourceId = ctx.getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            result = ctx.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * Adjust title position.
     */
    private void adjustTitlePosition() {
        int leftPadding = View.VISIBLE == mTitleBackContainer.getVisibility()
                ? mTitleBackContainer.getWidth() : 0;
        leftPadding += mTitleLeftContainer.getWidth();
        int rightPadding = mTitleRightContainer.getWidth();
        int maxPadding = Math.max(leftPadding, rightPadding);
        mTitleTextView.setPadding(maxPadding, 0, maxPadding, 0);
    }

    /**
     * Notify status bar.
     */
    private void notifyStatusBarHeight() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mStatusBar.getLayoutParams();
        lp.height = mStatusBarHeight;
    }

    /**
     * Notify content view.
     */
    private void notifyContentTopMargin() {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mContent.getLayoutParams();
        int oldTopMargin = lp.topMargin;
        int newTopMargin = getContentTopMargin();
        if (oldTopMargin != newTopMargin) {
            lp.topMargin = newTopMargin;
        }
    }

    /**
     * Get the top margin of content view.
     *
     * @return the top margin of content view.
     */
    private int getContentTopMargin() {
        int margin = 0;
        if (!mStatusBarFloatAble) {
            margin += mStatusBarHeight;
        }
        if (!mTitleFloatAble) {
            margin += mTitleHeight;
        }
        return margin;
    }

    /**
     * Set the title float.
     *
     * @param enable true：float on content, false：normal
     */
    public void setStatusBarFloatAble(boolean enable) {
        this.mStatusBarFloatAble = enable;
        this.notifyContentTopMargin();
    }

    /**
     * Set the status bar float.
     *
     * @param enable true：float on content, false：normal
     */
    public void setTitleFloatAble(boolean enable) {
        this.mTitleFloatAble = enable;
        this.notifyContentTopMargin();
    }

    /**
     * Set the action bar background color.
     *
     * @param color the color
     */
    public void setBackgroundColor(@ColorInt int color) {
        mActionBar.setBackgroundColor(color);
    }

    /**
     * Set the action bar background drawable.
     *
     * @param drawable the drawable
     */
    public void setBackgroundDrawable(Drawable drawable) {
        mActionBar.setBackgroundDrawable(drawable);
    }

    /**
     * Set the title visible.
     *
     * @param visible true：visible, false：gone
     */
    public void setTitleVisible(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        mTitleContainer.setVisibility(visibility);
    }

    /**
     * Set the title text.
     *
     * @param text text
     */
    public void setTitle(String text) {
        mTitleTextView.setText(text);
    }

    /**
     * Set the title background color.
     *
     * @param color the color
     */
    public void setTitleBackgroundColor(@ColorInt int color) {
        mTitleContainer.setBackgroundColor(color);
    }

    /**
     * Set the title background drawable.
     *
     * @param drawable the drawable
     */
    public void setTitleBackgroundDrawable(Drawable drawable) {
        mTitleContainer.setBackgroundDrawable(drawable);
    }

    /**
     * Set the status bar visible.
     *
     * @param visible true：visible, false：gone
     */
    public void setStatusBarVisible(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        mStatusBar.setVisibility(visibility);
    }

    /**
     * Set the status bar color.
     *
     * @param color the color
     */
    public void setStatusBarColor(@ColorInt int color) {
        mStatusBar.setBackgroundColor(color);
    }

    /**
     * Set the title back key visible.
     *
     * @param visible true：visible, false：gone
     */
    public void setTitleBackKeyVisible(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        mTitleBackContainer.setVisibility(visibility);
    }

    /**
     * Add the title left key
     *
     * @param text     the text
     * @param listener the click listener
     */
    public TextView addTitleLeftKey(String text, View.OnClickListener listener) {
        return addTitleRightKey(text, Color.WHITE, listener);
    }

    /**
     * Add the title left key
     *
     * @param text     the text
     * @param color    the color
     * @param listener the click listener
     */
    public TextView addTitleLeftKey(String text, @ColorInt int color,
                                    View.OnClickListener listener) {
        RelativeLayout rl = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams rlLp = new RelativeLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, mTitleHeight);
        rl.setLayoutParams(rlLp);
        rl.setMinimumWidth(mTitleHeight);
        rl.setGravity(Gravity.CENTER);
        rl.setPadding(mTitleKeyTextHorizontalMargin, 0, mTitleKeyTextHorizontalMargin, 0);

        TextView tv = new TextView(mContext);
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleKeyTextSize);
        tv.setGravity(Gravity.CENTER);
        tv.setOnClickListener(listener);
        tv.setTextColor(DrawableUtils.buildColorStateList(color, color,
                ColorUtils.alphaColor(0.8f, color), color));

        rl.addView(tv);
        mTitleLeftContainer.addView(rl);
        adjustTitlePosition();
        return tv;
    }

    /**
     * Add the title left key
     *
     * @param resId    the image id
     * @param listener the click listener
     */
    public ImageView addTitleLeftKey(@DrawableRes int resId, View.OnClickListener listener) {
        return addTitleLeftKey(resId, Color.WHITE, listener);
    }

    /**
     * Add the title left key
     *
     * @param resId    the image id
     * @param color    the color id
     * @param listener the click listener
     */
    public ImageView addTitleLeftKey(@DrawableRes int resId, @ColorInt int color,
                                     View.OnClickListener listener) {
        RelativeLayout ll = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams llLp = new RelativeLayout.LayoutParams(
                mTitleHeight, mTitleHeight);
        ll.setLayoutParams(llLp);
        ll.setGravity(Gravity.CENTER);

        FreeTintImageView iv = new FreeTintImageView(mContext);
        FrameLayout.LayoutParams ivLp = new FrameLayout.LayoutParams(
                mTitleKeyIconSize, mTitleKeyIconSize);
        iv.setLayoutParams(ivLp);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        iv.setImageResource(resId);
        iv.setOnClickListener(listener);
        iv.setColorStateList(DrawableUtils.buildColorStateList(color,
                color, ColorUtils.alphaColor(0.8f, color), color));
        ll.addView(iv);
        mTitleLeftContainer.addView(ll);
        adjustTitlePosition();
        return iv;
    }

    /**
     * Clear the title left key
     */
    public void clearTitleLeftKey() {
        mTitleLeftContainer.removeAllViews();
    }

    /**
     * Add the title right key
     *
     * @param text     the text
     * @param listener the click listener
     */
    public TextView addTitleRightKey(String text, View.OnClickListener listener) {
        return addTitleRightKey(text, Color.WHITE, listener);
    }

    /**
     * Add the title right key
     *
     * @param text     the text
     * @param color    the color
     * @param listener the click listener
     */
    public TextView addTitleRightKey(String text, @ColorInt int color,
                                     View.OnClickListener listener) {
        RelativeLayout rl = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams rlLp = new RelativeLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, mTitleHeight);
        rl.setLayoutParams(rlLp);
        rl.setMinimumWidth(mTitleHeight);
        rl.setGravity(Gravity.CENTER);
        rl.setPadding(mTitleKeyTextHorizontalMargin, 0,
                mTitleKeyTextHorizontalMargin, 0);

        TextView tv = new TextView(mContext);
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleKeyTextSize);
        tv.setGravity(Gravity.CENTER);
        tv.setOnClickListener(listener);
        tv.setTextColor(DrawableUtils.buildColorStateList(color, color,
                ColorUtils.alphaColor(0.8f, color), color));

        rl.addView(tv);
        mTitleRightContainer.addView(rl);
        adjustTitlePosition();
        return tv;
    }

    /**
     * Add the title right key
     *
     * @param resId    the image id
     * @param listener the click listener
     */
    public ImageView addTitleRightKey(@DrawableRes int resId, View.OnClickListener listener) {
        return addTitleRightKey(resId, Color.WHITE, listener);
    }

    /**
     * Add the title right key
     *
     * @param resId    the image id
     * @param color    the color id
     * @param listener the click listener
     */
    public ImageView addTitleRightKey(@DrawableRes int resId, @ColorInt int color,
                                      View.OnClickListener listener) {
        RelativeLayout ll = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams llLp = new RelativeLayout.LayoutParams(mTitleHeight, mTitleHeight);
        ll.setLayoutParams(llLp);
        ll.setGravity(Gravity.CENTER);

        FreeTintImageView iv = new FreeTintImageView(mContext);
        FrameLayout.LayoutParams ivLp = new FrameLayout.LayoutParams(mTitleKeyIconSize, mTitleKeyIconSize);
        iv.setLayoutParams(ivLp);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        iv.setImageResource(resId);
        iv.setOnClickListener(listener);
        iv.setColorStateList(DrawableUtils.buildColorStateList(color, color,
                ColorUtils.alphaColor(0.8f, color), color));
        ll.addView(iv);
        mTitleRightContainer.addView(ll);
        adjustTitlePosition();
        return iv;
    }

    /**
     * Clear the title right key
     */
    public void clearTitleRightKey() {
        mTitleRightContainer.removeAllViews();
    }

}
