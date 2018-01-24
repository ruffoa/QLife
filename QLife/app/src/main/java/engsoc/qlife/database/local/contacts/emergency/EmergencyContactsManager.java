package engsoc.qlife.database.local.contacts.emergency;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import engsoc.qlife.database.local.DatabaseManager;
import engsoc.qlife.database.local.DatabaseRow;

/**
 * Created by Carson on 21/06/2017.
 * Manages rows in EmergencyContact table in phone database.
 */

public class EmergencyContactsManager extends DatabaseManager {
    public EmergencyContactsManager(Context context) {
        super(context);
    }

    @Override
    public void insertRow(DatabaseRow row) {
        if (row instanceof EmergencyContact) {
            EmergencyContact contact = (EmergencyContact) row;
            ContentValues values = new ContentValues();
            values.put(EmergencyContact.ID, contact.getId());
            values.put(EmergencyContact.COLUMN_NAME, contact.getName());
            values.put(EmergencyContact.COLUMN_PHONE_NUMBER, contact.getPhoneNumber());
            values.put(EmergencyContact.COLUMN_DESCRIPTION, contact.getDescription());
            getDatabase().insert(EmergencyContact.TABLE_NAME, null, values);
        }
    }

    @Override
    public ArrayList<DatabaseRow> getTable() {
        return retrieveTable(EmergencyContact.TABLE_NAME, EmergencyContact.COLUMN_NAME);
    }

    @Override
    public EmergencyContact getRow(long id) {
        String selection = EmergencyContact.ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(id)};
        try (Cursor cursor = getDatabase().query(EmergencyContact.TABLE_NAME, null, selection, selectionArgs, null, null, null)) {
            EmergencyContact contact = null;
            if (cursor.moveToNext()) {
                contact = new EmergencyContact(cursor.getInt(EmergencyContact.ID_POS), cursor.getString(EmergencyContact.NAME_POS), cursor.getString(EmergencyContact.PHONE_NUMBER_POS),
                        cursor.getString(EmergencyContact.DESCRIPTION_POS));
                cursor.close();
            }
            return contact; //return only when the cursor has been closed.
            //Return statement never missed, try block always finishes this.
        }

    }

    @Override
    public void updateRow(long rowId, DatabaseRow newRow) {
        if (newRow instanceof EmergencyContact) {
            EmergencyContact newContact = (EmergencyContact) newRow;

            ContentValues values = new ContentValues();
            values.put(EmergencyContact.ID, newContact.getId());
            values.put(EmergencyContact.COLUMN_NAME, newContact.getName());
            values.put(EmergencyContact.COLUMN_PHONE_NUMBER, newContact.getPhoneNumber());
            values.put(EmergencyContact.COLUMN_DESCRIPTION, newContact.getDescription());

            String selection = EmergencyContact.ID + " LIKE ?";
            String selectionArgs[] = {String.valueOf(rowId)};
            getDatabase().update(EmergencyContact.TABLE_NAME, values, selection, selectionArgs);
        }
    }
}
