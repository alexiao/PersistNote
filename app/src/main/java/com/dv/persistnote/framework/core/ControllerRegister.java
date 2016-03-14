package com.dv.persistnote.framework.core;


import com.dv.persistnote.framework.core.ControllerCenter.MessagePolicy;

public class ControllerRegister{
   
    private ControllerCenter mControllerCenter;

    public ControllerRegister(ControllerCenter controllerCenter) {
        mControllerCenter = controllerCenter;
    }

    /**
     * 注册启动过程需要初始化的Controller
     */
    public void registerControllers() {

        registerRootController();

    }

    public void registerRootController(){
        int controllerID = ControllerID.ROOT_CONTROLLER;

        int[] messageIDs = new int[]{
                MsgDef.MSG_INIT_ROOTSCREEN,
                };
        mControllerCenter.addPolicy(MessagePolicy.create(controllerID, messageIDs));
    }

//	 private void registerHomePgaeFlushController() {
//         int controllerID = ControllerID.HOMEPAGE_FLUSH_CONTROLLER;
//         int[] messageIDs = new int[] {
//                 MsgDef.MSG_INFOFLOW_BARCODE_TEST_NEWS,
//                 MsgDef.MSG_JS_OPEN_INFOFLOW_IMAGEGALLERY,
//                 MsgDef.MSG_ON_GET_INFOFLOW_USER_INTERSET_TAG,
//                 MsgDef.MSG_OPEN_IFLOW_SINGLE_CHANNEL_WINDOW,
//         };
//
//         int[] notificationIDs = new int[] {
//                 NotificationDef.N_STARTUP_FINISHED_AFTER_1_SECONDS
//         };
//
//         mControllerCenter.addPolicy(MessagePolicy.create(controllerID, messageIDs));
//         mControllerCenter.addPolicy(NotificationPolicy.create(controllerID, notificationIDs));
//    }


}