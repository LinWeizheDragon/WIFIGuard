package com.dragon.wifiguard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * ���Թ������
 * @author �����
 * @version 2.0.0.3
 */
public class IgnorePage extends Activity  {

	private EditText mEditText1, mEditText2;
	private Button returnhome, submit;
	private TextView mExplain;
	private ListView mListView;
	private List<HashMap<String, Object>> mList = new ArrayList<HashMap<String, Object>>();
	private static ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();  
	
	public static String Port="Connection";
	public static int argument;
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	
        return super.onKeyDown(keyCode, event);
    }
	public void exit(){  
        
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
  		//Data app = (Data) getApplication();  
          //app.activities.remove(this); 
    }  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ignoreoption);

		mListView=(ListView)findViewById(R.id.mylistview);
		mListView.setOnItemClickListener(new OnItemClickListener() {  
			  
	            @Override  
	            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  
	                    long arg3) { 
	            	AlertDialog.Builder builder = new AlertDialog.Builder(IgnorePage.this);
	                builder.setIcon(R.drawable.ic_launcher);
	                builder.setTitle("��ѡ��Ը�Ӧ�õĴ���ʽ");
	                //    ָ�������б����ʾ����
	                final String[] options = {"���","����"};
	                //    ����һ���������б�ѡ����
	                argument=arg2;
	                //Log.e("����",listItem.get(arg2).get("ItemTitle").toString());
	                
	                
	                
	                
	                
	                builder.setItems(options, new DialogInterface.OnClickListener()
	                {
	                    @Override
	                    public void onClick(DialogInterface dialog, int which)
	                    {
	                    	
	                		List<PackageInfo> packages = getPackageManager()
	                				.getInstalledPackages(0);
	                		for (int i = 0; i < packages.size(); i++) {
	                			PackageInfo packageInfo = packages.get(i);
	                			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
	                				if (packageInfo.applicationInfo.loadLabel(getPackageManager()).toString().equals(listItem.get(argument).get("ItemTitle").toString()))
	                				{
	                					AActivity.SQLRunning=true;
	    		                		SQL db=new SQL();
	    		                		db.Port="Ignore";
	    		                		db.openDataBase();
	    		                		String appName=listItem.get(argument).get("ItemTitle").toString();
	    		                    	db.delData(packageInfo.packageName);
	    		                    	AActivity.AddLog("�޸Ķ�Ӧ��"+appName+"�ļ��״̬");
	    		                        if (which==0){
	    		                        	//���
	    		                        	db.addData(packageInfo.packageName,"0");
	    		                        }
	    		                        else{
	    		                        	//����
	    		                        	db.addData(packageInfo.packageName,"1");
	    		                        }
	    		                        AActivity.SQLRunning=false;
	    		                        Data app = (Data) AActivity.application;
	    		                		app.readAllIgnoreListAndReset();
	    		                        init();
	                				}
	                			}
	                		}
	                			
	                    }
	                });
	                builder.show();
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
	public void init(){

		
		
		AActivity.SQLRunning=true;
		SQL db=new SQL();
		db.Port="Ignore";
		db.openDataBase();
		// db.delAll();
		// db.addData("WIFI1", "1");
		mList.clear();
		mList = db.getData( db.Port);
		//Log.d("ConnectList",mList.size()+"");
		List<PackageInfo> packages = getPackageManager()
				.getInstalledPackages(0);
		Boolean Found=false;
		listItem.clear();
		
		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				//���ÿһ�������õ���Ӧ�ó���
				//�����������ȶ�
				Found=false;
				for (int x = 0; x < mList.size(); x++) {
					if (mList.get(x).get("name").toString().equals(packageInfo.packageName.toString())){
						//����У���ô��ӵ��б�
						HashMap<String, Object> map = new HashMap<String, Object>();
						int mode=Integer.parseInt(mList.get(x).get("pswd").toString());

						/*Log.e("����",packageInfo.applicationInfo.loadLabel(
								getPackageManager()).toString()+"��������" +
						app.getIgnoredOrNot(packageInfo.packageName.toString()));*/
						if (mode==0){
							//������
							map.put("ItemText","���");
							map.put("ItemImage", packageInfo.applicationInfo.loadIcon(
									getPackageManager()).getCurrent());
						}
						else if (mode==1)
						{
							//����
							map.put("ItemText","����");
							map.put("ItemImage", packageInfo.applicationInfo.loadIcon(
									getPackageManager()).getCurrent());
						}
						map.put("ItemTitle", packageInfo.applicationInfo.loadLabel(
								getPackageManager()).toString());
						listItem.add(map);
					}
				}
			}
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
