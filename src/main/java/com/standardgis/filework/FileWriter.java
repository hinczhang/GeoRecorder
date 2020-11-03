package com.standardgis.filework;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.standardgis.standardtime.Stime;
public class FileWriter {


    public void getInfo(String time,int code,double lat,double lon,String utc){
        String line=time+'\t'+lat+'\t'+lon+'\t'+code+'\t'+utc+'\n';
        try {
            mWriting(line);
        }catch (FileNotFoundException e){
            Log.e("Error in FileNotFound",e.getMessage());
        }
    }


    private void mWriting(String mLine) throws FileNotFoundException {
        String fileName=Path.COLLECTPATH+Stime.getYMD()+".txt";
        File file=new File(Path.COLLECTPATH);
        if(!file.exists())file.mkdirs();
        try{
            FileOutputStream mFile=new FileOutputStream(fileName,true);
            try{
            mFile.write(mLine.getBytes());
            }catch (IOException e){
                Log.e("Error in Writing",e.getMessage());
            }
            try {
                mFile.close();
            }catch (IOException e){
                Log.e("Error in Closing",e.getMessage());
            }
        }
        catch (FileNotFoundException e){
            Log.e("Error in Creating file",e.getMessage());
        }
    }


}
