package engsoc.qlife.ui.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import engsoc.qlife.R;
import engsoc.qlife.activities.MainTabActivity;
import engsoc.qlife.database.local.DatabaseRow;
import engsoc.qlife.database.local.courses.Course.Course;
import engsoc.qlife.database.local.courses.Course.CourseManager;
import engsoc.qlife.database.local.courses.OneClass.OneClass;
import engsoc.qlife.database.local.courses.OneClass.OneClassManager;
import engsoc.qlife.interfaces.enforcers.ActionbarFragment;
import engsoc.qlife.interfaces.enforcers.DrawerItem;
import engsoc.qlife.interfaces.enforcers.ListFragmentWithChild;
import engsoc.qlife.ui.recyclerview.list_objects.DataObject;
import engsoc.qlife.ui.recyclerview.list_objects.DayObject;
import engsoc.qlife.ui.recyclerview.recyler_adapters.DayAdapter;
import engsoc.qlife.ui.recyclerview.recyler_adapters.RecyclerViewAdapter;
import engsoc.qlife.utility.DayFragmentPosition;
import engsoc.qlife.utility.Util;

/**
 * Fragment that displays the classes for a given day. When a class is clicked, it starts
 * EventInfoFragment that provides details about the class.
 */
public class DayFragment extends Fragment implements ActionbarFragment, DrawerItem, ListFragmentWithChild {

    public static final String TAG_CODE = "event_code";
    public static final String TAG_NAME = "event_name";
    public static final String TAG_DATE = "date";
    public static final String TAG_LOC = "event_location";

    private DayFragmentPosition mPosition;
    private int mTotalDaysChange = 0;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private View mView;
    private TextView mDateText;
    private String mDateString;
    private Calendar mCalendar;
    private ArrayList<DayObject> mResult = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (inflater != null) {
            mView = inflater.inflate(R.layout.fragment_day, container, false);
        }
        setActionbarTitle();

