package com.Xhl.MyGame;
import com.unity3d.player.*;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends UnityPlayerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GameHelper.Init(this);
        TencentQQ.Init(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        TencentQQ.onActivityResult(requestCode,resultCode,data);
    }
}
