package com.dv.persistnote.framework;

import android.graphics.Typeface;

import com.dv.persistnote.base.ContextManager;

import java.lang.reflect.Type;

/**
 * Created by Hang on 2016/3/14.
 */
public class FontManager {

    private static FontManager sInstance;

    private Typeface mCache;

    public static FontManager getInstance() {
        if (sInstance == null) {
            sInstance = new FontManager();
        }
        return sInstance;
    }

    public Typeface getDefaultTypeface() {
        if(mCache == null) {
            mCache = Typeface.createFromAsset(ContextManager.getContext().getAssets(), "LTCXH.TTF");
        }
        return mCache;
    }

}
