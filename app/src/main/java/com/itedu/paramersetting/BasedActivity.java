package com.itedu.paramersetting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.itedu.paramersetting.quanxian.PermissionsManager;
import com.itedu.paramersetting.quanxian.PermissionsResultAction;
import com.itedu.paramersetting.socket.TcpClient;
import com.itedu.paramersetting.socket.TcpManager;
import com.itedu.paramersetting.utils.MyUtils;

import java.io.File;

/**
 * Created by luyafei on 2017/10/24.
 */
public abstract class BasedActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        initView();
        //请求权限
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {

            }

            @Override
            public void onDenied(String permission) {
                finish();
            }
        });
    }
    protected abstract int getLayoutResId();
    protected abstract void initView();

    @Override
    protected void onRestart() {
        super.onRestart();


    }

    @Override
    protected void onPause() {
        super.onPause();
    }


}
