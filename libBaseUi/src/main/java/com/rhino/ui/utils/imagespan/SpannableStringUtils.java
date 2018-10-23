package com.rhino.ui.utils.imagespan;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;

/**
 * <p>Some commonly used interface about SpannableString</p>
 *
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public class SpannableStringUtils {

    /**
     * Build the ImageSpan
     *
     * @param iconSize    the icon size
     * @param iconDrawable the icon drawable
     * @return the ImageSpan
     */
    @NonNull
    public static VerticalImageSpan buildImageSpan(int iconSize,
            @NonNull Drawable iconDrawable) {
        iconDrawable.setBounds(0, 0, iconSize, iconSize);
        return new VerticalImageSpan(iconDrawable, ImageSpan.ALIGN_BASELINE);
    }

    /**
     * Check whether set span
     *
     * @param spannableString the SpannableString
     * @param imageSpan       the ImageSpan
     * @param imageString     the string of ImageSpan
     * @return True set
     */
    public static boolean checkSetSpan(@NonNull SpannableString spannableString,
            VerticalImageSpan imageSpan, String imageString) {
        if (null == imageSpan || TextUtils.isEmpty(imageString)) {
            return false;
        }
        int start = spannableString.toString().indexOf(imageString);
        if (0 > start) {
            return false;
        }
        spannableString.setSpan(imageSpan, start, start + imageString.length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return true;
    }

}
