package com.standardgis.Services;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.standardgis.georecorder.NotificationActivity;
import com.standardgis.georecorder.ProcessConnection;

public class AssistService extends Service {

    private MyBinder mBinder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ProcessConnection iMyAidlInterface = ProcessConnection.Stub.asInterface(service);
            try {
                Log.i("RemoteService", "connected with " + iMyAidlInterface.getServiceName());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(AssistService.this,"链接断开，重新启动BackgroundService",Toast.LENGTH_LONG).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //android8.0以上通过startForegroundService启动service
                startForegroundService(new Intent(AssistService.this,BackGroundService.class));
            } else {
                startService(new Intent(AssistService.this,BackGroundService.class));
            }
            bindService(new Intent(AssistService.this,BackGroundService.class),connection, Context.BIND_IMPORTANT);
        }
    };

    public AssistService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this,"AssistService 启动",Toast.LENGTH_LONG).show();
        bindService(new Intent(this,BackGroundService.class),connection,Context.BIND_IMPORTANT);



        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        mBinder = new MyBinder();
        return mBinder;
    }
    @Override
    public void onCreate(){
        super.onCreate();
        NotificationActivity notificationActivity=new NotificationActivity(this,"后台保护程序运行中，请勿关闭");
        startForeground(2,notificationActivity.getBuilder().build());
    }

    private class MyBinder extends ProcessConnection.Stub{

        @Override
        public String getServiceName() throws RemoteException {
            return AssistService.class.getName();
        }

    }
}