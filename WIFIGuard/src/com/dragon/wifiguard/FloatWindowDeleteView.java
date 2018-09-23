package com.dragon.wifiguard;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * 旧的悬浮窗服务，已废弃
 * @author 林炜哲
 */
public class FloatWindowDeleteView extends LinearLayout{
	/**记录删除悬浮窗的宽度*/
	public static int viewWidth;
	/** 记录删除悬浮窗的高度*/
	public static int viewHeight;
	
	private TextView textView;

	public FloatWindowDeleteView(Context context) {
		super(context);
		
		LayoutInflater.from(context).inflate(R.layout.float_window_delete, this);
		textView=(TextView) findViewById(R.id.delete_textview);
		viewWidth = textView.getLayoutParams().width;
		viewHeight = textView.getLayoutParams().height;
		
		
	}

	public TextView getTextView() {
		return textView;
	}
}
