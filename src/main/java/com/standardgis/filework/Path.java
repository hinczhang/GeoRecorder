package com.standardgis.filework;

import android.os.Environment;
public class Path {
    public static final String SDCardPath = Environment.getExternalStorageDirectory().getPath().toString();

    public static final String PATH = SDCardPath + "/Geo"+GetMac.getUniqueID()+"/";

    public static final String COLLECTPATH = PATH+"Recoder/";
}
