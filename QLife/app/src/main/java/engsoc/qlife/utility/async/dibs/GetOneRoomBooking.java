package engsoc.qlife.utility.async.dibs;

import android.util.Log;

import engsoc.qlife.interfaces.observers.AsyncTaskObserver;
import engsoc.qlife.utility.Constants;
import engsoc.qlife.utility.async.DownloadTextTask;

/**
 * Created by Alex on 8/6/2017.
 * Class that gets one ILC room availability.
 */
public class GetOneRoomBooking extends DownloadTextTask<Integer, String> {

    public GetOneRoomBooking(AsyncTaskObserver observer) {
        super(observer);
    }

    @Override
    protected String backgroundTask(Integer val) {
        return null;
    }

    @Override
    protected String backgroundTaskMultiple(Integer[] values) {
        try {
            int roomId = values[0];
            int day = values[1];
            int month = values[2];
            int year = values[3];
            return getText(Constants.GET_ROOM_BOOKINGS + "year=" + year + "&month=" + (month + 1) + "&day=" + day + "&room=" + roomId);
        } catch (Exception e) {
            Log.d("HELLOTHERE", "BAD: " + e);
        }
        return null;
    }
}