package com.dragon.wifiguard;
import android.graphics.Color;
import android.telephony.TelephonyManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.EditText;

/**
 * CActivity:连接信息界面
 * @author 林炜哲
 * @version 1.5.0.1
 */
public class CActivity extends Activity{
	private ExpandableListView expandableListView;
	/**
	 * 列表标题数组
	 * */
	private String[][] generals = new String[][] {
            { "BSSID属性", "客户端的连通性", "SSID 是否被隐藏", "IP 地址", "连接速度", "Mac 地址" },
            { "总计流量", "总上传", "总下载", "本次总流量", "本次上传", "本次下载" },
            { "1", "2", "3", "4", "5","6" }
    };
	/**
	 * 列表LOGO数组
	 * */
	public int[][] generallogos = new int[][] {
            { R.drawable.dot, R.drawable.dot,
                    R.drawable.dot, R.drawable.dot,
                    R.drawable.dot, R.drawable.dot },
            { R.drawable.dot, R.drawable.dot,
                    R.drawable.dot, R.drawable.dot,
                    R.drawable.dot, R.drawable.dot },
            { R.drawable.dot, R.drawable.dot, R.drawable.dot,
                    R.drawable.dot, R.drawable.dot,R.drawable.dot } };
    
	private static final int REQUEST_ENABLE_BT = 3;  
    private WifiManager mWifi;  
    private String btMac;  
    private String WifiMac;  
	private  ExpandableListView  expandableListView_one;
	private EditText Updata;
	private EditText connectText;
	private EditText Downdata;
	boolean isExit;  //连续点击退出

	
	
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	// 点击HOME键时程序进入后台运行
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
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();  
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//记录Activity启动
  		Data app = (Data) getApplication();  
          app.activities.add(this); 
          
		setContentView(R.layout.connectinfo);
		Updata=(EditText)findViewById(R.id.updata);
		connectText=(EditText)findViewById(R.id.connectText);
		Downdata=(EditText)findViewById(R.id.downdata);
		
		
		
		final ExpandableListAdapter adapter = new BaseExpandableListAdapter() {
            //设置组视图的图片
            int[] logos = new int[] { R.drawable.connectlogo, R.drawable.connectnote,R.drawable.ic_launchersmall};
            //设置组视图的显示文字
            private String[] generalsTypes = new String[] { "连接信息", "连接统计", "应用统计" };
            //子视图显示文字
            //generals[]
            //子视图图片
            
            //自己定义一个获得文字信息的方法
            TextView getTextView() {
                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT, 64);
                TextView textView = new TextView(
                        CActivity.this);
                textView.setLayoutParams(lp);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                textView.setPadding(36, 0, 0, 0);
                textView.setTextSize(16);
                textView.setTextColor(Color.BLACK);
                return textView;
            }

            
            //重写ExpandableListAdapter中的各个方法
            @Override
            public int getGroupCount() {
                // TODO Auto-generated method stub
                return generalsTypes.length;
            }

            @Override
            public Object getGroup(int groupPosition) {
                // TODO Auto-generated method stub
                return generalsTypes[groupPosition];
            }

            @Override
            public long getGroupId(int groupPosition) {
                // TODO Auto-generated method stub
                return groupPosition;
            }

            @Override
            public int getChildrenCount(int groupPosition) {
                // TODO Auto-generated method stub
                return generals[groupPosition].length;
            }

            @Override
            public Object getChild(int groupPosition, int childPosition) {
                // TODO Auto-generated method stub
                return generals[groupPosition][childPosition];
            }

            @Override
            public long getChildId(int groupPosition, int childPosition) {
                // TODO Auto-generated method stub
                return childPosition;
            }

            @Override
            public boolean hasStableIds() {
                // TODO Auto-generated method stub
                return true;
            }

