package beta.qlife.ui.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

import beta.qlife.R;
import beta.qlife.interfaces.enforcers.DrawerItem;
import beta.qlife.interfaces.observers.AsyncTaskObserver;
import beta.qlife.utility.Constants;
import beta.qlife.utility.TimeSlot;
import beta.qlife.utility.Util;
import beta.qlife.utility.async.DownloadImageTask;
import beta.qlife.utility.async.dibs.GetOneRoomBooking;

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
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
        ArrayList<TimeSlot> availableTimes = getDayAvailability();
        if (availableTimes == null) {
            //no internet
            mView.findViewById(R.id.no_data).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.NoAvailability).setVisibility(View.GONE);
        } else if (availableTimes.isEmpty()) {
            //no rooms available
            mView.findViewById(R.id.NoAvailability).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.no_data).setVisibility(View.GONE);
        } else {
            mView.findViewById(R.id.NoAvailability).setVisibility(View.GONE);
            //know at least one available slot, can get it
            String firstStart = availableTimes.get(0).getStart();
            String firstEnd = availableTimes.get(0).getEnd();

            if (availableTimes.size() == 1) {
                //only one slot, don't try to combine multiple
                addAvailableTime(firstStart, firstEnd);
            } else {
                //combine overlapping available slots
                String start = firstStart;
                String end = firstEnd;
                for (int i = 1; i < availableTimes.size(); i++) {
                    String nextStart = availableTimes.get(i).getStart();
                    String nextEnd = availableTimes.get(i).getEnd();
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
    private ArrayList<TimeSlot> getDayAvailability() {
        ArrayList<TimeSlot> availability = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            if (Util.isWeekend()) {
                availability.add(new TimeSlot((i + 7) + ":00", (i + 8) + ":00"));
            } else {
                availability.add(new TimeSlot((i + 7) + ":" + 30, (i + 8) + ":" + 30));
            }
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
                        String startTime = availability.get(j).getStart();
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

                //set AM/PM on start and end times - only for weekdays
                for (TimeSlot data : availability) {
                    //get hour of start time - use to set AM/PM
                    String startTime = data.getStart();
                    String endTime = data.getEnd();
                    int startHour = Integer.parseInt(startTime.substring(0, startTime.indexOf(":")));
                    int endHour = Integer.parseInt(endTime.substring(0, endTime.indexOf(":")));

                    if (startHour > 12) {
                        data.setStart((startHour - 12) + endTime.substring(endTime.indexOf(":")) + " PM");
                    } else if (startHour == 12) {
                        data.setStart(startTime + " PM");
                    }
                    if (startHour >= 12) {
                        data.setEnd((endHour - 12) + endTime.substring(endTime.indexOf(":")) + " PM");
                    }
                    if (startHour <= 11) {
                        if (endHour == 12) {
                            data.setEnd(endTime + " PM");
                        }
                        data.setStart(startTime + " AM");
                    }
                    if (startHour < 11) {
                        data.setEnd(data.getEnd() + " AM");
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
