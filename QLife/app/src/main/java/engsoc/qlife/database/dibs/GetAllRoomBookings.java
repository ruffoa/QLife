package engsoc.qlife.database.dibs;

import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;

import engsoc.qlife.database.local.DatabaseRow;
import engsoc.qlife.database.local.rooms.Room;
import engsoc.qlife.interfaces.observers.AsyncTaskObserver;
import engsoc.qlife.ui.fragments.RoomsFragment;
import engsoc.qlife.utility.Constants;
import engsoc.qlife.utility.async.DownloadTextTask;

/**
 * Created by Carson on 8/6/2017.
 * Class that gets all ILC room availability.
 */
public class GetAllRoomBookings extends DownloadTextTask<Integer, SparseArray<String>> {
    private ArrayList<DatabaseRow> mRoomData;

    public GetAllRoomBookings(ArrayList<DatabaseRow> roomData, AsyncTaskObserver observer) {
        super(observer);
        mRoomData = roomData;
    }

    @Override
    protected SparseArray<String> backgroundTask(Integer val) {
        return null;
    }

    @Override
    protected SparseArray<String> backgroundTaskMultiple(Integer[] values) {
        SparseArray<String> roomAvailability = new SparseArray<>();
        for (DatabaseRow row : mRoomData) {
            Room room = (Room) row;
            try {
                int day = values[RoomsFragment.DAY_POS];
                int month = values[RoomsFragment.MONTH_POS];
                int year = values[RoomsFragment.YEAR_POS];

                //call php script on server that gets info from cloud database
                roomAvailability.put((int) room.getId(), getText(Constants.GET_ROOM_BOOKINGS + year + "-" +
                        (month + 1) + "-" + day + "/" + room.getId()));
            } catch (Exception e) {
                Log.d("HELLOTHERE", "BAD: " + e);
            }
        }
        return roomAvailability;
    }
}


