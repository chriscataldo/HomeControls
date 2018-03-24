package com.cataldo.chris.homeautomationcontroller;


import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Created by Chris on 4/23/2016.
 */
public class HTTPConnection {

    public String connectionError = null;
    public String getConnection (String url) {
        String result;
        try {
            result = new DownloadTextTask().execute(url).get();
        } catch (InterruptedException e) {
            result = "No data available";
        } catch (ExecutionException e) {
            result = "No data available";
        }
        return result;
    }

    private class DownloadTextTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                InputStream inputStream = downloadUrl(urls[0]);
                String returnString = null;
                if(inputStream != null) {
                    returnString = InputStreamToString(inputStream);
                    inputStream.close();
                }
                return returnString;
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
    }

    public String getUrlData(String url) {
        try {
            InputStream inputStream = downloadUrl(url);
            String returnString = null;
            if(inputStream != null) {
                returnString = InputStreamToString(inputStream);
                inputStream.close();
            }
            return returnString;
        } catch (IOException e) {
            return "Unable to retrieve web page. URL may be invalid.";
        }
    }

    private InputStream downloadUrl(String myurl) throws IOException {
        InputStream is;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(100000 /* milliseconds */);
            conn.setConnectTimeout(150000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            is = conn.getInputStream();
            return is;
        } catch (java.net.SocketTimeoutException e) {
            this.connectionError = "Timeout";
            return null;
        } catch (Exception e) {
            this.connectionError = "Invalid URL";
            return null;
        }
    }

    private String InputStreamToString(InputStream stream) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(stream));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
        }
        return total.toString();
    }
}
