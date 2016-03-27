package com.dv.persistnote.framework.ui;


import android.view.animation.Animation;

public class AbstractScreenInfo {
    
    /**
     * if mTransparent is false, in wallpaper mode, a extra background is enable for this window,
     * otherwise, this window will be seen through.
     */
    private boolean mTransparent = false;
    
    /**
     * true，创建的window背景为透明
     */
    private boolean mWindowTransparent = false;
    
    /**
     * if mSingleTop is true, when this window is attached, the window behind will be GONE
     */
    private boolean mSingleTop = true;

    /**
     * 是否自绘壁纸
     */
    private boolean mEnableBackground = false;
    

    private boolean mIsAnimating = false;

    //非AC使用android窗口级动画
    /**
     * android窗口级别的窗口动画
     */
    private int mAndroidWindowAnimation = -1;
    
    //AC使用View级动画
    /**
     * 窗口切入动画
     */
    private Animation mPushAnimation;
    /**
     * 窗口切入时底部窗口动画
     */
    private Animation mUnderPushAnimation;
    /**
     * 窗口切出动画
     */
    private Animation mPopAnimation;
    /**
     * 窗口切出时底部窗口动画
     */
    private Animation mUnderPopAnimation;

    public void setTransparent(boolean transparent) {
        mTransparent = transparent;
    }

    public boolean isTransparent() {
        return mTransparent;
    }
    
    public void setWindowTransparent(boolean transparent) {
        mWindowTransparent = transparent;
    }
    
    public boolean isWindowTransparent() {
        return mWindowTransparent;
    }

    public void setSingleTop(boolean singleTop) {
        mSingleTop = singleTop;
    }

    public boolean isSingleTop() {
        return mSingleTop;
    }


    public void setEnableBackground(boolean enable) {
        mEnableBackground = enable;
    }
    
    public boolean isEnableBackground() {
        return mEnableBackground;
    }
    
    public void setIsAnimating(boolean isAnimating) {
        mIsAnimating = isAnimating;
    }
    
    public boolean isAnimating() {
        return mIsAnimating;
    }
    
    public Animation getPushAnimation() {
        return mPushAnimation;
    }

    public void setPushAnimation(Animation pushAnimation) {
        mPushAnimation = pushAnimation;
    }

    public Animation getUnderPushAnimation() {
        return mUnderPushAnimation;
    }

    public void setUnderPushAnimation(Animation underPushAnimation) {
        mUnderPushAnimation = underPushAnimation;
    }

    public Animation getPopAnimation() {
        return mPopAnimation;
    }

    public void setPopAnimation(Animation popAnimation) {
        mPopAnimation = popAnimation;
    }

    public Animation getUnderPopAnimation() {
        return mUnderPopAnimation;
    }

    public void setUnderPopAnimation(Animation underPopAnimation) {
        mUnderPopAnimation = underPopAnimation;
    }
    
    public int getAndroidWindowAnimation() {
        //保留一个默认动画
        return mAndroidWindowAnimation;
    }
    
    public void setAndroidWindowAnimation(int animationStyle) {
        mAndroidWindowAnimation = animationStyle;
    }

}
