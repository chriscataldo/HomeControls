package com.cataldo.chris.homeautomationcontroller;


import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;



public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private Boolean isActivityRestarting = false;
    private Boolean reloadDataOnResume = false;
    private GlobalVars mApp;
   // private SharedPreferences  mPrefs = getPreferences(MODE_PRIVATE);
    private Long intitialStartTime;
    private Integer refreshTimeLimit;
    private String jsonDataString;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("DATA", "in onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        try {
//            mApp = ((GlobalVars) getApplicationContext());
//        } catch(Exception e) {
//            Toast toast = Toast.makeText(this, "onCreate mApp error: " + e.toString(), Toast.LENGTH_LONG);
//            toast.show();
//            restartApp();
//        }

        try {
            if(savedInstanceState != null) {
                Log.v("DATA", "in savedInstanceState != null");
                intitialStartTime = savedInstanceState.getLong("intitialStartTime");
                refreshTimeLimit = savedInstanceState.getInt("refreshTimeLimit");
                jsonDataString = savedInstanceState.getString("jsonDataString");
                toast = Toast.makeText(this, "in savedInstanceState != null", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Log.v("DATA", "in savedInstanceState = null");
                mApp = ((GlobalVars) getApplicationContext());
                intitialStartTime = mApp.getInitialStartTime();
                refreshTimeLimit = mApp.getRefreshTimeLimit();
                Log.v("DATA", "in savedInstanceState = null - intitialStartTime: " + intitialStartTime);

                Intent i = getIntent();
                jsonDataString = i.getStringExtra("jsonDataString");
            }
        } catch(Exception e) {
            Toast toast = Toast.makeText(this, "onCreate mApp error: " + e.toString(), Toast.LENGTH_LONG);
            toast.show();
            restartApp();
        }

        if(jsonDataString != null) {
            Log.v("DATA", "in jsonDataString != null");
            ArrayList<Device> devices = new ArrayList<Device>();
            try {
                JSONObject jsonData = new JSONObject(jsonDataString);
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
            try {
                // Create the adapter to convert the array to views
                DevicesAdapter adapter = new DevicesAdapter(this, devices);
                // Attach the adapter to a ListView
                ListView listView = (ListView) findViewById(R.id.mainListView);
                listView.setAdapter(adapter);
            } catch (Exception e) {
                Toast toast = Toast.makeText(this, "adapter error: " + e, Toast.LENGTH_SHORT);
                toast.show();
                restartApp();
            }

        } else {
            Toast toast = Toast.makeText(this, "JSON error", Toast.LENGTH_SHORT);
            toast.show();
            String connectionError = mApp.getConnectionError();
            showErrorAlert(connectionError);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.v("DATA", "in onSaveInstanceState");
        savedInstanceState.putLong("intitialStartTime", mApp.getInitialStartTime());
        savedInstanceState.putInt("refreshTimeLimit", mApp.getRefreshTimeLimit());
        savedInstanceState.putString("jsonDataString", jsonDataString);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.v("DATA", "in onResume");
        super.onResume();
        //toast = Toast.makeText(this, "App is resuming", Toast.LENGTH_SHORT);
        //toast.show();
        Long timeNow = System.currentTimeMillis();

        try {
            int elapsedTime = (int) (timeNow - intitialStartTime)/1000;
//        String message = elapsedTime + "";
//        toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
//        toast.show();
            if (elapsedTime > refreshTimeLimit) {
                Log.v("DATA", "in onResume try");
                restartApp();
            }
        } catch(Exception e) {
            Log.v("DATA", "in onResume catch");
            toast = Toast.makeText(this, "onResume Error: " + e.toString(), Toast.LENGTH_SHORT);
            toast.show();
            restartApp();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.optionmenu, menu);
        return true;
    }

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
            case R.id.edit_settings:
                editSettings();
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

    public void editSettings() {
        Intent intent = new Intent(this, EditSettings.class);
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

    private void restartApp() {
        Log.v("DATA", "in restartApp");
        Toast toast = Toast.makeText(this, "in restartApp", Toast.LENGTH_SHORT);
        toast.show();

        Intent i = getBaseContext().getPackageManager().
                getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);


//        Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
//        int mPendingIntentId = 9999;
//        PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//        AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
//        System.exit(0);
    }
}


