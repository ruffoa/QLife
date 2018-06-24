package beta.qlife.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import beta.qlife.R;
import beta.qlife.activities.MapsActivity;
import beta.qlife.database.local.buildings.Building;
import beta.qlife.database.local.buildings.BuildingManager;
import beta.qlife.interfaces.enforcers.ActionbarFragment;
import beta.qlife.interfaces.enforcers.DrawerItem;
import beta.qlife.interfaces.enforcers.ListItemDetailsFragment;
import beta.qlife.interfaces.enforcers.MapView;
import beta.qlife.interfaces.observers.CallableObj;
import beta.qlife.utility.HandlePermissions;
import beta.qlife.utility.Util;

public class EventInfoFragment extends Fragment implements ActionbarFragment, DrawerItem, MapView, ListItemDetailsFragment {

    private String mEventTitle, mEventLoc, mDate, mDetails;
    private Bundle mSavedInstanceState;
    private View myView;
    private com.google.android.gms.maps.MapView mMapView;
    private GoogleMap mGoogleMap;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_event_info, container, false);
        setActionbarTitle();
        addDataToViews();
        mSavedInstanceState = savedInstanceState;
        setMapView();
        return myView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        myView.setFocusableInTouchMode(true);
        myView.requestFocus();
        selectDrawer();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        deselectDrawer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MapsActivity.REQUEST_LOCATION_PERMISSIONS) {
            onRequestLocationPermissionsResult();
        }
    }

    @Override
    public void setActionbarTitle() {
        Util.setActionbarTitle(getString(R.string.fragment_event_info), (AppCompatActivity) getActivity());
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
    public void setMapView() {
        mMapView = myView.findViewById(R.id.event_map);
        Util.initMapView(mMapView, mSavedInstanceState, getActivity(), new CallableObj<Void>() {
            @Override
            public Void call(Object obj) {
                mGoogleMap = (GoogleMap) obj;
                String icsBuilding = mEventLoc.substring(mEventLoc.indexOf("at:") + 4, mEventLoc.length());
                try {
                    Building building = new BuildingManager(getContext()).getIcsBuilding(icsBuilding.substring(0, 4));
                    if (building != null) {
                        LatLng pos = new LatLng(building.getLat(), building.getLon());
                        mGoogleMap.addMarker(new MarkerOptions().position(pos).
                                title(building.getName())).showInfoWindow();

                        //For zooming automatically to the location of the marker
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(pos).zoom(16).build();
                        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                } catch (Exception e) {
                    //online or TBA class most likely, but for any error with map don't show it
                    mGoogleMap.clear();
                    mMapView.setVisibility(View.GONE);
                }
                return null;
            }
        });
    }

    @Override
    public void requestLocationPermissions() {
        HandlePermissions.requestLocationPermissions(getActivity());
    }

    @Override
    public void onRequestLocationPermissionsResult() {
        HandlePermissions.onLocationPermissionsGiven(getContext(), mGoogleMap);
    }

    @Override
    public void addDataToViews() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mEventTitle = bundle.getString(DayFragment.TAG_CODE);
            mDetails = bundle.getString(DayFragment.TAG_NAME);
            mEventLoc = bundle.getString(DayFragment.TAG_LOC);
            mDate = bundle.getString(DayFragment.TAG_DATE);
        }

        TextView eventDate = myView.findViewById(R.id.EventDate);
        eventDate.setText(mDate);
        TextView eventLoc = myView.findViewById(R.id.EventLoc);
        eventLoc.setText(mEventLoc);
        TextView eventName = myView.findViewById(R.id.EventName);
        eventName.setText(mEventTitle);
        TextView eventDetails = myView.findViewById(R.id.EventDetails);
        if (mDetails != null) {
            eventDetails.setText(mDetails);
        } else {
            eventDetails.setVisibility(View.GONE);
        }
    }
}
