package engsoc.qlife.interfaces;

/**
 * Created by Carson on 29/07/2017.
 * Interface for fragment or activity that uses a MapView.
 */

public interface IQLMapView {

    /**
     * Method that will initialize and set the MapView/GoogleMap.
     * Should call Util.initMapView() and pass the unique code to be done when the map
     * is ready using a CallableObj callback.
     * Should be called from onCreateView() after the view in inflated.
     */
    void setMapView();

    /**
     * Method that will request location permissions in order to allow
     * the user to go to their current location in a Google map.
     * <p>
     * Should call HandlePermissions.requestLocationPermissions().
     */
    void requestLocationPermissions();

    /**
     * Method that will handle logic after the user has responded to a
     * permissions request.
     * <p>
     * Should be called from onRequestPermissionResult() after checking
     * this invocation is from a location permissions request.
     * <p>
     * Should call HandlePermissions.onLocationPermissionsGiven().
     */
    void onRequestLocationPermissionsResult();
}
