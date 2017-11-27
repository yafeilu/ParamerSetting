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

    /**
     * 记录曲线要展示的时间段
     */
    private String currentTime;
    private String currentHour;

    @Override
    protected void failed() {
//        setContentView();
    }

    @Override
    public void onContentChanged() {//setContentView执行完会过来回调这个方法。
        super.onContentChanged();
    }

    @Override
    protected void showData(String data) {
        Log.d("yafei", "showData: 曲线");
        List<Model> models = parseJson(data);
//        if (models.size()<7){
//            models.add(new Model("123",23));
//            models.add(new Model("123",23));
//        }
        if(models!=null && models.size()>7){
            chart.setDatas(models);
        }else{
            Toast.makeText(this, "数据不合法", Toast.LENGTH_SHORT).show();
        }

    }

    private List<Model> parseJson(String data) {
        List<Model> list=new ArrayList<>();
        Type type= new TypeToken<ArrayList<CurveDataList>>(){}.getType();
        Gson gson=new Gson();
        ArrayList<CurveDataList> list1=gson.fromJson(data, type);
        for (CurveDataList curve:list1) {
            String[] split = curve.getTime().split(":");
            list.add(new Model(split[1],curve.getTemperature()));
        }
//        try {
//            JSONArray array=new JSONArray(data);
//            for (int i=0;i<array.length();i++){
//                JSONObject object=array.getJSONObject(i);
//                list.add(new Model(object.getString("time"),object.getInt("temperature")));
//            }
//        } catch (JSONException e) {
//            Log.d("yafei", "parseJson: json错误");
//            e.printStackTrace();
//        }
        return list;
    }

    @Override
    protected void post() {
        tcpClint.getTemperatureArray(currentTime);
    }
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_curve;
    }

    @Override
    protected void initView() {
        tcpClint.connect(ip,port);
        Date date=new Date(System.currentTimeMillis());
        Log.d("yafei", "initView:"+date.getHours());
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMMddHH");
        currentTime=dateFormat.format(date)+(String.format("%02d", date.getHours()-1));//实际值
        Log.d("mogu", "initView:实际值 "+currentTime);
//        currentTime="201711141012";//测试值
        Log.d("mogu", "initView:测试值 "+currentTime);
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
                    tcpClint.connect(ip,port);
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
}