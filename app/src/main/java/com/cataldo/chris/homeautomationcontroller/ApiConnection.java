package com.cataldo.chris.homeautomationcontroller;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

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

   public JSONObject retrieveData(String commandString) {
        JSONObject data = new JSONObject();
        GlobalVars mApp = ((GlobalVars) context.getApplicationContext());
        String dataUrl = "http://" + mApp.getDomain() + mApp.getHomeControlUrl() + "?AUTHCODE=" + mApp.getAuthCode() + commandString;
        try {
            Log.v("DBG", "in try");
            String jsonString = fetchDataFromUrl(dataUrl);
            Log.v("DBG", "jsonString: " + jsonString);

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
        } catch(Exception e) {
            Log.v("DBG", "error: " + e);
            showErrorAlert("Connection Error");
        }

        return null;
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
//        ContextCompat.getMainExecutor(context).execute(() -> {
//            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
//            alertDialog.setTitle("Connection Error");
//            alertDialog.setMessage(errorMessage);
//            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                    (dialog, which) -> dialog.dismiss());
//            alertDialog.show();
//        });

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}