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
 * ��������
 * @author �����
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
	// ������Ļ�Ŀ��
	static float x;
	static float y;
	public static int top;
	
	
	/**
	 * ������������
	 */
	public static  void createDesktopLayout() {
		mDesktopLayout = new DesktopLayout(AActivity.appContext);
		
		mDesktopLayout.setOnTouchListener(new OnTouchListener() {
			float mTouchStartX;
			float mTouchStartY;
			

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				try{
				// ��ȡ�����Ļ�����꣬������Ļ���Ͻ�Ϊԭ��
				x = event.getRawX();
				y = event.getRawY() - top; // 25��ϵͳ״̬���ĸ߶�
				//Log.i("startP", "startX" + mTouchStartX + "====startY"
				//		+ mTouchStartY);
				Log.i("TouchInfomation","TouchX:"+ x +"====TouchY:" + y);

				
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// ��ȡ���View�����꣬���Դ�View���Ͻ�Ϊԭ��
					
					mTouchStartX = event.getX();
					mTouchStartY = event.getY();
					Log.i("startP", "startX" + mTouchStartX + "====startY"
							+ mTouchStartY);
					long end = System.currentTimeMillis() - startTime;
					/*
					if (end < 300) {
						
						Data app = (Data)AActivity.application;
						String appname=app.getShowString();
						int a=appname.indexOf("��");
						int b=appname.indexOf("��");
						appname=appname.substring(a+1, b);
						Log.v("�з��ַ���", appname);
						Detail.showInstalledAppDetails(null, appname);
						closeDesk();
					}*/
					
					startTime = System.currentTimeMillis();
					break;
				case MotionEvent.ACTION_MOVE:
					// ���¸�������λ�ò���
					mLayout.x = (int) (x - mTouchStartX);
					mLayout.y = (int) (y - mTouchStartY);
					
					mLayout.alpha= (float)Math.abs( (1.0-Math.abs((float)(y - mTouchStartY))/120 * 1.0));
					mWindowManager.updateViewLayout(v, mLayout);
					//Log.i("TouchInfomation","Alpha:"+mLayout.alpha);
					if ((y - mTouchStartY)>=120){//�»� ��������
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

					// ���¸�������λ�ò���
					//mLayout.x = (int) (x - mTouchStartX);
					//mLayout.y = (int) (y - mTouchStartY);
					Log.e("ACTION_UP","ACTION_UP");
					mLayout.x=0;
					mLayout.y=0;
					mLayout.alpha=1;
					mWindowManager.updateViewLayout(v, mLayout);

					// �����ڴ˼�¼���һ�ε�λ��

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
		Log.v("���Ѵ��ڷ���","��ʼ�����");
	}
	public static void addInfomation(){
		
		Data app = (Data) AActivity.application;
		String[] str= app.getAllStrings();
		
		if (str[0].equals("nothing")){
			//�Ѿ������˵Ļ�
			return;
		}
		new Handler().postDelayed(new Runnable(){    
		    public void run() {  
		    	//30�����������
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
		  		app.DeleteRow();//ÿִ��һ��ɾ��һ�����պ�ɾ���ꡣ��
			}else break;
		}     
		//���Notification��ʾ�û�
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
	        Intent intent = new Intent(AActivity.appContext, ProcessPage.class);  
	        PendingIntent pendingIntent = PendingIntent.getActivity(AActivity.appContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);  
	        // ���״̬����ͼ����ֵ���ʾ��Ϣ����  
	        notification.setLatestEventInfo(AActivity.appContext, "��������Ӧ�ó���30�룺",content, pendingIntent);  
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
				
				int a=appname.indexOf("��");
				int b=appname.indexOf("��");
				appname=appname.substring(a+1, b);
				Log.v("�з��ַ���", appname);
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
	 * ��ʾDesktopLayout
	 */
	public  static void showDesk() {
		Data app = (Data)AActivity.application;
		app.SetIsEnd(false);//����������־
		Log.v("���Ѵ��ڷ���","��ʾ���塪����������");
		mLayout.x=0;
		mLayout.y=0;
		mLayout.alpha=1;
		mWindowManager.addView(mDesktopLayout, mLayout);
		Off=false;
	}
	/**
	 * ѯ���´����ѣ��ѷ���
	 */
	public static void AskForResult(){
		AlertDialog.Builder builder = new Builder(AActivity.appContext);
		builder.setMessage("�´μ������ѣ�");
		builder.setTitle("WIFI��ʿ");
		builder.setNegativeButton("��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				AActivity.LastAsked=" ";
				}
			});
		builder.setPositiveButton("��", null);
		AlertDialog dialog = builder.create();//need a <span style="font-family: 'Microsoft YaHei';">AlertDialog</span>  
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();  
	}
	
	private static boolean Off;
	/**
	 * �ر�DesktopLayout
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
				// û�ˡ���
				Log.v("���Ѵ��ڷ���", "��Ϣ���н���");

				app.SetIsEnd(true);// ���ý�����־
			} else {
				String info = app.getShowString();
				createDesktopLayout();
				int a = info.indexOf("||||");
				int b = info.indexOf("||", a + 4);
				int c = info.indexOf("��");
				int d = info.indexOf("��");
				Log.e("^", a + " " + b + " " + c + " " + d + " ");
				String appname = info.substring(0, a);
				String number = info.substring(a + 4, b);
				String type = info.substring(b + 2, c);
				String packagename = info.substring(c + 1, d);
				Log.e("^", appname + " " + number + " " + type + " "
						+ packagename + " ");
				if (type.toString().equals("dn"))
					type = "����";
				else
					type = "�ϴ�";
				HWindowManager.setInfoText(appname, "����" + type + "������ʹ��"
						+ number);
				showDesk();
				Log.v("���Ѵ��ڷ���", "������һ����" + app.getShowString());

			}
		} catch (Exception e) {
			System.out.println("���Ѵ��ڷ�������ջ�ѻ������Ѻ���\n" + e.getMessage());
			
		}*/
		
	}

	/**
	 * ����WindowManager
	 */
	public static void createWindowManager() {
		// ȡ��ϵͳ����
		mWindowManager = (WindowManager) AActivity.appContext.getSystemService("window");

		// ����Ĳ�����ʽ
		mLayout = new WindowManager.LayoutParams();

		// ���ô�����ʾ���͡���TYPE_SYSTEM_ALERT(ϵͳ��ʾ)
		mLayout.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

		// ���ô��役�㼰������
		// FLAG_NOT_FOCUSABLE(���ܻ�ð������뽹��)
		mLayout.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

		// ������ʾ��ģʽ
		mLayout.format = PixelFormat.RGBA_8888;

		// ���ö���ķ���
		mLayout.gravity = Gravity.TOP | Gravity.LEFT;

		// ���ô����Ⱥ͸߶�
		mLayout.width = WindowManager.LayoutParams.FILL_PARENT;
		mLayout.height = WindowManager.LayoutParams.WRAP_CONTENT;

	}
	

}
