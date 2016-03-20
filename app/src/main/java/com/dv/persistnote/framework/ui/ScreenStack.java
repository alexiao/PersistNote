package com.dv.persistnote.framework.ui;

import java.util.ArrayList;
import java.util.Stack;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.dv.persistnote.base.util.HardwareUtil;
import com.dv.persistnote.framework.core.INotify;
import com.dv.persistnote.framework.core.Notification;
import com.dv.persistnote.framework.core.NotificationCenter;
import com.dv.persistnote.framework.core.NotificationDef;


@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
public class ScreenStack extends FrameLayout implements INotify {

    public static final String TAG = "ACWindowStack";
    public static final boolean DEBUG = false;

    private static final int GC_DELAY = 100;
    
    private static final int ANIMATOR_DURATION = 300;

    private AbstractScreen mRootWindow;
    private AbstractScreen mFrontWin;
    private AbstractScreen mBackWin;
    private Stack<AbstractScreen> mViewsStack = new Stack<AbstractScreen>();

    private boolean mIsPushing;
    private boolean mIsPoping;
    
    private OnHierarchyChangeListener mHierarchyChangeListener;
    private ArrayList<Runnable> mRunnables = new ArrayList<Runnable>();
    /**
     * 当正在处于子View绘制期间的标志位
     */
    private boolean mIsDispatchDrawing = false;
    
    private Runnable mCleanUpAnimationRunnable = new Runnable() {
        @Override
        public void run() {
            cleanUpAnimation();
        }
    };
    
    public ScreenStack(Context context) {
        super(context);
    }
    
    public ScreenStack(Context context, AbstractScreen rootWindow) {
        super(context);
        assert (null != rootWindow);
        mRootWindow = rootWindow;
        mFrontWin = mRootWindow;
        addView(rootWindow);
        mViewsStack.push(mFrontWin);
        
        NotificationCenter.getGlobalInstance().register(this, NotificationDef.N_FULL_SCREEN_MODE_CHANGE);
        NotificationCenter.getGlobalInstance().register(this, NotificationDef.N_ORIENTATION_CHANGE);
        
    }
    
    AbstractScreen getRootWindow() {
        return mRootWindow;
    }

    void replaceRootWindow(AbstractScreen newRootWindow) {
        removeView(mRootWindow);
        mRootWindow = newRootWindow;
        mRootWindow.onWindowStateChange(AbstractScreen.STATE_ON_SHOW);
        mViewsStack.set(0, mRootWindow);
        addView(newRootWindow, 0);
        //free memory
        mFrontWin = null;
        mBackWin = null;
    }

    AbstractScreen getStackTopWindow() {
        return mViewsStack.peek();
    }
    
    public boolean removeStackView(AbstractScreen window) {
        return mViewsStack.remove(window);
    }
    
    public AbstractScreen getWindow(int index) {
        return mViewsStack.elementAt(index);
    }
    
    public int getWindowCount() {
        return mViewsStack.size();
    }
    
	void pushSingleTopWindow(AbstractScreen w, boolean animated) {
        AbstractScreen frontWin = w;
        AbstractScreen backWin = mViewsStack.peek();
        if (frontWin.getClass().equals(backWin.getClass())) {
            return;
        }
        for (AbstractScreen window : mViewsStack) {
            if (window.getClass().equals(w.getClass())) {
                mViewsStack.remove(window);
                removeView(window);
                break;
            }
        }

        pushScreen(w, animated);
    }
	
	void pushScreen(AbstractScreen w, boolean animated) {
	    pushScreen(w, animated, true, true);
	}
	
