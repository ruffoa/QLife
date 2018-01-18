package engsoc.qlife.ui.fragments;

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
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import engsoc.qlife.R;
import engsoc.qlife.activities.MapsActivity;
import engsoc.qlife.database.local.buildings.Building;
import engsoc.qlife.interfaces.IQLActionbarFragment;
import engsoc.qlife.interfaces.IQLDrawerItem;
import engsoc.qlife.interfaces.IQLListItemDetailsFragment;
import engsoc.qlife.interfaces.IQLMapView;
import engsoc.qlife.utility.CallableObj;
import engsoc.qlife.utility.HandlePermissions;
import engsoc.qlife.utility.Util;

/**
 * Created by Carson on 25/07/2017.
 * Fragment that shows details of one building from the list view
 */
public class OneBuildingFragment extends Fragment implements IQLActionbarFragment, IQLDrawerItem, IQLListItemDetailsFragment, IQLMapView {

    private Bundle mArgs;
    private View mView;
    private GoogleMap mGoogleMap;
    private Bundle mSavedInstanceState;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_one_building, container, false);
        mArgs = getArguments();
        setActionbarTitle();
        mSavedInstanceState = savedInstanceState;
        setMapView();
        addDataToViews();
        return mView;
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MapsActivity.REQUEST_LOCATION_PERMISSIONS) {
            onRequestLocationPermissionsResult();
        }
    }

    @Override
    public void setActionbarTitle() {
        Util.setActionbarTitle(mArgs.getString(Building.COLUMN_NAME), (AppCompatActivity) getActivity());
    }

    @Override
    public void deselectDrawer() {
        Util.setDrawerItemSelected(getActivity(), R.id.nav_buildings, false);
    }

    @Override
    public void selectDrawer() {
        Util.setDrawerItemSelected(getActivity(), R.id.nav_buildings, true);
    }

    @Override
    public void addDataToViews() {
        ArrayList<String> foodNames = mArgs.getStringArrayList(BuildingsFragment.TAG_FOOD_NAMES);
        StringBuilder foodBuilder = new StringBuilder();
        String foods = "";
        if (foodNames != null && !foodNames.isEmpty()) {
            for (String oneFood : foodNames) {
                foodBuilder.append(oneFood).append('\n');
            }
            foods = foodBuilder.toString().trim();//remove last \n
        } else {
            mView.findViewById(R.id.food_title).setVisibility(View.GONE);
            mView.findViewById(R.id.food).setVisibility(View.GONE);

        }
        ((TextView) mView.findViewById(R.id.food)).setText(foods);
        ((TextView) mView.findViewById(R.id.purpose)).setText(mArgs.getString(Building.COLUMN_PURPOSE));
        ((TextView) mView.findViewById(R.id.atm)).setText(mArgs.getBoolean(Building.COLUMN_ATM) ? "Yes" : "No");
        ((TextView) mView.findViewById(R.id.book_rooms)).setText(mArgs.getBoolean(Building.COLUMN_BOOK_ROOMS) ? "Yes" : "No");
    }

    @Override
    public void setMapView() {
        MapView mapView = mView.findViewById(R.id.map);
        Util.initMapView(mapView, mSavedInstanceState, getActivity(), new CallableObj<Void>() {
            @Override
            public Void call(Object obj) {
                if (obj instanceof GoogleMap) {
                    mGoogleMap = (GoogleMap) obj;
                    LatLng buildingInfo = new LatLng(mArgs.getDouble(Building.COLUMN_LAT), mArgs.getDouble(Building.COLUMN_LON));
                    mGoogleMap.addMarker(new MarkerOptions().position(buildingInfo).title(mArgs.getString(Building.COLUMN_NAME))).showInfoWindow();

                    //For zooming automatically to the location of the marker
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(buildingInfo).zoom(15).build();
                    mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
}
