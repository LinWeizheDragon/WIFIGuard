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
 * ���ݿ��װ��
 * 10/1�����ַ���ת��
 * @author �����
 * @version 1.3.0.5
 */
public class SQL {

	private List<HashMap<String, Object>> mList = new ArrayList<HashMap<String, Object>>();
	private SQLiteDatabase mSQLiteDataBase;

	public String Port = "table1";

	/* �����ݿ⣬������ */
	public void openDataBase() {
		mSQLiteDataBase = AActivity.appContext.openOrCreateDatabase(
				"examples.db", AActivity.appContext.MODE_PRIVATE, null);

		if (Port.equals("")) {
			Log.v("���ݿ���������", "");
			return;
		}
		Log.v("���ݿ�", "�򿪱�" + sqliteEscape(Port));
		String CREATE_TABLE = "create table if not exists " + sqliteEscape(Port)
				+ " (_id INTEGER PRIMARY KEY,name TEXT,pswd TEXT);";
	 	mSQLiteDataBase.execSQL(CREATE_TABLE);
		
	}

	/* ��ѯ���� */
	public List<HashMap<String, Object>> getData(String where) {
		HashMap<String, Object> hashMap;
		where=sqliteEscape(where);
		//Log.v("���ݿ����","ת��"+where);
		Cursor cur = mSQLiteDataBase.rawQuery("SELECT * FROM "+where, null);//ָ�����
		if (cur != null) {
			if (cur.moveToFirst()) {
				do {
					int id = cur.getInt(cur.getColumnIndex("_id"));
					String name = cur.getString(cur.getColumnIndex("name"));
					String pswd = cur.getString(cur.getColumnIndex("pswd"));

					 //Log.v("���ݿ�-Getting","�������"+name+pswd);
					hashMap = new HashMap<String, Object>();
					hashMap.put("recordid", String.valueOf(id));
					hashMap.put("name", name);
					hashMap.put("pswd", pswd);
					mList.add(hashMap);

				} while (cur.moveToNext());//�ƶ�ָ��
			}
		}
		return mList;
	}

	/* ���һ������ */
	public void addData(String name, String pswd) {
		// String name = mEditText1.getText().toString().trim();
		// String pswd = mEditText2.getText().toString().trim();

		if (!(name == null || pswd == null)) {
			/* ��ӷ�ʽһ */
			ContentValues cv = new ContentValues();
			cv.put("name", name);
			cv.put("pswd", pswd);
			mSQLiteDataBase.insert(sqliteEscape(Port), null, cv);
			//Log.v("���ݿ�-Adding",name+pswd);

			/* ��̬����ҳ����ʾ */
			//mList = getData("SELECT * FROM " + Port + " where name='" + name
			//		+ "' or name ='" + name + "2'");
		}
	}

	/* ɾ��һ������ */
	public void delData(String name) {
		Log.v("���ݿ�", "ɾ������" + name);
		String DELETE_DATA = "delete from " + sqliteEscape(Port) + " where name='"+name+"'";//ɾ��������SQL���
		
		mSQLiteDataBase.execSQL(DELETE_DATA);
	}
	
	public void delAll() {
		mSQLiteDataBase.execSQL("DROP TABLE IF EXISTS " + sqliteEscape(Port));
		Log.v("���ݿ�", "ɾ����" + Port);
	}
	/**
	 * �ַ���ת�庯��
	 * @param keyWord ��ת����ַ���
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
		//����ת��
		return OutputString;
	}*/
	/** 
	   * �滻�ַ��� 
	   * 
	   * @param from String ԭʼ�ַ��� 
	   * @param to String Ŀ���ַ��� 
	   * @param source String ĸ�ַ��� 
	   * @return String �滻����ַ��� 
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