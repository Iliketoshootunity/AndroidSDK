package com.Xhl.MyGame;
import com.unity3d.player.*;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class MainActivity extends UnityPlayerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GameHelper.Init(this);
        TencentQQ.Init(this);
        TencentWX.Init(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        TencentQQ.onActivityResult(requestCode,resultCode,data);
    }

    public  void  SaveRefreshToken(String token)
    {
        SharedPreferences sharedPreferences = getSharedPreferences("wxcahce", MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("refresh_token",token);
        editor.commit();
    }

    public  String  GetRefreshToken()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("wxcahce", MODE_PRIVATE);
        return sharedPreferences.getString("refresh_token","");
    }

}
