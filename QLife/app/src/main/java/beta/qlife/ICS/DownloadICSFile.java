package beta.qlife.ICS;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import beta.qlife.interfaces.observers.AsyncTaskObserver;

/**
 * Created by Alex on 1/29/2017.
 * Class to download the ICS file. Once it is downloaded, this class calls the parser class.
 */
public class DownloadICSFile extends AsyncTask<String, Integer, String> {
    private AsyncTaskObserver mObserver;

    public DownloadICSFile(AsyncTaskObserver observer) {
        mObserver = observer;
    }

    @Override
    protected String doInBackground(String... sUrl) {
        try {
            URL url = new URL(sUrl[0]);
            HttpURLConnection connection;
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }
            mObserver.duringTask(connection);
        } catch (IOException e) {
            Log.d("HELLOTHERE", e.getMessage());
        }
        return null;
    }
}
