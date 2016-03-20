package com.dv.persistnote.business.timeline;

import android.os.Message;
import android.view.KeyEvent;

import com.dv.persistnote.framework.core.AbstractController;
import com.dv.persistnote.framework.core.BaseEnv;
import com.dv.persistnote.framework.ui.AbstractScreen;

/**
 * Created by Hang on 2016/3/21.
 */
public class TimelineController extends AbstractController  {

    public TimelineController(BaseEnv baseEnv) {
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

    @Override
    public boolean handleAction(int actionId, Object arg, Object result) {
        return false;
    }
}
