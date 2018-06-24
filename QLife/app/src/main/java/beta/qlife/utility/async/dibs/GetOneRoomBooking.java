package beta.qlife.utility.async.dibs;

import android.util.Log;

import beta.qlife.interfaces.observers.AsyncTaskObserver;
import beta.qlife.utility.Constants;
import beta.qlife.utility.async.DownloadTextTask;

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
            return getText(Constants.GET_ROOM_BOOKINGS + "room=" + roomId);
        } catch (Exception e) {
            Log.d("HELLOTHERE", "BAD: " + e);
        }
        return null;
    }
}