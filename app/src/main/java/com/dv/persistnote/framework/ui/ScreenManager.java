package com.dv.persistnote.framework.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;

import com.dv.persistnote.framework.HandlEx;
import com.dv.persistnote.framework.core.INotify;
import com.dv.persistnote.framework.core.Notification;


public class ScreenManager implements INotify {

    public static final int DEFAULT_WINDOW_ANIMATION_DURATION = 300;

    protected Context mContext;

    public ScreenManager(Context context){
        mContext = context;
        setupWindowEnvironment(context);
    }

    private ScreenEnvironment mWinEnvironment;
    private View mTargetView;
    private boolean mTouchEventIntercepted;

    private Handler mHandler;

    private void setupWindowEnvironment(Context context) {
        if (null == mWinEnvironment) {
            mWinEnvironment = new ScreenEnvironment(context);
            mWinEnvironment.setFocusableInTouchMode(true);
        }
        ((Activity)context).setContentView(mWinEnvironment);
    }


    
    public void pushScreen(AbstractScreen window, boolean animated) {
        if (mWinEnvironment.getCurrentWindowStack() != null) {
            mWinEnvironment.getCurrentWindowStack().pushScreen(window, animated, true, true);
        }
    }

    public void pushScreen(int windowStackIndex, AbstractScreen window, boolean animated) {
        ScreenStack stack = mWinEnvironment.getWindowStackAt(windowStackIndex);
        if (stack != null) {
            stack.pushScreen(window, animated, true, true);
        }
    }

    

    public void popScreen(boolean animated) {
        if (mWinEnvironment.getCurrentWindowStack() != null) {
            mWinEnvironment.getCurrentWindowStack().popScreen(animated);
        }
    }

    
    public boolean removeWindow(AbstractScreen delWindow, boolean onlyCurStack) {
        if (onlyCurStack) {
            ScreenStack stack = mWinEnvironment.getCurrentWindowStack();
            if (stack != null) {
                stack.removeView(delWindow);
                return stack.removeStackView(delWindow);
            }
            return false;
        } else {
            boolean exist = false;
            for (int i = 0; i < mWinEnvironment.getWindowStackCount(); i ++) {
                ScreenStack stack = mWinEnvironment.getWindowStackAt(i);
                if (stack != null) {
                    stack.removeView(delWindow);
                    exist |= stack.removeStackView(delWindow);
                }
            }

            return exist;
        }
    }

    
    public void popToRootWindow(boolean animated) {
        if (mWinEnvironment.getCurrentWindowStack() != null) {
            mWinEnvironment.getCurrentWindowStack().popToRootWindow(animated);
        }
    }

    
    public void popToRootWindow(int index, boolean animated) {
        if (mWinEnvironment.getWindowStackAt(index) != null) {
            mWinEnvironment.getWindowStackAt(index).popToRootWindow(animated);
        }
    }

    
    public boolean replaceRootWindow(AbstractScreen rootWindow) {
        mWinEnvironment.getCurrentWindowStack().replaceRootWindow(rootWindow);
        return true;
    }

    
    public AbstractScreen getCurrentWindow() {
        if (mWinEnvironment.getCurrentWindowStack() == null) {
            return null;
        }
        return mWinEnvironment.getCurrentWindowStack().getStackTopWindow();
    }

    private AbstractScreen getWindowBehindFromStack(ScreenStack stack, AbstractScreen currentWindow) {
        final int stackSize = stack.getWindowCount();
        for (int i = stackSize - 1; i > 0; i--) {
            if (stack.getWindow(i) == currentWindow) {
                return stack.getWindow(i - 1);
            }
        }
        return null;
    }

    
    public AbstractScreen getWindowTop(int index, AbstractScreen currentWindow) {
        ScreenStack stack = getWindowStack(index);
        final int stackSize = stack.getWindowCount();
        for (int i = 0; i < stackSize-1; ++i) {
            if (stack.getWindow(i) == currentWindow) {
                return stack.getWindow(i + 1);
            }
        }
        return null;
    }

    
    public AbstractScreen getWindowBehind(AbstractScreen currentWindow) {
        if (mWinEnvironment.getCurrentWindowStack() == null) {
            return null;
        }
        return getWindowBehindFromStack(mWinEnvironment.getCurrentWindowStack(), currentWindow);
    }

    
    public AbstractScreen getWindowBehind(int index, AbstractScreen currentWindow) {
        ScreenStack stack = getWindowStack(index);
        if (stack == null) {
            return null;
        }
        return getWindowBehindFromStack(stack, currentWindow);
    }

    
    public AbstractScreen getCurrentRootWindow() {
        if (mWinEnvironment.getCurrentWindowStack() == null) {
            return null;
        }
        return mWinEnvironment.getCurrentWindowStack().getRootWindow();
    }

    
    public int getRootWindowIndex(AbstractScreen rootWindow) {
        final int stackCount = getWindowStackCount();
        AbstractScreen tempRootWin = null;
        for (int i=0; i<stackCount; i++) {
            tempRootWin = getRootWindowAt(i);
            if (tempRootWin == rootWindow)
                return i;
        }
        return -1;
    }

