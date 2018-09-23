package com.dragon.wifiguard;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
/**
 * 旧的悬浮窗服务，已废弃
 * @author 林炜哲
 */
public class FloatWindowBigView extends LinearLayout {
	/**记录大悬浮窗的宽度*/
	public static int viewWidth;
	/** 记录大悬浮窗的高度*/
	public static int viewHeight;
	
	/**当点击位置与悬浮框的距离小于该值时，不认为点击了空白区域*/
	private static final int OFFSET_DISTANCE=5;

	public FloatWindowBigView(final Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.float_window_big, this);
		View view = findViewById(R.id.big_window_layout);
		viewWidth = view.getLayoutParams().width;
		viewHeight = view.getLayoutParams().height;
		Button close = (Button) findViewById(R.id.close);
		Button back = (Button) findViewById(R.id.back);
		Button startApp = (Button) findViewById(R.id.start);
		
		//打开应用
		startApp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getContext(), MainUI.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getContext().startActivity(intent);
				
				MyWindowManager.removeBigWindow(context);
				MyWindowManager.createSmallWindow(context,null);
			}
		});
		
		//关闭悬浮框
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 点击关闭悬浮窗的时候，移除所有悬浮窗，并停止Service
				MyWindowManager.removeBigWindow(context);
				MyWindowManager.removeSmallWindow(context);
				Intent intent = new Intent(getContext(), FloatWindowService.class);
				context.stopService(intent);
			}
		});
		
		//返回
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 点击返回的时候，移除大悬浮窗，创建小悬浮窗
				MyWindowManager.removeBigWindow(context);
				MyWindowManager.createSmallWindow(context,null);
			}
		});
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction()==MotionEvent.ACTION_DOWN&&
				isTouchBlank(event.getRawX(), event.getRawY())){
			// 点击大悬浮框以外的区域，移除大悬浮窗，创建小悬浮窗
			MyWindowManager.removeBigWindow(getContext());
			MyWindowManager.createSmallWindow(getContext(),null);
		}
		return true;
	}
	
	/**判断是否点击了大悬浮框以外的区域*/
	private boolean isTouchBlank(float positionX, float positionY){
		if(positionX<(ScreenUtils.getScreenW()-viewWidth)/2-OFFSET_DISTANCE||
				positionX>ScreenUtils.getScreenW()/2+viewWidth/2+OFFSET_DISTANCE||
				positionY<(ScreenUtils.getScreenH()-viewHeight)/2+ScreenUtils.getStatusBarHeight()-OFFSET_DISTANCE||
				positionY>(ScreenUtils.getScreenH())/2+viewHeight/2+ScreenUtils.getStatusBarHeight()+OFFSET_DISTANCE  
				){
			return true;
		}
		return false;
	}
}
