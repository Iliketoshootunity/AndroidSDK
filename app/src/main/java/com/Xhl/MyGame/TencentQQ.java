package com.Xhl.MyGame;
import android.content.Intent;
import android.provider.SyncStateContract;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.connect.common.Constants;
import com.tencent.tauth.Tencent;

import org.json.JSONObject;

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

    //登陆成功
    public static  void Login(JSONObject jsonObj)
    {
        Log.d(m_Tag,"QQ Login");
        m_Tencent.login(m_Activity,"all",m_LoginCallBack);
    }

    //登出
    public  static  void LogOut()
    {

    }

    //登陆成功回调
    public  static  void LoaginCallBack(JSONObject jsonObj)
    {
        Log.d(m_Tag,"QQ LoaginCallBack");
        InitOpenIdAndTocken(jsonObj);
    }

    //登陆之后设置Token等数据
    protected  static void InitOpenIdAndTocken(JSONObject jsonObj)
    {
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

   // private  static  Tecent m_Tecent;
}
