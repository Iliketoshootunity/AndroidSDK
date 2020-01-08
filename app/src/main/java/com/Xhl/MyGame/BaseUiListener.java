package com.Xhl.MyGame;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

public class BaseUiListener implements  IUiListener  {


    @Override
    public void onComplete(Object o) {

        JSONObject obj=(JSONObject)o;
        if(obj.length()<=0)
        {
            return;
        }
        TencentQQ.LoaginCallBack(obj);
    }

    @Override
    public void onError(UiError uiError) {
        GameHelper.SendPlatformMsgToUnity(GameHelper.PLATFORM_MSG_QQLOGINCALLBACK,1,0,0,"","","");
    }

    @Override
    public void onCancel() {
        GameHelper.SendPlatformMsgToUnity(GameHelper.PLATFORM_MSG_QQLOGINCALLBACK,2,0,0,"","","");
    }
}
