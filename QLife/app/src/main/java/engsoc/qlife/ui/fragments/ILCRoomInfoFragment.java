package engsoc.qlife.ui.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import engsoc.qlife.R;
import engsoc.qlife.activities.MainTabActivity;
import engsoc.qlife.database.dibs.getDibsRoomInfo;
import engsoc.qlife.ui.recyclerview.DataObject;
import engsoc.qlife.database.dibs.ILCRoomObj;
import engsoc.qlife.database.dibs.ILCRoomObjManager;
import engsoc.qlife.database.dibs.getDibsApiInfo;
import engsoc.qlife.database.local.DatabaseRow;
import engsoc.qlife.ui.recyclerview.SectionedRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class ILCRoomInfoFragment extends Fragment {

    public static final String TAG_TITLE = "room_title";
    public static final String TAG_PIC = "pic";
    public static final String TAG_MAP = "map";
    public static final String TAG_DESC = "room_description";
    public static final String TAG_ROOM_ID = "room_id";
    public static final String TAG_DATE = "date";

    private static int mInstances = 0;
    private static SparseIntArray mArray = new SparseIntArray();
    private int mTotalDaysChange = 0;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private NavigationView mNavView;
    private View mView;
    private TextView mDateText;
    private JSONArray json;
    private String roomAvaliabiliy;
    private View mProgressView;

    private ArrayList<DataObject> mRoomData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_ilcroom_info, container, false);
        mDateText = (TextView) mView.findViewById(R.id.date);

        Bundle bundle = getArguments();

        ILCRoomObjManager roomInf = new ILCRoomObjManager(this.getContext());
        ArrayList<DatabaseRow> data = roomInf.getTable();
        if (data == null || data.size() == 0) {
            getDibsApiInfo dibs = new getDibsApiInfo(this.getContext());
            try {
                dibs.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        mRecyclerView = (RecyclerView) mView.findViewById(R.id.ilcRoomInfoRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new SectionedRecyclerView(getDayEventData());
        mRecyclerView.setAdapter(mAdapter);

        Button nextButton = (Button) mView.findViewById(R.id.next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeDate(1);
                mTotalDaysChange += 1;
            }
        });
        Button prevButton = (Button) mView.findViewById(R.id.prev);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeDate(-1);
                mTotalDaysChange += -1;
            }
        });

        mProgressView = mView.findViewById(R.id.ilcRoomInf_progress);

        FloatingActionButton myFab = (FloatingActionButton) mView.findViewById(R.id.sortRoomsFab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onFABClick();
            }
        });
        myFab.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                boolean res = false;
                res = onFABLongClick();
                return res;
            }
        });
        nextButton.setVisibility(View.GONE);
        prevButton.setVisibility(View.GONE);

        ((SectionedRecyclerView) mAdapter).setOnItemClickListener(new SectionedRecyclerView
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                DataObject data = ((SectionedRecyclerView) mAdapter).getItem(position);

                LinearLayout card = (LinearLayout) mView.findViewById(R.id.sectioned_card_view);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    card.setTransitionName("transistion_event_info" + position);
                }
                ILCRoomObjManager roomInf = new ILCRoomObjManager(getContext());
                ArrayList<DatabaseRow> info = roomInf.getTable();

                String map = "";
                String pic = "";

                if (data != null && info.size() > 0) {
                    for (DatabaseRow row : info) {
                        ILCRoomObj room = (ILCRoomObj) row;
                        if (room.getRoomId() == data.getID()) {
                            map = room.getMapUrl();
                            pic = room.getPicUrl();
                            break;
                        }
                    }
                }

                String cardName = card.getTransitionName();
                RoomAvaliabilityInfoFragment nextFrag = new RoomAvaliabilityInfoFragment();

                Bundle bundle = new Bundle();
                bundle.putString(TAG_TITLE, data.getmText1());
                bundle.putString(TAG_DESC, data.getDescription());
                bundle.putString(TAG_MAP, map);
                bundle.putString(TAG_PIC, pic);
                bundle.putString(TAG_DATE, Calendar.getInstance().toString());
                bundle.putInt(TAG_ROOM_ID, data.getID());


                nextFrag.setArguments(bundle);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().addToBackStack(null)
                        .replace(R.id.content_frame, nextFrag)
                        .addSharedElement(card, cardName)
                        .commit();
            }
        });
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionbar != null) {
            actionbar.setTitle(getString(R.string.fragment_ilc_rooms));
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstances++;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!((MainTabActivity) getActivity()).isToActivity()) {
            mArray.put(mInstances, mTotalDaysChange); //save number of days to move day view
        } else {
            mArray.put(mInstances, 0);
        }
        mNavView.getMenu().findItem(R.id.nav_rooms).setChecked(false);
    }

    @Override
    public void onDestroy() {
        mArray.delete(mInstances); //instance gone, don't need entry
        mInstances--;
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mNavView = (NavigationView) (getActivity()).findViewById(R.id.drawer_layout).findViewById(R.id.nav_view);
        changeDate(mArray.get(mInstances, 0)); //account for day changed before moved fragments
        mNavView.getMenu().findItem(R.id.nav_rooms).setChecked(true);
    }

    private void onFABClick() {     // on a click, we organize the ILC rooms from small to large, plus 1 "unknown" (aka rm 111)
        ArrayList<DataObject> small = new ArrayList<DataObject>();
        ArrayList<DataObject> med = new ArrayList<DataObject>();
        ArrayList<DataObject> large = new ArrayList<DataObject>();  // set up an arrayList to hold all of the rooms for each category
        ArrayList<DataObject> other = new ArrayList<DataObject>();
        ArrayList<DataObject> res = new ArrayList<DataObject>();

        for (DataObject obj : mRoomData) {
            if (Pattern.compile(Pattern.quote("small"), Pattern.CASE_INSENSITIVE).matcher(obj.getmText2()).find())  // essentially, this just means if the room description (obj.getmText2 (yay variable names :) ) contains the word "small", we add it to the list of small rooms
                small.add(obj);
            else if (Pattern.compile(Pattern.quote("medium"), Pattern.CASE_INSENSITIVE).matcher(obj.getmText2()).find())    // ditto for the other two types
                med.add(obj);
            else if (Pattern.compile(Pattern.quote("large"), Pattern.CASE_INSENSITIVE).matcher(obj.getmText2()).find())
                large.add(obj);
            else other.add(obj);    // it's not a small, medium or large room, so it *must* be rm 111 (at least until they change things)
        }


        small.get(0).setHeader("Small Group Rooms");
        med.get(0).setHeader("Medium Group Rooms");
        large.get(0).setHeader("Large Group Rooms");
        other.get(0).setHeader("Uncategorized Rooms");

        res.addAll(small);
        res.addAll(med);
        res.addAll(large);
        res.addAll(other);

        mAdapter = new SectionedRecyclerView(res);
        mRecyclerView.setAdapter(mAdapter);

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private boolean onFABLongClick() {  // for this function, we grab all rooms that are currently unbooked, and display a nice list of them

        mProgressView.setVisibility(View.VISIBLE);  // show a loading screen while this is going on, it takes a good 3-5 seconds depending on your internet connection, and that's with a SD650, which is a mid-high end CPU -> ToDo: make this more efficient if possible, although since network calls take a while, and we need to use a new call for every room ,it may not be possible to make this faster

        ILCRoomObjManager roomInf = new ILCRoomObjManager(this.getContext());   // get the database table containing the list of all the ILC rooms in the database
        ArrayList<DataObject> result = new ArrayList<DataObject>();                 // create a arratList to hold the rooms that are avaliable
        ArrayList<DatabaseRow> data = roomInf.getTable();                   // get the data from the roomInfo table in the database, and put it in a arrayList

        Calendar cal = Calendar.getInstance();  // create a new calendar, and initialize it to today

        try {
            if (data != null && data.size() > 0) {  // if there exists a room in the table (there should be some, unless this is the first launch, and the table has not been populated yet)

                showProgress(true);                 // display the loading scree -> ToDo: make this actually work :P
                mProgressView.setVisibility(View.VISIBLE);   // display the loading scree -> ToDo: make this actually work :P

                for (DatabaseRow row : data) {  // for each room within the database
                    getDibsRoomInfo dibs = new getDibsRoomInfo(this.getContext());  // create a new instance of the getDibsRoomInfo class, which essentially uses the d!bs API to get a JSON with room booking data for the given room at a given date
                    ILCRoomObj room = (ILCRoomObj) row; // create a new ILC Room Object
                    roomAvaliabiliy = dibs.execute(room.getRoomId(), cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR)).get();    // get the room avaliability on the UI thread (this means it's slllloooooooowwwwwwwwww)
                    int status = getDayAvaliability();  // see if the room is free today / right now
                    if (status == 0) {                  // room is free right now for the current hour timeslot
                        result.add(new DataObject(room.getName(), "Is Avaliable Now", room.getRoomId(), true, "", room.getDescription()));

                    } else if (status == 2) // if the room was booked for the current time slot, but the slot is almost over (sHour == now - 1 && nowMin < 30)
                        result.add(new DataObject(room.getName(), "Is Avaliable at " + cal.get(Calendar.HOUR) + ":30", room.getRoomId(), true, "", room.getDescription()));

                    else if (status == 4)   // if the room was booked for the current timeslot, but it is still before the timeslot starts (sHour == now && nowMin < 30)
                        result.add(new DataObject(room.getName(), "Is Avaliable Until " + cal.get(Calendar.HOUR) + ":30", room.getRoomId(), true, "", room.getDescription()));

                    else if (status == 3)   // if the room is booked for the next time slot (sHour == now + 1)
                        result.add(new DataObject(room.getName(), "Is Avaliable Until " + (cal.get(Calendar.HOUR) + 1) + ":30", room.getRoomId(), true, "", room.getDescription()));
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        }


        showProgress(false);

        mAdapter = new SectionedRecyclerView(result);
        mRecyclerView.setAdapter(mAdapter);

        return true;
    }

    public void changeDate(int numChange) {
//        mCalendar.add(Calendar.DAY_OF_YEAR, numChange);
//        mAdapter = new RecyclerViewAdapter(getDayEventData(mCalendar));
//        mRecyclerView.setAdapter(mAdapter);
    }

    public ArrayList<DataObject> getDayEventData() {
//        TextView noClassMessage = (TextView) mView.findViewById(R.id.no_class_message);
//        noClassMessage.setVisibility(View.GONE); //updates day view when go to new day - may have class
        ILCRoomObjManager roomInf = new ILCRoomObjManager(this.getContext());

        ArrayList<DataObject> result = new ArrayList<DataObject>();

        ArrayList<DatabaseRow> data = roomInf.getTable();

        if (data != null && data.size() > 0) {
            for (DatabaseRow row : data) {
                ILCRoomObj room = (ILCRoomObj) row;
                boolean hasTV = room.getDescription().contains("TV") || room.getDescription().contains("Projector") ? true : false;
                result.add(new DataObject(room.getName(), room.getDescription(), room.getRoomId(), hasTV, "")); // create a new DataObject with the inputed parameters to be shown within the recyclerView
            }
            mRoomData = result;
            return result;
        }
        return null;
    }

    public int getDayAvaliability() {   // gets the information sent to it, and returns whether or not the room is free at the current timeslot


        if (roomAvaliabiliy != null && roomAvaliabiliy.length() > 0) {  // if the JSON array containing the data is not null (which would be bad)
            try {
                JSONArray arr = new JSONArray(roomAvaliabiliy); // make a JSON array variable to hold a properly formatted JSON object

                int state = 0;

                Calendar cal = Calendar.getInstance();      // get a new calendar set to the current time
                int now = cal.get(Calendar.HOUR_OF_DAY);    // get the current hour in 24 hour format (it's a lot easier like this)
                int nowMin = cal.get(Calendar.MINUTE);      // get the current minute, for checking on the half hour purposes (because aligning everything to a hour start would have been too easy)

                for (int i = 0; i < arr.length(); i++) {    // for all of the data within the array
                    JSONObject roomInfo = arr.getJSONObject(i); // get the info for the JSON object (each booking is it's own object)
                    String start = roomInfo.getString("StartTime"); // get the startTime and endTime contained within the object
                    String end = roomInfo.getString("EndTime");

                    start = start.substring(start.indexOf("T") + 1);    // get the posistion of the start and end times, and set the string to be the useful part
                    end = end.substring(end.indexOf("T") + 1);

                    int sHour = Integer.parseInt(start.substring(0, 2));    // cast the starting hour to an int
                    int eHour = Integer.parseInt(start.substring(0, 2));    // cast the ending hour to an int
//                    if (sHour == now && nowMin < 30)            // if the room was booked for the current timeslot, but it is still before the timeslot starts
//                        return 4;
                    if (sHour == now || (sHour <= now && now <= eHour)) {   // if the current hour is equal to the start hour of a booking, or the current hour is within the time of a booking
                        return 1;       // return 1, the room is currently booked, so we are done, return immediately.
                    } else if (sHour == now - 1 && nowMin < 30) // if the room was booked for the current time slot, but the slot is almost over
                        state = 2;  // keep state as 2, so that people know that the room is free for the next hour block.  If return 1 is never called, 2 will be returned
                    else if (sHour == now + 1)                  // if the room is booked for the next time slot
                        state = 3;  // let users know that the room will be booked in the next slot, so that they know someone will be after them
                }
                return state;   // if the room is not currently booked, return the state of the room booking as explained above
            } catch (JSONException e) { // something broke :(
                
            }
        }
        return 0;
    }
}