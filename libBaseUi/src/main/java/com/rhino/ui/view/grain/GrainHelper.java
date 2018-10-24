package com.rhino.ui.view.grain;


import android.content.Context;
import android.graphics.Path;

import java.util.ArrayList;
import java.util.List;

/**
 * @since Created by LuoLin on 2018/1/26.
 **/
public class GrainHelper {

    /**
     * The default width of grain.(dp)
     **/
    private static final float DEFAULT_GRAIN_WIDTH = 1f;
    /**
     * The default gap ratio for radius.(1/3.25)
     **/
    private static final float DEFAULT_GRAIN_GAP_RATIO_FOR_RADIUS = 0.3077f;
    /**
     * The default center gap ratio for radius.(1/5)
     **/
    private static final float DEFAULT_CENTER_GAP_RATIO_FOR_RADIUS = 0.2f;
    /**
     * The default margin line length ratio of ending for width.(2/360)
     **/
    private static final float DEFAULT_END_MARGIN_LINE_LENGTH_RATIO_FOR_WIDTH = 0.0056f;
    /**
     * The default radius ratio of peak big for width.(12/360)
     **/
    private static final float DEFAULT_PEAK_BIG_RADIUS_RATIO_FOR_WIDTH = 0.0333f;
    /**
     * The default height ratio of first peak for height.((55-12)/300)
     **/
    private static final float DEFAULT_PEAK1_HEIGHT_RATIO_FOR_HEIGHT = 0.1433f;
    /**
     * The default height ratio of second peak for height.((102-12)/300)
     **/
    private static final float DEFAULT_PEAK2_HEIGHT_RATIO_FOR_HEIGHT = 0.3f;
    /**
     * The default height ratio of third peak for height.((122-12)/300)
     **/
    private static final float DEFAULT_PEAK3_HEIGHT_RATIO_FOR_HEIGHT = 0.3667f;
    /**
     * The default height ratio of fourth peak for height.((45-12)/300)
     **/
    private static final float DEFAULT_PEAK4_HEIGHT_RATIO_FOR_HEIGHT = 0.11f;
    /**
     * The default height ratio of center bolt for height.((93-12)/300)
     **/
    private static final float DEFAULT_CENTER_BOLT_HEIGHT_SCALE_FOR_HEIGHT = 0.27f;
    /**
     * The default angle A left center flag.
     */
    private static final float DEFAULT_ANGLE_A_LEFT_CENTER_FLAG = 48.5f;
    /**
     * The default angle A right center flag.
     */
    private static final float DEFAULT_ANGLE_B_RIGHT_CENTER_FLAG = 78f;
    /**
     * The default ratio for left line and right line of center flag.(48/65)
     */
    private static final float DEFAULT_RATIO_LEFT_LINE_AND_RIGHT_LINE_OF_CENTER_FLAG = 0.7385f;

    /**
     * The width of view.
     */
    private int mViewWidth;
    /**
     * The height of view.
     */
    private int mViewHeight;
    /**
     * The margin line length scale of ending.
     **/
    private float mEndMarginLineLength;
    /**
     * The width of per grain.
     **/
    private float mGrainWidth;
    /**
     * The gap of per grain.
     **/
    private float mGrainGap;
    /**
     * The radius scale of peak big.
     **/
    private float mPeakBigRadius;
    /**
     * The y offset of peak big.
     **/
    private float mPeakBigYOffset;
    /**
     * The height scale of first peak.
     **/
    private float mPeakHeight1;
    /**
     * The height scale of second peak.
     **/
    private float mPeakHeight2;
    /**
     * The height scale of third peak.
     **/
    private float mPeakHeight3;
    /**
     * The height scale of fourth peak.
     **/
    private float mPeakHeight4;
    /**
     * The angle A left center flag.
     */
    private double mA;
    /**
     * The angle B right center flag.
     */
    private double mB;

    /**
     * The height of big bolt.
     */
    private float mCenterBigBoltHeight;
    /**
     * The width of big bolt.
     */
    private float mCenterBigBoltWidth;
    /**
     * The length of line left big bolt.
     */
    private float mCenterBigBoltLeftLineLength;
    /**
     * The length of line right big bolt.
     */
    private float mCenterBigBoltRightLineLength;


