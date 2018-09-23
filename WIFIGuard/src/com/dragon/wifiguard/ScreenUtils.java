package com.dragon.wifiguard;

import java.lang.reflect.Field;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * 获取屏幕宽度和高度的工具类。
 *
 */
public class ScreenUtils {
	private static int screenW,screenH;
	private static int statusBarHeight;
	
	/**
	 * 初始化屏幕参数
	 */
	public static void initScreen(Activity mActivity){
		DisplayMetrics metric = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		screenW=metric.widthPixels; // 屏幕宽度（像素）
		screenH=metric.heightPixels; // 屏幕高度（像素）
		
		//状态栏高度
		statusBarHeight=getStatusBarHeight(mActivity);
	}
	

	/**
	 * 获取状态栏的高度。
	 */
	private static int getStatusBarHeight(Activity mActivity) {
		try {
			Class<?> c = Class.forName("com.android.internal.R$dimen");
			Object o = c.newInstance();
			Field field = c.getField("status_bar_height");
			int x = (Integer) field.get(o);
			return mActivity.getResources().getDimensionPixelSize(x);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}


	public static int getScreenW() {
		return screenW;
	}

	public static int getScreenH() {
		return screenH;
	}

	public static int getStatusBarHeight() {
		return statusBarHeight;
	}
	
}
