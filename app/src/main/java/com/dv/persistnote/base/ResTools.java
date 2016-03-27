package com.dv.persistnote.base;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * Created by Hang on 2016/3/21.
 */
public class ResTools {

    public static Drawable getDrawable(int drawableId) {
        return ContextManager.getResources().getDrawable(drawableId);
    }

    public static int getDimenInt(int id) {
        return (int) ContextManager.getResources().getDimension(id);
    }

    public static int getColor(int id) {
        return ContextManager.getResources().getColor(id);
    }

    public static String getString(int id) {
        return ContextManager.getResources().getString(id);
    }
}
