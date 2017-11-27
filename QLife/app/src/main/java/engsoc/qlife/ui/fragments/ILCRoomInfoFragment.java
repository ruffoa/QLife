package engsoc.qlife.ui.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import engsoc.qlife.R;
import engsoc.qlife.database.dibs.GetRooms;
import engsoc.qlife.database.dibs.GetRoomBookings;
import engsoc.qlife.database.local.DatabaseRow;
import engsoc.qlife.database.local.rooms.Room;
import engsoc.qlife.database.local.rooms.RoomManager;
import engsoc.qlife.interfaces.AsyncTaskObserver;
import engsoc.qlife.ui.recyclerview.DataObject;
import engsoc.qlife.ui.recyclerview.SectionedRecyclerView;
import engsoc.qlife.utility.Constants;

public class ILCRoomInfoFragment extends Fragment {
    public static final String TAG_TITLE = "room_title";
    public static final String TAG_PIC = "pic";
    public static final String TAG_MAP = "map";
    public static final String TAG_DESC = "room_description";
    public static final String TAG_ROOM_ID = "room_id";
    public static final String TAG_DATE = "date";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private View mView;
    private String roomAvailability;
    private View mProgressView;
    private ProgressDialog mProgressDialog;

    private ArrayList<DataObject> mRoomData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_ilcroom_info, container, false);

        RoomManager roomInf = new RoomManager(this.getContext());
        ArrayList<DatabaseRow> data = roomInf.getTable();
        if (data == null || data.size() == 0) {
            final Context context = getContext();
            GetRooms dibs = new GetRooms(new AsyncTaskObserver() {
                @Override
                public void onTaskCompleted(Object obj) {
                    //expect null to be passed
                    mProgressDialog.dismiss();
                }

                @Override
                public void beforeTaskStarted() {
                    mProgressDialog = new ProgressDialog(context);
                    mProgressDialog.setMessage("Downloading cloud database. Please wait...");
                    mProgressDialog.setIndeterminate(false);
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.show();
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

        mRecyclerView = mView.findViewById(R.id.ilcRoomInfoRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new SectionedRecyclerView(getDayEventData());
        mRecyclerView.setAdapter(mAdapter);
        mProgressView = mView.findViewById(R.id.ilcRoomInf_progress);

        FloatingActionButton myFab = mView.findViewById(R.id.sortRoomsFab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onFABClick();
            }
        });
        myFab.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                return onFABLongClick();
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

                String map = "";
                String pic = "";

                if (data != null && info.size() > 0) {
                    for (DatabaseRow row : info) {
                        Room room = (Room) row;
                        if (room.getRoomId() == data.getID()) {
                            map = room.getMapUrl();
                            pic = room.getPicUrl();
                            break;
                        }
                    }
                }

                String cardName = card.getTransitionName();
                RoomInformationFragment nextFrag = new RoomInformationFragment();

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

    private void onFABClick() {
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

    private boolean onFABLongClick() {
        mProgressView.setVisibility(View.VISIBLE);

        RoomManager roomInf = new RoomManager(this.getContext());
        ArrayList<DataObject> result = new ArrayList<>();
        ArrayList<DatabaseRow> data = roomInf.getTable();
        Calendar cal = Calendar.getInstance();

        try {
            if (data != null && data.size() > 0) {
                showProgress(true);
                mProgressView.setVisibility(View.VISIBLE);

                for (DatabaseRow row : data) {
                    GetRoomBookings dibs = new GetRoomBookings();
                    Room room = (Room) row;
                    roomAvailability = dibs.execute(room.getRoomId(), cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR)).get();
                    int status = getDayAvailability();
                    if (status == 0) {
                        result.add(new DataObject(room.getName(), "Is Avaliable Now", room.getRoomId(), true, "", room.getDescription()));
                    } else if (status == 2)
                        result.add(new DataObject(room.getName(), "Is Avaliable at " + cal.get(Calendar.HOUR) + ":30", room.getRoomId(), true, "", room.getDescription()));
                    else if (status == 3)
                        result.add(new DataObject(room.getName(), "Is Avaliable Until " + (cal.get(Calendar.HOUR) + 1) + ":30", room.getRoomId(), true, "", room.getDescription()));
                }
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        }

        showProgress(false);

        mAdapter = new SectionedRecyclerView(result);
        mRecyclerView.setAdapter(mAdapter);

        return true;
    }

    public ArrayList<DataObject> getDayEventData() {
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

    public int getDayAvailability() {
        if (roomAvailability != null && roomAvailability.length() > 0) {
            try {
                JSONArray arr = new JSONArray(roomAvailability);
                int state = 0;

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject roomInfo = arr.getJSONObject(i);
                    String start = roomInfo.getString("StartTime");
                    start = start.substring(start.indexOf("T") + 1);

                    int sHour = Integer.parseInt(start.substring(0, 2));
                    Calendar cal = Calendar.getInstance();
                    int now = cal.get(Calendar.HOUR_OF_DAY);
                    int nowMin = cal.get(Calendar.MINUTE);

                    if (sHour == now) {
                        return 1;
                    } else if (sHour == now - 1 && nowMin < 30)
                        state = 2;
                    else if (sHour == now + 1)
                        state = 3;
                }
                return state;
            } catch (JSONException e) {
                Log.d("HELLOTHERE", e.getMessage());
            }
        }
        return 0;
    }
}