package com.cataldo.chris.homeautomationcontroller;

/**
 * Created by Chris on 4/18/2016.
 */
public class Device {
    public String device;
    public String deviceName;
    public String deviceType;
    public String deviceStatus;

    public void setDevice(String device) {
        this.device = device;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public String getDevice() {
        return this.device;
    }

    public String getDeviceName() {
        return this.deviceName;
    }

    public String getDeviceType() {
        return this.deviceType;
    }

    public String getDeviceStatus() {
        return this.deviceStatus;
    }
}