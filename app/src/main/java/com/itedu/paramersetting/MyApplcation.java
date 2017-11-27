package com.itedu.paramersetting;

import android.app.Application;
import android.content.Context;

import com.itedu.paramersetting.socket.TcpManager;

/**
 * Created by luyafei on 2017/10/23.
 */
public class MyApplcation extends Application {
    private static  Context instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        TcpManager.getInstance();
    }
    public static Context getContext(){
        return instance;
    }
}
