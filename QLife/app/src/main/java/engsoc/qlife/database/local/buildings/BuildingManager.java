package engsoc.qlife.database.local.buildings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import engsoc.qlife.database.local.DatabaseManager;
import engsoc.qlife.database.local.DatabaseRow;

import java.util.ArrayList;

/**
 * Created by Carson on 26/06/2017.
 * Manages rows in Buildings table in phone database.
 */
public class BuildingManager extends DatabaseManager {
    public BuildingManager(Context context) {
        super(context);
    }

    @Override
    public void insertRow(DatabaseRow row) {
        if (row instanceof Building) {
            Building building = (Building) row;
            ContentValues values = new ContentValues();
            values.put(Building.ID, building.getId());
            values.put(Building.COLUMN_NAME, building.getName());
            values.put(Building.COLUMN_PURPOSE, building.getPurpose());
            values.put(Building.COLUMN_BOOK_ROOMS, building.getBookRooms());
            values.put(Building.COLUMN_FOOD, building.getFood());
            values.put(Building.COLUMN_ATM, building.getAtm());
            values.put(Building.COLUMN_LAT, building.getLat());
            values.put(Building.COLUMN_LON, building.getLon());
            getDatabase().insert(Building.TABLE_NAME, null, values);
        }
    }

    @Override
    public ArrayList<DatabaseRow> getTable() {
        ArrayList<DatabaseRow> buildings = new ArrayList<>();
        //try with resources - automatically closes cursor whether or not its completed normally
        //order by building name
        try (Cursor cursor = getDatabase().query(Building.TABLE_NAME, null, null, null, null, null, Building.COLUMN_NAME + " ASC")) {
            while (cursor.moveToNext()) {
                Building building = getRow(cursor.getInt(Building.ID_POS));
                buildings.add(building);
            }
            cursor.close();
            return buildings; //return only when the cursor has been closed
        }
    }

    @Override
    public Building getRow(long id) {
        String selection = Building.ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(id)};
        try (Cursor cursor = getDatabase().query(Building.TABLE_NAME, null, selection, selectionArgs, null, null, null)) {
            Building building = null;
            if (cursor != null && cursor.moveToNext()) {
                //getInt()>0 because SQLite doesn't have boolean types - 1 is true, 0 is false
                building = new Building(cursor.getInt(Building.ID_POS), cursor.getString(Building.NAME_POS), cursor.getString(Building.PURPOSE_POS),
                        cursor.getInt(Building.BOOK_ROOKS_POS) > 0, cursor.getInt(Building.FOOD_POS) > 0, cursor.getInt(Building.ATM_POS) > 0,
                        cursor.getDouble(Building.LAT_POS), cursor.getDouble(Building.LON_POST));
                cursor.close();
            }
            return building; //return only when the cursor has been closed.
            //Return statement never missed, try block always finishes this.
        }
    }

    /**
     * Method that gets a building based on the ICS file building name.
     * The ICS file has short forms, but all contain at least the first
     * 5 letters of the building name. So we search for a name that contains
     * that sequence.
     * Just in case there are multiple buildings returned, the first one
     * will always be returned. This causes an issue with Etherington Hall, where
     * the art centre location is returned instead. Thus, a hardcoded special case is made.
     * No class is ever held in the art centre, so if the returned Building name has 'Art
     * Centre' in it, the cursor is moved to next.
     *
     * @param icsName The ICS file building name.
     * @return The Building object corresponding to the ICS file name.
     */
    public Building getIcsBuilding(String icsName) {
        try (Cursor cursor = getDatabase().rawQuery("SELECT * FROM Buildings GROUP BY Name HAVING Name LIKE '%" + icsName + "%'", null)) {
            Building building = null;
            if (cursor.moveToNext()) {
                if (cursor.getString(Building.NAME_POS).contains("Art Centre")){
                    cursor.moveToNext();
                }
                //getInt()>0 because SQLite doesn't have boolean types - 1 is true, 0 is false
                building = new Building(cursor.getInt(Building.ID_POS), cursor.getString(Building.NAME_POS), cursor.getString(Building.PURPOSE_POS),
                        cursor.getInt(Building.BOOK_ROOKS_POS) > 0, cursor.getInt(Building.FOOD_POS) > 0, cursor.getInt(Building.ATM_POS) > 0,
                        cursor.getDouble(Building.LAT_POS), cursor.getDouble(Building.LON_POST));
            }
            cursor.close();
            return building; //return only when the cursor has been closed.
            //Return statement never missed, try block always finishes this.
        }
    }

    @Override
    public void updateRow(DatabaseRow oldRow, DatabaseRow newRow) {
        if (oldRow instanceof Building && newRow instanceof Building) {
            Building oldBuilding = (Building) oldRow;
            Building newBuilding = (Building) newRow;

            ContentValues values = new ContentValues();
            values.put(Building.ID, newBuilding.getId());
            values.put(Building.COLUMN_NAME, newBuilding.getName());
            values.put(Building.COLUMN_PURPOSE, newBuilding.getPurpose());
            values.put(Building.COLUMN_BOOK_ROOMS, newBuilding.getBookRooms());
            values.put(Building.COLUMN_FOOD, newBuilding.getFood());
            values.put(Building.COLUMN_ATM, newBuilding.getAtm());
            values.put(Building.COLUMN_LAT, newBuilding.getLat());
            values.put(Building.COLUMN_LON, newBuilding.getLon());

            String selection = Building.ID + " LIKE ?";
            String selectionArgs[] = {String.valueOf(oldBuilding.getId())};
            getDatabase().update(Building.TABLE_NAME, values, selection, selectionArgs);
        }
    }
}
