package com.cataldo.chris.homeautomationcontroller;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Chris on 3/30/2018.
 */

public class ApiConnection {
    private Context context;
    ApiConnection(Context context){
        this.context=context;
    }

    JSONObject retrieveData(String commandString) {
        JSONObject data = new JSONObject();
        GlobalVars mApp = ((GlobalVars) context.getApplicationContext());
        String dataUrl = "http://" + mApp.getDomain() + mApp.getHomeControlUrl() + "?AUTHCODE=" + mApp.getAuthCode() + commandString;
        try {
            String jsonString = Ion.with(context).load(dataUrl).asString().get();
            if(jsonString != null) {
                Log.v("DATA", jsonString);
                try {
                    JSONObject jsonData = new JSONObject(jsonString);
                    String statusError = jsonData.getString("Error");
                    if (statusError.length() > 0) {
                        showErrorAlert(statusError);
                    } else {
                        JSONObject resultObject = jsonData.getJSONObject("Data");
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

    void showErrorAlert(String errorMessage) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
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
