package beta.qlife.database.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Carson on 21/01/2017.
 * Manages opening and closing the database, as well as managing
 * the instances of the database helper class. Database managers extends this
 * class to access the database easily.
 */
public class DatabaseAccessor {
    private static SQLiteDatabase mDatabase;
    private DbHelper mDBHelper;
    private Context mContext;

    public DatabaseAccessor(Context context) {
        this.mContext = context;
        open();
    }

    /**
     * Creates an instance of the database helper and opens
     * a readable/writable instance of the database.
     */
    private void open() {
        if (mDBHelper == null) {
            mDBHelper = DbHelper.getInstance(mContext);
        }
        mDatabase = mDBHelper.getWritableDatabase();
    }

    public static SQLiteDatabase getDatabase(){
        return mDatabase;
    }
}

