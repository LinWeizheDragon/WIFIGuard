package com.dragon.wifiguard;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Service;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class PublicFunction extends Service{
	
	public void onCreate() {  
		Log.v("��������","OnCreated.");
	}
	/**
	   * ��������������ʾ
	   */
	  public void CreateInformation(){
		  Data app = (Data)getApplication();
			String info=app.getShowString();
			HWindowManager.createWindowManager();
	  		HWindowManager.createDesktopLayout();
	  		int a=info.indexOf("||||");
	  		int b=info.indexOf("||",a+4);
	  		int c=info.indexOf("��");
	  		int d=info.indexOf("��");
	  		Log.e("^",a+" "+b+" "+c+" "+d+" ");
	  		String appname=info.substring(0,a);
	  		String number=info.substring(a+4,b);
	  		String type=info.substring(b+2,c);
	  		String packagename=info.substring(c+1,d);
	  		Log.e("^",appname+" "+number+" "+type+" "+packagename+" ");
	  		if (type.toString().equals("dn"))
	  			type="����";
	  		else
	  			type="�ϴ�";
	  		HWindowManager.setInfoText(appname, "����"+type+"������ʹ��"+ DealData(Integer.valueOf(number)));
	  		HWindowManager.showDesk();
	  		
	  }
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v("��������","OnStarted.");
		CreateInformation();
		//stopSelf(startId);
		return super.onStartCommand(intent, flags, startId);
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
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
		
	}
	
}