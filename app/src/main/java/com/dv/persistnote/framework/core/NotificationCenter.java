package com.dv.persistnote.framework.core;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.SparseArray;

import com.dv.persistnote.base.util.ThreadManager;

/**
 * 该类是Notification的核心逻辑。</br>
 * 将核心逻辑独立一个类，是为了方便二级模块实例化自己的Notification。</br>
 * 原先在{@link NotificationCenter}定义的ID已经移到{@link NotificationDef}，后续请在那里定义ID。</br>
 * {@link NotificationCenter}支持多个实例，使用的时候可以调用{@link #getGlobalInstance()}获取它的全局实例，相当于原来的{@code getInstance()}，</br>
 * 也可以调用{@link #obtain(int)}函数获取它的局部实例，让二级模块可以拥有局部的广播实现。
 * 
 * @author zhangsl
 *
 */
public class  NotificationCenter {
    private final static String TAG = NotificationCenter.class.getName();
    
    private int[] mNotificationHandlerCountCheckList = new int[0];
    
    private final static int PRIVATE_MSG_REGISTER = 2;
    private final static int PRIVATE_MSG_UNREGISTER = 3;
    private final static int PRIVATE_MSG_NOTIFY_ON_MAINTHREAD = 4;
    
    private ArrayList<WeakReference<INotify>>[] mArray;
    private ArrayList<WeakReference<INotify>>[] mClearArray;
    private IStaticNotificationProxy mStaticNotificationProxy;
    private boolean mNotifyingLocked;
    private ArrayList<Integer> mNotifyingIds;
    private ArrayList<Integer> mRecursiveCallIds;
    private Handler mHandler = null;
    private boolean mIsInited = false;
    
    /**
     * 维护{@code NotificationCenter}的实例，以{@code tag}作为{@code key}，</br>
     * 以{@code NotificationCenter}作为{@code value}。</br>
     */
    public static SparseArray<NotificationCenter> sInstanceArray = new SparseArray<NotificationCenter>();
    
    
    //=========================================================================================================
    // Tag Declare
    //=========================================================================================================
    public static final int TAG_GLOBLE = 0;
    public static final int TAG_VIDEO = 1;
    public static final int TAG_FILEMGR = 2;
    public static final int TAG_BOOKMARK = 3;
    public static final int TAG_HISTORY = 4;
    
    
    //=========================================================================================================
    // FACTORY METHODS
    //=========================================================================================================
    /**
     * @return 获取浏览器内全局的{@code NotificationCenter}实例，会自动初始化
     */
    public synchronized static NotificationCenter getGlobalInstance() {
        NotificationCenter instance  = obtain(TAG_GLOBLE);
        
        //initialize the instance if need
        if (!instance.isInited()) {
            instance.init(Looper.getMainLooper(), NotificationDef.getIDCount());
            
            int[] notificationHandlerCountCheckWhiteList = new int[] {
                
            };
            
            instance.setHandlerCountCheckWhiteListForDebug(notificationHandlerCountCheckWhiteList);
        }
        
        return instance;
    }
    
    /**
     * 根据{@code tag}获取{@code NotificationCenter}实例。</br>
     * 如果当前该{@code tag}对应的{@code NotificationCenter}还没有创建，则会先创建，将它加入{@code array}中缓存，再返回实例。</br>
     * 一个{@code tag}在浏览器的生命周期中，只会对应一个{@code NotificationCenterEX}实例。
     * 
     * @param tag
     *          标志不同的{@code NotificationCenter}，例如{@link NotificationCenter#TAG_VIDEO}。
     * @return  {@code tag}对应的{@code NotificationCenter}实例
     */
    public synchronized static NotificationCenter obtain(int tag) {
        NotificationCenter instance = sInstanceArray.get(tag);
        
        if (instance == null) {
            instance = new NotificationCenter();
            sInstanceArray.append(tag, instance);
        }
        
        return instance;
    }
    
