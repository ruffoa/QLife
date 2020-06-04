package beta.qlife.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.ListFragment;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import beta.qlife.R;
import beta.qlife.utility.Constants;
import beta.qlife.utility.ShowCloudDBErrorCard;
import beta.qlife.utility.Util;
import beta.qlife.database.local.DatabaseRow;
import beta.qlife.database.local.buildings.Building;
import beta.qlife.database.local.buildings.BuildingManager;
import beta.qlife.database.local.food.Food;
import beta.qlife.database.local.food.FoodManager;
import beta.qlife.interfaces.enforcers.ActionbarFragment;
import beta.qlife.interfaces.enforcers.DrawerItem;
import beta.qlife.interfaces.enforcers.ListFragmentWithChild;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Carson on 26/06/2017.
 * Fragment that displays the buildings in the phone/cloud database. When a building is clicked, it starts
 * OneBuildingFragment that provides details about the building.
 */
public class BuildingsFragment extends ListFragment implements ActionbarFragment, DrawerItem, ListFragmentWithChild {

    public static final String TAG_FOOD_NAMES = "FOOD_NAMES";

    private BuildingManager mBuildingManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        setActionbarTitle();
        inflateListView();
        checkAndShowErrorCardIfRequired(v);
        return v;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        onListItemChosen(v);
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
    public void setActionbarTitle() {
        Util.setActionbarTitle(getString(R.string.fragment_buildings), (AppCompatActivity) getActivity());
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
    public void onListItemChosen(View view) {
        Bundle args = setDataForOneItem(view);

        OneBuildingFragment oneBuildingFragment = new OneBuildingFragment();
        oneBuildingFragment.setArguments(args);
        FragmentActivity activity = getActivity();
        if (activity != null) {
            FragmentManager fm = activity.getSupportFragmentManager();
            fm.beginTransaction().addToBackStack(null).replace(R.id.content_frame, oneBuildingFragment).commit();
        }
    }

    @Override
    public Bundle setDataForOneItem(View view) {
        Activity activity = getActivity();
        if (activity != null) {
            Bundle args = new Bundle();
            FoodManager foodManager = new FoodManager(getActivity().getApplicationContext());
            String sId = ((TextView) view.findViewById(R.id.db_id)).getText().toString();
            Building building = mBuildingManager.getRow(Integer.parseInt(sId));
            ArrayList<Food> food = foodManager.getFoodForBuilding(Integer.parseInt(sId));

            ArrayList<String> foodNames = new ArrayList<>();
            for (Food oneFood : food) {
                foodNames.add(oneFood.getName());
            }

            //deal with special case building names - common short forms for long names
            switch (building.getName()) {
                case Constants.ARC_FULL:
                    args.putString(Building.COLUMN_NAME, Constants.ARC);
                    break;
                case Constants.JDUC_FULL:
                    args.putString(Building.COLUMN_NAME, Constants.JDUC);
                    break;
                default:
                    args.putString(Building.COLUMN_NAME, building.getName());
                    break;
            }

            args.putString(Building.COLUMN_PURPOSE, building.getPurpose());
            args.putBoolean(Building.COLUMN_BOOK_ROOMS, building.getBookRooms());
            args.putBoolean(Building.COLUMN_ATM, building.getAtm());
            args.putDouble(Building.COLUMN_LAT, building.getLat());
            args.putDouble(Building.COLUMN_LON, building.getLon());
            args.putStringArrayList(TAG_FOOD_NAMES, foodNames);
            return args;
        }
        return null;
    }

    @Override
    public void inflateListView() {
        Activity activity = getActivity();
        if (activity != null) {
            mBuildingManager = new BuildingManager(activity.getApplicationContext());
            ArrayList<HashMap<String, String>> buildingsList = new ArrayList<>();

            ArrayList<DatabaseRow> buildings = mBuildingManager.getTable();

            for (DatabaseRow row : buildings) {
                    Building building = (Building) row;
                    HashMap<String, String> map = new HashMap<>();
                    map.put(Building.COLUMN_NAME, building.getName());
                    map.put(Building.COLUMN_PURPOSE, building.getPurpose());
                    String food = building.getFood() ? "Yes" : "No";
                    map.put(Building.COLUMN_FOOD, food);
                    map.put(FoodFragment.TAG_DB_ID, String.valueOf(building.getId()));
                    buildingsList.add(map);
                }
                ListAdapter adapter = new SimpleAdapter(activity.getApplicationContext(), buildingsList,
                        R.layout.buildings_list_item, new String[]{Building.COLUMN_NAME, Building.COLUMN_PURPOSE, Building.COLUMN_FOOD, FoodFragment.TAG_DB_ID},
                        new int[]{R.id.name, R.id.purpose, R.id.food, R.id.db_id});
                setListAdapter(adapter);
        }
    }

    void checkAndShowErrorCardIfRequired(View v) {
        Activity activity = getActivity();
        if (activity != null) {
            mBuildingManager = new BuildingManager(activity.getApplicationContext());
            ArrayList<DatabaseRow> buildings = mBuildingManager.getTable();
            Log.d("BUILDING TABLE CHECK", "Building table is: " + buildings.size() + " isEmpty? " + buildings.isEmpty());

            if (buildings.isEmpty()) {
                ShowCloudDBErrorCard cloudDBErrorCard = new ShowCloudDBErrorCard();
                cloudDBErrorCard.showCloudDBErrorCard(v, "buildings", activity);
            }
        }
    }
}
