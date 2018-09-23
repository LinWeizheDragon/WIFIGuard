package com.dragon.wifiguard;

import android.graphics.drawable.Drawable;
import android.util.Log;
/**
 * 应用程序信息存储类
 * @author 林炜哲
 * @version 1.0.0.3
 */
public class AppInfo {
public int appUid=0;
public String appName="";
public String packageName="";
public String versionName="";
public int versionCode=0;
public Drawable appIcon=null;
public void print(){

Log.v("app","Name:"+appName+" Package:"+packageName);
Log.v("app","Name:"+appName+" versionName:"+versionName);
Log.v("app","Name:"+appName+" versionCode:"+versionCode);
}

}