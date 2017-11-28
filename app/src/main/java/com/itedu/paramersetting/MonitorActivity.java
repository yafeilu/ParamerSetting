package com.itedu.paramersetting;

import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.itedu.paramersetting.bean.BitmapMagager;
import com.itedu.paramersetting.socket.TcpClient;
import com.itedu.paramersetting.socket.TcpManager;

public class MonitorActivity extends BasedActivity{

    private ImageView iv;



    @Override
    protected void onDestroy() {
        com.itedu.paramersetting.manager.TcpManager.getInstance().endImage(null,0,"#4");
        super.onDestroy();

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_monitor;
    }

    @Override
    protected void initView() {
        iv = (ImageView)findViewById(R.id.iv_monitor);
        com.itedu.paramersetting.manager.TcpManager.getInstance().getImage(null, 0, "#3", new com.itedu.paramersetting.manager.TcpManager.GetImageListener() {
            @Override
            public void showImage() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iv.setImageBitmap(BitmapMagager.getInstance().getBitmap());
                    }
                });

            }
            @Override
            public void success() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MonitorActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void timeOut() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MonitorActivity.this, "连接超时", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


}
