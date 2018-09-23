package com.dragon.wifiguard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
/**
 * DActivity:���ӹ���ҳ��
 * @author �����
 * @version 2.0.3.0
 *   �����������ַ�WIFI���ݿ�����������
 */
public class DActivity extends Activity  {

	private EditText mEditText1, mEditText2;
	private Button returnhome, submit;
	private TextView mExplain;
	private ListView mListView;
	private List<HashMap<String, Object>> mList = new ArrayList<HashMap<String, Object>>();
	  
	public static String Port="Connection";
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
    @Override  
    protected void onResume() {  
    	super.onResume();
    	init();
    }
	@Override  
    protected void onDestroy() {  
        super.onDestroy();  

  		//��¼Activity�˳�
  		Data app = (Data) getApplication();  
          app.activities.remove(this); 
    }  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_preferences);

		mListView=(ListView)findViewById(R.id.mylistview);
		mListView.setOnItemClickListener(new OnItemClickListener() {  
			  
	            @Override  
	            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  
	                    long arg3) { 
	            	Intent intent = new Intent(DActivity.this, AppInfoBox.class);
                	
	            	AppInfoBox.appname=mList.get(arg2).get("name").toString();
	            	AppInfoBox.mode=2;
	            	AppInfoBox.appver=mList.get(arg2).get("pswd").toString();
	            	

	            	String str = "";
	            	
	            	List<HashMap<String, Object>> newList = new ArrayList<HashMap<String, Object>>();
	          	  
	            	AActivity.SQLRunning=true;
	        		SQL db=new SQL();
	        		db.Port=mList.get(arg2).get("name").toString();
	        		  db.openDataBase();
	        		// db.delAll();
	        		// db.addData("WIFI1", "1");
	        		  newList = db.getData(db.Port);
	        		///ͳ��
	        		    int x=0;
	        			int y=0;
	        		for (int i = 0; i < newList.size(); i++) {
	        			//ͳ����������
	        			int split = newList.get(i).get("pswd").toString().indexOf("|",0);
        				String a=newList.get(i).get("pswd").toString().substring(0, split).toString();
        				String b=newList.get(i).get("pswd").toString().substring(split+1,newList.get(i).get("pswd").toString().length()).toString();
        				
	        			x+=Integer.valueOf(a);
        				y+=Integer.valueOf(b);
        				
	        			str=str+newList.get(i).get("name").toString()+"\n"
	        					+"����:"+DealData(Integer.valueOf(a))+"\n�ϴ�"+DealData(Integer.valueOf(b))+"\n";
	        			
	        		}
	            	str="������:"+DealData(x)+"\n���ϴ�"+DealData(y)+"\n"+str;
	        		
	        			AppInfoBox.desstr=str;
	        			
	        			startActivity(intent);
	            	//AppInfoBox.appicon=ListInfo.appIcon[arg2];
	        			AActivity.SQLRunning=false;
	            }  
	        });  
		init();
		
		//��¼Activity����
  		Data app = (Data) getApplication();  
          app.activities.add(this); 
          
	}
	/**
	   * ���ֽ�������Ϊ������ʽ
	   * @param a
	   *    ������ֽ���
	   */
	  public String DealData(int a)
	  {
	  	double data=a;
	  	String NewString="������";
	  	if  (data==-1)
	  	{
	  		return NewString;
	  	}
	  	if (data<1024&&data>=0)
	  		NewString=data +"B";
	  	if (data>=1024&&data<=1048576)
	  		NewString = Math.round((data/1024*10000)/10000.0)+"KB" ;
	  	if (data>=1048576&&data<=1073741824)
	  		NewString = Math.round((data/1024/1024*10000)/10000.0) +"MB" ;
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
	   * ��ʼ������
	   */
	public void init(){
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();  
		
		Data app = (Data) getApplication();  
    	//AActivity.AddLog("datainfo��"+dataInfo.Num);
		AActivity.SQLRunning=true;
		SQL db=new SQL();
		db.Port="Connection";
		  db.openDataBase();
		// db.delAll();
		// db.addData("WIFI1", "1");
		mList = db.getData( db.Port);
		Log.d("ConnectList",mList.size()+"");
		for (int i = 0; i < mList.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();

			Log.d("hehe",i+"");
			map.put("ItemTitle", mList.get(i).get("name"));
			//map.put("ItemText", mList.get(i).get("pswd"));
			int mode=Integer.parseInt(mList.get(i).get("pswd").toString());
			//map.put("ItemImage", R.drawable.ic_launcher);
			if (mode==2){
				//��������
				map.put("ItemText","��������");
				map.put("ItemImage", R.drawable.mobile);
			}
			else if (mode==1)
			{
				//WIFI ������
				map.put("ItemText","WIFI-������");
				map.put("ItemImage", R.drawable.wifi);
			}
			else if (mode==3){
				//WIFI ��Ҫ���
				map.put("ItemText","WIFI-��Ҫ���");
				map.put("ItemImage", R.drawable.wifi);
			}
			listItem.add(map);
		}
		AActivity.SQLRunning=false;
        //Comparator comp = new Mycomparator();
        //Collections.sort(listItem,comp); 
        
    	//������������Item�Ͷ�̬�����Ӧ��Ԫ��  
        SimpleAdapter listItemAdapter = new SimpleAdapter(this,listItem,//����Դ   
            R.layout.listlayout,//ListItem��XMLʵ��  
            //��̬������ImageItem��Ӧ������          
            new String[] {"ItemImage","ItemTitle", "ItemText"},   
            //ImageItem��XML�ļ������һ��ImageView,����TextView ID  
            new int[] {R.id.ItemImage,R.id.ItemTitle,R.id.ItemText}  
        );  
         
      //��Ӳ�����ʾ  
        mListView.setAdapter(listItemAdapter);  
        listItemAdapter.setViewBinder(new ViewBinder(){
            public boolean setViewValue(View view,Object data,String textRepresentation){
            	          if(view instanceof ImageView && data instanceof Drawable){
            	               ImageView iv=(ImageView)view;
                                iv.setImageDrawable((Drawable)data);
                             return true;
                           }
                           else return false;
            	           }
                   });
	}
}
