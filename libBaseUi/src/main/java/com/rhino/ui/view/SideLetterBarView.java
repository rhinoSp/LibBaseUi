package com.rhino.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * <p>The custom view to select the letter.</p>
 *
 * @author LuoLin
 * @since Created on 2017/8/18.
 **/
public class SideLetterBarView extends View {

    /**
     * The default text size.
     */
    private static final int DF_TEXT_SIZE = 20;
    /**
     * The default text color.
     */
    private static final int DF_TEXT_COLOR = Color.GRAY;
    /**
     * The default text color when select.
     */
    private static final int DF_TEXT_COLOR_SELECT = Color.BLACK;

    /**
     * The text color.
     */
    private int mTextColor = DF_TEXT_COLOR;
    /**
     * The text color when select.
     */
    private int mTextColorSelect = DF_TEXT_COLOR_SELECT;

    /**
     * The letter array.
     */
    private String[] mLetterDesc;
    /**
     * The paint.
     */
    private Paint mPaint;
    /**
     * To show the current letter.
     */
    private TextView mTvShowCurrSel;
    /**
     * The index current select.
     */
    private int mCurrSelIndex = -1;
    /**
     * The letter changed listener.
     */
    private IOnLetterChangedListener onLetterChangedListener;


    public SideLetterBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public SideLetterBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SideLetterBarView(Context context) {
        super(context);
        init();
    }

    private void init(){

        mPaint = new Paint();
        mPaint.setTextSize(DF_TEXT_SIZE);
        mPaint.setColor(mTextColor);
        mPaint.setAntiAlias(true);

        mLetterDesc = new String[]{"A", "B", "C", "D", "E", "F",
                "G", "H", "I", "J", "K", "L", "M","N", "O", "P",
                "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getHeight();
        int width = getWidth();
        int singleHeight = height / mLetterDesc.length;
        for (int i = 0; i < mLetterDesc.length; i++) {
            if (i == mCurrSelIndex) {
                mPaint.setColor(mTextColorSelect);
                mPaint.setFakeBoldText(true);
            } else {
                mPaint.setColor(mTextColor);
                mPaint.setFakeBoldText(false);
            }
            float xPos = width / 2 - mPaint.measureText(mLetterDesc[i]) / 2;
            float yPos = singleHeight * i + singleHeight;
            canvas.drawText(mLetterDesc[i], xPos, yPos, mPaint);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int index = (int) (event.getY() / getHeight() * mLetterDesc.length);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (mCurrSelIndex != index && onLetterChangedListener != null) {
                    if (index >= 0 && index < mLetterDesc.length) {
                        onLetterChangedListener.onLetterChanged(mLetterDesc[index], false);
                        mCurrSelIndex = index;
                        invalidate();
                        showCurrSelLetter(mLetterDesc[index]);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if(null != onLetterChangedListener){
                    onLetterChangedListener.onLetterChanged(mLetterDesc[mCurrSelIndex], true);
                }
                mCurrSelIndex = -1;
                invalidate();
                showCurrSelLetter(null);
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * show current letter.
     * @param letter the letter string
     */
    private void showCurrSelLetter(String letter){
        if(null == mTvShowCurrSel){
            return;
        }
        if(TextUtils.isEmpty(letter)){
            mTvShowCurrSel.setVisibility(GONE);
        } else {
            mTvShowCurrSel.setVisibility(VISIBLE);
            mTvShowCurrSel.setText(letter);
        }
    }

    /**
     * Set the TextView to show select letter.
     * @param textView the TextView
     */
    public void setShowCurrentSelTextView(@NonNull TextView textView){
        this.mTvShowCurrSel = textView;
    }

    /**
     * Set the letter string array.
     * @param letterDesc the letter string array
     */
    public void setLetterDesc(@NonNull String[] letterDesc) {
        mLetterDesc = letterDesc;
    }

    /**
     * Set the paint's text size. This value must be > 0
     *
     * @param textSize set the paint's text size.
     */
    public void setTextSise(float textSize){
        mPaint.setTextSize(textSize);
    }

    /**
     * Set the text color.
     * @param color the text color.
     */
    public void setTextColor(@ColorInt int color) {
        this.mTextColor = color;
    }

    /**
     * Set the text color when select.
     * @param color the text color when select.
     */
    public void setTextColorSelect(@ColorInt int color) {
        this.mTextColorSelect = color;
    }

    /**
     * Set the letter changed listener.
     * @param onLetterChangedListener the letter changed listener
     */
    public void setOnLetterChangedListener(IOnLetterChangedListener onLetterChangedListener) {
        this.onLetterChangedListener = onLetterChangedListener;
    }

    public interface IOnLetterChangedListener {
        void onLetterChanged(String letter, boolean isFinished);
    }

}