    /**
     * Measure the param.
     *
     * @param width  the width of view
     * @param height the height of view
     */
    public void measure(Context ctx, int width, int height) {
        if (0 >= width || 0 >= height) {
            return;
        }
        mViewWidth = width;
        mViewHeight = height;

        mA = Math.toRadians(DEFAULT_ANGLE_A_LEFT_CENTER_FLAG);
        mB = Math.toRadians(DEFAULT_ANGLE_B_RIGHT_CENTER_FLAG);

        mGrainWidth = dip2px(ctx, DEFAULT_GRAIN_WIDTH);

        mEndMarginLineLength = DEFAULT_END_MARGIN_LINE_LENGTH_RATIO_FOR_WIDTH * mViewWidth;
        mPeakBigRadius = DEFAULT_PEAK_BIG_RADIUS_RATIO_FOR_WIDTH * mViewWidth;
        mGrainGap = DEFAULT_GRAIN_GAP_RATIO_FOR_RADIUS * mPeakBigRadius;
        mPeakBigYOffset = 3 * mGrainGap + DEFAULT_CENTER_GAP_RATIO_FOR_RADIUS * mPeakBigRadius;

        mPeakHeight1 = DEFAULT_PEAK1_HEIGHT_RATIO_FOR_HEIGHT * mViewHeight;
        mPeakHeight2 = DEFAULT_PEAK2_HEIGHT_RATIO_FOR_HEIGHT * mViewHeight;
        mPeakHeight3 = DEFAULT_PEAK3_HEIGHT_RATIO_FOR_HEIGHT * mViewHeight;
        mPeakHeight4 = DEFAULT_PEAK4_HEIGHT_RATIO_FOR_HEIGHT * mViewHeight;

        mCenterBigBoltHeight = DEFAULT_CENTER_BOLT_HEIGHT_SCALE_FOR_HEIGHT * mViewHeight;
        mCenterBigBoltWidth = (float) (mCenterBigBoltHeight / Math.tan(mA) - mCenterBigBoltHeight / Math.tan(mB));

        float centerX = mViewWidth - 2 * mEndMarginLineLength - 8 * mPeakBigRadius - 8 * mPeakBigRadius;
        mCenterBigBoltLeftLineLength = (centerX - mCenterBigBoltWidth) / (1 + DEFAULT_RATIO_LEFT_LINE_AND_RIGHT_LINE_OF_CENTER_FLAG) * DEFAULT_RATIO_LEFT_LINE_AND_RIGHT_LINE_OF_CENTER_FLAG;
        mCenterBigBoltRightLineLength = centerX - mCenterBigBoltWidth - mCenterBigBoltLeftLineLength;
    }


    /**
     * Get grain path1 from left.
     *
     * @param inverted true bottom, false top
     * @return Path
     */
    public Path getGrainPath1FromLeft(boolean inverted) {
        float gap = 0;
        float topRadius = mPeakBigRadius - gap;
        float bottomRadius = mPeakBigRadius + gap;
        return getGrainPathFromLeft(gap, topRadius, bottomRadius, inverted);
    }

    /**
     * Get grain path1 from right.
     *
     * @param inverted true bottom, false top
     * @return Path
     */
    public Path getGrainPath1FromRight(boolean inverted) {
        float gap = mGrainWidth;
        float topRadius = mPeakBigRadius - gap;
        float bottomRadius = mPeakBigRadius + gap;
        return getGrainPathFromRight(gap, topRadius, bottomRadius, inverted);
    }

    /**
     * Get grain path2 from left.
     *
     * @param inverted true bottom, false top
     * @return Path
     */
    public Path getGrainPath2FromLeft(boolean inverted) {
        float gap = mGrainGap;
        float topRadius = mPeakBigRadius - gap;
        float bottomRadius = mPeakBigRadius + gap;
        return getGrainPathFromLeft(gap, topRadius, bottomRadius, inverted);
    }

    /**
     * Get grain path2 from right.
     *
     * @param inverted true bottom, false top
     * @return Path
     */
    public Path getGrainPath2FromRight(boolean inverted) {
        float gap = mGrainGap + mGrainWidth;
        float topRadius = mPeakBigRadius - gap;
        float bottomRadius = mPeakBigRadius + gap;
        return getGrainPathFromRight(gap, topRadius, bottomRadius, inverted);
    }

    /**
     * Get grain path3 from left.
     *
     * @param inverted true bottom, false top
     * @return Path
     */
    public Path getGrainPath3FromLeft(boolean inverted) {
        float gap = 2 * mGrainGap;
        float topRadius = mPeakBigRadius - gap;
        float bottomRadius = mPeakBigRadius + gap;
        return getGrainPathFromLeft(gap, topRadius, bottomRadius, inverted);
    }