    /**
     * 构造函数私有化</br>
     * {@code NotificationCenter}实例应该调用{@link #getGlobalInstance()}或者{@link #obtain(int)}获取。不应该自己{@code new}。
     */
    private NotificationCenter() {}
    
    
    //=========================================================================================================
    // PUBLIC METHODS
    //=========================================================================================================
    /**
     * <strong>每一个</strong>{@code tag}对应的{@code NotificationCenter} <strong>该且仅该</strong> 调用该函数初始化一次。</br>
     * 一个{@code NotificationCenter}如果重复调用该函数超过一次，直接崩溃。
     * 
     * @param looper
     *          初始化{@code NotificationCenter}的{@link Looper}，没有特殊情况，应该是{@code mainLooper}。
     * @param idCount
     *          使用{@code NotificationCenter}模块定义的{@code Notification}数
     */
    @SuppressWarnings("unchecked")
    public void init(Looper looper, final int idCount) {
        if (mIsInited) {
            throw new IllegalStateException("NotificationCenter instance has been inited!!!");
        }
        
        mHandler = new Handler(looper) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == PRIVATE_MSG_REGISTER) {
                    registerInner((INotify)msg.obj, msg.arg1);
                } else if (msg.what == PRIVATE_MSG_UNREGISTER) {
                    unregisterInner((INotify)msg.obj, msg.arg1);
                } else if (msg.what == PRIVATE_MSG_NOTIFY_ON_MAINTHREAD) {
                    notifyInner((Notification)msg.obj);
                }
            }
        };
        
        mArray = new ArrayList[idCount];
        for (int i = 0; i < idCount; i++) {
            mArray[i] = new ArrayList<WeakReference<INotify>>();
        }
        
        mClearArray = new ArrayList[idCount];
        for (int i = 0; i < idCount; i++) {
            mClearArray[i] = new ArrayList<WeakReference<INotify>>();
        }
        
        mNotifyingIds = new ArrayList<Integer>(10); 
        mRecursiveCallIds = new ArrayList<Integer>(10); 
        mIsInited = true;
    }
    
    
    /**
     * 设置{@link Notification}检查白名单，名单中的{@link Notification}可以没有对应Handler。</br>
     * 
     * 在Debug版本中，我们会检查每一个发送出去的{@link Notification}是否有对应的执行者，如果没有执行者，会通过{@link ExceptionHandler#processFatalException(Throwable)}抛异常。</br>
     * 如果有些{@link Notification}是允许没有执行者的，可以配置在这个白名单中。
     * 
     * @param handlerCountCheckWhiteList
     *              在这个白名单中的{@link Notification}，如果没有Handler执行它也不会报错。
     */
    public void setHandlerCountCheckWhiteListForDebug(int[] handlerCountCheckWhiteList) {
        if (handlerCountCheckWhiteList == null && mNotificationHandlerCountCheckList.length > 0) {
            mNotificationHandlerCountCheckList = new int[0];
        } else {
            mNotificationHandlerCountCheckList = handlerCountCheckWhiteList;
        }
    }
    
    public void setStaticNotificationProxy(IStaticNotificationProxy proxy) {
        mStaticNotificationProxy = proxy;
    }
    
    public void register(INotify notify, int notificationID) {
        if (ThreadManager.isMainThread()) {
            registerInner(notify, notificationID);
        } else {
            mHandler.sendMessage(mHandler.obtainMessage(PRIVATE_MSG_REGISTER, notificationID, 0, notify));
        }
    }
    
    public void unregister(INotify notify, int notificationID) {
        if (ThreadManager.isMainThread()) {
            unregisterInner(notify, notificationID);
        } else {
            mHandler.sendMessage(mHandler.obtainMessage(PRIVATE_MSG_UNREGISTER, notificationID, 0, notify));
        }
    }
    
    public void notify(Notification notification) {
        if (ThreadManager.isMainThread()) {
            notifyInner(notification);
        } else {
            mHandler.sendMessage(mHandler.obtainMessage(PRIVATE_MSG_NOTIFY_ON_MAINTHREAD, notification));
        }
    }
    
    public void notify(Notification notification, long delay) {
        mHandler.sendMessageDelayed(mHandler.obtainMessage(PRIVATE_MSG_NOTIFY_ON_MAINTHREAD, notification), delay);
    }
    
    /**
     * @return 是否调用过{@link #init(Looper, int)}
     */
    public boolean isInited() {
        return mIsInited;
    }
    
    /**
     * 暂时和{@link Notification#obtain(int, Object)}没有区别
     * @param id
     * @param extObj
     * @return
     */
    public Notification obtainNotification(int id, Object extObj) {
        return new Notification(id, extObj);
    }
    
    /**
     * 暂时和{@link Notification#obtain(int)}没有区别
     * @param id
     * @param extObj
     * @return
     */
    public Notification obtainNotification(int id) {
        return new Notification(id);
    }
    
    /**
     * 在浏览器退出时调用该函数，重置静态变量
     */
    public synchronized static void destroy() {
        sInstanceArray.clear();
    }
    
    

    private void registerInner(INotify notify, int notificationID) {
        mArray[notificationID].add(new WeakReference<INotify>(notify));
    }

    private void unregisterInner(INotify notify, int notificationID) {
        int size = mArray[notificationID].size();
        INotify arrayMember;
        for (int i = 0; i < size; i++) {
            WeakReference<INotify> weakObject = mArray[notificationID].get(i);
            if (null == weakObject) {
                continue;
            }
            
            arrayMember = weakObject.get();
            if (arrayMember != null && arrayMember == notify) {
                mArray[notificationID].remove(weakObject);
                break;
            }
        }
    }
    
    private void notifyInner(Notification notification) {
        //通知可能还没有被实例化的接收者
        checkOrRegisterStaticDeclareReceviers(notification);
        
        //记录是否是顶层调用，只有顶层调用才会去清除mArray中无用的WeakRefference，避免嵌套通知时数组的size发生改变
        final boolean isTopLevelCall = !mNotifyingLocked;
        mNotifyingLocked = true;
        mNotifyingIds.add(notification.id);
        mRecursiveCallIds.add(notification.id);
        

        //为了确保mNotifyingLocked能够被正确赋值为false，这里必须加上try/catch
        try {
            INotify notify;
            final int count = mArray[notification.id].size();
            
            for (int i = 0; i < count; ++i) {
                WeakReference<INotify> weakObject = mArray[notification.id].get(i);
                notify = weakObject.get();
                if (notify != null) {
                    try {
                        notify.notify(notification);
                    } catch (Throwable t) {
                    }
                } else {
                    mClearArray[notification.id].add(weakObject);
                }
            }
        } catch (Exception e) {
        } finally {
            mNotifyingIds.remove((Integer)notification.id);
           
            if (isTopLevelCall) {

                // mRecursiveCallIds记录了本次调用所有出现过的id，我们只需要清理这些id就行了
                // 从mRecursiveCallIds拿到一个id后，看看mClearArray[notificationId]里面有多少个需要删除的对象
                for (int i = 0; i < mRecursiveCallIds.size(); ++i) {
                    final int notificationId = mRecursiveCallIds.get(i);
                    for (int k = 0; k < mClearArray[notificationId].size(); ++k) {
                        mArray[notification.id].remove(mClearArray[notificationId].get(k));
                    }
                    mClearArray[notificationId].clear();
                }
                mNotifyingLocked = false;
                mRecursiveCallIds.clear();
                mNotifyingIds.clear();
            }
        }
    }
    
    private void checkOrRegisterStaticDeclareReceviers(Notification notification) {
        if(mStaticNotificationProxy != null){
            mStaticNotificationProxy.checkOrRegisterStaticDeclareReceviers(this, notification);
        }
    }
    
    
    //=========================================================================================================
    // Interface Declare
    //=========================================================================================================
    
    /**
     * Notification注册方式有两种，一种是调用{@link NotificationCenter#register(INotify, int)}进行动态注册。</br>
     * 另外一种是在{@link ControllerRegister}中静态注册。</br>
     * 这个接口将静态注册Notification的Controller初始化，并保证他们能收到消息。
     * @author zhangsl
     *
     */
    public static interface IStaticNotificationProxy{
        
        /**
         * 将静态注册了notification的Controller初始化，并注册到{@link NotificationCenter}中。
         * 
         * @param center
         *          提供Notification的注册以及派发。
         * @param notification
         *          具体的Notification。
         */
        public void checkOrRegisterStaticDeclareReceviers(NotificationCenter center, Notification notification);
    }
}
