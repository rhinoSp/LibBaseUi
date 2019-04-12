package com.rhino.ui;

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

import com.rhino.ui.utils.ui.ColorUtils;
import com.rhino.ui.utils.ui.DrawableUtils;
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
    private ViewGroup mActionBar;
    /**
     * The content.
     */
    private ViewGroup mContent;
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
    /**
     * The click listener of title back key.
     */
    private View.OnClickListener mTitleBackKeyClickListener;


    public ActionBarHelper(Context context) {
        this.mContext = context;
    }

    public void init(ViewGroup actionBar, ViewGroup content) {
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
        initListener();
        initResources();
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
     * Init listener.
     */
    private void initListener() {
        mTitleBackContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mTitleBackKeyClickListener) {
                    mTitleBackKeyClickListener.onClick(v);
                }
            }
        });
        ViewTreeObserver vto = mActionBar.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                autoLayoutTitleTextView();
            }
        });
    }

    /**
     * Init resources.
     */
    private void initResources() {
        mStatusBarHeight = getStatusBarHeight(mContext);
        mTitleHeight = (int) mContext.getResources().getDimension(R.dimen.dp_35);
        mTitleKeyIconSize = (int) mContext.getResources().getDimension(R.dimen.dp_14);
        mTitleKeyTextSize = (int) mContext.getResources().getDimension(R.dimen.sp_14);
        mTitleKeyTextHorizontalMargin = (int) mContext.getResources().getDimension(R.dimen.dp_8);
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
    private void autoLayoutTitleTextView() {
        int leftPadding, rightPadding;
        if (Gravity.CENTER == mTitleTextView.getGravity()) {
            int lPadding = View.VISIBLE == mTitleBackContainer.getVisibility()
                    ? mTitleBackContainer.getWidth() : 0;
            lPadding += mTitleLeftContainer.getWidth();
            int rPadding = mTitleRightContainer.getWidth();
            leftPadding = Math.max(lPadding, rPadding);
            rightPadding = leftPadding;
        } else {
            leftPadding = View.VISIBLE == mTitleBackContainer.getVisibility()
                    ? mTitleBackContainer.getWidth() : 0;
            leftPadding += mTitleLeftContainer.getWidth();
            rightPadding = mTitleRightContainer.getWidth();
        }
        mTitleTextView.setPadding(leftPadding, 0, rightPadding, 0);
    }

    /**
     * Notify height of status bar.
     */
    private void notifyStatusBarHeight() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mStatusBar.getLayoutParams();
        lp.height = mStatusBarHeight;
    }

    /**
     * Notify height of title.
     */
    private void notifyTitleHeight() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTitleContainer.getLayoutParams();
        lp.height = mTitleHeight;
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
        if (!mStatusBarFloatAble && mStatusBar.getVisibility() == View.VISIBLE) {
            margin += mStatusBarHeight;
        }
        if (!mTitleFloatAble && mTitleContainer.getVisibility() == View.VISIBLE) {
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
        notifyContentTopMargin();
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
     * Set the title text size.
     *
     * @param textSize the title text size
     */
    public void setTitleTextSize(int textSize) {
        mTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }

    /**
     * Sets the horizontal alignment of the title text.
     *
     * @see android.view.Gravity
     */
    public void setTitleGravity(int gravity) {
        mTitleTextView.setGravity(gravity);
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
        notifyContentTopMargin();
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
     * Set the click listener of title back key.
     *
     * @param listener the click listener of title back key
     */
    public void setTitleBackKeyClickListener(View.OnClickListener listener) {
        this.mTitleBackKeyClickListener = listener;
    }

    /**
     * Get the height of title.
     *
     * @return the height of title
     */
    public int getTitleHeight() {
        return mTitleHeight;
    }

    /**
     * Set the height of title.
     *
     * @param titleHeight the height of title
     */
    public void setTitleHeight(int titleHeight) {
        this.mTitleHeight = titleHeight;
        this.notifyTitleHeight();
    }

    /**
     * Get the size of icon for title key.
     *
     * @return the title key icon size
     */
    public int getTitleKeyIconSize() {
        return mTitleKeyIconSize;
    }

    /**
     * Set the size of icon for title key.
     *
     * @param titleKeyIconSize the title key icon size
     */
    public void setTitleKeyIconSize(int titleKeyIconSize) {
        this.mTitleKeyIconSize = titleKeyIconSize;
    }

    /**
     * Get the size of text for title key.
     *
     * @return the title key text size
     */
    public int getTitleKeyTextSize() {
        return mTitleKeyTextSize;
    }

    /**
     * Set the size of text for title key.
     *
     * @param titleKeyTextSize the title key text size
     */
    public void setTitleKeyTextSize(int titleKeyTextSize) {
        this.mTitleKeyTextSize = titleKeyTextSize;
    }

    /**
     * Get the title key text horizontal margin.
     *
     * @return the title key text horizontal margin
     */
    public int getTitleKeyTextHorizontalMargin() {
        return mTitleKeyTextHorizontalMargin;
    }

    /**
     * Set the title key text horizontal margin.
     *
     * @param titleKeyTextHorizontalMargin the title key text horizontal margin
     */
    public void setTitleKeyTextHorizontalMargin(int titleKeyTextHorizontalMargin) {
        this.mTitleKeyTextHorizontalMargin = titleKeyTextHorizontalMargin;
    }

    /**
     * Add the title left key
     *
     * @param text     the text
     * @param listener the click listener
     */
    public TextView addTitleLeftKey(String text, View.OnClickListener listener) {
        return addTitleLeftKey(text, Color.WHITE, listener);
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
        rl.setOnClickListener(listener);

        TextView tv = new TextView(mContext);
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleKeyTextSize);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(DrawableUtils.buildColorStateList(color, color,
                ColorUtils.alphaColor(0.6f, color), color));

        rl.addView(tv);
        mTitleLeftContainer.addView(rl);
        autoLayoutTitleTextView();
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
        RelativeLayout rl = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams llLp = new RelativeLayout.LayoutParams(
                mTitleHeight, mTitleHeight);
        rl.setLayoutParams(llLp);
        rl.setGravity(Gravity.CENTER);
        rl.setOnClickListener(listener);

        FreeTintImageView iv = new FreeTintImageView(mContext);
        FrameLayout.LayoutParams ivLp = new FrameLayout.LayoutParams(
                mTitleKeyIconSize, mTitleKeyIconSize);
        iv.setLayoutParams(ivLp);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        iv.setImageResource(resId);
        iv.setColorStateList(DrawableUtils.buildColorStateList(color,
                color, ColorUtils.alphaColor(0.6f, color), color));
        rl.addView(iv);
        mTitleLeftContainer.addView(rl);
        autoLayoutTitleTextView();
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
        rl.setOnClickListener(listener);

        TextView tv = new TextView(mContext);
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleKeyTextSize);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(DrawableUtils.buildColorStateList(color, color,
                ColorUtils.alphaColor(0.6f, color), color));

        rl.addView(tv);
        mTitleRightContainer.addView(rl);
        autoLayoutTitleTextView();
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
        RelativeLayout rl = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams llLp = new RelativeLayout.LayoutParams(mTitleHeight, mTitleHeight);
        rl.setLayoutParams(llLp);
        rl.setGravity(Gravity.CENTER);
        rl.setOnClickListener(listener);

        FreeTintImageView iv = new FreeTintImageView(mContext);
        FrameLayout.LayoutParams ivLp = new FrameLayout.LayoutParams(mTitleKeyIconSize, mTitleKeyIconSize);
        iv.setLayoutParams(ivLp);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        iv.setImageResource(resId);
        iv.setColorStateList(DrawableUtils.buildColorStateList(color, color,
                ColorUtils.alphaColor(0.6f, color), color));
        rl.addView(iv);
        mTitleRightContainer.addView(rl);
        autoLayoutTitleTextView();
        return iv;
    }

    /**
     * Add the title right key
     *
     * @param view    the view
     */
    public View addTitleRightKey(View view) {
        mTitleRightContainer.addView(view);
        autoLayoutTitleTextView();
        return view;
    }

    /**
     * Clear the title right key
     */
    public void clearTitleRightKey() {
        mTitleRightContainer.removeAllViews();
    }

}
