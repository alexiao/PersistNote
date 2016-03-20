package com.dv.persistnote.business.account;

import android.os.Message;
import android.view.KeyEvent;

import com.dv.persistnote.framework.ActionId;
import com.dv.persistnote.business.WelcomeScreen;
import com.dv.persistnote.framework.ui.AbstractScreen;
import com.dv.persistnote.framework.core.AbstractController;
import com.dv.persistnote.framework.core.BaseEnv;
import com.dv.persistnote.framework.core.MsgDef;

/**
 * Created by Hang on 2016/3/14.
 */
public class AccountController extends AbstractController{

    private WelcomeScreen mWelcomeScreen;

    public AccountController(BaseEnv baseEnv) {
        super(baseEnv);
    }

    @Override
    public void handleMessage(Message msg) {

        if(msg.what == MsgDef.MSG_SHOW_WELCOME_SCREEN){
            //未登录时显示登陆窗口
            mWelcomeScreen = new WelcomeScreen(mContext,this);
            mWindowMgr.pushScreen(mWelcomeScreen, false);
        }
    }



    @Override
    public void onWindowStateChange(AbstractScreen target, byte stateFlag) {

    }

    @Override
    public boolean onWindowKeyEvent(AbstractScreen target, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean handleAction(int actionId, Object arg, Object result) {
        switch (actionId) {
            case ActionId.OnLoginClick:
                mWindowMgr.popScreen(true);
                break;
        }
        return false;
    }

}
