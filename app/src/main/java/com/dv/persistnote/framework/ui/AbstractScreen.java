package com.dv.persistnote.framework.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.dv.persistnote.framework.core.INotify;
import com.dv.persistnote.framework.core.Notification;
import com.dv.persistnote.framework.core.NotificationCenter;
import com.dv.persistnote.framework.core.NotificationDef;


public abstract class AbstractScreen extends RelativeLayout implements INotify {
    
    public static final String TAG = "AbstractWindow";
    public static final boolean DEBUG = false;

    /**
     * When manipulating Window & WindowStack, WindowManager will callback by calling onWindowStateChange with certain flag.
     * These flags are devided into 2 categories: with animation(STATE_BEFORE_XXX, STATE_AFTER_XXX) & without animation(STATE_ON_XXX).
     * STATE_BEFORE_XXX called before animation.
     * STATE_AFTER_XXX called after animation.
     * 
     * Flags for onWindowStateChange are:
     * STATE_BEFORE_PUSH_IN: Window is going to animate in but not visible yet, usually slide from bottom to top.
     * STATE_AFTER_PUSH_IN: Window has just animated in.
     * STATE_ON_SHOW: Window shows up without animation.
     * STATE_BEFORE_POP_OUT: Window is going to animate out, usually side from top to bottom. Revert process to STATE_BEFORE_PUSH_IN.
     * STATE_AFTER_POP_OUT: Window has just animated out.
     * STATE_ON_HIDE: Window hide out without animation. Revert process to STATE_ON_SHOW.
     * STATE_BEFORE_SWITCH_IN: Reserved for later use
     * STATE_AFTER_SWITCH_IN: Reserved for later use
     * STATE_ON_SWITCH_IN: WindowStack shows up without animation. (switch from one WindowStack to another by gesture or MultiWindowList)
     * STATE_BEFORE_SWITCH_OUT: Reserved for later use
     * STATE_AFTER_SWITCH_OUT: Reserved for later use
     * STATE_ON_SWITCH_OUT: WindowStack hides out without animation.
     * STATE_ON_ATTACH: Window has just been added into WindowStack.
     * STATE_ON_DETACH: Window has just been removed from WindowStack.
     * STATE_ON_WIN_STACK_CREATE: Reserved for later use
     * STATE_ON_WIN_STACK_DESTROY: WindowStack is going to be destroyed.
     */
    public static final byte STATE_BEFORE_PUSH_IN = 0;
    public static final byte STATE_AFTER_PUSH_IN = 1;
    public static final byte STATE_ON_SHOW = 2;
    public static final byte STATE_BEFORE_POP_OUT = 3;
    public static final byte STATE_AFTER_POP_OUT = 4;
    public static final byte STATE_ON_HIDE = 5;
    public static final byte STATE_BEFORE_SWITCH_IN = 6; // reserved for later use
    public static final byte STATE_AFTER_SWITCH_IN = 7; // reserved for later use
    public static final byte STATE_ON_SWITCH_IN = 8;
    public static final byte STATE_BEFORE_SWITCH_OUT = 9;// reserved for later use
    public static final byte STATE_AFTER_SWITCH_OUT = 10;// reserved for later use
    public static final byte STATE_ON_SWITCH_OUT = 11;
    public static final byte STATE_ON_ATTACH = 12;
    public static final byte STATE_ON_DETACH = 13;
    public static final byte STATE_ON_WIN_STACK_CREATE = 14;
    public static final byte STATE_ON_WIN_STACK_DESTROY = 15;
    


    public static final LayoutParams WINDOW_LP = new LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    

    protected UICallBacks mCallBacks;
    protected Rect mWindowRect;
    protected AbstractScreenInfo mWindowInfo = new AbstractScreenInfo();
    
    public AbstractScreen(Context context, UICallBacks callBacks) {
        super(context);
        mCallBacks = callBacks;
        mWindowRect = new Rect();
        setWillNotDraw(false);
        registerNotification();
    }
    
    /****************************** Class Public methods - Begin ******************************/
    
    public void registerNotification() {
        NotificationCenter.getGlobalInstance().register(this, NotificationDef.N_THEME_CHANGE);
        NotificationCenter.getGlobalInstance().register(this, NotificationDef.N_WALLPAPER_CHANGE);
    }
    
    public void unRegisterNotification() {
        NotificationCenter.getGlobalInstance().unregister(this, NotificationDef.N_THEME_CHANGE);
        NotificationCenter.getGlobalInstance().unregister(this, NotificationDef.N_WALLPAPER_CHANGE);
    }
    
    /**
     * 对当前窗口进行截图,不包含背景透明壁纸
     * 
     * @param outBitmap
     *            截图内容将被绘制到这张图片里， 如果为空，则会新建一张跟当前窗口一样size的图片，
     *            如果图片宽高与屏幕不一致，则内容会发生裁剪
     * @param isWithWallPaper
     *            是否同时截取背景的透明壁纸，false为不带背景
     * @return outBitmap 返回最终图片结果
     */
    public Bitmap toSnapShot(Canvas snapShotCanvas, Bitmap outBitmap, boolean isWithWallPaper) {
        long begin;
        if (DEBUG)
            begin = SystemClock.currentThreadTimeMillis();
        
        if (null == outBitmap) {
            outBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                    Config.ARGB_8888);
            if (null == outBitmap) {
                return null;
            }
        }
        
