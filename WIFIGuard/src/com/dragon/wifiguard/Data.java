package com.dragon.wifiguard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.util.Log;
/**
 * Application Data����������
 * @author �����
 * @version 1.4.3.5
 */
public class Data extends Application{

    ArrayList<Activity> activities;  
    private static Data instance;  
  
    private String ConnectTo;
	private boolean IsEnd=true;
	private String Row[]=new String[21];
	/**��ʾ����*/
	private String ShowString;
	/**
	 * ȡ��ģʽ����
	 * */
	public String getConnectString(){
		  return ConnectTo;
	}
	/**
	 * ����ģʽ����
	 * */
	public void setConnectString(String str){
		ConnectTo=str;
	}
	/**
	 * ȡ����ʾ����
	 * */
	public String getShowString(){
		  return ShowString;
	}
	/**
	 * ������ʾ����
	 * */
	public void setShowString(String str){
		ShowString=str;
	}
	/**
	 * ��Ӷ���
	 * @param str ��ӵ��ı�
	 */
	public void AddRow(String str)
	{
		Log.e("���ݴ���","���ڴ���"+str);
		for (int i=0;i<20;i++)
		{
			if (!Row[i].equals("nothing")){
				if ((getAppName(Row[i]).equals(getAppName(str)))&&
						(getType(Row[i]).equals(getType(str)))){
					//�������ͬ��,��ô����֮
					Row[i]=cooperateInfo(Row[i],str);
					return;
				}
			}
			if (Row[i].equals("nothing"))
			{
				if (i==0){//����ǿն���
					ShowString=str;
				}
				
				Row[i]=str;
				Log.v(""+i,"����"+str);
				return;
			}
			
		}
	}
	/**
	 * ������ж�������
	 * @return ���飨��С20��
	 */
	public String[] getAllStrings(){
		return Row;
	}
	/**
	 * �����ַ�����ð���
	 * @param info �������ַ���
	 * @return ����
	 */
	private String getAppName(String info){
		//���������
		int a=info.indexOf("||||");
  		int b=info.indexOf("||",a+4);
  		int c=info.indexOf("��");
  		int d=info.indexOf("��");
  		String packagename=info.substring(c+1,d);
  		return packagename;
	}
	/**
	 * �����ַ�������ϴ���������
	 * @param info �������ַ���
	 * @return ���"dn"��"up"��
	 */
	private String getType(String info){
		//��������
		int a=info.indexOf("||||");
  		int b=info.indexOf("||",a+4);
  		int c=info.indexOf("��");
  		int d=info.indexOf("��");
  		String type=info.substring(b+2,c);
  		return type;
	}
	/**
	 * �ϲ�������Ϣ
	 * @param info1 ��Ϣ1
	 * @param info2 ��Ϣ2
	 * @return �ϲ�����Ϣ
	 */
	private String cooperateInfo(String info1,String info2){
		//�ϲ�������Ϣ
		int a=info1.indexOf("||||");
  		int b=info1.indexOf("||",a+4);
  		int c=info1.indexOf("��");
  		int d=info1.indexOf("��");
  		String number1=info1.substring(a+4,b);
  		String appname=info1.substring(0,a);
  		String type=info1.substring(b+2,c);
  		String packagename=info1.substring(c+1,d);
  		a=info2.indexOf("||||");
  		b=info2.indexOf("||",a+4);
  		c=info2.indexOf("��");
  		d=info2.indexOf("��");
  		String number2=info2.substring(a+4,b);
  		Log.v("���ݴ���","����"+number1+" "+ number2 );
  		String number=Integer.valueOf(number1)+Integer.valueOf(number2)+"";
  		return appname+"||||"+number+"||"+type+
  				"��"+packagename+"��";
	}
	/**
	 * ɾ����һ��Ŀ
	 */
	public void DeleteRow()
	{
		for(int i=0;i<20;i++){
			if (Row[i+1].equals("nothing")){
				Log.v("����ɾ��"+i, Row[i]);
				Row[i]="nothing";
				ShowString=Row[0];
				return;
			}else{
			Row[i]=Row[i+1];
			Log.v("���и���"+(i+1),""+i);
			}
		}
		ShowString=Row[0];
	}
	/**
	 * Debug���ԣ���ӡ���ж�����Ϣ
	 */
	public void ShowAll(){
		for(int i=0;i<20;i++)
		{
			if (Row[i].equals("nothing"))
				return;
			Log.v("����"+i, Row[i]);
		}
		Log.v("����", ShowString);
	}
	@Override
	public void onCreate(){
		super.onCreate();
		for(int i=0;i<20;i++)
			Row[i]="nothing";
        activities = new ArrayList<Activity>();  
        getInstance();  
        super.onCreate();  
	}
	/**
	 * ����������ѯ���Ƿ����
	 * @param a trueΪ������falseΪ��û����
	 */
	public void SetIsEnd(boolean a){
		IsEnd=a;
	}
	/**
	 * ѯ��������ѯ���Ƿ����
	 * @return trueΪ������falseΪ��û����
	 */
	public boolean GetIsEnd(){
		return IsEnd;
	}

	HashMap<String, String> map=new HashMap<String, String>(); 
	/**
	 * ��ȡSQLite�к����б���������ݲ����浽Hashmap��
	 */
	public void readAllIgnoreListAndReset(){
		SQL db=new SQL();
		AActivity.SQLRunning=true;
		db.Port="Ignore";
		db.openDataBase();
		List<HashMap<String, Object>> mList = new ArrayList<HashMap<String, Object>>();
		 map.clear();
		
		mList = db.getData(db.Port);
		for (int i = 0; i < mList.size(); i++) {
			map.put(mList.get(i).get("name").toString(), mList.get(i).get("pswd").toString());
		}
		AActivity.SQLRunning=false;
	}
  
	/**
	 * ���ĳ��Ӧ���Ƿ��ں���������
	 * @param packageName Ӧ�ð���
	 * @return trueΪ���ԣ�falseΪ������
	 */
	public boolean getIgnoredOrNot(String packageName){
		String result=map.get(packageName).toString();
		if (map.containsKey(packageName)){
			//Log.e("�����������",packageName + "_____"+result);
			if (result.equals("1")){
				return true;
			}else return false;
		}else{
			Log.e("�����������","û�д���Ŀ");
			return true;
		}
	}
    public static Data getInstance() {  
        if (null == instance) {  
            instance = new Data();  
        }  
        return instance;  
  
    }  
  
    public void exitApplication() {  
  
        List<Activity> lists = instance.activities;  
        for (Activity a : lists) {  
            a.finish();  
        }  
    }  
}