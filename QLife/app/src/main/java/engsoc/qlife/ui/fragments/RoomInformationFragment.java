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
import engsoc.qlife.database.dibs.GetRoomBookings;
import engsoc.qlife.interfaces.AsyncTaskObserver;
import engsoc.qlife.ui.recyclerview.DataObject;
import engsoc.qlife.utility.Constants;
import engsoc.qlife.utility.async.DownloadImageTask;

/**
 * Created by Alex on 8/21/2017.
 * Fragment that shows when each room is available.
 */
public class RoomInformationFragment extends Fragment {
    private String mRoomName, mRoomPicUrl;
    private int mRoomID;
    private String roomAvailability;
    private Calendar cal = Calendar.getInstance();
    private View mView;
    private ImageView mImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mRoomName = bundle.getString(ILCRoomInfoFragment.TAG_TITLE);
            mRoomPicUrl = bundle.getString(ILCRoomInfoFragment.TAG_PIC);
            mRoomID = bundle.getInt(ILCRoomInfoFragment.TAG_ROOM_ID);
        }

        GetRoomBookings dibs = new GetRoomBookings(null);
        try {
            roomAvailability = dibs.execute(mRoomID, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR)).get();
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
        ArrayList<DataObject> availability = getDayAvailability();
        if (availability == null || availability.isEmpty()) {
            mView.findViewById(R.id.NoAvailability).setVisibility(View.VISIBLE);
        } else {
            mView.findViewById(R.id.NoAvailability).setVisibility(View.GONE);
            //know at least one available slot, can get it
            String firstStart = availability.get(0).getmText1();
            String firstEnd = availability.get(0).getmText2();

            if (availability.size() == 1) {
                //only one slot, don't try to combine multiple
                addAvailableTime(firstStart, firstEnd);
            } else {
                //try to combine overlapping available slots
                String start = firstStart;
                String end = firstEnd;
                for (int i = 1; i < availability.size(); i++) {
                    String nextStart = availability.get(i).getmText1();
                    String nextEnd = availability.get(i).getmText2();
                    if (isSameTime(end, nextStart)) {
                        //overlap, so don't add row and move along
                        end = nextEnd;
                    } else {
                        //don't overlap, add start/end and move along
                        addAvailableTime(start, end);
                        start = nextStart;
                        end = nextEnd;
                    }
                    if (i == availability.size() - 1) {
                        //at end of loop, print out current slot (next start/end)
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
     * Function that determines if two availability Strings have the same time.
     * The availability strings have From or To with them, so this is needed.
     *
     * @param s1 String one. Usually the starting time.
     * @param s2 String two. Usually the ending time.
     * @return True if the two have the same time, else false.
     */
    private boolean isSameTime(String s1, String s2) {
        //times are after the first space, so check they are equal after the first space
        s1 = s1.substring(s1.indexOf(' ') + 1);
        s2 = s2.substring(s2.indexOf(' ') + 1);
        return s1.equals(s2);
    }

    /**
     * Displays the availability of the given ILC room. Currently puts each available hour slot
     * as a card in a recycler view.
     *
     * @return An array of objects that hold the room's available slots.
     */
    private ArrayList<DataObject> getDayAvailability() {
        ArrayList<DataObject> result = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            result.add(new DataObject((i + 7) + ":" + 30, (i + 8) + ":" + 30));
        }

        if (roomAvailability != null && roomAvailability.length() > 0) {
            try {
                JSONArray arr = new JSONArray(roomAvailability);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject roomInfo = arr.getJSONObject(i);
                    String start = roomInfo.getString("StartTime");
                    String end = roomInfo.getString("EndTime");

                    start = start.substring(start.indexOf("T") + 1);
                    end = end.substring(end.indexOf("T") + 1);

                    for (int j = 0; j < result.size(); j++) {
                        String test = result.get(j).getmText1().substring(6);
                        int startTime = Integer.parseInt(test.substring(0, test.indexOf(":")));

                        if (Integer.parseInt(start.substring(0, 2)) == startTime) {
                            result.remove(j);
                            if (j > 0)
                                j--;
                        } else if (Integer.parseInt(start.substring(0, 2)) < startTime && Integer.parseInt(end.substring(0, 2)) > startTime) {
                            result.remove(j);
                            if (j > 0)
                                j--;
                        }
                    }
                }

                for (int j = 0; j < result.size(); j++) {
                    //get hour of start time - use to set AM/PM
                    String startTime = result.get(j).getmText1();
                    int tempTime = Integer.parseInt(startTime.substring(0, startTime.indexOf(":")));

                    //set start/end time with PM
                    if (tempTime > 12) {
                        result.get(j).setmText1((tempTime - 12) + ":30 PM");
                    }
                    if (tempTime >= 12) {
                        startTime = result.get(j).getmText2();
                        int endTime = Integer.parseInt(startTime.substring(0, startTime.indexOf(":")));
                        result.get(j).setmText2((endTime - 12) + ":30 PM");
                    }
                    //set start/end time with AM
                    if (tempTime <= 11) {
                        result.get(j).setmText1(startTime + " AM");
                    }
                    if (tempTime < 11) {
                        result.get(j).setmText2(result.get(j).getmText2() + " AM");
                    }
                }
                return result;
            } catch (JSONException e) {
                Log.d("HELLOTHERE", e.getMessage());
            }
        }
        return null;
    }
}
