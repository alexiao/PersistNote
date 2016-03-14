package com.dv.persistnote;

import android.app.Activity;
import android.os.Bundle;

import com.dv.persistnote.base.ContextManager;
import com.dv.persistnote.framework.core.ControllerFactory;
import com.dv.persistnote.framework.core.ControllerRegister;
import com.dv.persistnote.framework.core.MsgDef;
import com.dv.persistnote.framework.core.BaseEnv;
import com.dv.persistnote.framework.core.ControllerCenter;


public class PersistNoteActivity extends Activity {

    private BaseEnv mEnv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initBaseEnv();
        initController();

    }

    /*
    初始化基础框架，包括消息通讯，UI基础框架
     */
    private void initBaseEnv() {
        mEnv = new BaseEnv(this);

        ContextManager.initialize(this);
        ContextManager.setApplicationContext(this.getApplicationContext());
    }

    /*
    初始化Controller框架，包括工厂和消息注册
     */
    private void initController() {
        ControllerCenter center = new ControllerCenter();
        ControllerRegister register = new ControllerRegister(center);
        mEnv.getMsgDispatcher().setControllerCenter(center);
        center.setControllerFactory(new ControllerFactory());
        center.setEnvironment(mEnv);
        register.registerControllers();

        mEnv.getMsgDispatcher().sendMessage(MsgDef.MSG_INIT_ROOTSCREEN);
    }

}
