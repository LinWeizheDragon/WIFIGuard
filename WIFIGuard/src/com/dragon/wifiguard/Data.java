package com.dragon.wifiguard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.util.Log;
/**
 * Application Data共用数据类
 * @author 林炜哲
 * @version 1.4.3.5
 */
public class Data extends Application{

    ArrayList<Activity> activities;  
    private static Data instance;  
  
    private String ConnectTo;
	private boolean IsEnd=true;
	private String Row[]=new String[21];
	/**提示文字*/
	private String ShowString;
	/**
	 * 取出模式文字
	 * */
	public String getConnectString(){
		  return ConnectTo;
	}
	/**
	 * 设置模式文字
	 * */
	public void setConnectString(String str){
		ConnectTo=str;
	}
	/**
	 * 取出提示文字
	 * */
	public String getShowString(){
		  return ShowString;
	}
	/**
	 * 设置提示文字
	 * */
	public void setShowString(String str){
		ShowString=str;
	}
	/**
	 * 添加队列
	 * @param str 添加的文本
	 */
	public void AddRow(String str)
	{
		Log.e("数据处理","正在处理"+str);
		for (int i=0;i<20;i++)
		{
			if (!Row[i].equals("nothing")){
				if ((getAppName(Row[i]).equals(getAppName(str)))&&
						(getType(Row[i]).equals(getType(str)))){
					//如果是相同项,那么叠加之
					Row[i]=cooperateInfo(Row[i],str);
					return;
				}
			}
			if (Row[i].equals("nothing"))
			{
				if (i==0){//如果是空队列
					ShowString=str;
				}
				
				Row[i]=str;
				Log.v(""+i,"加入"+str);
				return;
			}
			
		}
	}
	/**
	 * 获得所有队列内容
	 * @return 数组（大小20）
	 */
	public String[] getAllStrings(){
		return Row;
	}
	/**
	 * 分离字符串获得包名
	 * @param info 待分离字符串
	 * @return 包名
	 */
	private String getAppName(String info){
		//分离出包名
		int a=info.indexOf("||||");
  		int b=info.indexOf("||",a+4);
  		int c=info.indexOf("【");
  		int d=info.indexOf("】");
  		String packagename=info.substring(c+1,d);
  		return packagename;
	}
	/**
	 * 分离字符串获得上传下载类型
	 * @param info 待分离字符串
	 * @return 类别（"dn"或"up"）
	 */
	private String getType(String info){
		//分离出类别
		int a=info.indexOf("||||");
  		int b=info.indexOf("||",a+4);
  		int c=info.indexOf("【");
  		int d=info.indexOf("】");
  		String type=info.substring(b+2,c);
  		return type;
	}
	/**
	 * 合并两条信息
	 * @param info1 信息1
	 * @param info2 信息2
	 * @return 合并后信息
	 */
	private String cooperateInfo(String info1,String info2){
		//合并两条信息
		int a=info1.indexOf("||||");
  		int b=info1.indexOf("||",a+4);
  		int c=info1.indexOf("【");
  		int d=info1.indexOf("】");
  		String number1=info1.substring(a+4,b);
  		String appname=info1.substring(0,a);
  		String type=info1.substring(b+2,c);
  		String packagename=info1.substring(c+1,d);
  		a=info2.indexOf("||||");
  		b=info2.indexOf("||",a+4);
  		c=info2.indexOf("【");
  		d=info2.indexOf("】");
  		String number2=info2.substring(a+4,b);
  		Log.v("数据处理","叠加"+number1+" "+ number2 );
  		String number=Integer.valueOf(number1)+Integer.valueOf(number2)+"";
  		return appname+"||||"+number+"||"+type+
  				"【"+packagename+"】";
	}
	/**
	 * 删除第一项目
	 */
	public void DeleteRow()
	{
		for(int i=0;i<20;i++){
			if (Row[i+1].equals("nothing")){
				Log.v("队列删除"+i, Row[i]);
				Row[i]="nothing";
				ShowString=Row[0];
				return;
			}else{
			Row[i]=Row[i+1];
			Log.v("队列复制"+(i+1),""+i);
			}
		}
		ShowString=Row[0];
	}
	/**
	 * Debug调试：打印所有队列信息
	 */
	public void ShowAll(){
		for(int i=0;i<20;i++)
		{
			if (Row[i].equals("nothing"))
				return;
			Log.v("队列"+i, Row[i]);
		}
		Log.v("队列", ShowString);
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
	 * 设置悬浮窗询问是否结束
	 * @param a true为结束，false为还没结束
	 */
	public void SetIsEnd(boolean a){
		IsEnd=a;
	}
	/**
	 * 询问悬浮窗询问是否结束
	 * @return true为结束，false为还没结束
	 */
	public boolean GetIsEnd(){
		return IsEnd;
	}

	HashMap<String, String> map=new HashMap<String, String>(); 
	/**
	 * 读取SQLite中忽略列表的所有内容并保存到Hashmap中
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
	 * 获得某个应用是否在忽略名单中
	 * @param packageName 应用包名
	 * @return true为忽略，false为不忽略
	 */
	public boolean getIgnoredOrNot(String packageName){
		String result=map.get(packageName).toString();
		if (map.containsKey(packageName)){
			//Log.e("忽略名单检测",packageName + "_____"+result);
			if (result.equals("1")){
				return true;
			}else return false;
		}else{
			Log.e("忽略名单检测","没有此项目");
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