package com.itedu.paramersetting.bean;

/**
 * Created by luyafei on 2017/10/26.
 *
 */
public class HomeInfo {
    /**
     * air : 34
     * gas : 34
     * illumination : 34
     * temperature : 23
     * wet : 45
     */
    /**
     * {
     "state":1,
     "air": 34,
     "gas": 34,
     "illumination": 34,
     "temperature": 23,
     "wet": 45
     }
     */

    private int air;
    private int gas;
    private int illumination;
    private int temperature;
    private int wet;
    private int state;
    private int wetctl;
    private int gasctl;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getWetctl() {
        return wetctl;
    }

    public void setWetctl(int wetctl) {
        this.wetctl = wetctl;
    }

    public int getGasctl() {
        return gasctl;
    }

    public void setGasctl(int gasctl) {
        this.gasctl = gasctl;
    }

    public int getAir() {
        return air;
    }

    public void setAir(int air) {
        this.air = air;
    }

    public int getGas() {
        return gas;
    }

    public void setGas(int gas) {
        this.gas = gas;
    }

    public int getIllumination() {
        return illumination;
    }

    public void setIllumination(int illumination) {
        this.illumination = illumination;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getWet() {
        return wet;
    }

    public void setWet(int wet) {
        this.wet = wet;
    }
}
