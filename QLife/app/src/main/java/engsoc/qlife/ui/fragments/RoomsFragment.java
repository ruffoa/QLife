package engsoc.qlife.ui.fragments;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import engsoc.qlife.R;
import engsoc.qlife.database.dibs.GetRoomBookings;
import engsoc.qlife.database.dibs.GetRooms;
import engsoc.qlife.database.local.DatabaseRow;
import engsoc.qlife.database.local.rooms.Room;
import engsoc.qlife.database.local.rooms.RoomManager;
import engsoc.qlife.interfaces.AsyncTaskObserver;
import engsoc.qlife.interfaces.IQLActionbarFragment;
import engsoc.qlife.interfaces.IQLDrawerItem;
import engsoc.qlife.interfaces.IQLListFragment;
import engsoc.qlife.ui.recyclerview.DataObject;
import engsoc.qlife.ui.recyclerview.SectionedRecyclerView;
import engsoc.qlife.utility.Constants;
import engsoc.qlife.utility.Util;

public class RoomsFragment extends Fragment implements IQLActionbarFragment, IQLDrawerItem, IQLListFragment {
    public static final String TAG_TITLE = "room_title";
    public static final String TAG_PIC = "pic";
    public static final String TAG_ROOM_ID = "room_id";

    private boolean mReturning = false;
    private boolean mShowingAll = true;

    private Button mAvailableButton;
    private Button mAllButton;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private View mView;

