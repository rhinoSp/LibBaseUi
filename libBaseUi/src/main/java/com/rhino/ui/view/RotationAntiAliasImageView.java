package com.rhino.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by DengDongQi on 2019/9/3
 * 解决ImageView  setRotationX....等旋转时出现锯齿问题
 * <p>
 * 原理 : 某大佬研究出锯齿规律会出现为ImageView的边界 1-2 px处,解决办法是:
 * 重写onDraw且不走super方法,自己缩小ImageView内容1-2 px并居中绘制
 * <p>
 * 出处: (FQ)https://medium.com/@elye.project/smoothen-jagged-edges-of-rotated-image-view-1e56f6d8b5e9
 */
public class RotationAntiAliasImageView extends ImageView {

    private Paint paint;

    public RotationAntiAliasImageView(Context context) {
        super(context);
        init();
    }

    public RotationAntiAliasImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RotationAntiAliasImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
            BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

            Matrix matrix = new Matrix();
            float scaleY = (float) getHeight() / (float) bitmap.getHeight();
            float scaleX = (float) getWidth() / (float) bitmap.getWidth();
            matrix.setScale(scaleX, scaleY);
            shader.setLocalMatrix(matrix);
            paint.setShader(shader);
            canvas.drawColor(0);
            canvas.drawRect(0.0f, 0.0f, getWidth(), getHeight(), paint);
        } else {
            super.onDraw(canvas);
        }
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
    }
}