package engsoc.qlife.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;

import engsoc.qlife.interfaces.AsyncTaskObserver;

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
    protected Bitmap doInBackground(String... urls) {
        String urlDisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urlDisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.d("HELLOTHERE", e.getMessage());
        }
        return mIcon11;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        mObserver.onTaskCompleted(bitmap);
    }
}
