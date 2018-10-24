package com.rhino.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * 圆盘视图</br>
 * Created by LuoLin on 2017/5/15
 **/
public class CircleShape extends View {

    /**
     * view的宽度
     **/
    private int mViewWidth;
    /**
     * view的高度
     **/
    private int mViewHeight;
    /**
     * 最大圆半径
     **/
    private int mMaxCircleRadius;
    /**
     * paint
     **/
    private Paint mPaint;
    /**
     * 圆盘数据列表
     **/
    private ArrayList<shape> shapesList;

    public CircleShape(Context context) {
        super(context);
        init();
    }

    public CircleShape(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            mViewWidth = widthSize;
        } else {
            mViewWidth = getWidth();
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            mViewHeight = heightSize;
        } else {
            mViewHeight = getHeight();
        }

        setMeasuredDimension(mViewWidth, mViewHeight);
        initView(mViewWidth, mViewHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(mViewWidth / 2, mViewHeight / 2);
        drawCircleRingBg(canvas);
        canvas.restore();
    }

    /**
     * 绘圆环
     **/
    private void drawCircleRingBg(Canvas canvas) {
        canvas.save();
        for (int i = 0; i < shapesList.size(); i++) {
            mPaint.reset();
            mPaint.setColor(shapesList.get(i).color);
            canvas.drawCircle(0, 0, shapesList.get(i).scale * mMaxCircleRadius, mPaint);
        }
        canvas.restore();
    }

    /**
     * 初始化
     **/
    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        shapesList = new ArrayList<>();
    }

    /**
     * 初始化视图
     **/
    private void initView(int width, int height) {
        if (0 >= width || 0 >= height) {
            return;
        }
        mMaxCircleRadius = width > height ? height / 2 : width / 2;
    }

    class shape {

        /**
         * 比例0.0f - 1.0f
         **/
        float scale;
        /**
         * 颜色
         **/
        int color;

        public shape(float scale, int color) {
            this.scale = scale;
            this.color = color;
        }
    }

    /**
     * 增加一个圆盘，先添加的先绘制
     **/
    public void addShape(float scale, int color) {
        shapesList.add(new shape(scale, color));
    }
}
