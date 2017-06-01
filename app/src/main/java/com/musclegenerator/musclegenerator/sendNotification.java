package com.musclegenerator.musclegenerator;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Mert on 29.05.2017.
 */

public class sendNotification extends AsyncTask<Void, Void, String> {
    public static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";
    public static final String FCM_SERVER_API_KEY    = "AAAAJk4x7Gg:APA91bHl6L10AH9YZrD0gzEL3LxMLZJeRrbaxj9srMoUZAxbuh4Hq9E9_UtDmYQxp40MUTxxPWygImSWjnSXRjJkgtG8tJilUC6-kPZPHmZqoPrei6rwL7og2MJbBOuE0mgFVA2HPxqv";
    public String message = "";
    public String sendnotificationto = "";

    @Override
    protected String doInBackground(Void... params) {
        try{

            String authKey = FCM_SERVER_API_KEY;
            String FMCurl = FCM_URL;

            URL url2 = new URL(FMCurl);
            HttpURLConnection conn = (HttpURLConnection) url2.openConnection();

            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization","key="+authKey);
            conn.setRequestProperty("Content-Type","application/json");
            JSONObject json = new JSONObject();

            json.put("to",sendnotificationto);
            JSONObject info = new JSONObject();
            info.put("title", "MUSCLE GENERATOR"); // Notification title
            info.put("body", message); // Notification body
            info.put("sound", "default");
            json.put("data", info);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(json.toString());
            wr.flush();

            int responsecode = conn.getResponseCode();

            Log.d("RESPONSE CODE",""+responsecode);

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return "OK";
    }


    @Override
    protected void onPostExecute(String s) {

    }



}



