package com.snipertech.hopinn.repository;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class BackgroundTask extends AsyncTask<String, String, String> {

    private final WeakReference<Activity> weakActivity;

    public BackgroundTask(Activity myActivity) {
        this.weakActivity = new WeakReference<>(myActivity);
    }

    @Override
    protected String doInBackground(String... strings) {
        String addURL="http://192.168.1.18/Example/hopin.php";
            String name= strings[0];
            String message=strings[1];
            String userId = strings[2];
            try{
                URL url= new URL(addURL);
                try{
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
                    BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                    String insert_data = URLEncoder.encode("name", "UTF-8")+"="+URLEncoder.encode(name, "UTF-8")+
                            "&"+URLEncoder.encode("message", "UTF-8")+"="+URLEncoder.encode(message, "UTF-8")+
                            "&"+URLEncoder.encode("userId", "UTF-8")+"="+URLEncoder.encode(userId, "UTF-8");
                    bufferedWriter.write(insert_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String result= "";
                    String line="";
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((line = bufferedReader.readLine())!=null){
                        stringBuilder.append(line).append("\n");
                    }
                    result = stringBuilder.toString();
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return result;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        // Re-acquire a strong reference to the activity, and verify
        // that it still exists and is active.
        Activity activity = weakActivity.get();
        if (activity == null
                || activity.isFinishing()
                || activity.isDestroyed()) {
            // activity is no longer valid, don't do anything!
            return;
        }
    }

}