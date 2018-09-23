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
 * CActivity:������Ϣ����
 * @author �����
 * @version 1.5.0.1
 */
public class CActivity extends Activity{
	private ExpandableListView expandableListView;
	/**
	 * �б��������
	 * */
	private String[][] generals = new String[][] {
            { "BSSID����", "�ͻ��˵���ͨ��", "SSID �Ƿ�����", "IP ��ַ", "�����ٶ�", "Mac ��ַ" },
            { "�ܼ�����", "���ϴ�", "������", "����������", "�����ϴ�", "��������" },
            { "1", "2", "3", "4", "5","6" }
    };
	/**
	 * �б�LOGO����
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//��¼Activity����
  		Data app = (Data) getApplication();  
          app.activities.add(this); 
          
		setContentView(R.layout.connectinfo);
		Updata=(EditText)findViewById(R.id.updata);
		connectText=(EditText)findViewById(R.id.connectText);
		Downdata=(EditText)findViewById(R.id.downdata);
		
		
		
		final ExpandableListAdapter adapter = new BaseExpandableListAdapter() {
            //��������ͼ��ͼƬ
            int[] logos = new int[] { R.drawable.connectlogo, R.drawable.connectnote,R.drawable.ic_launchersmall};
            //��������ͼ����ʾ����
            private String[] generalsTypes = new String[] { "������Ϣ", "����ͳ��", "Ӧ��ͳ��" };
            //����ͼ��ʾ����
            //generals[]
            //����ͼͼƬ
            
            //�Լ�����һ�����������Ϣ�ķ���
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

            
            //��дExpandableListAdapter�еĸ�������
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
        
        
        //����item����ļ�����
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
			connectText.setText("������");
		} else {
			int mode = info.getSubtype();
			String modestr = getModeName(mode);

			// /ͳ�ƺ���������
			int x = 0;
			int y = 0;
			int tx = 0, ty = 0;
			for (int i = 0; i < dataInfo.Num; i++) {
				// ͳ����������
				x += Integer.valueOf(dataInfo.InitDownBytes[i]);
				y += Integer.valueOf(dataInfo.InitUpBytes[i]);
				// ���������Ӧ��
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("ItemImage", ListInfo.appIcon[i]);
				map.put("ItemTitle", ListInfo.appName[i]);
				map.put("down", Integer.valueOf(dataInfo.InitDownBytes[i]));
				map.put("up", Integer.valueOf(dataInfo.InitUpBytes[i]));
				listItem.add(map);
				tx += ListInfo.InitDownBytes[i];
				ty += ListInfo.InitUpBytes[i];
			}
			// ��������
			Comparator comp = new Mycomparator();
			Collections.sort(listItem, comp);
			for (int i = 0; i < listItem.size() && i < 3; i++) {
				generals[2][i] = "���ص�"
						+ (i + 1)
						+ "��"
						+ listItem.get(i).get("ItemTitle").toString()
						+ "\n�����أ�"
						+ DealData(Integer.valueOf(listItem.get(i).get("down")
								.toString()))
						+ "\n���ϴ���"
						+ DealData(Integer.valueOf(listItem.get(i).get("up")
								.toString()));
			}
			comp = new Mycomparator2();
			Collections.sort(listItem, comp);
			for (int i = 0; i < listItem.size() && i < 3; i++) {
				generals[2][i + 3] = "�ϴ���"
						+ (i + 1)
						+ "��"
						+ listItem.get(i).get("ItemTitle").toString()
						+ "\n���ϴ���"
						+ DealData(Integer.valueOf(listItem.get(i).get("up")
								.toString()))
						+ "\n�����أ�"
						+ DealData(Integer.valueOf(listItem.get(i).get("down")
								.toString()));
			}
			Downdata.setText(DealData(x));
			Updata.setText(DealData(y));
			// "�ܼ�����", "���ϴ�", "������", "����������", "�����ϴ�", "��������"
			generals[1][0] = "�ܼ�������\n" + DealData((tx + ty));
			generals[1][1] = "���ϴ���\n" + DealData(ty);
			generals[1][2] = "�����أ�\n" + DealData(tx);
			generals[1][3] = "������������\n" + DealData((x + y));
			generals[1][4] = "�����ϴ���\n" + DealData(y);
			generals[1][5] = "�������أ�\n" + DealData(x);
		}
		if (str.equals("NoConnection")) {
			generals[0][0] = "�����ѶϿ�\n����Ϊ��һ�����ӵĿ���";
		} else {
			if (str.equals("mobile")) {
				int mode = info.getSubtype();
				String modestr = getModeName(mode);
				connectText.setText("�����ӵ��ƶ�����");
				generals[0][0] = "��������\n������";
				generals[0][1] = "��Ӫ��\n" + getOperatorName();
				generals[0][2] = "IP ��ַ\n" + getLocalIpAddress();
				generals[0][3] = "�����ٶ�\n"
						+ isConnectionFast(info.getType(), info.getSubtype());
				generals[0][4] = "����\n" + modestr;
				generals[0][5] = "";
			} else {
				connectText.setText("�����ӵ���" + app.getConnectString());

				mWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				if (!mWifi.isWifiEnabled()) {
					mWifi.setWifiEnabled(true);
				}
				WifiInfo wifiInfo = mWifi.getConnectionInfo();

				// �鿴�Ѿ������ϵ�WIFI��Ϣ����Android��SDK��Ϊ�����ṩ��һ������WifiInfo�Ķ�������������ͨ��WifiManager.getConnectionInfo()����ȡ��WifiInfo�а����˵�ǰ�����е������Ϣ��
				// getBSSID() ��ȡBSSID����
				// getDetailedStateOf() ��ȡ�ͻ��˵���ͨ��
				// getHiddenSSID() ��ȡSSID �Ƿ�����
				// getIpAddress() ��ȡIP ��ַ
				// getLinkSpeed() ��ȡ���ӵ��ٶ�
				// getMacAddress() ��ȡMac ��ַ
				// getRssi() ��ȡ802.11n ������ź�
				// getSSID() ��ȡSSID
				// getSupplicanState() ��ȡ����ͻ���״̬����Ϣ
				generals[0][0] = "�����ӵ�WIFI�豸��MAC��ַ\n" + wifiInfo.getBSSID();
				generals[0][1] = "SSID �Ƿ�����\n" + wifiInfo.getHiddenSSID();
				generals[0][2] = "IP ��ַ\n" + intToIp(wifiInfo.getIpAddress());
				generals[0][3] = "�����ٶ�\n" + wifiInfo.getLinkSpeed();
				generals[0][4] = "MAC ��ַ\n" + wifiInfo.getMacAddress();
				generals[0][5] = "SSID\n" + wifiInfo.getSSID();

			}
		}
		final ExpandableListAdapter adapter = new BaseExpandableListAdapter() {
			// ��������ͼ��ͼƬ
			int[] logos = new int[] { R.drawable.connectlogo,
					R.drawable.connectnote, R.drawable.ic_launchersmall };
			// ��������ͼ����ʾ����
			private String[] generalsTypes = new String[] { "������Ϣ", "����ͳ��",
					"Ӧ��ͳ��" };

			// ����ͼ��ʾ����
			// generals[]
			// ����ͼͼƬ

			// �Լ�����һ�����������Ϣ�ķ���
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

			// ��дExpandableListAdapter�еĸ�������
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
	 * ����ƶ�����������������
	 * */
	private String getModeName(int mode) {
		/*
		 * NETWORK_TYPE_CDMA ��������ΪCDMA
		 * NETWORK_TYPE_EDGE ��������ΪEDGE
		 * NETWORK_TYPE_EVDO_0 ��������ΪEVDO0 
		 * NETWORK_TYPE_EVDO_A ��������ΪEVDOA
		 * NETWORK_TYPE_GPRS ��������ΪGPRS 
		 * NETWORK_TYPE_HSDPA ��������ΪHSDPA
		 * NETWORK_TYPE_HSPA ��������ΪHSPA 
		 * NETWORK_TYPE_HSUPA ��������ΪHSUPA
		 * NETWORK_TYPE_UMTS ��������ΪUMTS
		 * 
		 * ��ͨ��3GΪUMTS��HSDPA���ƶ�����ͨ��2GΪGPRS��EDGE��
		 * ���ŵ�2GΪCDMA������ ��3GΪEVDO
		 */
		switch(mode){
		case TelephonyManager.NETWORK_TYPE_LTE:
			return getOperatorName() +"4G"; // 1000-2000 kbps
        case TelephonyManager.NETWORK_TYPE_1xRTT:
            return "1xRTT"; // ~ 50-100 kbps
        case TelephonyManager.NETWORK_TYPE_CDMA:
            return "����2G"; // ~ 14-64 kbps
        case TelephonyManager.NETWORK_TYPE_EDGE:
        	if (getOperatorName().equals("�й��ƶ�")){
            return "�ƶ�2G"; // ~ 50-100 kbps
        	}else{
        		return "��ͨ2G";
        	}
        case TelephonyManager.NETWORK_TYPE_EVDO_0:
            return "����3G"; // ~ 400-1000 kbps
        case TelephonyManager.NETWORK_TYPE_EVDO_A:
            return "����3G"; // ~ 600-1400 kbps
        case TelephonyManager.NETWORK_TYPE_GPRS:
        	if (getOperatorName().equals("�й��ƶ�")){
                return "�ƶ�2G"; 
            	}else{
            		return "��ͨ2G";
            	} // ~ 100 kbps
        case TelephonyManager.NETWORK_TYPE_HSDPA:
            return "��ͨ3G"; // ~ 2-14 Mbps
        case TelephonyManager.NETWORK_TYPE_HSPA:
            return "HSPA"; // ~ 700-1700 kbps
        case TelephonyManager.NETWORK_TYPE_HSUPA:
            return "HSUPA"; // ~ 1-23 Mbps
        case TelephonyManager.NETWORK_TYPE_UMTS:
            return "��ͨ3G"; // ~ 400-7000 kbps
		}
		return "ʶ��ʧ��";
	}

	/**
	 * �ж����������ٶ�
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
	 * �����Ӫ������
	 * */
	private String getOperatorName() {
		TelephonyManager telephonyManager= (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);;
		String operator = telephonyManager.getSimOperator();
		if (operator != null) {
			if (operator.equals("46000") || operator.equals("46002")) {
				return "�й��ƶ�";
			} else if (operator.equals("46001")) {
				return "�й���ͨ";
			} else if (operator.equals("46003")) {
				return "�й�����";
			}
		}
		return "��ȡʧ��";
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
	@Override  
    protected void onDestroy() {  
        super.onDestroy();  

  		//��¼Activity�˳�
  		Data app = (Data) getApplication();  
          app.activities.remove(this); 
    }  
	private String intToIp(int ip)
    {
       return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "."
              + ((ip >> 24) & 0xFF);
    }
}
