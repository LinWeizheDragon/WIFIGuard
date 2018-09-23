package com.dragon.wifiguard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.baidu.mobstat.SendStrategyEnum;
import com.baidu.mobstat.StatService;
import com.dragon.wifiguard.R;
import com.dragon.wifiguard.SpringProgressView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
/**
 * AActivity:主界面
 * @author 林炜哲
 * @version 2.0.0.3
 */
public class AActivity extends Activity {
	boolean isExit; // 连续点击退出
	/*** 软件是否退出*/
	public static boolean turnoff = false;
	/***AActivity是否运行*/
	public static boolean running;
	/***SQL数据库是否正在占用*/
	public static boolean SQLRunning = false;
	/***流量限制*/
	public static int LimitMax;// 流量限制
	private boolean IsEnd = true;
	public static EditText editText;
	/***广播Receiver*/
	private MyReceiver receiver = null;
	private MyReceiver2 receiver2 = null;
	/***ApplicationContext储存*/
	public static Context appContext;
	public static Context AContext;// 本Activity的Context
	private static final String SCHEME = "package";
	/***SQL数据库*/
	public static SQLiteDatabase mDatabase;
	public static Application application;
	//常数定义
	public String MODE_WIFI = "1";
	public String MODE_MOBILE = "2";
	public String MODE_WIFIWATCH = "3";
	/***标记是否需要监控*/
	public static boolean NeedWatch;
	/***标记上次询问的应用*/
	public static String LastAsked = "";
	/***流量进度条*/
	private SpringProgressView progressView;

	/***Dialog是否正常显示*/
	public static boolean DialogShowed;
	/***是否正在询问中*/
	public static boolean AskingForResult=false;
	private EditText Updata;
	private EditText connectText;
	private EditText Downdata;

	/***添加日志消息
	 * @param LogText
	 *    日志内容
	 * */
	public static void AddLog(String LogText) {
		editText.setText(LogText + "\n" + editText.getText());
	}

