package com.dragon.wifiguard;

import android.graphics.drawable.Drawable;
import android.util.Log;
/**
 * 数据信息存储类
 * @author 林炜哲
 * @version 1.0.0.0
 */
public class dataInfo {
public static int Num;
public static int appUid[];
public static String appName[];
public static String packageName[];
public static String versionName[];
public static int versionCode[];
public static Drawable appIcon[];
public static int InitDownBytes[];
public static int InitUpBytes[];
public static int LastDownBytes[];
public static int LastUpBytes[];
public static void Rebuild(int num)
{
	Log.v("重建：",""+num);
	Num=num;
	appUid= new int [num];
	versionCode= new int [num];
	appName= new String [num];
	packageName= new String [num];
	versionName= new String [num];
	appIcon=new Drawable [num];
	InitDownBytes=new int [num];
	InitUpBytes=new int [num];
	LastDownBytes=new int [num];
	LastUpBytes=new int [num];
	}
}