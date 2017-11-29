package engsoc.qlife.database.dibs;

import android.util.Log;

import engsoc.qlife.interfaces.AsyncTaskObserver;
import engsoc.qlife.utility.Constants;
import engsoc.qlife.utility.async.DownloadTextTask;

/**
 * Created by Alex on 8/6/2017.
 * Class that gets ILC room availability.
 */
public class GetRoomBookings extends DownloadTextTask<Integer, String> {

    public GetRoomBookings(AsyncTaskObserver observer) {
        super(observer);
    }

    @Override
    protected String backgroundTask(Integer val) {
        return null;
    }

    @Override
    protected String backgroundTaskMultiple(Integer[] values) {
        try {
            int rMid = values[0];
            int day = values[1];
            int month = values[2];
            int year = values[3];

            //call php script on server that gets info from cloud database
            return getText(Constants.GET_ROOM_BOOKINGS + year + "-" + (month + 1) + "-" + day + "/" + rMid);
        } catch (Exception e) {
            Log.d("HELLOTHERE", "BAD: " + e);
        }
        return null;
    }
}


