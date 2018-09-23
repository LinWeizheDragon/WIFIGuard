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
 * MainActivity:Ӧ����Ϣ�б����
 * @author �����
 * @version 1.5.0.2
 */
public class MainActivity extends Activity {

	public NotificationManager notificationManager;  
	/**ˢ���Ƿ����*/
	private boolean IsEnd=true;
	private int i = 0;  
	/**��ʱ�����*/
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
		//��Layout�����ListView  
        list = (ListView) findViewById(R.id.listview1);  
        refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);
        refreshableView.setOnRefreshListener(new PullToRefreshListener() {
			@Override
			public void onRefresh() {
				try {
					//������ʱ����ִ��һ��
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
        //handler.postDelayed(runnable, TIME); //ÿ��1sִ�� 
        //��ӵ��  
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
            	desstr=desstr+"Ӧ�ð�����\n"+ListInfo.packageName[arg2]+"\n";
            	desstr=desstr+("������������\n"+DealData(dataInfo.InitDownBytes[arg2])+"\n");
            	desstr=desstr+("���ϴ�������\n"+DealData(dataInfo.InitUpBytes[arg2])+"\n");
            	desstr=desstr+("ϵͳ��¼������������\n"+DealData(ListInfo.InitDownBytes[arg2])+"\n");
            	desstr=desstr+("ϵͳ��¼���ϴ�������\n"+DealData(ListInfo.InitUpBytes[arg2])+"\n");
            	//AActivity.AddLog(getPermisson(ListInfo.packageName[arg2]));
            	//��ȡȨ�����׳�����
            	AppInfoBox.apppak=ListInfo.packageName[arg2];
            	AppInfoBox.mode=1;
            	AppInfoBox.desstr=desstr;
            	startActivity(intent);
            	
                setTitle("�����"+arg2+"����Ŀ"); 
                
            }  
        });  
          
        //NotificationExtend Noti=new NotificationExtend(this);
        //Noti.showNotification();
        ///ShowNotification();
        //CreateInfomation();
      //��ӳ������  
       /* list.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {  
              
            @Override  
            public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {  
                menu.setHeaderTitle("�����˵�-ContextMenu");     
                menu.add(0, 0, 0, "���������˵�0");  
                menu.add(0, 1, 0, "���������˵�1");     
            }  
        });   */
        

		
    }  
	/**
	   * ��ȡ�汾��
	   * @return ��ǰӦ�õİ汾��
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
		return "��ȡʧ��";
	}
	private String getPermisson(String packagename) {  
        try {  
        	String str="";
            PackageManager pm = this.getApplicationContext().getPackageManager();  
            PackageInfo pi = pm.getPackageInfo(packagename, 0);  
            // �õ��Լ��İ���  
            String pkgName = packagename;  
  
            PackageInfo pkgInfo = pm.getPackageInfo(pkgName,  
                    PackageManager.GET_PERMISSIONS);//ͨ�����������ذ���Ϣ  
            String sharedPkgList[] = pkgInfo.requestedPermissions;//�õ�Ȩ���б�  
  
            for (int i = 0; i < sharedPkgList.length; i++) {  
                String permName = sharedPkgList[i];  
  
                Log.v("Debug",permName);
                PermissionInfo tmpPermInfo = pm.getPermissionInfo(permName, 0);//ͨ��permName�õ���Ȩ�޵���ϸ��Ϣ  
                PermissionGroupInfo pgi = pm.getPermissionGroupInfo(  
                        tmpPermInfo.group, 0);//Ȩ�޷�Ϊ��ͬ��Ⱥ�飬ͨ��Ȩ���������ǵõ���Ȩ������ʲô���͵�Ȩ�ޡ�  
                
                str=str+(i + "-" + permName + "\n");  
                str=str+(i + "-" + pgi.loadLabel(pm).toString() + "\n");  
                str=str+(i + "-" + tmpPermInfo.loadLabel(pm).toString()+ "\n");  
                str=str+(i + "-" + tmpPermInfo.loadDescription(pm).toString()+ "\n");  
                str=str+("����������������������������" + "\n");  
        
            }  
            return str;
        } catch (NameNotFoundException e) {  
            Log.e("##ddd", "Could'nt retrieve permissions for package");  
  
        }
		return "��ȡʧ��";  
	}
	
	@Override  
    protected void onStop() {  
        super.onStop();  
        //moveTaskToBack(true);
    }  
	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	// ���HOME��ʱ��������̨����
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
            // handler�Դ�����ʵ�ֶ�ʱ��  
            try {  
            	IsEnd=false;
                handler.postDelayed(this, TIME);  
                setTitle("ˢ����");
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
     * ��������������ʾ
     */
    public void CreateInfomation(){
    	Data app = (Data)getApplication();
    	app.setShowString("��������");
    	Intent intent = new Intent(MainActivity.this, FloatWindowService.class);
		startService(intent);
    }
    /**
     * ����ˢ�²���
     */
    @SuppressWarnings("unchecked")
	public void  action()
    {
    	resetList();
    	ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();  
    	 
    	//AActivity.AddLog("datainfo��"+dataInfo.Num);
    
        for (int i=0;i<dataInfo.Num;i++)
        {
        HashMap<String, Object> map = new HashMap<String, Object>();  
        map.put("ItemText","���أ�"+ DealData(dataInfo.InitDownBytes[i])+
    			"  �ϴ���"+ DealData((dataInfo.InitUpBytes[i])));  
        map.put("ItemImage", listinfo.appIcon[i]);
		map.put("ItemTitle", listinfo.appName[i]);
		map.put("down", dataInfo.InitDownBytes[i]);
		map.put("up", dataInfo.InitUpBytes[i]);
		
		listItem.add(map);
        }
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
     * ��ʼ��ˢ���б��ȡ��Ϣ
     */
    public void  refresh()
    {
    	resetList();
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);

        //��������  
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
        	map.put("ItemText","���أ�"+ DealData(dataInfo.InitDownBytes[n])+
        			"  �ϴ���"+ DealData(dataInfo.InitUpBytes[n]));  
            map.put("ItemTitle", dataInfo.appName[n]);  //�����ϵͳӦ�ã��������appList
            map.put("ItemImage",listinfo.appIcon[n]);
            listItem.add(map);  
            //n++;
        
        }
        
        //������������Item�Ͷ�̬�����Ӧ��Ԫ��  
        SimpleAdapter listItemAdapter = new SimpleAdapter(this,listItem,//����Դ   
            R.layout.listlayout,//ListItem��XMLʵ��  
            //��̬������ImageItem��Ӧ������          
            new String[] {"ItemImage","ItemTitle", "ItemText"},   
            //ImageItem��XML�ļ������һ��ImageView,����TextView ID  
            new int[] {R.id.ItemImage,R.id.ItemTitle,R.id.ItemText}  
        );  
        
        //��Ӳ�����ʾ  
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

	// �����˵���Ӧ����
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		setTitle("����˳����˵�����ĵ�" + item.getItemId() + "����Ŀ");
		return super.onContextItemSelected(item);
	}

	public void resetList() {
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		listItem.clear();
		SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem,// ����Դ
				R.layout.listlayout,// ListItem��XMLʵ��
				// ��̬������ImageItem��Ӧ������
				new String[] { "ItemImage", "ItemTitle", "ItemText" },
				// ImageItem��XML�ļ������һ��ImageView,����TextView ID
				new int[] { R.id.ItemImage, R.id.ItemTitle, R.id.ItemText });
	}

	

}
