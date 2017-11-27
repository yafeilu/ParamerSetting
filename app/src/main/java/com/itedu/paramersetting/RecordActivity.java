package com.itedu.paramersetting;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class RecordActivity extends BasedActivity {

    @Override
    protected void failed() {

    }

    @Override
    protected void showData(String data) {
        Log.d("yafei", "showData: "+data);
    }

    @Override
    protected void post() {
        tcpClint.getRecord();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_record;
    }

    @Override
    protected void initView() {

    }
}
