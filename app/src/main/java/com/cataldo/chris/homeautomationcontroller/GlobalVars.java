package com.cataldo.chris.homeautomationcontroller;

import android.app.Application;

/**
 * Created by Chris on 4/26/2016.
 */
public class GlobalVars extends Application {
    private final static String DOMAIN = "cat66.ddns.net:81";
    private final static String HOME_CONTROL_URL = "/homecontrols/hc.php";
    private final static String AUTHCODE = "AUTHCODE=HC_V10";
    private final static String INIT_COMMAND = "command=getstatus&device=all";

    private final static int REFRESH_TIME_LIMIT = 600; // time in seconds

    private Long initialStartTime;
    private String connectionError = null;

    public String getDomain() {
        return DOMAIN;
    }
    public String getHomeControlUrl() {
        return HOME_CONTROL_URL;
    }
    public String getAuthCode() {
        return AUTHCODE;
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
