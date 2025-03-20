package com.cataldo.chris.homeautomationcontroller;

/**
 * Created by Chris on 4/30/2016.
 */
public class SecurityAlert {
    public String zone;
    public String action;
    public String date;
    public String time;

    public void setZone(String zone) {
        this.zone = zone;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getZone() {
        return this.zone;
    }

    public String getAction() {
        return this.action;
    }

    public String getDate() {
        return this.date;
    }

    public String getTime() {
        return this.time;
    }

}