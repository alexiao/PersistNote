package com.dv.persistnote.framework.core;

import android.content.Context;

import com.dv.persistnote.framework.ScreenManager;


public class BaseEnv {
    protected Context mContext;
    protected MsgDispatcher mDispatcher;
    protected ScreenManager mWindowMgr;

    public BaseEnv (Context context) {
        mContext = context;
        mDispatcher = new MsgDispatcher();
        mWindowMgr = new ScreenManager(context);
    }
    
    public void setMsgDispatcher(MsgDispatcher dispatcher) {
        mDispatcher = dispatcher;
    }
    
    public MsgDispatcher getMsgDispatcher() {
        return mDispatcher;
    }
    
    protected void setContext(Context context) {
        mContext = context;
    }
    
    public Context getContext() {
        return mContext;
    }

    protected void setWindowManager(ScreenManager windowMgr) {
        mWindowMgr = windowMgr;
    }

    public ScreenManager getWindowManager() {
        return mWindowMgr;
    }
    
    public static void copy(BaseEnv src, BaseEnv dest) {
        dest.setContext(src.getContext());
        dest.setMsgDispatcher(src.getMsgDispatcher());
    }
}
