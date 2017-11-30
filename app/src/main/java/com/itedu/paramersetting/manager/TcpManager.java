package com.itedu.paramersetting.manager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.itedu.paramersetting.bean.BitmapMagager;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by luyafei on 2017/11/24.
 */

public class TcpManager {
    private String IP="192.168.0.212";
    private int port=1030;
    private GetImageListener imageListener;
    private TcpManager(){}
    private static TcpManager instance;
    private Socket socket=null;
    private byte[] brr = new byte[1024*40];
    private int len = 0;//每一次从输入流中读到的长度。
    private int count = 0;//读到的总数，每一次读到衣服完整的图片要清零。
    private int offset=0;
    private  PrintWriter pw;
    private GetDataListener getDataListener;
    public static TcpManager getInstance(){
        if (instance==null){
            instance=new TcpManager();
        }
        return instance;
    }
    public void setIP(String IP) {
        this.IP = IP;
    }

    public void setPort(int port) {
        this.port = port;
    }
    public void getImage(String IP,int port,String pra,GetImageListener imageListener){
        this.imageListener=imageListener;
        new Thread(new TcpClint(pra)).start();//发送数据的线程
        new Thread(new GetAvailableByte()).start();//接收数据的线程
    }
    public void endImage(String IP,int port,String pra){
        imageListener=null;
        new Thread(new TcpClint(pra)).start();
    }
    public void getJson(String IP,int port,String pra,GetDataListener getDataListener){
        this.IP=IP;
        this.port=port;
        this.getDataListener=getDataListener;
        new Thread(new TcpClint(pra)).start();//发送数据的线程
        new Thread(new GetDataThread()).start();//接收数据的线程
    }
    public void controlCW(String s) {
        new Thread(new TcpClint(s)).start();//发送数据的线程
    }

    public void controlCP(String s) {
        new Thread(new TcpClint(s)).start();//发送数据的线程
    }
    private class GetDataThread implements Runnable{
        InputStreamReader isr=null;
        @Override
        public void run() {
            while (true){
                try {
                    if (socket!=null){
//                        Log.d("yafei", "run: jieshou ");
                        isr=new InputStreamReader(socket.getInputStream());
                        StringBuilder sb=new StringBuilder();
                        char[] data=new char[1024*10];
                        int len=isr.read(data);
                        sb.append(data,0,len);
                        if (getDataListener!=null){
                            Log.d("yafei", "run: jieshou 222"+sb.toString());
                            getDataListener.showData(sb.toString());
                            getDataListener=null;
                        }
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private class TcpClint implements Runnable{
        private String pra;//请求的参数
        public TcpClint(String pra) {
            this.pra = pra;
        }
        @Override
        public void run() {
            try {
                    socket=new Socket();
                    socket.connect(new InetSocketAddress(IP,port),3000);
                    if (imageListener!=null){
                        imageListener.success();
                    }
                    if (getDataListener!=null){
                        getDataListener.success();
                    }
                pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                        socket.getOutputStream())));
                pw.println(pra);//发送对应接口的指令
                pw.flush();
            } catch (IOException e) {
                if (e instanceof SocketTimeoutException){
                    if (imageListener!=null){
                        imageListener.timeOut();//超时提醒
                    }
                    if (getDataListener!=null){
                        getDataListener.timeOut();
                    }
                }
                e.printStackTrace();
            }finally {
//                if (socket != null) {
//                    try {
//                        socket.close();
//                        socket=null;
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//                if (pw!=null){
//                    pw.close();
//                }
            }
        }

    }
    public interface NetStateListener{
        void success();
        void timeOut();
    }
    public interface GetDataListener extends NetStateListener{//普通JSon数据回调接口

        void showData(String result);
    }
    public interface GetImageListener extends NetStateListener{//图片回调接口
        void showImage();
    }
    private class GetAvailableByte implements Runnable{

        private WeakReference<byte[]> clone;

        @Override
        public void run() {
            Log.d("yafei", "run: 接收线程开启");
            while(true){
                if (socket==null){
                    continue;//socket==null就结束循环进入下一次，知道socket!=null
                }
                BufferedInputStream isr=null;
                try {
                    isr=new BufferedInputStream(socket.getInputStream());
                    if (count+1024>40960){
                        count=0;
                    }
                    len = isr.read(brr, count, 1024);//返回值代表读到字节的个数
                    Log.d("yafei", "run: "+len);
//                    if (len==22){//图片头
//                        pw.write("#5");
//                        pw.flush();
//                    }
                    if(len==20){//说明此时读到一副图片的末尾
                        clone = new WeakReference<byte[]>(brr.clone());
                        new Thread(new Byte2Bitmap(clone,count)).start();
                        pw.write("#5");
                        pw.flush();
//                        brr =new byte[1024*40];//重置数组
                        count =0;//重置总数
                        continue;//进入下一次循环
                    }
                    count=count+len;
                    if (nextImage==null){
                        nextImage=new NextImage();
                        new Thread(nextImage).start();
                    }
                    lastFrame=System.currentTimeMillis();//记录当前帧的
                    pw.write("#5");//发请求接收一下一帧
                    pw.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private long lastFrame;
    private NextImage nextImage;
    private class NextImage implements Runnable{
        @Override
        public void run() {
           while (true){
                if(System.currentTimeMillis()-lastFrame>250){
                    pw.write("#5");//请求超时，发另一帧图片！
                    pw.flush();
                    lastFrame=System.currentTimeMillis();
                }
           }
        }
    }
    private int end;
    private class Byte2Bitmap implements Runnable{
        private WeakReference<byte[]> bytes;
        private int count1;

        public Byte2Bitmap(WeakReference<byte[]> bytes,int count1) {
            this.bytes = bytes;
            this.count1=count1;
        }

        @Override
        public void run() {
           if (bytes==null ){
               return;
           }
           if (bytes.get()==null){
               return;
           }
            for (int i = 0; i< count1; i++){
                if (bytes.get()[i]==(byte)255 && bytes.get()[i+1]==(byte)216){//寻找图片开头位置
                    offset =i;
                    break;
                }
            }
            Log.d("yafei", "run: 图片开头偏移量"+count1+"偏移量"+offset);
            for (int i = 0; i< count1; i++){
                if (bytes.get()[i]==(byte)255 && bytes.get()[i+1]==(byte)217){//寻找图片的结束位置
                    end =i+1;
                    break;
                }
            }
            Log.d("yafei", "run: "+(end-offset));
            if(offset !=0 && end - offset >1024*10){
                if(end - offset >0){
                    if (imageListener!=null){
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes.get(), offset,(end - offset +1));
                        BitmapMagager.getInstance().setBitmap(bitmap);
                        imageListener.showImage();
                    }
                }
                System.gc();
                System.gc();
            }else{
                offset=0;
                end=0;
            }
        }
    }
}