    private String mRoomAvailability;
    private ArrayList<DataObject> mRoomData = new ArrayList<>();
    private ArrayList<DataObject> mAllAvailableRooms = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_ilcroom_info, container, false);
        mRecyclerView = mView.findViewById(R.id.ilcRoomInfoRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new SectionedRecyclerView(getDayEventData());
        mRecyclerView.setAdapter(mAdapter);

        setActionbarTitle();
        inflateListView();

        mAvailableButton = mView.findViewById(R.id.available);
        mAllButton = mView.findViewById(R.id.all);
        mAvailableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //toggle selected
                mShowingAll = false;
                mAllButton.setBackground(new ColorDrawable(getResources().getColor(R.color.colorAccent)));
                mAvailableButton.setBackground(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
                showAvailableRooms();
            }
        });
        mAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //toggle selected
                mShowingAll = true;
                mAvailableButton.setBackground(new ColorDrawable(getResources().getColor(R.color.colorAccent)));
                mAllButton.setBackground(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
                showAllRooms();
            }
        });

        ((SectionedRecyclerView) mAdapter).setOnItemClickListener(new SectionedRecyclerView
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                DataObject data = ((SectionedRecyclerView) mAdapter).getItem(position);

                LinearLayout card = mView.findViewById(R.id.sectioned_card_view);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    card.setTransitionName("transistion_event_info" + position);
                }
                RoomManager roomInf = new RoomManager(getContext());
                ArrayList<DatabaseRow> info = roomInf.getTable();

                String pic = "";
                if (data != null && info.size() > 0) {
                    for (DatabaseRow row : info) {
                        Room room = (Room) row;
                        if (room.getRoomId() == data.getID()) {
                            pic = room.getPicUrl();
                            break;
                        }
                    }
                }
                Bundle bundle = new Bundle();
                if (data != null) {
                    bundle.putString(TAG_TITLE, data.getmText1());
                    bundle.putInt(TAG_ROOM_ID, data.getID());
                }
                bundle.putString(TAG_PIC, pic);

                String cardName = card.getTransitionName();
                OneRoomFragment nextFrag = new OneRoomFragment();
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

    /**
     * Shows all ILC rooms, sorted by small, medium and large.
     */
    private void showAllRooms() {
        ArrayList<DataObject> small = new ArrayList<>();
        ArrayList<DataObject> med = new ArrayList<>();
        ArrayList<DataObject> large = new ArrayList<>();
        ArrayList<DataObject> other = new ArrayList<>();
        ArrayList<DataObject> res = new ArrayList<>();

        for (DataObject obj : mRoomData) {
            if (Pattern.compile(Pattern.quote("small"), Pattern.CASE_INSENSITIVE).matcher(obj.getmText2()).find())
                small.add(obj);
            else if (Pattern.compile(Pattern.quote("medium"), Pattern.CASE_INSENSITIVE).matcher(obj.getmText2()).find())
                med.add(obj);
            else if (Pattern.compile(Pattern.quote("large"), Pattern.CASE_INSENSITIVE).matcher(obj.getmText2()).find())
                large.add(obj);
            else other.add(obj);
        }

        small.get(0).setHeader("Small Group Rooms");
        med.get(0).setHeader("Medium Group Rooms");
        large.get(0).setHeader("Large Group Rooms");
        other.get(0).setHeader("Un-categorized Rooms");

        res.addAll(small);
        res.addAll(med);
        res.addAll(large);
        res.addAll(other);

        mAdapter = new SectionedRecyclerView(res);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void showAvailableRooms() {
        RoomManager roomInf = new RoomManager(this.getContext());
        ArrayList<DatabaseRow> data = roomInf.getTable();
        Calendar cal = Calendar.getInstance();

        try {
            if (data != null && data.size() > 0) {
                for (DatabaseRow row : data) {
                    GetRoomBookings dibs = new GetRoomBookings(null);
                    Room room = (Room) row;
                    mRoomAvailability = dibs.execute(room.getRoomId(), cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR)).get();
                    if (currentlyAvailable()) {
                        mAllAvailableRooms.add(new DataObject(room.getName(), room.getDescription(), room.getRoomId(), true, "", room.getDescription()));
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        ArrayList<DataObject> small = new ArrayList<>();
        ArrayList<DataObject> med = new ArrayList<>();
        ArrayList<DataObject> large = new ArrayList<>();
        ArrayList<DataObject> other = new ArrayList<>();

        for (DataObject obj : mAllAvailableRooms) {
            if (Pattern.compile(Pattern.quote("small"), Pattern.CASE_INSENSITIVE).matcher(obj.getmText2()).find())
                small.add(obj);
            else if (Pattern.compile(Pattern.quote("medium"), Pattern.CASE_INSENSITIVE).matcher(obj.getmText2()).find())
                med.add(obj);
            else if (Pattern.compile(Pattern.quote("large"), Pattern.CASE_INSENSITIVE).matcher(obj.getmText2()).find())
                large.add(obj);
            else other.add(obj);
        }

        mAllAvailableRooms.clear(); //remove rooms so they can be re-added in proper order
        small.get(0).setHeader("Small Group Rooms");
        med.get(0).setHeader("Medium Group Rooms");
        large.get(0).setHeader("Large Group Rooms");
        other.get(0).setHeader("Un-categorized Rooms");

        mAllAvailableRooms.addAll(small);
        mAllAvailableRooms.addAll(med);
        mAllAvailableRooms.addAll(large);
        mAllAvailableRooms.addAll(other);
        mAdapter = new SectionedRecyclerView(mAllAvailableRooms);
        mRecyclerView.setAdapter(mAdapter);
    }

    private ArrayList<DataObject> getDayEventData() {
        RoomManager roomInf = new RoomManager(this.getContext());
        ArrayList<DataObject> result = new ArrayList<>();
        ArrayList<DatabaseRow> data = roomInf.getTable();

        if (data != null && data.size() > 0) {
            for (DatabaseRow row : data) {
                Room room = (Room) row;
                boolean hasTV = room.getDescription().contains(Constants.TV) || room.getDescription().contains(Constants.PROJECTOR);
                result.add(new DataObject(room.getName(), room.getDescription(), room.getRoomId(), hasTV, ""));
            }
            mRoomData = result;
            return result;
        }
        return null;
    }

    private boolean currentlyAvailable() {
        if (mRoomAvailability != null && mRoomAvailability.length() > 0) {
            try {
                JSONArray arr = new JSONArray(mRoomAvailability);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject roomInfo = arr.getJSONObject(i);
                    String start = roomInfo.getString("StartTime");
                    start = start.substring(start.indexOf("T") + 1);

                    int sHour = Integer.parseInt(start.substring(0, 2));
                    Calendar cal = Calendar.getInstance();
                    int now = cal.get(Calendar.HOUR_OF_DAY);
                    int nowMin = cal.get(Calendar.MINUTE);

                    if (sHour == now || (sHour == now - 1 && nowMin < 30)) {
                        return false;
                    }
                }
            } catch (JSONException e) {
                Log.d("HELLOTHERE", e.getMessage());
            }
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        selectDrawer();
        //check if coming back to fragment on a back press
        if (mReturning) {
            //check for available/all shown before and restore that state
            if (mShowingAll) {
                mAllAvailableRooms.clear();
                mAllButton.performClick();
            } else {
                mShowingAll = false;
                mAllButton.setBackground(new ColorDrawable(getResources().getColor(R.color.colorAccent)));
                mAvailableButton.setBackground(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
                mAdapter = new SectionedRecyclerView(mAllAvailableRooms);
                mRecyclerView.setAdapter(mAdapter);
            }
        } else {
            //show all rooms
            mAllAvailableRooms.clear();
            getDayEventData();
            mAllButton.performClick();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        deselectDrawer();
        mReturning = true; //will come back on a back button press - will be overridden by onDestroy if necessary
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mReturning = false; //won't come back to this instance
    }

    @Override
    public void deselectDrawer() {
        Util.setDrawerItemSelected(getActivity(), R.id.nav_rooms, false);
    }

    @Override
    public void selectDrawer() {
        Util.setDrawerItemSelected(getActivity(), R.id.nav_rooms, true);
    }

    @Override
    public void inflateListView() {
        RoomManager roomInf = new RoomManager(this.getContext());
        ArrayList<DatabaseRow> data = roomInf.getTable();
        if (data == null || data.size() == 0) {
            final Context context = getContext();
            GetRooms dibs = new GetRooms(new AsyncTaskObserver() {
                @Override
                public void onTaskCompleted(Object obj) {
                }

                @Override
                public void beforeTaskStarted() {
                }

                @Override
                public void duringTask(Object obj) {
                    if (obj.getClass() == JSONArray.class) {
                        JSONArray rooms = (JSONArray) obj;
                        try {
                            RoomManager tableManager = new RoomManager(context);
                            for (int i = 0; i < rooms.length(); i++) {
                                JSONObject roomInfo = rooms.getJSONObject(i);
                                tableManager.insertRow(new Room(roomInfo.getInt(Room.COLUMN_ROOM_ID), roomInfo.getInt(Room.COLUMN_BUILDING_ID), roomInfo.getString(Room.COLUMN_DESCRIPTION),
                                        roomInfo.getString(Room.COLUMN_MAP_URL), roomInfo.getString(Room.COLUMN_NAME), roomInfo.getString(Room.COLUMN_PIC_URL), roomInfo.getInt(Room.COLUMN_ROOM_ID)));
                            }
                        } catch (JSONException e) {
                            Log.d("HELLOTHERE", "EMERG: " + e);
                        }
                    }
                }
            });
            try {
                dibs.execute().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setActionbarTitle() {
        Util.setActionbarTitle(getString(R.string.fragment_ilc_rooms), (AppCompatActivity) getActivity());
    }
}