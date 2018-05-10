package com.rhino.ui.utils;


/**
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public class TempUtils {

    public static int centi2fah(int centi) {
        return (int)(centi * 1.8 + 32.0);
    }

    public static int fah2centi(int fah) {
        return (int) ((fah - 32.0) / 1.8);
    }

    public static float centi2fah(float centi) {
        return (float)(centi * 1.8 + 32.0);
    }

    public static float fah2centi(float fah) {
        return (float) ((fah - 32.0) / 1.8);
    }
}
