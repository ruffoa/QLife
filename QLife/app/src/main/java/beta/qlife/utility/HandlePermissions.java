package beta.qlife.utility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;
import java.util.List;

import beta.qlife.ui.fragments.MapsFragment;

/**
 * Created by Carson on 13/08/2017.
 * Class used to handle any permission request.
 * Not confined to Android permissions, for example could handle getting student number permissions.
 */
public class HandlePermissions {

    public static void requestLocationPermissions(Activity activity) {
        if (activity != null) {
            int coarsePermission = ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
            int finePermission = ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
            List<String> listPermissionsNeeded = new ArrayList<>();

            if (finePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (coarsePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(activity,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                        MapsFragment.REQUEST_LOCATION_PERMISSIONS);
            }
        }
    }

    /**
     * Callback for when location permissions given for a MapView.
     *
     * @param context The context where location permissions are requested.
     * @param map     The GoogleMap used where location is requested.
     */
    public static void onLocationPermissionsGiven(Context context, GoogleMap map) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }
    }
}
