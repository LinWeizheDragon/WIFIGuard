package com.dragon.wifiguard;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
/**
 * 旧的悬浮窗服务，已废弃
 * @author 林炜哲
 */
public class MyWindowManager {

	
	/**小悬浮窗View的实例*/
	private static FloatWindowSmallView smallWindow;
	/**大悬浮窗View的实例*/
	private static FloatWindowBigView bigWindow;
	/**删除框*/
	private static FloatWindowDeleteView deleteWindow;

	/**小悬浮窗View的参数*/
	private static LayoutParams smallWindowParams;
	/**大悬浮窗View的参数*/
	private static LayoutParams bigWindowParams;
	/**删除框的参数*/
	private static LayoutParams deleteWindowParams;

	/**用于控制在屏幕上添加或移除悬浮窗*/
	private static WindowManager mWindowManager;

	/**用于获取手机可用内存*/
	private static ActivityManager mActivityManager;

	public static String MyString;
	/**
	 * 创建一个小悬浮窗。初始位置为屏幕的右部中间位置。
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 */
	public static void createSmallWindow(Context context,
			String ShowString) {
		WindowManager windowManager = getWindowManager(context);
		if (smallWindow == null) {
			smallWindow = new FloatWindowSmallView(context);
			MyString=ShowString;
			
			if (smallWindowParams == null) {
				smallWindowParams = new LayoutParams();
				smallWindowParams.type = LayoutParams.TYPE_PHONE;
				smallWindowParams.format = PixelFormat.RGBA_8888;
				smallWindowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
						| LayoutParams.FLAG_NOT_FOCUSABLE;
				smallWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
				smallWindowParams.width = FloatWindowSmallView.viewWidth;
				smallWindowParams.height = FloatWindowSmallView.viewHeight;
				smallWindowParams.x = ScreenUtils.getScreenW();
				smallWindowParams.y = ScreenUtils.getScreenH() / 2;
			}
			smallWindow.setParams(smallWindowParams);
			windowManager.addView(smallWindow, smallWindowParams);
		}
	}

	/**
	 * 将小悬浮窗从屏幕上移除。
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 */
	public static void removeSmallWindow(Context context) {
		if (smallWindow != null) {
			WindowManager windowManager = getWindowManager(context);
			windowManager.removeView(smallWindow);
			smallWindow = null;
		}
	}

	/**
	 * 创建一个大悬浮窗。位置为屏幕正中间。
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 */
	public static void createBigWindow(Context context) {
		WindowManager windowManager = getWindowManager(context);
		if (bigWindow == null) {
			bigWindow = new FloatWindowBigView(context);
			if (bigWindowParams == null) {
				bigWindowParams = new LayoutParams();
				bigWindowParams.x = ScreenUtils.getScreenW() / 2 - FloatWindowBigView.viewWidth / 2;
				bigWindowParams.y = ScreenUtils.getScreenH() / 2 - FloatWindowBigView.viewHeight / 2;
				bigWindowParams.type = LayoutParams.TYPE_PHONE;
				bigWindowParams.format = PixelFormat.RGBA_8888;
				bigWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
				bigWindowParams.width = FloatWindowBigView.viewWidth;
				bigWindowParams.height = FloatWindowBigView.viewHeight;
			}
			windowManager.addView(bigWindow, bigWindowParams);
		}
	}

	/**
	 * 将大悬浮窗从屏幕上移除。
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 */
	public static void removeBigWindow(Context context) {
		if (bigWindow != null) {
			WindowManager windowManager = getWindowManager(context);
			windowManager.removeView(bigWindow);
			bigWindow = null;
		}
	}
	
	/**创建删除悬浮框，位于屏幕下方*/
	public static void createDeleteWindow(Context context){
		WindowManager windowManager = getWindowManager(context);
		if(deleteWindow==null){
			deleteWindow=new FloatWindowDeleteView(context);
			if(deleteWindowParams==null){
				deleteWindowParams = new LayoutParams();
				deleteWindowParams.x = (ScreenUtils.getScreenW()-
						FloatWindowDeleteView.viewWidth)/2;
				deleteWindowParams.y = 0;
				deleteWindowParams.type = LayoutParams.TYPE_PHONE;
				deleteWindowParams.format = PixelFormat.RGBA_8888;
				deleteWindowParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
				deleteWindowParams.width = FloatWindowDeleteView.viewWidth;
				deleteWindowParams.height = FloatWindowDeleteView.viewHeight;
			}
			windowManager.addView(deleteWindow, deleteWindowParams);
		}
	}
	
	/**移除删除悬浮框*/
	public static void removeDeleteWindow(Context context){
		if (deleteWindow != null) {
			WindowManager windowManager = getWindowManager(context);
			windowManager.removeView(deleteWindow);
			deleteWindow = null;
		}
	}

	/**
	 * 更新小悬浮窗的TextView上的数据，显示内存使用的百分比。
	 * 
	 * @param context
	 *            可传入应用程序上下文。
	 */
	public static void updateUsedPercent(Context context) {
		if (smallWindow != null) {
			TextView percentView = (TextView) smallWindow.findViewById(R.id.percent);
			percentView.setText(MyString);
		}
	}

	/**
	 * 是否有悬浮窗(包括小悬浮窗和大悬浮窗)显示在屏幕上。
	 * 
	 * @return 有悬浮窗显示在桌面上返回true，没有的话返回false。
	 */
	public static boolean isWindowShowing() {
		return smallWindow != null || bigWindow != null;
	}
	
	/**删除悬浮框是否正在显示*/
	public static boolean isDeleteWindowShowing() {
		return deleteWindow != null;
	}
	
	/**设置删除框字体颜色*/
	public static void setDeleteTextColor(int color){
		if(deleteWindow!=null){
			deleteWindow.getTextView().setTextColor(color);
		}
	}

	/**
	 * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 * @return WindowManager的实例，用于控制在屏幕上添加或移除悬浮窗。
	 */
	private static WindowManager getWindowManager(Context context) {
		if (mWindowManager == null) {
			mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		}
		return mWindowManager;
	}

	/**
	 * 如果ActivityManager还未创建，则创建一个新的ActivityManager返回。否则返回当前已创建的ActivityManager。
	 * 
	 * @param context
	 *            可传入应用程序上下文。
	 * @return ActivityManager的实例，用于获取手机可用内存。
	 */
	private static ActivityManager getActivityManager(Context context) {
		if (mActivityManager == null) {
			mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		}
		return mActivityManager;
	}

	/**
	 * 计算已使用内存的百分比，并返回。
	 * 
	 * @param context
	 *            可传入应用程序上下文。
	 * @return 已使用内存的百分比，以字符串形式返回。
	 */
	public static String getUsedPercentValue(Context context) {
		String dir = "/proc/meminfo";
		try {
			FileReader fr = new FileReader(dir);
			BufferedReader br = new BufferedReader(fr, 2048);
			String memoryLine = br.readLine();
			String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
			br.close();
			long totalMemorySize = Integer.parseInt(subMemoryLine.replaceAll("\\D+", ""));
			long availableSize = getAvailableMemory(context) / 1024;
			int percent = (int) ((totalMemorySize - availableSize) / (float) totalMemorySize * 100);
			return percent + "%";
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "悬浮窗";
	}

	/**
	 * 获取当前可用内存，返回数据以字节为单位。
	 * 
	 * @param context
	 *            可传入应用程序上下文。
	 * @return 当前可用内存。
	 */
	private static long getAvailableMemory(Context context) {
		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		getActivityManager(context).getMemoryInfo(mi);
		return mi.availMem;
	}

}
