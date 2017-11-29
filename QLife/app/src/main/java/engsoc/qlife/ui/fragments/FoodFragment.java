package engsoc.qlife.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import engsoc.qlife.R;
import engsoc.qlife.utility.Util;
import engsoc.qlife.database.local.DatabaseRow;
import engsoc.qlife.database.local.buildings.Building;
import engsoc.qlife.database.local.buildings.BuildingManager;
import engsoc.qlife.database.local.food.Food;
import engsoc.qlife.database.local.food.FoodManager;
import engsoc.qlife.interfaces.IQLActionbarFragment;
import engsoc.qlife.interfaces.IQLDrawerItem;
import engsoc.qlife.interfaces.IQLListFragmentWithChild;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Carson on 05/07/2017.
 * Fragment displaying data in phone database regarding food establishments. When a food place is clicked, it starts
 * OneFoodFragment that provides details about the food place.
 */
public class FoodFragment extends ListFragment implements IQLActionbarFragment, IQLDrawerItem, IQLListFragmentWithChild {
    public static final String TAG_DB_ID = "DB_ID";
    public static final String TAG_BUILDING_NAME = "BUILDING_NAME";

    private FoodManager mFoodManager;
    private BuildingManager mBuildingManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        setActionbarTitle();

        mFoodManager = new FoodManager(getActivity().getApplicationContext());
        inflateListView();
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
        Util.setActionbarTitle(getString(R.string.fragment_food), (AppCompatActivity) getActivity());
    }

    @Override
    public void deselectDrawer() {
        Util.setDrawerItemSelected(getActivity(), R.id.nav_food, false);
    }

    @Override
    public void selectDrawer() {
        Util.setDrawerItemSelected(getActivity(), R.id.nav_food, true);
    }

    @Override
    public void inflateListView() {
        ArrayList<HashMap<String, String>> foodList = new ArrayList<>();
        ArrayList<DatabaseRow> food = mFoodManager.getTable();
        mBuildingManager = new BuildingManager(getContext());
        for (DatabaseRow row : food) {
            foodList.add(packFoodMap(row));
        }

        ListAdapter adapter = new SimpleAdapter(getActivity().getApplicationContext(), foodList,
                R.layout.food_list_item, new String[]{Food.COLUMN_NAME, TAG_BUILDING_NAME, Food.COLUMN_MEAL_PLAN, Food.COLUMN_CARD, TAG_DB_ID, Food.COLUMN_BUILDING_ID},
                new int[]{R.id.name, R.id.building, R.id.meal_plan, R.id.card, R.id.db_id, R.id.building_db_id});
        setListAdapter(adapter);
    }

    @Override
    public void onListItemChosen(View view) {
        Bundle args = setDataForOneItem(view);
        OneFoodFragment oneFoodFragment = new OneFoodFragment();
        oneFoodFragment.setArguments(args);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.beginTransaction().addToBackStack(null).replace(R.id.content_frame, oneFoodFragment).commit();
    }

    @Override
    public Bundle setDataForOneItem(View view) {
        Bundle args = new Bundle();
        String foodId = ((TextView) view.findViewById(R.id.db_id)).getText().toString();
        Food food = mFoodManager.getRow(Integer.parseInt(foodId));
        String buildingId = ((TextView) view.findViewById(R.id.building_db_id)).getText().toString();
        Building building = mBuildingManager.getRow(Integer.parseInt(buildingId));
        String buildingName = ((TextView) view.findViewById(R.id.building)).getText().toString();

        //deal with special case food names - common short forms for long names
        switch (food.getName()) {
            case "Common Ground Coffeehouse":
                args.putString(Food.COLUMN_NAME, "CoGro");
                break;
            case "The Canadian Grilling Company":
                args.putString(Food.COLUMN_NAME, "CGC");
                break;
            default:
                args.putString(Food.COLUMN_NAME, food.getName());
                break;
        }

        args.putBoolean(Food.COLUMN_MEAL_PLAN, food.isMealPlan());
        args.putBoolean(Food.COLUMN_CARD, food.isCard());
        args.putString(Food.COLUMN_INFORMATION, food.getInformation());

        //no building ID so no DB access in details fragment
        args.putString(TAG_BUILDING_NAME, buildingName);
        args.putDouble(Building.COLUMN_LAT, building.getLat());
        args.putDouble(Building.COLUMN_LON, building.getLon());

        args.putDouble(Food.COLUMN_MON_START_HOURS, food.getMonStartHours());
        args.putDouble(Food.COLUMN_MON_STOP_HOURS, food.getMonStopHours());
        args.putDouble(Food.COLUMN_TUE_START_HOURS, food.getTueStartHours());
        args.putDouble(Food.COLUMN_TUE_STOP_HOURS, food.getTueStopHours());
        args.putDouble(Food.COLUMN_WED_START_HOURS, food.getWedStartHours());
        args.putDouble(Food.COLUMN_WED_STOP_HOURS, food.getWedStopHours());
        args.putDouble(Food.COLUMN_THUR_START_HOURS, food.getThurStartHours());
        args.putDouble(Food.COLUMN_THUR_STOP_HOURS, food.getThurStopHours());
        args.putDouble(Food.COLUMN_FRI_START_HOURS, food.getFriStartHours());
        args.putDouble(Food.COLUMN_FRI_STOP_HOURS, food.getFriStopHours());
        args.putDouble(Food.COLUMN_SAT_START_HOURS, food.getSatStartHours());
        args.putDouble(Food.COLUMN_SAT_STOP_HOURS, food.getSatStopHours());
        args.putDouble(Food.COLUMN_SUN_START_HOURS, food.getSunStartHours());
        args.putDouble(Food.COLUMN_SUN_STOP_HOURS, food.getSunStopHours());
        return args;
    }

    /**
     * Helper method that packs a hash-map with key and value for a piece of Food information.
     *
     * @param row The Food information to pack for.
     */
    private HashMap<String, String> packFoodMap(DatabaseRow row) {
        Food oneFood = (Food) row;
        HashMap<String, String> map = new HashMap<>();
        map.put(Food.COLUMN_NAME, oneFood.getName());
        map.put(Food.COLUMN_BUILDING_ID, String.valueOf(oneFood.getBuildingID()));
        map.put(TAG_BUILDING_NAME, mBuildingManager.getRow(oneFood.getBuildingID()).getName()); //Building.COLUMN_NAME is the same as Food's
        map.put(Food.COLUMN_MEAL_PLAN, oneFood.isMealPlan() ? "Yes" : "No");
        map.put(Food.COLUMN_CARD, oneFood.isCard() ? "Yes" : "No");
        map.put(TAG_DB_ID, String.valueOf(oneFood.getId()));
        return map;
    }
}