    public ScreenStack getWindowStack(int index) {
        return mWinEnvironment.getWindowStackAt(index);
    }

    
    public int getWindowStackCount() {
        return mWinEnvironment.getWindowStackCount();
    }

    
    public int getCurrentWindowStatckIndex() {
        return mWinEnvironment.getCurrentWindowStatckIndex();
    }

    
    public boolean createWindowStack(AbstractScreen rootWindow) {
        return createWindowStack(rootWindow, -1);
    }

    
    public boolean createWindowStack(AbstractScreen rootWindow, int index) {
        if (null == mWinEnvironment.getCurrentWindowStack()) {
            //第一个stack默认创建后即跳转
            mWinEnvironment.createWindowStack(new ScreenStack(mContext,rootWindow), index, true);
        } else {
            mWinEnvironment.createWindowStack(new ScreenStack(mContext,rootWindow), index, false);
        }
        return true;
    }

    
    public boolean createAndSwitchToWindowStack(AbstractScreen rootWindow, int index) {
        mWinEnvironment.createWindowStack(new ScreenStack(mContext, rootWindow), index, true);
        return true;
    }

    
    public void switchToWindowStack(int index) {
        mWinEnvironment.switchWindowStack(index);
    }

    
    public boolean destroyWindowStack(int index) {
        return mWinEnvironment.destroyWindowStack(index);
    }

    
    public AbstractScreen getRootWindowAt(int index) {
        if (mWinEnvironment.getWindowStackAt(index) == null) {
            return null;
        }
        return mWinEnvironment.getWindowStackAt(index).getRootWindow();
    }

    
    public AbstractScreen getTopWindowAt(int index) {
        if (mWinEnvironment.getWindowStackAt(index) == null) {
            return null;
        }
        return mWinEnvironment.getWindowStackAt(index).getStackTopWindow();
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean result = false;
        final int action = event.getAction();

        if (!mTouchEventIntercepted ) {
            mTouchEventIntercepted = true;
        }

        if (action == MotionEvent.ACTION_DOWN) {
            if (mTargetView != null) {
                mTargetView = null;
            }

            if (!mTouchEventIntercepted && (mWinEnvironment.getCurrentWindowStack() != null )) {
                if (mWinEnvironment.getCurrentWindowStack().isWindowAnimating()) {
                    return true;
                }
                final AbstractScreen topWindow = mWinEnvironment.getCurrentWindowStack().getStackTopWindow();
                if (topWindow != null) {
                    final Rect hitRect = new Rect();
                    final int realX = (int) event.getX();
                    final int realY = (int) event.getY();
                    topWindow.getHitRect(hitRect);
                    if (hitRect.contains(realX, realY)) {
                        mTargetView = topWindow;
                    }
                }
            }
        }

        if (mTouchEventIntercepted) {
            if (mTargetView != null) {
                event.setAction(MotionEvent.ACTION_CANCEL);
                mTargetView.dispatchTouchEvent(event);
                mTargetView = null;
                event.setAction(action);
            }
//            result = onTouchEvent(event);
        } else if (mTargetView != null) {
            result = mTargetView.dispatchTouchEvent(event);
        } else {
            result = false;
        }

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            mTargetView = null;
            mTouchEventIntercepted = false;
        }

        return result;
    }


    public void showWindowLayer() {
        mWinEnvironment.showWindowLayer();
    }

    public void hideWindowLayer() {
        mWinEnvironment.hideWindowLayer();
    }


    private Handler getHandler() {
        if (mHandler == null) {
            mHandler = new HandlEx("ACWindowMgr", Looper.getMainLooper());
        }
        return mHandler;
    }

    
    public void notify(Notification notification) {
    }

}
