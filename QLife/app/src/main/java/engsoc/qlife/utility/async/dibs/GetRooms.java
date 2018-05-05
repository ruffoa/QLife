package engsoc.qlife.utility.async.dibs;

import android.util.Log;

import org.json.JSONArray;

import engsoc.qlife.interfaces.observers.AsyncTaskObserver;
import engsoc.qlife.utility.Constants;
import engsoc.qlife.utility.async.DownloadTextTask;

/**
 * Created by Alex Ruffo on 21/06/2017.
 * Gets the D!bs API data for the selected day
 */
public class GetRooms extends DownloadTextTask<Void, Void> {

    public GetRooms(AsyncTaskObserver observer) {
        super(observer);
    }

    @Override
    protected Void backgroundTask(Void val) {
        try {
            String jsonStr = getText(Constants.GET_DIBS_ROOMS);
            if (mObserver != null)
                mObserver.duringTask(new JSONArray(jsonStr));
        } catch (Exception e) {
            //must catch JSONException, this will catch exception from
            //null jsonStr when trying to make a JSONArray
            Log.d("HELLOTHERE", "BAD: " + e);
        }
        return null;
    }
}
