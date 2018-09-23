package com.dragon.wifiguard;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * 网络状态监听：广播接收器
 * @author 林炜哲
 * @version 2.0.0.0
 */
public class NetworkState extends BroadcastReceiver { 

	public static Context Mycontext;
	public boolean askEnd=false;
    public String MODE_WIFI="1";
    public String MODE_MOBILE="2";
    public String MODE_WIFIWATCH="3";
    
	 /**
	  * 获取3G、WIFI信息
	  * @param type
	  * 1为WIFI，2为3G
	  * @return
	  */
	 public  String checkNetworkInfo(int type)
	 {
	 ConnectivityManager conMan = (ConnectivityManager) AActivity.appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
	 if (type==1){
		 //wifi
		 State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		 return wifi.toString(); //显示wifi连接状态
	 }else{
		 //mobile 3G Data Network
		 State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		return mobile.toString(); //显示3G网络连接状态
	 }
	 }
	@Override  
	public void onReceive(Context context, Intent intent) {
		//Thread newThread;         //声明一个子线程
		//Mycontext=this.getApplicationContent();
		   new Thread() {
			   @Override
				public void run() {
				   Looper.prepare(); 
				     //这里写入子线程需要做的工作
				   if (true) {
						Log.d("网络监控服务", "触发");
						
						while(AActivity.AskingForResult)
						{
							//如果已经有正在询问的项目，那么等待
							try {
								Thread.currentThread().sleep(500);//阻断0.5秒
								} catch (InterruptedException e) {
								e.printStackTrace();
								}
							Log.d("网络监控服务", "等待中");
						};
						
						AActivity.AskingForResult=true;//标记执行中
						/////Log.d("网络监控服务", "onononononononon");
						// TODO Auto-generated method stub
						// Toast.makeText(context, intent.getAction(), 1).show();
						ConnectivityManager manager = (ConnectivityManager) AActivity.appContext
								.getSystemService(Context.CONNECTIVITY_SERVICE);
						NetworkInfo mobileInfo = manager
								.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
						NetworkInfo wifiInfo = manager
								.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
						NetworkInfo activeInfo = manager.getActiveNetworkInfo();
						// Toast.makeText(context,
						// "mobile:"+mobileInfo.isConnected()+"\n"+"wifi:"+wifiInfo.isConnected()
						// +"\n"+"active:"+activeInfo.getTypeName(), 1).show();
						if (AActivity.running) {
							Data app = (Data) AActivity.application;
							AActivity.SQLRunning = true;

							if (wifiInfo.isConnected()) {
								sendMessageToLog("WIFI已连接");
								//////AActivity.AddLog("WIFI:" + checkNetworkInfo(1));
								// WIFI连接模式
								Log.v("网络监听服务", "WIFIMODE");
							} else {
								sendMessageToLog("WIFI已断开");
								//////AActivity.AddLog("WIFI disconnected.");
							}
							if (mobileInfo.isConnected()) {
								sendMessageToLog("移动数据已连接");
								//////AActivity.AddLog("3G:" + checkNetworkInfo(2));
								Log.v("网络监听服务", "MOBILEMODE");
								// 数据连接模式

							} else {
								sendMessageToLog("移动数据已断开");
								//////AActivity.AddLog("3G disconnected.");
							}
							if (activeInfo != null) {
								if (activeInfo.isAvailable() && activeInfo.isConnected()) {
									// 有网络连接
								}
							} else {
								//////AActivity.AddLog("无网络连接");
								app.setConnectString("NoConnection");
								MakeToast("网络连接已断开");
								sendMessageToLog("无网络连接");
								AActivity.AskingForResult=false;//标记结束
								return;
							} 
							if (activeInfo.getTypeName().toString().equals("mobile")) {
								app.setConnectString("mobile");
								MakeToast("已连接到移动数据");
								//sendMessageToLog("移动数据已连接");
							} else {
								WifiManager wifiManager = (WifiManager) AActivity.appContext
										.getSystemService(Context.WIFI_SERVICE);
								WifiInfo wifiInfox = wifiManager.getConnectionInfo();
								Log.v("wifiInfo", wifiInfox.toString());
								Log.v("SSID", wifiInfox.getSSID());

								String SSID = wifiInfox.getSSID();
								SSID = SSID.substring(1, SSID.length() - 1);
								app.setConnectString(SSID);
							}

							List<HashMap<String, Object>> mList = new ArrayList<HashMap<String, Object>>();
							SQL db = new SQL();
							db.Port = "Connection";
							db.openDataBase();

							// db.addData("WIFI1", "1");
							mList = db.getData(db.Port);
							boolean Found = false;
							Log.v("网络监听服务", "Start");
							
							for (int i = 0; i < mList.size(); i++) {
								//////AActivity.AddLog(mList.get(i).get("name") + "   "
								//		+ mList.get(i).get("pswd"));
								if (app.getConnectString().toString()
										.equals(mList.get(i).get("name"))) {
									//Log.e("Test",mList.get(i).get("name").toString());
									if (mList.get(i).get("pswd").toString().equals(MODE_WIFIWATCH.toString())) {
										// 如果是需要监控的WIFi
										AActivity.NeedWatch = true;
										MakeToast(mList.get(i).get("name").toString()+"\n已连接\n偷渡监控服务已启动");
										////AActivity.AddLog("偷渡监控服务已启动");
										sendMessageToLog("偷渡监控服务已启动");
									} else {
										AActivity.NeedWatch = false;
										MakeToast(mList.get(i).get("name").toString()+"\n已连接\n偷渡监控服务已关闭");
										////AActivity.AddLog("偷渡监控服务已关闭");
										sendMessageToLog("偷渡监控服务已关闭");
									 }
									Found = true;
									Log.v("网络监听服务", "Found");
									AActivity.AskingForResult=false;//标记结束
								}
							}
							if (Found == false) {
								if (app.getConnectString().toString().equals("mobile")) {
									db.addData(app.getConnectString(), MODE_MOBILE);
									AActivity.AskingForResult=false;//标记结束
									Log.v("网络监听服务","执行完毕");
								} else {
									
									AlertDialog.Builder builder = new Builder(
											AActivity.appContext);
									builder.setMessage("新的WIFI连接！是否要对该WIFI进行监控？\n	·实时监控后台大流量程序\n	·实时提醒您“偷渡”流量的程序");
									builder.setTitle("WIFI卫士");
									builder.setNegativeButton("是", new OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											dialog.dismiss();
											Data app = (Data) AActivity.application;
											SQL db = new SQL();
											db.Port = "Connection";
											db.openDataBase();
											db.addData(app.getConnectString(),
													MODE_WIFIWATCH);
											AActivity.NeedWatch = true;
											AActivity.AskingForResult=false;//标记结束
											Log.v("网络监听服务","执行完毕");
										}
									});
									builder.setPositiveButton("否", new OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											dialog.dismiss();
											Data app = (Data) AActivity.application;
											SQL db = new SQL();
											db.Port = "Connection";
											db.openDataBase();
											db.addData(app.getConnectString(), MODE_WIFI);
											AActivity.NeedWatch = false;
											AActivity.AskingForResult=false;//标记结束
											Log.v("网络监听服务","执行完毕");
										}
									});
									builder.setCancelable(false);
									
									AlertDialog dialog = builder.create();// need a <span
																			// style="font-family: 'Microsoft YaHei';">AlertDialog</span>

									dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
										@Override
										public boolean onKey(DialogInterface dialog,
												int keyCode, KeyEvent event) {
											if (keyCode == KeyEvent.KEYCODE_SEARCH) {
												return true;
											} else {
												return false; // 默认返回 false
											}
											
										}
									});
									dialog.getWindow().setType(
											WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
									dialog.show();
									/*while(!askEnd)
									{
										//如果对话框未结束，那么等待
										Log.v("网络监听服务","对话框等待中");
										try {
											Thread.currentThread().sleep(2000);//阻断2秒
											} catch (InterruptedException e) {
											e.printStackTrace();
											}
									};*/
								}

								
							}
							
							
							AActivity.SQLRunning = false;
						}
						 	
					}
				   Looper.loop();
				}
				
				
				
		   }.start();
		 
			
			
	}			

	
	public static void sendMessageToLog(String Content){
		
		// 发送广播
		Intent intent = new Intent();
		intent.putExtra("Content",Content );
		intent.putExtra("Type", 0);
		intent.setAction("com.dragon.wifiguard.NetworkState");
		AActivity.appContext.sendBroadcast(intent);
		//Log.v("网络监听服务","广播发送"+Content);
	}
	public static void MakeToast(String Content){
		Toast toast = Toast.makeText(AActivity.appContext,
			     Content, Toast.LENGTH_LONG);
			   toast.setGravity(Gravity.CENTER, 0, 0);
			   LinearLayout toastView = (LinearLayout) toast.getView();
			   ImageView imageCodeProject = new ImageView(AActivity.appContext);
			   imageCodeProject.setImageResource(R.drawable.wifi);
			   toastView.addView(imageCodeProject, 0);
			   toast.show();
	}
}