    /**
     * Get grain path3 from right.
     *
     * @param inverted true bottom, false top
     * @return Path
     */
    public Path getGrainPath3FromRight(boolean inverted) {
        float gap = 2 * mGrainGap + mGrainWidth;
        float topRadius = mPeakBigRadius - gap;
        float bottomRadius = mPeakBigRadius + gap;
        return getGrainPathFromRight(gap, topRadius, bottomRadius, inverted);
    }

    /**
     * Get grain top path4 from left.
     *
     * @return Path
     */
    public Path getGrainTop4Path() {
        float gap = 3 * mGrainGap;
        float topRadius = mPeakBigRadius - gap;
        float bottomRadius = mPeakBigRadius + gap;
        return getGrainPathFromLeft(gap, topRadius, bottomRadius, false);
    }

    /**
     * Get grain bottom path4 from right.
     *
     * @return Path
     */
    public Path getGrainBottom4Path() {
        float gap = 3 * mGrainGap;
        float topRadius = mPeakBigRadius - gap;
        float bottomRadius = mPeakBigRadius + gap;
        return getGrainPathFromRight(gap, topRadius, bottomRadius, true);
    }


    /**
     * Get grain path from left.
     *
     * @param gap          the peak gap
     * @param topRadius    the radius of top peak
     * @param bottomRadius the radius of bottom peak
     * @param inverted     true bottom, false top
     * @return Path
     */
    public Path getGrainPathFromLeft(float gap, float topRadius, float bottomRadius, boolean inverted) {

        float yOffset = -mPeakBigYOffset + gap;
        float centerBoltWidth = (float) (mCenterBigBoltWidth - gap / Math.sin(mA) + gap * Math.tan(Math.toRadians(90 - DEFAULT_ANGLE_A_LEFT_CENTER_FLAG)) - gap / Math.sin(mB) - gap / Math.tan(mB));
        float centerBoltHeight = (float) (centerBoltWidth * mCenterBigBoltHeight / (mCenterBigBoltHeight / Math.tan(mA) - mCenterBigBoltHeight / Math.tan(mB)));
        float centerBoltTipOffset = (float) (centerBoltHeight / Math.tan(mB));
        float centerBoltLeftLineLength = (float) (mCenterBigBoltLeftLineLength + gap / Math.sin(mA) - gap * Math.tan(Math.toRadians(90 - DEFAULT_ANGLE_A_LEFT_CENTER_FLAG)));
        float centerBoltRightLineLength = (float) (mCenterBigBoltRightLineLength + gap / Math.sin(mB) + gap / Math.tan(mB));

        int invert = inverted ? -1 : 1;

        List<GrainPathPoint> list = new ArrayList<>();
        float tmp = 0;
        list.add(GrainPathPoint.newPoint(tmp, 0));
        list.add(GrainPathPoint.newPoint(tmp, invert * yOffset));

        // end margin line
        tmp = mEndMarginLineLength;
        list.add(GrainPathPoint.newPoint(tmp, invert * yOffset));

        // arc left first peak
        list.add(GrainPathPoint.newArc(mEndMarginLineLength - bottomRadius,
                inverted ? -yOffset : (yOffset - 2 * bottomRadius),
                mEndMarginLineLength + bottomRadius,
                inverted ? -(yOffset - 2 * bottomRadius) : yOffset,
                invert * -270f, invert * -90f));

        // first peak
        tmp = tmp + bottomRadius;
        list.add(GrainPathPoint.newPoint(tmp, invert * (yOffset - mPeakHeight1 + topRadius)));
        list.add(GrainPathPoint.newArc(tmp,
                inverted ? -(yOffset - mPeakHeight1 + 2 * topRadius) : (yOffset - mPeakHeight1),
                tmp + 2 * topRadius,
                inverted ? -(yOffset - mPeakHeight1) : (yOffset - mPeakHeight1 + 2 * topRadius),
                invert * 180f, invert * 180f));
        list.add(GrainPathPoint.newPoint(tmp + 2 * topRadius, invert * (yOffset - bottomRadius)));

        // arc between first peak and second peak
        tmp = tmp + 2 * topRadius;
        list.add(GrainPathPoint.newArc(tmp,
                inverted ? -yOffset : (yOffset - 2 * bottomRadius),
                tmp + 2 * bottomRadius,
                inverted ? -(yOffset - 2 * bottomRadius) : yOffset,
                invert * -180f, invert * -180f));

        // second peak
        tmp = tmp + 2 * bottomRadius;
        list.add(GrainPathPoint.newPoint(tmp, invert * (yOffset - mPeakHeight2 + topRadius)));
        list.add(GrainPathPoint.newArc(tmp,
                inverted ? -(yOffset - mPeakHeight2 + 2 * topRadius) : (yOffset - mPeakHeight2),
                tmp + 2 * topRadius,
                inverted ? -(yOffset - mPeakHeight2) : (yOffset - mPeakHeight2 + 2 * topRadius),
                invert * 180f, invert * 180f));
        list.add(GrainPathPoint.newPoint(tmp + 2 * topRadius, invert * (yOffset - bottomRadius)));

        // arc right second peak
        tmp = tmp + 2 * topRadius;
        list.add(GrainPathPoint.newArc(tmp,
                inverted ? -yOffset : (yOffset - 2 * bottomRadius),
                tmp + 2 * bottomRadius,
                inverted ? -(yOffset - 2 * bottomRadius) : yOffset,
                invert * -180f, invert * -90f));

        // center flag
        tmp = tmp + bottomRadius;
        list.add(GrainPathPoint.newPoint(inverted ? tmp + centerBoltRightLineLength : tmp + centerBoltLeftLineLength, invert * yOffset));
        list.add(GrainPathPoint.newPoint(inverted ? tmp + centerBoltRightLineLength - centerBoltTipOffset : tmp + centerBoltLeftLineLength + centerBoltWidth + centerBoltTipOffset, invert * (yOffset - centerBoltHeight)));
        list.add(GrainPathPoint.newPoint(inverted ? tmp + centerBoltRightLineLength + centerBoltWidth : tmp + centerBoltLeftLineLength + centerBoltWidth, invert * yOffset));
        list.add(GrainPathPoint.newPoint(tmp + centerBoltLeftLineLength + centerBoltWidth + centerBoltRightLineLength, invert * yOffset));

        // arc left third peak
        tmp = tmp + centerBoltLeftLineLength + centerBoltWidth + centerBoltRightLineLength;
        list.add(GrainPathPoint.newArc(tmp - bottomRadius,
                inverted ? -yOffset : (yOffset - 2 * bottomRadius),
                tmp + bottomRadius,
                inverted ? -(yOffset - 2 * bottomRadius) : yOffset,
                invert * -270f, invert * -90f));

        // third peak
        tmp = tmp + bottomRadius;
        list.add(GrainPathPoint.newPoint(tmp, invert * (yOffset - mPeakHeight3 + topRadius)));
        list.add(GrainPathPoint.newArc(tmp,
                inverted ? -(yOffset - mPeakHeight3 + 2 * topRadius) : (yOffset - mPeakHeight3),
                tmp + 2 * topRadius,
                inverted ? -(yOffset - mPeakHeight3) : (yOffset - mPeakHeight3 + 2 * topRadius),
                invert * 180f, invert * 180f));
        list.add(GrainPathPoint.newPoint(tmp + 2 * topRadius, invert * (yOffset - bottomRadius)));

        // arc between third peak and fourth peak
        tmp = tmp + 2 * topRadius;
        list.add(GrainPathPoint.newArc(tmp,
                inverted ? -yOffset : (yOffset - 2 * bottomRadius),
                tmp + 2 * bottomRadius,
                inverted ? -(yOffset - 2 * bottomRadius) : yOffset,
                invert * -180f, invert * -180f));

        // fourth peak
        tmp = tmp + 2 * bottomRadius;
        list.add(GrainPathPoint.newPoint(tmp, invert * (yOffset - mPeakHeight4 + topRadius)));
        list.add(GrainPathPoint.newArc(tmp,
                inverted ? -(yOffset - mPeakHeight4 + 2 * topRadius) : (yOffset - mPeakHeight4),
                tmp + 2 * topRadius,
                inverted ? -(yOffset - mPeakHeight4) : (yOffset - mPeakHeight4 + 2 * topRadius),
                invert * -180f, invert * 180f));
        list.add(GrainPathPoint.newPoint(tmp + 2 * topRadius, invert * (yOffset - bottomRadius)));

        // arc right fourth peak
        tmp = tmp + 2 * topRadius;
        list.add(GrainPathPoint.newArc(tmp,
                inverted ? -yOffset : (yOffset - 2 * bottomRadius),
                tmp + 2 * bottomRadius,
                inverted ? -(yOffset - 2 * bottomRadius) : yOffset,
                invert * -180f, invert * -90f));

        // end margin line
        tmp = tmp + bottomRadius;
        list.add(GrainPathPoint.newPoint(tmp + mEndMarginLineLength, invert * yOffset));
        list.add(GrainPathPoint.newPoint(tmp + mEndMarginLineLength, 0));

        return GrainPathPoint.getGrainPath(list);
    }


