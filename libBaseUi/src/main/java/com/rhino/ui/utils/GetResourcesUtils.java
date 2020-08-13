package com.rhino.ui.utils;

import android.content.Context;

/**
 * @author rhino
 * @since Create on 2019/12/25.
 */
public class GetResourcesUtils {

    /**
     * 获取资源文件的id
     */
    public static int getId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "id", context.getPackageName());
    }

    /**
     * 获取资源文件中string的id
     */
    public static int getStringId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "string", context.getPackageName());
    }

    /**
     * 获取资源文件mipmap的id
     */
    public static int getMipmapId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "mipmap", context.getPackageName());
    }

    /**
     * 获取资源文件layout的id
     */
    public static int getLayoutId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "layout", context.getPackageName());
    }


    /**
     * 获取资源文件style的id
     */
    public static int getStyleId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "style", context.getPackageName());
    }

    /**
     * 获取资源文件color的id
     */
    public static int getColorId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "color", context.getPackageName());
    }

    /**
     * 获取资源文件dimen的id
     */
    public static int getDimenId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "dimen", context.getPackageName());
    }

    /**
     * 获取资源文件ainm的id
     */
    public static int getAnimId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "anim", context.getPackageName());
    }

    /**
     * 获取资源文件menu的id
     */
    public static int getMenuId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "menu", context.getPackageName());
    }

}