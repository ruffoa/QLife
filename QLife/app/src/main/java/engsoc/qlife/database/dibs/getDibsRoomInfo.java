package engsoc.qlife.database.dibs;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import engsoc.qlife.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Alex on 8/6/2017.
 */

public class getDibsRoomInfo extends AsyncTask<Integer, Void, String> {

    /**
     * Created by Carson on 21/06/2017.
     * Async task that downloads and parses the cloud database into the phone database.
     */

    private static final String TAG_SUCCESS = "Success";

    private Context mContext;

    public getDibsRoomInfo(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Integer... roomID) {
        try {
//            Calendar cal;
//            cal = Calendar.getInstance();
            int rmid = roomID[0];
            int day = roomID[1];
            int month = roomID[2];
            int year = roomID[3];

            //call php script on server that gets info from cloud database
            String jsonStr = getJSON(mContext.getString(R.string.dibs_get_rooms_times) + year + "-" + (month + 1) + "-" + day + "/" + rmid, 5000);
            return jsonStr;
//            if (json != null) {
//                return json;
//            }
        } catch (Exception e) {
            Log.d("HELLOTHERE", "BAD: " + e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }

    private String getJSON(String url, int timeout) {
        HttpURLConnection con = null;
        try {
            URL u = new URL(url);
            con = (HttpURLConnection) u.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-length", "0");
            con.setRequestProperty("Connection", "close");
            con.setUseCaches(false);
            con.setAllowUserInteraction(false);
            con.setConnectTimeout(timeout);
            con.setReadTimeout(timeout);
            con.connect();
            int status = con.getResponseCode();
            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();
                    return sb.toString();
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e) {
            Log.d("HELLOTHERE", "bad io " + e);
        } finally {
            if (con != null) {
                try {
                    con.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }

//    public JSONArray getRoomTimes(JSONArray json, int roomID) {
//        try {
//            Calendar cal;
//            cal = Calendar.getInstance();
//
//            String jsonStr = getJSON(mContext.getString(R.string.dibs_get_rooms_times) + cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH) + "-" + roomID, 5000);
//            JSONArray json = new JSONArray(jsonStr);
//            return json;
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

}


