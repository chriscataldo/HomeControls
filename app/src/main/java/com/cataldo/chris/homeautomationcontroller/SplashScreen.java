package com.cataldo.chris.homeautomationcontroller;

/*
 * Created by Chris on 4/21/2016.
 */


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;


public class SplashScreen extends Activity {

    ApiConnection connection;

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

            connection = new ApiConnection(this);
//            String dataUrl = "http://" + mApp.getDomain() + mApp.getHomeControlUrl() + "?AUTHCODE=" + mApp.getAuthCode() + "&" + mApp.getInitCommand();
            String commandString = "&" + mApp.getInitCommand();
            Log.v("DBG", "commandString: " + commandString);
            getInitialData(connection, commandString);



//            Ion.with(SplashScreen.this)
//            .load(dataUrl)
//            .asString()
//            .setCallback(new FutureCallback<String>() {
//                @Override
//                public void onCompleted(Exception e, String JSONData) {
//                    if (e == null) {
//                        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
//                        intent.putExtra("jsonDataString", JSONData);
//                        startActivity(intent);
//                        finish(); // close this activity
//                    } else {
//                        Toast.makeText(
//                            SplashScreen.this,
//                            "Connection Error - " + e,
//                        Toast.LENGTH_LONG).show();
//                        Intent intent = new Intent(SplashScreen.this, EditSettings.class);
//                        startActivity(intent);
//                        finish(); // close this activity
//                    }
//                }
//            });
        }
    }

    private void getInitialData(ApiConnection connection, String commandString) {
        new Thread(new Runnable() {
            JSONObject data;

            Exception error;
            @Override
            public void run() {
                try {
                    data = connection.retrieveData(commandString);
                    Log.v("DBG", "data: " + data);
                } catch (Exception e) {
                    error = e;
                }

                runOnUiThread(() -> {
                    if(error != null) {
                        Toast.makeText(SplashScreen.this,"Connection Error - " + error, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SplashScreen.this, EditSettings.class);
                        startActivity(intent);
                        finish(); // close this activity
                    } else {
                        if(data != null) {
                            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                            intent.putExtra("jsonDataString", data.toString());
                            startActivity(intent);
                            finish(); // close this activity
                        }
                    }
                });
            }
        }).start();
    }
}