package engsoc.qlife.database.dibs;

/**
 * Created by Alex on 8/21/2017.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import engsoc.qlife.R;
import engsoc.qlife.database.local.DatabaseRow;
import engsoc.qlife.ui.fragments.ILCRoomInfoFragment;

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
 * Created by Alex Ruffo on 21/06/2017.
 * Gets the D!bs API data for the selected day
 */
public class getDibsApiInfo extends AsyncTask<Void, Void, Void> {

    private static final String TAG_SUCCESS = "Success";

    private ProgressDialog mProgressDialog;
    private Context mContext;

    public getDibsApiInfo(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Downloading cloud database. Please wait...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            //call php script on server that gets info from cloud database
            String jsonStr = getJSON(mContext.getString(R.string.dibs_get_rooms), 5000);
            JSONArray json = new JSONArray(jsonStr);

            if (json != null) {
                cloudToPhoneDB(json);
            }
        } catch (JSONException e) {
            Log.d("HELLOTHERE", "BAD: " + e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mProgressDialog.dismiss();
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

    private void cloudToPhoneDB(JSONArray json) {
        getRoomInfo(json);
//        engineeringContacts(json);
//        buildings(json);
//        food(json);
//        cafeterias(json);
    }

    public JSONArray getRoomTimes(int roomID) {
        try {
            Calendar cal;
            cal = Calendar.getInstance();


            String jsonStr = getJSON(mContext.getString(R.string.dibs_get_rooms_times)  + cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH) + "-" + roomID, 5000);
            JSONArray json = new JSONArray(jsonStr);
            return json;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void getRoomInfo(JSONArray rooms) {
        try {
            ILCRoomObjManager tableManager = new ILCRoomObjManager(mContext);
//            for (DatabaseRow row: tableManager.getTable()
//                 ) {
//                tableManager.deleteRow(row);
//            }
            for (int i = 0; i < rooms.length(); i++) {
                JSONObject roomInfo = rooms.getJSONObject(i);
                tableManager.insertRow(new ILCRoomObj(roomInfo.getInt(ILCRoomObj.COLUMN_ROOM_ID), roomInfo.getInt(ILCRoomObj.COLUMN_BUILDING_ID), roomInfo.getString(ILCRoomObj.COLUMN_DESCRIPTION),
                        roomInfo.getString(ILCRoomObj.COLUMN_MAP_URL), roomInfo.getString(ILCRoomObj.COLUMN_NAME), roomInfo.getString(ILCRoomObj.COLUMN_PIC_URL), roomInfo.getInt(ILCRoomObj.COLUMN_ROOM_ID)));
            }
        } catch (JSONException e) {
            Log.d("HELLOTHERE", "EMERG: " + e);
        }
    }

}
