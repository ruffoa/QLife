package engsoc.qlife.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import engsoc.qlife.R;
import engsoc.qlife.utility.Util;
import engsoc.qlife.interfaces.IQLActionbarFragment;
import engsoc.qlife.interfaces.IQLDrawerItem;

import java.util.Calendar;

/**
 * Created by Carson on 02/12/2016.
 * Fragment used to control UI of the calendar.
 * Other scheduling information could be added to other parts of the screen.
 * This is the first screen user sees upon logging in (unless first time login).
 * Attached to MainTabActivity only.
 */
public class MonthFragment extends Fragment implements IQLActionbarFragment, IQLDrawerItem {

    public static final String TAG_DAY = "day";
    public static final String TAG_MONTH = "month";
    public static final String TAG_YEAR = "year";
    public static final String TAG_FROM_MONTH = "from_month";

    private DatePicker mDatePicker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_month, container, false);
        setActionbarTitle();

        mDatePicker = v.findViewById(R.id.datePicker);
        return v;
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        mDatePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        getCalData();
                    }
                }
        );
    }

    public void getCalData() {
        DayFragment nextFrag = new DayFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TAG_FROM_MONTH, TAG_FROM_MONTH); //tell day fragment bundle is from month fragment
        bundle.putInt(TAG_DAY, mDatePicker.getDayOfMonth());
        bundle.putInt(TAG_MONTH, mDatePicker.getMonth());
        bundle.putInt(TAG_YEAR, mDatePicker.getYear());
        nextFrag.setArguments(bundle);
        this.getFragmentManager().beginTransaction().addToBackStack(null)
                .replace(R.id.content_frame, nextFrag)
                .commit();
    }

    @Override
    public void setActionbarTitle() {
        Util.setActionbarTitle(getString(R.string.fragment_month), (AppCompatActivity) getActivity());
    }

    @Override
    public void deselectDrawer() {
        Util.setDrawerItemSelected(getActivity(), R.id.nav_month, false);
    }

    @Override
    public void selectDrawer() {
        Util.setDrawerItemSelected(getActivity(), R.id.nav_month, true);
    }
}
