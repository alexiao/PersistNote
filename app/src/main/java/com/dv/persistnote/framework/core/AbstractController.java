package com.dv.persistnote.framework.core;

import android.content.Context;
import android.os.Message;

import com.dv.persistnote.framework.ui.ScreenManager;
import com.dv.persistnote.framework.ui.UICallBacks;

import java.util.ArrayList;

public abstract class AbstractController implements UICallBacks, INotify {

	@Override
	public abstract void handleMessage(Message msg);
	
	@Override
	public Object handleMessageSync(Message msg) {
		return null;
	}

	@Override
	public ArrayList<Integer> messages() {
		return mMessages;
	}

	public static final String TAG = "AbstractController";
	
	private ArrayList<Integer> mMessages;

	protected Context mContext;
	protected ScreenManager mWindowMgr;
	protected MsgDispatcher mDispatcher;
	protected BaseEnv mEnvironment = null;

	/**
	 * AbstractController需要通过{@link IControllerFactory}创建，在那里会传入构造需要的BaseEnv。</br>
	 * 无参构造不应该被使用，不要把它变成public。
	 */
	private AbstractController() {}
	
    public AbstractController(BaseEnv baseEnv) {
        if (baseEnv != null) {
            mEnvironment = baseEnv;
            mContext = baseEnv.getContext();
            mWindowMgr = baseEnv.getWindowManager();
            mDispatcher = baseEnv.getMsgDispatcher();
        }
    }
	
    public void setEnvironment(BaseEnv baseEnv) {
	    if (baseEnv == null) {
	        return;
	    }
	    
	    mEnvironment = baseEnv;
	    mContext = baseEnv.getContext();
        mWindowMgr = baseEnv.getWindowManager();
        mDispatcher = baseEnv.getMsgDispatcher();
	}
	
	
	public BaseEnv getEnvironment() {
	    return mEnvironment;
	}
	
	protected void registerMessage(int message) {
	    if (mMessages == null) {
	        mMessages = new ArrayList<Integer>();
	        mDispatcher.register(this);
	    }
		mMessages.add(message);
	}
	
	protected boolean onWindowBackKeyEvent() {
		return false;
	}
	
	@Override
	public void onWindowExitEvent(boolean withAnimation) {
	}
	
	@Override
	public void notify(Notification notification) {
		
	}
	
	public boolean sendMessage(Message message) {
		return mDispatcher.sendMessage(message);
	}
	
	public void sendMessage(Message message, long delay) {
	    mDispatcher.sendMessage(message, delay);
	}
	
	public boolean sendMessage(int what) {
        return mDispatcher.sendMessage(what);
    }
	
	public boolean sendMessage(int what, int arg1, int arg2, Object obj) {
		return mDispatcher.sendMessage(what, arg1, arg2, obj);
	}
	
	public boolean sendMessage(int what, int arg1, int arg2) {
        return mDispatcher.sendMessage(what, arg1, arg2);
    }
	
	public Object sendMessageSync(int what) {
		return mDispatcher.sendMessageSync(what);
	}
	
	public Object sendMessageSync(int what, Object obj) {
        return mDispatcher.sendMessageSync(what, obj);
    }
	
	public Object sendMessageSync(int what, int arg1, int arg2) {
        return mDispatcher.sendMessageSync(what, arg1, arg2);
    }
	
	public Object sendMessageSync(Message message) {
        return mDispatcher.sendMessageSync(message);
    }
	
}
