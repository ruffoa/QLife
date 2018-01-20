package engsoc.qlife.database.local.food;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import engsoc.qlife.database.local.DatabaseManager;
import engsoc.qlife.database.local.DatabaseRow;

/**
 * Created by Carson on 04/07/2017.
 * Class that handles rows in phone database for Food table.
 */
public class FoodManager extends DatabaseManager {
    public FoodManager(Context context) {
        super(context);
    }

    @Override
    public void insertRow(DatabaseRow row) {
        if (row instanceof Food) {
            Food food = (Food) row;

            ContentValues values = new ContentValues();
            values.put(Food.ID, food.getId());
            values.put(Food.COLUMN_NAME, food.getName());
            values.put(Food.COLUMN_MEAL_PLAN, food.isMealPlan());
            values.put(Food.COLUMN_CARD, food.isCard());
            values.put(Food.COLUMN_INFORMATION, food.getInformation());
            values.put(Food.COLUMN_BUILDING_ID, food.getBuildingID());
            values.put(Food.COLUMN_MON_START_HOURS, food.getMonStartHours());
            values.put(Food.COLUMN_MON_STOP_HOURS, food.getMonStopHours());
            values.put(Food.COLUMN_TUE_START_HOURS, food.getTueStartHours());
            values.put(Food.COLUMN_TUE_STOP_HOURS, food.getTueStopHours());
            values.put(Food.COLUMN_WED_START_HOURS, food.getWedStartHours());
            values.put(Food.COLUMN_WED_STOP_HOURS, food.getWedStopHours());
            values.put(Food.COLUMN_THUR_START_HOURS, food.getThurStartHours());
            values.put(Food.COLUMN_THUR_STOP_HOURS, food.getThurStopHours());
            values.put(Food.COLUMN_FRI_START_HOURS, food.getFriStartHours());
            values.put(Food.COLUMN_FRI_STOP_HOURS, food.getFriStopHours());
            values.put(Food.COLUMN_SAT_START_HOURS, food.getSatStartHours());
            values.put(Food.COLUMN_SAT_STOP_HOURS, food.getSatStopHours());
            values.put(Food.COLUMN_SUN_START_HOURS, food.getSunStartHours());
            values.put(Food.COLUMN_SUN_STOP_HOURS, food.getSunStopHours());

            getDatabase().insert(Food.TABLE_NAME, null, values);
        }
    }

    @Override
    public ArrayList<DatabaseRow> getTable() {
        return retrieveTable(Food.TABLE_NAME, Food.COLUMN_NAME);
    }

    @Override
    public Food getRow(long id) {
        String selection = Food.ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(id)};
        try (Cursor cursor = getDatabase().query(Food.TABLE_NAME, null, selection, selectionArgs, null, null, null)) {
            Food food = null;
            if (cursor != null && cursor.moveToNext()) {
                //getInt()>0 because SQLite doesn't have boolean types - 1 is true, 0 is false
                food = new Food(cursor.getInt(Food.ID_POS), cursor.getString(Food.POS_NAME), cursor.getInt(Food.POS_BUILDING_ID),
                        cursor.getString(Food.POS_INFORMATION), cursor.getInt(Food.POS_MEAL_PLAN) > 0, cursor.getInt(Food.POS_CARD) > 0,
                        cursor.getDouble(Food.POS_MON_START_HOURS), cursor.getDouble(Food.POS_MON_STOP_HOURS), cursor.getDouble(Food.POS_TUE_START_HOURS),
                        cursor.getDouble(Food.POS_TUE_STOP_HOURS), cursor.getDouble(Food.POS_WED_START_HOURS), cursor.getDouble(Food.POS_WED_STOP_HOURS),
                        cursor.getDouble(Food.POS_THUR_START_HOURS), cursor.getDouble(Food.POS_THUR_STOP_HOURS), cursor.getDouble(Food.POS_FRI_START_HOURS),
                        cursor.getDouble(Food.POS_FRI_STOP_HOURS), cursor.getDouble(Food.POS_SAT_START_HOURS),
                        cursor.getDouble(Food.POS_SAT_STOP_HOURS), cursor.getDouble(Food.POS_SUN_START_HOURS), cursor.getDouble(Food.POS_SUN_STOP_HOURS));
                cursor.close();
            }
            return food; //return only when the cursor has been closed.
            //Return statement never missed, try block always finishes this.
        }
    }

    /**
     * Gets a single known Food from the Food table.
     *
     * @param buildingID building ID of the Food to get from the table. String to differentiate from long ID getRow
     *                   string converted to int inside method.
     * @return Food class obtained from the table. Contains all information
     * held in row.
     */
    public ArrayList<Food> getFoodForBuilding(int buildingID) {
        String selection = Food.COLUMN_BUILDING_ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(buildingID)};
        ArrayList<Food> food = new ArrayList<>();
        //try with resources - automatically closes cursor whether or not its completed normally
        //order table by name, ascending
        try (Cursor cursor = getDatabase().query(Food.TABLE_NAME, null, selection, selectionArgs, null, null, Food.COLUMN_NAME + " ASC")) {
            while (cursor.moveToNext()) {
                Food oneFood = getRow(cursor.getInt(Food.ID_POS));
                food.add(oneFood);
            }
            cursor.close();
            return food; //return only when the cursor has been closed
        }
    }

    @Override
    public void updateRow(DatabaseRow oldRow, DatabaseRow newRow) {
        if (oldRow instanceof Food && newRow instanceof Food) {
            Food oldFood = (Food) oldRow;
            Food newFood = (Food) newRow;

            ContentValues values = new ContentValues();
            values.put(Food.ID, newFood.getId());
            values.put(Food.COLUMN_NAME, newFood.getName());
            values.put(Food.COLUMN_MEAL_PLAN, newFood.isMealPlan());
            values.put(Food.COLUMN_CARD, newFood.isCard());
            values.put(Food.COLUMN_INFORMATION, newFood.getInformation());
            values.put(Food.COLUMN_BUILDING_ID, newFood.getBuildingID());
            values.put(Food.COLUMN_MON_START_HOURS, newFood.getMonStartHours());
            values.put(Food.COLUMN_MON_STOP_HOURS, newFood.getMonStopHours());
            values.put(Food.COLUMN_TUE_START_HOURS, newFood.getTueStartHours());
            values.put(Food.COLUMN_TUE_STOP_HOURS, newFood.getTueStopHours());
            values.put(Food.COLUMN_WED_START_HOURS, newFood.getWedStartHours());
            values.put(Food.COLUMN_WED_STOP_HOURS, newFood.getWedStopHours());
            values.put(Food.COLUMN_THUR_START_HOURS, newFood.getThurStartHours());
            values.put(Food.COLUMN_THUR_STOP_HOURS, newFood.getThurStopHours());
            values.put(Food.COLUMN_FRI_START_HOURS, newFood.getFriStartHours());
            values.put(Food.COLUMN_FRI_STOP_HOURS, newFood.getFriStopHours());
            values.put(Food.COLUMN_SAT_START_HOURS, newFood.getSatStartHours());
            values.put(Food.COLUMN_SAT_STOP_HOURS, newFood.getSatStopHours());
            values.put(Food.COLUMN_SUN_START_HOURS, newFood.getSunStartHours());
            values.put(Food.COLUMN_SUN_STOP_HOURS, newFood.getSunStopHours());

            String selection = Food.ID + " LIKE ?";
            String selectionArgs[] = {String.valueOf(oldFood.getId())};
            getDatabase().update(Food.TABLE_NAME, values, selection, selectionArgs);
        }
    }
}
