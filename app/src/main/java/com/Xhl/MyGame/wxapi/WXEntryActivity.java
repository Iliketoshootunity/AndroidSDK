package com.Xhl.MyGame.wxapi;

import android.app.Activity;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;

import com.Xhl.MyGame.GameHelper;
import com.Xhl.MyGame.TencentQQ;
import com.Xhl.MyGame.TencentWX;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private  String m_Tag="WXEntryActivity";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //必须写，不然收不到回调
        TencentWX.m_WXAPI.handleIntent(getIntent(),this);
    }
    //微信发送请求到第三方应用时，会回调该方法
    @Override
    public void onReq(BaseReq baseReq) {

    }
    //第三方应用发送消息到微信时，会回调该方法
    @Override
    public void onResp(BaseResp baseResp) {
        int type=baseResp.getType();
        switch (baseResp.errCode)
        {
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                Log.d(m_Tag,"拒绝微信授权登陆");
                GameHelper.SendPlatformMsgToUnity(GameHelper.PLATFORM_MSG_WXLOGINCALLBACK,1,0,0,"","","");
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                Log.d(m_Tag,"取消登陆");
                GameHelper.SendPlatformMsgToUnity(GameHelper.PLATFORM_MSG_WXLOGINCALLBACK,2,0,0,"","","");
                break;
            case BaseResp.ErrCode.ERR_OK:
                Log.d(m_Tag,"登陆成功");
                if(type==1) //成功
                {
                    String code=((SendAuth.Resp)baseResp).code;
                    Log.d(m_Tag,"登陆成功");
                    TencentWX.GetAccessToken(code);
                }
                else if( type ==2) //分享
                {

                }
                break;
        }
    }
}
