package com.cataldo.chris.homeautomationcontroller;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Chris on 5/9/2016.
 */
public class RebootSystem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reboot_system);

        final Button rebootButton = (Button) findViewById(R.id.reboot_button);

        if (rebootButton != null) {
            rebootButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rebootSystem();
                }
            });
        }
    }

    private void rebootSystem() {
        String commandString = "&command=rebootsystem";
        ApiConnection connection = new ApiConnection(this);
        JSONObject data = connection.retrieveData(commandString);
        try {
            String result = data.getString("status");
            String message = "";
            if(result.equals("success")) {
                message = "System Rebooted.";
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


