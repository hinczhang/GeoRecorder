package com.standardgis.standardtime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Stime {
    public String getStime(){

        /*获取当前系统时间*/
        long time = System.currentTimeMillis();
        /*时间戳转换成IOS8601字符串*/
        Date date = new Date(time);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowAsIOS = df.format(date);
        return nowAsIOS;
    }

    public static String getYMD(){
        long time=System.currentTimeMillis();
        Date date=new Date(time);
        DateFormat df=new SimpleDateFormat("yyyy-MM-dd");
        String YMD=df.format(date);
        return YMD;
    }

    public String getUTC(){
        //获取时间
        SimpleDateFormat fmt = new SimpleDateFormat("yyMMddHHmmss");
        fmt.setTimeZone(TimeZone.getTimeZone("Etc/GMT+0"));
        String utcTime=fmt.format(new Date());
        return utcTime;
    }
}
