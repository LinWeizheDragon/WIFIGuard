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
 * ����״̬�������㲥������
 * @author �����
 * @version 2.0.0.0
 */
public class NetworkState extends BroadcastReceiver { 

	public static Context Mycontext;
	public boolean askEnd=false;
    public String MODE_WIFI="1";
    public String MODE_MOBILE="2";
    public String MODE_WIFIWATCH="3";
    
	 /**
	  * ��ȡ3G��WIFI��Ϣ
	  * @param type
	  * 1ΪWIFI��2Ϊ3G
	  * @return
	  */
	 public  String checkNetworkInfo(int type)
	 {
	 ConnectivityManager conMan = (ConnectivityManager) AActivity.appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
	 if (type==1){
		 //wifi
		 State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		 return wifi.toString(); //��ʾwifi����״̬
	 }else{
		 //mobile 3G Data Network
		 State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		return mobile.toString(); //��ʾ3G��������״̬
	 }
	 }
	@Override  
	public void onReceive(Context context, Intent intent) {
		//Thread newThread;         //����һ�����߳�
		//Mycontext=this.getApplicationContent();
		   new Thread() {
			   @Override
				public void run() {
				   Looper.prepare(); 
				     //����д�����߳���Ҫ���Ĺ���
				   if (true) {
						Log.d("�����ط���", "����");
						
						while(AActivity.AskingForResult)
						{
							//����Ѿ�������ѯ�ʵ���Ŀ����ô�ȴ�
							try {
								Thread.currentThread().sleep(500);//���0.5��
								} catch (InterruptedException e) {
								e.printStackTrace();
								}
							Log.d("�����ط���", "�ȴ���");
						};
						
						AActivity.AskingForResult=true;//���ִ����
						/////Log.d("�����ط���", "onononononononon");
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
								sendMessageToLog("WIFI������");
								//////AActivity.AddLog("WIFI:" + checkNetworkInfo(1));
								// WIFI����ģʽ
								Log.v("�����������", "WIFIMODE");
							} else {
								sendMessageToLog("WIFI�ѶϿ�");
								//////AActivity.AddLog("WIFI disconnected.");
							}
							if (mobileInfo.isConnected()) {
								sendMessageToLog("�ƶ�����������");
								//////AActivity.AddLog("3G:" + checkNetworkInfo(2));
								Log.v("�����������", "MOBILEMODE");
								// ��������ģʽ

							} else {
								sendMessageToLog("�ƶ������ѶϿ�");
								//////AActivity.AddLog("3G disconnected.");
							}
							if (activeInfo != null) {
								if (activeInfo.isAvailable() && activeInfo.isConnected()) {
									// ����������
								}
							} else {
								//////AActivity.AddLog("����������");
								app.setConnectString("NoConnection");
								MakeToast("���������ѶϿ�");
								sendMessageToLog("����������");
								AActivity.AskingForResult=false;//��ǽ���
								return;
							} 
							if (activeInfo.getTypeName().toString().equals("mobile")) {
								app.setConnectString("mobile");
								MakeToast("�����ӵ��ƶ�����");
								//sendMessageToLog("�ƶ�����������");
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
							Log.v("�����������", "Start");
							
							for (int i = 0; i < mList.size(); i++) {
								//////AActivity.AddLog(mList.get(i).get("name") + "   "
								//		+ mList.get(i).get("pswd"));
								if (app.getConnectString().toString()
										.equals(mList.get(i).get("name"))) {
									//Log.e("Test",mList.get(i).get("name").toString());
									if (mList.get(i).get("pswd").toString().equals(MODE_WIFIWATCH.toString())) {
										// �������Ҫ��ص�WIFi
										AActivity.NeedWatch = true;
										MakeToast(mList.get(i).get("name").toString()+"\n������\n͵�ɼ�ط���������");
										////AActivity.AddLog("͵�ɼ�ط���������");
										sendMessageToLog("͵�ɼ�ط���������");
									} else {
										AActivity.NeedWatch = false;
										MakeToast(mList.get(i).get("name").toString()+"\n������\n͵�ɼ�ط����ѹر�");
										////AActivity.AddLog("͵�ɼ�ط����ѹر�");
										sendMessageToLog("͵�ɼ�ط����ѹر�");
									 }
									Found = true;
									Log.v("�����������", "Found");
									AActivity.AskingForResult=false;//��ǽ���
								}
							}
							if (Found == false) {
								if (app.getConnectString().toString().equals("mobile")) {
									db.addData(app.getConnectString(), MODE_MOBILE);
									AActivity.AskingForResult=false;//��ǽ���
									Log.v("�����������","ִ�����");
								} else {
									
									AlertDialog.Builder builder = new Builder(
											AActivity.appContext);
									builder.setMessage("�µ�WIFI���ӣ��Ƿ�Ҫ�Ը�WIFI���м�أ�\n	��ʵʱ��غ�̨����������\n	��ʵʱ��������͵�ɡ������ĳ���");
									builder.setTitle("WIFI��ʿ");
									builder.setNegativeButton("��", new OnClickListener() {
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
											AActivity.AskingForResult=false;//��ǽ���
											Log.v("�����������","ִ�����");
										}
									});
									builder.setPositiveButton("��", new OnClickListener() {
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
											AActivity.AskingForResult=false;//��ǽ���
											Log.v("�����������","ִ�����");
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
												return false; // Ĭ�Ϸ��� false
											}
											
										}
									});
									dialog.getWindow().setType(
											WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
									dialog.show();
									/*while(!askEnd)
									{
										//����Ի���δ��������ô�ȴ�
										Log.v("�����������","�Ի���ȴ���");
										try {
											Thread.currentThread().sleep(2000);//���2��
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
		
		// ���͹㲥
		Intent intent = new Intent();
		intent.putExtra("Content",Content );
		intent.putExtra("Type", 0);
		intent.setAction("com.dragon.wifiguard.NetworkState");
		AActivity.appContext.sendBroadcast(intent);
		//Log.v("�����������","�㲥����"+Content);
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