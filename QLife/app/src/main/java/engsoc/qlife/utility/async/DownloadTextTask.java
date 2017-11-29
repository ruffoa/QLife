package engsoc.qlife.utility.async;

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
 * Created by Carson on 2017-11-29.
 * Abstract class to be buffer between AsyncTask and tasks that download text.
 * Abstract the connection and text acquiring.
 * Two type parameters to fill in the types of the AsyncTask. The first type is
 * the parameter type sent to the backgroundTask() function. The second type
 * parameter is the type of units showing progress, which for QLife is always Void.
 * The final parameter is the type of the parameter sent to onPostExecute().
 */
public abstract class DownloadTextTask<T, S> extends AsyncTask<T, Void, S> {
    protected AsyncTaskObserver mObserver;

    /**
     * Protected constructor so only children classes can instantiate, by way of calling super().
     */
    protected DownloadTextTask(AsyncTaskObserver observer) {
        mObserver = observer;
    }

    /**
     * Method that children classes will use to define the behaviour done in the background of
     * the AsyncTask.
     *
     * @param val The value passed into the backgroundTask() AsyncTask function.
     * @return The result of the background task. Usually null.
     */
    abstract protected S backgroundTask(T val);

    /**
     * backgroundTask() (with different name) that takes an array of values, instead of just one.
     * Not abstract so that not all children have to implement, as usually only one parameter
     * is passed.
     *
     * @param values The array of values passed.
     * @return The result of the background task. Usually null.
     */
    protected S backgroundTaskMultiple(T[] values) {
        return null;
    }

    @Override
    protected S doInBackground(T[] ts) {
        //parameter passed could be Void, if so ts[0] is out of bounds
        if (ts.length <= 0) {
            return backgroundTask(null);
        } else if (ts.length == 1) {
            return backgroundTask(ts[0]);
        } else {
            return backgroundTaskMultiple(ts);
        }
    }

    @Override
    protected void onPreExecute() {
        if (mObserver != null) {
            mObserver.beforeTaskStarted();
        } else {
            super.onPreExecute();
        }
    }

    @Override
    protected void onPostExecute(S s) {
        if (mObserver != null) {
            mObserver.onTaskCompleted(s);
        } else {
            super.onPostExecute(s);
        }
    }

    /**
     * Method that connects to a URL and reads the text on that page into a string.
     * For the database, the page is just text in JSON format. Another example is the
     * login page where the HTML code is parsed to find the ICS file download link.
     *
     * @param url The URL of the page being read.
     * @return The string of the text read.
     */
    protected String getText(String url) {
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
