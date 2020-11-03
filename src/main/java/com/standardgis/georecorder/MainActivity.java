package com.standardgis.georecorder;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.standardgis.Services.BackGroundService;

public class MainActivity extends Activity {
    MapView mMapView = null;
    private UiSettings mUiSettings;
    private TextView textView=null;
    Marker marker;
    AMap aMap=null;
    GPSBroadReciver gpsBroadReciver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent OutService=new Intent(this, BackGroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //android8.0以上通过startForegroundService启动service
            startForegroundService(OutService);
        } else {
            startService(OutService);
        }

        textView=findViewById(R.id.textView);
        gpsBroadReciver=new GPSBroadReciver();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("cn.abel.action.broadcast");
        registerReceiver(gpsBroadReciver,intentFilter);

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);


        if (aMap == null) {
            aMap = mMapView.getMap();
            aMap.moveCamera(CameraUpdateFactory.zoomTo(20));
            aMap.showIndoorMap(true);
            mUiSettings=aMap.getUiSettings();
            mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_CENTER);//高德地图标志的摆放位置
            mUiSettings.setZoomControlsEnabled(true);//地图缩放控件是否可见
            mUiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_BUTTOM);//地图缩放控件的摆放位置
            //aMap  为地图控制器对象
            aMap.getUiSettings().setMyLocationButtonEnabled(true);//地图的定位标志是否可见
            aMap.setMyLocationEnabled(true);//地图定位标志是否可以点
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();

    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();

        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    private class GPSBroadReciver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if(action.equals("cn.abel.action.broadcast")){
                String mContet=intent.getStringExtra("GPSMessage");
                String locContent=intent.getStringExtra("AddressMessage");
                String strLat=intent.getStringExtra("lat");
                String strLon=intent.getStringExtra("lon");
                double lat=Double.valueOf(strLat);
                double lon=Double.valueOf(strLon);
                textView.setText(mContet);
                if(marker!=null)marker.destroy();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(lat,lon));
                markerOptions.title("附近的POI：");
                markerOptions.snippet(locContent);
                markerOptions.visible(true);
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
                markerOptions.icon(bitmapDescriptor);
                markerOptions.draggable(true);
                marker = aMap.addMarker(markerOptions);
                marker.showInfoWindow();
            }
        }
    }
}
