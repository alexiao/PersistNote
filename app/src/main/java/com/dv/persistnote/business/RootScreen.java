package com.dv.persistnote.business;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dv.persistnote.framework.AbstractScreen;
import com.dv.persistnote.framework.UICallBacks;

/**
 * Created by Hang on 2016/3/13.
*/
public class RootScreen extends AbstractScreen {

    public RootScreen(Context context, UICallBacks callBacks) {
        super(context, callBacks);
        init();
        setBackgroundColor(Color.parseColor("#10ffffff"));
    }

    private void init() {
        TextView center = new TextView(getContext());
        center.setText("坚持笔记首页");

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        addView(center, lp);
    }


}