    void pushScreen(AbstractScreen w, boolean animated, boolean notifyFrontWindow, boolean notifyBackWindow) {
        if (w.getParent() != null)
            return;
        
        ensureAnimationFinished();
    
        mFrontWin = w;
        mBackWin = mViewsStack.peek();

        if (!mFrontWin.isTransparent()) {
            if (animated) {
                mFrontWin.setEnableBackground(true);
            }
        }
        
        if (mFrontWin.getVisibility() != View.VISIBLE) {
            mFrontWin.setVisibility(View.VISIBLE);
        }
      
        addView(w);

        if (animated) {
            if (notifyFrontWindow) {
                mFrontWin.onWindowStateChange(AbstractScreen.STATE_BEFORE_PUSH_IN);
            }
            if (notifyBackWindow) {
                mBackWin.onWindowStateChange(AbstractScreen.STATE_BEFORE_POP_OUT);
            }
            mViewsStack.push(mFrontWin);
            if (notifyFrontWindow) {
                mFrontWin.onWindowStateChange(AbstractScreen.STATE_ON_ATTACH);
            }
            startPushAnimation(); 
        } else {
            if (notifyFrontWindow) {
                mFrontWin.onWindowStateChange(AbstractScreen.STATE_ON_SHOW);
            }
            if (notifyBackWindow) {
                mBackWin.onWindowStateChange(AbstractScreen.STATE_ON_HIDE);
            }
            if (!mFrontWin.isTransparent()) {
                mBackWin.setVisibility(View.INVISIBLE);
            }
            if (mFrontWin.isSingleTop()) {
                mBackWin.setVisibility(GONE);
            }
            
            mViewsStack.push(mFrontWin);
            if (notifyFrontWindow) {
                mFrontWin.onWindowStateChange(AbstractScreen.STATE_ON_ATTACH);
            }
            
            mFrontWin = null;
            mBackWin = null;
        }
    }
       
    void popScreen(boolean animated) {
        if (mViewsStack.size() <= 1) {
            return;
        }
        
        ensureAnimationFinished();    
        
        mFrontWin = mViewsStack.pop();
        mBackWin = mViewsStack.peek();
        if (mFrontWin == mRootWindow || mFrontWin == null) {
            return;
        }
        
        Log.d(TAG, "popScreen: " + mFrontWin);
        
        if (!mFrontWin.isTransparent()) {
            if (animated ) {
                mFrontWin.setEnableBackground(true);
                mFrontWin.invalidate();
            }
        }
        
        if (mBackWin.getVisibility() != View.VISIBLE) {
            mBackWin.setVisibility(View.VISIBLE);
        }
        
        if (animated) {           
            mFrontWin.onWindowStateChange(AbstractScreen.STATE_BEFORE_POP_OUT);
            mBackWin.onWindowStateChange(AbstractScreen.STATE_BEFORE_PUSH_IN);
            startPopAnimation();
        } else {
            ViewGroup.LayoutParams lp = mFrontWin.getLayoutParams();
            if (lp != null && lp instanceof WindowManager.LayoutParams) {
                ((WindowManager.LayoutParams)lp).windowAnimations = 0;
                if (mFrontWin.getParent() != null) {
                }
            }
            mFrontWin.onWindowStateChange(AbstractScreen.STATE_ON_HIDE);
            mBackWin.onWindowStateChange(AbstractScreen.STATE_ON_SHOW);
           
            removeView(mFrontWin);
            mFrontWin.onWindowStateChange(AbstractScreen.STATE_ON_DETACH);
            
            mFrontWin = null;
            mBackWin = null;
        }        
    }

     void popToRootWindow(boolean animated) {
         Log.d(TAG, "===PopToRootWindow");
        int count = mViewsStack.size();
        if (count == 1) {
            return;
        }

        AbstractScreen w;
        // remove all the views except the top most view and the bottom view
        for (int i = count - 2; i > 0; i--) {
            w = mViewsStack.remove(i);
            Log.d(TAG, "PopToRootWindow: " + w);
            removeView(w);
            w.onWindowStateChange(AbstractScreen.STATE_ON_DETACH);
        }

        popScreen(animated);
    }
        
