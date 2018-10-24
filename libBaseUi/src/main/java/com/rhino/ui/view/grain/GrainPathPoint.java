package com.rhino.ui.view.grain;


import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * @since Created by LuoLin on 2018/1/26.
 **/
public class GrainPathPoint {
    public static final int STYLE_LINE = 0;
    public static final int STYLE_ARC = 1;
    public int style;

    // for simple
    public float x;
    public float y;

    // for arc
    public RectF rectF;
    public float startAngle;
    public float sweepAngle;


    public static GrainPathPoint newPoint(float x, float y) {
        GrainPathPoint point = new GrainPathPoint();
        point.style = STYLE_LINE;
        point.x = x;
        point.y = y;
        return point;
    }

    public static GrainPathPoint newArc(float left, float top, float right, float bottom,
            float startAngle, float sweepAngle) {
        GrainPathPoint point = new GrainPathPoint();
        point.style = STYLE_ARC;
        point.rectF = new RectF(left, top, right, bottom);
        point.startAngle = startAngle;
        point.sweepAngle = sweepAngle;
        return point;
    }

    public static Path getGrainPath(@NonNull List<GrainPathPoint> pathPoints) {
        Path mPath = new Path();
        int count = pathPoints.size();
        for (int i = 0; i < count; i++) {
            GrainPathPoint pathPoint = pathPoints.get(i);
            if (0 == i) {
                mPath.moveTo(pathPoint.x, pathPoint.y);
            } else {
                if (GrainPathPoint.STYLE_LINE == pathPoint.style) {
                    mPath.lineTo(pathPoint.x, pathPoint.y);
                } else if (GrainPathPoint.STYLE_ARC == pathPoint.style) {
                    mPath.arcTo(pathPoint.rectF, pathPoint.startAngle, pathPoint.sweepAngle);
                }
            }
        }
        return mPath;
    }
}