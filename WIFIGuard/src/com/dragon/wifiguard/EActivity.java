package com.dragon.wifiguard;

import com.baidu.autoupdatesdk.BDAutoUpdateSDK;
import com.baidu.autoupdatesdk.UICheckUpdateCallback;
import com.dragon.wifiguard.SwitchButton.OnChangeListener;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
/**
 * EActivity:����ҳ��
 * @author �����
 * @version 2.0.0.0
 */
public class EActivity extends Activity{

	SwitchButton sb;
	SwitchButton sb2;
	boolean isExit;  //��������˳�
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	// ���HOME��ʱ��������̨����
        if(keyCode == KeyEvent.KEYCODE_HOME){
            moveTaskToBack(true);                
            return true;
        }
        if(keyCode == KeyEvent.KEYCODE_BACK){
        	exit();
        	return true;
        }
        return super.onKeyDown(keyCode, event);
    }
	public void exit(){  
        if (!isExit) {  
            isExit = true;  
            Toast.makeText(getApplicationContext(), "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();  
            mHandler.sendEmptyMessageDelayed(0, 2000);  
        } else {  
            /*Intent intent = new Intent(Intent.ACTION_MAIN);  
            intent.addCategory(Intent.CATEGORY_HOME);  
            startA ctivity(intent);  
            System.exit(0);  */
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
    
    /**
     * ��ʵʱ��ط�����п���
     * @param i trueΪ�򿪣�falseΪ�ر�
     */
	public static void ChangeService(boolean i){
		
		if(i){
			if (!RoutimeService.RoutimeServiceIsRunning){
			Intent intent = new Intent(AActivity.appContext, RoutimeService.class);
    		AActivity.appContext.startService(intent);
    		AActivity.editText.setText("ʵʱ��ط���������"+"\n" +AActivity.editText.getText());
			}
		}
		else{
			if (RoutimeService.RoutimeServiceIsRunning){
			Intent intent = new Intent(AActivity.appContext, RoutimeService.class);
			AActivity.appContext.stopService(intent);
    		AActivity.editText.setText("ʵʱ��ط����ѹر�"+"\n" +AActivity.editText.getText());
			}
		}
		
	}
	/**
	 * ǿ���˳���������
	 */
	public void stopInfomation(){
		mService = new Intent(EActivity.this, PublicFunction.class);
		bindService();
	}
	  private Intent mService;

	  /**
	   * ��������
	   */
	public void bindService() {
	    startService(mService);
	    
	    Log.v("��������","�����С�");
	    //�������񡭡�
	  }



	@Override
	public void onResume(){
		super.onResume();
		sb2.mSwitchOn=AActivity.NeedWatch;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//��¼Activity����
  		Data app = (Data) getApplication();  
          app.activities.add(this); 
          dialog = new ProgressDialog(this);
  		dialog.setIndeterminate(true);

  		//�Զ�����
  		dialog.show();
		BDAutoUpdateSDK.uiUpdateAction(EActivity.this, new MyUICheckUpdateCallback());
        
		setContentView(R.layout.about);
		ImageButton btn = (ImageButton) findViewById(R.id.exitButton);  
		ImageButton btn2=(ImageButton)findViewById(R.id.ignoreButton);
		btn2.setOnClickListener(new View.OnClickListener() {  
			public void onClick(View v){
				//���ú����б����
				Intent intent = new Intent(EActivity.this ,IgnorePage.class);    
	            startActivity(intent);    
	            
	            
	            /*
	            NetString a=new NetString();
	            NetString b=new NetString();
	            NetString c=new NetString();
	            a.setData(1000000);
	            b.setData(30000000);
	            
	            Log.v("NetString a",a.toString());
	            Log.v("NetString b",b.toString());
	            Log.v("NetString c",c.combineData(a, b).toString());
	            */
				/*
        		Data app = (Data)getApplication();
        	  	app.AddRow("UC�����||||2300||dn��com.UCMobile��");
        	  	app.AddRow("UC�����||||8888||up��com.UCMobile��");
        	  	app.AddRow("UC�����||||3900||up��com.UCMobile��");
        	  	app.AddRow("WIFIGuard||||6666||up��com.dragon.wifiguard��");
        	  	app.AddRow("UC�����||||8888||up��com.UCMobile��");
        	  	app.AddRow("WIFIGuard||||6666||up��com.dragon.wifiguard��");
        	  	app.AddRow("UC�����||||8888||dn��com.UCMobile��");
        	  	app.AddRow("��������||||390020||dn��in.huohua.Yuki��");
        	  	app.AddRow("�ſ�||||389992||dn��com.youku.phone��");
        	  	app.AddRow("TED||||444432||dn��com.ted.android��");
        	  	app.AddRow("�ٶ���||||323333||dn��com.baidu.netdisk��");
        	  	app.AddRow("Ĵָ��||||56444||dn��com.muzhiwan.market��");
        	  	app.AddRow("�ٶ���||||682233||up��com.baidu.netdisk��");
        	  	app.AddRow("����������||||435765||dn��com.netease.cloudmusic��");
        	  	
        	  	app.ShowAll();
        	  	
        	  	if (app.GetIsEnd()) {
        			// /Intent intent = new Intent(this, FloatWindowService.class);
        			// startService(intent);
        			Intent intent1 = new Intent(EActivity.this, PublicFunction.class);
        			startService(intent1);
        		}
        	  	*/
        	  	
			}
		});
      	
        btn.setOnClickListener(new View.OnClickListener() {  
              
        	public void onClick(View v){
        		//�˳���
        		if (RoutimeService.RoutimeServiceIsRunning)
        		RoutimeService.SaveData();
        		ChangeService(false);
        		AActivity.turnoff=true;
        		Intent intent1 = new Intent(EActivity.this, PublicFunction.class);
    			stopService(intent1);
        		android.os.Process.killProcess(android.os.Process.myPid());
        		/*
        		Data app = (Data)getApplication();
        	  	app.AddRow("360�ֻ�����||||8888||dn��com.qihu.360��");
        	  	app.AddRow("360�ֻ�����2||||8888||dn��com.qihu.360��");
        	  	app.AddRow("360�ֻ�����3||||8888||dn��com.qihu.360��");
        	  	if (app.GetIsEnd()){
        	  	///Intent intent = new Intent(this, FloatWindowService.class);
        			//startService(intent);
        	  		//Intent intent1 = new Intent(EActivity.this, PublicFunction.class);
        			//startService(intent1);
        	  		stopInfomation();
        	  	}*/
        	  	
        		
        	}
        }); 
        sb = (SwitchButton) findViewById(R.id.wiperSwitch1);  
        sb2 = (SwitchButton) findViewById(R.id.wiperSwitch2); 
        
        sb2.mSwitchOn=AActivity.NeedWatch;
        sb.setOnChangeListener(new OnChangeListener() {  
              
            @Override  
            public void onChange(SwitchButton sb, boolean state) {  
                // TODO Auto-generated method stub  
                Log.d("switchButton", state ? "��":"��");  
               ChangeService (state);
                //Toast.makeText(EActivity.this, state ? "��":"��", Toast.LENGTH_SHORT).show();  
            }  
        }); 
        sb2.setOnChangeListener(new OnChangeListener() {  
            
            @Override  
            public void onChange(SwitchButton sb, boolean state) {  
                // TODO Auto-generated method stub  
                Log.d("switchButton", state ? "��":"��");  
               AActivity.NeedWatch=state;
               Data app = (Data) getApplication();  
               String NetName = app.getConnectString();
               SQL db = new SQL();
       		   db.Port = NetName;
       		   db.openDataBase();
               if (state)
            	   AActivity.AddLog("͵�ɼ�ط����ѿ���");
               else
            	   AActivity.AddLog("͵�ɼ�ط����ѹر�");
                //Toast.makeText(EActivity.this, state ? "��":"��", Toast.LENGTH_SHORT).show();  
            }  
        }); 
        
	}
	
	private ProgressDialog dialog;
	private class MyUICheckUpdateCallback implements UICheckUpdateCallback {

		@Override
		public void onCheckComplete() {
			Log.v("������·���","�����ϣ�");
			dialog.dismiss();
		}

	}
	@Override  
    protected void onDestroy() {  
        super.onDestroy();  

  		//��¼Activity�˳�
  		Data app = (Data) getApplication();  
          app.activities.remove(this); 
    }  
}
