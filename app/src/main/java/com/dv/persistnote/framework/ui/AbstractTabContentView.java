package com.dv.persistnote.framework.ui;

import android.content.Context;
import android.widget.FrameLayout;

public class AbstractTabContentView extends FrameLayout {
    public static final byte STATE_ON_SHOW = 001;
    public static final byte STATE_ON_HIDE = 002;

    private byte mContentViewState = STATE_ON_HIDE;

    public AbstractTabContentView(Context context) {
        super(context);
    }

    protected  void onThemeChange() {

    }

    public void onWindowStateChange(byte stateFlag) {
        mContentViewState = stateFlag;
    }

    public byte getWindowState() {
        return mContentViewState;
    }

    public boolean onClickCheckedBottomItem() {

        return false;
    }
}
