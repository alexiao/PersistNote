
package com.dv.persistnote.base.util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class FileStorageSys
{
    // SAFE_STATIC_VAR
	private static FileStorageSys mInstance;
	private List<String> mListStorageAll = null;
	private List<String> mListStorageAvaliable = null;
	private List<String> mListStorageExternal = null;
	private List<String> mListStorageInternal = null;
	private String mStoragePrimary = null;
	private boolean mExternalStorageWriteable = false;
	
    public static final int SDK_INT_21=7;
    public static final String PATH_SPLIT_DELIMITER = "/";

	private FileStorageSys(Context context) {
		mListStorageAll = new ArrayList<String>();
		mListStorageAvaliable = new ArrayList<String>();
		mListStorageExternal = new ArrayList<String>();
		mListStorageInternal = new ArrayList<String>();
		mStoragePrimary = "";
		
		getSysStorage(context);
	}
	
	public static FileStorageSys initInstance(Context context) {
		if (mInstance == null) {
			mInstance = new FileStorageSys(context);
		}
		return mInstance;
	}
	
	public static FileStorageSys getInstance() {
		if (mInstance == null) {
			throw new IllegalStateException("FileStorageSys is not initialized!");
		}
		return mInstance;
	}
	
	public final static int APILEVEL_12_HONEYCOMB_MR1 = 12;

	private void getSysStorage(Context context) {
		// 这是用常规sdk公开的方法获取存储空间，只能获取到一个
		if (Build.VERSION.SDK_INT >= APILEVEL_12_HONEYCOMB_MR1) {
			getSysStorageByReflect(context);
		} else {
			getSysStorageByNormalAPI();
		}
	}
	
	/**
	 * 调用系统原生支持的API去获取当前默认的存储器
	 */
	private void getSysStorageByNormalAPI() {
		setStoragePrimary();
		setPrimaryStorageIntoList();
	}
	
	/**
	 * 通过反射方式去获取存储器列表
	 */
	private void getSysStorageByReflect(Context context) {
		try {
		// 下面是用一些隐藏的api获取存储空间，可以获得多个存储返回
		// 这个类低版本的API就有，但是其中的getVolumeList方法是API Level 12以上才有
			Object storageManager = context.getSystemService("storage");
		
			// 保存StorageVolume类型对象
			Class<?> storageVolumeClass = null;
			// 这个类需要API Level 12以上才有，而且是@hide的
			String storageVolumeClassName = "android.os.storage.StorageVolume";
			storageVolumeClass = Class.forName(storageVolumeClassName);
			
			Method methodGetVolumeList = null;
			// 这个方法是StorageManager中的@hide方法
			methodGetVolumeList = storageManager.getClass().getDeclaredMethod("getVolumeList", (Class[])null);
			//			methodGetVolumeList = StorageManager.class.getMethod("getVolumeList");
			methodGetVolumeList.setAccessible(true);
			
			// 通过getVolumeList方法取得所有StorageVolume的可用扇区
			Object[] storageVolume = null;
			storageVolume = (Object[]) methodGetVolumeList.invoke(storageManager, new Object[] {});
			
			//取得一个扇区存储路径的方法
			Method methodGetPath = storageVolumeClass.getMethod("getPath");
			methodGetPath.setAccessible(true);
			
			//取得一个扇区是否可移动的方法
			Method methodIsremoveable = storageVolumeClass.getMethod("isRemovable");
			methodIsremoveable.setAccessible(true);
			
			//输出结果
			for (int i = 0; i < storageVolume.length; i++) {
				String path = (String) methodGetPath.invoke(storageVolume[i], new Object[] {});
				boolean removeable = (Boolean) methodIsremoveable.invoke(storageVolume[i], new Object[] {});
				
				mListStorageAll.add(path);
				
				if (removeable) {
					mListStorageExternal.add(path);
				} else {
					mListStorageInternal.add(path);
				}
				
				if (new File(path).canWrite()) {
					mListStorageAvaliable.add(path);
				}
				
				//Log.d("TAG", "Kenlai_getSysStorageByReflect() volume " + i + " path is :" + path + "  ,storageId is:" + Integer.toHexString(storageId) + ", isRemoveable:" + removeable + " ,isEmulated:" + emulated);
			}
			
			setStoragePrimary();
			setPrimaryStorageIntoList();
		}
		catch (Exception e) {
			e.printStackTrace();
			getSysStorageByNormalAPI();
		}
	}
	
	private void setPrimaryStorageIntoList() {
		if (!(this.mStoragePrimary == null || "".equalsIgnoreCase(this.mStoragePrimary))) {
			if (!this.mListStorageAll.contains(this.mStoragePrimary)) {
				// all也补充上以统一数据
				this.mListStorageAll.add(0, this.mStoragePrimary);
			}
			
			if (!this.mListStorageAvaliable.contains(this.mStoragePrimary)) {
				// 确保旧的sdcard一定在available里．
				this.mListStorageAvaliable.add(0, this.mStoragePrimary);
			}
			
			if (!this.mListStorageExternal.contains(this.mStoragePrimary)) {
				// 加到外置列表里
				this.mListStorageExternal.add(0, this.mStoragePrimary);
			}
			
			if (this.mListStorageInternal.contains(this.mStoragePrimary)) {
				// 如果在内置列表里面存在,将其去除.
				this.mListStorageExternal.remove(this.mStoragePrimary);
			}
		}
	}
	
	/**
	 * 重新刷新存储器状态
	 * 
	 * @param context
	 */
	public void refreshStorageState(Context context) {
		mListStorageAll.clear();
		mListStorageAvaliable.clear();
		mListStorageExternal.clear();
		mListStorageInternal.clear();
		getSysStorage(context);
	}
	
	/**
	 * 获取所有存储器列表(包含模拟存储器)
	 * 
	 * @return
	 */
	public List<String> getStorageListAll() {
		return mListStorageAll;
	}
	
	/**
	 * 获取所有可用存储器列表(不包含模拟存储器)
	 * 
	 * @return
	 */
	public List<String> getStorageListAvailable() {
		return mListStorageAvaliable;
	}
	
	/**
	 * 返回所有外置可移动存储器列表
	 * 
	 * @return
	 */
	public List<String> getStorageExternal() {
		return mListStorageExternal;
	}
	
	/**
	 * 返回所有内置不可移动存储器列表
	 * 
	 * @return
	 */
	public List<String> getStorageInternal() {
		return mListStorageInternal;
	}
	
	/**
	 * 获取系统默认主存储器
	 * 
	 * @return
	 */
	public String getStoragePrimary() {
		return mStoragePrimary;
	}
	
	/**
	 * 设置系统默认存储器内容
	 */
	private void setStoragePrimary() {
	    String externalStorageState = null;
	    try {
	        externalStorageState = Environment.getExternalStorageState();
	    } catch (Exception e) {
	        // see bug 0311362, Environment.getExternalStorageState() may cause NPE
	    }
		if (Environment.MEDIA_MOUNTED.equalsIgnoreCase(externalStorageState)) {
			mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equalsIgnoreCase(externalStorageState)) {
			mExternalStorageWriteable = false;
		}
		
		mStoragePrimary = Environment.getExternalStorageDirectory().getAbsolutePath();
	}
	
	/**
	 * 判断系统默认主存储器是否可读可写
	 * 
	 * @return
	 */
	public boolean isDefaultStorageWriteable() {
		return mExternalStorageWriteable;
	}
}