        mDateText = mView.findViewById(R.id.date);
        mCalendar = Calendar.getInstance();
        inflateListView();
        onListItemChosen(null); //this is special case that doesn't need view - RecyclerView not ListView here
        return mView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPosition = new DayFragmentPosition(getContext());
        mPosition.addInstance();
    }

    @Override
    public void onPause() {
        super.onPause();
        MainTabActivity activity = (MainTabActivity) getActivity();
        if (activity != null && !activity.isToActivity()) {
            mPosition.changeInstance(mTotalDaysChange);
        } else {
            mPosition.changeInstance(0);
        }
        deselectDrawer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPosition.removeInstance();
        mPosition.homeStopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        selectDrawer();
        mPosition.homeStartListening();
        changeDate(mPosition.getInstanceChange()); //account for day changed before moved fragments
    }

    /**
     * Helper method that sets the date of the day fragment. Used to adjust the shown day
     * when the user comes back to this fragment from another one, after previously changing the day.
     *
     * @param numChange The number of days to change by. Negative numbers means go backwards in time.
     */
    public void changeDate(int numChange) {
        mResult.clear();
        mCalendar.add(Calendar.DAY_OF_YEAR, numChange);
        mAdapter = new DayAdapter(getDayEventData(mCalendar));
        mRecyclerView.setAdapter(mAdapter);
    }

    public ArrayList<DayObject> getDayEventData(Calendar calendar) {
        TextView noClassMessage = mView.findViewById(R.id.no_class_message);
        noClassMessage.setVisibility(View.GONE); //updates day view when go to new day - may have class
        OneClassManager oneClassManager = new OneClassManager(this.getContext());
        CourseManager courseManager = new CourseManager(this.getContext());

        List<String> list = new ArrayList<>();
        List<String> detailsList = new ArrayList<>();
        List<String> loc = new ArrayList<>();
        List<String> time = new ArrayList<>();
        List<Long> classID = new ArrayList<>();
        List<Boolean> hasName = new ArrayList<>();
        ArrayList<DatabaseRow> data = oneClassManager.getTable();

        int day, month, year;
        boolean eventsToday = false;

        int calDay = calendar.get(Calendar.DAY_OF_MONTH);
        int calMon = calendar.get(Calendar.MONTH) + 1;
        int calYear = calendar.get(Calendar.YEAR);
        CharSequence date = DateFormat.format("EEEE, MMMM d, yyyy", mCalendar.getTime());
        mDateString = date.toString();
        mDateText.setText(date);

        //look for selected day of event in database
        for (int i = 0; i < data.size(); i++) {
            OneClass oneClass = (OneClass) data.get(i);
            day = Integer.parseInt(oneClass.getDay());
            month = Integer.parseInt(oneClass.getMonth());
            year = Integer.parseInt(oneClass.getYear());

            if (year == calYear && month == calMon && calDay == day) { // if the day matches add the event
                loc.add(oneClass.getRoomNum());
                time.add(oneClass.getStartTime() + "-" + oneClass.getEndTime());
                classID.add(oneClass.getCourseID());
                DatabaseRow courseRow = courseManager.getRow(oneClass.getCourseID());
                Course course = (Course) courseRow;
                hasName.add(course.isSetName());
                list.add(course.getCode());
                detailsList.add(course.getName());
                eventsToday = true;
            }
        }

        if (!eventsToday) {
            noClassMessage.setVisibility(View.VISIBLE);
            return mResult;
        }

        int startHour;
        int startMin;
        int posSmall = 0;
        int minHour;
        int minMin;
        int endHour = 0;
        int endMin = 0;
        for (int i = 0; i < list.size(); i++) {
            minHour = 25;
            minMin = 61;

            for (int j = 0; j < list.size(); j++) {
                String s = time.get(j);
                String s1 = s.substring(0, s.indexOf("-"));
                int div = s1.indexOf(":");
                String shour = s1.substring(0, div);
                String smin = s1.substring(div + 1, s1.length());

                int index = s.indexOf("-") + 1;
                String s2 = s.substring(index, s.length());
                div = s2.indexOf(":");

                startHour = Integer.parseInt(shour);
                startMin = Integer.parseInt(smin);
                if (startHour < minHour) {
                    posSmall = j;
                    minHour = startHour;
                    minMin = startMin;
                    endHour = Integer.parseInt(s2.substring(0, div));
                    endMin = Integer.parseInt(s2.substring(div + 1, s2.length()));

                } else if (startHour == minHour) {
                    if (startMin < minMin) {
                        posSmall = j;
                        minHour = startHour;
                        minMin = startMin;
                        endHour = Integer.parseInt(s2.substring(0, div));
                        endMin = Integer.parseInt(s2.substring(div + 1, s2.length()));

                    }
                }
            }
            String amPMTime;
            if (minHour > 12)
                amPMTime = (minHour - 12) + ":" + minMin + "-" + (endHour - 12) + ":" + endMin + " PM";
            else if (minHour < 12 && endHour >= 12)
                if (endHour == 12)
                    amPMTime = (minHour) + ":" + minMin + " AM-" + (endHour) + ":" + endMin + " PM";
                else
                    amPMTime = (minHour) + ":" + minMin + " AM-" + (endHour - 12) + ":" + endMin + " PM";
            else if (endHour > 12)
                amPMTime = (minHour) + ":" + minMin + "-" + (endHour - 12) + ":" + endMin + " PM";
            else amPMTime = time.get(posSmall) + " AM";

            mResult.add(new DayObject(list.get(posSmall), amPMTime + " at: " + loc.get(posSmall), classID.get(posSmall), hasName.get(posSmall), detailsList.get(posSmall)));
            list.remove(posSmall);
            time.remove(posSmall);
            loc.remove(posSmall);
            classID.remove(posSmall);
            hasName.remove(posSmall);
            detailsList.remove(posSmall);
            i = -1;
        }
        if (list.size() > 0) {
            mResult.add(new DayObject(list.get(0), time.get(0) + " at: " + loc.get(0) + " description: " + list.get(0),
                    classID.get(posSmall), hasName.get(posSmall), detailsList.get(0)));
        }
        return mResult;
    }

    @Override
    public void setActionbarTitle() {
        Util.setActionbarTitle(getString(R.string.fragment_day), (AppCompatActivity) getActivity());
    }

    @Override
    public void deselectDrawer() {
        Util.setDrawerItemSelected(getActivity(), R.id.nav_day, false);
    }

    @Override
    public void selectDrawer() {
        Util.setDrawerItemSelected(getActivity(), R.id.nav_day, true);
    }

    @Override
    public void inflateListView() {
        if (getArguments() != null && getArguments().getString(MonthFragment.TAG_FROM_MONTH, "").equals(MonthFragment.TAG_FROM_MONTH)) {
            mCalendar.set(Calendar.DAY_OF_MONTH, getArguments().getInt(MonthFragment.TAG_DAY));
            mCalendar.set(Calendar.MONTH, getArguments().getInt(MonthFragment.TAG_MONTH));
            mCalendar.set(Calendar.YEAR, getArguments().getInt(MonthFragment.TAG_YEAR));
        }

        mRecyclerView = mView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new DayAdapter(getDayEventData(mCalendar));
        mRecyclerView.setAdapter(mAdapter);

        Button nextButton = mView.findViewById(R.id.next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeDate(1);
                mTotalDaysChange += 1;
            }
        });
        Button prevButton = mView.findViewById(R.id.prev);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeDate(-1);
                mTotalDaysChange += -1;
            }
        });
        Button todayButton = mView.findViewById(R.id.today);
        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mResult.clear();
                mCalendar = Calendar.getInstance(); //current date
                mAdapter = new DayAdapter(getDayEventData(mCalendar));
                mRecyclerView.setAdapter(mAdapter);
                mTotalDaysChange = 0;
            }
        });
    }

    @Override
    public void onListItemChosen(View view) {
        ((RecyclerViewAdapter) mAdapter).setOnItemClickListener(new RecyclerViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                FragmentActivity activity = getActivity();
                if (activity != null) {
                    DataObject dataObj = ((RecyclerViewAdapter) mAdapter).getItem(position);
                    if (dataObj instanceof DayObject) {
                        DayObject data = (DayObject) dataObj;
                        CardView card = mView.findViewById(R.id.card_view);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            card.setTransitionName("transistion_event_info" + position);
                        }

                        String cardName = card.getTransitionName();
                        EventInfoFragment nextFrag = new EventInfoFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(TAG_CODE, data.getClassCode());
                        bundle.putString(TAG_NAME, data.getName());
                        bundle.putString(TAG_LOC, data.getWhere());
                        bundle.putString(TAG_DATE, mDateString);
                        nextFrag.setArguments(bundle);

                        FragmentManager fragmentManager = activity.getSupportFragmentManager();
                        fragmentManager.beginTransaction().addToBackStack(null)
                                .replace(R.id.content_frame, nextFrag)
                                .addSharedElement(card, cardName)
                                .commit();
                    }
                }
            }
        });
    }

    @Override
    public Bundle setDataForOneItem(View view) {
        return null; //RecyclerView special case, others ListFragment
    }
}