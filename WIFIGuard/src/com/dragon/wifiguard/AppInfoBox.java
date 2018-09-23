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
 * 详细信息盒子，用于应用详情和连接详情
 * @author 林炜哲
 * @version 1.5.3.0
 */
public  class AppInfoBox extends Activity {
	private EditText str ;
	public static String appname,desstr,appver,apppak;
	public static int mode=1;//1是应用，2是连接
	public  static Drawable appicon;
	private Activity activity;
	/**
	 * 初始化函数
	 */
	public void init(){
		final EditText name = (EditText) findViewById(R.id.appname); 
		str = (EditText) findViewById(R.id.desstr); 
		final EditText ver = (EditText) findViewById(R.id.appver); 
		final Button btn=(Button)findViewById(R.id.callButton);
		final ImageView Image=(ImageView)findViewById(R.id.appicon);
		if (mode==1){
			btn.setText("系统菜单");
			name.setText("应用名称："+appname);
			ver.setText("版本："+appver);
			Image.setImageDrawable(appicon);
		}
		else{
			btn.setText("清除数据");
			name.setText("连接名称："+appname);
			if(Integer.parseInt(appver)==1){
			     ver.setText("类型：WIFI");
			     Image.setImageResource(R.drawable.wifi);
			}else if (Integer.parseInt(appver)==3){
				ver.setText("类型：WIFI");
			     Image.setImageResource(R.drawable.wifi);
			}
			else{
				ver.setText("类型：数据连接");
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
            		//呼叫系统菜单
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
	 * 清除所选连接的记录
	 * 注：实时监控服务必须没有正在监控此连接否则会失败
	 */
	public void ClearData(){

		AlertDialog.Builder builder = new Builder(AppInfoBox.this);
		builder.setMessage("确定要清除"+appname+"的所有记录吗？\n注意：软件将重新启动！");
		builder.setTitle("清除数据");
		builder.setNegativeButton("确认", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Data app=(Data)AActivity.application;
				if ((app.getConnectString().equals(appname))&&(RoutimeService.RoutimeServiceIsRunning)){
					Toast.makeText(getApplicationContext(), "请在非此连接的情况或者暂时关闭实时监控的情况下进行此操作\n否则实时监控服务结束时会将记录重新还原！",
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
		builder.setPositiveButton("取消", null);
		builder.create().show();
				

	}
}