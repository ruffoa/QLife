package engsoc.qlife.ui.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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
        setTable();
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

    private void setTable() {
        TableLayout table = mView.findViewById(R.id.availability_table);
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
                TextView textView = new TextView(getContext());
                String text = firstStart + " " + firstEnd;
                textView.setText(text);
                TableRow newRow = new TableRow(getContext());
                newRow.addView(textView);
                table.addView(newRow);
            } else {
                //try to combine overlapping available slots
                String start = firstStart;
                String end = firstEnd;
                for (int i = 1; i < availability.size(); i++) {
                    String nextStart = availability.get(i).getmText1();
                    String nextEnd = availability.get(i).getmText2();
                    if (!sameTime(end, nextStart)) {
                        //don't overlap, add start/end and move along
                        TextView textView = new TextView(getContext());
                        String text = start + " " + end;
                        textView.setText(text);
                        TableRow newRow = new TableRow(getContext());
                        newRow.addView(textView);
                        table.addView(newRow);
                        start = nextStart;
                        end = nextEnd;
                    } else {
                        //overlap, so don't add row and move along
                        end = nextEnd;
                    }
                    if (i == availability.size() - 1) {
                        //at end of loop, print out current slot (next start/end)
                        TextView textView = new TextView(getContext());
                        String text = start + " " + end;
                        textView.setText(text);
                        TableRow newRow = new TableRow(getContext());
                        newRow.addView(textView);
                        table.addView(newRow);
                    }
                }
            }
        }
    }

    private boolean sameTime(String s1, String s2) {
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
            result.add(new DataObject("From: " + (i + 7) + ":" + 30, "To: " + (i + 8) + ":" + 30));
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
                    String test = result.get(j).getmText1().substring(6);
                    int tempTime = Integer.parseInt(test.substring(0, test.indexOf(":")));

                    if (tempTime > 12) {
                        result.get(j).setmText1("From: " + (tempTime - 12) + ":30 PM");
                    }
                    if (tempTime >= 12) {
                        test = result.get(j).getmText2().substring(4);
                        int endTime = Integer.parseInt(test.substring(0, test.indexOf(":")));
                        result.get(j).setmText2("To: " + (endTime - 12) + ":30 PM");
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
