package com.itedu.paramersetting;

import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.itedu.paramersetting.bean.BitmapMagager;
import com.itedu.paramersetting.socket.TcpClient;
import com.itedu.paramersetting.socket.TcpManager;

public class MonitorActivity extends BasedActivity implements TcpClient.ImageLoadingListener {

    private ImageView iv;

    @Override
    protected void showData(String data) {
        Log.d("yafei", "showData: "+data);
    }

    @Override
    protected void failed() {

    }

    @Override
    protected void showBitmap() {
//        Toast.makeText(this, "展示图片", Toast.LENGTH_SHORT).show();


    }

    @Override
    protected void post() {
         tcpClint.getMonitorBitmap();
    }

    @Override
    protected void onDestroy() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                tcpClint.exit();
            }
        }).start();

        super.onDestroy();

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_monitor;
    }

    @Override
    protected void initView() {
        iv = (ImageView)findViewById(R.id.iv_monitor);
        TcpClient tcpClient = TcpManager.getInstance().getTcpClient();
        tcpClient.setImageLoadingListener(this);
    }
    private int i=0;
    @Override
    public void showImage(final String path) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iv.setImageBitmap(BitmapMagager.getInstance().getBitmap());
            }
        });

    }
}
