package com.example.kaushiknsanji.hydrationtrackerdemo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.kaushiknsanji.hydrationtrackerdemo.data.HydrationContract.HydrationEntry;

/**
 * Database Helper class that manages Database creation and Version management.
 *
 * @author Kaushik N Sanji
 */
public class HydrationDbHelper extends SQLiteOpenHelper {

    //Constant for the Database Version
    private static final int DATABASE_VERSION = 1;

    //Constant for the Database File Name
    private static final String DATABASE_NAME = "hydration.db";

    //Stores the instance of this class
    private static HydrationDbHelper mDbHelper;


    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use to open or create the database
     */
    private HydrationDbHelper(Context context) {
        //Propagating the call to super, to initialize the database
        super(context,
                DATABASE_NAME,
                null,  //Passing null to use the default CursorFactory to create cursor objects
                DATABASE_VERSION
        );
    }

    /**
     * Static Constructor exposed to create a single instance of {@link HydrationDbHelper}
     * through the use of Singleton pattern.
     *
     * @param context is the {@link Context} of the Activity used to open/create the database
     * @return Instance of {@link HydrationDbHelper}
     */
    public static synchronized HydrationDbHelper getInstance(Context context) {
        if (mDbHelper == null) {
            //Using Application Context instead of Activity Context to prevent memory leaks
            mDbHelper = new HydrationDbHelper(context.getApplicationContext());
        }
        //Returning the Instance of HydrationDbHelper
        return mDbHelper;
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Query for creating the Hydration Database Table
        String SQL_CREATE_TABLE = "CREATE TABLE " + HydrationEntry.TABLE_NAME + " ("
                + HydrationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + HydrationEntry.COLUMN_GLASS_WATER_COUNT + " INTEGER NOT NULL DEFAULT 1, "
                + HydrationEntry.COLUMN_ACTIVITY_DATE + " TEXT NOT NULL, "
                + HydrationEntry.COLUMN_ACTIVITY_TIME + " TEXT NOT NULL, "
                + HydrationEntry.COLUMN_DATETIMESTAMP + " TEXT NOT NULL"
                + ")";

        //Executing the SQL statement to create the database
        db.execSQL(SQL_CREATE_TABLE);
    }

    /**
     * Called when the database needs to be upgraded.
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Query for Dropping the database table
        String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + HydrationEntry.TABLE_NAME;
        //Discarding the data and starting over on Version Upgrade: START
        db.execSQL(SQL_DROP_TABLE);
        onCreate(db);
        //Discarding the data and starting over on Version Upgrade: END
    }
}
