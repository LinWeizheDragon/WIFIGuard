package com.dragon.wifiguard;





import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;

import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;



/**
 * Notification��չ��(δʹ�ã����ο�)
 * @Version 1.0.0.0
 */
public class NotificationExtend extends Activity {
    private Activity context;

    

    public NotificationExtend(Activity context) {

        // TODO Auto-generated constructor stub

        this.context = context;

    }

    

    // ��ʾNotification

    public void showNotification() {

        // ����һ��NotificationManager������

        NotificationManager notificationManager = (

                NotificationManager)context.getSystemService(

                        android.content.Context.NOTIFICATION_SERVICE);

        

        // ����Notification�ĸ�������

        Notification notification = new Notification(
                R.drawable.logo,"WIFIGuard", 
                System.currentTimeMillis());

        // ����֪ͨ�ŵ�֪ͨ����"Ongoing"��"��������"����

        notification.flags |= Notification.FLAG_ONGOING_EVENT;

        // �����ڵ����֪ͨ���е�"���֪ͨ"�󣬴�֪ͨ�Զ������
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;

        notification.defaults = Notification.DEFAULT_LIGHTS;

        notification.ledARGB = Color.BLUE;

        notification.ledOnMS = 5000;

        
        

        Intent notificationIntent = new Intent(context,context.getClass());
        PendingIntent contentIntent = PendingIntent.getActivity(
        context, 0, notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        notification.contentIntent = contentIntent; 
        //notification.setLatestEventInfo(
        //context, contentTitle, contentText, contentIntent);
      //�Զ������   
        RemoteViews rv = new RemoteViews(getPackageName(), R.layout.notification);  
        rv.setTextViewText(R.id.tv_rv, "�����Զ���� notification");  
        //rv.setProgressBar(R.id.pb_rv, 80, 20, false);  
        notification.contentView = rv;  
        // ��Notification���ݸ�NotificationManager
        notificationManager.notify(0, notification);







    }

    

    // ȡ��֪ͨ

    public void cancelNotification(){

        NotificationManager notificationManager = (

                NotificationManager) context.getSystemService(

                        android.content.Context.NOTIFICATION_SERVICE);

        notificationManager.cancel(0);

    }

}