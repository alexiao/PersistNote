package com.dv.persistnote.framework;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.dv.persistnote.R;
import com.dv.persistnote.base.ResTools;
import com.dv.persistnote.framework.ui.AbstractScreen;
import com.dv.persistnote.framework.ui.UICallBacks;

/**
 * Created by Hang on 2016/3/21.
 */
public class DefaultScreen extends AbstractScreen {

    private final static int ID_TITLE_BAR = 101;

    protected DefaultTitleBar mTitleBar;

    public DefaultScreen(Context context, UICallBacks callBacks) {
        super(context, callBacks);
        init();
    }

    private void init() {
        mTitleBar = new DefaultTitleBar(getContext());
        mTitleBar.setId(ID_TITLE_BAR);
        LayoutParams titleLp = new LayoutParams(LayoutParams.MATCH_PARENT, ResTools.getDimenInt(R.dimen.title_bar_height));
        addView(mTitleBar, titleLp);
    }

    public void setTitle(String title) {
        mTitleBar.setTitle(title);
    }

    public void setContent(View v) {
        LayoutParams contentLp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        contentLp.addRule(BELOW, ID_TITLE_BAR);
        addView(v, contentLp);
    }


}
