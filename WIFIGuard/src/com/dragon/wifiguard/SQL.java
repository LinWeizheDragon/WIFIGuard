package com.dragon.wifiguard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 数据库封装类
 * 10/1修正字符串转义
 * @author 林炜哲
 * @version 1.3.0.5
 */
public class SQL {

	private List<HashMap<String, Object>> mList = new ArrayList<HashMap<String, Object>>();
	private SQLiteDatabase mSQLiteDataBase;

	public String Port = "table1";

	/* 打开数据库，创建表 */
	public void openDataBase() {
		mSQLiteDataBase = AActivity.appContext.openOrCreateDatabase(
				"examples.db", AActivity.appContext.MODE_PRIVATE, null);

		if (Port.equals("")) {
			Log.v("数据库启动出错", "");
			return;
		}
		Log.v("数据库", "打开表" + sqliteEscape(Port));
		String CREATE_TABLE = "create table if not exists " + sqliteEscape(Port)
				+ " (_id INTEGER PRIMARY KEY,name TEXT,pswd TEXT);";
	 	mSQLiteDataBase.execSQL(CREATE_TABLE);
		
	}

	/* 查询数据 */
	public List<HashMap<String, Object>> getData(String where) {
		HashMap<String, Object> hashMap;
		where=sqliteEscape(where);
		//Log.v("数据库服务","转义"+where);
		Cursor cur = mSQLiteDataBase.rawQuery("SELECT * FROM "+where, null);//指针变量
		if (cur != null) {
			if (cur.moveToFirst()) {
				do {
					int id = cur.getInt(cur.getColumnIndex("_id"));
					String name = cur.getString(cur.getColumnIndex("name"));
					String pswd = cur.getString(cur.getColumnIndex("pswd"));

					 //Log.v("数据库-Getting","获得数据"+name+pswd);
					hashMap = new HashMap<String, Object>();
					hashMap.put("recordid", String.valueOf(id));
					hashMap.put("name", name);
					hashMap.put("pswd", pswd);
					mList.add(hashMap);

				} while (cur.moveToNext());//移动指针
			}
		}
		return mList;
	}

	/* 添加一条数据 */
	public void addData(String name, String pswd) {
		// String name = mEditText1.getText().toString().trim();
		// String pswd = mEditText2.getText().toString().trim();

		if (!(name == null || pswd == null)) {
			/* 添加方式一 */
			ContentValues cv = new ContentValues();
			cv.put("name", name);
			cv.put("pswd", pswd);
			mSQLiteDataBase.insert(sqliteEscape(Port), null, cv);
			//Log.v("数据库-Adding",name+pswd);

			/* 动态更新页面显示 */
			//mList = getData("SELECT * FROM " + Port + " where name='" + name
			//		+ "' or name ='" + name + "2'");
		}
	}

	/* 删除一条数据 */
	public void delData(String name) {
		Log.v("数据库", "删除数据" + name);
		String DELETE_DATA = "delete from " + sqliteEscape(Port) + " where name='"+name+"'";//删除操作的SQL语句
		
		mSQLiteDataBase.execSQL(DELETE_DATA);
	}
	
	public void delAll() {
		mSQLiteDataBase.execSQL("DROP TABLE IF EXISTS " + sqliteEscape(Port));
		Log.v("数据库", "删除表" + Port);
	}
	/**
	 * 字符串转义函数
	 * @param keyWord 需转义的字符串
	 * */
	public static String sqliteEscape(String keyWord){  
	    keyWord = keyWord.replace("/", "//");  
	    keyWord = keyWord.replace("'", "''");  
	    keyWord = keyWord.replace("[", "/[");  
	    keyWord = keyWord.replace("]", "/]");  
	    keyWord = keyWord.replace("%", "/%");  
	    keyWord = keyWord.replace("&","/&");  
	    keyWord = keyWord.replace("_", "/_");  
	    keyWord = keyWord.replace("(", "/(");  
	    keyWord = keyWord.replace(")", "/)");  
	    
	    return "\""+keyWord+"\"";  
	}  
	/*public String TransferString(String InputString){
		String OutputString="";
		String CatString[]=new String[]
				{"&","@","_","#"};
		String RepString[]=new String[]
				{"/&","/@","/_","/#"};
		int i=0;
		OutputString=InputString;
		for (i=0;i<10;i++){
			OutputString=replace(CatString[i],RepString[i],OutputString);
		}
		//轮流转义
		return OutputString;
	}*/
	/** 
	   * 替换字符串 
	   * 
	   * @param from String 原始字符串 
	   * @param to String 目标字符串 
	   * @param source String 母字符串 
	   * @return String 替换后的字符串 
	   */  
	public static String replace(String from, String to, String source) {  
	    if (source == null || from == null || to == null)  
	      return null;  
	    StringBuffer bf = new StringBuffer("");  
	    int index = -1;  
	    while ((index = source.indexOf(from)) != -1) {  
	      bf.append(source.substring(0, index) + to);  
	      source = source.substring(index + from.length());  
	      index = source.indexOf(from);  
	    }  
	    bf.append(source);  
	    return bf.toString();  
	}  
}