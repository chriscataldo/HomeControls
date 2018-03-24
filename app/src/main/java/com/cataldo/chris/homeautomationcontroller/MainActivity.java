package com.cataldo.chris.homeautomationcontroller;


import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;



public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private Boolean isActivityRestarting = false;
    private Boolean reloadDataOnResume = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = getIntent();
        String JSONData = i.getStringExtra("JSONData");

        if(JSONData != null) {

            ArrayList<Device> devices = new ArrayList<Device>();

            try {
                JSONObject jsonData = new JSONObject(JSONData);
                JSONObject resultObject = jsonData.getJSONObject("Data");

                Iterator<String> iter = resultObject.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    Device device = new Device();
                    device.setDevice(key);

                    try {
                        JSONObject deviceData = resultObject.getJSONObject(key);
                        String deviceDataName = deviceData.getString("name");
                        device.setDeviceName(deviceDataName);
                        String deviceDataType = deviceData.getString("type");
                        device.setDeviceType(deviceDataType);
                        String deviceDataStatus = deviceData.getString("status");
                        device.setDeviceStatus(deviceDataStatus);
                    } catch (JSONException e) {
                        showErrorAlert("Invalid Json Response");
                    }
                    devices.add(device);
                }

            } catch(Exception e) {
                showErrorAlert("Invalid Json Response");
            }

            // sort the devices
            Collections.sort(devices, new DeviceComparator());

            // testing
    //        for (Device data : devices) {
    //            Log.v("Data","device:" + data.getDevice());
    //            Log.v("Data","name:" + data.getDeviceName());
    //             Log.v("Data","type:" + data.getDeviceType());
    //            Log.v("Data","status:" + data.getDeviceStatus());
    //        }

            // Create the adapter to convert the array to views
            DevicesAdapter adapter = new DevicesAdapter(this, devices);
            // Attach the adapter to a ListView
            ListView listView = (ListView) findViewById(R.id.mainListView);
            listView.setAdapter(adapter);

        } else {
            GlobalVars mApp = ((GlobalVars)getApplicationContext());
            String connectionError = mApp.getConnectionError();
            showErrorAlert(connectionError);
        }

    }


    public void onResume() {
        super.onResume();
        Long timeNow = System.currentTimeMillis();
        GlobalVars mApp = ((GlobalVars)getApplicationContext());
        Long initialStartTime = mApp.getInitialStartTime();
        Long elapsedTime = (timeNow - initialStartTime)/1000;
        if (elapsedTime > mApp.getRefreshTimeLimit()) {
            mApp.setInitialStartTime(timeNow);
            Intent intent = new Intent(this, SplashScreen.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.optionmenu, menu);
        return true;
    }
//
//    public boolean ShowMenuOption(MenuItem item) {
//        //Menu item pressed
//        Toast.makeText(this,"Settings menu was pressed.", Toast.LENGTH_SHORT).show();
//        return true; //Indicated menu press was handled
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.adjust_cat_feeder:
                adjustCatFeeder();
                return true;
            case R.id.set_away_status:
                setAwayStatus();
                return true;
            case R.id.view_security_log:
                viewSecurityLog();
                return true;
            case R.id.restart_x10:
                restartX10();
                return true;
            case R.id.reboot_system:
                rebootSystem();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void adjustCatFeeder() {
        Intent intent = new Intent(this, AdjustCatFeeder.class);
        startActivity(intent);
    }

    public void setAwayStatus() {
        Intent intent = new Intent(this, SetAwayStatus.class);
        startActivity(intent);
    }

    public void viewSecurityLog() {
        Intent intent = new Intent(this, ViewSecurityLog.class);
        startActivity(intent);
    }

    public void restartX10() {
        Intent intent = new Intent(this, RestartAutomation.class);
        startActivity(intent);
    }

    public void rebootSystem() {
        Intent intent = new Intent(this, RebootSystem.class);
        startActivity(intent);
    }

    private void showErrorAlert(String errorMessage) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Connection Error");
        alertDialog.setMessage(errorMessage);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }


}


