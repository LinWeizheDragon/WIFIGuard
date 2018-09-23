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
 * Notification扩展类(未使用，做参考)
 * @Version 1.0.0.0
 */
public class NotificationExtend extends Activity {
    private Activity context;

    

    public NotificationExtend(Activity context) {

        // TODO Auto-generated constructor stub

        this.context = context;

    }

    

    // 显示Notification

    public void showNotification() {

        // 创建一个NotificationManager的引用

        NotificationManager notificationManager = (

                NotificationManager)context.getSystemService(

                        android.content.Context.NOTIFICATION_SERVICE);

        

        // 定义Notification的各种属性

        Notification notification = new Notification(
                R.drawable.logo,"WIFIGuard", 
                System.currentTimeMillis());

        // 将此通知放到通知栏的"Ongoing"即"正在运行"组中

        notification.flags |= Notification.FLAG_ONGOING_EVENT;

        // 表明在点击了通知栏中的"清除通知"后，此通知自动清除。
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
      //自定义界面   
        RemoteViews rv = new RemoteViews(getPackageName(), R.layout.notification);  
        rv.setTextViewText(R.id.tv_rv, "我是自定义的 notification");  
        //rv.setProgressBar(R.id.pb_rv, 80, 20, false);  
        notification.contentView = rv;  
        // 把Notification传递给NotificationManager
        notificationManager.notify(0, notification);







    }

    

    // 取消通知

    public void cancelNotification(){

        NotificationManager notificationManager = (

                NotificationManager) context.getSystemService(

                        android.content.Context.NOTIFICATION_SERVICE);

        notificationManager.cancel(0);

    }

}