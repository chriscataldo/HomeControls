package com.cataldo.chris.homeautomationcontroller;

import android.app.Application;

/**
 * Created by Chris on 4/26/2016.
 */
public class GlobalVars extends Application {
    private static String DOMAIN = "";
    private final static String HOME_CONTROL_URL = "/homecontrols/hc.php";
    private static String AUTHCODE = "";
    private final static String INIT_COMMAND = "command=getstatus&device=all";

    private final static int REFRESH_TIME_LIMIT = 600; // time in seconds

    private Long initialStartTime;
    private String connectionError = null;

    public String getDomain() {
        return DOMAIN;
    }
    public void setDomain(String str) {
        DOMAIN = str;
    }
    public String getHomeControlUrl() {
        return HOME_CONTROL_URL;
    }
    public String getAuthCode() {
        return AUTHCODE;
    }
    public void setAuthcode(String str) {
        AUTHCODE = str;
    }
    public String getInitCommand() {
        return INIT_COMMAND;
    }

    public int getRefreshTimeLimit() {
        return REFRESH_TIME_LIMIT;
    }

    public Long getInitialStartTime() {
        return initialStartTime;
    }
    public void setInitialStartTime(Long time) {
        initialStartTime = time;
    }

    public String getConnectionError() {
        return connectionError;
    }
    public void setConnectionError(String str) {
        connectionError = str;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Long tsLong = System.currentTimeMillis();
        this.setInitialStartTime(tsLong);
    }
}
