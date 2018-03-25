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
 * Created by Chris on 4/27/2016.
 */
public class RestartAutomation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restart_automation);

        final Button restartButton = (Button) findViewById(R.id.restart_button);

        if (restartButton != null) {
            restartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    restartAutomationScripts();
                }
            });
        }
    }

    private void restartAutomationScripts() {
        String commandString = "&command=restartx10";
        HTTPConnection connection = new HTTPConnection();
        GlobalVars mApp = ((GlobalVars)getApplicationContext());
        String dataUrl = "http://" + mApp.getDomain() + mApp.getHomeControlUrl() + "?AUTHCODE=" + mApp.getAuthCode() + commandString;
        String jsonString = connection.getConnection(dataUrl);
        if(jsonString != null) {
            try {
                JSONObject jsonData = new JSONObject(jsonString);
                String statusError = jsonData.getString("Error");
                if (statusError.length() > 0) {
                    showErrorAlert(statusError);
                } else {
                    Toast toast = Toast.makeText(this, "Automation Restarted", Toast.LENGTH_SHORT);
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
        AlertDialog alertDialog = new AlertDialog.Builder(RestartAutomation.this).create();
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
