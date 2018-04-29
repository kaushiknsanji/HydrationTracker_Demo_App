package com.example.kaushiknsanji.hydrationtrackerdemo.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.example.kaushiknsanji.hydrationtrackerdemo.data.HydrationContract.HydrationEntry;
import com.example.kaushiknsanji.hydrationtrackerdemo.utils.DateUtility;

/**
 * A Database Access Object class that performs the CRUD Operations
 * on the database's only table 'hydration'
 *
 * @author Kaushik N Sanji
 */
public class HydrationDao {

    /**
     * Private constructor to avoid instantiating this
     */
    private HydrationDao() {
    }

    /**
     * Method that inserts an entry into the table 'hydration' for the count of
     * 'Glass of Water' drank by the user at that moment.
     *
     * @param dbHelper             instance of {@link HydrationDbHelper}
     * @param numberOfGlassesDrank is the count of 'Glass of Water' drank by the user
     * @return Long value representing the Key of the new record inserted into the table.
     */
    public static long insertRecord(@NonNull SQLiteOpenHelper dbHelper, int numberOfGlassesDrank) {
        //Retrieving the current datetime in millis
        long currentDateTimeInMillis = System.currentTimeMillis();

        //Preparing the Content Values with the data to be inserted
        ContentValues values = new ContentValues();
        if (numberOfGlassesDrank > 0) {
            //Honouring the value of #numberOfGlassesDrank when greater than 0.
            //If less than or equal to 0, then a default value of 1 is entered by SQLite
            values.put(HydrationEntry.COLUMN_GLASS_WATER_COUNT, numberOfGlassesDrank);
        }
        values.put(HydrationEntry.COLUMN_ACTIVITY_DATE, DateUtility.getFormattedDate(currentDateTimeInMillis));
        values.put(HydrationEntry.COLUMN_ACTIVITY_TIME, DateUtility.getFormattedTime(currentDateTimeInMillis));
        values.put(HydrationEntry.COLUMN_DATETIMESTAMP, DateUtility.getDateTimeInIsoFormat(currentDateTimeInMillis));

        //Creating/Opening the Database in write mode
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();

        //Stores the Primary key of the record being inserted. Defaulted to -1.
        long newRowId = -1;

        //Writing data within a Transaction: START
        writableDatabase.beginTransaction();
        try {
            //Retrieving the Primary key of the record being inserted
            newRowId = writableDatabase.insert(HydrationEntry.TABLE_NAME, null, values);
            //Marking the transaction as successful
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction(); //Closing the Transaction
        }
        //Writing data within a Transaction: END

        //Returning the Primary key of the record inserted
        return newRowId;
    }

    /**
     * Method that deletes a record identified by the Key of the record passed
     * from the table 'hydration'.
     *
     * @param dbHelper instance of {@link HydrationDbHelper}
     * @param rowKey   is the Integer value of the Key of the record entry to be deleted
     * @return Integer value representing the count of records deleted.
     */
    public static int deleteRecordByKey(@NonNull SQLiteOpenHelper dbHelper, int rowKey) {
        //Creating/Opening the Database in write mode
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();

        //Specifying the WHERE Clause filter
        String selection = HydrationEntry._ID + " = ?";

        //Specifying the values for the columns involved in the WHERE Clause filter
        String[] selectionArgs = new String[]{String.valueOf(rowKey)};

        //Stores the number of records deleted
        int countOfRecordsDeleted = 0;

        //Deleting the record within a Transaction: START
        writableDatabase.beginTransaction();
        try {
            //Executing delete
            countOfRecordsDeleted = writableDatabase.delete(
                    HydrationEntry.TABLE_NAME,  //Table Name
                    selection,                  //Where Clause
                    selectionArgs               //Where Clause values
            );
            //Marking the transaction as successful
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction(); //Closing the Transaction
        }
        //Deleting the record within a Transaction: END

        //Returning the number of records deleted
        return countOfRecordsDeleted;
    }

    /**
     * Method that deletes all the records in the table 'hydration'
     * which is the only table in the database.
     *
     * @param dbHelper instance of {@link HydrationDbHelper}
     * @return Integer value representing the count of records deleted.
     */
    public static int deleteAllRecords(@NonNull SQLiteOpenHelper dbHelper) {
        //Creating/Opening the Database in write mode
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();

        //Specifying '1' to the WHERE Clause filter
        //in order to retrieve the count after deleting all the records
        String selection = "1";

        //Stores the number of records deleted
        int countOfRecordsDeleted = 0;

        //Deleting the record within a Transaction: START
        writableDatabase.beginTransaction();
        try {
            //Executing delete
            countOfRecordsDeleted = writableDatabase.delete(
                    HydrationEntry.TABLE_NAME,  //Table Name
                    selection,                  //Where Clause value of '1'
                    null              //'NULL' for Where Clause values
            );
            //Marking the transaction as successful
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction(); //Closing the Transaction
        }

        //Returning the number of records deleted
        return countOfRecordsDeleted;
    }

