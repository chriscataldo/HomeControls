package com.cataldo.chris.homeautomationcontroller;

/*
 * Created by Chris on 4/21/2016.
 */


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;


public class SplashScreen extends Activity {

    private String JSONData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

/*        Showing splash screen while making network calls to download necessary
          data before launching the app*/

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String domain = preferences.getString("domain", null);
        String authcode = preferences.getString("authcode", null);

        if(TextUtils.isEmpty(domain) || TextUtils.isEmpty(authcode)) {
            Intent intent = new Intent(this, EditSettings.class);
            intent.putExtra("initialRun","true");
            startActivity(intent);
        } else {
            GlobalVars mApp = ((GlobalVars) getApplicationContext());
            mApp.setDomain(domain);
            mApp.setAuthcode(authcode);
            mApp.setInitialStartTime();

            String dataUrl = "http://" + mApp.getDomain() + mApp.getHomeControlUrl() + "?AUTHCODE=" + mApp.getAuthCode() + "&" + mApp.getInitCommand();

            Ion.with(SplashScreen.this)
            .load(dataUrl)
            .asString()
            .setCallback(new FutureCallback<String>() {
                @Override
                public void onCompleted(Exception e, String JSONData) {
                    if (e == null) {
                        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                        intent.putExtra("jsonDataString", JSONData);
                        startActivity(intent);
                        finish(); // close this activity
                    } else {
                        Toast.makeText(
                            SplashScreen.this,
                            "Connection Error - " + e.toString(),
                        Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SplashScreen.this, EditSettings.class);
                        startActivity(intent);
                        finish(); // close this activity
                    }
                }
            });
        }
    }
}