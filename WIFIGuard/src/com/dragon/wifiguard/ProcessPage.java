package com.dragon.wifiguard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
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
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
/**
 * 超速处理页面
 * @author 林炜哲
 * @version 2.0.0.0
 */
public class ProcessPage extends Activity  {

	private EditText mEditText1, mEditText2;
	private Button returnhome, submit;
	private TextView mExplain;
	private ListView mListView;
	private List<HashMap<String, Object>> mList = new ArrayList<HashMap<String, Object>>();
	private static ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();  
	public static boolean ProcessPageOnShow=false;
	public static String Port="Connection";
	public static int argument;
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	
        return super.onKeyDown(keyCode, event);
    }
    @Override  
    protected void onResume() {  
    	super.onResume();
    	init();
    }
	@Override  
    protected void onDestroy() {  
        super.onDestroy();  

        
		//添加Notification提示用户
		Data app = (Data) AActivity.application;
		
		String[] str=app.getAllStrings();
		ProcessPageOnShow=false;
		if (str[0].equals("nothing")){
			//已经结束了的话
			app.SetIsEnd(true);
			return;
		}
		new Handler().postDelayed(new Runnable(){    
		    public void run() {  
		    	//30秒后重新运行
		    	if (!ProcessPageOnShow){
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
		  		app.DeleteRow();//每执行一次删除一条，刚好删除完。。
			}else break;
		}
		
		 NotificationManager manager = (NotificationManager) AActivity.appContext  
	        .getSystemService(Context.NOTIFICATION_SERVICE);  
	        // 创建一个Notification  
	        Notification notification = new Notification();  
	        // 设置显示在手机最上边的状态栏的图标  
	        notification.icon = R.drawable.logo;
	        // 当当前的notification被放到状态栏上的时候，提示内容  
	        //notification.tickerText = "注意了，我被扔到状态栏了";  
	          
	        /*** 
	         * notification.contentIntent:一个PendingIntent对象，当用户点击了状态栏上的图标时，该Intent会被触发 
	         * notification.contentView:我们可以不在状态栏放图标而是放一个view 
	         * notification.deleteIntent 当当前notification被移除时执行的intent 
	         * notification.vibrate 当手机震动时，震动周期设置 
	         */  
	        // 添加声音提示  
	        notification.defaults=Notification.DEFAULT_SOUND;  
	        // audioStreamType的值必须AudioManager中的值，代表着响铃的模式  
	        notification.audioStreamType= android.media.AudioManager.ADJUST_LOWER;  
	          
	        //下边的两个方式可以添加音乐  
	        //notification.sound = Uri.parse("file:///sdcard/notification/ringer.mp3");   
	        //notification.sound = Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI, "6");   
	        Intent intent = new Intent(AActivity.appContext, ProcessPage.class);  
	        PendingIntent pendingIntent = PendingIntent.getActivity(AActivity.appContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);  
	        // 点击状态栏的图标出现的提示信息设置  
	        notification.setLatestEventInfo(AActivity.appContext, "忽略以下应用超速30秒：",content, pendingIntent);  
	        manager.notify(2, notification);  
        
        
        /*
        Log.e("提醒窗口服务",app.getShowString().toString());
        if (app.getShowString().toString().equals("nothing")) {
			// 没了……
			Log.v("提醒窗口服务", "消息队列结束");

			app.SetIsEnd(true);// 设置结束标志
		} else {
			String info = app.getShowString();
			HWindowManager.createDesktopLayout();
			int a = info.indexOf("||||");
			int b = info.indexOf("||", a + 4);
			int c = info.indexOf("【");
			int d = info.indexOf("】");
			Log.e("^", a + " " + b + " " + c + " " + d + " ");
			String appname = info.substring(0, a);
			String number = info.substring(a + 4, b);
			String type = info.substring(b + 2, c);
			if (type.toString().equals("dn"))
				type = "下载";
			else
				type = "上传";
			HWindowManager.setInfoText(appname, "超速" + type + "，流量使用"
					+ DealData(Integer.valueOf(number)));
			HWindowManager.showDesk();
			Log.v("提醒窗口服务", "启动下一队列" + app.getShowString());
                 */
        
		
    }  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ignoreoption);

		ProcessPageOnShow=true;
		EditText edit1=(EditText)findViewById(R.id.editText1);
		edit1.setText("超速列表");
		mListView=(ListView)findViewById(R.id.mylistview);
		mListView.setOnItemClickListener(new OnItemClickListener() {  
			  
	            @Override  
	            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  
	                    long arg3) { 
	            	
	            	if (arg2!=0){
	            		//按顺序处理来着
	            		Toast.makeText(getApplicationContext(), "请按顺序处理哦亲(^o^)",
	        					Toast.LENGTH_SHORT).show();
	            		return;
	            	}
	            	
	            	AlertDialog.Builder builder = new AlertDialog.Builder(ProcessPage.this);
	                builder.setIcon(R.drawable.ic_launcher);
	                builder.setTitle("请选择对该应用的处理方式");
	                //    指定下拉列表的显示数据
	                final String[] options = {"打开系统页面进行终止","将其加入忽略名单"};
	                //    设置一个下拉的列表选择项
	                argument=arg2;
	                
	                
	                builder.setItems(options, new DialogInterface.OnClickListener()
	                {
	                    @Override
	                    public void onClick(DialogInterface dialog, int which)
	                    {
	                    	String appname=listItem.get(argument).get("ItemTitle").toString();
	                    	if (which==0){
	                    		//终止
	                    		showInstalledAppDetails(null, getPackageNameFromAppName(appname));
	                    	}else{
	                    		//加入忽略列表
	                    		AActivity.SQLRunning=true;
	                    		SQL db=new SQL();
	                    		db.Port="Ignore";
	                    		db.openDataBase();
	                    		if (!getPackageNameFromAppName(appname).equals("Nothing")){
		                    		db.delData(getPackageNameFromAppName(appname).toString());
		                    		db.addData(getPackageNameFromAppName(appname).toString(), "1");
	                    		}
	                    		Data app = (Data) AActivity.application;
	                    		app.readAllIgnoreListAndReset();
	                    	}

	                		Data app = (Data)getApplication();
	                		app.DeleteRow();
	                		init();
	                		
	                    }
	                });
	                builder.show();
	            }  
	        });  
		init();
		
		
		handler.postDelayed(runnable, 1000);
		
		//记录Activity启动
  		Data app = (Data) getApplication();  
          app.activities.add(this); 
          
	}
	
	Handler handler = new Handler();
	/***
	 * 定时器，每1秒刷新内容
	 */
    Runnable runnable = new Runnable(){
        @Override
        public void run() {
            try {
                handler.postDelayed(this, 1000);
                //System.out.println("定时器");
                init();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("定时器错误...");
            }
        }
    };
	
	
	
	/***
	 * 从应用名获得包名
	 */
	private String getPackageNameFromAppName(String appName){
		List<PackageInfo> packages = getPackageManager()
				.getInstalledPackages(0);

		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				//每一个搜索得到的应用程序
				if (packageInfo.applicationInfo.loadLabel(getPackageManager()).toString().equals(appName)){
					return packageInfo.packageName;
				}
			}
		}
		return "Nothing";
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
		Data app = (Data)getApplication();
		String[] str= app.getAllStrings();
		listItem.clear();
		if (str[0].equals("nothing")){
			//已经没有啦
			finish();
			
		}
		
		for (int i=0;i<21;i++){
			if  (!str[i].equals("nothing"))
			{
				String info=str[i];
				int a=info.indexOf("||||");
		  		int b=info.indexOf("||",a+4);
		  		int c=info.indexOf("【");
		  		int d=info.indexOf("】");
		  		String appname=info.substring(0,a);
		  		String number=info.substring(a+4,b);
		  		String type=info.substring(b+2,c);
		  		String packagename=info.substring(c+1,d);

				HashMap<String, Object> map = new HashMap<String, Object>();
				if (type.equals("dn"))
					map.put("ItemText","超速下载总共"+DealData(Integer.valueOf(number)));
				else
					map.put("ItemText","超速上传总共"+DealData(Integer.valueOf(number)));
				map.put("ItemTitle", appname);
				
				
				//获得应用图标
				try {  
					PackageManager pm ;  
					pm = this.getPackageManager();  
		             ApplicationInfo pmif = pm.getApplicationInfo(packagename, 0);   
		             map.put("ItemImage", pmif.loadIcon(pm));
		        } catch (NameNotFoundException e) {  
		            // TODO Auto-generated catch block  
		        	map.put("ItemImage", R.drawable.wifi);
		            e.printStackTrace();  
		        }  
				
				
				
				listItem.add(map);
			}else break;
		}
		
        
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
	
	private static final String SCHEME = "package";
	/**
	 * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.1及之前版本)
	 */
	private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
	/**
	 * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.2)
	 */
	private static final String APP_PKG_NAME_22 = "pkg";
	/**
	 * InstalledAppDetails所在包名
	 */
	private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
	/**
	 * InstalledAppDetails类名
	 */
	private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";
	/**
	 * 调用系统InstalledAppDetails界面显示已安装应用程序的详细信息。 对于Android 2.3（Api Level
	 * 9）以上，使用SDK提供的接口； 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）。
	 * 
	 * @param context
	 * 
	 * @param packageName
	 *            应用程序的包名
	 */
	public static void showInstalledAppDetails(Context context, String packageName) {
		Intent intent = new Intent();

			intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
			Uri uri = Uri.fromParts(SCHEME, packageName, null);
			intent.setData(uri);
		AActivity.appContext.startActivity(intent);
		

	}
}
