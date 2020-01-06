package com.Xhl.MyGame;
import android.content.Intent;
import android.provider.SyncStateContract;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.connect.common.Constants;
import com.tencent.tauth.Tencent;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class TencentQQ {

    private  static  Tencent m_Tencent;
    private  static  MainActivity m_Activity;
    private  static  String AppId="11";
    private  static  String m_Tag="TencentQQ";

    private  static  BaseUiListener m_LoginCallBack;

    private  static  void  Init(MainActivity activity)
    {
        Log.d(m_Tag,"QQ Init");
        m_Activity=activity;
        m_Tencent=Tencent.createInstance(AppId,activity.getApplicationContext());
        m_Activity=activity;

        m_LoginCallBack =new BaseUiListener();
    }

    //登陆
    public static  void Login()
    {
        Log.d(m_Tag,"QQ Login");
        m_Tencent.login(m_Activity,"all",m_LoginCallBack);
    }

    //登出
    public  static  void LogOut()
    {
        Log.d(m_Tag,"QQ LogOut");
        m_Tencent.logout(m_Activity);
    }

    //检查当前票据是否有效
    public  static  boolean CheckAutorVaild()
    {
        Log.d(m_Tag,"QQ CheckAutorVaild");
        return m_Tencent.checkSessionValid(AppId);
    }

    //获取刷新票据
    public  static  JSONObject RefreshSession()
    {
        JSONObject jsonObj=m_Tencent.loadSession(AppId);
        if(jsonObj==null)
        {
            Login();
        }
        else
        {
            m_Tencent.initSessionCache(jsonObj);
        }
        Log.d(m_Tag,"RefreshSession"+jsonObj.toString());
        return  SetSelfData(jsonObj);

    }
    //登陆成功回调
    public  static  void LoaginCallBack(final JSONObject jsonObj)
    {
        Log.d(m_Tag,"QQ LoaginCallBack");

        new Thread(new Runnable() {
            @Override
            public void run() {
                InitOpenIdAndTocken(jsonObj);
                JSONObject obj=SetSelfData(jsonObj);
                GameHelper.SendPlatformMsgToUnity(GameHelper.PLATFORM_MSG_QQLOGINCALLBACK,0,0,0,obj.toString(),"","");
            }
        });
    }

    //登陆之后设置Token等数据
    protected  static void InitOpenIdAndTocken(JSONObject jsonObj)
    {
        Log.d(m_Tag,"QQ InitOpenIdAndTocken  Init");
        try {
            String token= jsonObj.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires=jsonObj.getString(Constants.PARAM_EXPIRES_IN);
            String openId=jsonObj.getString(Constants.PARAM_OPEN_ID);
            if(TextUtils.isEmpty(token)||TextUtils.isEmpty(expires)||TextUtils.isEmpty(openId))
            {
                return;
            }
            m_Tencent.setAccessToken(token,expires);
            m_Tencent.setOpenId(openId);
        }
        catch (Exception e)
        {
            Log.d(m_Tag,"QQ InitOpenIdAndTocken error;"+e.toString());
        }
    }

    //对传过来的Json进行统一格式化处理
    protected  static  JSONObject SetSelfData(JSONObject jsonObj)
    {
        Log.d(m_Tag,"SetSelfData");
        JSONObject obj=new JSONObject();
        try
        {
            String token=jsonObj.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires=jsonObj.getString(Constants.PARAM_EXPIRES_IN);
            String openId=jsonObj.getString(Constants.PARAM_OPEN_ID);
            String paytoken=jsonObj.getString("pay_token");
            String pf=jsonObj.getString("pf");
            String pfkey=jsonObj.getString("pfkey");
            String expirestime=jsonObj.getString(Constants.PARAM_EXPIRES_TIME);
            String unionid=GetUnionid(token);

            obj.put("openid",openId);
            obj.put("token",token);
            obj.put("unionid",unionid);
            obj.put("refreshtoken","");
            obj.put("expires",expires);
            obj.put("paytoken",paytoken);
            obj.put("pf",pf);
            obj.put("pfkey",pfkey);
            obj.put("expirestime",expirestime);

            Log.d(m_Tag,"SetSelfData Data:"+obj.toString());
        }
        catch (Exception e)
        {
            Log.e(m_Tag,"SetSelfData Error:"+e.toString());
        }
        return  obj;
    }

    protected  static  String GetUnionid(String token)
    {
        String unionid="";
        try
        {
            URL url=new URL("https://graph.qq.com/oauth2.0/me?access_token="+token+"&unionid=1");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            //这是一个同步请求，可以直接得到结果
            int code=connection.getResponseCode();
            if(code==HttpURLConnection.HTTP_OK)
            {
                InputStream inputStream=connection.getInputStream();
                byte[] data=ReadStream(inputStream);
                String json=new String(data);
                json =json.replace("(","").replace(")","").replace("callback","");
                JSONObject jsonObj=new JSONObject("json");
                unionid =jsonObj.getString("unionid");
            }
        }
        catch (Exception e)
        {
            Log.e(m_Tag,"GetUnionid error:"+ e.toString());
        }
        return  unionid;
    }

    protected static  byte[] ReadStream(InputStream inputStream) throws  Exception
    {
        ByteArrayOutputStream bo=new ByteArrayOutputStream();
        byte[] buffer=new byte[1024];
        int len=0;
        while ((len=inputStream.read(buffer))!=-1)
        {
            bo.write(buffer,0,len);
        }
        bo.close();
        inputStream.close();
        return  bo.toByteArray();
    }

    public  static  void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode== Constants.REQUEST_API)
        {
            if(resultCode==Constants.REQUEST_LOGIN)
            {
                m_Tencent.handleResultData(data,m_LoginCallBack);
            }
            m_Tencent.onActivityResultData(requestCode,resultCode,data,m_LoginCallBack);
        }
    }

}
