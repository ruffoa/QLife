package engsoc.qlife.ui.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import engsoc.qlife.R;
import engsoc.qlife.database.dibs.getDibsRoomInfo;
import engsoc.qlife.ui.recyclerview.DataObject;
import engsoc.qlife.ui.recyclerview.RecyclerViewAdapter;
import engsoc.qlife.utility.Constants;

/**
 * Created by Alex on 8/21/2017.
 * Fragment that shows when each room is available.
 */
public class RoomInformationFragment extends Fragment {
    private String mRoomName, mRoomDescription, mRoomPicUrl;
    private int mRoomID;
    private RecyclerView.Adapter mAdapter;
    private String roomAvailability;
    private Calendar cal = Calendar.getInstance();
    private RecyclerView mRecyclerView;
    private ImageView mImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mRoomName = bundle.getString(ILCRoomInfoFragment.TAG_TITLE);
            mRoomDescription = bundle.getString(ILCRoomInfoFragment.TAG_DESC);
            mRoomPicUrl = bundle.getString(ILCRoomInfoFragment.TAG_PIC);
            mRoomID = bundle.getInt(ILCRoomInfoFragment.TAG_ROOM_ID);
        }

        getDibsRoomInfo dibs = new getDibsRoomInfo(this.getContext());
        try {
            roomAvailability = dibs.execute(mRoomID, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        View v = inflater.inflate(R.layout.fragment_room_avaliability_info, container, false);
        mRecyclerView = v.findViewById(R.id.avaliabilityRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false); //this is to allow for entire card scrolling
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new RecyclerViewAdapter(getDayAvailability());
        mRecyclerView.setAdapter(mAdapter);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        TextView roomName = view.findViewById(R.id.RoomName);
        roomName.setText(mRoomName);
        TextView roomDesc = view.findViewById(R.id.RoomDescription);
        roomDesc.setText(mRoomDescription);
        TextView roomLoc = view.findViewById(R.id.RoomLoc);
        String roomLocText = getString(R.string.ilc_room) + mRoomName;
        roomLoc.setText(roomLocText);

        if (mAdapter.getItemCount() > 0) {
            TextView dateAvailability = view.findViewById(R.id.RoomAvaliabilityDate);
            if (Calendar.getInstance() == cal)
                dateAvailability.setText(getString(R.string.current_availability));
            else {
                CharSequence date = DateFormat.format("EEEE, MMMM d, yyyy", cal.getTime());
                String mDateString = date.toString();
                String dateAvailabilityText = getString(R.string.future_availability) + mDateString;
                dateAvailability.setText(dateAvailabilityText);
            }
        }

        if (mRoomPicUrl != null && mRoomPicUrl.length() > 4 && mRoomPicUrl.contains(Constants.HTTP)) {
            mImageView = view.findViewById(R.id.RoomPic);
            new DownloadImageTask().execute(mRoomPicUrl);
        }
    }

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

    /**
     * Downloads the image URL for an ILC room.
     * Not static, however no memory leaks. Follows Google's example Login inner AsyncTask.
     */
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            int recyclerHeight = mRecyclerView.getHeight();
            mImageView.setImageBitmap(result);
            mRecyclerView.getLayoutParams().height = recyclerHeight;

        }
    }
}
