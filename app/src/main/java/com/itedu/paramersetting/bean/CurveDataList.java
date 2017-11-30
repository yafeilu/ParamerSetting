package com.itedu.paramersetting.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by luyafei on 2017/10/26.
 */
public class CurveDataList {


    private int airQuality186; // FIXME check this code
    private String humidity;
    private String temperature;
    private String time;
    private String toxic_gas;
    private String wet;//目前没有湿度字段，以后添加使用wet

    public String getWet() {
        return wet;
    }

    public void setWet(String wet) {
        this.wet = wet;
    }

    public int getAirQuality() {
        return airQuality186;
    }

    public void setAirQuality(int airQuality186) {
        this.airQuality186 = airQuality186;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getToxic_gas() {
        return toxic_gas;
    }

    public void setToxic_gas(String toxic_gas) {
        this.toxic_gas = toxic_gas;
    }
}
