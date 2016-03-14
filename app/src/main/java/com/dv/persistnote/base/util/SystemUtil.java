package com.dv.persistnote.base.util;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.dv.persistnote.base.ContextManager;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.HashMap;


public class SystemUtil {


    private static int sStatusBarHeight;
    private static boolean sHasCheckStatusBarHeight;
    
    public static int getStatusBarHeight(Context context) {
        if (sHasCheckStatusBarHeight) {
            return sStatusBarHeight;
        }
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object o = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = (Integer) field.get(o);
            sStatusBarHeight = context.getResources().getDimensionPixelSize(x);
            sHasCheckStatusBarHeight = true;
        } catch (Exception e) {
            sStatusBarHeight = guessStatusBarHeight(context);
            sHasCheckStatusBarHeight = true;
        }
        return sStatusBarHeight;
    }
    
    private static int guessStatusBarHeight(Context context) {
        try {
            if (context != null) {
                final int statusBarHeightDP = 25;
                float density = context.getResources().getDisplayMetrics().density;
                return Math.round(density*statusBarHeightDP);
            }
        } catch (Exception e) {
        }
        return 0;
    }
    
    private static int sNaviBarHeight = -1;
    public static int getNaviBarHeight(Context context) {
        if (sNaviBarHeight > 0) {
            return sNaviBarHeight;
        }
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object o = c.newInstance();
            Field field = c.getField("navigation_bar_height");
            int x = (Integer) field.get(o);
            sNaviBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            final int dp = 48;
            float density = context.getResources().getDisplayMetrics().density;
            sNaviBarHeight = Math.round(density * dp);
        }
        return sNaviBarHeight;
    }
    
    private static int sVerticalNaviBarHeight = -1;
    public static int getVerticalNaviBarHeight(Context context) {
        if (sVerticalNaviBarHeight > 0) {
            return sVerticalNaviBarHeight;
        }
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object o = c.newInstance();
            Field field = c.getField("navigation_bar_width");
            int x = (Integer) field.get(o);
            sVerticalNaviBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            final int dp = 42;
            float density = context.getResources().getDisplayMetrics().density;
            sVerticalNaviBarHeight = Math.round(density * dp);
        }
        return sVerticalNaviBarHeight;
    }
    
    public static boolean isResolutionHigherThanQHD(int width, int height) {
		return Math.max(width, height) >= 960 
				&& Math.min(width, height) >= 540;
	}
	
	public static boolean isResolutionHigherThanWVGA(int width, int height) {
        return Math.max(width, height) >= 800 
                && Math.min(width, height) >= 480;
    }

    private static boolean gIsStatusBarHidden;
    
    /**
     * 系统通知栏是否隐藏
     */
    public static boolean isStatusBarHidden() {
        return gIsStatusBarHidden;
    }
    
    public static void setStatusBarHidden(boolean isStatusBarHidden) {
        gIsStatusBarHidden = isStatusBarHidden;
    }
    
    /**
     * @return The screen backlight brightness between 0 and 255.
     */
    static int getSystemBrightness(Context context) {
        int brightness = 0;
        ContentResolver contentResolver = context.getContentResolver();
        try {
            brightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, -1);
        } catch (Exception e) {
        }
        return brightness;
    }

    /**
     * Return application icon bitmap by package name.
     * @param packageName Request application package name. 
     * @return If the application got by package name is valid, then return the icon,
     *         else return null.
     */
    static Bitmap getInstalledAppIcon(Context context, String packageName) {
        if (context == null || packageName == null || "".equals(packageName.trim())) {
            return null;
        }

        PackageManager pm = context.getPackageManager();
        try {
            Drawable drawable = pm.getApplicationIcon(packageName);
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            }
        } catch (Throwable e) {
        }
        return null;
    }

    static boolean installApkFile(Context context, String filePath) {
        if(context == null || filePath == null || "".equals(filePath.trim())) {
            return false;
        }

        try {
            File apkFile = new File(filePath);
            if(!apkFile.exists()) {
                return false;
            }

            Intent i = new Intent();
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            i.setAction(Intent.ACTION_VIEW);
            i.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            context.startActivity(i);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    static boolean uninstallPackage(Context context, String packageName) {
        if(context == null || packageName == null || "".equals(packageName.trim())) {
            return false;
        }

        try {
            Uri packageURI = Uri.parse("package:" + packageName);
            Intent i = new Intent(Intent.ACTION_DELETE, packageURI);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private static int getWindowWidth(Context context) {
        final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int deviceWidth = Math.min(dm.widthPixels, dm.heightPixels);
        return deviceWidth;
    }
    

    private static HashMap<String, SimpleDateFormat> mSimpleDateFormatCache = new HashMap<String, SimpleDateFormat>();
    public static SimpleDateFormat getSimpleDateFormat(String format) {
        if (!ThreadManager.isMainThread()) {
            return new SimpleDateFormat(format);
        }
        
        SimpleDateFormat sdf = mSimpleDateFormatCache.get(format);
        if (sdf == null) {
            sdf = new SimpleDateFormat(format);
            mSimpleDateFormatCache.put(format, sdf);
        }
        
        return sdf;
    }
    
    public static long getFreeMemory() {
        MemoryInfo mi = new MemoryInfo();
        ActivityManager activityManager = (ActivityManager) ContextManager.getApplicationContext().getSystemService(Activity.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        long availableMegs = mi.availMem / 1048576L;
        return availableMegs;
    }


}