    /**
     * Method that returns the details of the latest record in the table 'hydration'
     * of the database in the form of {@link ContentValues} object.
     *
     * @param dbHelper instance of {@link HydrationDbHelper}
     * @return {@link ContentValues} object containing the values of the latest record.
     */
    public static ContentValues getRecentRecordDetails(@NonNull SQLiteOpenHelper dbHelper) {
        //Retrieving the Cursor to the latest record
        Cursor cursor = readRecentRecord(dbHelper);

        //Iterating over the cursor to prepare the ContentValues object: START
        ContentValues values = new ContentValues();
        try {
            if (cursor.moveToPosition(0)) {
                //Moving to the first record in the result.
                //There will be only one record if present since we are using
                //the Limit clause that says to return only one record.

                //Building the ContentValues map when there is a recent record
                values.put(HydrationEntry._ID, cursor.getInt(cursor.getColumnIndex(HydrationEntry._ID)));
                values.put(HydrationEntry.COLUMN_GLASS_WATER_COUNT, cursor.getInt(cursor.getColumnIndex(HydrationEntry.COLUMN_GLASS_WATER_COUNT)));
                values.put(HydrationEntry.COLUMN_ACTIVITY_DATE, cursor.getString(cursor.getColumnIndex(HydrationEntry.COLUMN_ACTIVITY_DATE)));
                values.put(HydrationEntry.COLUMN_ACTIVITY_TIME, cursor.getString(cursor.getColumnIndex(HydrationEntry.COLUMN_ACTIVITY_TIME)));
                values.put(HydrationEntry.COLUMN_DATETIMESTAMP, cursor.getString(cursor.getColumnIndex(HydrationEntry.COLUMN_DATETIMESTAMP)));
            }
        } finally {
            //Closing the cursor to release its resources
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        //Iterating over the cursor to prepare the ContentValues object: END

        //Returning the ContentValues prepared for the recent record
        return values;
    }

    /**
     * Method that returns a Cursor to the latest record in the table 'hydration'
     * of the database.
     *
     * @param dbHelper instance of {@link HydrationDbHelper}
     * @return Cursor pointing to the latest record in the table 'hydration'
     */
    public static Cursor readRecentRecord(@NonNull SQLiteOpenHelper dbHelper) {
        //Creating/Opening the Database in read mode
        SQLiteDatabase readableDatabase = dbHelper.getReadableDatabase();

        //Specifying the ORDER BY Clause
        String orderBy = HydrationEntry._ID + " DESC";

        //Specifying the LIMIT Clause
        String limit = "1";

        //Firing the query and returning the Cursor to the Recent record
        return readableDatabase.query(
                HydrationEntry.TABLE_NAME,      //Table Name
                null,                   //Selecting all Columns in Projection
                null,                  //No Where Clause
                null,               //No Where Clause Values
                null,                  //No Group By Clause
                null,                   //No Having Clause
                orderBy,                       //Order By Clause
                limit                          //Limit Clause
        );
    }

    /**
     * Method that returns the total value of 'glass_water_count' from the table 'hydration'.
     *
     * @param dbHelper instance of {@link HydrationDbHelper}
     * @return Long value representing the total value of 'glass_water_count'
     */
    public static long getTotalGlassOfWaterCount(@NonNull SQLiteOpenHelper dbHelper) {
        //Initializing the sum of 'glass_water_count' to 0
        long totalGlassOfWaterDrank = 0;

        //Creating/Opening the Database in read mode
        SQLiteDatabase readableDatabase = dbHelper.getReadableDatabase();

        //Specifying the Columns to be returned
        String[] projection = new String[]{
                //using the SUM() of 'glass_water_count' column to retrieve its total value
                "SUM(" + HydrationEntry.COLUMN_GLASS_WATER_COUNT + ") AS "
                        + HydrationEntry.ALIAS_COLUMN_GLASS_WATER_COUNT_TOTAL
        };

        //Firing the query and retrieving the Cursor to the record
        Cursor cursor = readableDatabase.query(
                HydrationEntry.TABLE_NAME,     //Table Name
                projection,                    //Columns to be returned
                null,                 //No Where Clause Filter
                null,               //No Where Clause Values
                null,                  //No Group By Clause
                null,                   //No Having Clause
                null                   //No Order By Clause
        );

        //Iterating over the cursor to retrieve the total of 'glass_water_count'
        try {
            if (cursor.moveToPosition(0)) {
                //Moving to the first record in the result.
                //Since we are using an aggregate function over the entire table,
                //there will be one and only one record in the cursor

                //Retrieving the Column Index of the Column we are interested in
                int columnIndex = cursor.getColumnIndex(HydrationEntry.ALIAS_COLUMN_GLASS_WATER_COUNT_TOTAL);
                if (!cursor.isNull(columnIndex)) {
                    //When the Sum is not NULL

                    //Retrieving the Sum of 'glass_water_count' from the cursor
                    totalGlassOfWaterDrank = cursor.getLong(columnIndex);
                }
            }
        } finally {
            //Closing the cursor to release its resources
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        //Returning the sum of 'glass_water_count'
        return totalGlassOfWaterDrank;
    }

    /**
     * Method that returns the total number of records present in the table 'hydration'.
     *
     * @param dbHelper instance of {@link HydrationDbHelper}
     * @return Long value representing the total number of records present in the table 'hydration'.
     */
    public static long getTotalRecordCount(@NonNull SQLiteOpenHelper dbHelper) {
        //Returning the total number of records/entries found in the table using DatabaseUtils
        return DatabaseUtils.queryNumEntries(dbHelper.getReadableDatabase(), HydrationEntry.TABLE_NAME);
    }

}