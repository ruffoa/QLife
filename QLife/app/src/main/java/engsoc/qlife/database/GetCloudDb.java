package engsoc.qlife.database;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import engsoc.qlife.interfaces.AsyncTaskObserver;
import engsoc.qlife.utility.Constants;

/**
 * Created by Carson on 21/06/2017.
 * Async task that downloads and parses the cloud database into the phone database.
 */
public class GetCloudDb extends AsyncTask<Void, Void, Void> {
    private static final String TAG_SUCCESS = "Success";

    private AsyncTaskObserver mObserver;

    public GetCloudDb(AsyncTaskObserver observer) {
        mObserver = observer;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        JSONObject json;
        try {
            //call php script on server that gets info from cloud database
            json = new JSONObject(getJSON(Constants.GET_DATABASE));
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1) {
                mObserver.duringTask(json);
            }
        } catch (JSONException e) {
            Log.d("HELLOTHERE", "BAD: " + e);
        }
        return null;
    }

    private String getJSON(String url) {
        HttpURLConnection con = null;
        try {
            URL u = new URL(url);
            con = (HttpURLConnection) u.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-length", "0");
            con.setRequestProperty("Connection", "close");
            con.setUseCaches(false);
            con.setAllowUserInteraction(false);
            con.setConnectTimeout(Constants.TIMEOUT);
            con.setReadTimeout(Constants.TIMEOUT);
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
            Log.d("HELLOTHERE", "bad " + e);
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
}