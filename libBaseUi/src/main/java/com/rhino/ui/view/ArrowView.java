package com.rhino.ui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.rhino.ui.R;


/**
 * <p>“<<<” 这种带动画的图标,可设置箭头的方向</p>
 * Created by LuoLin on 2017/7/31
 **/
public class ArrowView extends View {

    private int mDuration = 1800;
    private Drawable drawable;
    private ValueAnimator animator;
    private int mFirAlpha;
    private int mSecAlpha;
    private int mAngle;

    public enum Gravity {
        Left,
        Top,
        Right,
        Bottom
    }

    public ArrowView(Context context) {
        this(context, null);
    }

    public ArrowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        drawable = getResources().getDrawable(R.mipmap.ic_arrow_anim);
        animator = ValueAnimator.ofFloat(0, 6);
        animator.setDuration(mDuration);
        animator.setRepeatCount(-1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (float) animation.getAnimatedValue();
                mFirAlpha = formatAlpha((int) (alpha * 255));
                mSecAlpha = formatAlpha((int) ((alpha - 0.5F) * 255));
                invalidate();
            }
        });
    }

    private int formatAlpha(int alpha) {
        if (alpha < 0) {
            return 0;
        }
        if (alpha > 255) {
            return 255;
        }
        return alpha;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());

        canvas.save();
        canvas.rotate(mAngle, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
        canvas.translate(0, getMeasuredHeight() / 5);
        drawable.setAlpha(mFirAlpha);
        drawable.draw(canvas);

        canvas.translate(0, -getHeight() * 2 / 5);
        drawable.setAlpha(mSecAlpha);
        drawable.draw(canvas);

        canvas.restore();
    }

    public void rotate(Gravity gravity) {
        switch (gravity) {
            case Left:
                mAngle = 270;
                break;
            case Top:
                mAngle = 0;
                break;
            case Right:
                mAngle = 90;
                break;
            case Bottom:
                mAngle = 180;
                break;
            default:
                break;
        }
    }

    public void setColorFilter(int color) {
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    public void start() {
        animator.start();
    }

    public void stop() {
        animator.cancel();
    }
}
