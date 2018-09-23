package com.dragon.wifiguard;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Service;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
import android.view.View.OnClickListener;

/**
 * 详情服务类
 * @author 林炜哲
 * @version 1.0.0.1
 */
public class Detail extends Service{
	private static final String SCHEME = "package";
	/**
	 * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.1及之前版本)
	 */
	private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
	/**
	 * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.2)
	 */
	private static final String APP_PKG_NAME_22 = "pkg";
	/**
	 * InstalledAppDetails所在包名
	 */
	private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
	/**
	 * InstalledAppDetails类名
	 */
	private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";
	/**
	 * 调用系统InstalledAppDetails界面显示已安装应用程序的详细信息。 对于Android 2.3（Api Level
	 * 9）以上，使用SDK提供的接口； 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）。
	 * 
	 * @param context
	 *            可用上下文
	 * @param packageName
	 *            应用程序的包名
	 */
	public static void showInstalledAppDetails(Context context, String packageName) {
		Intent intent = new Intent();

			intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
			Uri uri = Uri.fromParts(SCHEME, packageName, null);
			intent.setData(uri);
		AActivity.appContext.startActivity(intent);
		AActivity.LastAsked=" ";
		AActivity.AddLog(AActivity.LastAsked);
		

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v("详情服务","Created.");
		//showInstalledAppDetails(this.getApplicationContext(),"com.dragon.wifiguard");
		stopSelf(startId);
		return startId;
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}