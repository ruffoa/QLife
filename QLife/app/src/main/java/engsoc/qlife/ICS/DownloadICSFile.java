package engsoc.qlife.ICS;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Alex on 1/29/2017.
 * Class to download the ICS file. Once it is downloaded, this class calls the parser class.
 */
public class DownloadICSFile extends AsyncTask<String, Integer, String> {

    private static final String TAG = DownloadICSFile.class.getSimpleName();

    private Context mContext;

    public DownloadICSFile(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected String doInBackground(String... sUrl) {
        InputStream input = null;
        FileOutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(sUrl[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            Log.d(TAG, "connection Successful!");

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();
            output = this.mContext.getApplicationContext().openFileOutput("cal.ics", MODE_PRIVATE);
            Log.d(TAG, "set file output");

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
//                logcatArray(data);    // this prints the output to the console, uncomment this for debugging purposes
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            //close streams, end connection
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }
            if (connection != null)
                connection.disconnect();
        }
        final ParseICS parser = new ParseICS(mContext);
        parser.parseICSData();
        parser.getClassTypes();   // assign the value of this to a variable so that android waits for it to complete
        return null;
    }

    /**
     * Method to output a byte array's contents on the debug logcat.
     *
     * @param array The byte array to be outputted.
     */
    private void logcatArray(byte[] array) {
        String output = "";
        for (int i : array) {
            output += array[i] + " ";
        }
        Log.d(TAG, output);
    }
}
