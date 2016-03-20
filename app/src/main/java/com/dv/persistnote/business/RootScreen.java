package com.dv.persistnote.business;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dv.persistnote.R;
import com.dv.persistnote.base.ResTools;
import com.dv.persistnote.framework.ActionId;
import com.dv.persistnote.framework.DefaultScreen;
import com.dv.persistnote.framework.ui.AbstractScreen;
import com.dv.persistnote.framework.ui.UICallBacks;

/**
 * Created by Hang on 2016/3/13.
*/
public class RootScreen extends DefaultScreen {

   private TextView center;

    public RootScreen(Context context, UICallBacks callBacks) {
        super(context, callBacks);
        init();
        setBackgroundColor(ResTools.getColor(R.color.default_white));
        setTitle(ResTools.getString(R.string.app_name));
    }

    private void init() {
        FrameLayout content = new FrameLayout(getContext());
        setContent(content);

        center = new TextView(getContext());
        center.setText("打卡按钮");

        center.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallBacks.handleAction(ActionId.OnCheckInClick, null, null);
            }
        });

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        content.addView(center, lp);
    }


    public void setCheckInText(String checkInText) {
        center.setText(checkInText);
    }
}
