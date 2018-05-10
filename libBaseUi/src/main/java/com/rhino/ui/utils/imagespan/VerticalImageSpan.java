package com.rhino.ui.utils.imagespan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.style.ImageSpan;

/**
 * <p>The vertical ImageSpan</p>
 *
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public class VerticalImageSpan extends ImageSpan {

    public VerticalImageSpan(Bitmap b) {
        super(b);
    }

    public VerticalImageSpan(Bitmap b, int verticalAlignment) {
        super(b, verticalAlignment);
    }

    public VerticalImageSpan(Context context, Bitmap b) {
        super(context, b);
    }

    public VerticalImageSpan(Context context, Bitmap b, int verticalAlignment) {
        super(context, b, verticalAlignment);
    }

    public VerticalImageSpan(Drawable d) {
        super(d);
    }

    public VerticalImageSpan(Drawable d, int verticalAlignment) {
        super(d, verticalAlignment);
    }

    public VerticalImageSpan(Drawable d, String source) {
        super(d, source);
    }

    public VerticalImageSpan(Drawable d, String source, int verticalAlignment) {
        super(d, verticalAlignment);
    }

    public VerticalImageSpan(Context context, Uri uri) {
        super(context, uri);
    }

    public VerticalImageSpan(Context context, Uri uri, int verticalAlignment) {
        super(context, uri, verticalAlignment);
    }

    public VerticalImageSpan(Context context, int resourceId) {
        super(context, resourceId);
    }

    public VerticalImageSpan(Context context, int resourceId, int verticalAlignment) {
        super(context, resourceId, verticalAlignment);
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end,
            Paint.FontMetricsInt fontMetricsInt) {
        Drawable drawable = getDrawable();
        Rect rect = drawable.getBounds();
        if (fontMetricsInt != null) {
            Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
            int fontHeight = fmPaint.bottom - fmPaint.top;
            int drHeight = rect.bottom - rect.top;

            int top = drHeight / 2 - fontHeight / 4;
            int bottom = drHeight / 2 + fontHeight / 4;

            fontMetricsInt.ascent = -bottom;
            fontMetricsInt.top = -bottom;
            fontMetricsInt.bottom = top;
            fontMetricsInt.descent = top;
        }
        return rect.right;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x,
            int top, int y, int bottom, Paint paint) {
        Drawable drawable = getDrawable();
        canvas.save();
        int transY = ((bottom - top) - drawable.getBounds().bottom) / 2 + top;
        canvas.translate(x, transY);
        drawable.draw(canvas);
        canvas.restore();
    }
} 