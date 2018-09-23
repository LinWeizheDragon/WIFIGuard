package com.dragon.wifiguard;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;
/**
 * 并没有用到的BActivity，被初始制作的MainActivity替代作用
 * @author 林炜哲
 */
public class BActivity extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TextView tv = new TextView(this);
		tv.setText("This is B Activity!");
		tv.setGravity(Gravity.CENTER);
		setContentView(tv);
		//记录Activity启动
  		Data app = (Data) getApplication();  
          app.activities.add(this); 
          
	}
	@Override
	protected void onDestroy() {  
        super.onDestroy();  

  		//记录Activity退出
  		Data app = (Data) getApplication();  
          app.activities.remove(this); 
  
    }  
}
