package engsoc.qlife.database.local.contacts.engineering;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import engsoc.qlife.database.local.DatabaseManager;
import engsoc.qlife.database.local.DatabaseRow;

/**
 * Created by Carson on 21/06/2017.
 * Handles rows in phone database for EngineeringContact table.
 */
public class EngineeringContactsManager extends DatabaseManager {
    public EngineeringContactsManager(Context context) {
        super(context);
    }

    @Override
    public void insertRow(DatabaseRow row) {
        if (row instanceof EngineeringContact) {
            EngineeringContact contact = (EngineeringContact) row;
            ContentValues values = new ContentValues();
            values.put(EngineeringContact.ID, contact.getId());
            values.put(EngineeringContact.COLUMN_NAME, contact.getName());
            values.put(EngineeringContact.COLUMN_EMAIL, contact.getEmail());
            values.put(EngineeringContact.COLUMN_POSITION, contact.getPosition());
            values.put(EngineeringContact.COLUMN_DESCRIPTION, contact.getDescription());
            getDatabase().insert(EngineeringContact.TABLE_NAME, null, values);
        }
    }

    @Override
    public ArrayList<DatabaseRow> getTable() {
        return retrieveTable(EngineeringContact.TABLE_NAME, EngineeringContact.COLUMN_NAME);
    }

    @Override
    public EngineeringContact getRow(long id) {
        String selection = EngineeringContact.ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(id)};
        try (Cursor cursor = getDatabase().query(EngineeringContact.TABLE_NAME, null, selection, selectionArgs, null, null, null)) {
            EngineeringContact contact = null;
            if (cursor != null && cursor.moveToNext()) {
                contact = new EngineeringContact(cursor.getInt(EngineeringContact.ID_POS), cursor.getString(EngineeringContact.NAME_POS), cursor.getString(EngineeringContact.EMAIL_POS),
                        cursor.getString(EngineeringContact.POSITION_POS), cursor.getString(EngineeringContact.DESCRIPTION_POS));
                cursor.close();
            }
            return contact; //return only when the cursor has been closed.
            //Return statement never missed, try block always finishes this.
        }

    }

    @Override
    public void updateRow(long rowId, DatabaseRow newRow) {
        if (newRow instanceof EngineeringContact) {
            EngineeringContact newContact = (EngineeringContact) newRow;

            ContentValues values = new ContentValues();
            values.put(EngineeringContact.ID, newContact.getId());
            values.put(EngineeringContact.COLUMN_NAME, newContact.getName());
            values.put(EngineeringContact.COLUMN_EMAIL, newContact.getEmail());
            values.put(EngineeringContact.COLUMN_POSITION, newContact.getPosition());
            values.put(EngineeringContact.COLUMN_DESCRIPTION, newContact.getDescription());

            String selection = EngineeringContact.ID + " LIKE ?";
            String selectionArgs[] = {String.valueOf(rowId)};
            getDatabase().update(EngineeringContact.TABLE_NAME, values, selection, selectionArgs);
        }
    }
}
