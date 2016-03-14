package com.dv.persistnote.framework.core;


public class NotificationDef {
	
	private static int sNotificationBase = 0;
	
	private static int generateNotificationID() {
		return sNotificationBase ++;
	}
	
	/**
     * @return 当前注册的NotificationID的个数。</br>
     *              该函数依赖{@link #sNotificationBase}，故应确保{@link #generateNotificationID()}除了用于生成ID外，不用于其他用途
     */
    public static int getIDCount() {
        return sNotificationBase;
    }
    
    //=========================================================================================================
    //      Notification Declare
    //=========================================================================================================

	public static final int N_ORIENTATION_CHANGE = generateNotificationID();
	public static final int N_THEME_CHANGE = generateNotificationID();
	public static final int N_TYPEFACE_CHANGE = generateNotificationID();
	public static final int N_WALLPAPER_CHANGE = generateNotificationID(); // 自绘壁纸的控件需要注册这个消息
	public static final int N_FOREGROUND_CHANGE = generateNotificationID();
	public static final int N_STARTUP_FINISHED = generateNotificationID();
	public static final int N_STARTUP_FINISHED_AFTER_1_SECONDS = generateNotificationID();
	public static final int N_STARTUP_FINISHED_AFTER_10_SECONDS = generateNotificationID();
	public static final int N_NETWORK_STATE_CHANGE = generateNotificationID();
    public static final int N_ON_EXITING = generateNotificationID();
	public static final int N_FULL_SCREEN_MODE_CHANGE = generateNotificationID(); //全屏模式变化
}
