package engsoc.qlife.ui.fragments;

/**
 * Created by Alex on 8/21/2017.
 */


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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
import engsoc.qlife.utility.Util;
import engsoc.qlife.ui.recyclerview.DataObject;
import engsoc.qlife.ui.recyclerview.RecyclerViewAdapter;
import engsoc.qlife.database.dibs.ILCRoomObj;
import engsoc.qlife.database.dibs.ILCRoomObjManager;
import engsoc.qlife.database.dibs.getDibsRoomInfo;
import engsoc.qlife.database.local.DatabaseRow;
import engsoc.qlife.interfaces.IQLActionbarFragment;


public class RoomAvaliabilityInfoFragment extends Fragment {

    private String mRoomName, mRoomDescription, mDate, mRoomMapUrl, mRoomPicUrl;
    private int mRoomID;
    private View myView;
    private NavigationView mNavView;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private String roomAvaliabiliy;
    private Calendar cal = Calendar.getInstance();
    private int recyclerHeight = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mRoomName = bundle.getString(ILCRoomInfoFragment.TAG_TITLE);
            mRoomDescription = bundle.getString(ILCRoomInfoFragment.TAG_DESC);
            mDate = bundle.getString(ILCRoomInfoFragment.TAG_DATE);
            mRoomMapUrl = bundle.getString(ILCRoomInfoFragment.TAG_MAP);
            mRoomPicUrl = bundle.getString(ILCRoomInfoFragment.TAG_PIC);
            mRoomID = bundle.getInt(ILCRoomInfoFragment.TAG_ROOM_ID);
        }
        myView = inflater.inflate(R.layout.fragment_room_avaliability_info, container, false);

        getDibsRoomInfo dibs = new getDibsRoomInfo(this.getContext());
        try {
            roomAvaliabiliy = dibs.execute(mRoomID, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR)).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        mRecyclerView = (RecyclerView) myView.findViewById(R.id.avaliabilityRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);     // this is to allow for entire card scrolling
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new RecyclerViewAdapter(getDayAvaliability());
        ((RecyclerViewAdapter) mAdapter).setOnItemClickListener(new RecyclerViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
            }
        });

        mRecyclerView.setAdapter(mAdapter);

        return myView;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            recyclerHeight = mRecyclerView.getHeight();
            bmImage.setImageBitmap(result);
//            mAdapter.notifyDataSetChanged();
            mRecyclerView.getLayoutParams().height = recyclerHeight;

        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        TextView roomName = (TextView) view.findViewById(R.id.RoomName);
        roomName.setText(mRoomName);
        TextView roomDesc = (TextView) view.findViewById(R.id.RoomDescription);
        roomDesc.setText(mRoomDescription);
        TextView roomLoc = (TextView) view.findViewById(R.id.RoomLoc);
        roomLoc.setText("ILC Room " + mRoomName);

        if (mAdapter.getItemCount() > 0) {
            TextView dateAvaliability = (TextView) view.findViewById(R.id.RoomAvaliabilityDate);
            if (Calendar.getInstance() == cal)
                dateAvaliability.setText("Current room availability for today");
            else {
                CharSequence date = DateFormat.format("EEEE, MMMM d, yyyy", cal.getTime());
                String mDateString = date.toString();
                dateAvaliability.setText("Current room availability for " + mDateString);
            }
        }

        if (mRoomPicUrl != null && mRoomPicUrl.length() > 4 && mRoomPicUrl.contains("http")) {
            new DownloadImageTask((ImageView) view.findViewById(R.id.RoomPic))
                    .execute(mRoomPicUrl);

        }
//        setActionbarTitle((AppCompatActivity) getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        mNavView = (NavigationView) (getActivity()).findViewById(R.id.drawer_layout).findViewById(R.id.nav_view);
        mNavView.getMenu().findItem(R.id.nav_day).setChecked(true);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onPause() {
        super.onPause();
        mNavView.getMenu().findItem(R.id.nav_day).setChecked(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    }

//    @Override
//    public void setActionbarTitle(AppCompatActivity activity) {
//        Util.setActionbarTitle(R.string.fragment_event_info, activity);
//    }

    public ArrayList<DataObject> getDayAvaliability() {

        ArrayList<DataObject> result = new ArrayList<DataObject>();
        ArrayList<DataObject> list = new ArrayList<DataObject>();
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);

        for (int i = cal.get(Calendar.HOUR_OF_DAY) - 1; i < 23; i++) {
            result.add(new DataObject("From: " + (i)+":" + 30,"To: " + (i + 1)+":" + 30));
        }

        if (roomAvaliabiliy != null && roomAvaliabiliy.length() > 0) {
            try {
                JSONArray arr = new JSONArray(roomAvaliabiliy);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject roomInfo = arr.getJSONObject(i);
                    String start = roomInfo.getString("StartTime");
                    String end = roomInfo.getString("EndTime");

                    start = start.substring(start.indexOf("T") + 1);
                    end = end.substring(end.indexOf("T") + 1);

                    DataObject currentTime = new DataObject("From: " + start, "To: " + end);

                    list.add(currentTime);

                    for (int j = 0; j < result.size(); j++) {
                            DataObject temp = result.get(j);
                        String test = result.get(j).getmText1().substring(6);
                        int startTime = Integer.parseInt(test.substring(0,test.indexOf(":")));
//                        int tempend = Integer.parseInt(result.get(j).getmText2().substring(0,result.get(j).getmText1().indexOf(":")));

                        if (Integer.parseInt(start.substring(0,2)) == startTime){
                            result.remove(j);
                            if (j > 0)
                                j --;
                        }
                        else if (Integer.parseInt(start.substring(0,2)) < startTime && Integer.parseInt(end.substring(0,2)) > startTime ){
                            result.remove(j);
                            if (j > 0)
                                j --;
                        }
                    }
//                    if (result.contains(currentTime)){
//                        result.remove(currentTime);
//                    }

//                    list.add(new DataObject("From: " + start, "To: " + end));
                }

                for (int j = 0; j < result.size(); j++) {
                    String test = result.get(j).getmText1().substring(6);
                    int temptime = Integer.parseInt(test.substring(0,test.indexOf(":")));

                    if (temptime >= 12) {
                        result.get(j).setmText1("From: " + (temptime - 12) + ":30 PM");
                        test = result.get(j).getmText2().substring(4);
                        int endtime = Integer.parseInt(test.substring(0,test.indexOf(":")));
                        result.get(j).setmText2("To: " + (endtime - 12) + ":30 PM");
                    }
                    else if (temptime == 11)
                    {
                        test = result.get(j).getmText2().substring(4);
                        int endtime = Integer.parseInt(test.substring(0,test.indexOf(":")));
                        result.get(j).setmText2("To: " + (endtime - 12) + ":30 PM");
                    }
                    else
                    {
                        result.get(j).setmText1("From: " + (temptime) + ":30 AM");
                        test = result.get(j).getmText2().substring(4);
                        int endtime = Integer.parseInt(test.substring(0,test.indexOf(":")));
                        result.get(j).setmText2("To: " + (endtime) + ":30 AM");
                    }
                }
                return result;
            } catch (JSONException e) {

            }
        }
        return null;
    }
}
