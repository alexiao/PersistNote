package com.dv.persistnote.framework.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.dv.persistnote.base.util.HardwareUtil;


final class ScreenEnvironment extends FrameLayout {

    private static final String TAG = "ACWindowEnvironment";
    private static final boolean DEBUG_MEASURE_LAYOUT_EFFICIENCY = false;

    private LayoutParams mMatchParentLP;
    private static final DisplayMetrics sACDisplayMetrics = new DisplayMetrics();
    private WindowLayer mWindowLayer;
    private ExtendedLayer mExtendedLayer;
    
    private ScreenStack mCurrentStack;
    
    private static class ExtendedLayer extends FrameLayout {

        public ExtendedLayer(Context aContext) {
            super(aContext);
        }
        
    }
    
    public ScreenEnvironment(Context context) {
        super(context);
        mMatchParentLP = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        
        mWindowLayer = new WindowLayer(context);

        addView(mWindowLayer, mMatchParentLP);
        
        mExtendedLayer = new ExtendedLayer(context);
       
        addView(mExtendedLayer, mMatchParentLP);

        setBackgroundColor(Color.WHITE);
    }
    
    void createWindowStack(ScreenStack winStack, int index, boolean switchTo) {
        winStack.getRootWindow().onWindowStateChange(AbstractScreen.STATE_ON_WIN_STACK_CREATE);
        if (switchTo) {
            mWindowLayer.addView(winStack, index);
            switchWindowStack(winStack);
        } else {
            winStack.setVisibility(View.INVISIBLE);
            mWindowLayer.addView(winStack, index);
        }
    }
    
    private boolean ensureIndexLegal(int index) {
        if (index < 0 || index > mWindowLayer.getChildCount() - 1) {
            return false;
        }
        return true;
    }
    
    boolean destroyWindowStack(int index) {
        if (ensureIndexLegal(index)) {
            getWindowStackAt(index).popToRootWindow(false);
            if (mWindowLayer.getChildCount() == 1) {
                return false;
            }
            getWindowStackAt(index).getRootWindow()
                .onWindowStateChange(AbstractScreen.STATE_ON_WIN_STACK_DESTROY);
            if (mCurrentStack == getWindowStackAt(index)) {
                int fallbackCurrentStackIndex = (index > 0) ? index - 1: index;
                mWindowLayer.removeViewAt(index);
                switchWindowStack(fallbackCurrentStackIndex);
            } else {                    
                mWindowLayer.removeViewAt(index);                   
            }
            return true;
        }
        return false;
    }
    
    int getWindowStackCount() {
        return mWindowLayer.getChildCount();
    }
    
    void switchWindowStack(int index) {
        if (index == getStackIndex(mCurrentStack)) {
            return;
        }
        if (ensureIndexLegal(index)) {
            int count = mWindowLayer.getChildCount();
            for (int i = 0; i < count; i++) {
                if (i == index) {
                    
                    mCurrentStack.getRootWindow().clearAnimation();
                    mCurrentStack.getRootWindow().onWindowStateChange(AbstractScreen.STATE_ON_SWITCH_OUT);
                    if (mCurrentStack.getRootWindow() != mCurrentStack.getStackTopWindow()) {
                        mCurrentStack.getStackTopWindow().clearAnimation();
                        mCurrentStack.getStackTopWindow().onWindowStateChange(AbstractScreen.STATE_ON_SWITCH_OUT);
                    }
                    mCurrentStack = getWindowStackAt(index);
                    mCurrentStack.getRootWindow().onWindowStateChange(AbstractScreen.STATE_ON_SWITCH_IN);
                    if (mCurrentStack.getRootWindow() != mCurrentStack.getStackTopWindow()) {
                        mCurrentStack.getStackTopWindow().onWindowStateChange(AbstractScreen.STATE_ON_SWITCH_IN);
                    }
                    // 必须先更新mCurrentStack，再设置VISIBLE
                    mCurrentStack.setVisibility(View.VISIBLE);
                    /**
                     * 设置VISIBLE后 stack并没有requestLayout，需要手动调一下requestLayout
                     */
                    mCurrentStack.requestLayout();

                    View topView = mCurrentStack.getChildAt(mCurrentStack.getChildCount()-1);
                    if (topView != null) {
                        topView.setVisibility(View.VISIBLE);
                    }
                }
            }
            
            // 将当前Stack隐藏会引发窗口onVisibilityChanged,而该方法内部需要根据新的Stack是否指向WebView以处理壁纸是否需要隐藏的问题。
            // 所以：在变更mCurrentStack之后才能隐藏当前Stack，否则onVisibilityChanged将无法获取正确的Stack，引发GPU overDraw。
            for (int i = 0; i < count; ++i) {
                if (i != index) {
                    mWindowLayer.getChildAt(i).setVisibility(View.INVISIBLE);
                }
            }
        }
    }
    
