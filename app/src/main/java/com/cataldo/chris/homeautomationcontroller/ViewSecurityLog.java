package com.cataldo.chris.homeautomationcontroller;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;


/**
 * Created by Chris on 4/24/2016.
 */
public class ViewSecurityLog extends AppCompatActivity {
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_security_log);

        JSONObject JSONData = getSecurityLogData();
        ArrayList<SecurityAlert> securityAlerts = new ArrayList<SecurityAlert>();
        try {
            JSONArray resultArray = JSONData.getJSONArray("Data");
            for (int i = 0; i < resultArray.length(); i++) {
                SecurityAlert alert = new SecurityAlert();
                try {
                    JSONObject alertData = resultArray.getJSONObject(i);
                    String alertZone = alertData.getString("zone");
                    alert.setZone(alertZone);
                    String alertAction = alertData.getString("action");
                    alert.setAction(alertAction);
                    String alertDate = alertData.getString("date");
                    alert.setDate(alertDate);
                    String alertTime = alertData.getString("time");
                    alert.setTime(alertTime);
                } catch (JSONException e) {
                    showErrorAlert("Invalid Json Response");
                }
                securityAlerts.add(alert);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // sort the data
        Collections.sort(securityAlerts, new SecurityAlertComparator());

        // testing
        //        for (SecurityAlert data : alerts) {
        //            Log.v("Data","zone:" + data.getZone());
        //            Log.v("Data","action:" + data.getAction());
        //            Log.v("Data","date:" + data.getDate());
        //            Log.v("Data","time:" + data.getTime());
        //        }

        // Create the adapter to convert the array to views
        SecurityAlertsAdapter adapter = new SecurityAlertsAdapter(this, securityAlerts);
        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.alertsListView);
        listView.setAdapter(adapter);
    }

    private JSONObject getSecurityLogData() {
        String commandString = "&command=getsecurityalerts";
        GlobalVars mApp = ((GlobalVars)getApplicationContext());
        String dataUrl = "http://" + mApp.getDomain() + mApp.getHomeControlUrl() + "?AUTHCODE=" + mApp.getAuthCode() + commandString;
        try {
            String jsonString = Ion.with(ViewSecurityLog.this).load(dataUrl).asString().get();
            if(jsonString != null) {
                try {
                    JSONObject jsonData = new JSONObject(jsonString);
                    String statusError = jsonData.getString("Error");
                    if (statusError.length() > 0) {
                        showErrorAlert(statusError);
                    } else {
                        return jsonData;
                    }
                } catch (JSONException e) {
                    showErrorAlert("Invalid Json Response");
                }
            } else {
                showErrorAlert("No Data Returned");
            }
        } catch(Exception e) {
            showErrorAlert("Connection Error");
        }

        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    private void showErrorAlert(String errorMessage) {
        AlertDialog alertDialog = new AlertDialog.Builder(ViewSecurityLog.this).create();
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



