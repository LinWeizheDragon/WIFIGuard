package com.dragon.wifiguard;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * ��ϸ��Ϣ���ӣ�����Ӧ���������������
 * @author �����
 * @version 1.5.3.0
 */
public  class AppInfoBox extends Activity {
	private EditText str ;
	public static String appname,desstr,appver,apppak;
	public static int mode=1;//1��Ӧ�ã�2������
	public  static Drawable appicon;
	private Activity activity;
	/**
	 * ��ʼ������
	 */
	public void init(){
		final EditText name = (EditText) findViewById(R.id.appname); 
		str = (EditText) findViewById(R.id.desstr); 
		final EditText ver = (EditText) findViewById(R.id.appver); 
		final Button btn=(Button)findViewById(R.id.callButton);
		final ImageView Image=(ImageView)findViewById(R.id.appicon);
		if (mode==1){
			btn.setText("ϵͳ�˵�");
			name.setText("Ӧ�����ƣ�"+appname);
			ver.setText("�汾��"+appver);
			Image.setImageDrawable(appicon);
		}
		else{
			btn.setText("�������");
			name.setText("�������ƣ�"+appname);
			if(Integer.parseInt(appver)==1){
			     ver.setText("���ͣ�WIFI");
			     Image.setImageResource(R.drawable.wifi);
			}else if (Integer.parseInt(appver)==3){
				ver.setText("���ͣ�WIFI");
			     Image.setImageResource(R.drawable.wifi);
			}
			else{
				ver.setText("���ͣ���������");
				Image.setImageResource(R.drawable.mobile);
			}
		}
		
		str.setText(desstr);
		
		
		
	}
	
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	@Override
	protected void onResume() {
		super.onResume();
		init();
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appinfobox);
		activity=this;
		
		final ImageButton button = (ImageButton) findViewById(R.id.backButton);  
        button.setOnClickListener(new View.OnClickListener() {  
            public void onClick(View v) {  
            	activity.finish();
            }  
        });  
        final Button button2 = (Button) findViewById(R.id.callButton);  
        button2.setOnClickListener(new View.OnClickListener() {  
            public void onClick(View v) {  
            	if (mode==1){
            		//����ϵͳ�˵�
                	Detail.showInstalledAppDetails(null, apppak);
        		}
        		else{
        			ClearData();
        			//finish();
        		}
            	
            }  
        });  
	}
	/**
	 * �����ѡ���ӵļ�¼
	 * ע��ʵʱ��ط������û�����ڼ�ش����ӷ����ʧ��
	 */
	public void ClearData(){

		AlertDialog.Builder builder = new Builder(AppInfoBox.this);
		builder.setMessage("ȷ��Ҫ���"+appname+"�����м�¼��\nע�⣺���������������");
		builder.setTitle("�������");
		builder.setNegativeButton("ȷ��", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Data app=(Data)AActivity.application;
				if ((app.getConnectString().equals(appname))&&(RoutimeService.RoutimeServiceIsRunning)){
					Toast.makeText(getApplicationContext(), "���ڷǴ����ӵ����������ʱ�ر�ʵʱ��ص�����½��д˲���\n����ʵʱ��ط������ʱ�Ὣ��¼���»�ԭ��",
        					Toast.LENGTH_SHORT).show();
					return;
				} 
				AActivity.SQLRunning=true;
				SQL db=new SQL();
				db.Port=appname;
				  db.openDataBase();
				 db.delAll();
				 db.Port="Connection";
				 db.openDataBase();
				 db.delData(appname);
				 AActivity.SQLRunning=false;
				 if (RoutimeService.RoutimeServiceIsRunning)
		        		RoutimeService.SaveData();
		        		EActivity.ChangeService(false);
		        		AActivity.turnoff=true;
		        		Intent intent1 = new Intent(AActivity.appContext, PublicFunction.class);
		    			stopService(intent1);
		    			Intent i = getBaseContext().getPackageManager()  
		    			        .getLaunchIntentForPackage(getBaseContext().getPackageName());  
		    			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
		    			startActivity(i);  
		        		android.os.Process.killProcess(android.os.Process.myPid());
				}
			});
		builder.setPositiveButton("ȡ��", null);
		builder.create().show();
				

	}
}