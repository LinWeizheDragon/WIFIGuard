package com.dragon.wifiguard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.SimpleAdapter;
import android.widget.Toast;

/**
 * 实时监控服务
 * 
 * @author 林炜哲
 * @version 2.0.12.0
 * 
 */
public class RoutimeService extends Service {
	/** 上次更新时间 */
	private long LastRefreshTime;
	/** 超速标准 */
	private int Speed = 50 * 1024;
	/** 上一个消息是否处理完毕 */
	private boolean IsEnd = true;
	// private int Row[];
	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;
	private int Num;
	/** 列表信息类 */
	private ListInfo listinfo = new ListInfo();
	private dataInfo datainfo = new dataInfo();
	private int ID;
	private boolean Closed;
	private static List<HashMap<String, Object>> mList = new ArrayList<HashMap<String, Object>>();
	private static String LastConnect = "NoConnection";
	public static boolean RoutimeServiceIsRunning = true;
	private Notification notification;
	RemoteViews rv;

	// 处理从线程收到的消息们
	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {

			Data app = (Data) AActivity.application;
			do {
				Log.v("实时监控服务", "运行触发" + LastConnect);
				if (!LastConnect.equals(app.getConnectString())) {
					if (!LastConnect.equals("NoConnection")) {
						// 保存数据
						SaveData();
						if (!app.getConnectString().equals("NoConnection")) {
							Log.v("实时监控服务", "重新加载" + app.getConnectString());

							Init();// 重新初始化
						} else {
							// 有链接——》无连接
							LastConnect = "NoConnection";
						}
					} else {
						Log.v("实时监控服务", "重新加载" + app.getConnectString());
						Init();// 重新初始化
					}
				}

				DoSleep(5);

				if (Closed) {
					// 主界面要求服务关闭
					RoutimeServiceIsRunning = false;
					SaveData();
					Log.v("RoutimeService", "Service Ended.");
					stopSelf(msg.arg1);
					return;
				}
				Work();
			} while (true);

			// Log.v("RoutimeService", "Service Ended.");
			// 使用startId停止服务，从而使我们不会在处理
			// 另一个工作的中间停止service
			// stopSelf(msg.arg1);
		}
	}

	/**
	 * 保存现有的数据
	 */
	public static void SaveData() {
		// 保存数据
		if (LastConnect.equals("NoConnection"))
			return;
		SQL db = new SQL();
		db.Port = LastConnect;
		db.openDataBase();
		db.delAll();
		db.openDataBase();
		Log.v("实时监控服务", "保存" + LastConnect);
		for (int i = 0; i < dataInfo.appName.length; i++) {
			db.addData(dataInfo.appName[i], dataInfo.InitDownBytes[i] + "|"
					+ dataInfo.InitUpBytes[i]);
			// 添加数据
		}

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
	 * 工作函数，处理应用流量信息
	 */
	public void Work() {

		Data app = (Data) AActivity.application;
		if (app.getConnectString().equals("NoConnection")) {
			Log.v("实时监控服务", "无连接，不工作");
			NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			// 自定义界面

			rv.setTextViewText(R.id.tv_rv, "连接已断开：" + LastConnect);
			if (LastConnect.equals("mobile")) {
				rv.setTextViewText(R.id.tv_rv, "移动数据连接已断开");
			}
			// rv.setProgressBar(R.id.pb_rv, 80, 20, false);
			rv.setTextViewText(R.id.DownText, "已下载：无数据");
			rv.setTextViewText(R.id.UpText, "已上传：无数据");
			notification.contentView = rv;
			startForeground(ID, notification);
			return;
		}
		int TRx = 0, TTx = 0;// 总共
		int Rx, Tx;
		for (int i = 0; i < Num; i++) {
			Rx = (int) TrafficStats.getUidRxBytes(listinfo.appUid[i])
					- listinfo.LastDownBytes[i];
			Tx = (int) TrafficStats.getUidTxBytes(listinfo.appUid[i])
					- listinfo.LastUpBytes[i];
			if (AActivity.NeedWatch) {
				if (Rx >= Speed) {
					// Log.v("实时监控服务",listinfo.appName[i]+"超速使用了"+DealData(Rx)+"的下载流量");
					// 发送广播
					Intent intent = new Intent();
					intent.putExtra("Rx", DealData(Rx));
					intent.putExtra("Type", 0);
					intent.putExtra("App", listinfo.appName[i]);
					intent.setAction("com.dragon.wifiguard.RoutimeService");
					sendBroadcast(intent);

					Log.v("实时监控服务",listinfo.packageName[i].toString());
					Log.v("实时监控服务",AActivity.LastAsked);
					Log.d("实时监控服务", AActivity.LastAsked.equals(listinfo.packageName[i]) ? "LastAsked":"NoLastAsked");  
					if (!isTopActivity(listinfo.packageName[i].toString())
							&& !(app.getIgnoredOrNot(listinfo.packageName[i]))) {
						//Log.v("实时监控服务","后台超速触发");
						CreateInformation(listinfo.appName[i] + "||||"
								+ Rx + "||dn【"
								+ listinfo.packageName[i] + "】");
					}
					AActivity.LastAsked = listinfo.packageName[i];
					// AActivity.AddLog(AActivity.LastAsked);
				}
				if (Tx >= Speed) {
					// Log.v("实时监控服务",listinfo.appName[i]+"超速使用了"+DealData(Tx)+"的上传流量");
					// 发送广播
					Intent intent = new Intent();
					intent.putExtra("App", listinfo.appName[i]);
					intent.putExtra("Type", 1);
					intent.putExtra("Tx", DealData(Tx));
					intent.setAction("com.dragon.wifiguard.RoutimeService");
					sendBroadcast(intent);
					if (!isTopActivity(listinfo.packageName[i].toString())
							&& !(app.getIgnoredOrNot(listinfo.packageName[i]))) {
						CreateInformation(listinfo.appName[i] + "||||"
								+ Tx + "||up【"
								+ listinfo.packageName[i] + "】");
					}
					AActivity.LastAsked = listinfo.packageName[i];
				}
			}
			
			listinfo.LastDownBytes[i] += Rx;
			listinfo.LastUpBytes[i] += Tx;
			datainfo.InitDownBytes[i] += Rx;
			datainfo.InitUpBytes[i] += Tx;
			TRx += datainfo.InitDownBytes[i];
			TTx += datainfo.InitUpBytes[i];
			
			// Log.v("数据库服务",datainfo.appName[i]+"已更新"+datainfo.InitDownBytes[i]);
			// LastRefreshTime= System.currentTimeMillis();
			// Log.v("实时测速",Rx/(System.currentTimeMillis()-LastRefreshTime)+"/s");
		}
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// 自定义界面

		rv.setTextViewText(R.id.tv_rv, "已连接到：" + LastConnect);
		if (LastConnect.equals("mobile")) {
			rv.setTextViewText(R.id.tv_rv, "已连接到移动数据");
		}
		// rv.setProgressBar(R.id.pb_rv, 80, 20, false);
		rv.setTextViewText(R.id.DownText, "已下载：" + DealData(TRx));
		rv.setTextViewText(R.id.UpText, "已上传：" + DealData(TTx));
		notification.contentView = rv;
		startForeground(ID, notification);
	}

	/**
	 * 线程休眠
	 * 
	 * @param DelayTime
	 *            睡眠时间，单位为秒
	 */  
	public void DoSleep(long DelayTime) {
		long endTime = System.currentTimeMillis() + DelayTime * 1000;
		while (System.currentTimeMillis() < endTime) {
			synchronized (this) {
				try {
					wait(endTime - System.currentTimeMillis());
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * 寻找项目（配合数据库读取）
	 */
	public int findItem(String str) {

		for (int i = 0; i < datainfo.appName.length; i++) {
			if (datainfo.appName[i].toString().equals(str)) {
				return i;

			}
		}
		return -1;// 没有找到项目

	}

	/**
	 * 进行初始化工作
	 */
	public void Init() {
		RoutimeServiceIsRunning = true;
		List<PackageInfo> packages = getPackageManager()
				.getInstalledPackages(0);
		Log.v("实时监控服务", "初始化启动");

		Data app = (Data) AActivity.application;
		String connect = app.getConnectString();
		if (app.getConnectString().equals("NoConnection")) {
			LastConnect = "NoConnection";
			Log.v("实时监控服务", "无连接，初始化完毕");
			return;
		}

		// 防止数据库冲突
		do {
			DoSleep(1);
			Log.v("实时监控服务", "数据库等待");
		} while (AActivity.SQLRunning);
		// 打开数据库
		AActivity.SQLRunning = true;

		SQL db = new SQL();
		db.Port = connect;
		Log.v("数据库服务-连接到", connect);
		db.openDataBase();
		String str;
		int split;
		// db.addData("WIFI1", "1");
		mList = db.getData(db.Port);
		boolean Found = false;

		AActivity.SQLRunning = false;


		int Result = AActivity.Limit_Open(app.getConnectString());
		if (Result == -2) {
			AActivity.Limit_Add(app.getConnectString(),-1);
			AActivity.LimitMax = AActivity.Limit_Open(app.getConnectString());
		} else {
			AActivity.LimitMax = Result;
		}
		Num = 0;
		// 循环遍历所有包，获得可用包的数量
		for (int a = 0; a < packages.size(); a++) {
			PackageInfo packageInfo = packages.get(a);
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
				Num++;
		}
		int n = 0;
		listinfo.Rebuild(Num);
		// Row=new int [Num];
		datainfo.Rebuild(Num);
		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				listinfo.appName[n] = packageInfo.applicationInfo.loadLabel(
						getPackageManager()).toString();
				listinfo.packageName[n] = packageInfo.packageName;
				listinfo.versionName[n] = packageInfo.versionName;
				listinfo.versionCode[n] = packageInfo.versionCode;
				listinfo.appIcon[n] = packageInfo.applicationInfo.loadIcon(
						getPackageManager()).getCurrent();
				// 获得所有应用名
				datainfo.appName[n] = listinfo.appName[n];
				datainfo.InitDownBytes[n] = 0;
				datainfo.InitUpBytes[n] = 0;
				try {
					PackageManager pm = getPackageManager();
					ApplicationInfo ai = pm.getApplicationInfo(
							listinfo.packageName[n],
							PackageManager.GET_ACTIVITIES);
					listinfo.appUid[n] = ai.uid;

				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
				// listinfo.InitDownBytes[n]=10000;
				listinfo.InitDownBytes[n] = (int) TrafficStats
						.getUidRxBytes(listinfo.appUid[n]);
				listinfo.InitUpBytes[n] = (int) TrafficStats
						.getUidTxBytes(listinfo.appUid[n]);
				listinfo.LastDownBytes[n] = listinfo.InitDownBytes[n];
				listinfo.LastUpBytes[n] = listinfo.InitUpBytes[n];
				// listinfo.appIcon[] =
				// packageInfo.applicationInfo.loadIcon(getPackageManager());
				// Log.v("BreakPoint11111", "dd"+listinfo.InitUpBytes[n]);
				// db.addData(listinfo.appName[n],
				// listinfo.InitDownBytes[n]+"|"+listinfo.InitUpBytes[n]);
				n++;

			}
		}
		DatabaseInit();

	}

	/**
	 * 数据库初始化函数
	 */
	public void DatabaseInit() {
		do {
			DoSleep(1);
		} while (AActivity.SQLRunning);
		AActivity.SQLRunning = true;
		Data app = (Data) AActivity.application;
		String connect = app.getConnectString();
		SQL db = new SQL();
		db.Port = connect;
		Log.v("数据库服务-初始化启动", connect);
		String str;
		db.openDataBase();
		Log.v("数据库服务", "Start");
		// 根据datainfo从mList取出数据
		int Index = -1, split;
		Log.v("数据库服务：获取的数据总共有", mList.size() + "");
		for (int i = 0; i < mList.size(); i++) {
			if (mList.get(i).get("name").toString().equals("null")) {

			} else {

				Index = findItem(mList.get(i).get("name").toString());

				if (Index == -1) {
					// Log.v("数据库服务",i+"未找到"+mList.get(i).get("name").toString()+mList.get(i).get("pswd").toString());
				} else {
					str = mList.get(i).get("pswd").toString();
					split = str.indexOf("|", 0);
					datainfo.InitDownBytes[Index] = Integer.valueOf(str
							.substring(0, split).toString());
					datainfo.InitUpBytes[Index] = Integer.valueOf(str
							.substring(split + 1, str.length()).toString());
					// Log.v("数据库服务ID:"+i,
					// datainfo.appName[Index]+"初始化完成");
					// Log.v("数据库服务ID:"+i,
					// datainfo.InitDownBytes[Index]+"  "+datainfo.InitDownBytes[Index]);
				}
			}
		}

		AActivity.SQLRunning = false;
		// DebugSQLList(app.getConnectString());
		LastConnect = app.getConnectString();

		Log.v("数据库服务", "初始化完成");

	}

	/**
	 * 启动浮动窗口提示
	 */
	public void CreateInformation(String info) {
		
		Data app = (Data) getApplication();
		app.AddRow(info);
		if (app.GetIsEnd()) {
			// /Intent intent = new Intent(this, FloatWindowService.class);
			// startService(intent);
			Intent intent1 = new Intent(this, PublicFunction.class);
			startService(intent1);
			Log.v("实时监控服务","启动悬浮窗。");
		}else Log.v("实时监控服务","未启动悬浮窗。");
	}

	@Override
	public void onCreate() {
		// CreateInformation("服务已启动");
		// 启动运行service的线程．注意我创建了一个
		// 分离的线程，因为service通常都是在进程的
		// 主线程中运行，但我们不想让主线程阻塞．我们还把新线程
		// 搞成后台级的优先级，从而减少对UI线程（主线程的影响)．
		Log.v("RoutimeService", "Service Started.");
		/*
		 * long endTime = System.currentTimeMillis() + 5*1000; while
		 * (System.currentTimeMillis() < endTime) { synchronized (this) { try {
		 * wait(endTime - System.currentTimeMillis()); } catch (Exception e) { }
		 * } }
		 */
		Init();
		HandlerThread thread = new HandlerThread("ServiceStartArguments",
				android.os.Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		// Get the HandlerThread's Looper and use it for our Handler
		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "实时监控服务启动", Toast.LENGTH_SHORT).show();
		RoutimeServiceIsRunning = true;
		Closed = false;
		// 对于每个开始请求，发送一消息来开始一次工作，并且把
		// start ID也传过去，所以当完成一个工作时，我们才知道要停止哪个请求．
		Message msg = mServiceHandler.obtainMessage();
		msg.arg1 = startId;
		mServiceHandler.sendMessage(msg);
		ID = msg.arg1;
		// 如果我们在这里返回后被被杀死了，重启之．

		String tickerText = "服务启动";
		long when = System.currentTimeMillis();
		notification = new Notification(R.drawable.logo, tickerText, when);

		// 不能手动清理
		notification.flags |= Notification.FLAG_NO_CLEAR;
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		// 设置用户点击notification的动作
		// pendingIntent 延期的意图
		// Intent intent = new Intent(this,MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(
				AActivity.AContext, 0, intent, 0);
		notification.contentIntent = pendingIntent;

		// 自定义界面
		rv = new RemoteViews(getPackageName(), R.layout.notification);
		rv.setTextViewText(R.id.tv_rv, "已连接到：" + LastConnect);

		// rv.setProgressBar(R.id.pb_rv, 80, 20, false);
		notification.contentView = rv;
		// rv.setImageViewResource(R.id.ig1, R.id.logo);
		// 把定义的notification 传递给 notificationmanager
		startForeground(ID, notification);
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// We don't provide binding, so return null
		return null;
	}

	@Override
	public void onDestroy() {

		RoutimeServiceIsRunning = false;
		Log.v("实时监控服务", "Thread already stoped.");
		Toast.makeText(this, "实时监控服务关闭", Toast.LENGTH_SHORT).show();
		stopSelf(ID);
		Closed = true;
		stopForeground(true);
	}

	/**
	 * 制造一个提示
	 */
	public void Broadcast(String a) {
		Toast.makeText(this, a, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 调试使用，检测数据库列表
	 */
	public void DebugSQLList(String a) {
		do {
			DoSleep(1);
		} while (AActivity.SQLRunning);
		AActivity.SQLRunning = true;
		// Log.v("数据库Debug","开始");
		SQL db = new SQL();
		db.Port = a;
		db.openDataBase();
		// db.delAll();
		// db.addData("WIFI1", "1");
		mList = db.getData(db.Port);
		boolean Found = false;

		for (int i = 0; i < mList.size() - 1; i++) {
			// AActivity.AddLog(a+":"+mList.get(i).get("name").toString()+
			// "=="+mList.get(i).get("pswd").toString());
		}
		AActivity.SQLRunning = false;
	}

	/**
	 * 判断应用程序是否在前台工作
	 * 
	 * @param packageName
	 *            应用程序包名
	 */
	private boolean isTopActivity(String packageName) {
		ActivityManager activityManager = (ActivityManager) this
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
		if (tasksInfo.size() > 0) {
			// 应用程序位于堆栈的顶层
			if (packageName.equals(tasksInfo.get(0).topActivity
					.getPackageName())) {
				return true;
			}
		}
		return false;
	}

}