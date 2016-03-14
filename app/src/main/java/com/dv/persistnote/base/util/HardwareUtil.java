package com.dv.persistnote.base.util;



import android.content.Context;
import android.graphics.Paint;
import android.opengl.GLES10;
import android.os.Build;
import android.os.Debug;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.microedition.khronos.opengles.GL10;

public class HardwareUtil {
    // SAFE_STATIC_VAR
    private static Context sContext = null;
    
    private static final boolean DEBUG = false;
    private static final String TAG = "HardwareUtil";
    
    public static final int LAYER_TYPE_NONE = 0;
    public static final int LAYER_TYPE_SOFTWARE = 1;
    public static final int LAYER_TYPE_HARDWARE = 2;
    
    private static final String CPU_INFO_CORE_COUNT_FILE_PATH = "/sys/devices/system/cpu/";
    private static final String CPU_INFO_MAX_FREQ_FILE_PATH = 
            "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq";
    private static final String MEMORY_INFO_PATH = "/proc/meminfo";
    private static final String MEMORY_OCUPIED_INFO_PATH = "/proc/self/status";
    private static final String MEMORY_INFO_TAG_VM_RSS = "VmRSS:";
    private static final String MEMORY_INFO_TAG_VM_DATA = "VmData:";
    public static final String FILE_IMEI = "8B277D535A8C846BDDD370A589B9D93C3B2B6247";
    
    /**
     * 这是在7台不同尺寸和分辨率的手机上测试得出的一个估计值。
     * 原先我们使用densityDpi来计算尺寸，但densityDpi是一个大概的值，不够精确；
     * 现在增加采用xdpi和ydpi两个来计算的结果。当两种计算结果的差距不大于下面这个值时，
     * 我们认同后一种计算方式；否则仍采用旧的方式的计算结果。
     */
    private static final double COMPUTE_SCREENSIZE_DIFF_LIMIT = 0.5f;

    
    // SAFE_STATIC_VAR
    private static boolean sHasInitedAndroidId = false;
    // SAFE_STATIC_VAR
    private static String sAndroidId = "";
    // SAFE_STATIC_VAR
    private static boolean sHasInitMacAddress = false;
    // SAFE_STATIC_VAR
    private static String sMacAddress = "";
    // SAFE_STATIC_VAR
    private static boolean sHasInitIMEI = false;
    // SAFE_STATIC_VAR
    private static String sIMEI = "";
    // SAFE_STATIC_VAR
    private static boolean sHasInitCpuCoreCount = false;
    // SAFE_STATIC_VAR
    private static int sCpuCoreCount = 1;
    // SAFE_STATIC_VAR
    private static boolean sHasInitMaxCpuFrequence = false;
    // SAFE_STATIC_VAR
    private static int sMaxCpuFrequence = 0;
    // SAFE_STATIC_VAR
    private static boolean sHasInitCpuArch = false;
    // SAFE_STATIC_VAR
    private static String sCpuArch = "";
    // SAFE_STATIC_VAR
    private static boolean sHasInitTotalMemory = false;
    // SAFE_STATIC_VAR
    private static long sTotalMemory = 0;
    // SAFE_STATIC_VAR
    private static boolean sHasInitDeviceSize = false;
    // SAFE_STATIC_VAR
    private static double sDeviceSize = 0;
    
    private static boolean sHasInitCpuInfo = false;
    private static String sCpuInfoArch = "";
    private static String sCpuInfoVfp = "";
    private static String sCpuArchit = "";

    /**
     * 是否支持HEVC，lentp格式图片解码时要使用
     * */
    private static int sSupportHEVC = -1;
    public static final int HEVC_NOTSUPPORT = 0;
    public static final int HEVC_SUPPORT = 1;

    /**
     * screenWidth & screenHeight means the display resolution.
     * windowWidth & windowHeight means the application window rectangle.
     * in not full screen mode:
     * screenHeight == windowHeight + systemStatusBarHeight
     * in full screen mode: 
     * screenHeight == windowHeight
     * 
     * no matter what situation, screenWidth === windowWidth;
     */
    // SAFE_STATIC_VAR
    public static int screenWidth, screenHeight;
    // SAFE_STATIC_VAR
    public static int windowWidth, windowHeight;
    public static float density = 1.0f; //屏幕密度
    
    /**
     * @note You must call this before calling any other methods!!
     * @param context 
     */
    public static void initialize(Context context) {
        if (context != null) {
            sContext = context.getApplicationContext();
        }
    }
    
    /**
     * Call this to clear reference of Context instance, which is set by {@link #initialize(Context)}.
     */
    public static void destroy() {
        sContext = null;
    }
    
