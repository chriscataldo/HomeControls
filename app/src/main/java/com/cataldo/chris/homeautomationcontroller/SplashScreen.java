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

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;


public class SplashScreen extends Activity {

    private String JSONData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /**
         * Showing splash screen while making network calls to download necessary
         * data before launching the app Will use AsyncTask to make http call
         */
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String domain = preferences.getString("domain", null);
        String authcode = preferences.getString("authcode", null);

        if(TextUtils.isEmpty(domain) || TextUtils.isEmpty(authcode)) {
            Intent intent = new Intent(this, EditSettings.class);
            startActivity(intent);
        } else {
            GlobalVars mApp = ((GlobalVars) getApplicationContext());
            mApp.setDomain(domain);
            mApp.setAuthcode(authcode);

            String dataUrl = "http://" + mApp.getDomain() + mApp.getHomeControlUrl() + "?AUTHCODE=" + mApp.getAuthCode() + "&" + mApp.getInitCommand();

            Ion.with(SplashScreen.this)
            .load(dataUrl)
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject JSONData) {
                    if (e == null) {
                        Intent i = new Intent(SplashScreen.this, MainActivity.class);
                        i.putExtra("JSONData", JSONData.toString());
                        startActivity(i);
                        // close this activity
                        finish();
                    } else {
                        Toast.makeText(
                            SplashScreen.this,
                            "Connection Error.",
                            Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}