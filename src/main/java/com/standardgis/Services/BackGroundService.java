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

import com.amap.api.location.AMapLocation;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.standardgis.filework.FileWriter;
import com.standardgis.filework.GetMac;
import com.standardgis.georecorder.NotificationActivity;
import com.standardgis.georecorder.ProcessConnection;
import com.standardgis.location.MapLocationHelper;
import com.standardgis.standardtime.Stime;

import java.util.Timer;
import java.util.TimerTask;


public class BackGroundService extends Service {

    private MyBinder mBinder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ProcessConnection iMyAidlInterface = ProcessConnection.Stub.asInterface(service);
            try {
                Log.i("LocalService", "connected with " + iMyAidlInterface.getServiceName());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(BackGroundService.this,"链接断开，重新启动 AssistService",Toast.LENGTH_LONG).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //android8.0以上通过startForegroundService启动service
                startForegroundService(new Intent(BackGroundService.this,AssistService.class));
            } else {
                startService(new Intent(BackGroundService.this,AssistService.class));
            }
            bindService(new Intent(BackGroundService.this,AssistService.class),connection, Context.BIND_IMPORTANT);
        }
    };

    public BackGroundService() {

    }

    private Timer timer;
    GeocodeSearch geocodeSearch=null;
    FileWriter fileWriter;
    Intent intent;
    Context context=this;
    @Override
    public void onCreate() {
        super.onCreate();
        NotificationActivity notificationActivity=new NotificationActivity(this,"GeoRecoder运行中，请勿关闭");
        startForeground(1,notificationActivity.getBuilder().build());
        intent=new Intent();
        intent.setAction("cn.abel.action.broadcast");
        geocodeSearch=new GeocodeSearch(this);
        fileWriter=new FileWriter();
        timer=new Timer();
        timer.schedule(new mTimerTask(),0,3000);
        GetMac.commitUniqueID(this);

    }

    public class mTimerTask extends TimerTask {
        @Override
        public void run(){
            //初始化定位实例
            MapLocationHelper helper=new MapLocationHelper(context, new MapLocationHelper.LocationCallBack() {
                @Override
                public void onCallLocationSuc(AMapLocation location) {
                    Stime mytime=new Stime();
                    int rCode=location.getTrustedLevel();
                    if(rCode==AMapLocation.TRUSTED_LEVEL_LOW||rCode==AMapLocation.TRUSTED_LEVEL_BAD)rCode=-1;
                    else rCode=1;
                    final String ShowInfo="Lat:"+location.getLatitude()+" Lon:"+location.getLongitude()+" quality:"+rCode+'\n'+
                            "UTC时间:"+mytime.getUTC()+" time:"+mytime.getStime()+"\n本机UID："+ GetMac.getUniqueID();
                    final double lat=location.getLatitude();
                    final double lon=location.getLongitude();
                    fileWriter.getInfo(mytime.getStime(),rCode,location.getLatitude(),location.getLongitude(),mytime.getUTC());

                    geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
                        @Override
                        public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {


                            RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
                            String formatAddress = regeocodeAddress.getBuilding();

                           if(formatAddress.isEmpty())
                               formatAddress="未获得附近POI";

                            intent.putExtra("GPSMessage",ShowInfo);
                            intent.putExtra("AddressMessage",formatAddress);
                            intent.putExtra("lat",String.valueOf(lat));
                            intent.putExtra("lon",String.valueOf(lon));
                            context.sendBroadcast(intent);

                        }

                        @Override
                        public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

                        }
                    });
                    LatLonPoint latLonPoint=new LatLonPoint(location.getLatitude(),location.getLongitude());
                    RegeocodeQuery query=new RegeocodeQuery(latLonPoint,300,GeocodeSearch.AMAP);
                    geocodeSearch.getFromLocationAsyn(query);
                }

            });
            helper.startMapLocation();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this,"BackgroundService 启动",Toast.LENGTH_LONG).show();
        startService(new Intent(BackGroundService.this,AssistService.class));
        bindService(new Intent(this,AssistService.class),connection, Context.BIND_IMPORTANT);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        mBinder = new MyBinder();
        return mBinder;
    }

    private class MyBinder extends ProcessConnection.Stub{

        @Override
        public String getServiceName() throws RemoteException {
            return BackGroundService.class.getName();
        }

    }

}
