package com.itedu.paramersetting;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itedu.paramersetting.bean.CurveDataList;
import com.itedu.paramersetting.curve.Chart;
import com.itedu.paramersetting.curve.Model;
import com.itedu.paramersetting.socket.TcpClient;
import com.itedu.paramersetting.socket.TcpManager;
import com.itedu.paramersetting.view.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 这个页面记录历史立记录，以曲线的形式展示出来，根据上个页面传递过来的label区分
 */
public class CurveActivity extends BasedActivity {
    private Chart chart;
    /**
     * 记录点击的是否是结束时间的TextView
     */
    private boolean isEnd;
    private TextView tvStart;
    private TextView tvEnd;
    /**
     * 服务器Ip和端口
     */
    private int port =5555;
    private String ip="123.207.17.225";
//    private String ip="192.168.0.113";
    /**
     * 记录曲线要展示的时间段
     */
    private String currentTime;
    private String currentHour;
    private String label;//判断是温度还是湿度

    @Override
    public void onContentChanged() {//setContentView执行完会过来回调这个方法。
        super.onContentChanged();
    }

    private List<Model> parseJson(String data) {
        List<Model> list=new ArrayList<>();
        Type type= new TypeToken<ArrayList<CurveDataList>>(){}.getType();
        Gson gson=new Gson();
        ArrayList<CurveDataList> list1=gson.fromJson(data, type);
        for (CurveDataList curve:list1) {
            String[] split = curve.getTime().split(":");//把时间切割取出小时显示在横轴上
            if("wet".equals(label)){
                list.add(new Model(split[1],Float.valueOf(curve.getWet())));
            }else{
                list.add(new Model(split[1],Float.valueOf(curve.getTemperature())));
            }

        }
        return list;
    }
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_curve;
    }

    @Override
    protected void initView() {
        label = getIntent().getStringExtra("label");
        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        if("wet".equals(label)){
            tvTitle.setText("湿度值时间关系图");
        }
        Date date=new Date(System.currentTimeMillis());
        Log.d("yafei", "initView:"+date.getHours());
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMMdd");
        currentTime=dateFormat.format(date)+(String.format("%02d", date.getHours()-1))+(String.format("%02d", date.getHours()));//实际值
        currentTime="201711291617";
        Log.d("mogu", "initView:实际值 "+currentTime);
//        currentTime="201711141012";//测试值
        Log.d("mogu", "initView:测试值 "+currentTime);
        getData();
        final TimePickerDialog timeDialog=new TimePickerDialog(this);
        timeDialog.setCallback(new TimePickerDialog.OnClickCallback() {
            @Override
            public void onCancel() {
                timeDialog.dismiss();
            }

            @Override
            public void onSure(int year, int month, int day, int hour, int minter, long time) {
                Log.d("yafei", "onSure: "+(year+1970));
                Log.d("yafei", "onSure: "+String.format("%02d", month+1));
                Log.d("yafei", "onSure: "+String.format("%02d", day+1));
                Log.d("yafei", "onSure: "+String.format("%02d", hour+12));
                if (isEnd){//点击结束时间
                    tvEnd.setText((year+1970)+"年"+String.format("%02d", month+1)+"月"+String.format("%02d", day+1)+"日"+String.format("%02d", hour+12)+"时");
                    currentTime=""+(year+1970)+String.format("%02d", month+1)+String.format("%02d", day+1)+currentHour+String.format("%02d", hour+12);
                    Log.d("mogu", "initView:实际值 "+currentTime);
                    getData();
                }else{//点击开始时间
                    currentHour = String.format("%02d", hour+12);
                    tvStart.setText((year+1970)+"年"+String.format("%02d", month+1)+"月"+String.format("%02d", day+1)+"日"+String.format("%02d", hour+12)+"时");
                }
                timeDialog.dismiss();
            }
        });
        chart = (Chart) findViewById(R.id.chart);
//        setContentView();

        chart.setDrawPoints(false).setFillArea(true).setPlayAnim(true);
        findViewById(R.id.tv_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (timeDialog.isShowing()){
                    return;
               }
               isEnd=false;
                timeDialog.show();
            }
        });
        findViewById(R.id.tv_time2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timeDialog.isShowing()){
                    return;
                }
                isEnd=true;
                timeDialog.show();
            }
        });
        tvStart = (TextView) findViewById(R.id.tv_time11);
        tvEnd = (TextView) findViewById(R.id.tv_time22);
    }

    private void getData() {
        com.itedu.paramersetting.manager.TcpManager.getInstance().getJson(ip, port, "&" + currentTime, new com.itedu.paramersetting.manager.TcpManager.GetDataListener() {
            @Override
            public void showData(final String result) {
                Log.d("yafie", "showData: "+currentTime);
                Log.d("yafei", "showData: "+result);
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       List<Model> models = parseJson(result);
                       if(models!=null && models.size()>7){
                           chart.setDatas(models);
                       }else{
                           Toast.makeText(CurveActivity.this, "数据不合法", Toast.LENGTH_SHORT).show();
                       }
                   }
               });
            }

            @Override
            public void success() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CurveActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void timeOut() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CurveActivity.this, "连接超时", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