    /**
     * blockMeasureLayout to optimize.
     * there are 3 kinds of situations the has to reMeasure/layout:
     * 1. first create
     * 2. orientation change
     * 3. full screen toggle
     * the rest of all the other situations is ignore
     */
    private boolean mBolckMeasureLayout = false;
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mBolckMeasureLayout && getVisibility() == INVISIBLE) {
            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
            return;
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
            int bottom) {
        if (mBolckMeasureLayout && getVisibility() == INVISIBLE) {
            return;
        }
        super.onLayout(changed, left, top, right, bottom);
    }
    
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        mBolckMeasureLayout = true;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        mIsDispatchDrawing = true;
        super.dispatchDraw(canvas);
        mBolckMeasureLayout = true;
        mIsDispatchDrawing = false;
    }

    @Override
    public void notify(Notification notification) {
        if (notification.id == NotificationDef.N_FULL_SCREEN_MODE_CHANGE) {
            mBolckMeasureLayout = false;
        } else if (notification.id == NotificationDef.N_ORIENTATION_CHANGE) {
            mBolckMeasureLayout = false;
        }
    }
    
    private void startPushAnimation() {

        Animation pushAnimation = mFrontWin.getPushAnimation();

        if (pushAnimation != null) {
            pushAnimation.setAnimationListener(new AnimationListener() {
                
                @Override
                public void onAnimationStart(Animation animation) {
                }
                
                @Override
                public void onAnimationRepeat(Animation animation) {}
                
                @Override
                public void onAnimationEnd(Animation animation) {  
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            dealWithWindowAfterPush();
                            mRunnables.remove(this);
                        }
                    };
                    mRunnables.add(runnable);
                    post(runnable);
                }
                
            });
            mIsPushing = true;
            mFrontWin.startAnimation(pushAnimation);
        } else {
          final ViewPropertyAnimator anim = mFrontWin.animate();
          anim.cancel();
          mFrontWin.setTranslationX(getWidth() * 0.8f);
          anim.translationX(0);
          anim.setDuration(ANIMATOR_DURATION);
          anim.setInterpolator(new DecelerateInterpolator());
          anim.setListener(new AnimatorListenerAdapter() {
              boolean isCanceled = false;
              
              @Override
              public void onAnimationCancel(Animator animator) {
                  if (!isCanceled) {
                      isCanceled = true;
                      post(mCleanUpAnimationRunnable);
                  }
              }
  
              @Override
              public void onAnimationEnd(Animator animator) {
                  if (!isCanceled) {
                      Runnable runnable = new Runnable() {
                          @Override
                          public void run() {
                              HardwareUtil.setLayerType(mFrontWin, HardwareUtil.LAYER_TYPE_NONE);
                              dealWithWindowAfterPush();
                              mRunnables.remove(this);
                          }
                      };
                      mRunnables.add(runnable);
                      post(runnable);
                  }
              }
          });
          mIsPushing = true;
          HardwareUtil.setLayerType(mFrontWin, HardwareUtil.LAYER_TYPE_HARDWARE);
          HardwareUtil.buildLayer(mFrontWin);
          anim.start();
        }
    }
    
    private void startPopAnimation() {
        Animation popAnimation = mFrontWin.getPopAnimation();

        if (popAnimation != null) {
            popAnimation.setAnimationListener(new AnimationListener() {
                
                @Override
                public void onAnimationStart(Animation animation) {}
                
                @Override
                public void onAnimationRepeat(Animation animation) {}
                
                @Override
                public void onAnimationEnd(Animation animation) {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            dealWithWindowAfterPop();
                            mRunnables.remove(this);
                        }
                    };
                    mRunnables.add(runnable);
                    post(runnable);
                }
            });
            mIsPoping = true;
            mFrontWin.startAnimation(popAnimation);
        } else {
            final ViewPropertyAnimator anim = mFrontWin.animate();
            anim.cancel();
            mFrontWin.setTranslationX(0);
            anim.translationX(getWidth());
            anim.setDuration(ANIMATOR_DURATION);
            anim.setInterpolator(new DecelerateInterpolator());
            anim.setListener(new AnimatorListenerAdapter() {
                boolean isCanceled = false;
                
                @Override
                public void onAnimationCancel(Animator animator) {
                    if (!isCanceled) {
                        isCanceled = true;
                        post(mCleanUpAnimationRunnable);
                    }
                }
    
                @Override
                public void onAnimationEnd(Animator animator) {
                    if (!isCanceled) {
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                HardwareUtil.setLayerType(mFrontWin, HardwareUtil.LAYER_TYPE_NONE);
                                dealWithWindowAfterPop();
                                mRunnables.remove(this);
                            }
                        };
                        mRunnables.add(runnable);
                        post(runnable);
                    }
                }
            });
            mIsPoping = true;
            HardwareUtil.setLayerType(mFrontWin, HardwareUtil.LAYER_TYPE_HARDWARE);
            HardwareUtil.buildLayer(mFrontWin);
            anim.start();
        }
    }
    
    /**
     * 切入动画 结束后的处理
     */
    private void dealWithWindowAfterPush(){
        cleanUpAnimation();
        if (mFrontWin != null && mBackWin != null) {                    
            if (!mFrontWin.isTransparent()) {
                mBackWin.setVisibility(View.INVISIBLE);
            }
            mBackWin.onWindowStateChange(AbstractScreen.STATE_AFTER_POP_OUT);
            mFrontWin.onWindowStateChange(AbstractScreen.STATE_AFTER_PUSH_IN);
            
            if (mFrontWin.isSingleTop()) {
                mBackWin.setVisibility(GONE);
            }
        }               
        mIsPushing = false;
        mFrontWin = null;
        mBackWin = null;
    }

    /**
     * 切出动画 结束后的处理，包括更改状态，removeViews等
     */
    private void dealWithWindowAfterPop() {
        cleanUpAnimation();
        if (mFrontWin != null && mBackWin != null) {    
            mBackWin.onWindowStateChange(AbstractScreen.STATE_AFTER_PUSH_IN);
            mFrontWin.onWindowStateChange(AbstractScreen.STATE_AFTER_POP_OUT);
            
            removeView(mFrontWin);
            mFrontWin.onWindowStateChange(AbstractScreen.STATE_ON_DETACH);
        }
        mIsPoping = false;
        mFrontWin = null;
        mBackWin = null;
    }
    
    /**
     * 确保上一个动画已经结束，不影响新的动画
     */
    private void ensureAnimationFinished(){
        if (mRunnables.size() > 0) {
            for (Runnable runnable : mRunnables) {
                removeCallbacks(runnable);
            }
            mRunnables.clear();
        }
        
        if (!mIsPushing && !mIsPoping) {
            //Pushing,Poping这两种情况以及分别在相应的处理方法中调用了cleanUpAnimation
            //这里只是为了处理异常情况
            cleanUpAnimation();
        }
        
        if(mIsPushing){
            dealWithWindowAfterPush();
        }
        
        if(mIsPoping){
            dealWithWindowAfterPop();
        }

    }
    
    private void cleanUpAnimation() {
        if (mFrontWin != null) {
            mFrontWin.setAnimation(null);
            mFrontWin.animate().cancel();
            mFrontWin.setTranslationX(0);
            mFrontWin.setTranslationY(0);
        }
        
        if (mBackWin != null) {
            mBackWin.setAnimation(null);
            mBackWin.animate().cancel();
            mBackWin.setTranslationX(0);
            mBackWin.setTranslationY(0);
        }
        removeCallbacks(mCleanUpAnimationRunnable);
    }
    
    public void replaceWindow(AbstractScreen window, AbstractScreen replaceWindow) {
        if (window == null || replaceWindow == null) {
            return;
        }
        
        for (int index = 0; index < getChildCount(); ++index) {
            View view = getChildAt(index);
            if (view == window) {
                addView(replaceWindow, index);
                removeView(window);
                return;
            }
        }
    }

    public boolean isWindowAnimating() {
        return mIsPushing || mIsPoping;
    }
}
