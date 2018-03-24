package com.cataldo.chris.homeautomationcontroller;

/**
 * Created by Chris on 4/21/2016.
 */


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;


public class SplashScreen extends Activity {

    private String JSONData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /**
         * Showing splashscreen while making network calls to download necessary
         * data before launching the app Will use AsyncTask to make http call
         */
        new PrefetchData().execute();

    }

    /**
     * Async Task to make http call
     */
    private class PrefetchData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // before making http calls

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HTTPConnection connection = new HTTPConnection();
            GlobalVars mApp = ((GlobalVars)getApplicationContext());
            String dataUrl = "http://" + mApp.getDomain() + mApp.getHomeControlUrl() + "?" + mApp.getAuthCode() + "&" + mApp.getInitCommand();
            JSONData = connection.getUrlData(dataUrl);


            if(JSONData == null) {
                String errorString = connection.connectionError;
                errorString = errorString + " - dataUrl";
                mApp.setConnectionError(errorString);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // After completing http call
            // will close this activity and launch main activity
            Intent i = new Intent(SplashScreen.this, MainActivity.class);
            i.putExtra("JSONData", JSONData);
            startActivity(i);
            // close this activity
            finish();
        }
    }
}