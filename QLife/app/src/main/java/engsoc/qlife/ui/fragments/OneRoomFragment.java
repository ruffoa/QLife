package engsoc.qlife.ui.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import engsoc.qlife.R;
import engsoc.qlife.database.dibs.GetOneRoomBooking;
import engsoc.qlife.interfaces.observers.AsyncTaskObserver;
import engsoc.qlife.interfaces.enforcers.DrawerItem;
import engsoc.qlife.ui.recyclerview.DataObject;
import engsoc.qlife.utility.Constants;
import engsoc.qlife.utility.Util;
import engsoc.qlife.utility.async.DownloadImageTask;

/**
 * Created by Alex on 8/21/2017.
 * Fragment that shows when each room is available.
 */
public class OneRoomFragment extends Fragment implements DrawerItem {
    private String mRoomName, mRoomPicUrl;
    private int mRoomID;
    private String mBookedRooms;
    private Calendar cal = Calendar.getInstance();
    private View mView;
    private ImageView mImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mRoomName = bundle.getString(RoomsFragment.TAG_TITLE);
            mRoomPicUrl = bundle.getString(RoomsFragment.TAG_PIC);
            mRoomID = bundle.getInt(RoomsFragment.TAG_ROOM_ID);
        }

        GetOneRoomBooking dibs = new GetOneRoomBooking(null);
        try {
            mBookedRooms = dibs.execute(mRoomID, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        mView = inflater.inflate(R.layout.fragment_room_avaliability_info, container, false);
        setAvailableTimes();
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        TextView roomName = view.findViewById(R.id.RoomName);
        roomName.setText(mRoomName);
        TextView dateAvailability = view.findViewById(R.id.RoomAvailabilityDate);
        CharSequence date = DateFormat.format("EEEE, MMMM d, yyyy", cal.getTime());
        String availText = getString(R.string.current_availability) + date.toString();
        dateAvailability.setText(availText);

        if (mRoomPicUrl != null && mRoomPicUrl.length() > 4 && mRoomPicUrl.contains(Constants.HTTP)) {
            mImageView = view.findViewById(R.id.RoomPic);
            DownloadImageTask downloadImage = new DownloadImageTask(new AsyncTaskObserver() {
                @Override
                public void onTaskCompleted(Object obj) {
                    if (obj instanceof Bitmap) {
                        Bitmap result = (Bitmap) obj;
                        mImageView.setImageBitmap(result);
                    }
                }

                @Override
                public void beforeTaskStarted() {
                }

                @Override
                public void duringTask(Object obj) {
                }
            });
            downloadImage.execute(mRoomPicUrl);
        }
    }

    /**
     * Adds the available room times to the fragment.
     */
    private void setAvailableTimes() {
        ArrayList<DataObject> availableTimes = getDayAvailability();
        if (availableTimes == null) {
            //no internet
            mView.findViewById(R.id.NoInternet).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.NoAvailability).setVisibility(View.GONE);
        } else if (availableTimes.isEmpty()) {
            //no rooms available
            mView.findViewById(R.id.NoAvailability).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.NoInternet).setVisibility(View.GONE);
        } else {
            mView.findViewById(R.id.NoAvailability).setVisibility(View.GONE);
            //know at least one available slot, can get it
            String firstStart = availableTimes.get(0).getmText1();
            String firstEnd = availableTimes.get(0).getmText2();

            if (availableTimes.size() == 1) {
                //only one slot, don't try to combine multiple
                addAvailableTime(firstStart, firstEnd);
            } else {
                //combine overlapping available slots
                String start = firstStart;
                String end = firstEnd;
                for (int i = 1; i < availableTimes.size(); i++) {
                    String nextStart = availableTimes.get(i).getmText1();
                    String nextEnd = availableTimes.get(i).getmText2();
                    if (end.equals(nextStart)) {
                        //overlap, so don't add row and move along
                        end = nextEnd;
                    } else {
                        //don't overlap, add start/end and move along
                        addAvailableTime(start, end);
                        start = nextStart;
                        end = nextEnd;
                    }
                    if (i == availableTimes.size() - 1) {
                        //at end of loop, add current slot (next start/end)
                        addAvailableTime(start, end);
                    }
                }
            }
        }
    }

    /**
     * Function that adds an available time to the fragment view.
     *
     * @param start The starting time string.
     * @param end   The ending time string.
     */
    private void addAvailableTime(String start, String end) {
        LinearLayout layout = mView.findViewById(R.id.room_availability);
        TextView textView = new TextView(getContext());
        String text = start + " - " + end;
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
        layout.addView(textView);
    }

    /**
     * Displays the availability of the given ILC room. Currently puts each available hour slot
     * as a card in a recycler view.
     *
     * @return An array of objects that hold the room's available slots.
     */
    private ArrayList<DataObject> getDayAvailability() {
        ArrayList<DataObject> availability = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            availability.add(new DataObject((i + 7) + ":" + 30, (i + 8) + ":" + 30));
        }

        if (mBookedRooms != null && mBookedRooms.length() > 0) {
            try {
                JSONArray arr = new JSONArray(mBookedRooms);
                for (int i = 0; i < arr.length(); i++) {
                    //find times room is booked
                    JSONObject roomInfo = arr.getJSONObject(i);
                    String start = roomInfo.getString("StartTime");
                    String end = roomInfo.getString("EndTime");

                    start = start.substring(start.indexOf("T") + 1);
                    end = end.substring(end.indexOf("T") + 1);

                    //remove booked times from available times
                    for (int j = 0; j < availability.size(); j++) {
                        String startTime = availability.get(j).getmText1();
                        int startHour = Integer.parseInt(startTime.substring(0, startTime.indexOf(":")));

                        if (Integer.parseInt(start.substring(0, 2)) == startHour) {
                            availability.remove(j);
                            if (j > 0)
                                j--;
                        } else if (Integer.parseInt(start.substring(0, 2)) < startHour && Integer.parseInt(end.substring(0, 2)) > startHour) {
                            availability.remove(j);
                            if (j > 0)
                                j--;
                        }
                    }
                }

                //set AM/PM on start and end times
                for (int j = 0; j < availability.size(); j++) {
                    //get hour of start time - use to set AM/PM
                    String startTime = availability.get(j).getmText1();
                    int tempTime = Integer.parseInt(startTime.substring(0, startTime.indexOf(":")));

                    if (tempTime > 12) {
                        availability.get(j).setmText1((tempTime - 12) + ":30 PM");
                    }
                    if (tempTime >= 12) {
                        startTime = availability.get(j).getmText2();
                        int endTime = Integer.parseInt(startTime.substring(0, startTime.indexOf(":")));
                        availability.get(j).setmText2((endTime - 12) + ":30 PM");
                    }
                    if (tempTime <= 11) {
                        availability.get(j).setmText1(startTime + " AM");
                    }
                    if (tempTime < 11) {
                        availability.get(j).setmText2(availability.get(j).getmText2() + " AM");
                    }
                }
                return availability;
            } catch (JSONException e) {
                Log.d("HELLOTHERE", e.getMessage());
            }
        }
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        selectDrawer();
    }

    @Override
    public void onPause() {
        super.onPause();
        deselectDrawer();
    }

    @Override
    public void deselectDrawer() {
        Util.setDrawerItemSelected(getActivity(), R.id.nav_rooms, false);
    }

    @Override
    public void selectDrawer() {
        Util.setDrawerItemSelected(getActivity(), R.id.nav_rooms, true);
    }
}
