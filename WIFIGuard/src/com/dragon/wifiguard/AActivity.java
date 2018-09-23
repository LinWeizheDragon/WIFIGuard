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
 * AActivity:������
 * @author �����
 * @version 2.0.0.3
 */
public class AActivity extends Activity {
	boolean isExit; // ��������˳�
	/*** ����Ƿ��˳�*/
	public static boolean turnoff = false;
	/***AActivity�Ƿ�����*/
	public static boolean running;
	/***SQL���ݿ��Ƿ�����ռ��*/
	public static boolean SQLRunning = false;
	/***��������*/
	public static int LimitMax;// ��������
	private boolean IsEnd = true;
	public static EditText editText;
	/***�㲥Receiver*/
	private MyReceiver receiver = null;
	private MyReceiver2 receiver2 = null;
	/***ApplicationContext����*/
	public static Context appContext;
	public static Context AContext;// ��Activity��Context
	private static final String SCHEME = "package";
	/***SQL���ݿ�*/
	public static SQLiteDatabase mDatabase;
	public static Application application;
	//��������
	public String MODE_WIFI = "1";
	public String MODE_MOBILE = "2";
	public String MODE_WIFIWATCH = "3";
	/***����Ƿ���Ҫ���*/
	public static boolean NeedWatch;
	/***����ϴ�ѯ�ʵ�Ӧ��*/
	public static String LastAsked = "";
	/***����������*/
	private SpringProgressView progressView;

	/***Dialog�Ƿ�������ʾ*/
	public static boolean DialogShowed;
	/***�Ƿ�����ѯ����*/
	public static boolean AskingForResult=false;
	private EditText Updata;
	private EditText connectText;
	private EditText Downdata;

	/***�����־��Ϣ
	 * @param LogText
	 *    ��־����
	 * */
	public static void AddLog(String LogText) {
		editText.setText(LogText + "\n" + editText.getText());
	}

