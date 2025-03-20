package com.cataldo.chris.homeautomationcontroller;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Chris on 3/30/2018.
 */

public class ApiConnection {
    private final Context context;
    ApiConnection(Context context){
        this.context=context;
    }

    JSONObject retrieveData(String commandString) {
        JSONObject data = new JSONObject();
        GlobalVars mApp = ((GlobalVars) context.getApplicationContext());
        String dataUrl = "http://" + mApp.getDomain() + mApp.getHomeControlUrl() + "?AUTHCODE=" + mApp.getAuthCode() + commandString;
        try {
            Log.v("DBG", "in try");
            String jsonString = fetchDataFromUrl(dataUrl);
            Log.v("DBG", "jsonString: " + jsonString);
            Log.v("DBG", jsonString);
            try {
                JSONObject jsonData = new JSONObject(jsonString);
                String statusError = jsonData.getString("Error");
                if (!statusError.isEmpty()) {
                    showErrorAlert(statusError);
                } else {
                    return jsonData.getJSONObject("Data");
                }
            } catch (JSONException e) {
                Log.v("DBG", "error: " + e);
                showErrorAlert("Invalid Json Response");
            }
        } catch(Exception e) {
            Log.v("DBG", "error: " + e);
            showErrorAlert("Connection Error");
        }

        return data;
    }

    private static String fetchDataFromUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);
            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    void showErrorAlert(String errorMessage) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Connection Error");
        alertDialog.setMessage(errorMessage);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }
}