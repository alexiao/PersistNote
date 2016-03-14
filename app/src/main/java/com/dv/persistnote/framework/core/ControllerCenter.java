package com.dv.persistnote.framework.core;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ControllerCenter {
    private static final int MAX_MESSAGE_POLICY_COUNT = 80;
    
    private ControllerFactory mControllerFactory;
    private BaseEnv mEnvironment;
    
    private SparseArray<MessagePolicy> mMessagePolicys = new SparseArray<MessagePolicy>(MAX_MESSAGE_POLICY_COUNT+1);
    private List<NotificationPolicy> mNotificationPolicys = new LinkedList<NotificationPolicy>();
    private SparseArray<AbstractController> mControllerCache = new SparseArray<AbstractController>();
    private List<DependPolicy> mDependPolicies = new ArrayList<DependPolicy>();

    public void setControllerFactory(ControllerFactory controllerFactory) {
        mControllerFactory = controllerFactory;
    }
    
    public void setEnvironment(BaseEnv environment) {
        mEnvironment = environment;
    }
    
    private AbstractController findOrCreateControllerByID(int controllerId) {
        AbstractController controller = mControllerCache.get(controllerId);
        if (controller == null) {
            controller = mControllerFactory.createControllerByID(mEnvironment, controllerId);
            if (controller != null) {
                mControllerCache.put(controllerId, controller);
            }
            for (DependPolicy dePolicy : mDependPolicies) {
                if(dePolicy.mControllerID == controllerId){
                    findOrCreateControllerByID(dePolicy.mDependControllerID);
                }
            }
        }
        return controller;
    }
    
    public AbstractController findOrCreateControllerByPolicy(Policy policy) {
       if(policy == null){
           return null;
       }
       AbstractController controller = findOrCreateControllerByID(policy.mControllerID);
        return controller;
    }
    
    public AbstractController findControllerByMessageID(int messageID) {
        
        AbstractController controller = null;
        
        MessagePolicy policy = mMessagePolicys.get(messageID);
        if (policy != null) {
            controller = findOrCreateControllerByPolicy(policy);
        }
        
        return controller;
    }
    
    public void addPolicy(Policy policy) {
        if (policy == null) {
            return;
        }
        if (policy instanceof MessagePolicy) {
            MessagePolicy msgPolicy = (MessagePolicy)policy;
            for (int msgId : msgPolicy.mMessageIDs) {
                mMessagePolicys.put(msgId, msgPolicy);
            }
        } else if(policy instanceof NotificationPolicy) {
            mNotificationPolicys.add((NotificationPolicy)policy);
        } else if(policy instanceof DependPolicy) {
            mDependPolicies.add((DependPolicy)policy);
        }
    }

    public List<NotificationPolicy> getNotificationPolicies() {
        return mNotificationPolicys;
    }

    //-------------------------- IChildInfoGetter ----------------------------
    public AbstractController findChildController(int controllerId) {
        return findOrCreateControllerByID(controllerId);
    }

    public static class Policy {
        public static final int MODEL_TYPE_NONE = -1;

        /**
         * 处理完消息缓存Controller实例
         */
        public static final byte PERMANENT = 0;

        /**
         * 处理完消息不缓存Controller实例
         */
        public static final byte TEMPORARY = 1;

        /**
         * 控制器ID,定义在ControllID类
         */
        public int mControllerID;

        public byte mMemoryStrategy;

        public byte mLifeCycle = PERMANENT;

    }

    public static class MessagePolicy extends Policy {
        /**
         * 在MsgDispatcher注册的消息
         */
        public int[] mMessageIDs;

        public static MessagePolicy create(int controllerID, int[] messageIDs) {
            MessagePolicy policy = new MessagePolicy();
            policy.mControllerID = controllerID;
            policy.mMessageIDs = messageIDs;
            return policy;
        }
    }

    public static class NotificationPolicy extends Policy{

        public int[] mNotificationIDs;

        public static NotificationPolicy create(int controllerID, int[] messageIDs) {
            NotificationPolicy policy = new NotificationPolicy();
            policy.mControllerID = controllerID;
            policy.mNotificationIDs = messageIDs;
            return policy;
        }
    }

    public static class DependPolicy extends Policy {
        int mDependControllerID;
        public static DependPolicy create(int controllerID, int dependID) {
            DependPolicy policy = new DependPolicy();
            policy.mControllerID = controllerID;
            policy.mDependControllerID = dependID;
            return policy;
        }
    }



}
