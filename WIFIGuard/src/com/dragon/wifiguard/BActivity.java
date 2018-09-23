package com.dragon.wifiguard;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;
/**
 * ��û���õ���BActivity������ʼ������MainActivity�������
 * @author �����
 */
public class BActivity extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TextView tv = new TextView(this);
		tv.setText("This is B Activity!");
		tv.setGravity(Gravity.CENTER);
		setContentView(tv);
		//��¼Activity����
  		Data app = (Data) getApplication();  
          app.activities.add(this); 
          
	}
	@Override
	protected void onDestroy() {  
        super.onDestroy();  

  		//��¼Activity�˳�
  		Data app = (Data) getApplication();  
          app.activities.remove(this); 
  
    }  
}
