package com.Xhl.MyGame;

import android.util.Log;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TencentWX {
    public   static IWXAPI m_WXAPI;
    private  static  MainActivity m_MainActivity;
    private  static  String m_Tag="TencentWX";
    private  static  String AppId= "sssss";
    private  static  String AppSecret="ssdsdwdw";

    public  void Init(MainActivity activity)
    {
        Log.d(m_Tag,"WX Init ");
        m_MainActivity=activity;
        m_WXAPI = WXAPIFactory.createWXAPI(m_MainActivity,AppId,true);
        m_WXAPI.registerApp(AppId);
    }

    public  void Login()
    {
        Log.d(m_Tag,"QQ Login");
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        m_WXAPI.sendReq(req);
    }


    protected  static  void  GetAccessToken (final

                                             String wxcode)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try
                {
                    URL url=new URL("https://api.weixin.qq.com/sns/oauth2/access_token?appid="+AppId+"&secret="+AppSecret+"&code="+wxcode+"&grant_type=authorization_code");
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
                        JSONObject jsonObj=new JSONObject(json);
                        JSONObject jsonData =SetSelfData(jsonObj);
                        GameHelper.SendPlatformMsgToUnity(GameHelper.PLATFORM_MSG_WXLOGINCALLBACK,0,0,0,"","",jsonData.toString());
                    }
                }
                catch (Exception e)
                {
                    Log.e(m_Tag,"GetAccessToken error:"+ e.toString());
                }
            }
        });

    }

    //对传过来的Json进行统一格式化处理
    protected  static  JSONObject SetSelfData(JSONObject jsonObj)
    {
        Log.d(m_Tag,"SetSelfData");
        JSONObject obj=new JSONObject();
        try
        {
            String token=jsonObj.getString("access_token");
            String expires=jsonObj.getString("expires_in");
            String refreshtoken=jsonObj.getString("refresh_token");
            String openId=jsonObj.getString("openid");
            String scope=jsonObj.getString("scope");
            String expirestime=jsonObj.getString("expires_in");
            String unionid=GetUnionid(token,openId);

            obj.put("openid",openId);
            obj.put("token",token);
            obj.put("unionid",unionid);
            obj.put("refreshtoken",refreshtoken);
            obj.put("expires",expires);
            obj.put("paytoken","");
            obj.put("pf","");
            obj.put("pfkey","");
            obj.put("expirestime",expirestime);

            Log.d(m_Tag,"SetSelfData Data:"+obj.toString());
        }
        catch (Exception e)
        {
            Log.e(m_Tag,"SetSelfData Error:"+e.toString());
        }
        return  obj;
    }

    protected  static  String GetUnionid(String token ,String openid)
    {
        String unionid="";
        try
        {
            URL url=new URL("https://api.weixin.qq.com/sns/userinfo?access_token="+token+"&openid="+openid+"");
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
                JSONObject jsonObj=new JSONObject(json);
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

}