            @Override
            public View getGroupView(int groupPosition, boolean isExpanded,
                    View convertView, ViewGroup parent) {
                // TODO Auto-generated method stub
                LinearLayout ll = new LinearLayout(
                        CActivity.this);
                ll.setOrientation(0);
                ImageView logo = new ImageView(CActivity.this);
                logo.setImageResource(logos[groupPosition]);
                logo.setPadding(70, 0, 0, 0);
                ll.addView(logo);
                TextView textView = getTextView();
                textView.setTextColor(Color.BLACK);
                textView.setText(getGroup(groupPosition).toString());
                ll.addView(textView);

                return ll;
            }

            @Override
            public View getChildView(int groupPosition, int childPosition,
                    boolean isLastChild, View convertView, ViewGroup parent) {
                // TODO Auto-generated method stub
                LinearLayout ll = new LinearLayout(
                        CActivity.this);
                ll.setOrientation(0);
                ImageView generallogo = new ImageView(
                		CActivity.this);
                generallogo
                        .setImageResource(generallogos[groupPosition][childPosition]);
                ll.addView(generallogo);
                TextView textView = getTextView();
                textView.setText(getChild(groupPosition, childPosition)
                        .toString());
                ll.addView(textView);
                return ll;
            }

            @Override
            public boolean isChildSelectable(int groupPosition,
                    int childPosition) {
                // TODO Auto-generated method stub
                return true;
            }

        };
     
        
        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        expandableListView.setAdapter(adapter);
        
        
        //设置item点击的监听器
        expandableListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                    int groupPosition, int childPosition, long id) {

                Toast.makeText(
                        CActivity.this,
                        ""+adapter.getChild(groupPosition, childPosition),
                        Toast.LENGTH_SHORT).show();

                return false;
            }
        });
	}

	

	private void toast(String str) {
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onResume() {
		super.onResume();
		ReFreshUI();
	}
	private void ReFreshUI(){
		ListInfo list = new ListInfo();
		Data app = (Data) getApplication();
		String str = app.getConnectString();
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		ConnectivityManager connectMgr = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo info = connectMgr.getActiveNetworkInfo();
		
		if (str.equals("NoConnection")) {
			connectText.setText("无连接");
		} else {
			int mode = info.getSubtype();
			String modestr = getModeName(mode);

			// /统计和下载排序
			int x = 0;
			int y = 0;
			int tx = 0, ty = 0;
			for (int i = 0; i < dataInfo.Num; i++) {
				// 统计流量总数
				x += Integer.valueOf(dataInfo.InitDownBytes[i]);
				y += Integer.valueOf(dataInfo.InitUpBytes[i]);
				// 排序耗流量应用
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("ItemImage", ListInfo.appIcon[i]);
				map.put("ItemTitle", ListInfo.appName[i]);
				map.put("down", Integer.valueOf(dataInfo.InitDownBytes[i]));
				map.put("up", Integer.valueOf(dataInfo.InitUpBytes[i]));
				listItem.add(map);
				tx += ListInfo.InitDownBytes[i];
				ty += ListInfo.InitUpBytes[i];
			}
			// 下载排序
			Comparator comp = new Mycomparator();
			Collections.sort(listItem, comp);
			for (int i = 0; i < listItem.size() && i < 3; i++) {
				generals[2][i] = "下载第"
						+ (i + 1)
						+ "："
						+ listItem.get(i).get("ItemTitle").toString()
						+ "\n已下载："
						+ DealData(Integer.valueOf(listItem.get(i).get("down")
								.toString()))
						+ "\n已上传："
						+ DealData(Integer.valueOf(listItem.get(i).get("up")
								.toString()));
			}
			comp = new Mycomparator2();
			Collections.sort(listItem, comp);
			for (int i = 0; i < listItem.size() && i < 3; i++) {
				generals[2][i + 3] = "上传第"
						+ (i + 1)
						+ "："
						+ listItem.get(i).get("ItemTitle").toString()
						+ "\n已上传："
						+ DealData(Integer.valueOf(listItem.get(i).get("up")
								.toString()))
						+ "\n已下载："
						+ DealData(Integer.valueOf(listItem.get(i).get("down")
								.toString()));
			}
			Downdata.setText(DealData(x));
			Updata.setText(DealData(y));
			// "总计流量", "总上传", "总下载", "本次总流量", "本次上传", "本次下载"
			generals[1][0] = "总计流量：\n" + DealData((tx + ty));
			generals[1][1] = "总上传：\n" + DealData(ty);
			generals[1][2] = "总下载：\n" + DealData(tx);
			generals[1][3] = "本次总流量：\n" + DealData((x + y));
			generals[1][4] = "本次上传：\n" + DealData(y);
			generals[1][5] = "本次下载：\n" + DealData(x);
		}
		if (str.equals("NoConnection")) {
			generals[0][0] = "网络已断开\n以下为上一次连接的快照";
		} else {
			if (str.equals("mobile")) {
				int mode = info.getSubtype();
				String modestr = getModeName(mode);
				connectText.setText("已连接到移动数据");
				generals[0][0] = "数据连接\n已连接";
				generals[0][1] = "运营商\n" + getOperatorName();
				generals[0][2] = "IP 地址\n" + getLocalIpAddress();
				generals[0][3] = "连接速度\n"
						+ isConnectionFast(info.getType(), info.getSubtype());
				generals[0][4] = "类型\n" + modestr;
				generals[0][5] = "";
			} else {
				connectText.setText("已连接到：" + app.getConnectString());

				mWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				if (!mWifi.isWifiEnabled()) {
					mWifi.setWifiEnabled(true);
				}
				WifiInfo wifiInfo = mWifi.getConnectionInfo();

				// 查看已经连接上的WIFI信息，在Android的SDK中为我们提供了一个叫做WifiInfo的对象，这个对象可以通过WifiManager.getConnectionInfo()来获取。WifiInfo中包含了当前连接中的相关信息。
				// getBSSID() 获取BSSID属性
				// getDetailedStateOf() 获取客户端的连通性
				// getHiddenSSID() 获取SSID 是否被隐藏
				// getIpAddress() 获取IP 地址
				// getLinkSpeed() 获取连接的速度
				// getMacAddress() 获取Mac 地址
				// getRssi() 获取802.11n 网络的信号
				// getSSID() 获取SSID
				// getSupplicanState() 获取具体客户端状态的信息
				generals[0][0] = "所连接的WIFI设备的MAC地址\n" + wifiInfo.getBSSID();
				generals[0][1] = "SSID 是否被隐藏\n" + wifiInfo.getHiddenSSID();
				generals[0][2] = "IP 地址\n" + intToIp(wifiInfo.getIpAddress());
				generals[0][3] = "连接速度\n" + wifiInfo.getLinkSpeed();
				generals[0][4] = "MAC 地址\n" + wifiInfo.getMacAddress();
				generals[0][5] = "SSID\n" + wifiInfo.getSSID();

			}
		}
		final ExpandableListAdapter adapter = new BaseExpandableListAdapter() {
			// 设置组视图的图片
			int[] logos = new int[] { R.drawable.connectlogo,
					R.drawable.connectnote, R.drawable.ic_launchersmall };
			// 设置组视图的显示文字
			private String[] generalsTypes = new String[] { "连接信息", "连接统计",
					"应用统计" };

			// 子视图显示文字
			// generals[]
			// 子视图图片

			// 自己定义一个获得文字信息的方法
			TextView getTextView() {
				AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
						ViewGroup.LayoutParams.FILL_PARENT,
						ViewGroup.LayoutParams.FILL_PARENT);
				TextView textView = new TextView(CActivity.this);
				textView.setLayoutParams(lp);
				textView.setGravity(Gravity.CENTER_VERTICAL);
				textView.setPadding(36, 0, 0, 0);
				textView.setTextSize(16);
				textView.setTextColor(Color.BLACK);
				return textView;
			}

			// 重写ExpandableListAdapter中的各个方法
			@Override
			public int getGroupCount() {
				// TODO Auto-generated method stub
				return generalsTypes.length;
			}

			@Override
			public Object getGroup(int groupPosition) {
				// TODO Auto-generated method stub
				return generalsTypes[groupPosition];
			}

			@Override
			public long getGroupId(int groupPosition) {
				// TODO Auto-generated method stub
				return groupPosition;
			}

			@Override
			public int getChildrenCount(int groupPosition) {
				// TODO Auto-generated method stub
				return generals[groupPosition].length;
			}

			@Override
			public Object getChild(int groupPosition, int childPosition) {
				// TODO Auto-generated method stub
				return generals[groupPosition][childPosition];
			}

			@Override
			public long getChildId(int groupPosition, int childPosition) {
				// TODO Auto-generated method stub
				return childPosition;
			}

			@Override
			public boolean hasStableIds() {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public View getGroupView(int groupPosition, boolean isExpanded,
					View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				LinearLayout ll = new LinearLayout(CActivity.this);
				ll.setOrientation(0);
				ImageView logo = new ImageView(CActivity.this);
				logo.setImageResource(logos[groupPosition]);
				logo.setPadding(70, 0, 0, 0);
				ll.addView(logo);
				TextView textView = getTextView();
				textView.setTextColor(Color.BLACK);
				textView.setText(getGroup(groupPosition).toString());
				ll.addView(textView);

				return ll;
			}

			@Override
			public View getChildView(int groupPosition, int childPosition,
					boolean isLastChild, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				LinearLayout ll = new LinearLayout(CActivity.this);
				ll.setOrientation(0);
				ImageView generallogo = new ImageView(CActivity.this);
				generallogo
						.setImageResource(generallogos[groupPosition][childPosition]);
				ll.addView(generallogo);
				TextView textView = getTextView();
				textView.setText(getChild(groupPosition, childPosition)
						.toString());
				ll.addView(textView);
				return ll;
			}

			@Override
			public boolean isChildSelectable(int groupPosition,
					int childPosition) {
				// TODO Auto-generated method stub
				return true;
			}

		};
		
		
        expandableListView.setAdapter(adapter);
	}
	
	
	/**
	 * 获得移动数据网络连接类型
	 * */
	private String getModeName(int mode) {
		/*
		 * NETWORK_TYPE_CDMA 网络类型为CDMA
		 * NETWORK_TYPE_EDGE 网络类型为EDGE
		 * NETWORK_TYPE_EVDO_0 网络类型为EVDO0 
		 * NETWORK_TYPE_EVDO_A 网络类型为EVDOA
		 * NETWORK_TYPE_GPRS 网络类型为GPRS 
		 * NETWORK_TYPE_HSDPA 网络类型为HSDPA
		 * NETWORK_TYPE_HSPA 网络类型为HSPA 
		 * NETWORK_TYPE_HSUPA 网络类型为HSUPA
		 * NETWORK_TYPE_UMTS 网络类型为UMTS
		 * 
		 * 联通的3G为UMTS或HSDPA，移动和联通的2G为GPRS或EDGE，
		 * 电信的2G为CDMA，电信 的3G为EVDO
		 */
		switch(mode){
		case TelephonyManager.NETWORK_TYPE_LTE:
			return getOperatorName() +"4G"; // 1000-2000 kbps
        case TelephonyManager.NETWORK_TYPE_1xRTT:
            return "1xRTT"; // ~ 50-100 kbps
        case TelephonyManager.NETWORK_TYPE_CDMA:
            return "电信2G"; // ~ 14-64 kbps
        case TelephonyManager.NETWORK_TYPE_EDGE:
        	if (getOperatorName().equals("中国移动")){
            return "移动2G"; // ~ 50-100 kbps
        	}else{
        		return "联通2G";
        	}
        case TelephonyManager.NETWORK_TYPE_EVDO_0:
            return "电信3G"; // ~ 400-1000 kbps
        case TelephonyManager.NETWORK_TYPE_EVDO_A:
            return "电信3G"; // ~ 600-1400 kbps
        case TelephonyManager.NETWORK_TYPE_GPRS:
        	if (getOperatorName().equals("中国移动")){
                return "移动2G"; 
            	}else{
            		return "联通2G";
            	} // ~ 100 kbps
        case TelephonyManager.NETWORK_TYPE_HSDPA:
            return "联通3G"; // ~ 2-14 Mbps
        case TelephonyManager.NETWORK_TYPE_HSPA:
            return "HSPA"; // ~ 700-1700 kbps
        case TelephonyManager.NETWORK_TYPE_HSUPA:
            return "HSUPA"; // ~ 1-23 Mbps
        case TelephonyManager.NETWORK_TYPE_UMTS:
            return "联通3G"; // ~ 400-7000 kbps
		}
		return "识别失败";
	}

	/**
	 * 判断网络连接速度
	 * */
	public static boolean isConnectionFast(int type, int subType) {
		if (type == ConnectivityManager.TYPE_WIFI) {
			System.out.println("CONNECTED VIA WIFI");
			return true;
		} else if (type == ConnectivityManager.TYPE_MOBILE) {
			switch (subType) {
			case TelephonyManager.NETWORK_TYPE_1xRTT:
				return false; // ~ 50-100 kbps
			case TelephonyManager.NETWORK_TYPE_CDMA:
				return false; // ~ 14-64 kbps
			case TelephonyManager.NETWORK_TYPE_EDGE:
				return false; // ~ 50-100 kbps
			case TelephonyManager.NETWORK_TYPE_EVDO_0:
				return true; // ~ 400-1000 kbps
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
				return true; // ~ 600-1400 kbps
			case TelephonyManager.NETWORK_TYPE_GPRS:
				return false; // ~ 100 kbps
			case TelephonyManager.NETWORK_TYPE_HSDPA:
				return true; // ~ 2-14 Mbps
			case TelephonyManager.NETWORK_TYPE_HSPA:
				return true; // ~ 700-1700 kbps
			case TelephonyManager.NETWORK_TYPE_HSUPA:
				return true; // ~ 1-23 Mbps
			case TelephonyManager.NETWORK_TYPE_UMTS:
				return true; // ~ 400-7000 kbps
			case TelephonyManager.NETWORK_TYPE_UNKNOWN:
				return false;
			default:
				return false;
			}
		} else {
			return false;
		}
	}
	/**
	 * 获得运营商名字
	 * */
	private String getOperatorName() {
		TelephonyManager telephonyManager= (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);;
		String operator = telephonyManager.getSimOperator();
		if (operator != null) {
			if (operator.equals("46000") || operator.equals("46002")) {
				return "中国移动";
			} else if (operator.equals("46001")) {
				return "中国联通";
			} else if (operator.equals("46003")) {
				return "中国电信";
			}
		}
		return "获取失败";
	}

	
	public String getLocalIpAddress()
    {
        try
        {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
            {
               NetworkInterface intf = en.nextElement();
               for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
               {
                   InetAddress inetAddress = enumIpAddr.nextElement();
                   if (!inetAddress.isLoopbackAddress())
                   {
                       return inetAddress.getHostAddress().toString();
                   }
               }
           }
        }
        catch (SocketException ex)
        {
            Log.e("WifiPreference IpAddress", ex.toString());
        }
        return null;
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
	@Override  
    protected void onDestroy() {  
        super.onDestroy();  

  		//记录Activity退出
  		Data app = (Data) getApplication();  
          app.activities.remove(this); 
    }  
	private String intToIp(int ip)
    {
       return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "."
              + ((ip >> 24) & 0xFF);
    }
}
