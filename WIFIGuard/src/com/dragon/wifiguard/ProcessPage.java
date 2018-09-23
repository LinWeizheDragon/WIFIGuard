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
 * ���ٴ���ҳ��
 * @author �����
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

        
		//���Notification��ʾ�û�
		Data app = (Data) AActivity.application;
		
		String[] str=app.getAllStrings();
		ProcessPageOnShow=false;
		if (str[0].equals("nothing")){
			//�Ѿ������˵Ļ�
			app.SetIsEnd(true);
			return;
		}
		new Handler().postDelayed(new Runnable(){    
		    public void run() {  
		    	//30�����������
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
		  		app.DeleteRow();//ÿִ��һ��ɾ��һ�����պ�ɾ���ꡣ��
			}else break;
		}
		
		 NotificationManager manager = (NotificationManager) AActivity.appContext  
	        .getSystemService(Context.NOTIFICATION_SERVICE);  
	        // ����һ��Notification  
	        Notification notification = new Notification();  
	        // ������ʾ���ֻ����ϱߵ�״̬����ͼ��  
	        notification.icon = R.drawable.logo;
	        // ����ǰ��notification���ŵ�״̬���ϵ�ʱ����ʾ����  
	        //notification.tickerText = "ע���ˣ��ұ��ӵ�״̬����";  
	          
	        /*** 
	         * notification.contentIntent:һ��PendingIntent���󣬵��û������״̬���ϵ�ͼ��ʱ����Intent�ᱻ���� 
	         * notification.contentView:���ǿ��Բ���״̬����ͼ����Ƿ�һ��view 
	         * notification.deleteIntent ����ǰnotification���Ƴ�ʱִ�е�intent 
	         * notification.vibrate ���ֻ���ʱ������������ 
	         */  
	        // ���������ʾ  
	        notification.defaults=Notification.DEFAULT_SOUND;  
	        // audioStreamType��ֵ����AudioManager�е�ֵ�������������ģʽ  
	        notification.audioStreamType= android.media.AudioManager.ADJUST_LOWER;  
	          
	        //�±ߵ�������ʽ�����������  
	        //notification.sound = Uri.parse("file:///sdcard/notification/ringer.mp3");   
	        //notification.sound = Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI, "6");   
	        Intent intent = new Intent(AActivity.appContext, ProcessPage.class);  
	        PendingIntent pendingIntent = PendingIntent.getActivity(AActivity.appContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);  
	        // ���״̬����ͼ����ֵ���ʾ��Ϣ����  
	        notification.setLatestEventInfo(AActivity.appContext, "��������Ӧ�ó���30�룺",content, pendingIntent);  
	        manager.notify(2, notification);  
        
        
        /*
        Log.e("���Ѵ��ڷ���",app.getShowString().toString());
        if (app.getShowString().toString().equals("nothing")) {
			// û�ˡ���
			Log.v("���Ѵ��ڷ���", "��Ϣ���н���");

			app.SetIsEnd(true);// ���ý�����־
		} else {
			String info = app.getShowString();
			HWindowManager.createDesktopLayout();
			int a = info.indexOf("||||");
			int b = info.indexOf("||", a + 4);
			int c = info.indexOf("��");
			int d = info.indexOf("��");
			Log.e("^", a + " " + b + " " + c + " " + d + " ");
			String appname = info.substring(0, a);
			String number = info.substring(a + 4, b);
			String type = info.substring(b + 2, c);
			if (type.toString().equals("dn"))
				type = "����";
			else
				type = "�ϴ�";
			HWindowManager.setInfoText(appname, "����" + type + "������ʹ��"
					+ DealData(Integer.valueOf(number)));
			HWindowManager.showDesk();
			Log.v("���Ѵ��ڷ���", "������һ����" + app.getShowString());
                 */
        
		
    }  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ignoreoption);

		ProcessPageOnShow=true;
		EditText edit1=(EditText)findViewById(R.id.editText1);
		edit1.setText("�����б�");
		mListView=(ListView)findViewById(R.id.mylistview);
		mListView.setOnItemClickListener(new OnItemClickListener() {  
			  
	            @Override  
	            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  
	                    long arg3) { 
	            	
	            	if (arg2!=0){
	            		//��˳��������
	            		Toast.makeText(getApplicationContext(), "�밴˳����Ŷ��(^o^)",
	        					Toast.LENGTH_SHORT).show();
	            		return;
	            	}
	            	
	            	AlertDialog.Builder builder = new AlertDialog.Builder(ProcessPage.this);
	                builder.setIcon(R.drawable.ic_launcher);
	                builder.setTitle("��ѡ��Ը�Ӧ�õĴ���ʽ");
	                //    ָ�������б����ʾ����
	                final String[] options = {"��ϵͳҳ�������ֹ","��������������"};
	                //    ����һ���������б�ѡ����
	                argument=arg2;
	                
	                
	                builder.setItems(options, new DialogInterface.OnClickListener()
	                {
	                    @Override
	                    public void onClick(DialogInterface dialog, int which)
	                    {
	                    	String appname=listItem.get(argument).get("ItemTitle").toString();
	                    	if (which==0){
	                    		//��ֹ
	                    		showInstalledAppDetails(null, getPackageNameFromAppName(appname));
	                    	}else{
	                    		//��������б�
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
		
		//��¼Activity����
  		Data app = (Data) getApplication();  
          app.activities.add(this); 
          
	}
	
	Handler handler = new Handler();
	/***
	 * ��ʱ����ÿ1��ˢ������
	 */
    Runnable runnable = new Runnable(){
        @Override
        public void run() {
            try {
                handler.postDelayed(this, 1000);
                //System.out.println("��ʱ��");
                init();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("��ʱ������...");
            }
        }
    };
	
	
	
	/***
	 * ��Ӧ������ð���
	 */
	private String getPackageNameFromAppName(String appName){
		List<PackageInfo> packages = getPackageManager()
				.getInstalledPackages(0);

		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				//ÿһ�������õ���Ӧ�ó���
				if (packageInfo.applicationInfo.loadLabel(getPackageManager()).toString().equals(appName)){
					return packageInfo.packageName;
				}
			}
		}
		return "Nothing";
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
		Data app = (Data)getApplication();
		String[] str= app.getAllStrings();
		listItem.clear();
		if (str[0].equals("nothing")){
			//�Ѿ�û����
			finish();
			
		}
		
		for (int i=0;i<21;i++){
			if  (!str[i].equals("nothing"))
			{
				String info=str[i];
				int a=info.indexOf("||||");
		  		int b=info.indexOf("||",a+4);
		  		int c=info.indexOf("��");
		  		int d=info.indexOf("��");
		  		String appname=info.substring(0,a);
		  		String number=info.substring(a+4,b);
		  		String type=info.substring(b+2,c);
		  		String packagename=info.substring(c+1,d);

				HashMap<String, Object> map = new HashMap<String, Object>();
				if (type.equals("dn"))
					map.put("ItemText","���������ܹ�"+DealData(Integer.valueOf(number)));
				else
					map.put("ItemText","�����ϴ��ܹ�"+DealData(Integer.valueOf(number)));
				map.put("ItemTitle", appname);
				
				
				//���Ӧ��ͼ��
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
	
	private static final String SCHEME = "package";
	/**
	 * ����ϵͳInstalledAppDetails���������Extra����(����Android 2.1��֮ǰ�汾)
	 */
	private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
	/**
	 * ����ϵͳInstalledAppDetails���������Extra����(����Android 2.2)
	 */
	private static final String APP_PKG_NAME_22 = "pkg";
	/**
	 * InstalledAppDetails���ڰ���
	 */
	private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
	/**
	 * InstalledAppDetails����
	 */
	private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";
	/**
	 * ����ϵͳInstalledAppDetails������ʾ�Ѱ�װӦ�ó������ϸ��Ϣ�� ����Android 2.3��Api Level
	 * 9�����ϣ�ʹ��SDK�ṩ�Ľӿڣ� 2.3���£�ʹ�÷ǹ����Ľӿڣ��鿴InstalledAppDetailsԴ�룩��
	 * 
	 * @param context
	 * 
	 * @param packageName
	 *            Ӧ�ó���İ���
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
