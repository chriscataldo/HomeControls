package com.cataldo.chris.homeautomationcontroller;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Created by Chris on 10/17/2016.
 */
public class SetAwayStatus extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_away_status);
        ToggleButton switchSetting = (ToggleButton) findViewById(R.id.away_status);

        switchSetting.setText("Home");
        switchSetting.setTextOff("Home");
        switchSetting.setTextOn("Away");

        Integer currentAwayStatus = getAwayStatus();

        // set the switch to the correct position
        if(currentAwayStatus == 1) {
            switchSetting.setChecked(true);
        } else {
            switchSetting.setChecked(false);
        }
        /** Getting the toggle button corresponding to the clicked item */
        final ToggleButton button = switchSetting;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer newState = button.isChecked() ? 1 : 0;
                changeAwayStatus(newState);
            }
        });
    }

    private void changeAwayStatus(Integer awayStatus) {
        String commandString = "&command=setawaystatus&awaystatus=" + awayStatus;
        HTTPConnection connection = new HTTPConnection();
        GlobalVars mApp = ((GlobalVars)getApplicationContext());
        String dataUrl = "http://" + mApp.getDomain() + mApp.getHomeControlUrl() + "?" + mApp.getAuthCode() + commandString;
        String jsonString = connection.getConnection(dataUrl);
        if(jsonString != null) {
            try {
                JSONObject jsonData = new JSONObject(jsonString);
                String statusError = jsonData.getString("Error");
                if (statusError.length() > 0) {
                    showErrorAlert(statusError);
                } else {
                    Toast toast = Toast.makeText(this, "Away Status Changed", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }
            } catch (JSONException e) {
                showErrorAlert("Invalid Json Response");
            }
        } else {
            String connectionError = mApp.getConnectionError();
            showErrorAlert(connectionError);
        }
    }


    private Integer getAwayStatus() {
        Integer currentawaystatus = 0;
        String commandString = "&command=getawaystatus";
        HTTPConnection connection = new HTTPConnection();
        GlobalVars mApp = ((GlobalVars)getApplicationContext());
        String dataUrl = "http://" + mApp.getDomain() + mApp.getHomeControlUrl() + "?" + mApp.getAuthCode() + commandString;
        String jsonString = connection.getConnection(dataUrl);
        if(jsonString != null) {
            try {
                JSONObject jsonData = new JSONObject(jsonString);
                String statusError = jsonData.getString("Error");
                if (statusError.length() > 0) {
                    showErrorAlert(statusError);
                } else {
                    JSONObject resultObject = jsonData.getJSONObject("Data");
                    return resultObject.getInt("awaycurrent");
                }
            } catch (JSONException e) {
                showErrorAlert("Invalid Json Response");
            }
        } else {
            String connectionError = mApp.getConnectionError();
            showErrorAlert(connectionError);
        }

        return currentawaystatus;
    }

    private void showErrorAlert(String errorMessage) {
        AlertDialog alertDialog = new AlertDialog.Builder(SetAwayStatus.this).create();
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


}
