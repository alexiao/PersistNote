package com.dv.persistnote.framework;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class HandlEx extends Handler {
    private String mName;
    
    public HandlEx(String name) {
        setName(name);
    }
    
    public HandlEx(String name, Callback callback) {
        super(callback);
        setName(name);
    }
    
    public HandlEx(String name, Looper looper) {
        super(looper);
        setName(name);
    }
    
    public HandlEx(String name, Looper looper, Callback callback) {
        super(looper, callback);
        setName(name);
    }

    public void setName(String name) {
        this.mName = name;
    }
    
    public String getName() {
        return mName;
    }

    @Override
    public String toString() {
        return "HandlerEx (" + mName + ") {}";
    }
    
	@Override
	public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
		boolean sent = super.sendMessageAtTime(msg, uptimeMillis);
//		CrashMsgCacheReporter.getInstance().onSend(msg);
		return sent;
	}

	@Override
	public void dispatchMessage(Message msg) {
//		CrashMsgCacheReporter.getInstance().onHandle(msg);
		super.dispatchMessage(msg);
	}
}
