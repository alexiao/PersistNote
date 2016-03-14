package com.dv.persistnote.business;

import android.os.Message;
import android.util.SparseArray;
import android.view.KeyEvent;

import com.dv.persistnote.framework.AbstractScreen;
import com.dv.persistnote.framework.AbstractTabContentView;
import com.dv.persistnote.framework.core.MsgDef;
import com.dv.persistnote.framework.core.AbstractController;
import com.dv.persistnote.framework.core.BaseEnv;

/**
 * Created by Hang on 2016/3/13.
 * 最底部的窗口，用于承载各个Tab
 */
public class RootController extends AbstractController{

    private RootScreen mRootScreen;
    private SparseArray<AbstractTabContentView> mTabViews = new SparseArray<AbstractTabContentView>();

    @Override
    public void handleMessage(Message msg) {
        if(msg.what == MsgDef.MSG_INIT_ROOTSCREEN) {
            mRootScreen = new RootScreen(mContext, this);
            mWindowMgr.createWindowStack(mRootScreen);

            //未登录时显示登陆窗口
            AbstractScreen screen = new WelcomeScreen(mContext,this);
            mWindowMgr.pushScreen(screen, false);
        }
    }

    public RootController(BaseEnv baseEnv) {
        super(baseEnv);
    }

    @Override
    public void onWindowStateChange(AbstractScreen target, byte stateFlag) {

    }

    @Override
    public boolean onWindowKeyEvent(AbstractScreen target, int keyCode, KeyEvent event) {
        return false;
    }
}
