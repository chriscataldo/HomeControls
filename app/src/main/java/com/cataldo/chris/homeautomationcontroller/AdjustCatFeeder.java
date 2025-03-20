package com.cataldo.chris.homeautomationcontroller;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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

        final TextView currentDurationValue = findViewById(R.id.current_value);
        final Button setDurationButton = findViewById(R.id.set_new_duration);

        feederDuration feederStats = getCatFeederDuration();
        currentDurationValue.setText(feederStats.feedercurrent);

        durationControl = findViewById(R.id.duration_bar);
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
            setDurationButton.setOnClickListener(v -> setNewDuration((String) currentDurationValue.getText()));
        }
    }

    private void setNewDuration(String duration) {
        String commandString = "&command=setfeederduration&duration=" + duration;
        ApiConnection connection = new ApiConnection(this);
        JSONObject data = connection.retrieveData(commandString);
        try {
            String result = data.getString("status");
            String message;
            if(result.equals("success")) {
                message = "Duration Changed.";
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

    private feederDuration getCatFeederDuration() {
        feederDuration feeder;
        feeder = new feederDuration();
        String commandString = "&command=getfeederduration";
        ApiConnection connection = new ApiConnection(this);
        JSONObject data = connection.retrieveData(commandString);
        try {
            feeder.feedermin = data.getString("feedermin");
            feeder.feedermax = data.getString("feedermax");
            feeder.feedercurrent = data.getString("feedercurrent");
        } catch (JSONException e) {
            connection.showErrorAlert("Invalid Json Response");
        }
        return feeder;
    }

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

}