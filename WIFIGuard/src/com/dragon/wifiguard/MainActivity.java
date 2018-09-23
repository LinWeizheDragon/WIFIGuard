package com.dragon.wifiguard;

import java.util.ArrayList;  
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;  
import java.util.List;

import com.dragon.wifiguard.FloatWindowService;
import com.dragon.wifiguard.ScreenUtils;
import com.dragon.wifiguard.R;
import com.dragon.wifiguard.RefreshableView;
import com.dragon.wifiguard.RefreshableView.PullToRefreshListener;
import com.dragon.wifiguard.MainActivity;
  
import android.app.Activity;  
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;  
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;  
import android.view.KeyEvent;
import android.view.MenuItem;  
import android.view.View;  
import android.view.ContextMenu.ContextMenuInfo;  
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;  
import android.widget.AdapterView;  
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;  
import android.widget.RemoteViews;
import android.widget.SimpleAdapter;  
import android.widget.AdapterView.OnItemClickListener;  
import android.widget.SimpleAdapter.ViewBinder;
import android.net.TrafficStats;

/**
 * MainActivity:应用信息列表界面
 * @author 林炜哲
 * @version 1.5.0.2
 */
public class MainActivity extends Activity {

	public NotificationManager notificationManager;  
	/**刷新是否完成*/
	private boolean IsEnd=true;
	private int i = 0;  
	/**定时器间隔*/
    private int TIME = 1;  
    private ListView list;
    private ListInfo listinfo=new ListInfo();
    private int Num=0;
    private RefreshableView refreshableView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        ScreenUtils.initScreen(this);
		//绑定Layout里面的ListView  
        list = (ListView) findViewById(R.id.listview1);  
        refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);
        refreshableView.setOnRefreshListener(new PullToRefreshListener() {
			@Override
			public void onRefresh() {
				try {
					//启动计时器，执行一次
					handler.postDelayed(runnable, TIME); 
					Thread.sleep(1000);
					while (IsEnd=false)
					{
						Thread.sleep(500);
					}
					
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
				refreshableView.finishRefreshing();
				
			}
		}, 0);
        
        refresh();
        //handler.postDelayed(runnable, TIME); //每隔1s执行 
        //添加点击  
        list.setOnItemClickListener(new OnItemClickListener() {  
  
            @Override  
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  
                    long arg3) {  

            	Intent intent = new Intent(MainActivity.this, AppInfoBox.class);
            	AppInfoBox.appname=ListInfo.appName[arg2];
            	AppInfoBox.appver=getVersion(ListInfo.packageName[arg2]);
            	AppInfoBox.appicon=ListInfo.appIcon[arg2];
            	//AppInfoBox.desstr=getPermisson(ListInfo.packageName[arg2]);
            	String desstr="";
            	desstr=desstr+"应用包名：\n"+ListInfo.packageName[arg2]+"\n";
            	desstr=desstr+("已下载流量：\n"+DealData(dataInfo.InitDownBytes[arg2])+"\n");
            	desstr=desstr+("已上传流量：\n"+DealData(dataInfo.InitUpBytes[arg2])+"\n");
            	desstr=desstr+("系统记录已下载流量：\n"+DealData(ListInfo.InitDownBytes[arg2])+"\n");
            	desstr=desstr+("系统记录已上传流量：\n"+DealData(ListInfo.InitUpBytes[arg2])+"\n");
            	//AActivity.AddLog(getPermisson(ListInfo.packageName[arg2]));
            	//获取权限容易出错……
            	AppInfoBox.apppak=ListInfo.packageName[arg2];
            	AppInfoBox.mode=1;
            	AppInfoBox.desstr=desstr;
            	startActivity(intent);
            	
                setTitle("点击第"+arg2+"个项目"); 
                
            }  
        });  
          
        //NotificationExtend Noti=new NotificationExtend(this);
        //Noti.showNotification();
        ///ShowNotification();
        //CreateInfomation();
      //添加长按点击  
       /* list.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {  
              
            @Override  
            public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {  
                menu.setHeaderTitle("长按菜单-ContextMenu");     
                menu.add(0, 0, 0, "弹出长按菜单0");  
                menu.add(0, 1, 0, "弹出长按菜单1");     
            }  
        });   */
        

		
    }  
	/**
	   * 获取版本号
	   * @return 当前应用的版本号
	   */
	public String getVersion(String PackageName) {
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(PackageName, 0);
			String version = info.versionName;
			return  version;
		} catch (Exception e) {
			e.printStackTrace();
			//this.getString(R.string.can_not_find_version_name);
		}
		return "获取失败";
	}
	private String getPermisson(String packagename) {  
        try {  
        	String str="";
            PackageManager pm = this.getApplicationContext().getPackageManager();  
            PackageInfo pi = pm.getPackageInfo(packagename, 0);  
            // 得到自己的包名  
            String pkgName = packagename;  
  
            PackageInfo pkgInfo = pm.getPackageInfo(pkgName,  
                    PackageManager.GET_PERMISSIONS);//通过包名，返回包信息  
            String sharedPkgList[] = pkgInfo.requestedPermissions;//得到权限列表  
  
            for (int i = 0; i < sharedPkgList.length; i++) {  
                String permName = sharedPkgList[i];  
  
                Log.v("Debug",permName);
                PermissionInfo tmpPermInfo = pm.getPermissionInfo(permName, 0);//通过permName得到该权限的详细信息  
                PermissionGroupInfo pgi = pm.getPermissionGroupInfo(  
                        tmpPermInfo.group, 0);//权限分为不同的群组，通过权限名，我们得到该权限属于什么类型的权限。  
                
                str=str+(i + "-" + permName + "\n");  
                str=str+(i + "-" + pgi.loadLabel(pm).toString() + "\n");  
                str=str+(i + "-" + tmpPermInfo.loadLabel(pm).toString()+ "\n");  
                str=str+(i + "-" + tmpPermInfo.loadDescription(pm).toString()+ "\n");  
                str=str+("——————————————" + "\n");  
        
            }  
            return str;
        } catch (NameNotFoundException e) {  
            Log.e("##ddd", "Could'nt retrieve permissions for package");  
  
        }
		return "获取失败";  
	}
	
	@Override  
    protected void onStop() {  
        super.onStop();  
        //moveTaskToBack(true);
    }  
	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	// 点击HOME键时程序进入后台运行
        if(keyCode == KeyEvent.KEYCODE_HOME){
            moveTaskToBack(true);                
            return true;
        }
        if(keyCode == KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);                
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
	
	Handler handler = new Handler();  
    Runnable runnable = new Runnable() {  
        @Override  
        public void run() {  
            // handler自带方法实现定时器  
            try {  
            	IsEnd=false;
                handler.postDelayed(this, TIME);  
                setTitle("刷新了");
                action();
                System.out.println("do...");  
            } catch (Exception e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
                System.out.println("exception...");  
            }  
            IsEnd=true;
            handler.removeCallbacks(runnable);
        }  
    };
    /**
     * 启动浮动窗口提示
     */
    public void CreateInfomation(){
    	Data app = (Data)getApplication();
    	app.setShowString("哈哈哈哈");
    	Intent intent = new Intent(MainActivity.this, FloatWindowService.class);
		startService(intent);
    }
    /**
     * 进行刷新操作
     */
    @SuppressWarnings("unchecked")
	public void  action()
    {
    	resetList();
    	ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();  
    	 
    	//AActivity.AddLog("datainfo有"+dataInfo.Num);
    
        for (int i=0;i<dataInfo.Num;i++)
        {
        HashMap<String, Object> map = new HashMap<String, Object>();  
        map.put("ItemText","下载："+ DealData(dataInfo.InitDownBytes[i])+
    			"  上传："+ DealData((dataInfo.InitUpBytes[i])));  
        map.put("ItemImage", listinfo.appIcon[i]);
		map.put("ItemTitle", listinfo.appName[i]);
		map.put("down", dataInfo.InitDownBytes[i]);
		map.put("up", dataInfo.InitUpBytes[i]);
		
		listItem.add(map);
        }
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
        list.setAdapter(listItemAdapter);  
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
    /**
     * 初始化刷新列表获取信息
     */
    public void  refresh()
    {
    	resetList();
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);

        //加入数据  
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();  
        Num=listinfo.Num;
        int n=0;
        //listinfo.Rebuild(Num);
        
        for(n=0;n<Num;n++) { 

        		
        HashMap<String, Object>  map = new HashMap<String, Object>();  
        /*
        AppInfo tmpInfo = new AppInfo(); 
        tmpInfo.appName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString(); 
        tmpInfo.packageName = packageInfo.packageName; 
        tmpInfo.versionName = packageInfo.versionName; 
        tmpInfo.versionCode = packageInfo.versionCode; 
        listinfo.appName[n]=tmpInfo.appName;
        listinfo.packageName[n]=tmpInfo.packageName;
        listinfo.versionName[n]=tmpInfo.versionName;
        listinfo.versionCode[n]=tmpInfo.versionCode;
        try {
            PackageManager pm = getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(tmpInfo.packageName, PackageManager.GET_ACTIVITIES);
            tmpInfo.appUid=ai.uid;
            listinfo.appUid[n]=ai.uid;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        //listinfo.InitDownBytes[n]=10000;
        listinfo.InitDownBytes[n]=(int) TrafficStats.getUidRxBytes(listinfo.appUid[n]);
        //listinfo.InitUpBytes[n]=(int) TrafficStats.getUidTxBytes(listinfo.appUid[n]);
        tmpInfo.appIcon = packageInfo.applicationInfo.loadIcon(getPackageManager());
       //Log.v("BreakPoint11111", "dd"+listinfo.InitUpBytes[n]);
        	*/
        	map.put("ItemText","下载："+ DealData(dataInfo.InitDownBytes[n])+
        			"  上传："+ DealData(dataInfo.InitUpBytes[n]));  
            map.put("ItemTitle", dataInfo.appName[n]);  //如果非系统应用，则添加至appList
            map.put("ItemImage",listinfo.appIcon[n]);
            listItem.add(map);  
            //n++;
        
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
        list.setAdapter(listItemAdapter);  
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

	// 长按菜单响应函数
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		setTitle("点击了长按菜单里面的第" + item.getItemId() + "个项目");
		return super.onContextItemSelected(item);
	}

	public void resetList() {
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		listItem.clear();
		SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem,// 数据源
				R.layout.listlayout,// ListItem的XML实现
				// 动态数组与ImageItem对应的子项
				new String[] { "ItemImage", "ItemTitle", "ItemText" },
				// ImageItem的XML文件里面的一个ImageView,两个TextView ID
				new int[] { R.id.ItemImage, R.id.ItemTitle, R.id.ItemText });
	}

	

}
