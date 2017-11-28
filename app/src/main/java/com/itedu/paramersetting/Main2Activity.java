package com.itedu.paramersetting;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.itedu.paramersetting.bean.HomeInfo;
import com.itedu.paramersetting.manager.TcpManager;
import com.itedu.paramersetting.view.DevicePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Main2Activity extends BasedActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener {
    private DrawerLayout drawer;
    private EditText etIP;
    private EditText etPort;
    //温度控件
    private TextView tvAir;
    private TextView tvTemp;
    private TextView tvGas;
    private TextView tvIllumination;
    private TextView tvWet;
    //设备编号   更新时间
    private TextView tvId;
    private TextView tvTime;

    //更新最新的数据
    private SwipeRefreshLayout srRefresh;

    //记录当前的设备号，初始化为01
    private String currentId="01";
    private DevicePickerDialog devicePickerDialog;
    private HomeInfo homeInfo;
    private HomeInfo parseJson(String data) {
        HomeInfo homeInfo=null;
        JSONObject jsonObject= null;
        try {
            jsonObject = new JSONObject(data);
            homeInfo=new HomeInfo();
            homeInfo.setAir(jsonObject.getInt("air"));
            homeInfo.setGas(jsonObject.getInt("gas"));
            homeInfo.setIllumination(jsonObject.getInt("light"));
            homeInfo.setTemperature(jsonObject.getInt("temp"));
            homeInfo.setWet(jsonObject.getInt("wet"));
            homeInfo.setGasctl(jsonObject.getInt("gasctl"));
            homeInfo.setWetctl(jsonObject.getInt("wetctl"));
        } catch (JSONException e) {
            Toast.makeText(Main2Activity.this, "error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return homeInfo;
    }
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main2;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        findViewById(R.id.rl_temperature).setOnClickListener(this);
        findViewById(R.id.rl_wet).setOnClickListener(this);
        findViewById(R.id.rl_air_quality).setOnClickListener(this);
        findViewById(R.id.rl_illumination).setOnClickListener(this);
        findViewById(R.id.rl_noxious_gas).setOnClickListener(this);
        tvAir = (TextView) findViewById(R.id.tv_air);
        tvGas = (TextView) findViewById(R.id.tv_gas);
        tvId=(TextView)findViewById(R.id.tv_id);
        tvTime=(TextView)findViewById(R.id.tv_time);
        tvIllumination = (TextView) findViewById(R.id.tv_illumination);
        tvWet = (TextView) findViewById(R.id.tv_wet);
        tvTemp = (TextView) findViewById(R.id.tv_temp);
        srRefresh=(SwipeRefreshLayout)findViewById(R.id.sr_refresh);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        /**
         * 网络连接的接口
         */
        View view = navigationView.getHeaderView(0);
        Button btnConnect = (Button)(view.findViewById(R.id.btn_connect));
        etIP = (EditText)(view.findViewById(R.id.et_ip));
        etPort = (EditText)(view.findViewById(R.id.et_port));
        btnConnect.setOnClickListener(this);
        srRefresh.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        srRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                /**
                 * 去请求新数据
                 */
//                if(srRefresh.isRefreshing()){
//                    Toast.makeText(Main2Activity.this, "正在加载数据哟！亲", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                srRefresh.setRefreshing(true);
                connectServer();
            }
        });
        devicePickerDialog = new DevicePickerDialog(this);
        devicePickerDialog.setCallback(new DevicePickerDialog.OnClickCallback() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onSure(String data) {
                /**
                 * 根据选择的设备号更新当前的设备
                 */
                currentId=data;
                Log.d("yafei", "onSure: "+currentId);
                /**
                 * 连接网络
                 */
                connectServer();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        TcpManager.getInstance().setIP(etIP.getText().toString());
        TcpManager.getInstance().setPort(Integer.valueOf(etPort.getText().toString()));
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        if (homeInfo==null){
            return true;
        }
        if (homeInfo.getGasctl()==0){
            menu.getItem(R.id.action_cp).setTitle("打开排风扇");
        }else{
            menu.getItem(R.id.action_cp).setTitle("关闭排风扇");
        }
        if (homeInfo.getWetctl()==0){
            menu.getItem(R.id.action_cw).setTitle("打开加湿器");
        }else{
            menu.getItem(R.id.action_cw).setTitle("关闭加湿器");
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            /**
             * 点击切换设备   弹出切换设备的对话框
             */
            devicePickerDialog.show();
            return true;
        }else if(id == R.id.action_cw){
            if (isWetOpen){
                item.setTitle("关闭加湿");
                isWetOpen=false;
            }else{
                item.setTitle("打开加湿");
                isWetOpen=true;
            }
//            tcpClint.controllCW("#CP");
        }else if(id == R.id.action_cp){
            if (isPaiOpen){
                item.setTitle("关闭排风");
                isPaiOpen=false;
            }else{
                item.setTitle("打开排风");
                isPaiOpen=true;
            }
//            tcpClint.controllCP("#CW");
        }

        return super.onOptionsItemSelected(item);
    }
    private boolean isWetOpen=false;
    private boolean isPaiOpen=false;
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_camera) {
            startActivity(new Intent(this,MonitorActivity.class));
        } else if (id == R.id.nav_gallery) {
            startActivity(new Intent(this,RecordActivity.class));
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_connect:
                drawer.closeDrawer(GravityCompat.START);
                connectServer();
                break;
            case R.id.rl_temperature:
                startActivity(new Intent(this,CurveActivity.class));
                break;
        }
    }

    private void connectServer() {
//            tcpClint.connect(etIP.getText().toString(),Integer.valueOf(etPort.getText().toString()));
        TcpManager.getInstance().getJson(etIP.getText().toString(), Integer.valueOf(etPort.getText().toString()), "#1"+currentId, new TcpManager.GetDataListener() {
            @Override
            public void showData(final String result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        netResultShow(result);
                    }
                });

            }
            @Override
            public void success() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Main2Activity.this, "数据更新成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void timeOut() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (srRefresh.isRefreshing()){
                            srRefresh.setRefreshing(false);
                        }
                        Toast.makeText(Main2Activity.this, "请求超时", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void netResultShow(String result) {
        if (srRefresh.isRefreshing()){
            srRefresh.setRefreshing(false);
        }
        Date date=new Date(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MM月dd日HH时mm分");
        tvTime.setText("更新于："+simpleDateFormat.format(date));
        tvId.setText("当前设备号："+currentId);
        homeInfo = parseJson(result);
        if(homeInfo ==null){
            return;
        }
        tvTemp.setText(homeInfo.getTemperature()+"");
        tvAir.setText(homeInfo.getAir()+"");
        tvGas.setText(homeInfo.getGas()+"");
        tvIllumination.setText(homeInfo.getIllumination()+"");
        tvWet.setText(homeInfo.getWet()+"");
    }
}
