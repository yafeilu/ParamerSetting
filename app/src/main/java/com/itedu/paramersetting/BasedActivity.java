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
    protected TcpClient tcpClint=TcpManager.getInstance().getTcpClient();
    protected BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case TcpManager.CONNECTION_SUCCESS://连接成功的监听 此时请求首页的数据。
                    Toast.makeText(MyUtils.getContext(), "连接成功", Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            /**
                             * 连接成功发请求
                             */
                            post();
                        }
                    }).start();
                    break;
                case TcpManager.BITMAP:
                    Log.d("yafei", "onReceive: 345");
//                    final Bitmap bit = (Bitmap) intent.getParcelableExtra("bitmap");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showBitmap();
                        }
                    });

                    break;
                case TcpManager.CONNECTION_BREAK://连接断开的广播，收到该广播。执行重新连接
//                    connectServer();
                    break;
                case TcpManager.CONNECTION_RECEIVE://接收到数据的广播，此时将数据展示到首页控件上边。
                    Toast.makeText(MyUtils.getContext(), "接收到数据", Toast.LENGTH_SHORT).show();
                    String data = intent.getStringExtra("data");
                    Log.d("yafei", "onReceive1: "+data);
                    showData(data);
                    break;
                case TcpManager.SEND:
                    String data1 = intent.getStringExtra("data");
                    Log.d("yafei", "onReceive2: "+data1);
                    Toast.makeText(MyUtils.getContext(), "发送成功", Toast.LENGTH_SHORT).show();
                    break;
                case TcpManager.CONNECTION_FAILED:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            failed();
                        }
                    });
                    break;
            }
        }
    };

    protected abstract void failed();

    protected void showBitmap() {
    }

    protected abstract void showData(String data);

    protected abstract void post();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        registerReceiver(receiver,new IntentFilter(TcpManager.CONNECTION_SUCCESS));
//        registerReceiver(receiver,new IntentFilter(TcpManager.CONNECTION_BREAK));
        registerReceiver(receiver,new IntentFilter(TcpManager.CONNECTION_FAILED));
        registerReceiver(receiver,new IntentFilter(TcpManager.CONNECTION_RECEIVE));
        registerReceiver(receiver,new IntentFilter(TcpManager.SEND));
        registerReceiver(receiver,new IntentFilter(TcpManager.BITMAP));
        initView();
        if (tcpClint.isConnected()){
            MyUtils.getContext().sendBroadcast(new Intent(TcpManager.CONNECTION_SUCCESS));
        }else{
            Toast.makeText(this, "侧滑或者下拉联网", Toast.LENGTH_SHORT).show();
        }
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
        registerReceiver(receiver,new IntentFilter(TcpManager.CONNECTION_SUCCESS));
//        registerReceiver(receiver,new IntentFilter(TcpManager.CONNECTION_BREAK));
        registerReceiver(receiver,new IntentFilter(TcpManager.CONNECTION_FAILED));
        registerReceiver(receiver,new IntentFilter(TcpManager.CONNECTION_RECEIVE));
        registerReceiver(receiver,new IntentFilter(TcpManager.SEND));
        registerReceiver(receiver,new IntentFilter(TcpManager.BITMAP));

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }


}