    void switchWindowStack(ScreenStack winStack) {
        int count = mWindowLayer.getChildCount();
        View child = null;
        for (int i = 0; i < count; i++) {
            child = mWindowLayer.getChildAt(i);
            if (winStack == child) {
                if (null != mCurrentStack) {
//                    mCurrentStack.popToRootWindow(false);
                    
                    mCurrentStack.getRootWindow().clearAnimation();
                    mCurrentStack.getRootWindow().onWindowStateChange(AbstractScreen.STATE_ON_SWITCH_OUT);
                    if (mCurrentStack.getStackTopWindow() != mCurrentStack.getRootWindow()) {
                        mCurrentStack.getStackTopWindow().clearAnimation();
                        mCurrentStack.getStackTopWindow().onWindowStateChange(AbstractScreen.STATE_ON_SWITCH_OUT);
                    }
                }
                mCurrentStack = winStack;
                mCurrentStack.getRootWindow().onWindowStateChange(AbstractScreen.STATE_ON_SWITCH_IN);
                if (mCurrentStack.getRootWindow() != mCurrentStack.getStackTopWindow()) {
                    mCurrentStack.getStackTopWindow().onWindowStateChange(AbstractScreen.STATE_ON_SWITCH_IN);
                }
                
                // 必须先更新mCurrentStack，再设置VISIBLE
                mCurrentStack.setVisibility(View.VISIBLE);
            }
        }

        // 将当前Stack隐藏会引发窗口onVisibilityChanged,而该方法内部需要根据新的Stack是否指向WebView以处理壁纸是否需要隐藏的问题。
        // 所以：在变更mCurrentStack之后才能隐藏当前Stack，否则onVisibilityChanged将无法获取正确的Stack，引发GPU overDraw。
        for (int i = 0; i < count; ++i) {
            child = mWindowLayer.getChildAt(i);
            if (null != child && winStack != child) {
                child.setVisibility(View.INVISIBLE);
            }
        }
    }
    
    ScreenStack getWindowStackAt(int index) {
        if (ensureIndexLegal(index)) {
            return (ScreenStack)mWindowLayer.getChildAt(index);
        }
        return null;
    }

    int getStackIndex(ScreenStack stack) {
        for (int i = 0; i < getWindowStackCount(); i++) {
            if (stack == getWindowStackAt(i)) {
                return i;
            }
        }
        return -1;
    }
    
    ScreenStack getCurrentWindowStack() {
        return mCurrentStack;
    }
    
    int getCurrentWindowStatckIndex() {
        return getStackIndex(mCurrentStack);
    }
    
    void showWindowLayer() {
        mWindowLayer.setVisibility(View.VISIBLE);
    }
    
    void hideWindowLayer() {
        mWindowLayer.setVisibility(View.GONE);
    }
    


    void addLayer(View layer) {
        addView(layer, mMatchParentLP);
    }
    
    void removeLayer(View layer) {
        removeView(layer);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent ev) {
        if (mCurrentStack != null && mCurrentStack.getStackTopWindow() != null) {
            return mCurrentStack.getStackTopWindow().dispatchKeyEvent(ev);
        }
        return super.dispatchKeyEvent(ev);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (DEBUG_MEASURE_LAYOUT_EFFICIENCY) {
            final long startTime = System.currentTimeMillis();
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            final long consume = System.currentTimeMillis() - startTime;
            if (DEBUG_MEASURE_LAYOUT_EFFICIENCY) {
                Log.d(TAG, "onMeasure in : " + this + " Consumes : " + consume);
            }
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
            int bottom) {
        if (DEBUG_MEASURE_LAYOUT_EFFICIENCY) {
            final long startTime = System.currentTimeMillis();
            super.onLayout(changed, left, top, right, bottom);
            final long consume = System.currentTimeMillis() - startTime;
            if (DEBUG_MEASURE_LAYOUT_EFFICIENCY) {
                Log.d(TAG, "onLayout in : " + this + " Consumes : " + consume);
            }
        } else {
            super.onLayout(changed, left, top, right, bottom);
        }
        if (changed) {
            final Display display = ((Activity)getContext()).getWindowManager().getDefaultDisplay();
            HardwareUtil.screenWidth = display.getWidth();
            HardwareUtil.screenHeight = display.getHeight();
            HardwareUtil.windowWidth = right - left;
            HardwareUtil.windowHeight = bottom - top;
            display.getMetrics(sACDisplayMetrics);
            HardwareUtil.density = sACDisplayMetrics.density;
        }
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private static class WindowLayer extends FrameLayout {

        private boolean mBlockDispatchDraw;

        public WindowLayer(Context context) {
            super(context);
        }
        
    }
}