        final boolean lastState = isEnableBackground();
        setEnableBackground(isWithWallPaper); 
        
        snapShotCanvas.setBitmap(outBitmap);
        draw(snapShotCanvas);
        
        setEnableBackground(lastState); 
        if (DEBUG) {
            long end = SystemClock.currentThreadTimeMillis();
            Log.i(TAG, "snap shot cost time: " + String.valueOf(end - begin));
        }
        
        return outBitmap;
    }
    
    /**
     * 对当前窗口进行截图,不包含背景透明壁纸
     * 
     * @param outBitmap
     *            截图内容将被绘制到这张图片里， 如果为空，则会新建一张跟当前窗口一样size的图片，
     *            如果图片宽高与屏幕不一致，则内容会发生裁剪
     * @param isWithWallPaper
     *            是否同时截取背景的透明壁纸，false为不带背景
     * @return outBitmap 返回最终图片结果
     */
    public Bitmap toSnapShot(Bitmap outBitmap, boolean isWithWallPaper) {
        return toSnapShot(new Canvas(), outBitmap, isWithWallPaper);
    }
    
    public void setTransparent(boolean transparent) {
        mWindowInfo.setTransparent(transparent);
    }
    
    public boolean isTransparent() {
        return mWindowInfo.isTransparent();
    }
    
    public void setWindowTransparent(boolean transparent) {
        mWindowInfo.setWindowTransparent(transparent);
    }
    
    public boolean isWindowTransparent() {
        return mWindowInfo.isWindowTransparent();
    }
    
    public void setSingleTop(boolean singleTop) {
        mWindowInfo.setSingleTop(singleTop);
    }
    
    public boolean isSingleTop() {
        return mWindowInfo.isSingleTop();
    }
    
    public void setEnableBackground(boolean enable) {
        mWindowInfo.setEnableBackground(enable);
    }
    
    public boolean isEnableBackground() {
        return mWindowInfo.isEnableBackground();
    }
    
    public boolean isAnimating() {
        return mWindowInfo.isAnimating();
    }
    

    //这些都是View级窗口动画，只有ACWindowManager会用到
    public Animation getPushAnimation() {
        return mWindowInfo.getPushAnimation();
    }
    public Animation getPopAnimation() {
        return mWindowInfo.getPopAnimation();
    }
    public Animation getUnderPushAnimation() {
        return mWindowInfo.getUnderPushAnimation();
    }
    public Animation getUnderPopAnimation() {
        return mWindowInfo.getUnderPopAnimation();
    }
    public void setPushAnimation(int animationStyle) {
        mWindowInfo.setPushAnimation(AnimationUtils.loadAnimation(getContext(), animationStyle));
    }
    public void setPopAnimation(int animationStyle) {
        mWindowInfo.setPopAnimation(AnimationUtils.loadAnimation(getContext(), animationStyle));
    }
    public void setUnderPushAnimation(int animationStyle) {
        mWindowInfo.setUnderPushAnimation(AnimationUtils.loadAnimation(getContext(), animationStyle));
    }
    public void setUnderPopAnimation(int animationStyle) {
        mWindowInfo.setUnderPopAnimation(AnimationUtils.loadAnimation(getContext(), animationStyle));
    }
    
    public void setPopAnimation(Animation animation) {
        mWindowInfo.setPopAnimation(animation);
    }
    public void setPushAnimation(Animation animation) {
        mWindowInfo.setPushAnimation(animation);
    }

    protected UICallBacks getUICallbacks() {
        return mCallBacks;
    }
    
    protected void onThemeChange() {
    }
    
    protected void onWallpaperChange() {
        invalidate();
    }

    protected void onWindowStateChange(byte stateFlag) {
        if (stateFlag == STATE_BEFORE_PUSH_IN || stateFlag == STATE_BEFORE_POP_OUT) {
            mWindowInfo.setIsAnimating(true);
            mWindowInfo.setEnableBackground(true);
            invalidate();
        }
        
        mCallBacks.onWindowStateChange(this, stateFlag);
    }

    protected RelativeLayout onCreateButtonLayer() {
        return createDefaultLayer();
    }

    protected RelativeLayout onCreateBarLayer() {
        return createDefaultLayer();
    }

    protected RelativeLayout onCreateExtLayer() {
        return createDefaultLayer();
    }
    
    protected FrameLayout createDefaultStatusLayer(){
        return new FrameLayout(getContext());
    }

    protected RelativeLayout createDefaultLayer() {
        return new RelativeLayout(getContext());
    }
    
    /****************************** Class Protected methods - End ******************************/
    
    /****************************** Class Private methods - Begin ******************************/
    
    private void disableWallPaperOnFirstTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN && mWindowInfo.isEnableBackground()) {
            mWindowInfo.setEnableBackground(false);
        }
    }

    private String resolveLayerName(int index) {
        switch (index) {
        case 0:
            return "BaseLayer";
        case 1:
            return "BtnLayer";
        case 2:
            return "ExtLayer";
        case 3:
            return "BarLayer";
        default:
            throw new IllegalStateException("AbstractWindow state illegal:"
                    + index);
        }
    }
    
    @Override
    public void notify(Notification notification) {
        if (notification.id == NotificationDef.N_THEME_CHANGE) {
            onThemeChange();
        } else if (notification.id == NotificationDef.N_WALLPAPER_CHANGE) {
            onWallpaperChange();
        }
    }


}
