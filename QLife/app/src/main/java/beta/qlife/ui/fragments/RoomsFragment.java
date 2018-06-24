package beta.qlife.ui.fragments;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
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

import beta.qlife.R;
import beta.qlife.database.local.DatabaseRow;
import beta.qlife.database.local.rooms.Room;
import beta.qlife.database.local.rooms.RoomManager;
import beta.qlife.interfaces.enforcers.ActionbarFragment;
import beta.qlife.interfaces.enforcers.DrawerItem;
import beta.qlife.interfaces.enforcers.ListFragment;
import beta.qlife.interfaces.observers.AsyncTaskObserver;
import beta.qlife.ui.recyclerview.list_objects.RoomsObject;
import beta.qlife.ui.recyclerview.recyler_adapters.RoomsAdapter;
import beta.qlife.utility.Constants;
import beta.qlife.utility.Util;
import beta.qlife.utility.async.dibs.GetAllRoomBookings;
import beta.qlife.utility.async.dibs.GetRooms;

public class RoomsFragment extends Fragment implements ActionbarFragment, DrawerItem, ListFragment {
    public static final String TAG_TITLE = "room_title";
    public static final String TAG_PIC = "pic";
    public static final String TAG_ROOM_ID = "room_id";

    private boolean mReturning = false;
    private boolean mShowingAll = true;

    private Button mAvailableButton;
    private Button mAllButton;
    private RecyclerView mRecyclerView;
    private RoomsAdapter mAdapter;
    private View mView;

