package com.dv.persistnote.framework.core;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;

import java.util.ArrayList;

public final class MsgDispatcher implements Callback{
    
    public static final String TAG = "MsgDispatcher";
    

    private ArrayList<IMessageHandler> mHandlers = new ArrayList<IMessageHandler>();
    
    private Handler mMsgDispatcherHandler = null;

    private ControllerCenter mControllerCenter;

    public MsgDispatcher() {
        if (null == mMsgDispatcherHandler) {
            mMsgDispatcherHandler = new Handler(Looper.getMainLooper(), this);
        }
    }

	public void setControllerCenter(ControllerCenter controllerCenter) {
	    mControllerCenter = controllerCenter;
	}
	
	public void register(IMessageHandler handler){
		mHandlers.add(handler);
	}
	
	public void unregister(IMessageHandler handler){
		mHandlers.remove(handler);
	}
	
	public boolean sendMessage(int what, long delay) {
		Message message = mMsgDispatcherHandler.obtainMessage();
		message.what = what;
		return mMsgDispatcherHandler.sendMessageDelayed(message, delay);
	}
	
	public boolean sendMessage(int what) {
		return sendMessage(what, 0);
	}
	
	public boolean sendMessage(int what, int arg1, int arg2, long delay) {
		Message message = mMsgDispatcherHandler.obtainMessage(what, arg1, arg2, null);
		return mMsgDispatcherHandler.sendMessageDelayed(message, delay);
	}
	
	public boolean sendMessage(int what, int arg1, int arg2) {
		return sendMessage(what, arg1, arg2, 0);
	}

	public boolean sendMessage(Message message, long delay) {
		return mMsgDispatcherHandler.sendMessageDelayed(message, delay);
	}
	
	public boolean sendMessage(Message message) {
		return sendMessage(message,0);
	}
	
	public boolean sendMessage(int what, int arg1, int arg2, Object obj, long delay) {
		return mMsgDispatcherHandler.sendMessageDelayed(mMsgDispatcherHandler.obtainMessage(what, arg1, arg2, obj), delay);
	}
	
	public boolean sendMessage(int what, int arg1, int arg2, Object obj) {
		return sendMessage(what, arg1, arg2, obj, 0);
	}
	
	private synchronized IMessageHandler findHandler(Message msg) {
        if (msg == null) {
            return null;
        }
        
        for (int i = 0; i < mHandlers.size(); ++i) {
            IMessageHandler c = mHandlers.get(i);
            ArrayList<Integer> msgs = c.messages();
            if ( null != msgs && msgs.contains(msg.what)){
                return c;
            }
        }
        
        IMessageHandler handler = null;
        if ( mControllerCenter != null) {
            handler = mControllerCenter.findControllerByMessageID(msg.what);
        } else {
            // mControllerCenter is null before calling setControllerCenter() method.
        }
        
        return handler;
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		assert(msg != null);

		IMessageHandler c = findHandler(msg);
		
		if (c != null) {
			c.handleMessage(msg);
			return true;
		}
		return false;
	}
	
	/**
	 * 发送同步消息，马上获取返回值</br>
	 * <strong>注意线程安全，多线程调用有风险!!!</strong>
	 * @param msg 
	 * @return Object类型返回值
	 */
	public Object sendMessageSync(Message msg) {
		assert(msg != null);

		IMessageHandler c = findHandler(msg);
		
		if (c != null) {
			return c.handleMessageSync(msg);
		}
		
		return null;
	}
	
	/**
	 * 发送同步消息，马上获取返回值
	 * @param what 消息类型
	 * @return Object类型返回值
	 */
	public Object sendMessageSync(int what) {
		Message message = mMsgDispatcherHandler.obtainMessage();
		message.what = what;
		return sendMessageSync(message);
	}
	
	public Object sendMessageSync(int what, Object obj) {
	    return sendMessageSync(what, 0, 0, obj);
	}
	
	public Object sendMessageSync(int what, int arg1, int arg2) {
	    return sendMessageSync(what, arg1, arg2, null);
	}
	
	public Object sendMessageSync(int what, int arg1, int arg2, Object obj) {
		Message message = mMsgDispatcherHandler.obtainMessage();
		message.what = what;
		message.arg1 = arg1;
		message.arg2 = arg2;
		message.obj = obj;
		return sendMessageSync(message);
	}
	
	public interface IMessageHandler {
		public ArrayList<Integer> messages();
		public void handleMessage(Message msg);
		public Object handleMessageSync(Message msg);
	}
	
}
