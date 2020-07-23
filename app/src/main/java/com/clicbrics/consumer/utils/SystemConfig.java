package com.clicbrics.consumer.utils;

import android.content.Context;
import android.util.Log;

import com.clicbrics.consumer.wrapper.NotificationWrapper;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paras
 * Class Responsible for save notification data to cache
 */
public class SystemConfig {
    private static final String SYS_CONFIG_FILE = "sysconfig";
    private static final String TAG = "SystemConfig";
    private static Context mContext;
    private static SystemConfig msystemConfig;
    public static SystemConfig getInstanceSystemConfig(Context context)
    {
        mContext=context;
        if(msystemConfig==null)
        {
            msystemConfig = new SystemConfig();
        }
        return msystemConfig;
    }


    public static ArrayList<NotificationWrapper> getNotificationList(){
        ArrayList<NotificationWrapper> notificationInfo;

        Type listType = new TypeToken<List<NotificationWrapper>>(){}.getType();

        notificationInfo = new Gson().fromJson(readConfigFromFile(), listType);
        return notificationInfo;

    }
    private static String readConfigFromFile(){
        String str="";
        try {
            File file = new File(mContext.getFilesDir(), SYS_CONFIG_FILE);
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                byte[] data = new byte[(int) file.length()];
                fis.read(data);
                fis.close();
                str = new String(data);
                Log.d(TAG, "get config: "+str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public void resetConfig(){
        try {
            File file = new File(mContext.getFilesDir(), SYS_CONFIG_FILE);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void writeConfig(String str){
        try {
        //Save to Files
        Gson gson = new Gson();
        File file = new File(mContext.getFilesDir(), SYS_CONFIG_FILE);
        FileOutputStream fos = new FileOutputStream(file);
//        String str = gson.toJson(SystemConfig.this);
                Log.d(TAG, "save config: "+str);
        fos.write(str.getBytes());
        fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String loadJSONFromAsset(String fileName) {
        String json = null;
        try {
            InputStream is = mContext.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
