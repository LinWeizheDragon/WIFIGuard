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
 * ʵʱ��ط���
 * 
 * @author �����
 * @version 2.0.12.0
 * 
 */
public class RoutimeService extends Service {
	/** �ϴθ���ʱ�� */
	private long LastRefreshTime;
	/** ���ٱ�׼ */
	private int Speed = 50 * 1024;
	/** ��һ����Ϣ�Ƿ������ */
	private boolean IsEnd = true;
	// private int Row[];
	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;
	private int Num;
	/** �б���Ϣ�� */
	private ListInfo listinfo = new ListInfo();
	private dataInfo datainfo = new dataInfo();
	private int ID;
	private boolean Closed;
	private static List<HashMap<String, Object>> mList = new ArrayList<HashMap<String, Object>>();
	private static String LastConnect = "NoConnection";
	public static boolean RoutimeServiceIsRunning = true;
	private Notification notification;
	RemoteViews rv;

	// ������߳��յ�����Ϣ��
	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {

			Data app = (Data) AActivity.application;
			do {
				Log.v("ʵʱ��ط���", "���д���" + LastConnect);
				if (!LastConnect.equals(app.getConnectString())) {
					if (!LastConnect.equals("NoConnection")) {
						// ��������
						SaveData();
						if (!app.getConnectString().equals("NoConnection")) {
							Log.v("ʵʱ��ط���", "���¼���" + app.getConnectString());

							Init();// ���³�ʼ��
						} else {
							// �����ӡ�����������
							LastConnect = "NoConnection";
						}
					} else {
						Log.v("ʵʱ��ط���", "���¼���" + app.getConnectString());
						Init();// ���³�ʼ��
					}
				}

				DoSleep(5);

				if (Closed) {
					// ������Ҫ�����ر�
					RoutimeServiceIsRunning = false;
					SaveData();
					Log.v("RoutimeService", "Service Ended.");
					stopSelf(msg.arg1);
					return;
				}
				Work();
			} while (true);

			// Log.v("RoutimeService", "Service Ended.");
			// ʹ��startIdֹͣ���񣬴Ӷ�ʹ���ǲ����ڴ���
			// ��һ���������м�ֹͣservice
			// stopSelf(msg.arg1);
		}
	}

	/**
	 * �������е�����
	 */
	public static void SaveData() {
		// ��������
		if (LastConnect.equals("NoConnection"))
			return;
		SQL db = new SQL();
		db.Port = LastConnect;
		db.openDataBase();
		db.delAll();
		db.openDataBase();
		Log.v("ʵʱ��ط���", "����" + LastConnect);
		for (int i = 0; i < dataInfo.appName.length; i++) {
			db.addData(dataInfo.appName[i], dataInfo.InitDownBytes[i] + "|"
					+ dataInfo.InitUpBytes[i]);
			// �������
		}

	}

	/**
	 * ���ֽ�������Ϊ������ʽ
	 * 
	 * @param a
	 *            ������ֽ���
	 */
	public String DealData(int a) {
		double data = a;
		String NewString = "������";
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
	 * ��������������Ӧ��������Ϣ
	 */
	public void Work() {

		Data app = (Data) AActivity.application;
		if (app.getConnectString().equals("NoConnection")) {
			Log.v("ʵʱ��ط���", "�����ӣ�������");
			NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			// �Զ������

			rv.setTextViewText(R.id.tv_rv, "�����ѶϿ���" + LastConnect);
			if (LastConnect.equals("mobile")) {
				rv.setTextViewText(R.id.tv_rv, "�ƶ����������ѶϿ�");
			}
			// rv.setProgressBar(R.id.pb_rv, 80, 20, false);
			rv.setTextViewText(R.id.DownText, "�����أ�������");
			rv.setTextViewText(R.id.UpText, "���ϴ���������");
			notification.contentView = rv;
			startForeground(ID, notification);
			return;
		}
		int TRx = 0, TTx = 0;// �ܹ�
		int Rx, Tx;
		for (int i = 0; i < Num; i++) {
			Rx = (int) TrafficStats.getUidRxBytes(listinfo.appUid[i])
					- listinfo.LastDownBytes[i];
			Tx = (int) TrafficStats.getUidTxBytes(listinfo.appUid[i])
					- listinfo.LastUpBytes[i];
			if (AActivity.NeedWatch) {
				if (Rx >= Speed) {
					// Log.v("ʵʱ��ط���",listinfo.appName[i]+"����ʹ����"+DealData(Rx)+"����������");
					// ���͹㲥
					Intent intent = new Intent();
					intent.putExtra("Rx", DealData(Rx));
					intent.putExtra("Type", 0);
					intent.putExtra("App", listinfo.appName[i]);
					intent.setAction("com.dragon.wifiguard.RoutimeService");
					sendBroadcast(intent);

					Log.v("ʵʱ��ط���",listinfo.packageName[i].toString());
					Log.v("ʵʱ��ط���",AActivity.LastAsked);
					Log.d("ʵʱ��ط���", AActivity.LastAsked.equals(listinfo.packageName[i]) ? "LastAsked":"NoLastAsked");  
					if (!isTopActivity(listinfo.packageName[i].toString())
							&& !(app.getIgnoredOrNot(listinfo.packageName[i]))) {
						//Log.v("ʵʱ��ط���","��̨���ٴ���");
						CreateInformation(listinfo.appName[i] + "||||"
								+ Rx + "||dn��"
								+ listinfo.packageName[i] + "��");
					}
					AActivity.LastAsked = listinfo.packageName[i];
					// AActivity.AddLog(AActivity.LastAsked);
				}
				if (Tx >= Speed) {
					// Log.v("ʵʱ��ط���",listinfo.appName[i]+"����ʹ����"+DealData(Tx)+"���ϴ�����");
					// ���͹㲥
					Intent intent = new Intent();
					intent.putExtra("App", listinfo.appName[i]);
					intent.putExtra("Type", 1);
					intent.putExtra("Tx", DealData(Tx));
					intent.setAction("com.dragon.wifiguard.RoutimeService");
					sendBroadcast(intent);
					if (!isTopActivity(listinfo.packageName[i].toString())
							&& !(app.getIgnoredOrNot(listinfo.packageName[i]))) {
						CreateInformation(listinfo.appName[i] + "||||"
								+ Tx + "||up��"
								+ listinfo.packageName[i] + "��");
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
			
			// Log.v("���ݿ����",datainfo.appName[i]+"�Ѹ���"+datainfo.InitDownBytes[i]);
			// LastRefreshTime= System.currentTimeMillis();
			// Log.v("ʵʱ����",Rx/(System.currentTimeMillis()-LastRefreshTime)+"/s");
		}
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// �Զ������

		rv.setTextViewText(R.id.tv_rv, "�����ӵ���" + LastConnect);
		if (LastConnect.equals("mobile")) {
			rv.setTextViewText(R.id.tv_rv, "�����ӵ��ƶ�����");
		}
		// rv.setProgressBar(R.id.pb_rv, 80, 20, false);
		rv.setTextViewText(R.id.DownText, "�����أ�" + DealData(TRx));
		rv.setTextViewText(R.id.UpText, "���ϴ���" + DealData(TTx));
		notification.contentView = rv;
		startForeground(ID, notification);
	}

	/**
	 * �߳�����
	 * 
	 * @param DelayTime
	 *            ˯��ʱ�䣬��λΪ��
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
	 * Ѱ����Ŀ��������ݿ��ȡ��
	 */
	public int findItem(String str) {

		for (int i = 0; i < datainfo.appName.length; i++) {
			if (datainfo.appName[i].toString().equals(str)) {
				return i;

			}
		}
		return -1;// û���ҵ���Ŀ

	}

	/**
	 * ���г�ʼ������
	 */
	public void Init() {
		RoutimeServiceIsRunning = true;
		List<PackageInfo> packages = getPackageManager()
				.getInstalledPackages(0);
		Log.v("ʵʱ��ط���", "��ʼ������");

		Data app = (Data) AActivity.application;
		String connect = app.getConnectString();
		if (app.getConnectString().equals("NoConnection")) {
			LastConnect = "NoConnection";
			Log.v("ʵʱ��ط���", "�����ӣ���ʼ�����");
			return;
		}

		// ��ֹ���ݿ��ͻ
		do {
			DoSleep(1);
			Log.v("ʵʱ��ط���", "���ݿ�ȴ�");
		} while (AActivity.SQLRunning);
		// �����ݿ�
		AActivity.SQLRunning = true;

		SQL db = new SQL();
		db.Port = connect;
		Log.v("���ݿ����-���ӵ�", connect);
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
		// ѭ���������а�����ÿ��ð�������
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
				// �������Ӧ����
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
	 * ���ݿ��ʼ������
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
		Log.v("���ݿ����-��ʼ������", connect);
		String str;
		db.openDataBase();
		Log.v("���ݿ����", "Start");
		// ����datainfo��mListȡ������
		int Index = -1, split;
		Log.v("���ݿ���񣺻�ȡ�������ܹ���", mList.size() + "");
		for (int i = 0; i < mList.size(); i++) {
			if (mList.get(i).get("name").toString().equals("null")) {

			} else {

				Index = findItem(mList.get(i).get("name").toString());

				if (Index == -1) {
					// Log.v("���ݿ����",i+"δ�ҵ�"+mList.get(i).get("name").toString()+mList.get(i).get("pswd").toString());
				} else {
					str = mList.get(i).get("pswd").toString();
					split = str.indexOf("|", 0);
					datainfo.InitDownBytes[Index] = Integer.valueOf(str
							.substring(0, split).toString());
					datainfo.InitUpBytes[Index] = Integer.valueOf(str
							.substring(split + 1, str.length()).toString());
					// Log.v("���ݿ����ID:"+i,
					// datainfo.appName[Index]+"��ʼ�����");
					// Log.v("���ݿ����ID:"+i,
					// datainfo.InitDownBytes[Index]+"  "+datainfo.InitDownBytes[Index]);
				}
			}
		}

		AActivity.SQLRunning = false;
		// DebugSQLList(app.getConnectString());
		LastConnect = app.getConnectString();

		Log.v("���ݿ����", "��ʼ�����");

	}

	/**
	 * ��������������ʾ
	 */
	public void CreateInformation(String info) {
		
		Data app = (Data) getApplication();
		app.AddRow(info);
		if (app.GetIsEnd()) {
			// /Intent intent = new Intent(this, FloatWindowService.class);
			// startService(intent);
			Intent intent1 = new Intent(this, PublicFunction.class);
			startService(intent1);
			Log.v("ʵʱ��ط���","������������");
		}else Log.v("ʵʱ��ط���","δ������������");
	}

	@Override
	public void onCreate() {
		// CreateInformation("����������");
		// ��������service���̣߳�ע���Ҵ�����һ��
		// ������̣߳���Ϊserviceͨ�������ڽ��̵�
		// ���߳������У������ǲ��������߳����������ǻ������߳�
		// ��ɺ�̨�������ȼ����Ӷ����ٶ�UI�̣߳����̵߳�Ӱ��)��
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
		Toast.makeText(this, "ʵʱ��ط�������", Toast.LENGTH_SHORT).show();
		RoutimeServiceIsRunning = true;
		Closed = false;
		// ����ÿ����ʼ���󣬷���һ��Ϣ����ʼһ�ι��������Ұ�
		// start IDҲ����ȥ�����Ե����һ������ʱ�����ǲ�֪��Ҫֹͣ�ĸ�����
		Message msg = mServiceHandler.obtainMessage();
		msg.arg1 = startId;
		mServiceHandler.sendMessage(msg);
		ID = msg.arg1;
		// ������������ﷵ�غ󱻱�ɱ���ˣ�����֮��

		String tickerText = "��������";
		long when = System.currentTimeMillis();
		notification = new Notification(R.drawable.logo, tickerText, when);

		// �����ֶ�����
		notification.flags |= Notification.FLAG_NO_CLEAR;
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		// �����û����notification�Ķ���
		// pendingIntent ���ڵ���ͼ
		// Intent intent = new Intent(this,MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(
				AActivity.AContext, 0, intent, 0);
		notification.contentIntent = pendingIntent;

		// �Զ������
		rv = new RemoteViews(getPackageName(), R.layout.notification);
		rv.setTextViewText(R.id.tv_rv, "�����ӵ���" + LastConnect);

		// rv.setProgressBar(R.id.pb_rv, 80, 20, false);
		notification.contentView = rv;
		// rv.setImageViewResource(R.id.ig1, R.id.logo);
		// �Ѷ����notification ���ݸ� notificationmanager
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
		Log.v("ʵʱ��ط���", "Thread already stoped.");
		Toast.makeText(this, "ʵʱ��ط���ر�", Toast.LENGTH_SHORT).show();
		stopSelf(ID);
		Closed = true;
		stopForeground(true);
	}

	/**
	 * ����һ����ʾ
	 */
	public void Broadcast(String a) {
		Toast.makeText(this, a, Toast.LENGTH_SHORT).show();
	}

	/**
	 * ����ʹ�ã�������ݿ��б�
	 */
	public void DebugSQLList(String a) {
		do {
			DoSleep(1);
		} while (AActivity.SQLRunning);
		AActivity.SQLRunning = true;
		// Log.v("���ݿ�Debug","��ʼ");
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
	 * �ж�Ӧ�ó����Ƿ���ǰ̨����
	 * 
	 * @param packageName
	 *            Ӧ�ó������
	 */
	private boolean isTopActivity(String packageName) {
		ActivityManager activityManager = (ActivityManager) this
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
		if (tasksInfo.size() > 0) {
			// Ӧ�ó���λ�ڶ�ջ�Ķ���
			if (packageName.equals(tasksInfo.get(0).topActivity
					.getPackageName())) {
				return true;
			}
		}
		return false;
	}

}