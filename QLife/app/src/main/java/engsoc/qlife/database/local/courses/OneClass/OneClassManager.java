package engsoc.qlife.database.local.courses.OneClass;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import engsoc.qlife.database.local.DatabaseManager;
import engsoc.qlife.database.local.DatabaseRow;
import engsoc.qlife.database.local.contacts.engineering.EngineeringContact;

/**
 * Created by Carson on 07/02/2017.
 * Class that handles data for the phone database OneClass table.
 */
public class OneClassManager extends DatabaseManager {
    public OneClassManager(Context context) {
        super(context);
    }

    @Override
    public void insertRow(DatabaseRow row) {
        if (row instanceof OneClass) {
            OneClass oneClass = (OneClass) row;
            ContentValues values = new ContentValues();
            values.put(OneClass.COLUMN_CLASS_TYPE, oneClass.getType());
            values.put(OneClass.COLUMN_BUILDING_ID, oneClass.getBuildingID());
            values.put(OneClass.COLUMN_ROOM_NUM, oneClass.getRoomNum());
            values.put(OneClass.COLUMN_START_TIME, oneClass.getStartTime());
            values.put(OneClass.COLUMN_END_TIME, oneClass.getEndTime());
            values.put(OneClass.COLUMN_DAY, oneClass.getDay());
            values.put(OneClass.COLUMN_MONTH, oneClass.getMonth());
            values.put(OneClass.COLUMN_YEAR, oneClass.getYear());
            values.put(OneClass.COLUMN_COURSE_ID, oneClass.getCourseID());
            values.put(OneClass.COLUMN_HAS_NAME, oneClass.getHasName());
            getDatabase().insert(OneClass.TABLE_NAME, null, values);
        }
    }

    @Override
    public ArrayList<DatabaseRow> getTable() {
        return retrieveTable(OneClass.TABLE_NAME, null);
    }

    @Override
    public OneClass getRow(long id) {
        String selection = OneClass.ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(id)};
        try (Cursor cursor = getDatabase().query(OneClass.TABLE_NAME, null, selection, selectionArgs, null, null, null)) {
            OneClass oneClass = null;
            if (cursor != null && cursor.moveToNext()) {
                oneClass = new OneClass(cursor.getInt(OneClass.ID_POS), cursor.getString(OneClass.CLASS_TYPE_POS),
                        cursor.getString(OneClass.ROOM_NUM_POS), cursor.getString(OneClass.STIME_POS),
                        cursor.getString(OneClass.ETIME_POS),
                        cursor.getString(OneClass.DAY_POS), cursor.getString(OneClass.MONTH_POS),
                        cursor.getString(OneClass.YEAR_POS), cursor.getString(OneClass.COURSE_HAS_NAME_POS));
                oneClass.setBuildingID(cursor.getInt(OneClass.BUILDING_ID_POS));
                oneClass.setCourseID(cursor.getInt(OneClass.COURSE_ID_POS));
                cursor.close();
            }
            return oneClass;
        }
    }

    @Override
    public void updateRow(DatabaseRow oldRow, DatabaseRow newRow) {
        if (oldRow instanceof OneClass && newRow instanceof OneClass) {
            OneClass oldClass = (OneClass) oldRow;
            OneClass newClass = (OneClass) newRow;
            ContentValues values = new ContentValues();
            values.put(OneClass.COLUMN_CLASS_TYPE, newClass.getType());
            values.put(OneClass.COLUMN_ROOM_NUM, newClass.getRoomNum());
            values.put(OneClass.COLUMN_START_TIME, newClass.getStartTime());
            values.put(OneClass.COLUMN_END_TIME, newClass.getEndTime());
            values.put(OneClass.COLUMN_DAY, newClass.getDay());
            values.put(OneClass.COLUMN_MONTH, newClass.getMonth());
            values.put(OneClass.COLUMN_YEAR, newClass.getYear());
            values.put(OneClass.COLUMN_COURSE_ID, newClass.getCourseID());
            values.put(OneClass.COLUMN_HAS_NAME, newClass.getHasName());
            String selection = OneClass.ID + " LIKE ?";
            String selectionArgs[] = {String.valueOf(oldClass.getId())};
            getDatabase().update(OneClass.TABLE_NAME, values, selection, selectionArgs);
        }
    }
}
