package engsoc.qlife.database.local;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import engsoc.qlife.database.local.buildings.Building;

/**
 * Created by Carson on 27/07/2017.
 * Abstract class that defines what methods a phone database manager must have, and provides some method bodies.
 */
public abstract class DatabaseManager extends DatabaseAccessor {

    public DatabaseManager(Context context) {
        super(context);
    }

    /**
     * Method that deletes the given row from a table. Which table is defined by the child manager.
     *
     * @param row THe row to be deleted.
     */
    public void deleteRow(DatabaseRow row) {
        String selection = DatabaseRow.ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(row.getId())};
        getDatabase().delete(DatabaseRow.TABLE_NAME, selection, selectionArgs);
    }

    /**
     * Method that deletes a table in the phone database.
     *
     * @param tableName The String name of the table to be deleted.
     */
    public void deleteTable(String tableName) {
        getDatabase().delete(tableName, null, null);
    }

    /**
     * Method that iserts a row into a table in the phone database. Which table is defined
     * by the child database manager
     *
     * @param row The data to be inserted.
     */
    public abstract void insertRow(DatabaseRow row);

    /**
     * Method that retrieves an entire table. Which table is defined by the child manager.
     * Should return retrieveTable() with the information that will define the table.
     *
     * @return ArrayList of the rows in the table.
     */
    public abstract ArrayList<DatabaseRow> getTable();

    /**
     * Method that actually performs the table retrieval. Called from getTable() to
     * define which table the retrieval is from.
     *
     * @param tableName  Name of table to retrieve from.
     * @param columnName Name of column to sort by.
     * @return An ArrayList of the rows retrieved from the table.
     */
    protected ArrayList<DatabaseRow> retrieveTable(String tableName, String columnName) {
        ArrayList<DatabaseRow> rows = new ArrayList<>();
        //try with resources - automatically closes cursor whether or not its completed normally
        //order by building name
        try (Cursor cursor = getDatabase().query(tableName, null, null, null, null, null, columnName + " ASC")) {
            while (cursor.moveToNext()) {
                rows.add(getRow(cursor.getInt(DatabaseRow.ID_POS)));
            }
            cursor.close();
            return rows; //return only when the cursor has been closed
        }
    }

    /**
     * Method that retrieves a row from a table. Which table is defined by the child manager.
     *
     * @param id The ID of the row to get.
     * @return The row data.
     */
    public abstract DatabaseRow getRow(long id);

    /**
     * Method that changes information in an existing row. Which table is defined by
     * the child manager.
     *
     * @param oldRow The data of the row to be changed.
     * @param newRow The data to use for the change.
     */
    public abstract void updateRow(DatabaseRow oldRow, DatabaseRow newRow);
}
