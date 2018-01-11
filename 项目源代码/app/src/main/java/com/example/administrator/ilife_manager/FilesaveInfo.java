package com.example.administrator.ilife_manager;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lenovo on 2017/12/27.
 */

public class FilesaveInfo {
    public static boolean saveUserInfo(Context context, String nickname, String email,String phone)
    {
        try
        {
            FileOutputStream fos=context.openFileOutput("information.txt",Context.MODE_PRIVATE);
            fos.write((nickname+":"+email+":"+phone).getBytes());
            fos.close();
            return true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
    public static Map<String,String> getUserInfo(Context context)
    {
        String content="";
        try
        {
            FileInputStream fis=context.openFileInput("information.txt");
            byte[]buffer=new byte[fis.available()];
            fis.read(buffer);
            content=new String(buffer);
            Map<String,String> userMap=new HashMap<String,String>();
            String[]infos=content.split(":");
            userMap.put("nickname",infos[0]);
            userMap.put("email",infos[1]);
            userMap.put("phone",infos[2]);
            fis.close();
            return userMap;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
