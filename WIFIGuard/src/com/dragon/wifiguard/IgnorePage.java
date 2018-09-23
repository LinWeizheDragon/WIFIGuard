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
 * 忽略管理界面
 * @author 林炜哲
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

  		//记录Activity退出
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
	                builder.setTitle("请选择对该应用的处理方式");
	                //    指定下拉列表的显示数据
	                final String[] options = {"监控","忽略"};
	                //    设置一个下拉的列表选择项
	                argument=arg2;
	                //Log.e("参数",listItem.get(arg2).get("ItemTitle").toString());
	                
	                
	                
	                
	                
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
	    		                    	AActivity.AddLog("修改对应用"+appName+"的监控状态");
	    		                        if (which==0){
	    		                        	//监控
	    		                        	db.addData(packageInfo.packageName,"0");
	    		                        }
	    		                        else{
	    		                        	//忽略
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
		
		//记录Activity启动
  		Data app = (Data) getApplication();  
          app.activities.add(this); 
          
	}
	/**
	   * 将字节数处理为正常形式
	   * @param a
	   *    传入的字节数
	   */
	  public String DealData(int a)
	  {
	  	double data=a;
	  	String NewString="无数据";
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
				//添加每一个搜索得到的应用程序
				//与已有名单比对
				Found=false;
				for (int x = 0; x < mList.size(); x++) {
					if (mList.get(x).get("name").toString().equals(packageInfo.packageName.toString())){
						//如果有，那么添加到列表
						HashMap<String, Object> map = new HashMap<String, Object>();
						int mode=Integer.parseInt(mList.get(x).get("pswd").toString());

						/*Log.e("测试",packageInfo.applicationInfo.loadLabel(
								getPackageManager()).toString()+"————" +
						app.getIgnoredOrNot(packageInfo.packageName.toString()));*/
						if (mode==0){
							//不忽略
							map.put("ItemText","监控");
							map.put("ItemImage", packageInfo.applicationInfo.loadIcon(
									getPackageManager()).getCurrent());
						}
						else if (mode==1)
						{
							//忽略
							map.put("ItemText","忽略");
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
        
    	//生成适配器的Item和动态数组对应的元素  
        SimpleAdapter listItemAdapter = new SimpleAdapter(this,listItem,//数据源   
            R.layout.listlayout,//ListItem的XML实现  
            //动态数组与ImageItem对应的子项          
            new String[] {"ItemImage","ItemTitle", "ItemText"},   
            //ImageItem的XML文件里面的一个ImageView,两个TextView ID  
            new int[] {R.id.ItemImage,R.id.ItemTitle,R.id.ItemText}  
        );  
         
      //添加并且显示  
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
