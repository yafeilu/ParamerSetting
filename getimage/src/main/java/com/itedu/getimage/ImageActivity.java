package com.itedu.getimage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageActivity extends AppCompatActivity {

    private ImageView ivContent;//添加了

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivContent = (ImageView) findViewById(R.id.iv_content);
        findViewById(R.id.btn_get).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                TcpManager.getInstance().getImage("192.168.0.212", 1030,"#3", new TcpManager.GetImageListener() {
//                    @Override
//                    public void success() {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(ImageActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void timeOut() {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(ImageActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void showImage() {
//                       runOnUiThread(new Runnable() {
//                           @Override
//                           public void run() {
//                               ivContent.setImageBitmap(BitmapMagager.getInstance().getBitmap());
//                           }
//                       });
//                    }
//                });
                TcpManager.getInstance().getJson("192.168.0.212", 1030, "#101", new TcpManager.GetDataListener() {
                    @Override
                    public void success() {
                        Log.d("yafei", "showData: 成功");
                    }

                    @Override
                    public void timeOut() {
                        Log.d("yafei", "showData: 失败");
                    }

                    @Override
                    public void showData(String result) {
                        Log.d("yafei", "showData: "+result);
                    }
                });
            }
        });

    }
}
