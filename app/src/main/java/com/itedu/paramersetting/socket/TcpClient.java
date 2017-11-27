package com.itedu.paramersetting.socket;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.itedu.paramersetting.bean.BitmapMagager;
import com.itedu.paramersetting.utils.FileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by luyafei on 2017/10/19.
 */
public abstract class TcpClient implements Runnable{

    private  String IP;
    private int port;
    boolean connect = false;
    private Socket socket;
      private byte[] brr;
    private int len;
    private int count;
    private int offset;
    private int end;
    private PrintWriter pw;
    private int start;

    public void setIP(String IP) {
        this.IP = IP;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void connect(String IP, int port){
        this.IP = IP;
        this.port = port;
        new Thread(this).start();
    }
    @Override
    public void run() {
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(IP,port),3000);
            connect = true;
            Log.d("yafei", "run:Socket连接成功");
            this.onConnectSuccess();
        } catch (IOException e) {
            this.onConnectFalied();
            e.printStackTrace();
        }

    }

    public  boolean isConnected(){
        return connect;
    }

    public void disConnected(){
        connect = false;
    }
    /**
     * 请求温度曲线
     */
    public void getTemperatureArray(String currentTime){
        /**
         * 如果是断开的情况重新连接
         */
        reconnection();
        /**
         * 连接状态直接发参数
         */
        postData("&"+currentTime);
    }

    private void reconnection() {
        if (socket.isConnected()){
            socket=null;
            socket = new Socket();
            try {
                socket.connect(new InetSocketAddress(IP,port));
            } catch (IOException e) {
                onConnectFalied();
                e.printStackTrace();
            }
        }
    }

    /**
     * 请求湿度曲线
     */
    public void getWetArray(){
        /**
         * 如果是断开的情况重新连接
         */
        reconnection();
        /**
         * 连接状态直接发参数
         */

        postData("wet");
    }
    /**
     * 请求首页的单个数据
     */
    public void getSingleParameter(String currentId){
        postData("#1"+currentId);
    }

    private void postData(String msg) {
        Log.d("yafei", "postData: "+msg);
        BufferedReader br = null;
        PrintWriter pw = null;
        InputStreamReader isr=null;
        try {
            br = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            isr=new InputStreamReader(socket.getInputStream());
            Log.d("yafei", "postData: "+br.toString());
            pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    socket.getOutputStream())));
                pw.println(msg);
                pw.flush();
                StringBuilder sb=new StringBuilder();
                char[] data=new char[1024];
                int len=isr.read(data);
                sb.append(data,0,len);
                onReceive(sb.toString());
        } catch (IOException e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }finally {
            try {
                if (br!=null){
                    br.close();
                }
                if (pw!=null){
                    pw.close();
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 请求监控图像
     */
    public void getMonitorBitmap(){
        /**
         * 如果是断开的情况重新连接
         */
        reconnection();
        /**
         * 连接状态直接发参数
         */

        postData4Bitmap("#3");
    }

    private void postData4Bitmap(String s) {
        Log.d("yafei", "postData: "+s);
        pw = null;
        BufferedInputStream isr=null;
        try {
            pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    socket.getOutputStream())));
            isr=new BufferedInputStream(socket.getInputStream());
            pw.println(s);
            pw.flush();
            brr = new byte[1024*40];
            len = 0;
            count = 0;
            while (true){
                len =isr.read(brr, count,1024);
                if (len ==20){//读到图片的结尾
                    offset = 0;//记录数组的偏移量
                    end = 0;//记录图片的字节的个数
                    for (int i = 0; i< count; i++){
                        if (brr[i]==(byte)255 && brr[i+1]==(byte)216){
                            offset =i;
                            break;
                        }
                    }
                    for (int i = 0; i< count; i++){
                        if (brr[i]==(byte)255 && brr[i+1]==(byte)217){
                            end =i+1;
                            break;
                        }
                    }
                    if(offset !=0 && end - offset >1024*10){
                        if(end - offset >0){
                            if (imageLoadingListener!=null){
                                Bitmap bitmap = BitmapFactory.decodeByteArray(brr, offset,(end - offset +1));
                                BitmapMagager.getInstance().setBitmap(bitmap);
                                imageLoadingListener.showImage("");
                            }
                        }
                    }
                    pw.write("#5");
                    pw.flush();
                    brr =new byte[1024*40];//重置数组
                    count =0;//重置总数
                    continue;//进入下一次循环
                }
                count = count + len;
                if (pw!=null){
                    pw.write("#5");
                    pw.flush();
                }

            }
        } catch (IOException e) {
            Log.d("yafei", "postData4Bitmap: 异常");
            e.printStackTrace();
        }finally {
                if (pw !=null){
                    pw.close();
                }
            if (isr!=null){
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public abstract void onConnectSuccess();

    public abstract void onConnectBreak();

    public abstract void onReceive(String s);
    public abstract void onReceive(byte[] brr,int offset,int end);

    public abstract void onConnectFalied();

    public abstract void  onSendSuccess(String s);

    public void getRecord() {
        /**
         * 如果是断开的情况重新连接
         */
        reconnection();
        /**
         * 连接状态直接发参数
         */
        postData("record");
    }

    public void exit() {
//        /**
//         * 如果是断开的情况重新连接
//         */
//        reconnection();
        /**
         * 连接状态直接发参数
         */
        postData("#4");

    }

    public void setImageLoadingListener(ImageLoadingListener imageLoadingListener) {
        this.imageLoadingListener = imageLoadingListener;
    }

    ImageLoadingListener imageLoadingListener;

    public void controllCW(String s) {
        postData(s);
    }

    public void controllCP(String s) {
        postData(s);
    }

    public interface ImageLoadingListener{
        void showImage(String path);
    }
}
