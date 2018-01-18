package engsoc.qlife.utility;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;

import engsoc.qlife.R;

/**
 * Created by Carson on 01/08/2017.
 * Class for common methods. All are short and static.
 */
public class Util {
    public static void initMapView(final MapView mapView, Bundle savedInstanceState,
                                   final Activity activity, final CallableObj<Void> callback) {
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        try {
            MapsInitializer.initialize(activity.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(activity.getApplicationContext(),
                                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    HandlePermissions.requestLocationPermissions(activity);
                } else {
                    googleMap.setMyLocationEnabled(true);
                }
                try {
                    callback.call(googleMap);
                } catch (Exception e) {
                    googleMap.clear();
                    mapView.setVisibility(View.GONE);
                }
            }
        });
    }

    public static void setActionbarTitle(String title, AppCompatActivity activity) {
        ActionBar actionbar = activity.getSupportActionBar();
        if (actionbar != null) {
            actionbar.setTitle(title);
        }
    }

    /**
     * Helper method that will set if a drawer item is set as checked or not.
     *
     * @param activity  The activity holding the drawer.
     * @param itemId    The ID of the drawer item to change.
     * @param isChecked Boolean flag, true checks the item, false un-checks the item.
     */
    public static void setDrawerItemSelected(Activity activity, int itemId, boolean isChecked) {
        NavigationView navView = activity.findViewById(R.id.drawer_layout).findViewById(R.id.nav_view);
        navView.getMenu().findItem(itemId).setChecked(isChecked);
    }

    /**
     * Helper method that sets the back button to be displayed in an action bar.
     *
     * @param actionBar The actionbar that will have the back button displayed.
     */
    public static void setBackButton(ActionBar actionBar) {
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static void inflateOptionsMenu(int menuId, Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(menuId, menu);
    }

    /**
     * Helper method that gets the hours between two given times.
     *
     * @param startHour The starting time.
     * @param endHour   The ending time.
     * @return A String that says "start time 'to' end time".
     */
    public static String getHours(double startHour, double endHour) {
        //check for closed all day flag
        if (startHour < 0) {
            return "Closed";
        }

        String start = getOneTimeBoundary(startHour);
        String end = getOneTimeBoundary(endHour);
        return start + " to " + end;
    }

    /**
     * Helper method to getHours() that turns one time into h:mm format.
     *
     * @param hour The time to convert.
     * @return String format of the time in h:mm am/pm format.
     */
    private static String getOneTimeBoundary(double hour) {
        String sHour = "";
        if (hour < 1 || hour >= 13) { //24 hour time
            sHour += String.valueOf((int) hour - 12);
        } else {
            sHour += String.valueOf((int) hour);
        }

        double min = (hour - (int) hour) * 60; //convert 0.5, 0.75 to 30, 45 min
        sHour += ":" + String.valueOf((int) min);
        if (min == 0) {
            sHour += "0";
        }

        if (hour < 12) {
            sHour += " am";
        } else {
            sHour += " pm";
        }
        return sHour;
    }
}
