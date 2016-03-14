package com.dv.persistnote.base;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;


public class ContextManager {
    // SAFE_STATIC_VAR
    private static Context mContext;
    
    private static Context mAppContext;

    /**
     * initialize Context Manager
     * 
     * @param context
     *            -- this context is share the same life cycle with activity.
     */
    public static void initialize(Context context){
        if ( null != mContext ) {
            Log.i("ContextManager", "mContext is existed");
        }
        mContext = context;
    }

    public static void destroy(){
        mContext = null;
    }

    public static Context getContext(){
    	return mContext;
    }

    public static Resources getResources(){
        if ( null != mContext ) {
            return mContext.getResources();
        } else if ( null != mAppContext ) {
            return mAppContext.getResources();
        } else {
        }
        return null;
    }

    public static AssetManager getAssetManager(){
        if ( null != mContext ) {
            return mContext.getAssets();
        } else if ( null != mAppContext ) {
            return mAppContext.getAssets();
        } else {
        }
        return null;
    }

    public static ContentResolver getContentResolver(){
        if ( null != mContext ) {
            return mContext.getContentResolver();
        } else if ( null != mAppContext ) {
            return mAppContext.getContentResolver();
        } else {
        }
        
        return null;
    }

    public static Window getWindow(){
        if ( null != mContext ) {
            return ((Activity) mContext).getWindow();
        }
        
        return null;
    }

    public static WindowManager getWindowManager(){
        if ( null != mContext ) {
            return ((Activity) mContext).getWindowManager();
        }
        return null;
    }

    public static Object getSystemService(String name){
        if ( null == name ){
            return null;
        }

        return mAppContext.getSystemService(name);
    }

    public static SharedPreferences getSharedPreferences(String name, int mode){
        if ( null != mContext ) {
            return mContext.getSharedPreferences(name, mode);
        } else if (null != mAppContext ) {
            return mAppContext.getSharedPreferences(name, mode);
        } else {
        }
        
        return null;
    }

    public static PackageManager getPackageManager(){

        if ( null != mContext ) {
            return mContext.getPackageManager();
        } else if (null != mAppContext ) {
            return mAppContext.getPackageManager();
        } else {
        }
        return null;
    }

    public static String getPackageName(){
        if ( null != mContext ) {
            return mContext.getPackageName();
        } else if ( null != mAppContext ){
            return mAppContext.getPackageName();
        } else {
        }
        
        return "";
    }

    public static void setRequestedOrientation(int orientation){
        if ( null != mContext ) {
            ((Activity) mContext).setRequestedOrientation(orientation);
        }
    }

    /**
     * This method return the global object of this application.
     * 
     * @return context (this context can use in dialog)
     */
    public static Context getApplicationContext(){
        if ( null == mAppContext && null != mContext ) {
            mAppContext = mContext.getApplicationContext();
            if (mContext.getApplicationContext() == null) {
                mAppContext = mContext;
            }
        }

        return mAppContext;
    }
    
    public static void setApplicationContext(Context context){
        if(context == null) {
            return;
        }
        mAppContext = context; 
    }

    public static LayoutInflater getLayoutInflater(){
        return LayoutInflater.from(mContext);
    }

    public static DisplayMetrics getDisplayMetrics(){
        if ( null != mContext ) {
            return mContext.getResources().getDisplayMetrics();
        } else if ( null != mAppContext ) {
            return mAppContext.getResources().getDisplayMetrics();
        } else {
        }
        return null;
    }
}
