package com.standardgis.georecorder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

public class NotificationActivity{
    //定义一个NotifactionManager对象
    private NotificationManager manager;
    private Context mContext;
    Notification notification;
    Notification.Builder builder;
    public NotificationActivity(Context context,String mIntent){
        mContext=context;
        manager=(NotificationManager)mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
        //构建一个Notifaction的Builder对象
        builder=new Notification.Builder(mContext);
        builder.setContentTitle("GeoRecoder运行中")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentText(mIntent);
        //设置灯
        builder.setLights(Color.GREEN,1000,2000);
        builder.setOngoing(true);

        //发出通知，参数是（通知栏的id，设置内容的对象）
        // 兼容  API 26，Android 8.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            // 第三个参数表示通知的重要程度，默认则只在通知栏闪烁一下
            NotificationChannel notificationChannel = new NotificationChannel("AppTestNotificationId", "AppTestNotificationName", NotificationManager.IMPORTANCE_DEFAULT);
            // 注册通道，注册后除非卸载再安装否则不改变
            manager.createNotificationChannel(notificationChannel);
            builder.setChannelId("AppTestNotificationId");
        }

    }
    public Notification.Builder getBuilder(){
        return builder;
    }

}
