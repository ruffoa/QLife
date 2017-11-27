package engsoc.qlife.database.dibs;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

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
 * Created by Alex Ruffo on 21/06/2017.
 * Gets the D!bs API data for the selected day
 */
public class GetRooms extends AsyncTask<Void, Void, Void> {
    private AsyncTaskObserver mObserver;

    public GetRooms(AsyncTaskObserver observer) {
        mObserver = observer;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mObserver.beforeTaskStarted();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            //call php script on server that gets info from cloud database
            String jsonStr = getJSON(Constants.GET_DIBS_ROOMS);
            mObserver.duringTask(new JSONArray(jsonStr));
        } catch (JSONException e) {
            Log.d("HELLOTHERE", "BAD: " + e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mObserver.onTaskCompleted(null);
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
            Log.d("HELLOTHERE", ex.getMessage());
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
}
