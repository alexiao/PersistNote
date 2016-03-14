package com.dv.persistnote.business.account;

import android.os.Message;
import android.view.KeyEvent;

import com.dv.persistnote.framework.AbstractScreen;
import com.dv.persistnote.framework.core.AbstractController;
import com.dv.persistnote.framework.core.BaseEnv;

/**
 * Created by Hang on 2016/3/14.
 */
public class AccountController extends AbstractController{



    public AccountController(BaseEnv baseEnv) {
        super(baseEnv);
    }

    @Override
    public void handleMessage(Message msg) {

    }

    @Override
    public void onWindowStateChange(AbstractScreen target, byte stateFlag) {

    }

    @Override
    public boolean onWindowKeyEvent(AbstractScreen target, int keyCode, KeyEvent event) {
        return false;
    }
}