	@Override
	protected void onDestroy() {

		if (turnoff) {
			super.onDestroy();
			// ��¼Activity�˳�
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
		// ��¼Activity����
		Data app = (Data) getApplication();
		app.activities.add(this);
		appContext = this.getApplicationContext();
		application = this.getApplication();
		AContext = this.getBaseContext();
  
		StatService.setSessionTimeOut(30);
        // setOnҲ������AndroidManifest.xml�ļ�����д��BaiduMobAd_EXCEPTION_LOG���򿪱��������ռ���Ĭ���ǹرյ�
        StatService.setOn(this, StatService.EXCEPTION_LOG);
        /*
         * ��������ʱ��־������ʱ������<br/> ��λΪ�룬��СΪ0s��30s֮��<br/> ע������StatService.setSendLogStrategy֮ǰ���ã��������ò�������
         * 
         * ������õ��Ƿ��Ͳ���������ʱ���ͣ���ô��������ͻ��ڷ���ǰ��������õ������������ʾ�ӳٶ���S���͡�<br/> ���������������ʱֻ֧�ִ�����룬
         * �������׸�������Activity�е�onCreate������ʹ�þͿ��ԡ�<br/>
         */
        StatService.setLogSenderDelayed(10);
        /*
         * ����������־���Ͳ���<br /> Ƕ��λ�ã�Activity��onCreate()������ <br />
         * 
         * ���÷�ʽ��StatService.setSendLogStrategy(this,SendStrategyEnum. SET_TIME_INTERVAL, 1, false); �ڶ���������ѡ��
         * SendStrategyEnum.APP_START SendStrategyEnum.ONCE_A_DAY SendStrategyEnum.SET_TIME_INTERVAL ������������
         * ��������ڵڶ�������ѡ��SendStrategyEnum.SET_TIME_INTERVALʱ��Ч�� ȡֵ��Ϊ1-24֮�������,��1<=rtime_interval<=24����СʱΪ��λ ���ĸ�������
         * ��ʾ�Ƿ��֧��wifi����־���ͣ���Ϊtrue����ʾ����wifi�����·�����־����Ϊfalse����ʾ�������κ����������·�����־
         */
        StatService.setSendLogStrategy(this, SendStrategyEnum.APP_START, 0);
        // ���԰ٶ�ͳ��SDK��Log���أ�������Eclipse�п���sdk��ӡ����־������ʱȥ�����ã���������Ϊfalse
        StatService.setDebugOn(true);
		
		
		
		
		// ע��㲥������
		receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.dragon.wifiguard.RoutimeService");
		AActivity.this.registerReceiver(receiver, filter);

		// ע��㲥������
		receiver2 = new MyReceiver2();
		IntentFilter filter2 = new IntentFilter();
		filter2.addAction("com.dragon.wifiguard.NetworkState");
		AActivity.this.registerReceiver(receiver2, filter2);
		
		
		Updata = (EditText) findViewById(R.id.updata);
		connectText = (EditText) findViewById(R.id.connectText);
		Downdata = (EditText) findViewById(R.id.downdata);

		SQLRunning = true;// �������ʹ�����ݿ�
		Intent intent = new Intent(AActivity.this, RoutimeService.class);
		startService(intent);
		running = true;

		// ���������Ϣ
		RefreshNetwork();
		if (app.getConnectString().equals("NoConnection")) {
			// һ�򿪾�û������
			AddLog("��ǰ������");
			Intent intent2 = new Intent(AActivity.this, NetworkState.class);
			startService(intent2);
			SQLRunning = false;// ���ݿ�ʹ�����
			progressView = (SpringProgressView) findViewById(R.id.spring_progress_view);
			mLimitText=(TextView)findViewById(R.id.TotalTextOut);
			mState=(TextView)findViewById(R.id.stateTextOut);
			Button resetbtn = (Button) findViewById(R.id.resetbtn);
			resetbtn.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					// �޸İ�ť

					EditLimitData(AActivity.this);
				}
			});
			Limit_ReFreshUI();
			return;
		}

		AddLog("��ǰ����Ϊ��" + app.getConnectString());

		List<HashMap<String, Object>> mList = new ArrayList<HashMap<String, Object>>();
		SQL db = new SQL();
		db.Port = "Connection";
		db.openDataBase();
		// db.delAll();
		// db.addData("WIFI1", "1");
		mList = db.getData( db.Port);
		boolean Found = false;
		Log.v("���������ݿ����", "Start");
		for (int i = 0; i < mList.size(); i++) {
			
			//Log.d("���������ݿ����","������"+mList.size());
			// AddLog(mList.get(i).get("name")+"   "+mList.get(i).get("pswd"));
			if (app.getConnectString().toString()
					.equals(mList.get(i).get("name"))) {
				Log.d("���������ݿ����","�ҵ���");
				if (mList.get(i).get("pswd").toString().equals(MODE_WIFIWATCH)) {
					// �������Ҫ��ص�WIFi
					AActivity.NeedWatch = true;
					AActivity.AddLog("͵�ɼ�ط���������");
				} else {
					AActivity.NeedWatch = false;
					AActivity.AddLog("͵�ɼ�ط����ѹر�");
				}
				Found = true;
				Log.v("���������ݿ����", "Found");
			}
		}
		
		// AActivity.NeedWatch=true;
		
		if (Found == false) {
			//Log.d("���������ݿ����","δ�ҵ�");
			if (app.getConnectString().toString().equals("mobile")) {
				db.addData(app.getConnectString(), MODE_MOBILE);
			} else {
				//Log.d("���������ݿ����","��ʼѯ��");
				DialogShowed=false;
				AlertDialog.Builder builder = new Builder(AActivity.appContext);
				builder.setMessage("�µ�WIFI���ӣ��Ƿ�Ҫ�Ը�WIFI���м�أ�\n	��ʵʱ��غ�̨����������\n	��ʵʱ��������͵�ɡ������ĳ���");
				builder.setTitle("WIFI��ʿ");
				builder.setNegativeButton("��", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Data app = (Data) AActivity.application;
						SQL db = new SQL();
						db.Port = "Connection";
						db.openDataBase();
						db.addData(app.getConnectString(), MODE_WIFIWATCH);
						AActivity.NeedWatch = true;
						AActivity.AddLog("͵�ɼ�ط���������");
						DialogShowed=true;
					}
				});
				builder.setPositiveButton("��", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Data app = (Data) AActivity.application;
						SQL db = new SQL();
						db.Port = "Connection";
						db.openDataBase();
						db.addData(app.getConnectString(), MODE_WIFI);
						AActivity.NeedWatch = false;
						AActivity.AddLog("͵�ɼ�ط����ѹر�");
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
							return false; // Ĭ�Ϸ��� false
						}
					}
				});

				dialog.getWindow().setType(
						WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
				dialog.show();
			
				if (DialogShowed=false)
				{
					//����Ի���û��������ʾ��Ĭ�ϲ����������û�
					addNotificaction("���棺�Ի���δ������",
							"С���ֻ���Ĭ�Ϲر�����������������-Ӧ���п�����");
					Log.d("���������ݿ����","�����������Σ�Ĭ�ϴ���");
					db.Port = "Connection";
					db.openDataBase();
					db.addData(app.getConnectString(), MODE_WIFI);
					AActivity.NeedWatch = false;
					AActivity.AddLog("͵�ɼ�ط����ѹر�");
					DialogShowed=true;
				}
			}
			

		}
		

		//�����б��ʼ��
		initIgnoreList();
		//Data���ȡ�����б�
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
		Log.v("�������ѷ���", LimitMax + "");

		Intent intent2 = new Intent(AActivity.this, NetworkState.class);
		startService(intent2);

		SQLRunning = false;// ���ݿ�ʹ�����

		progressView = (SpringProgressView) findViewById(R.id.spring_progress_view);
		Button resetbtn = (Button) findViewById(R.id.resetbtn);

		mLimitText=(TextView)findViewById(R.id.TotalTextOut);
		mState=(TextView)findViewById(R.id.stateTextOut);
		resetbtn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				EditLimitData(AActivity.this);
			}
		});
		Limit_ReFreshUI();//ˢ��UI

		
	}

	
	
	static EditText  mLimitData;
	static TextView  mLimitText;
	static TextView  mState;
	/**
	 * �������ѷ��� �޸���������
	 * @param context ����������
	 */
	private static void EditLimitData(Context context){
		try{
		// �޸İ�ť
        //��̬���ز�������View����  
        LayoutInflater layoutInflater = LayoutInflater.from(context);  
        View longinDialogView = layoutInflater.inflate(R.layout.limitedit, null);  
              
        //��ȡ�����еĿؼ�  
        mLimitData = (EditText)longinDialogView.findViewById(R.id.edit_username);  
        
        //����һ��AlertDialog�Ի���  
        AlertDialog.Builder longinDialog = new AlertDialog.Builder(context);  
        longinDialog .setTitle("�޸���������")  ;
         longinDialog .setView(longinDialogView);                //�����Զ���ĶԻ���ʽ��  
            longinDialog.setPositiveButton("ȷ��", new OnClickListener() {
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
            longinDialog.setNeutralButton("ȡ��",null )  ;
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
	 * ��ʱ����ÿ1��ˢ������
	 */
    Runnable runnable = new Runnable(){
        @Override
        public void run() {
            try {
                handler.postDelayed(this, 1000);
                //System.out.println("��ʱ��");
                Data app =  (Data) getApplication();
                int Result = Limit_Open(app.getConnectString());
        		if (Result == -2) {
        			Limit_Add(app.getConnectString(),-1);

        			LimitMax = Limit_Open(app.getConnectString());
        		} else {
        			LimitMax = Result;
        		}
        		Log.v("�������ѷ���", LimitMax + "");
                Limit_ReFreshUI();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("��ʱ������...");
            }
        }
    };
	
	
	
	/**
	 * �������ѷ��� ���
	 * @param ConnectName ������ 
	 * @param SetValue ������������
	 * */
	public static int Limit_Add(String ConnectName, int SetValue) {
		SQL db = new SQL();
		Log.v("�������ѷ���", "����" + ConnectName);
		db.Port = "LimitData";
		db.openDataBase();
		db.delData(ConnectName);
		db.addData(ConnectName, String.valueOf(SetValue));
		return 0;
	}
	/**
	 * �������ѷ��� ��ȡ
	 * @param ConnectName ������ 
	 * */
	public static int Limit_Open(String ConnectName) {
		SQL db = new SQL();
		db.Port = "LimitData";
		db.openDataBase();
		List<HashMap<String, Object>> mList = new ArrayList<HashMap<String, Object>>();

		Log.v("�������ѷ���", "��ѯ" + ConnectName);
		mList = db.getData( db.Port);
		for (int i = 0; i < mList.size(); i++) {
			if (ConnectName.toString().equals(mList.get(i).get("name"))) {
				return Integer.valueOf(mList.get(i).get("pswd").toString());
			}
		}
		return -2;// û�м�¼

	}
	/**
	 * ˢ��ActivityUI
	 * */
	public void Limit_ReFreshUI() {

		
		Data app = (Data) getApplication();
		if (app.getConnectString().equals("NoConnection")){
			progressView.setMaxCount(100);
			progressView.setCurrentCount(100);
		}
		// /ͳ�ƺ���������
		int x = 0;
		int y = 0;
		for (int i = 0; i < dataInfo.Num; i++) {
			// ͳ����������
			x += Integer.valueOf(dataInfo.InitDownBytes[i]);
			y += Integer.valueOf(dataInfo.InitUpBytes[i]);
		}
		Downdata.setText(DealData(x));
		Updata.setText(DealData(y));
		if (LimitMax == -1) {// û����������
			progressView.setMaxCount(100);
			progressView.setCurrentCount(100);
		} else {
			progressView.setMaxCount(LimitMax);
			progressView.setCurrentCount(x + y);
		}
		mLimitText.setText(DealData(x + y)+"/"+DealData(LimitMax));
		String a="";
		if (RoutimeService.RoutimeServiceIsRunning)
			a = a + "ʵʱ��� ";
		else
			a = a + "��ʵʱ��� ";
		if (AActivity.NeedWatch)
			a = a + "͵�ɼ��";
		else
			a = a + "��͵�ɼ��";
		mState.setText(a);
		
		return;
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
	 * �㲥���շ��� ����RoutineProcess��Ϣ
	 * */
	public class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			int type = bundle.getInt("Type");

			if (type == 0) {
				String Rx = bundle.getString("Rx");
				String app = bundle.getString("App");
				editText.setText(app + "ʹ����" + Rx + "����������" + "\n"
						+ editText.getText());
				// CreateInfomation(app+"ʹ����"+Rx+"����������");

				// Data App=(Data)getApplication();
				// App.AddRow(app+"ʹ����"+Rx+"����������"+"\n"+editText.getText());
				// App.ShowAll();

			}
			if (type == 1) {
				String Tx = bundle.getString("Tx");
				String app = bundle.getString("App");
				editText.setText(app + "ʹ����" + Tx + "���ϴ�����" + "\n"
						+ editText.getText());
				// CreateInfomation(app+"ʹ����"+Tx+"���ϴ�����");
			}

			Log.v("Activity�㲥����", "�յ���Ϣ��");
		}
	}

	/**
	 * �㲥���շ��� ����NetworkState��Ϣ
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

			Log.v("Activity�㲥����2", "�յ���Ϣ��");
		}
	}

	public void CreateTh() {
		Thread newThread; // ����һ�����߳�
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
		newThread.start(); // �����߳�
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// ���HOME��ʱ��������̨����
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
	 * ��������������ʾ
	 */
	public void CreateInfomation(String info) {
		Data app = (Data) getApplication();
		app.SetIsEnd(false);
		Intent intent = new Intent(AActivity.this, FloatWindowService.class);
		startService(intent);

	}

	/**
	 * ˢ������״̬
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
					// WIFI����ģʽ
					Log.v("�����������", "WIFIMODE");
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
					Log.v("�����������", "MOBILEMODE");
					// ��������ģʽ
					app.setConnectString("mobile");
				} else {
					AActivity.AddLog("3G disconnected.");
				}

			}
		}
	} // �������������activeInfoΪnull

	public String checkNetworkInfo(int type) {
		ConnectivityManager conMan = (ConnectivityManager) AActivity.appContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (type == 1) {
			// wifi
			State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
					.getState();
			return wifi.toString(); // ��ʾwifi����״̬
		} else {
			// mobile 3G Data Network
			State mobile = conMan.getNetworkInfo(
					ConnectivityManager.TYPE_MOBILE).getState();
			return mobile.toString(); // ��ʾ3G��������״̬
		}

	}


	public void ShowNotification(String str) {
		// ��ȡ��ϵͳ��notificationManager
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		String tickerText = "��������";
		long when = System.currentTimeMillis();
		Notification notification = new Notification(R.drawable.logo,
				tickerText, when);

		// �����ֶ�����
		notification.flags |= Notification.FLAG_NO_CLEAR;
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		// �����û����notification�Ķ���
		// pendingIntent ���ڵ���ͼ
		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, 0);
		notification.contentIntent = pendingIntent;

		// �Զ������
		RemoteViews rv = new RemoteViews(getPackageName(),
				R.layout.notification);
		rv.setTextViewText(R.id.tv_rv, str);
		// rv.setProgressBar(R.id.pb_rv, 80, 20, false);
		notification.contentView = rv;
		// rv.setImageViewResource(R.id.ig1, R.id.logo);
		// �Ѷ����notification ���ݸ� notificationmanager
		notificationManager.notify(0, notification);
	}

	public void exit() {
		if (!isExit) {
			isExit = true;
			Toast.makeText(getApplicationContext(), "�ٰ�һ���˳�����",
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
		// /ȡ��������ͼ����,ע�⣬�����Ҫ���ñ�����ʽ�������������ڱ�����ʽ֮�󣬷�������
		getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		HWindowManager.top = rect.top;// ״̬���ĸ߶ȣ�����rect.height,rect.width�ֱ���ϵͳ�ĸ߶ȵĿ��

		//Log.i("top", "" + HWindowManager.top);
	}

	public static void StartInfomation() {
		HWindowManager.createWindowManager();
		HWindowManager.createDesktopLayout();
		// HWindowManager.setInfoText(info, info);
		HWindowManager.showDesk();
	}

	 /** 
     * ���һ��notification 
     */  
    public static void addNotificaction(String Title,String Content) {  
        NotificationManager manager = (NotificationManager) AActivity.appContext  
        .getSystemService(Context.NOTIFICATION_SERVICE);  
        // ����һ��Notification  
        Notification notification = new Notification();  
        // ������ʾ���ֻ����ϱߵ�״̬����ͼ��  
        notification.icon = R.drawable.logo;
        // ����ǰ��notification���ŵ�״̬���ϵ�ʱ����ʾ����  
        //notification.tickerText = "ע���ˣ��ұ��ӵ�״̬����";  
          
        /*** 
         * notification.contentIntent:һ��PendingIntent���󣬵��û������״̬���ϵ�ͼ��ʱ����Intent�ᱻ���� 
         * notification.contentView:���ǿ��Բ���״̬����ͼ����Ƿ�һ��view 
         * notification.deleteIntent ����ǰnotification���Ƴ�ʱִ�е�intent 
         * notification.vibrate ���ֻ���ʱ������������ 
         */  
        // ���������ʾ  
        notification.defaults=Notification.DEFAULT_SOUND;  
        // audioStreamType��ֵ����AudioManager�е�ֵ�������������ģʽ  
        notification.audioStreamType= android.media.AudioManager.ADJUST_LOWER;  
          
        //�±ߵ�������ʽ�����������  
        //notification.sound = Uri.parse("file:///sdcard/notification/ringer.mp3");   
        //notification.sound = Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI, "6");   
        Intent intent = new Intent(AActivity.appContext, MainUI.class);  
        PendingIntent pendingIntent = PendingIntent.getActivity(AActivity.appContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);  
        // ���״̬����ͼ����ֵ���ʾ��Ϣ����  
        notification.setLatestEventInfo(AActivity.appContext, Title,Content, pendingIntent);  
        manager.notify(2, notification);  
          
    }  
    
    /**
     * ����������ʼ��
     */
    public void initIgnoreList(){
		List<HashMap<String, Object>> mList = new ArrayList<HashMap<String, Object>>();
		SQL db = new SQL();
		db.Port = "Ignore";
		db.openDataBase();
		mList = db.getData(db.Port);
		Log.v("����������ʼ��", "Start");
		if (mList.size()==0){
			//��һ����������û�г�ʼ������������
			//��ʼ��ʼ��
			List<PackageInfo> packages = getPackageManager()
					.getInstalledPackages(0);
			for (int i = 0; i < packages.size(); i++) {
				PackageInfo packageInfo = packages.get(i);
				if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
					//���ÿһ�������õ���Ӧ�ó���
					String packageName = packageInfo.packageName;
					db.addData(packageName, "0");
					Log.v("�����������", packageName);
					//Ĭ��Ϊ������
				}
			}
			//��ʼ����ϣ��˳�����
			return;
		}
		boolean Found=false;
		//���ǵ�һ������
		List<PackageInfo> packages = getPackageManager()
				.getInstalledPackages(0);
		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				//���ÿһ�������õ���Ӧ�ó���
				//�����������ȶ�
				Found=false;
				for (int x = 0; x < mList.size(); x++) {
					if (mList.get(x).get("name").toString().equals(packageInfo.packageName.toString())){
						//����У���ô����
						Log.v("���������ȶ�","��������"+packageInfo.packageName);
						Found=true;
					}
				}
				if(!Found){
					//û�������
					db.addData(packageInfo.packageName, "0");
					Log.v("���������ȶ�","�������"+packageInfo.packageName);
				}
			}
		}
				
    }
    
}
