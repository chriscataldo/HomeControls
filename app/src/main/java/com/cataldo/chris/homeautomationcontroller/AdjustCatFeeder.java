package com.cataldo.chris.homeautomationcontroller;


import android.content.DialogInterface;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Chris on 4/24/2016.
 */
public class AdjustCatFeeder extends AppCompatActivity {
    private static class feederDuration {
        String feedermin;
        String feedermax;
        String feedercurrent;
    }
    SeekBar durationControl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adjust_cat_feeder);

        final TextView currentDurationValue = (TextView) findViewById(R.id.current_value);
        final Button setDurationButton = (Button) findViewById(R.id.set_new_duration);

        feederDuration feederStats = getCatFeederDuration();
        currentDurationValue.setText(feederStats.feedercurrent);

        durationControl = (SeekBar) findViewById(R.id.duration_bar);
        final int intMin = Integer.parseInt(feederStats.feedermin);
        int intMax = Integer.parseInt(feederStats.feedermax);
        int spread = intMax - intMin;
        durationControl.setMax(spread);
        durationControl.setProgress(Integer.parseInt(feederStats.feedercurrent) - intMin);

        durationControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){

                int newValue = intMin + progress;
                currentDurationValue.setText(String.valueOf(newValue));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }

            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        if (setDurationButton != null) {
            setDurationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setNewDuration((String) currentDurationValue.getText());
                }
            });
        }
    }

    private void setNewDuration(String duration) {
        String commandString = "&command=setfeederduration&duration=" + duration;
        JSONObject data = retrieveDataFromApi(commandString);
        Toast toast = Toast.makeText(this, "Duration Changed", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    private feederDuration getCatFeederDuration() {
        feederDuration feeder = null;
        feeder = new feederDuration();
        String commandString = "&command=getfeederduration";
        JSONObject data = retrieveDataFromApi(commandString);
        try {
            feeder.feedermin = data.getString("feedermin");
            feeder.feedermax = data.getString("feedermax");
            feeder.feedercurrent = data.getString("feedercurrent");
        } catch (JSONException e) {
            showErrorAlert("Invalid Json Response");
        }
        return feeder;
    }

    private void showErrorAlert(String errorMessage) {
        AlertDialog alertDialog = new AlertDialog.Builder(AdjustCatFeeder.this).create();
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

    private JSONObject retrieveDataFromApi(String commandString) {
        JSONObject data = new JSONObject();
        GlobalVars mApp = ((GlobalVars)getApplicationContext());
        String dataUrl = "http://" + mApp.getDomain() + mApp.getHomeControlUrl() + "?AUTHCODE=" + mApp.getAuthCode() + commandString;
        try {
            String jsonString = Ion.with(AdjustCatFeeder.this).load(dataUrl).asString().get();
            if(jsonString != null) {
                try {
                    JSONObject jsonData = new JSONObject(jsonString);
                    String statusError = jsonData.getString("Error");
                    if (statusError.length() > 0) {
                        showErrorAlert(statusError);
                    } else {
                        JSONObject resultObject = jsonData.getJSONObject("Data");
                        Log.v("DATA", resultObject.toString());
                        return jsonData.getJSONObject("Data");
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

        return data;
    }

}
