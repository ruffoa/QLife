package engsoc.qlife.ICS;

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

import engsoc.qlife.interfaces.AsyncTaskObserver;
import engsoc.qlife.utility.Constants;

/**
 * Created by Alex on 8/6/2017.
 * Asynchronously goes to course web pages and parses for the class title.
 */
public class GetCourseInfo extends AsyncTask<String, Void, String> {
    private AsyncTaskObserver mObserver;

    public GetCourseInfo(AsyncTaskObserver observer) {
        mObserver = observer;
    }

    @Override
    protected String doInBackground(String... info) {
        try {
            String courseType = info[0];
            String htmlStr;

            //call php script on server that gets info from cloud database
            if (courseType.contains("COMM"))
                htmlStr = getHTML(Constants.GET_COMM_CLASS);
            else
                htmlStr = getHTML(Constants.GET_ENG_CLASS + courseType + Constants.FILTER_ENG_CLASS);
            return htmlStr;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        mObserver.onTaskCompleted(s);
    }

    private String getHTML(String url) {
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


