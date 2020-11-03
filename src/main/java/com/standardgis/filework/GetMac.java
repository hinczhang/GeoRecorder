package com.standardgis.filework;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.standardgis.georecorder.R;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Handler;

public class GetMac {
    protected volatile static String uniqueId;
    public static void commitUniqueID(Context context){
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        try{
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        uniqueId = deviceUuid.toString();

        }
        catch (SecurityException e){
            AlertDialog alertDialog1 = new AlertDialog.Builder(context)
                    .setTitle("权限警告")//标题
                    .setMessage("请授予其所有权限")//内容
                    .setIcon(R.mipmap.ic_launcher)//图标
                    .create();
            alertDialog1.show();
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        Thread.sleep(3000);//休眠3秒
                        System.exit(0);
                    }catch (InterruptedException e){
                        Log.e("Error in Sleeping",e.getMessage());
                    }
                }
            }.start();

        }

    }


    public static String getUniqueID () {
        return uniqueId;
    }
}
