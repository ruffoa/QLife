package beta.qlife.utility.async;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;

import beta.qlife.interfaces.observers.AsyncTaskObserver;

/**
 * Created by Carson on 2017-11-28.
 * Asynchronously downloads an image.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    private AsyncTaskObserver mObserver;

    public DownloadImageTask(AsyncTaskObserver observer) {
        mObserver = observer;
    }

    @Override
    protected void onPreExecute() {
        mObserver.beforeTaskStarted();
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        String urlDisplay = urls[0];
        Bitmap icon = null;
        try {
            InputStream in = new java.net.URL(urlDisplay).openStream();
            icon = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.d("HELLOTHERE", e.getMessage());
        }
        return icon;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        mObserver.onTaskCompleted(bitmap);
    }
}
