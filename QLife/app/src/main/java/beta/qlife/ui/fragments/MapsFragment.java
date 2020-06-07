package beta.qlife.ui.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import beta.qlife.R;
import beta.qlife.database.local.DatabaseRow;
import beta.qlife.database.local.buildings.Building;
import beta.qlife.database.local.buildings.BuildingManager;
import beta.qlife.interfaces.enforcers.ActionbarFragment;
import beta.qlife.interfaces.enforcers.DrawerItem;
import beta.qlife.interfaces.enforcers.MapView;
import beta.qlife.interfaces.observers.CallableObj;
import beta.qlife.utility.HandlePermissions;
import beta.qlife.utility.Util;

public class MapsFragment extends Fragment implements ActionbarFragment, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, MapView, DrawerItem {

    public static final int REQUEST_LOCATION_PERMISSIONS = 1;
    private GoogleMap mGoogleMap;
    private View myView;
    private Bundle mSavedInstanceState;
    private com.google.android.gms.maps.MapView mMapView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_maps, container, false);
        setActionbarTitle();
        mSavedInstanceState = savedInstanceState;
        setMapView();
        return myView;
    }

    @Override
    public void setMapView() {
        mMapView = myView.findViewById(R.id.map);
        Util.initMapView(mMapView, mSavedInstanceState, getActivity(), new CallableObj<Void>() {
            @Override
            public Void call(Object obj) {
                mGoogleMap = (GoogleMap) obj;

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestLocationPermissions();
                } else {
                    mGoogleMap.setMyLocationEnabled(true);
                }
                createMarkers();
                //move map to ILC area
                CameraPosition campusPosition = new CameraPosition.Builder().target(new LatLng(44.225743, -76.495610)).zoom(15.5f).build();
                mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(campusPosition));

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
        HandlePermissions.onLocationPermissionsGiven(getActivity(), mGoogleMap);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSIONS) {
            onRequestLocationPermissionsResult();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        //need permission to allow user to go to their location - check if already have it
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissions();
        } else {
            mGoogleMap.setMyLocationEnabled(true);
        }

        createMarkers();
        //move map to ILC area
        CameraPosition campusPosition = new CameraPosition.Builder().target(new LatLng(44.225743, -76.495610)).zoom(15.5f).build();
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(campusPosition));
//        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));  // disabled as the animation is sort of overkill / unnecessary :p
    }

    /**
     * Method that uses the Buildings table in the phone database to set markers on the Google map.
     * Each building gets a marker.
     */
    private void createMarkers() {
        ArrayList<DatabaseRow> buildings = new BuildingManager(getActivity()).getTable();
        for (DatabaseRow row : buildings) {
            Building building = (Building) row;
            MarkerOptions marker = new MarkerOptions().position(new LatLng(building.getLat(), building.getLon())).title(building.getName());
            mGoogleMap.addMarker(marker);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void setActionbarTitle() {
        Util.setActionbarTitle(getString(R.string.map_fragment_title), (AppCompatActivity) getActivity());
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
    public void deselectDrawer() {
        Util.setDrawerItemSelected(getActivity(), R.id.nav_map, false);
    }

    @Override
    public void selectDrawer() {
        Util.setDrawerItemSelected(getActivity(), R.id.nav_map, true);
    }


}
