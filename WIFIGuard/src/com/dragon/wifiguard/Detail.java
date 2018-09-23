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
 * ���������
 * @author �����
 * @version 1.0.0.1
 */
public class Detail extends Service{
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
	 *            ����������
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
		AActivity.LastAsked=" ";
		AActivity.AddLog(AActivity.LastAsked);
		

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v("�������","Created.");
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