package com.itedu.paramersetting.socket;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.itedu.paramersetting.bean.BitmapMagager;
import com.itedu.paramersetting.utils.MyUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by luyafei on 2017/10/23.
 */
public class TcpManager {
    public static final String CONNECTION_SUCCESS = "CONNECTION_SUCCESS";
    public static final String CONNECTION_BREAK = "CONNECTION_BREAK";
    public static final String CONNECTION_FAILED = "CONNECTION_FAILED";
    public static final String CONNECTION_RECEIVE = "CONNECTION_RECEIVE";
    public static final String SEND = "SEND";
    public static final String BITMAP = "BITMAP";

    private TcpManager(){

    }
    private static TcpManager instance=new TcpManager();
    public static TcpManager getInstance(){
        if (instance==null){
            instance=new TcpManager();
        }
        return instance;
    }

    public TcpClient getTcpClient() {
        return tcpClient;
    }

    private TcpClient  tcpClient = new TcpClient() {

        @Override
        public void onConnectSuccess() {

                   MyUtils.getContext().sendBroadcast(new Intent(CONNECTION_SUCCESS));
        }

        @Override
        public void onConnectBreak() {
            //Toast.makeText(MyUtils.getContext(), "连接断开", Toast.LENGTH_SHORT).show();
            MyUtils.getContext().sendBroadcast(new Intent(CONNECTION_BREAK));
           // Toast.makeText(MyUtils.getContext(), "重新连接中...", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onReceive(String s) {
            Log.d("yafei", "onReceive:发送广播");
            Intent intent = new Intent(CONNECTION_RECEIVE);
            intent.putExtra("data",s);
            MyUtils.getContext().sendBroadcast(intent);
        }

        @Override
        public void onReceive(byte[] brr, int offset, int end) {
            Log.d("yafei", "onReceive:发送tupian广播");
            Intent intent = new Intent(BITMAP);
            Bitmap bitmap = BitmapFactory.decodeByteArray(brr, offset, end);
            BitmapMagager.getInstance().setBitmap(bitmap);
            MyUtils.getContext().sendBroadcast(intent);
        }

        @Override
        public void onConnectFalied() {
//            Toast.makeText(MyUtils.getContext(), "连接失败", Toast.LENGTH_SHORT).show();
            MyUtils.getContext().sendBroadcast(new Intent(CONNECTION_FAILED));
        }

        @Override
        public void onSendSuccess(String s) {
            Intent intent = new Intent(SEND);
            intent.putExtra("data",s);
            MyUtils.getContext().sendBroadcast(intent);
        }
    };

}
