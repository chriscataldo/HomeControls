package com.cataldo.chris.homeautomationcontroller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;


import org.json.JSONException;
import org.json.JSONObject;


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
        ApiConnection connection = new ApiConnection(this);
        JSONObject data = connection.retrieveData(commandString);
        try {
            String result = data.getString("status");
            String message = "";
            if(result.equals("success")) {
                message = "Away Status Changed.";
            } else {
                message = "There was a problem.";
            }

            Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
        } catch (JSONException e) {
            connection.showErrorAlert("Invalid Json Response");
        }
    }

    private Integer getAwayStatus() {
        Integer currentawaystatus = 0;
        String commandString = "&command=getawaystatus";
        ApiConnection connection = new ApiConnection(this);
        JSONObject data = connection.retrieveData(commandString);
        try {
            currentawaystatus = data.getInt("awaycurrent");
        } catch (JSONException e) {
            connection.showErrorAlert("Invalid Json Response");
        }
        return currentawaystatus;
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
