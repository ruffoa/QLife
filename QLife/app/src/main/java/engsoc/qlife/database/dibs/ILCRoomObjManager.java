package engsoc.qlife.database.dibs;

/**
 * Created by Alex on 8/21/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import engsoc.qlife.database.local.DatabaseManager;
import engsoc.qlife.database.local.DatabaseRow;

import java.util.ArrayList;

/**
 * Created by Alex on 7/28/2017.
 */

public class ILCRoomObjManager extends DatabaseManager {


    /**
     * Created by Alex on 7/28/2017.
     */
    public ILCRoomObjManager(Context context) {
        super(context);
    }

    @Override
    public void insertRow(DatabaseRow row) {
        if (row instanceof ILCRoomObj) {
            ILCRoomObj room = (ILCRoomObj) row;
            ContentValues values = new ContentValues();
            values.put(ILCRoomObj.ID, room.getId());
            values.put(ILCRoomObj.COLUMN_BUILDING_ID, room.getBuildingId());
            values.put(ILCRoomObj.COLUMN_DESCRIPTION, room.getDescription());
            values.put(ILCRoomObj.COLUMN_MAP_URL, room.getMapUrl());
            values.put(ILCRoomObj.COLUMN_NAME, room.getName());
            values.put(ILCRoomObj.COLUMN_PIC_URL, room.getPicUrl());
            values.put(ILCRoomObj.COLUMN_ROOM_ID, room.getRoomId());
            getDatabase().insert(ILCRoomObj.TABLE_NAME, null, values);
        }
    }

    @Override
    public ArrayList<DatabaseRow> getTable() {
        String[] projection = {
                ILCRoomObj.ID,
                ILCRoomObj.COLUMN_BUILDING_ID,
                ILCRoomObj.COLUMN_DESCRIPTION,
                ILCRoomObj.COLUMN_MAP_URL,
                ILCRoomObj.COLUMN_NAME,
                ILCRoomObj.COLUMN_PIC_URL,
                ILCRoomObj.COLUMN_ROOM_ID
        };
        ArrayList<DatabaseRow> ILCRooms = new ArrayList<>();
        //try with resources - automatically closes cursor whether or not its completed normally
        //order by building name
        try (Cursor cursor = getDatabase().query(ILCRoomObj.TABLE_NAME, projection, null, null, null, null, ILCRoomObj.COLUMN_NAME + " ASC")) {
            while (cursor.moveToNext()) {
                ILCRoomObj room = getRow(cursor.getInt(ILCRoomObj.ID_POS));
                ILCRooms.add(room);
            }
            cursor.close();
            return ILCRooms; //return only when the cursor has been closed
        }
    }

    @Override
    public ILCRoomObj getRow(long id) {
        String[] projection = {
                ILCRoomObj.ID,
                ILCRoomObj.COLUMN_BUILDING_ID,
                ILCRoomObj.COLUMN_DESCRIPTION,
                ILCRoomObj.COLUMN_MAP_URL,
                ILCRoomObj.COLUMN_NAME,
                ILCRoomObj.COLUMN_PIC_URL,
                ILCRoomObj.COLUMN_ROOM_ID
        };
        String selection = ILCRoomObj.ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(id)};
        try (Cursor cursor = getDatabase().query(ILCRoomObj.TABLE_NAME, projection, selection, selectionArgs, null, null, null)) {
            cursor.moveToNext();
            //getInt()>0 because SQLite doesn't have boolean types - 1 is true, 0 is false
            ILCRoomObj room = new ILCRoomObj(cursor.getInt(ILCRoomObj.ROOM_ID_POS), cursor.getInt(ILCRoomObj.BUILDING_ID_POS), cursor.getString(ILCRoomObj.DESCRIPTION_POS),
                    cursor.getString(ILCRoomObj.MAP_URL_POS), cursor.getString(ILCRoomObj.NAME_POS), cursor.getString(ILCRoomObj.PIC_URL_POS),
                    cursor.getInt(ILCRoomObj.ROOM_ID_POS));
            cursor.close();
            return room; //return only when the cursor has been closed.
            //Return statement never missed, try block always finishes this.
        }

    }

    @Override
    public void updateRow(DatabaseRow oldRow, DatabaseRow newRow) {
        if (oldRow instanceof ILCRoomObj && newRow instanceof ILCRoomObj) {
            ILCRoomObj oldRoom = (ILCRoomObj) oldRow;
            ILCRoomObj newRoom = (ILCRoomObj) newRow;

            ContentValues values = new ContentValues();
            values.put(ILCRoomObj.ID, newRoom.getId());
            values.put(ILCRoomObj.COLUMN_BUILDING_ID, newRoom.getBuildingId());
            values.put(ILCRoomObj.COLUMN_DESCRIPTION, newRoom.getDescription());
            values.put(ILCRoomObj.COLUMN_MAP_URL, newRoom.getMapUrl());
            values.put(ILCRoomObj.COLUMN_NAME, newRoom.getName());
            values.put(ILCRoomObj.COLUMN_PIC_URL, newRoom.getPicUrl());
            values.put(ILCRoomObj.COLUMN_ROOM_ID, newRoom.getRoomId());

            String selection = ILCRoomObj.ID + " LIKE ?";
            String selectionArgs[] = {String.valueOf(oldRoom.getId())};
            getDatabase().update(ILCRoomObj.TABLE_NAME, values, selection, selectionArgs);
        }
    }
}


