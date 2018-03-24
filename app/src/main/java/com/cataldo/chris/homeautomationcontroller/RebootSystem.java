package com.cataldo.chris.homeautomationcontroller;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
                    Toast toast = Toast.makeText(this, "System Rebooted", Toast.LENGTH_SHORT);
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
        AlertDialog alertDialog = new AlertDialog.Builder(RebootSystem.this).create();
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


