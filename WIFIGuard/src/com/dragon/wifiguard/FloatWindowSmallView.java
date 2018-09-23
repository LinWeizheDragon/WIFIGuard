package com.dragon.wifiguard;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
/**
 * 旧的悬浮窗服务，已废弃
 * @author 林炜哲
 */
public class FloatWindowSmallView extends LinearLayout {

	/**记录小悬浮窗的宽度*/
	public static int viewWidth;
	/**记录小悬浮窗的高度*/
	public static int viewHeight;

	/**用于更新小悬浮窗的位置*/
	private WindowManager windowManager;
	/**小悬浮窗的参数*/
	private WindowManager.LayoutParams mParams;

	/** 记录当前手指位置在屏幕上的横坐标值*/
	private float xInScreen;
	/**记录当前手指位置在屏幕上的纵坐标值*/
	private float yInScreen;
	/**记录手指按下时在屏幕上的横坐标的值*/
	private float xDownInScreen;
	/**记录手指按下时在屏幕上的纵坐标的值*/
	private float yDownInScreen;
	/**记录手指按下时在小悬浮窗的View上的横坐标的值*/
	private float xInView;
	/**记录手指按下时在小悬浮窗的View上的纵坐标的值*/
	private float yInView;
	TextView percentView;

	public FloatWindowSmallView(Context context) {
		super(context);
		windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		LayoutInflater.from(context).inflate(R.layout.float_window_small, this);
		View view = findViewById(R.id.small_window_layout);
		viewWidth = view.getLayoutParams().width;
		viewHeight = view.getLayoutParams().height;
		percentView = (TextView) findViewById(R.id.percent);
		
		//percentView.setText("12345");
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
			xInView = event.getX();
			yInView = event.getY();
			xDownInScreen = event.getRawX();
			yDownInScreen = event.getRawY() - ScreenUtils.getStatusBarHeight();
			xInScreen = event.getRawX();
			yInScreen = event.getRawY() - ScreenUtils.getStatusBarHeight();
			break;
		case MotionEvent.ACTION_MOVE:
			xInScreen = event.getRawX();
			yInScreen = event.getRawY() - ScreenUtils.getStatusBarHeight();
			// 手指移动的时候更新小悬浮窗的位置
			updateViewPosition();
			
			//移动小悬浮框时，显示删除框
			if(!MyWindowManager.isDeleteWindowShowing()){
				MyWindowManager.createDeleteWindow(getContext());
			}
			
			if(isInDeleteArea(xInScreen, yInScreen)){
				MyWindowManager.setDeleteTextColor(Color.YELLOW);
			}else{
				MyWindowManager.setDeleteTextColor(Color.WHITE);
			}
			break;
		case MotionEvent.ACTION_UP:
			// 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
			if (xDownInScreen == xInScreen && yDownInScreen == yInScreen) {
				//openBigWindow();
				String str=(String)percentView.getText();
				int a=str.indexOf("【");
				int b=str.indexOf("】");
				String app=str.substring(a+1, b);
				Log.v("切分字符串", app);
				
				
				Detail.showInstalledAppDetails(null, app);
				MyWindowManager.removeBigWindow(getContext());
				MyWindowManager.removeSmallWindow(getContext());
				Intent intent1 = new Intent(getContext(), PublicFunction.class);
				getContext().startService(intent1);
				Intent intent = new Intent(getContext(), FloatWindowService.class);
				getContext().stopService(intent);
				//Detail de = new Detail();
				//de.showInstalledAppDetails(this.getContext(),app);
			}
			
			//移除删除框
			MyWindowManager.removeDeleteWindow(getContext());
			//用户将小图标拖动到了删除框，则移除所有悬浮窗，并停止Service
			if(isInDeleteArea(xInScreen, yInScreen)){
				MyWindowManager.removeBigWindow(getContext());
				MyWindowManager.removeSmallWindow(getContext());
				Intent intent1 = new Intent(getContext(), PublicFunction.class);
				getContext().startService(intent1);
				Intent intent = new Intent(getContext(), FloatWindowService.class);
				getContext().stopService(intent);
				
			}
			
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
	 */
	public void setParams(WindowManager.LayoutParams params) {
		mParams = params;
	}

	/**
	 * 更新小悬浮窗在屏幕中的位置。
	 */
	private void updateViewPosition() {
		mParams.x = (int) (xInScreen - xInView);
		mParams.y = (int) (yInScreen - yInView);
		windowManager.updateViewLayout(this, mParams);
	}

	/**
	 * 打开大悬浮窗，同时关闭小悬浮窗。
	 */
	private void openBigWindow() {
		MyWindowManager.createBigWindow(getContext());
		MyWindowManager.removeSmallWindow(getContext());
	}
	
	/**判断小悬浮框的当前位置是否在删除框范围内*/
	private boolean isInDeleteArea(float positionX, float positionY){
		if(positionY>ScreenUtils.getScreenH()-FloatWindowDeleteView.viewHeight
				&&positionX>(ScreenUtils.getScreenW()-FloatWindowDeleteView.viewWidth)/2
				&&positionX<ScreenUtils.getScreenW()/2+FloatWindowDeleteView.viewWidth/2
				){
			return true;
		}
		return false;
	}
}
