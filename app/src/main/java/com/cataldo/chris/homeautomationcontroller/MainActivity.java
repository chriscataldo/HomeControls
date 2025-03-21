package com.cataldo.chris.homeautomationcontroller;


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
import java.util.Iterator;


public class MainActivity extends AppCompatActivity {
    private GlobalVars mApp;
   // private SharedPreferences  mPrefs = getPreferences(MODE_PRIVATE);
    private Long intitialStartTime;
    private Integer refreshTimeLimit;
    private String jsonDataString;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("DBG", "in onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            if(savedInstanceState != null) {
                Log.v("DBG", "in savedInstanceState != null");
                intitialStartTime = savedInstanceState.getLong("intitialStartTime");
                refreshTimeLimit = savedInstanceState.getInt("refreshTimeLimit");
                jsonDataString = savedInstanceState.getString("jsonDataString");
                toast = Toast.makeText(this, "in savedInstanceState != null", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Log.v("DBG", "in savedInstanceState = null");
                mApp = ((GlobalVars) getApplicationContext());
                intitialStartTime = mApp.getInitialStartTime();
                refreshTimeLimit = mApp.getRefreshTimeLimit();
                Log.v("DBG", "in savedInstanceState = null - intitialStartTime: " + intitialStartTime);

                Intent intent = getIntent();
                jsonDataString = intent.getStringExtra("jsonDataString");
            }
        } catch(Exception e) {
            Toast toast = Toast.makeText(this, "onCreate mApp error: " + e, Toast.LENGTH_LONG);
            toast.show();
            restartApp();
        }

        if(jsonDataString != null) {
            Log.v("DBG", "in jsonDataString != null");
            ArrayList<Device> devices = new ArrayList<>();
            try {
                JSONObject jsonData = new JSONObject(jsonDataString);
                Iterator<String> iter = jsonData.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    Device device = new Device();
                    device.setDevice(key);

                    try {
                        JSONObject deviceData = jsonData.getJSONObject(key);
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
            devices.sort(new DeviceComparator());

            try {
                // Create the adapter to convert the array to views
                DevicesAdapter adapter = new DevicesAdapter(this, devices);
                // Attach the adapter to a ListView
                ListView listView = findViewById(R.id.mainListView);
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
        Log.v("DBG", "in onSaveInstanceState");
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
        Log.v("DBG", "in onResume");
        super.onResume();
        Long timeNow = System.currentTimeMillis();

        try {
            int elapsedTime = (int) (timeNow - intitialStartTime)/1000;
            if (elapsedTime > refreshTimeLimit) {
                Log.v("DBG", "in onResume try");
                restartApp();
            }
        } catch(Exception e) {
            Log.v("DBG", "in onResume catch");
            toast = Toast.makeText(this, "onResume Error: " + e, Toast.LENGTH_SHORT);
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
            case R.id.set_away_status:
                setAwayStatus();
                return true;
            case R.id.edit_settings:
                editSettings();
                return true;
            case R.id.view_security_log:
                viewSecurityLog();
                return true;
            case R.id.about:
                showAbout();
                return true;
            case R.id.reboot_system:
                rebootSystem();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    private void showAbout() {
        Intent intent = new Intent(this, About.class);
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
                (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }

    private void restartApp() {
        Log.v("DBG", "in restartApp");
        Toast toast = Toast.makeText(this, "Re-loading Data", Toast.LENGTH_SHORT);
        toast.show();

        Intent intent = getBaseContext().getPackageManager().
                getLaunchIntentForPackage(getBaseContext().getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}