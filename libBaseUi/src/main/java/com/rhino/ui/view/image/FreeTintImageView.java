package com.rhino.ui.view.image;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.rhino.ui.R;
import com.rhino.ui.utils.ui.DrawableUtils;


/**
 * <p>This is custom ImageView to free tint</p>
 *
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public class FreeTintImageView extends AppCompatImageView {

    private ColorStateList mColorList;

    public FreeTintImageView(Context context) {
        this(context, null);
    }

    public FreeTintImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FreeTintImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        if (mColorList != null) {
            drawable = DrawableUtils.tintDrawable(drawable, mColorList);
        }
        super.setImageDrawable(drawable);
    }

    @Override
    public void setImageResource(int resId) {
        this.setImageDrawable(getResources().getDrawable(resId));
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FreeTintImageView);
        mColorList = ta.getColorStateList(R.styleable.FreeTintImageView_ftiv_stateTint);
        ta.recycle();
        refreshDrawable();
    }

    private void refreshDrawable() {
        Drawable drawable = getDrawable();
        if (drawable != null) {
            setImageDrawable(drawable);
        }
    }

    public void setColorStateList(ColorStateList list) {
        this.mColorList = list;
        refreshDrawable();
    }
}