    //need as instance variable so that when coming back, don't have to take
    //time to re-get availability data. Highly likely data hasn't changed in time not on this fragment.
    private ArrayList<RoomsObject> mAllAvailableRooms = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_ilcroom_info, container, false);
        inflateListView();
        setViews();
        setActionbarTitle();
        setOnClicks();
        return mView;
    }

    /**
     * Helper method that sorts the supplied list of rooms by small, medium and then large.
     * The API call already returns them in order of number.
     *
     * @param rooms The list of rooms to sort.
     * @return The sorted list of rooms.
     */
    private ArrayList<RoomsObject> sortRooms(ArrayList<RoomsObject> rooms) {
        ArrayList<RoomsObject> small = new ArrayList<>();
        ArrayList<RoomsObject> med = new ArrayList<>();
        ArrayList<RoomsObject> large = new ArrayList<>();
        ArrayList<RoomsObject> other = new ArrayList<>();
        ArrayList<RoomsObject> res = new ArrayList<>();

        for (RoomsObject room : rooms) {
            if (Pattern.compile(Pattern.quote("small"), Pattern.CASE_INSENSITIVE).matcher(room.getDescription()).find())
                small.add(room);
            else if (Pattern.compile(Pattern.quote("medium"), Pattern.CASE_INSENSITIVE).matcher(room.getDescription()).find())
                med.add(room);
            else if (Pattern.compile(Pattern.quote("large"), Pattern.CASE_INSENSITIVE).matcher(room.getDescription()).find())
                large.add(room);
            else other.add(room);
        }

        //find each type of room
        if (small.size() > 0)
            small.get(0).setHeader("Small Group Rooms");
        if (med.size() > 0)
            med.get(0).setHeader("Medium Group Rooms");
        if (large.size() > 0)
            large.get(0).setHeader("Large Group Rooms");
        if (other.size() > 0)
            other.get(0).setHeader("Un-categorized Rooms");

        //sequentially add each type of room so they show up in order
        res.addAll(small);
        res.addAll(med);
        res.addAll(large);
        res.addAll(other);
        return res;
    }

    /**
     * Shows all ILC rooms, sorted by small, medium and large.
     */
    private void showAllRooms() {
        ArrayList<RoomsObject> rooms = getAllRooms();
        if (rooms.isEmpty()) {
            //no rooms means no internet
            mView.findViewById(R.id.no_data).setVisibility(View.VISIBLE);
        } else {
            mView.findViewById(R.id.no_data).setVisibility(View.GONE);
            rooms = sortRooms(rooms);
        }
        setAdapter(rooms);
    }

    /**
     * Shows available ILC rooms, sorted by small, medium and large.
     */
    private void showAvailableRooms() {
        RoomManager roomManager = new RoomManager(this.getContext());
        final ArrayList<DatabaseRow> rooms = roomManager.getTable();
        Calendar cal = Calendar.getInstance();

        if (rooms != null && rooms.size() > 0) {
            final ProgressDialog dialog = new ProgressDialog(getContext());
            GetAllRoomBookings dibs = new GetAllRoomBookings(rooms, new AsyncTaskObserver() {
                @Override
                public void onTaskCompleted(Object obj) {
                    dialog.dismiss();
                    if (obj != null && obj instanceof SparseArray) {
                        SparseArray roomAvailability = (SparseArray) obj;
                        mAllAvailableRooms.clear();
                        for (DatabaseRow data : rooms) {
                            Room room = (Room) data;
                            //get an object because roomAvailability just holds objects
                            Object objAvail = roomAvailability.get((int) room.getId());
                            //check retrieved a String - availability should be a String
                            String availability = null;
                            if (objAvail != null && objAvail instanceof String) {
                                availability = (String) objAvail;
                            }
                            if (currentlyAvailable(availability))
                                mAllAvailableRooms.add(new RoomsObject(room.getName(), room.getDescription(), room.getRoomId(), true, ""));
                        }
                        mAllAvailableRooms = sortRooms(mAllAvailableRooms);
                        setAdapter(mAllAvailableRooms);
                    } else {
                        mAllAvailableRooms.add(new RoomsObject("Data could not be retrieved", null, -1, false, null));
                        setAdapter(mAllAvailableRooms);
                    }
                }

                @Override
                public void beforeTaskStarted() {
                    dialog.setMessage("Please Wait...");
                    dialog.setCancelable(false);
                    dialog.show();
                }

                @Override
                public void duringTask(Object obj) {
                }
            });
            dibs.execute(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
        }
    }

    /**
     * Method that retrieves all rooms from the phone database and packs them into a list.
     *
     * @return List of room information packed into RoomsObjects.
     */
    private ArrayList<RoomsObject> getAllRooms() {
        RoomManager roomManager = new RoomManager(this.getContext());
        ArrayList<RoomsObject> result = new ArrayList<>();
        ArrayList<DatabaseRow> roomData = roomManager.getTable();

        if (roomData != null && roomData.size() > 0) {
            for (DatabaseRow row : roomData) {
                Room room = (Room) row;
                boolean hasTV = room.getDescription().contains(Constants.TV) || room.getDescription().contains(Constants.PROJECTOR);
                result.add(new RoomsObject(room.getName(), room.getDescription(), room.getRoomId(), hasTV, ""));
            }
        }
        return result;
    }

    /**
     * Determines if one room is available.
     *
     * @param roomAvailability String that holds the availability information for one room.
     * @return True if the room is currently available, else false.
     */
    private boolean currentlyAvailable(String roomAvailability) {
        if (roomAvailability == null) {
            //means no internet
            mView.findViewById(R.id.no_data).setVisibility(View.VISIBLE);
            return false;
        } else if (roomAvailability.length() > 0) {
            mView.findViewById(R.id.no_data).setVisibility(View.GONE);
            try {
                JSONArray arr = new JSONArray(roomAvailability);

                for (int i = 0; i < arr.length(); i++) {
                    //parse for useful information
                    JSONObject roomInfo = arr.getJSONObject(i);
                    String start = roomInfo.getString("StartTime");
                    start = start.substring(start.indexOf("T") + 1);
                    String end = roomInfo.getString("EndTime");
                    end = end.substring(end.indexOf('T') + 1);

                    //get proper types for interesting times
                    int startHour = Integer.parseInt(start.substring(0, 2));
                    int endHour = Integer.parseInt(end.substring(0, 2));
                    Calendar cal = Calendar.getInstance();
                    int curHour = cal.get(Calendar.HOUR_OF_DAY);

                    //check if available - weekdays are on half hour, weekend on the hour
                    if (Util.isWeekend()) {
                        if (startHour <= curHour && curHour < endHour) {
                            return false;
                        }
                    } else {
                        if ((startHour == curHour && cal.get(Calendar.MINUTE) > 30) || (startHour <= curHour && curHour <= endHour)) {
                            return false;
                        }
                    }
                }
            } catch (JSONException e) {
                Log.d("HELLOTHERE", e.getMessage());
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        selectDrawer();
        //check if coming back to fragment on a back press - if are, restore old state
        if (mReturning) {
            if (mShowingAll) {
                mAllAvailableRooms.clear();
                mAllButton.performClick();
            } else {
                mShowingAll = false;
                setButtonColours();
                setAdapter(mAllAvailableRooms);
            }
        } else {
            mAllAvailableRooms.clear();
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
        RoomManager roomManager = new RoomManager(this.getContext());
        ArrayList<DatabaseRow> roomData = roomManager.getTable();
        if (roomData == null || roomData.size() == 0) {
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
                            RoomManager tableManager = new RoomManager(getContext());
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

    /**
     * Helper method that sets the recycler view adapter to a new view, and then assigns it
     * to actually be the recycler view adapter.
     *
     * @param list List holding the data to be shown in the recycler view.
     */
    private void setAdapter(ArrayList<RoomsObject> list) {
        mAdapter = new RoomsAdapter(list);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * Helper method used to assign views instead of doing so in onCreate.
     */
    private void setViews() {
        mRecyclerView = mView.findViewById(R.id.ilcRoomInfoRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        setAdapter(getAllRooms());
        mAvailableButton = mView.findViewById(R.id.available);
        mAllButton = mView.findViewById(R.id.all);
    }

    /**
     * Helper method that sets the colours of the All/Available buttons to match which is pressed.
     */
    private void setButtonColours() {
        if (mShowingAll) {
            mAvailableButton.setBackground(new ColorDrawable(getResources().getColor(R.color.colorAccent)));
            mAllButton.setBackground(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
        } else {
            mAllButton.setBackground(new ColorDrawable(getResources().getColor(R.color.colorAccent)));
            mAvailableButton.setBackground(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
        }
    }

    /**
     * Helper method that sets the onClick methods for buttons (and recycler view items),
     * instead of doing so in onCreate.
     */
    private void setOnClicks() {
        //onClick for button that shows available rooms
        mAvailableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //toggle selected
                mShowingAll = false;
                setButtonColours();
                showAvailableRooms();
            }
        });

        //onClick for button that will show all rooms
        mAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //toggle selected
                mShowingAll = true;
                setButtonColours();
                showAllRooms();
            }
        });

        //onClick for item in the recycler view (a room)
        mAdapter.setOnItemClickListener(new RoomsAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                FragmentActivity activity = getActivity();
                if (activity != null) {
                    RoomsObject data = (RoomsObject) mAdapter.getItem(position);

                    LinearLayout card = mView.findViewById(R.id.sectioned_card_view);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        card.setTransitionName("transistion_event_info" + position);
                    }
                    RoomManager roomInf = new RoomManager(getContext());
                    ArrayList<DatabaseRow> info = roomInf.getTable();

                    //search for the chosen room in the database, get the picture URL
                    String pic = "";
                    if (data != null && info.size() > 0) {
                        for (DatabaseRow row : info) {
                            Room room = (Room) row;
                            if (room.getRoomId() == data.getId()) {
                                pic = room.getPicUrl();
                                break;
                            }
                        }
                    }
                    Bundle bundle = new Bundle();
                    if (data != null) {
                        bundle.putString(TAG_TITLE, data.getName());
                        bundle.putInt(TAG_ROOM_ID, data.getId());
                    }
                    bundle.putString(TAG_PIC, pic);

                    String cardName = card.getTransitionName();
                    OneRoomFragment nextFrag = new OneRoomFragment();
                    nextFrag.setArguments(bundle);
                    FragmentManager fragmentManager = activity.getSupportFragmentManager();
                    fragmentManager.beginTransaction().addToBackStack(null)
                            .replace(R.id.content_frame, nextFrag)
                            .addSharedElement(card, cardName)
                            .commit();
                }
            }
        });
    }
}