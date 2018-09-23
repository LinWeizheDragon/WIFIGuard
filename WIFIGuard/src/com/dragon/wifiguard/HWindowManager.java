package com.dragon.wifiguard;

import com.dragon.wifiguard.DesktopLayout;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Application;
import android.app.Service;
import android.os.IBinder;

/**
 * 悬浮窗类
 * @author 林炜哲
 * @version 2.0.0.0
 */
public class HWindowManager  {

	
	private static WindowManager mWindowManager;
	private static WindowManager.LayoutParams mLayout;
	private static DesktopLayout mDesktopLayout;
	private static long startTime;
	private static TextView Text1,Text2;
	private static ImageView IMG;
	private static Button btn;
	// 声明屏幕的宽高
	static float x;
	static float y;
	public static int top;
	
	
	/**
	 * 创建悬浮窗体
	 */
	public static  void createDesktopLayout() {
		mDesktopLayout = new DesktopLayout(AActivity.appContext);
		
		mDesktopLayout.setOnTouchListener(new OnTouchListener() {
			float mTouchStartX;
			float mTouchStartY;
			

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				try{
				// 获取相对屏幕的坐标，即以屏幕左上角为原点
				x = event.getRawX();
				y = event.getRawY() - top; // 25是系统状态栏的高度
				//Log.i("startP", "startX" + mTouchStartX + "====startY"
				//		+ mTouchStartY);
				Log.i("TouchInfomation","TouchX:"+ x +"====TouchY:" + y);

				
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// 获取相对View的坐标，即以此View左上角为原点
					
					mTouchStartX = event.getX();
					mTouchStartY = event.getY();
					Log.i("startP", "startX" + mTouchStartX + "====startY"
							+ mTouchStartY);
					long end = System.currentTimeMillis() - startTime;
					/*
					if (end < 300) {
						
						Data app = (Data)AActivity.application;
						String appname=app.getShowString();
						int a=appname.indexOf("【");
						int b=appname.indexOf("】");
						appname=appname.substring(a+1, b);
						Log.v("切分字符串", appname);
						Detail.showInstalledAppDetails(null, appname);
						closeDesk();
					}*/
					
					startTime = System.currentTimeMillis();
					break;
				case MotionEvent.ACTION_MOVE:
					// 更新浮动窗口位置参数
					mLayout.x = (int) (x - mTouchStartX);
					mLayout.y = (int) (y - mTouchStartY);
					
					mLayout.alpha= (float)Math.abs( (1.0-Math.abs((float)(y - mTouchStartY))/120 * 1.0));
					mWindowManager.updateViewLayout(v, mLayout);
					//Log.i("TouchInfomation","Alpha:"+mLayout.alpha);
					if ((y - mTouchStartY)>=120){//下滑 继续提醒
						//AActivity.LastAsked=" ";
						addInfomation();
						closeDesk();
						}
					if ((y - mTouchStartY)<=-top){
						closeDesk();
						addInfomation();
					}
					break;
				case MotionEvent.ACTION_UP:

					// 更新浮动窗口位置参数
					//mLayout.x = (int) (x - mTouchStartX);
					//mLayout.y = (int) (y - mTouchStartY);
					Log.e("ACTION_UP","ACTION_UP");
					mLayout.x=0;
					mLayout.y=0;
					mLayout.alpha=1;
					mWindowManager.updateViewLayout(v, mLayout);

					// 可以在此记录最后一次的位置

					//mTouchStartX = mTouchStartY = 0;
					break;
				}
				}catch ( Exception e ){
				System.out.println("error\n" + e.getMessage());
				closeDesk();
				}

				
				return true;
			}

		});
		Log.v("提醒窗口服务","初始化完毕");
	}
	public static void addInfomation(){
		
		Data app = (Data) AActivity.application;
		String[] str= app.getAllStrings();
		
		if (str[0].equals("nothing")){
			//已经结束了的话
			return;
		}
		new Handler().postDelayed(new Runnable(){    
		    public void run() {  
		    	//30秒后重新运行
		    	if (!ProcessPage.ProcessPageOnShow){
					Data app = (Data) AActivity.application;
					app.SetIsEnd(true);
					}
		    }    
		 }, 30000);
		
		String content="";
		for(int i = 0;i<21;i++){
			if  (!str[0].equals("nothing"))
			{
				String info=str[0];
				int a=info.indexOf("||||");
		  		String appname=info.substring(0,a);
		  		content=content + appname+" ";
		  		app.DeleteRow();//每执行一次删除一条，刚好删除完。。
			}else break;
		}     
		//添加Notification提示用户
		 NotificationManager manager = (NotificationManager) AActivity.appContext  
	        .getSystemService(Context.NOTIFICATION_SERVICE);  
	        // 创建一个Notification  
	        Notification notification = new Notification();  
	        // 设置显示在手机最上边的状态栏的图标  
	        notification.icon = R.drawable.logo;
	        // 当当前的notification被放到状态栏上的时候，提示内容  
	        //notification.tickerText = "注意了，我被扔到状态栏了";  
	          
	        /*** 
	         * notification.contentIntent:一个PendingIntent对象，当用户点击了状态栏上的图标时，该Intent会被触发 
	         * notification.contentView:我们可以不在状态栏放图标而是放一个view 
	         * notification.deleteIntent 当当前notification被移除时执行的intent 
	         * notification.vibrate 当手机震动时，震动周期设置 
	         */  
	        // 添加声音提示  
	        notification.defaults=Notification.DEFAULT_SOUND;  
	        // audioStreamType的值必须AudioManager中的值，代表着响铃的模式  
	        notification.audioStreamType= android.media.AudioManager.ADJUST_LOWER;  
	          
	        //下边的两个方式可以添加音乐  
	        //notification.sound = Uri.parse("file:///sdcard/notification/ringer.mp3");   
	        //notification.sound = Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI, "6");   
	        Intent intent = new Intent(AActivity.appContext, ProcessPage.class);  
	        PendingIntent pendingIntent = PendingIntent.getActivity(AActivity.appContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);  
	        // 点击状态栏的图标出现的提示信息设置  
	        notification.setLatestEventInfo(AActivity.appContext, "忽略以下应用超速30秒：",content, pendingIntent);  
	        manager.notify(2, notification);  
	}
	public static void setInfoText(String Texta,String Textb){
		Text1=(TextView)mDesktopLayout.findViewById(R.id.Text1);
		Text1.setText(Texta);
		Text2=(TextView)mDesktopLayout.findViewById(R.id.Text2);
		Text2.setText(Textb);
		IMG=(ImageView)mDesktopLayout.findViewById(R.id.IMG);
		btn=(Button)mDesktopLayout.findViewById(R.id.Button1);
		btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				/*
				Data app = (Data)AActivity.application;
				
				String appname=app.getShowString();
				
				int a=appname.indexOf("【");
				int b=appname.indexOf("】");
				appname=appname.substring(a+1, b);
				Log.v("切分字符串", appname);
				Detail.showInstalledAppDetails(null, appname);
				*/
				Intent intent=new Intent(AActivity.appContext, ProcessPage.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS); 
				AActivity.appContext.startActivity(intent);
				closeDesk();
				return; 
			}
		}); 
		//IMG.setImageResource(R.drawable.ic_launcher);
	}
	
	

	/**
	 * 显示DesktopLayout
	 */
	public  static void showDesk() {
		Data app = (Data)AActivity.application;
		app.SetIsEnd(false);//设置启动标志
		Log.v("提醒窗口服务","显示窗体——————");
		mLayout.x=0;
		mLayout.y=0;
		mLayout.alpha=1;
		mWindowManager.addView(mDesktopLayout, mLayout);
		Off=false;
	}
	/**
	 * 询问下次提醒，已废弃
	 */
	public static void AskForResult(){
		AlertDialog.Builder builder = new Builder(AActivity.appContext);
		builder.setMessage("下次继续提醒？");
		builder.setTitle("WIFI卫士");
		builder.setNegativeButton("是", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				AActivity.LastAsked=" ";
				}
			});
		builder.setPositiveButton("否", null);
		AlertDialog dialog = builder.create();//need a <span style="font-family: 'Microsoft YaHei';">AlertDialog</span>  
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();  
	}
	
	private static boolean Off;
	/**
	 * 关闭DesktopLayout
	 */
	public static void closeDesk() {
		Off = true;
		mWindowManager.removeView(mDesktopLayout);
		
		
		/*
		try {
			AskForResult();
			Data app = (Data) AActivity.application;

			app.DeleteRow();
			mWindowManager.removeView(mDesktopLayout);

			if (app.getShowString().toString().equals("nothing")) {
				// 没了……
				Log.v("提醒窗口服务", "消息队列结束");

				app.SetIsEnd(true);// 设置结束标志
			} else {
				String info = app.getShowString();
				createDesktopLayout();
				int a = info.indexOf("||||");
				int b = info.indexOf("||", a + 4);
				int c = info.indexOf("【");
				int d = info.indexOf("】");
				Log.e("^", a + " " + b + " " + c + " " + d + " ");
				String appname = info.substring(0, a);
				String number = info.substring(a + 4, b);
				String type = info.substring(b + 2, c);
				String packagename = info.substring(c + 1, d);
				Log.e("^", appname + " " + number + " " + type + " "
						+ packagename + " ");
				if (type.toString().equals("dn"))
					type = "下载";
				else
					type = "上传";
				HWindowManager.setInfoText(appname, "超速" + type + "，流量使用"
						+ number);
				showDesk();
				Log.v("提醒窗口服务", "启动下一队列" + app.getShowString());

			}
		} catch (Exception e) {
			System.out.println("提醒窗口服务运行栈堆积——已忽略\n" + e.getMessage());
			
		}*/
		
	}

	/**
	 * 设置WindowManager
	 */
	public static void createWindowManager() {
		// 取得系统窗体
		mWindowManager = (WindowManager) AActivity.appContext.getSystemService("window");

		// 窗体的布局样式
		mLayout = new WindowManager.LayoutParams();

		// 设置窗体显示类型——TYPE_SYSTEM_ALERT(系统提示)
		mLayout.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

		// 设置窗体焦点及触摸：
		// FLAG_NOT_FOCUSABLE(不能获得按键输入焦点)
		mLayout.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

		// 设置显示的模式
		mLayout.format = PixelFormat.RGBA_8888;

		// 设置对齐的方法
		mLayout.gravity = Gravity.TOP | Gravity.LEFT;

		// 设置窗体宽度和高度
		mLayout.width = WindowManager.LayoutParams.FILL_PARENT;
		mLayout.height = WindowManager.LayoutParams.WRAP_CONTENT;

	}
	

}