	@Override
	protected void onDestroy() {

		if (turnoff) {
			super.onDestroy();
			// 记录Activity退出
			Data app = (Data) getApplication();
			app.activities.remove(this);
			running = false;
		} else {
			this.moveTaskToBack(true);
		}
		super.onDestroy();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		editText = (EditText) findViewById(R.id.editText);
		// 记录Activity启动
		Data app = (Data) getApplication();
		app.activities.add(this);
		appContext = this.getApplicationContext();
		application = this.getApplication();
		AContext = this.getBaseContext();
  
		StatService.setSessionTimeOut(30);
        // setOn也可以在AndroidManifest.xml文件中填写，BaiduMobAd_EXCEPTION_LOG，打开崩溃错误收集，默认是关闭的
        StatService.setOn(this, StatService.EXCEPTION_LOG);
        /*
         * 设置启动时日志发送延时的秒数<br/> 单位为秒，大小为0s到30s之间<br/> 注：请在StatService.setSendLogStrategy之前调用，否则设置不起作用
         * 
         * 如果设置的是发送策略是启动时发送，那么这个参数就会在发送前检查您设置的这个参数，表示延迟多少S发送。<br/> 这个参数的设置暂时只支持代码加入，
         * 在您的首个启动的Activity中的onCreate函数中使用就可以。<br/>
         */
        StatService.setLogSenderDelayed(10);
        /*
         * 用于设置日志发送策略<br /> 嵌入位置：Activity的onCreate()函数中 <br />
         * 
         * 调用方式：StatService.setSendLogStrategy(this,SendStrategyEnum. SET_TIME_INTERVAL, 1, false); 第二个参数可选：
         * SendStrategyEnum.APP_START SendStrategyEnum.ONCE_A_DAY SendStrategyEnum.SET_TIME_INTERVAL 第三个参数：
         * 这个参数在第二个参数选择SendStrategyEnum.SET_TIME_INTERVAL时生效、 取值。为1-24之间的整数,即1<=rtime_interval<=24，以小时为单位 第四个参数：
         * 表示是否仅支持wifi下日志发送，若为true，表示仅在wifi环境下发送日志；若为false，表示可以在任何联网环境下发送日志
         */
        StatService.setSendLogStrategy(this, SendStrategyEnum.APP_START, 0);
        // 调试百度统计SDK的Log开关，可以在Eclipse中看到sdk打印的日志，发布时去除调用，或者设置为false
        StatService.setDebugOn(true);
		
		
		
		
		// 注册广播接收器
		receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.dragon.wifiguard.RoutimeService");
		AActivity.this.registerReceiver(receiver, filter);

		// 注册广播接收器
		receiver2 = new MyReceiver2();
		IntentFilter filter2 = new IntentFilter();
		filter2.addAction("com.dragon.wifiguard.NetworkState");
		AActivity.this.registerReceiver(receiver2, filter2);
		
		
		Updata = (EditText) findViewById(R.id.updata);
		connectText = (EditText) findViewById(R.id.connectText);
		Downdata = (EditText) findViewById(R.id.downdata);

		SQLRunning = true;// 标记正在使用数据库
		Intent intent = new Intent(AActivity.this, RoutimeService.class);
		startService(intent);
		running = true;

		// 获得网络信息
		RefreshNetwork();
		if (app.getConnectString().equals("NoConnection")) {
			// 一打开就没有链接
			AddLog("当前无连接");
			Intent intent2 = new Intent(AActivity.this, NetworkState.class);
			startService(intent2);
			SQLRunning = false;// 数据库使用完毕
			progressView = (SpringProgressView) findViewById(R.id.spring_progress_view);
			mLimitText=(TextView)findViewById(R.id.TotalTextOut);
			mState=(TextView)findViewById(R.id.stateTextOut);
			Button resetbtn = (Button) findViewById(R.id.resetbtn);
			resetbtn.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					// 修改按钮

					EditLimitData(AActivity.this);
				}
			});
			Limit_ReFreshUI();
			return;
		}

		AddLog("当前连接为：" + app.getConnectString());

		List<HashMap<String, Object>> mList = new ArrayList<HashMap<String, Object>>();
		SQL db = new SQL();
		db.Port = "Connection";
		db.openDataBase();
		// db.delAll();
		// db.addData("WIFI1", "1");
		mList = db.getData( db.Port);
		boolean Found = false;
		Log.v("主界面数据库服务", "Start");
		for (int i = 0; i < mList.size(); i++) {
			
			//Log.d("主界面数据库服务","遍历中"+mList.size());
			// AddLog(mList.get(i).get("name")+"   "+mList.get(i).get("pswd"));
			if (app.getConnectString().toString()
					.equals(mList.get(i).get("name"))) {
				Log.d("主界面数据库服务","找到了");
				if (mList.get(i).get("pswd").toString().equals(MODE_WIFIWATCH)) {
					// 如果是需要监控的WIFi
					AActivity.NeedWatch = true;
					AActivity.AddLog("偷渡监控服务已启动");
				} else {
					AActivity.NeedWatch = false;
					AActivity.AddLog("偷渡监控服务已关闭");
				}
				Found = true;
				Log.v("主界面数据库服务", "Found");
			}
		}
		
		// AActivity.NeedWatch=true;
		
		if (Found == false) {
			//Log.d("主界面数据库服务","未找到");
			if (app.getConnectString().toString().equals("mobile")) {
				db.addData(app.getConnectString(), MODE_MOBILE);
			} else {
				//Log.d("主界面数据库服务","开始询问");
				DialogShowed=false;
				AlertDialog.Builder builder = new Builder(AActivity.appContext);
				builder.setMessage("新的WIFI连接！是否要对该WIFI进行监控？\n	·实时监控后台大流量程序\n	·实时提醒您“偷渡”流量的程序");
				builder.setTitle("WIFI卫士");
				builder.setNegativeButton("是", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Data app = (Data) AActivity.application;
						SQL db = new SQL();
						db.Port = "Connection";
						db.openDataBase();
						db.addData(app.getConnectString(), MODE_WIFIWATCH);
						AActivity.NeedWatch = true;
						AActivity.AddLog("偷渡监控服务已启动");
						DialogShowed=true;
					}
				});
				builder.setPositiveButton("否", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Data app = (Data) AActivity.application;
						SQL db = new SQL();
						db.Port = "Connection";
						db.openDataBase();
						db.addData(app.getConnectString(), MODE_WIFI);
						AActivity.NeedWatch = false;
						AActivity.AddLog("偷渡监控服务已关闭");
						DialogShowed=true;
					}
				});
				builder.setCancelable(false);
				AlertDialog dialog = builder.create();// need a <span
														// style="font-family: 'Microsoft YaHei';">AlertDialog</span>

				dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
					@Override
					public boolean onKey(DialogInterface dialog, int keyCode,
							KeyEvent event) {
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
			
				if (DialogShowed=false)
				{
					//如果对话框没有正常显示，默认操作并提醒用户
					addNotificaction("警告：对话框未被开启",
							"小米手机等默认关闭悬浮窗，请在设置-应用中开启！");
					Log.d("主界面数据库服务","悬浮窗被屏蔽，默认处理");
					db.Port = "Connection";
					db.openDataBase();
					db.addData(app.getConnectString(), MODE_WIFI);
					AActivity.NeedWatch = false;
					AActivity.AddLog("偷渡监控服务已关闭");
					DialogShowed=true;
				}
			}
			

		}
		

		//忽略列表初始化
		initIgnoreList();
		//Data类读取忽略列表
		app.readAllIgnoreListAndReset();
		
		db.Port = "Connection";
		db.openDataBase();

		db.delData("NoConnection");

		int Result = Limit_Open(app.getConnectString());
		if (Result == -2) {
			Limit_Add(app.getConnectString(),-1);

			LimitMax = Limit_Open(app.getConnectString());
		} else {
			LimitMax = Result;
		}
		Log.v("流量提醒服务", LimitMax + "");

		Intent intent2 = new Intent(AActivity.this, NetworkState.class);
		startService(intent2);

		SQLRunning = false;// 数据库使用完毕

		progressView = (SpringProgressView) findViewById(R.id.spring_progress_view);
		Button resetbtn = (Button) findViewById(R.id.resetbtn);

		mLimitText=(TextView)findViewById(R.id.TotalTextOut);
		mState=(TextView)findViewById(R.id.stateTextOut);
		resetbtn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				EditLimitData(AActivity.this);
			}
		});
		Limit_ReFreshUI();//刷新UI

		
	}

	
	
	static EditText  mLimitData;
	static TextView  mLimitText;
	static TextView  mState;
	/**
	 * 流量提醒服务 修改流量上限
	 * @param context 可用上下文
	 */
	private static void EditLimitData(Context context){
		try{
		// 修改按钮
        //动态加载布局生成View对象  
        LayoutInflater layoutInflater = LayoutInflater.from(context);  
        View longinDialogView = layoutInflater.inflate(R.layout.limitedit, null);  
              
        //获取布局中的控件  
        mLimitData = (EditText)longinDialogView.findViewById(R.id.edit_username);  
        
        //创建一个AlertDialog对话框  
        AlertDialog.Builder longinDialog = new AlertDialog.Builder(context);  
        longinDialog .setTitle("修改流量上限")  ;
         longinDialog .setView(longinDialogView);                //加载自定义的对话框式样  
            longinDialog.setPositiveButton("确定", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,
						int which) {
					
					Data app = (Data) AActivity.application;
					int i = Integer.parseInt(mLimitData.getText().toString())*1024*1024;
					
					if (i==0){
						Limit_Add(app.getConnectString(),-1);
					    LimitMax=-1;
					}
					else{
						Limit_Add(app.getConnectString(),i);
					    LimitMax=i;
					}
					dialog.dismiss();
				}
            });  
            longinDialog.setNeutralButton("取消",null )  ;
            longinDialog.create();    
              
            AlertDialog Dialog = longinDialog.show();  
		}catch (Exception e) {
			System.out.println("ERROR:\n" + e.getMessage());
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		Limit_ReFreshUI();
		handler.postDelayed(runnable, 1000);
	}

	Handler handler = new Handler();
	/***
	 * 定时器，每1秒刷新内容
	 */
    Runnable runnable = new Runnable(){
        @Override
        public void run() {
            try {
                handler.postDelayed(this, 1000);
                //System.out.println("定时器");
                Data app =  (Data) getApplication();
                int Result = Limit_Open(app.getConnectString());
        		if (Result == -2) {
        			Limit_Add(app.getConnectString(),-1);

        			LimitMax = Limit_Open(app.getConnectString());
        		} else {
        			LimitMax = Result;
        		}
        		Log.v("流量提醒服务", LimitMax + "");
                Limit_ReFreshUI();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("定时器错误...");
            }
        }
    };
	
	
	
	/**
	 * 流量提醒服务 添加
	 * @param ConnectName 连接名 
	 * @param SetValue 设置流量限制
	 * */
	public static int Limit_Add(String ConnectName, int SetValue) {
		SQL db = new SQL();
		Log.v("流量提醒服务", "创建" + ConnectName);
		db.Port = "LimitData";
		db.openDataBase();
		db.delData(ConnectName);
		db.addData(ConnectName, String.valueOf(SetValue));
		return 0;
	}
	/**
	 * 流量提醒服务 读取
	 * @param ConnectName 连接名 
	 * */
	public static int Limit_Open(String ConnectName) {
		SQL db = new SQL();
		db.Port = "LimitData";
		db.openDataBase();
		List<HashMap<String, Object>> mList = new ArrayList<HashMap<String, Object>>();

		Log.v("流量提醒服务", "查询" + ConnectName);
		mList = db.getData( db.Port);
		for (int i = 0; i < mList.size(); i++) {
			if (ConnectName.toString().equals(mList.get(i).get("name"))) {
				return Integer.valueOf(mList.get(i).get("pswd").toString());
			}
		}
		return -2;// 没有记录

	}
	/**
	 * 刷新ActivityUI
	 * */
	public void Limit_ReFreshUI() {

		
		Data app = (Data) getApplication();
		if (app.getConnectString().equals("NoConnection")){
			progressView.setMaxCount(100);
			progressView.setCurrentCount(100);
		}
		// /统计和下载排序
		int x = 0;
		int y = 0;
		for (int i = 0; i < dataInfo.Num; i++) {
			// 统计流量总数
			x += Integer.valueOf(dataInfo.InitDownBytes[i]);
			y += Integer.valueOf(dataInfo.InitUpBytes[i]);
		}
		Downdata.setText(DealData(x));
		Updata.setText(DealData(y));
		if (LimitMax == -1) {// 没有流量限制
			progressView.setMaxCount(100);
			progressView.setCurrentCount(100);
		} else {
			progressView.setMaxCount(LimitMax);
			progressView.setCurrentCount(x + y);
		}
		mLimitText.setText(DealData(x + y)+"/"+DealData(LimitMax));
		String a="";
		if (RoutimeService.RoutimeServiceIsRunning)
			a = a + "实时监控 ";
		else
			a = a + "无实时监控 ";
		if (AActivity.NeedWatch)
			a = a + "偷渡监控";
		else
			a = a + "无偷渡监控";
		mState.setText(a);
		
		return;
	}

	/**
	 * 将字节数处理为正常形式
	 * 
	 * @param a
	 *            传入的字节数
	 */
	public String DealData(int a) {
		double data = a;
		String NewString = "无数据";
		if (data == -1) {
			return NewString;
		}
		if (data < 1024 && data >= 0)
			NewString = data + "B";
		if (data >= 1024 && data <= 1048576)
			NewString = Math.round((data / 1024 * 10000) / 10000.0) + "KB";
		if (data >= 1048576 && data <= 1073741824)
			NewString = Math.round((data / 1024 / 1024 * 10000) / 10000.0)
					+ "MB";
		if (data >= 1073741824)
		{
			int x=(int)Math.floor((data / 1024 / 1024 / 1024 * 10000) / 10000.0);
			NewString = x+ "GB";
			int b=a-x*1024*1024*1024;
			NewString = NewString+DealData(b);
		}
		return NewString;

	}

	/**
	 * 广播接收服务 接受RoutineProcess消息
	 * */
	public class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			int type = bundle.getInt("Type");

			if (type == 0) {
				String Rx = bundle.getString("Rx");
				String app = bundle.getString("App");
				editText.setText(app + "使用了" + Rx + "的下载流量" + "\n"
						+ editText.getText());
				// CreateInfomation(app+"使用了"+Rx+"的下载流量");

				// Data App=(Data)getApplication();
				// App.AddRow(app+"使用了"+Rx+"的下载流量"+"\n"+editText.getText());
				// App.ShowAll();

			}
			if (type == 1) {
				String Tx = bundle.getString("Tx");
				String app = bundle.getString("App");
				editText.setText(app + "使用了" + Tx + "的上传流量" + "\n"
						+ editText.getText());
				// CreateInfomation(app+"使用了"+Tx+"的上传流量");
			}

			Log.v("Activity广播服务", "收到消息！");
		}
	}

	/**
	 * 广播接收服务 接受NetworkState消息
	 * */
	public class MyReceiver2 extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			int type = bundle.getInt("Type");

			if (type == 0) {
				String Rx = bundle.getString("Content");
				AActivity.AddLog(Rx);
			}

			Log.v("Activity广播服务2", "收到消息！");
		}
	}

	public void CreateTh() {
		Thread newThread; // 声明一个子线程
		newThread = new Thread(new Runnable() {
			@Override
			public void run() {
				Data app =  (Data) getApplication();
				do {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} while (!app.GetIsEnd());

				CreateInfomation(app.getShowString());
			}
		});
		newThread.start(); // 启动线程
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 点击HOME键时程序进入后台运行
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			moveTaskToBack(true);
			handler.removeCallbacks(runnable);
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 启动浮动窗口提示
	 */
	public void CreateInfomation(String info) {
		Data app = (Data) getApplication();
		app.SetIsEnd(false);
		Intent intent = new Intent(AActivity.this, FloatWindowService.class);
		startService(intent);

	}

	/**
	 * 刷新网络状态
	 * */
	public void RefreshNetwork() {
		if (true) {
			// TODO Auto-generated method stub
			// Toast.makeText(context, intent.getAction(), 1).show();
			ConnectivityManager manager = (ConnectivityManager) appContext
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
				if (activeInfo == null) {
					app.setConnectString("NoConnection");
					return;
				}
				if (wifiInfo.isConnected()) {
					AActivity.AddLog("WIFI:" + checkNetworkInfo(1));
					// WIFI连接模式
					Log.v("网络监听服务", "WIFIMODE");
					WifiManager wifiManager = (WifiManager) AActivity.appContext
							.getSystemService(Context.WIFI_SERVICE);
					WifiInfo wifiInfox = wifiManager.getConnectionInfo();
					Log.v("wifiInfo", wifiInfox.toString());
					Log.v("SSID", wifiInfox.getSSID());

					String SSID = wifiInfox.getSSID();
					SSID = SSID.substring(1, SSID.length() - 1);
					AddLog(SSID);
					app.setConnectString(SSID);
					return;
				} else {
					AActivity.AddLog("WIFI disconnected.");
				}
				if (mobileInfo.isConnected()) {
					AActivity.AddLog("3G:" + checkNetworkInfo(2));
					Log.v("网络监听服务", "MOBILEMODE");
					// 数据连接模式
					app.setConnectString("mobile");
				} else {
					AActivity.AddLog("3G disconnected.");
				}

			}
		}
	} // 如果无网络连接activeInfo为null

	public String checkNetworkInfo(int type) {
		ConnectivityManager conMan = (ConnectivityManager) AActivity.appContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (type == 1) {
			// wifi
			State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
					.getState();
			return wifi.toString(); // 显示wifi连接状态
		} else {
			// mobile 3G Data Network
			State mobile = conMan.getNetworkInfo(
					ConnectivityManager.TYPE_MOBILE).getState();
			return mobile.toString(); // 显示3G网络连接状态
		}

	}


	public void ShowNotification(String str) {
		// 获取到系统的notificationManager
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		String tickerText = "服务启动";
		long when = System.currentTimeMillis();
		Notification notification = new Notification(R.drawable.logo,
				tickerText, when);

		// 不能手动清理
		notification.flags |= Notification.FLAG_NO_CLEAR;
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		// 设置用户点击notification的动作
		// pendingIntent 延期的意图
		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, 0);
		notification.contentIntent = pendingIntent;

		// 自定义界面
		RemoteViews rv = new RemoteViews(getPackageName(),
				R.layout.notification);
		rv.setTextViewText(R.id.tv_rv, str);
		// rv.setProgressBar(R.id.pb_rv, 80, 20, false);
		notification.contentView = rv;
		// rv.setImageViewResource(R.id.ig1, R.id.logo);
		// 把定义的notification 传递给 notificationmanager
		notificationManager.notify(0, notification);
	}

	public void exit() {
		if (!isExit) {
			isExit = true;
			Toast.makeText(getApplicationContext(), "再按一次退出程序",
					Toast.LENGTH_SHORT).show();
			mHandler.sendEmptyMessageDelayed(0, 2000);
		} else {
			handler.removeCallbacks(runnable);
			/*
			 * Intent intent = new Intent(Intent.ACTION_MAIN);
			 * intent.addCategory(Intent.CATEGORY_HOME); startA ctivity(intent);
			 * System.exit(0);
			 */
			this.finish();
			moveTaskToBack(true);
		}
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			isExit = false;
		}
	};

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		Rect rect = new Rect();
		// /取得整个视图部分,注意，如果你要设置标题样式，这个必须出现在标题样式之后，否则会出错
		getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		HWindowManager.top = rect.top;// 状态栏的高度，所以rect.height,rect.width分别是系统的高度的宽度

		//Log.i("top", "" + HWindowManager.top);
	}

	public static void StartInfomation() {
		HWindowManager.createWindowManager();
		HWindowManager.createDesktopLayout();
		// HWindowManager.setInfoText(info, info);
		HWindowManager.showDesk();
	}

	 /** 
     * 添加一个notification 
     */  
    public static void addNotificaction(String Title,String Content) {  
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
        Intent intent = new Intent(AActivity.appContext, MainUI.class);  
        PendingIntent pendingIntent = PendingIntent.getActivity(AActivity.appContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);  
        // 点击状态栏的图标出现的提示信息设置  
        notification.setLatestEventInfo(AActivity.appContext, Title,Content, pendingIntent);  
        manager.notify(2, notification);  
          
    }  
    
    /**
     * 忽略名单初始化
     */
    public void initIgnoreList(){
		List<HashMap<String, Object>> mList = new ArrayList<HashMap<String, Object>>();
		SQL db = new SQL();
		db.Port = "Ignore";
		db.openDataBase();
		mList = db.getData(db.Port);
		Log.v("忽略名单初始化", "Start");
		if (mList.size()==0){
			//第一次启动程序，没有初始化过忽略名单
			//开始初始化
			List<PackageInfo> packages = getPackageManager()
					.getInstalledPackages(0);
			for (int i = 0; i < packages.size(); i++) {
				PackageInfo packageInfo = packages.get(i);
				if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
					//添加每一个搜索得到的应用程序
					String packageName = packageInfo.packageName;
					db.addData(packageName, "0");
					Log.v("忽略名单添加", packageName);
					//默认为不忽略
				}
			}
			//初始化完毕，退出函数
			return;
		}
		boolean Found=false;
		//并非第一次启动
		List<PackageInfo> packages = getPackageManager()
				.getInstalledPackages(0);
		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				//添加每一个搜索得到的应用程序
				//与已有名单比对
				Found=false;
				for (int x = 0; x < mList.size(); x++) {
					if (mList.get(x).get("name").toString().equals(packageInfo.packageName.toString())){
						//如果有，那么不管
						Log.v("忽略名单比对","已有数据"+packageInfo.packageName);
						Found=true;
					}
				}
				if(!Found){
					//没有则添加
					db.addData(packageInfo.packageName, "0");
					Log.v("忽略名单比对","添加数据"+packageInfo.packageName);
				}
			}
		}
				
    }
    
}