    /**
     * Get grain path from left.
     *
     * @param gap          the peak gap
     * @param topRadius    the radius of top peak
     * @param bottomRadius the radius of bottom peak
     * @param inverted     true bottom, false top
     * @return Path
     */
    public Path getGrainPathFromRight(float gap, float topRadius, float bottomRadius, boolean inverted) {

        float yOffset = -mPeakBigYOffset + gap;
        float centerBoltWidth = (float) (mCenterBigBoltWidth - gap / Math.sin(mA) + gap * Math.tan(Math.toRadians(90 - DEFAULT_ANGLE_A_LEFT_CENTER_FLAG)) - gap / Math.sin(mB) - gap / Math.tan(mB));
        float centerBoltHeight = (float) (centerBoltWidth * mCenterBigBoltHeight / (mCenterBigBoltHeight / Math.tan(mA) - mCenterBigBoltHeight / Math.tan(mB)));
        float centerBoltTipOffset = (float) (centerBoltHeight / Math.tan(mB));
        float centerBoltLeftLineLength = (float) (mCenterBigBoltLeftLineLength + gap / Math.sin(mA) - gap * Math.tan(Math.toRadians(90 - DEFAULT_ANGLE_A_LEFT_CENTER_FLAG)));
        float centerBoltRightLineLength = (float) (mCenterBigBoltRightLineLength + gap / Math.sin(mB) + gap / Math.tan(mB));

        int invert = inverted ? -1 : 1;

        List<GrainPathPoint> list = new ArrayList<>();

        float tmp = mViewWidth;
        list.add(GrainPathPoint.newPoint(tmp, 0));
        list.add(GrainPathPoint.newPoint(tmp, invert * yOffset));

        // end margin line
        tmp = tmp - mEndMarginLineLength;
        list.add(GrainPathPoint.newPoint(tmp, invert * yOffset));

        // arc right fourth peak
        list.add(GrainPathPoint.newArc(tmp - bottomRadius,
                inverted ? -yOffset : (yOffset - 2 * bottomRadius),
                tmp + bottomRadius,
                inverted ? -(yOffset - 2 * bottomRadius) : yOffset,
                invert * -270f, invert * 90f));

        // fourth peak
        tmp = tmp - bottomRadius;
        list.add(GrainPathPoint.newPoint(tmp, invert * (yOffset - mPeakHeight4 + topRadius)));
        list.add(GrainPathPoint.newArc(tmp - 2 * topRadius,
                inverted ? -(yOffset - mPeakHeight4 + 2 * topRadius) : (yOffset - mPeakHeight4),
                tmp,
                inverted ? -(yOffset - mPeakHeight4) : (yOffset - mPeakHeight4 + 2 * topRadius),
                0f, invert * -180f));
        list.add(GrainPathPoint.newPoint(tmp - 2 * topRadius, invert * (yOffset - bottomRadius)));

        // arc between third peak and fourth peak
        tmp = tmp - 2 * topRadius;
        list.add(GrainPathPoint.newArc(tmp - 2 * bottomRadius,
                inverted ? -yOffset : (yOffset - 2 * bottomRadius),
                tmp,
                inverted ? -(yOffset - 2 * bottomRadius) : yOffset,
                0f, invert * 180f));

        // third peak
        tmp = tmp - 2 * bottomRadius;
        list.add(GrainPathPoint.newPoint(tmp, invert * (yOffset - mPeakHeight3 + topRadius)));
        list.add(GrainPathPoint.newArc(tmp - 2 * topRadius,
                inverted ? -(yOffset - mPeakHeight3 + 2 * topRadius) : (yOffset - mPeakHeight3),
                tmp,
                inverted ? -(yOffset - mPeakHeight3) : (yOffset - mPeakHeight3 + 2 * topRadius),
                0f, invert * -180f));
        list.add(GrainPathPoint.newPoint(tmp - 2 * topRadius, invert * (yOffset - bottomRadius)));

        // arc left third peak
        tmp = tmp - 2 * topRadius;
        list.add(GrainPathPoint.newArc(tmp - 2 * bottomRadius,
                inverted ? -yOffset : (yOffset - 2 * bottomRadius),
                tmp,
                inverted ? -(yOffset - 2 * bottomRadius) : yOffset,
                0f, invert * 90f));

        // center flag
        tmp = tmp - bottomRadius;
        list.add(GrainPathPoint.newPoint(inverted ? tmp - centerBoltLeftLineLength : tmp - centerBoltRightLineLength, invert * yOffset));
        list.add(GrainPathPoint.newPoint(inverted ? tmp - centerBoltLeftLineLength - centerBoltWidth - centerBoltTipOffset : tmp - centerBoltRightLineLength + centerBoltTipOffset, invert * (yOffset - centerBoltHeight)));
        list.add(GrainPathPoint.newPoint(inverted ? tmp - centerBoltLeftLineLength - centerBoltWidth : tmp - centerBoltRightLineLength - centerBoltWidth, invert * yOffset));
        list.add(GrainPathPoint.newPoint(tmp - centerBoltRightLineLength - centerBoltWidth - centerBoltLeftLineLength, invert * yOffset));

        // arc right second peak
        tmp = tmp - centerBoltRightLineLength - centerBoltWidth - centerBoltLeftLineLength;
        list.add(GrainPathPoint.newArc(tmp - bottomRadius,
                inverted ? -yOffset : (yOffset - 2 * bottomRadius),
                tmp + bottomRadius,
                inverted ? -(yOffset - 2 * bottomRadius) : yOffset,
                invert * -270f, invert * 90f));

        // second peak
        tmp = tmp - bottomRadius;
        list.add(GrainPathPoint.newPoint(tmp, invert * (yOffset - mPeakHeight2 + topRadius)));
        list.add(GrainPathPoint.newArc(tmp - 2 * topRadius,
                inverted ? -(yOffset - mPeakHeight2 + 2 * topRadius) : (yOffset - mPeakHeight2),
                tmp,
                inverted ? -(yOffset - mPeakHeight2) : (yOffset - mPeakHeight2 + 2 * topRadius),
                0f, invert * -180f));
        list.add(GrainPathPoint.newPoint(tmp - 2 * topRadius, invert * (yOffset - bottomRadius)));

        // arc between first peak and second peak
        tmp = tmp - 2 * topRadius;
        list.add(GrainPathPoint.newArc(tmp - 2 * bottomRadius,
                inverted ? -yOffset : (yOffset - 2 * bottomRadius),
                tmp,
                inverted ? -(yOffset - 2 * bottomRadius) : yOffset,
                0f, invert * 180f));

        // first peak
        tmp = tmp - 2 * bottomRadius;
        list.add(GrainPathPoint.newPoint(tmp, invert * (yOffset - mPeakHeight1 + topRadius)));
        list.add(GrainPathPoint.newArc(tmp - 2 * topRadius,
                inverted ? -(yOffset - mPeakHeight1 + 2 * topRadius) : (yOffset - mPeakHeight1),
                tmp,
                inverted ? -(yOffset - mPeakHeight1) : (yOffset - mPeakHeight1 + 2 * topRadius),
                0f, invert * -180f));
        list.add(GrainPathPoint.newPoint(tmp - 2 * topRadius, invert * (yOffset - bottomRadius)));

        // arc left first peak
        tmp = tmp - 2 * topRadius;
        list.add(GrainPathPoint.newArc(tmp - 2 * bottomRadius,
                inverted ? -yOffset : (yOffset - 2 * bottomRadius),
                tmp,
                inverted ? -(yOffset - 2 * bottomRadius) : yOffset,
                0f, invert * 90f));

        // end margin line
        tmp = tmp - bottomRadius;
        list.add(GrainPathPoint.newPoint(tmp - mEndMarginLineLength, invert * yOffset));
        list.add(GrainPathPoint.newPoint(tmp - mEndMarginLineLength, 0));

        return GrainPathPoint.getGrainPath(list);
    }

    /**
     * Change dp to px.
     *
     * @param ctx     the context
     * @param dpValue the dp value
     * @return the px value
     */
    public int dip2px(Context ctx, float dpValue) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
