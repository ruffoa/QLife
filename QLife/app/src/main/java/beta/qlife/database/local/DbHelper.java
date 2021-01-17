package beta.qlife.database.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Carson on 19/01/2017.
 * Manages creating/upgrading/downgrading the database.
 * Also holds static SQL query strings to create/delete the database.
 * <p>
 * Uses the Singleton pattern to avoid multiple database connections.
 */
public class DbHelper extends SQLiteOpenHelper {

    //**NOTE** this must be incremented if you are trying to run changes to the database schema
    private static final int DATABASE_VERSION = 8;

    private static DbHelper mInstance = null;

    public DbHelper(Context context) {
        super(context, SqlStringStatements.PHONE_DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creates an instance of the DBHelper. Implements the Singleton pattern.
     *
     * @param context Context to create the DBHelper for.
     * @return returns the instance of DBHelper.
     */
    static DbHelper getInstance(Context context) {
        if (mInstance == null) {
            //use application context so as to not accidentally leak application context in database.
            mInstance = new DbHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SqlStringStatements.CREATE_USERS);
        db.execSQL(SqlStringStatements.CREATE_COURSES);
        db.execSQL(SqlStringStatements.CREATE_CLASSES);
        db.execSQL(SqlStringStatements.CREATE_EMERGENCY_CONTACTS);
        db.execSQL(SqlStringStatements.CREATE_ENGINEERING_CONTACTS);
        db.execSQL(SqlStringStatements.CREATE_BUILDINGS);
        db.execSQL(SqlStringStatements.CREATE_FOOD);
        db.execSQL(SqlStringStatements.CREATE_CAFETERIAS);
        db.execSQL(SqlStringStatements.CREATE_ILC_ROOM_INFO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //deletes the database and then re-creates the new version
        db.execSQL(SqlStringStatements.DELETE_USERS);
        db.execSQL(SqlStringStatements.DELETE_COURSES);
        db.execSQL(SqlStringStatements.DELETE_CLASSES);
        db.execSQL(SqlStringStatements.DELETE_EMERGENCY_CONTACTS);
        db.execSQL(SqlStringStatements.DELETE_ENGINEERING_CONTACTS);
        db.execSQL(SqlStringStatements.DELETE_BUILDINGS);
        db.execSQL(SqlStringStatements.DELETE_FOOD);
        db.execSQL(SqlStringStatements.DELETE_CAFETERIAS);
        db.execSQL(SqlStringStatements.DELETE_ILC_ROOM_INFO);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
