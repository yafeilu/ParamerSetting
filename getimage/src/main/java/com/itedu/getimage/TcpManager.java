package com.itedu.getimage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by luyafei on 2017/11/24.
 */

public class TcpManager {
    private String IP;
    private int port;
    private GetImageListener imageListener;
    private TcpManager (){}
    private static TcpManager instance;
    private Socket socket=null;
    private byte[] brr = new byte[1024*40];
    private int len = 0;//每一次从输入流中读到的长度。
    private int count = 0;//读到的总数，每一次读到衣服完整的图片要清零。
    private int offset=0;
    private  PrintWriter pw;
    public static TcpManager getInstance(){
        if (instance==null){
            instance=new TcpManager();
        }
        return instance;
    }
    public void connection(String IP,int port,GetImageListener imageListener){
        this.IP=IP;
        this.port=port;
        this.imageListener=imageListener;
        new Thread(new TcpClint()).start();//发送数据的线程
        new Thread(new GetAvailableByte()).start();//接收数据的线程
    }
    private class TcpClint implements Runnable{

        @Override
        public void run() {
            try {
               socket=new Socket();
               socket.connect(new InetSocketAddress(IP,port),3000);
                if (imageListener!=null){
                    imageListener.success();//成功提醒
                }
                pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                        socket.getOutputStream())));
                pw.println("#3");//发送图片指令
                pw.flush();
            } catch (IOException e) {
                if (e instanceof SocketTimeoutException){
                    if (imageListener!=null){
                        imageListener.timeOut();//超时提醒
                    }
                }
                e.printStackTrace();
            }
        }

    }
    public interface GetImageListener{
        void success();
        void timeOut();
        void showImage();
    }
    private class GetAvailableByte implements Runnable{

        private byte[] clone;

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
                    if(len==20){//说明此时读到一副图片的末尾
                        clone = brr.clone();
                        new Thread(new Byte2Bitmap(clone,count)).start();
                        pw.write("#5");
                        pw.flush();
//                        brr =new byte[1024*40];//重置数组
                        count =0;//重置总数
                        continue;//进入下一次循环
                    }
                    count=count+len;
//                    if (nextImage==null){
//                        nextImage=new NextImage();
//                        new Thread(nextImage).start();
//                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                ssh-keygen -t rsa -C "1204808083@qq.com"
            }
        }
    }
    private NextImage nextImage;
    private class NextImage implements Runnable{
        @Override
        public void run() {
            while (true){
                try {
                    Thread.sleep(25);
//                    if (pw!=null){
                        pw.write("#5");//发请求接收一下一帧
                        pw.flush();
//                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private int end;
    private class Byte2Bitmap implements Runnable{
        private byte[] bytes;
        private int count1;

        public Byte2Bitmap(byte[] bytes,int count1) {
            this.bytes = bytes;
            this.count1=count1;
        }

        @Override
        public void run() {
            for (int i = 0; i< count1; i++){
                if (bytes[i]==(byte)255 && bytes[i+1]==(byte)216){//寻找图片开头位置
                    offset =i;
                    break;
                }
            }
            Log.d("yafei", "run: 图片开头偏移量"+count1+"偏移量"+offset);
            for (int i = 0; i< count1; i++){
                if (bytes[i]==(byte)255 && bytes[i+1]==(byte)217){//寻找图片的结束位置
                    end =i+1;
                    break;
                }
            }
            Log.d("yafei", "run: "+(end-offset));
            if(offset !=0 && end - offset >1024*10){
                if(end - offset >0){
                    if (imageListener!=null){
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, offset,(end - offset +1));
                        BitmapMagager.getInstance().setBitmap(bitmap);
                        imageListener.showImage();
                    }
                }
            }else{
                offset=0;
                end=0;
            }
        }
    }
}
