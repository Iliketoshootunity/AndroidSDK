package com.Xhl.MyGame;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;
import android.util.Log;
import com.unity3d.player.UnityPlayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.RandomAccessFile;

public class GameHelper {

    private  static MainActivity m_Activity;
    private  static  String m_PlatformObj = "PlatformObj";      //Unity的平台物体
    private  static  String m_PlatformFunction = "OnMessage"; //Unity的平台脚本
    public static  String m_Tag = "GameHelper";

    public  static  void Init(MainActivity activity)
    {
        m_Activity = activity;
    }

    //发送平台消息给Unity
    public static  void  SendPlatformMsgToUnity(int iMsgId, int iParam1, int iParam2, int iParam3, String strParam1, String strParam2, String strParam3)
    {
        Log.d(m_Tag,"SendPlatformMsgToUnity: MsgId :"+iMsgId+" Int Param1:"+iParam1+" Int Param2:"+iParam2+" Int Param3:"+iParam3+" str parem1:"+strParam1+" str parem2:"+strParam2+" str parem3:"+strParam3);
        String jsonStr=GetJsonStr(iMsgId,iParam1,iParam2,iParam3,strParam1,strParam2,strParam3);
        UnityPlayer.UnitySendMessage(m_PlatformObj,m_PlatformFunction,jsonStr);
    }

    //Unity发送消息给平台
    public  static  void SendUnityMsgToPlatform(int iMsg, int iParam1, int iParam2, int iParam3, String strParam1, String strParam2, String strParam3)
    {
        //TODO
    }

    //Unity获取平台整形数据
    public  static int  GetIntFromPlatform(int type)
    {
        return 0;
    }

    //Unity获取平台长整形数据
    public  static  long GetLongFromPlatform(int type)
    {
        switch (type)
        {
            case 1:
                return  GetTotalMemory();
            case 2:
                return  GetRemaingMemory();
            case  3:
                return  GetUsedMemory();
        }
        return  0;
    }

    //Unity获取平台长整形数据
    public  static  long GetLongFromPlatform2(int type, int iParam1, int iParam2, int iParam3, String strParam1, String strParam2, String strParam3)
    {
        return  0;
    }

    //Unity获取平台字符串形数据
    public  static  String GetStringFromPlatform(int type)
    {
        return "";
    }

    //数据转为Json字符串
    public  static String  GetJsonStr(int iMsgId, int iParam1, int iParam2, int iParam3, String strParam1, String strParam2, String strParam3)
    {
        try
        {
            JSONObject object = new JSONObject();
            object.put("iMsgId",iMsgId);
            object.put("iParam1",iParam1);
            object.put("iParam2",iParam2);
            object.put("iParam3",iParam3);
            object.put("strParam1",strParam1);
            object.put("strParam2",strParam2);
            object.put("strParam3",strParam3);
            return object.toString();
        }
        catch (JSONException E)
        {
            Log.d(m_Tag,"错误： Id为："+iMsgId+" "+E.toString());
            return "";
        }
    }

    //获取剩余的内存
    protected  static  long GetRemaingMemory()
    {
        ActivityManager am=(ActivityManager)m_Activity.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo m=new ActivityManager.MemoryInfo();
        am.getMemoryInfo(m);
        return  m.availMem;
    }

    //获取总的内存
    protected  static  long GetTotalMemory()
    {
        long tm=0;
        try {
            RandomAccessFile reader= new RandomAccessFile("/proc/meminfo","r");
            String load=reader.readLine();
            reader.close();
            String[] totrm=load.split("KB");
            String[] trm=totrm[0].split("");
            tm=Long.parseLong(trm[trm.length-1])*1024;
        }
        catch (IOException ex)
        {
                ex.printStackTrace();;
        }
        return  tm;
    }

    //获取应用使用的内存
    protected  static  long GetUsedMemory()
    {
        Debug.MemoryInfo m= new Debug.MemoryInfo();
        Debug.getMemoryInfo(m);
        return  m.getTotalPss()*1024;
    }
}