    private static void checkIfContextInitialized() {
        if (sContext == null) {
            throw new RuntimeException("context has not been initialized! You MUST call this only after initialize() is invoked.");
        }
    }
    
    
    /**
     * @return A 64-bit number (as a hex string) that is randomly generated on the device's first boot and 
     * should remain constant for the lifetime of the device. (The value may change if a factory reset is
     * performed on the device
     */
    public static String getAndroidId() {
        checkIfContextInitialized();
        if (sHasInitedAndroidId) {
            return sAndroidId;
        }
        
        try {
            sAndroidId = Settings.Secure.getString(sContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
             
        }
        
        if (sAndroidId == null) {
            sAndroidId = "";
        }
        sHasInitedAndroidId = true;
        if (DEBUG) {
            Log.i(TAG, "getAndroidId: " + sAndroidId);
        }
        return sAndroidId;
    }

    public static String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> allInterface = NetworkInterface.getNetworkInterfaces();
            if (allInterface == null) {
                return null;
            }
            InetAddress foundAddr = null;
            NetworkInterface foundInerface = null;

            for (; allInterface.hasMoreElements();) {
                NetworkInterface element = allInterface.nextElement();
                if (element == null) {
                    continue;
                }
                Enumeration<InetAddress> enumIpAddr = element.getInetAddresses();
                if (enumIpAddr == null) {
                    continue;
                }

                for (; enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress == null) {
                        continue;
                    }
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                        // 1、p2p直连网络ip最高优化使用
                        // 2、wifi网络ip次高优先使用
                        // 3、如果不是wifi网络，第一个找到的ip当成当前ip
                        if (null == foundAddr) {
                            foundAddr = inetAddress;
                            foundInerface = element;
                        } else {
                            String foundName = foundInerface.getName();
                            String newName = element.getName();
                            if (foundName != null && !foundName.contains("p2p") && newName != null
                                    && (newName.contains("p2p"))) {
                                foundAddr = inetAddress;
                                foundInerface = element;
                            } else if (foundName != null && !foundName.contains("wlan") && !foundName.contains("p2p")
                                    && newName != null && (newName.contains("wlan"))) {
                                // 如果当前找到不是wifi，而下一个是wifi网络，则使用下个替换当前找到
                                foundAddr = inetAddress;
                                foundInerface = element;
                            }
                        }
                    }
                }
            }

            if (foundAddr != null) {
                return foundAddr.getHostAddress();
            }

        } catch (SocketException ex) {
             
        }
        return null;
    }
    
    public static int getCpuCoreCount() {
         if (sHasInitCpuCoreCount) {
             return sCpuCoreCount;
         }

         final class CpuFilter implements FileFilter {
             @Override
             public boolean accept(File pathname) {
                 try {
                     if(Pattern.matches("cpu[0-9]+", pathname.getName())) {
                         return true;
                     }
                 } catch (Throwable t) {
                      
                 }
                 return false;
             }      
         }

         try {
             File dir = new File(CPU_INFO_CORE_COUNT_FILE_PATH);
             File[] files = dir.listFiles(new CpuFilter());
             sCpuCoreCount = files.length;
         } catch(Throwable e) {
              
         }

         if (sCpuCoreCount < 1) {
             sCpuCoreCount = 1;
         }
         sHasInitCpuCoreCount = true;
         if (DEBUG) {
             Log.i(TAG, "getCpuCoreCount: " + sCpuCoreCount);
         }
         return sCpuCoreCount;
    }
    
    public static int getMaxCpuFrequence() {
         if (sHasInitMaxCpuFrequence) {
             return sMaxCpuFrequence;
         }

         FileReader fr = null;
         BufferedReader br = null;
         try {
             fr = new FileReader(CPU_INFO_MAX_FREQ_FILE_PATH);
             br = new BufferedReader(fr);
             String text = br.readLine();
             if (text != null) {
                 sMaxCpuFrequence = Integer.parseInt(text.trim());
             }
         } catch (Exception e) {
              
         } finally {
             if (br != null) {
                 try {
                     br.close();
                 } catch (IOException e) {
                      
                 }
             }
             if (fr != null) {
                 try {
                     fr.close();
                 } catch (IOException e) {
                      
                 }
             }
         }

         if (sMaxCpuFrequence < 0) {
             sMaxCpuFrequence = 0;
         }
         sHasInitMaxCpuFrequence = true;
         if (DEBUG) {
             Log.i(TAG, "getMaxCpuFrequence: " + sMaxCpuFrequence + " Hz");
         }
         return sMaxCpuFrequence;
    }

    /**
     * 
        Processor       : ARMv7 Processor rev 0 (v7l)
        processor       : 0
        BogoMIPS        : 996.14
        
        processor       : 1
        BogoMIPS        : 996.14
        
        Features        : swp half thumb fastmult vfp edsp vfpv3 vfpv3d16
        CPU implementer : 0x41
        CPU architecture: 7
        CPU variant     : 0x1
        CPU part        : 0xc09
        CPU revision    : 0
        
        Hardware        : star
        Revision        : 0000
        Serial          : 0000000000000000
     */
    private static void initCpuInfo() {
        if (sHasInitCpuInfo) {
            return;
        }
        BufferedReader bis = null;
        try {
            bis = new BufferedReader(new FileReader(new File("/proc/cpuinfo")));
            HashMap<String, String> cpuInfoMap = new HashMap<String, String>();
            String line;
            while ((line = bis.readLine()) != null) {
                line = line.trim();
                if (line.length() > 0) {
                    String[] pairs = line.split(":");
                    if (pairs.length > 1) {
                        cpuInfoMap.put(pairs[0].trim(), pairs[1].trim());
                    }
                }
            }
            
            String processor = cpuInfoMap.get("Processor");
            if(processor != null){
                int index1 = processor.indexOf("(");
                int index2 = processor.lastIndexOf(")");
                int len = index2 - index1;
                if(index1 > 0 && index2 > 0 &&len > 0){
                    sCpuInfoArch = processor.substring(index1+1, index2);
                }else{
                    sCpuInfoArch = "v"+cpuInfoMap.get("CPU architecture");
                }
            }
            sCpuInfoVfp = cpuInfoMap.get("Features");
            sCpuArchit = cpuInfoMap.get("CPU part");
            sHasInitCpuInfo = true;
        } catch (Exception e) {
             
        } finally {
            try {
                if (bis != null)
                    bis.close();
            } catch (IOException e) {
                 
            }
        }
    }
    
    /**
     *  从/proc/cpuinfo解释arch
     * @return
     */
    public static String getCpuInfoArch(){
        initCpuInfo();
        return sCpuInfoArch;
    }
    /**
     *  从/proc/cpuinfo解释archit
     * @return
     */
    public  static String getCpuInfoArchit(){
        initCpuInfo();
        return sCpuArchit;
    }
    
    /**
     * 从/proc/cpuinfo解释vfp
     * @return
     */
    public static String getCpuInfoVfp(){
        initCpuInfo();
        return sCpuInfoVfp;
    }
    
    /**
     * @return device total memory (KB)
     */
    public static long getTotalMemory() {
        if (sHasInitTotalMemory) {
            return sTotalMemory;
        }

        final int bufferSize = 8192; //设置一个缓存大小
        try {
            FileReader fr = new FileReader(MEMORY_INFO_PATH);
            BufferedReader br = new BufferedReader(fr, bufferSize);
            String memory = br.readLine(); // 读取meminfo第一行，系统总内存大小  , 得到类似"MemTotal:  204876 kB"的string
            if (memory != null) {
                String[] arrayOfString = memory.split("\\s+");
                if (arrayOfString != null && arrayOfString.length > 1 && arrayOfString[1] != null) {
                    sTotalMemory = Long.parseLong(arrayOfString[1].trim());// 获得系统总内存，单位是KB
                }
            }
            br.close();
            fr.close();
        } catch (Exception e) {
             
        }

        if (sTotalMemory < 0) {
            sTotalMemory = 0;
        }
        sHasInitTotalMemory = true;
        if (DEBUG) {
            Log.i(TAG, "getTotalMemory: " + sTotalMemory + " KB");
        }
        return sTotalMemory;
    }
    

    /**
     * 通过Runtime和Debug类获取程序占用的Java堆内存大小
     * @return 单位：byte
     */
    public static long getJavaHeapSize() {
         if (Build.VERSION.SDK_INT >= 9) {
             //2.3以上ROM只计算totalMemory
             return Runtime.getRuntime().totalMemory();
         } else {
             //2.3以及以下ROM计算totalMemory+NativeHeap
             return Runtime.getRuntime().totalMemory() + Debug.getNativeHeapAllocatedSize();
         }
    }
    
    /**
     * @return 屏幕尺寸的一个估计值 (单位：英寸)
     */
    public static double getDeviceSize() {
        checkIfContextInitialized();
//        return HardwareUtilImpl.getDeviceSize(sContext);
        if (sHasInitDeviceSize || sContext == null) {
            return sDeviceSize;
        }

        /**
         * TODO: 这些值的读取如何做优化？
         */
        final DisplayMetrics dm = new DisplayMetrics();
        final WindowManager wm = (WindowManager) sContext.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        final int width = guessSolutionValue(dm.widthPixels);
        final int height = guessSolutionValue(dm.heightPixels);
        final float dpi = dm.densityDpi;
        final float xdpi = dm.xdpi;
        final float ydpi = dm.ydpi;
        
        double screenSize = 0;
        if (dpi != 0) {
            screenSize = Math.sqrt(width * width + height * height) / dpi;
        }
        
        double screenSize2 = 0;
        if (xdpi != 0 && ydpi != 0) {
            double widthInches = width / xdpi;
            double heightInches = height / ydpi;
            screenSize2 = Math.sqrt(widthInches * widthInches + heightInches * heightInches);
        }
        
        final double diff = Math.abs(screenSize2 - screenSize);
        sDeviceSize = diff <= COMPUTE_SCREENSIZE_DIFF_LIMIT ? screenSize2 : screenSize;
        
        sHasInitDeviceSize = true;

        Log.i(TAG, "Kenlai_getDeviceSize(): " + sDeviceSize + " inches");
        return sDeviceSize;
    }
    

    /**
     * 获取OpenGL支持的最大纹理尺寸
     * @return
     */
    public static int getGlMaxTextureSize(){
        int[] maxTextureSize = new int[1];
        GLES10.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
        return maxTextureSize[0];
    }

   private static int getWindowWidth(Context context) {
        final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int deviceWidth = Math.min(dm.widthPixels, dm.heightPixels);
        return deviceWidth;
    }
    
    /**
     * 获取设备的短边
     * @return
     */
    public static int getDeviceWidth() {
        return screenWidth < screenHeight ? screenWidth : screenHeight;
    }

    /**
     * 获取设备的长边
     * @return
     */
    public static int getDeviceHeight() {
        return screenWidth > screenHeight ? screenWidth : screenHeight;
    }
    
    private static int guessSolutionValue(int value) {
        if (value >= 1180 && value <= 1280) {
            return 1280;
        }
        return value;
    }
    
    
    /**
     * 获取系统是否root了
     * @return
     */
    public static boolean hasRoot() {
        final String rootFileOne = "system/bin/su";
        final String rootFileTwo = "system/xbin/su";
        File file = new File(rootFileOne);
        if (file.exists()) {
            return true;
        }
        file = new File(rootFileTwo);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    public static void setSupportHEVC(int supportHEVC) {
        sSupportHEVC = supportHEVC;
    }

    public static int getSupportHEVC() {
        return sSupportHEVC;
    }

    /**
     *
     * <p>
     * Specifies the type of layer backing this view. The layer can be
     * {@link #LAYER_TYPE_NONE}, {@link #LAYER_TYPE_SOFTWARE} or
     * {@link #LAYER_TYPE_HARDWARE}.
     * </p>
     *
     * @param v
     * @param type
     *            The type of layer to use with this view, must be one of
     *            {@link #LAYER_TYPE_NONE}, {@link #LAYER_TYPE_SOFTWARE} or
     *            {@link #LAYER_TYPE_HARDWARE}
     *
     * @see #LAYER_TYPE_NONE
     * @see #LAYER_TYPE_SOFTWARE
     * @see #LAYER_TYPE_HARDWARE
     */
    public static void setLayerType(View v, int type) {
        try {
            Integer realType = -1;
            switch (type) {
                case LAYER_TYPE_NONE:
                    realType = ReflectionHelper.getIntFileValueFromClass(
                            View.class, "LAYER_TYPE_NONE");
                    break;
                case LAYER_TYPE_SOFTWARE:
                    realType = ReflectionHelper.getIntFileValueFromClass(
                            View.class, "LAYER_TYPE_SOFTWARE");
                    break;
                case LAYER_TYPE_HARDWARE:
                    realType = ReflectionHelper.getIntFileValueFromClass(
                            View.class, "LAYER_TYPE_HARDWARE");
                    break;
                default:
                    throw new RuntimeException("unsupported layer type");
            }
            if (ReflectionHelper.INVALID_VALUE == realType) {
                return;
            }

            Class<View> cls = View.class;
            @SuppressWarnings("rawtypes")
            Class paramtypes[] = new Class[2];
            paramtypes[0] = Integer.TYPE;
            paramtypes[1] = Paint.class;
            Method method = cls.getMethod("setLayerType", paramtypes);
            Object arglist[] = new Object[2];
            arglist[0] = realType;
            arglist[1] = null;
            method.invoke(v, arglist);
        } catch (Exception ex) {
        }
    }

    public static void buildLayer(View v) {
        try {
            Class<View> cls = View.class;
            Method method = cls.getMethod("buildLayer", new Class[0]);
            method.invoke(v, new Object[0]);
        } catch (Exception ex) {
        }
    }
}
