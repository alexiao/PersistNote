package com.dv.persistnote.framework.ui;

import android.view.KeyEvent;

import com.dv.persistnote.framework.core.MsgDispatcher.IMessageHandler;


public interface UICallBacks extends IMessageHandler {
	public void onWindowExitEvent(boolean withAnimation);
	public void onWindowStateChange(AbstractScreen target, byte stateFlag);
	public boolean onWindowKeyEvent(AbstractScreen target, int keyCode, KeyEvent event);
	public boolean handleAction(int actionId, Object arg, Object result);
}



