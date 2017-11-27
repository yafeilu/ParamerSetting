package com.itedu.paramersetting.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by luyafei on 2017/10/26.
 */
public class CurveDataList {


    private int airQuality186; // FIXME check this code
    private int humidity;
    private int temperature;
    private String time;
    private int toxic_gas;

    public int getAirQuality() {
        return airQuality186;
    }

    public void setAirQuality(int airQuality186) {
        this.airQuality186 = airQuality186;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getToxic_gas() {
        return toxic_gas;
    }

    public void setToxic_gas(int toxic_gas) {
        this.toxic_gas = toxic_gas;
    }
}
