package engsoc.qlife.database;

import android.util.Log;

import org.json.JSONObject;

import engsoc.qlife.interfaces.observers.AsyncTaskObserver;
import engsoc.qlife.utility.Constants;
import engsoc.qlife.utility.async.DownloadTextTask;

/**
 * Created by Carson on 21/06/2017.
 * Async task that downloads and parses the cloud database into the phone database.
 */
public class GetCloudDb extends DownloadTextTask<Void, Void> {
    private static final String TAG_SUCCESS = "Success";

    public GetCloudDb(AsyncTaskObserver observer) {
        super(observer);
    }

    @Override
    protected Void backgroundTask(Void val) {
        try {
            //call php script on server that gets info from cloud database
            JSONObject json = new JSONObject(getText(Constants.GET_DATABASE));
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1) {
                mObserver.duringTask(json);
            }
        } catch (Exception e) {
            //catch JSONException as required
            //catch exception when getText() result is null - no data for whatever getText()
            //request failed
            Log.d("HELLOTHERE", "BAD: " + e);
        }
        return null;
    }
}