package com.cataldo.chris.homeautomationcontroller;

import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Chris on 10/17/2016.
 */
public class SetAwayStatus extends AppCompatActivity {
    ApiConnection connection;
    int currentAwayStatus = 0;
    ToggleButton switchSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_away_status);
        switchSetting = findViewById(R.id.away_status);

        switchSetting.setText("Home");
        switchSetting.setTextOff("Home");
        switchSetting.setTextOn("Away");

        connection = new ApiConnection(this);

        getAwayStatus(connection);
    }

    private void setupView() {
        // set the switch to the correct position
        switchSetting.setChecked(currentAwayStatus == 1);

        /* Getting the toggle button corresponding to the clicked item */
        final ToggleButton button = switchSetting;

        button.setOnClickListener(v -> {
            Integer newState = button.isChecked() ? 1 : 0;
            setAwayStatus(connection, newState);
        });

        switchSetting.setVisibility(VISIBLE);
    }

//    private void changeAwayStatus(Integer awayStatus) {
//        String commandString = "&command=setawaystatus&awaystatus=" + awayStatus;
//        ApiConnection connection = new ApiConnection(this);
//        JSONObject data = connection.retrieveData(commandString);
//        try {
//            String result = data.getString("status");
//            String message;
//            if(result.equals("success")) {
//                message = "Away Status Changed.";
//            } else {
//                message = "There was a problem.";
//            }
//
//            Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//            toast.show();
//        } catch (JSONException e) {
//            connection.showErrorAlert("Invalid Json Response");
//        }
//    }

//    private Integer getAwayStatus() {
//        int currentawaystatus = 0;
//        String commandString = "&command=getawaystatus";
//        ApiConnection connection = new ApiConnection(this);
//        JSONObject data = connection.retrieveData(commandString);
//        try {
//            currentawaystatus = data.getInt("awaycurrent");
//        } catch (JSONException e) {
//            connection.showErrorAlert("Invalid Json Response");
//        }
//        return currentawaystatus;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private void getAwayStatus(ApiConnection connection) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String commandString = "&command=getawaystatus";
                JSONObject data = connection.retrieveData(commandString);
                try {
                    currentAwayStatus = data.getInt("awaycurrent");
                    Log.v("DBG", "currentAwayStatus: " + currentAwayStatus);
                } catch (JSONException e) {
                    connection.showErrorAlert("Invalid Json Response");
                }

                runOnUiThread(() -> {
                    Log.v("DBG", "in onPostExecute");
                    setupView();
                });
            }
        }).start();
    }

    private void setAwayStatus(ApiConnection connection, int awayStatus) {
        new Thread(new Runnable() {
            String message;
            @Override
            public void run() {
                String commandString = "&command=setawaystatus&awaystatus=" + awayStatus;
                JSONObject data = connection.retrieveData(commandString);

                try {
                    String result = data.getString("status");
                    if(result.equals("success")) {
                        message = "Away Status Changed.";
                    } else {
                        message = "There was a problem.";
                    }
                } catch (JSONException e) {
                    connection.showErrorAlert("Invalid Json Response");
                }

                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.v("DBG", "in onPostExecute");
                        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.show();
                    }
                });
            }
        }).start();
    }

}