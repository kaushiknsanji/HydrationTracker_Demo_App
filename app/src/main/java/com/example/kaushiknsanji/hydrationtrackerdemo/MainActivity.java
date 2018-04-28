package com.example.kaushiknsanji.hydrationtrackerdemo;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import android.support.constraint.Group;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kaushiknsanji.hydrationtrackerdemo.data.HydrationContract;
import com.example.kaushiknsanji.hydrationtrackerdemo.data.HydrationDao;
import com.example.kaushiknsanji.hydrationtrackerdemo.data.HydrationDbHelper;
import com.example.kaushiknsanji.hydrationtrackerdemo.utils.TextAppearanceUtility;

/**
 * The Main Activity of the App that inflates the layout 'R.layout.activity_main'
 * to show the total value of 'Glasses of Water' drank/recorded by the user
 * along with other relevant details including a table 'R.layout.layout_table' that shows the
 * latest record inserted/deleted.
 *
 * @author Kaushik N Sanji
 */
public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    //Bundle Key to save/restore the 'Last Record Inserted' Column Values
    private static final String BUNDLE_LAST_INSERT_CV_KEY = "ContentValues.LastRecordInserted";
    //Bundle Key to save/restore the 'Last Record Deleted' Column Values
    private static final String BUNDLE_LAST_DELETE_CV_KEY = "ContentValues.LastRecordDeleted";
    //Handler for executing on UI Threads
    private Handler mMainHandler;
    //Handler for executing on Worker Threads
    private Handler mWorkHandler;
    //HandlerThread for processing database operations in background thread
    private HandlerThread mDatabaseHandlerThread;
    //SQLiteHelper that creates and manages the database
    private HydrationDbHelper mDbHelper;
    //For the TextView that shows the count of 'Glasses of Water' drank
    private TextView mWaterRecordCountTextView;
    //For the ImageView that shows the Cup of Water
    private ImageView mCupWaterImageView;
    //For the TextView that shows that Total records in the database
    private TextView mDatabaseRecordCountTextView;
    //For the TextViews in the 'Last Record Inserted' Column
    private TextView mInsertIdTextView;
    private TextView mInsertGlassOfWaterTextView;
    private TextView mInsertDateTextView;
    private TextView mInsertTimeTextView;
    private TextView mInsertTimestampTextView;
    //For the TextViews in the 'Last Record Deleted' Column
    private TextView mDeleteIdTextView;
    private TextView mDeleteGlassOfWaterTextView;
    private TextView mDeleteDateTextView;
    private TextView mDeleteTimeTextView;
    private TextView mDeleteTimestampTextView;
    //For the EditText Input that captures the number of 'Glass of Water' drank
    private TextInputEditText mWaterCupCountEditText;
    //For the Wrapper Layout of the above EditText that shows the hint as a floating label
    private TextInputLayout mWaterCupCountEditTextLayout;
    //For the Constraint Group that controls the visibility of database delete action buttons
    private Group mGroupDeleteActions;
    //For managing the Toast messages to display
    private Toast mToast;

    /**
     * Called when the activity is to be created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing the required Handlers
        initHandlers();

        //Initializing the DB Helper
        mDbHelper = HydrationDbHelper.getInstance(this);

        //Initializing the Views
        findViews();

        //Initialize the Views with Data
        initData(savedInstanceState);
    }

    /**
     * Method that initializes the Handlers required for
     * processing stuff in UI Thread and Background Thread separately.
     */
    private void initHandlers() {
        //Creating a Handler attached to the UI Looper
        mMainHandler = new Handler(Looper.getMainLooper());

        //Creating a background thread for processing the database operations separately
        mDatabaseHandlerThread = new HandlerThread("DatabaseHandlerThread",
                Process.THREAD_PRIORITY_BACKGROUND);
        //Starting the thread. This initializes the Looper and its queue
        mDatabaseHandlerThread.start();

        //Creating a Handler attached to the mDatabaseHandlerThread's Looper
        mWorkHandler = new Handler(mDatabaseHandlerThread.getLooper());
    }

    /**
     * Method that finds the Views required and registers the required listeners
     */
    private void findViews() {
        mWaterRecordCountTextView = findViewById(R.id.textview_main_water_record_count);
        mCupWaterImageView = findViewById(R.id.imageview_main_cup_water);
        mDatabaseRecordCountTextView = findViewById(R.id.textview_main_db_record_count);
        mInsertIdTextView = findViewById(R.id.textview_layout_table_cell_insert_key);
        mInsertGlassOfWaterTextView = findViewById(R.id.textview_layout_table_cell_insert_gow);
        mInsertDateTextView = findViewById(R.id.textview_layout_table_cell_insert_date);
        mInsertTimeTextView = findViewById(R.id.textview_layout_table_cell_insert_time);
        mInsertTimestampTextView = findViewById(R.id.textview_layout_table_cell_insert_datetime);
        mDeleteIdTextView = findViewById(R.id.textview_layout_table_cell_delete_key);
        mDeleteGlassOfWaterTextView = findViewById(R.id.textview_layout_table_cell_delete_gow);
        mDeleteDateTextView = findViewById(R.id.textview_layout_table_cell_delete_date);
        mDeleteTimeTextView = findViewById(R.id.textview_layout_table_cell_delete_time);
        mDeleteTimestampTextView = findViewById(R.id.textview_layout_table_cell_delete_datetime);
        mWaterCupCountEditText = findViewById(R.id.edittext_main_cup_water_count);
        mWaterCupCountEditTextLayout = findViewById(R.id.edittext_layout_main_cup_water_count);
        mGroupDeleteActions = findViewById(R.id.group_main_delete_actions);

        //Registering the Click Listeners on the Buttons
        findViewById(R.id.button_main_insert_action).setOnClickListener(this);
        findViewById(R.id.button_main_delete_action).setOnClickListener(this);
        findViewById(R.id.button_main_clear_action).setOnClickListener(this);
    }

    /**
     * Method that initializes the View Components with necessary data.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    private void initData(Bundle savedInstanceState) {
        //Retrieving the total count of records in the database
        long totalRecordCount = HydrationDao.getTotalRecordCount(mDbHelper);

        if (totalRecordCount > 0) {
            //When there are records in the database

            //Making the delete buttons visible
            showDeleteActions();

            //Executing on a worker thread
            mWorkHandler.post(new Runnable() {
                @Override
                public void run() {
                    //Reloading the total record count of Database and that of 'glass_water_count'
                    reloadRecordCounts();
                }
            });

            if (savedInstanceState == null) {
                //When the activity was just launched

                //Executing on a worker thread
                mWorkHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //Retrieving the latest record details from database for UI update
                        final ContentValues recentRecordDetails = HydrationDao.getRecentRecordDetails(mDbHelper);

                        //Executing on UI thread
                        mMainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                //Loading a recent record into the 'Last Record Inserted' column
                                modifyInsertColumnData(recentRecordDetails);
                            }
                        });

                    }
                });

            }

        } else {
            //When there are NO records in the database

            //Hiding the delete buttons
            hideDeleteActions();

            //Resetting the water cup image to empty state
            modifyWaterCupState(true);

            //Initializing the total count of records in the database to 0
            modifyTotalRecordCountText(totalRecordCount);

            //Initializing the total count of 'glass_water_count' to 0
            modifyTotalGlassOfWaterCountText(totalRecordCount);

        }

        if (savedInstanceState != null) {
            //When the activity was relaunched

            //Reloading a record into the 'Last Record Inserted' column from the Bundle if present: START
            ContentValues lastRecordInsertedValues = savedInstanceState.getParcelable(BUNDLE_LAST_INSERT_CV_KEY);
            if (lastRecordInsertedValues.size() > 0) {
                modifyInsertColumnData(lastRecordInsertedValues);
            }
            //Reloading a record into the 'Last Record Inserted' column from the Bundle if present: END

            //Reloading a record into the 'Last Record Deleted' column from the Bundle if present: START
            ContentValues lastRecordDeletedValues = savedInstanceState.getParcelable(BUNDLE_LAST_DELETE_CV_KEY);
            if (lastRecordDeletedValues.size() > 0) {
                modifyDeleteColumnData(lastRecordDeletedValues);
            }
            //Reloading a record into the 'Last Record Deleted' column from the Bundle if present: END
        }

    }

    /**
     * Called to retrieve per-instance state from an activity before being killed
     * so that the state can be restored in {@link #onCreate} or
     * {@link #onRestoreInstanceState} (the {@link Bundle} populated by this method
     * will be passed to both).
     * <p>
     * <p>This method is called before an activity may be killed so that when it
     * comes back some time in the future it can restore its state.
     * <p>
     * <p>If called, this method will occur before {@link #onStop}.  There are
     * no guarantees about whether it will occur before or after {@link #onPause}.
     *
     * @param outState Bundle in which to place your saved state.
     * @see #onCreate
     * @see #onRestoreInstanceState
     * @see #onPause
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Preparing ContentValues for saving the content of 'Last Record Inserted' Column
        ContentValues lastRecordInsertedValues = new ContentValues();
        //Retrieving the Key of the record being displayed if any
        String lastRecordInsertedKeyStr = mInsertIdTextView.getText().toString();
        if (!TextUtils.isEmpty(lastRecordInsertedKeyStr)) {
            //If the Key is present, then extract and save the values of the record in the ContentValues
            lastRecordInsertedValues.put(HydrationContract.HydrationEntry._ID,
                    Integer.parseInt(lastRecordInsertedKeyStr)
            );
            lastRecordInsertedValues.put(HydrationContract.HydrationEntry.COLUMN_GLASS_WATER_COUNT,
                    Integer.parseInt(mInsertGlassOfWaterTextView.getText().toString())
            );
            lastRecordInsertedValues.put(HydrationContract.HydrationEntry.COLUMN_ACTIVITY_DATE,
                    mInsertDateTextView.getText().toString()
            );
            lastRecordInsertedValues.put(HydrationContract.HydrationEntry.COLUMN_ACTIVITY_TIME,
                    mInsertTimeTextView.getText().toString()
            );
            lastRecordInsertedValues.put(HydrationContract.HydrationEntry.COLUMN_DATETIMESTAMP,
                    mInsertTimestampTextView.getText().toString()
            );
        }
        //Saving the content of 'Last Record Inserted' Column in the Bundle
        outState.putParcelable(BUNDLE_LAST_INSERT_CV_KEY, lastRecordInsertedValues);

        //Preparing ContentValues for saving the content of 'Last Record Deleted' Column
        ContentValues lastRecordDeletedValues = new ContentValues();
        //Retrieving the Key of the record being displayed if any
        String lastRecordDeletedKeyStr = mDeleteIdTextView.getText().toString();
        if (!TextUtils.isEmpty(lastRecordDeletedKeyStr)) {
            //If the Key is present, then extract and save the values of the record in the ContentValues
            lastRecordDeletedValues.put(HydrationContract.HydrationEntry._ID,
                    Integer.parseInt(lastRecordDeletedKeyStr)
            );
            lastRecordDeletedValues.put(HydrationContract.HydrationEntry.COLUMN_GLASS_WATER_COUNT,
                    Integer.parseInt(mDeleteGlassOfWaterTextView.getText().toString())
            );
            lastRecordDeletedValues.put(HydrationContract.HydrationEntry.COLUMN_ACTIVITY_DATE,
                    mDeleteDateTextView.getText().toString()
            );
            lastRecordDeletedValues.put(HydrationContract.HydrationEntry.COLUMN_ACTIVITY_TIME,
                    mDeleteTimeTextView.getText().toString()
            );
            lastRecordDeletedValues.put(HydrationContract.HydrationEntry.COLUMN_DATETIMESTAMP,
                    mDeleteTimestampTextView.getText().toString()
            );
        }
        //Saving the content of 'Last Record Deleted' Column in the Bundle
        outState.putParcelable(BUNDLE_LAST_DELETE_CV_KEY, lastRecordDeletedValues);

    }

    /**
     * Method that updates the Cells of the Column 'Last Record Inserted'
     * with the values read from the ContentValues #recordValues passed.
     * <p>
     * Runs on the UI thread.
     * </p>
     *
     * @param recordValues is a {@link ContentValues} object with the values of
     *                     the database record to be updated on the views.
     */
    private void modifyInsertColumnData(ContentValues recordValues) {
        mInsertIdTextView.setText(
                String.valueOf(recordValues.getAsInteger(HydrationContract.HydrationEntry._ID))
        );
        mInsertGlassOfWaterTextView.setText(
                String.valueOf(recordValues.getAsInteger(HydrationContract.HydrationEntry.COLUMN_GLASS_WATER_COUNT))
        );
        mInsertDateTextView.setText(
                recordValues.getAsString(HydrationContract.HydrationEntry.COLUMN_ACTIVITY_DATE)
        );
        mInsertTimeTextView.setText(
                recordValues.getAsString(HydrationContract.HydrationEntry.COLUMN_ACTIVITY_TIME)
        );
        mInsertTimestampTextView.setText(
                recordValues.getAsString(HydrationContract.HydrationEntry.COLUMN_DATETIMESTAMP)
        );
    }

    /**
     * Method that clears the value on each Cell content of the Column 'Last Record Inserted'
     * on the view.
     * <p>
     * Runs on the UI thread.
     * </p>
     */
    private void clearInsertColumnContent() {
        final String emptyText = "";
        mInsertIdTextView.setText(emptyText);
        mInsertGlassOfWaterTextView.setText(emptyText);
        mInsertDateTextView.setText(emptyText);
        mInsertTimeTextView.setText(emptyText);
        mInsertTimestampTextView.setText(emptyText);
    }

    /**
     * Method that updates the Cells of the Column 'Last Record Deleted'
     * with the values read from the ContentValues #recordValues passed.
     * <p>
     * Runs on the UI thread.
     * </p>
     *
     * @param recordValues is a {@link ContentValues} object with the values of
     *                     the database record to be updated on the views.
     */
    private void modifyDeleteColumnData(ContentValues recordValues) {
        mDeleteIdTextView.setText(
                String.valueOf(recordValues.getAsInteger(HydrationContract.HydrationEntry._ID))
        );
        mDeleteGlassOfWaterTextView.setText(
                String.valueOf(recordValues.getAsInteger(HydrationContract.HydrationEntry.COLUMN_GLASS_WATER_COUNT))
        );
        mDeleteDateTextView.setText(
                recordValues.getAsString(HydrationContract.HydrationEntry.COLUMN_ACTIVITY_DATE)
        );
        mDeleteTimeTextView.setText(
                recordValues.getAsString(HydrationContract.HydrationEntry.COLUMN_ACTIVITY_TIME)
        );
        mDeleteTimestampTextView.setText(
                recordValues.getAsString(HydrationContract.HydrationEntry.COLUMN_DATETIMESTAMP)
        );
    }

    /**
     * Method that clears the value on each Cell content of the Column 'Last Record Deleted'
     * on the view.
     * <p>
     * Runs on the UI thread.
     * </p>
     */
    private void clearDeleteColumnContent() {
        final String emptyText = "";
        mDeleteIdTextView.setText(emptyText);
        mDeleteGlassOfWaterTextView.setText(emptyText);
        mDeleteDateTextView.setText(emptyText);
        mDeleteTimeTextView.setText(emptyText);
        mDeleteTimestampTextView.setText(emptyText);
    }

    /**
     * Method that hides the buttons 'R.id.button_main_delete_action' and 'R.id.button_main_clear_action'
     * that perform delete actions on the database
     * <p>
     * Runs on the UI thread.
     * </p>
     */
    private void hideDeleteActions() {
        mGroupDeleteActions.setVisibility(View.GONE);
    }

    /**
     * Method that shows the buttons 'R.id.button_main_delete_action' and 'R.id.button_main_clear_action'
     * that perform delete actions on the database
     * <p>
     * Runs on the UI thread.
     * </p>
     */
    private void showDeleteActions() {
        mGroupDeleteActions.setVisibility(View.VISIBLE);
    }

    /**
     * Method that changes the image of the Water cup being shown
     * based on the boolean state #resetState being passed.
     * <p>
     * Runs on the UI thread.
     * </p>
     *
     * @param resetState <b>TRUE</b> means the user drank/recorded atleast one glass of water.
     *                   Image shown is a Water cup that is filled in this case.
     *                   <br/>FALSE</b> means the user never drank/recorded a glass of water drank.
     *                   Image shown is an Empty Water cup in this case.
     */
    private void modifyWaterCupState(boolean resetState) {
        //Updating the Image based on the reset state passed
        mCupWaterImageView.setImageResource(
                resetState ? R.drawable.ic_main_cup_water_empty : R.drawable.ic_main_cup_water_filled
        );
    }

    /**
     * Method that updates the TextView 'R.id.textview_main_water_record_count'
     * with the total value of 'glass_water_count' from the database.
     * This also takes care of updating the corresponding water cup image.
     * <p>
     * Runs on the UI thread.
     * </p>
     *
     * @param recordCount is the Integer value of the total 'glass_water_count' from the database.
     */
    private void modifyTotalGlassOfWaterCountText(long recordCount) {
        //Updating the record count value on the Text 'R.id.textview_main_water_record_count'
        mWaterRecordCountTextView.setText(String.valueOf(recordCount));

        //Updating the Water Cup Image state based on the total value of 'glass_water_count'
        modifyWaterCupState(recordCount == 0);
    }

    /**
     * Method that updates the TextView 'R.id.textview_main_db_record_count'
     * with the total count of records from the database.
     * <p>
     * Runs on the UI thread.
     * </p>
     *
     * @param recordCount is the Integer value of the total count of records from the database
     */
    private void modifyTotalRecordCountText(long recordCount) {
        //Updating the record count value on the Text 'R.id.textview_main_db_record_count'
        TextAppearanceUtility.setHtmlText(mDatabaseRecordCountTextView,
                getString(R.string.main_db_record_count_text, recordCount));

        //Coloring the record count value in the text based on its value
        if (recordCount > 0) {
            //Coloring black when above 0
            TextAppearanceUtility.modifyTextColor(mDatabaseRecordCountTextView,
                    String.valueOf(recordCount),
                    ContextCompat.getColor(this, android.R.color.black)
            );
        } else {
            //Coloring Red when equal to 0
            TextAppearanceUtility.modifyTextColor(mDatabaseRecordCountTextView,
                    String.valueOf(recordCount),
                    ContextCompat.getColor(this, R.color.mainDbRecordCountTextColorRed500)
            );
        }
    }

    /**
     * Method that triggers the queries to update the record counts on the
     * TextViews 'R.id.textview_main_db_record_count' and
     * 'R.id.textview_main_water_record_count'
     * <p>
     * Runs on a worker thread.
     * </p>
     *
     * @see #modifyTotalRecordCountText
     * @see #modifyTotalGlassOfWaterCountText
     */
    private void reloadRecordCounts() {
        //Retrieving the total record count from the database for UI update
        final long totalRecordCount = HydrationDao.getTotalRecordCount(mDbHelper);
        //Retrieving the total value of 'glass_water_count' from the database for UI update
        final long totalGlassOfWaterCount = HydrationDao.getTotalGlassOfWaterCount(mDbHelper);

        //Executing on UI thread
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                //Loading the total record count of Database
                modifyTotalRecordCountText(totalRecordCount);

                //Loading the total of 'glass_water_count'
                modifyTotalGlassOfWaterCountText(totalGlassOfWaterCount);
            }
        });

    }

    /**
     * Called when a view has been clicked.
     *
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {
        //Executing based on the view clicked
        switch (view.getId()) {
            case R.id.button_main_insert_action:
                //For 'INSERT' Button

                //Retrieving the EditText Input value for 'Glass of Water' drank
                String waterCupCountInputStr = mWaterCupCountEditText.getText().toString();
                if (!TextUtils.isEmpty(waterCupCountInputStr)) {
                    //When Not empty, parse for the value
                    final int waterCupCount = Integer.parseInt(waterCupCountInputStr);

                    //Clearing focus on the EditText
                    mWaterCupCountEditText.clearFocus();
                    mWaterCupCountEditText.setFocusable(false);
                    mWaterCupCountEditText.setFocusableInTouchMode(false);

                    //Hiding the error field
                    mWaterCupCountEditTextLayout.setErrorEnabled(false);

                    //Executing on a worker thread
                    mWorkHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Invoking with the number of 'Glass of Water' entered by the user
                            doInsertAction(waterCupCount);
                        }
                    });

                    //Making the EditText focusable
                    mWaterCupCountEditText.setFocusable(true);
                    mWaterCupCountEditText.setFocusableInTouchMode(true);

                } else {
                    //When the input is Empty, displaying an error message underneath the input,
                    //seeking attention of the user
                    mWaterCupCountEditTextLayout.setError(getString(R.string.main_error_cup_water_count_input_empty));

                    //Setting focus on the EditText
                    mWaterCupCountEditText.setFocusable(true);
                    mWaterCupCountEditText.setFocusableInTouchMode(true);
                    mWaterCupCountEditText.requestFocus();

                }

                break;
            case R.id.button_main_delete_action:
                //For 'DELETE' Button

                //Executing on a worker thread
                mWorkHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        doDeleteAction();
                    }
                });
                break;
            case R.id.button_main_clear_action:
                //For 'CLEAR' Button

                //Executing on a worker thread
                mWorkHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        doClearAction();
                    }
                });
                break;
        }
    }

    /**
     * Method that inserts a record into the database
     * when the 'INSERT' Button ('R.id.button_main_insert_action') is clicked.
     * <p>
     * Runs on a worker thread.
     * </p>
     *
     * @param inputGlassOfWaterCount is the number of 'Glass of Water' drank by the user
     *                               which is captured from the UI through
     *                               the EditText 'R.id.edittext_main_cup_water_count'
     */
    private void doInsertAction(int inputGlassOfWaterCount) {
        //Executing insert and retrieving the Key of the new record inserted
        long insertedRecordKey = HydrationDao.insertRecord(mDbHelper, inputGlassOfWaterCount);

        //Evaluating the Key of the new record
        if (insertedRecordKey > -1) {
            //When the record is successfully inserted

            //Retrieving the latest record details from the database for UI update
            final ContentValues recentRecordDetails = HydrationDao.getRecentRecordDetails(mDbHelper);

            //Executing on UI thread
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    //Displaying a Toast message on success of inserting a new record
                    displayToast(getString(R.string.main_msg_success_insert));

                    //Updating the Table Cells of 'Last Record Inserted' Column with this record data
                    modifyInsertColumnData(recentRecordDetails);

                    //Making the delete buttons visible when the record is inserted
                    if (mGroupDeleteActions.getVisibility() == View.GONE) {
                        showDeleteActions();
                    }
                }
            });

            //Reloading the total record count of Database and that of 'glass_water_count'
            reloadRecordCounts();

        } else {
            //When failed to insert a record

            //Executing on UI thread
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    //Displaying a Toast message on failure of inserting a new record
                    displayToast(getString(R.string.main_msg_failure_insert));
                }
            });

        }
    }

    /**
     * Method that deletes the latest record from the database
     * when the 'DELETE' Button ('R.id.button_main_delete_action') is clicked.
     * <p>
     * Runs on a worker thread.
     * </p>
     */
    private void doDeleteAction() {
        //Retrieving the details of the latest record for updating the display after delete
        final ContentValues recentRecordDetails = HydrationDao.getRecentRecordDetails(mDbHelper);

        //Retrieving the Key of the record to be deleted
        int keyToDelete = recentRecordDetails.getAsInteger(HydrationContract.HydrationEntry._ID);

        //Deleting the record
        int countOfRecordsDeleted = HydrationDao.deleteRecordByKey(mDbHelper, keyToDelete);

        if (countOfRecordsDeleted > 0) {
            //When the record is successfully deleted

            //Retrieving the current total count of records in the database, for UI update
            final long totalRemainingRecordCount = HydrationDao.getTotalRecordCount(mDbHelper);

            //Executing on UI thread
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    //Displaying a Toast message on success of deleting the latest record by Key
                    displayToast(getString(R.string.main_msg_success_delete));

                    //Updating the Table Cells of 'Last Record Deleted' Column with this record data which was deleted
                    modifyDeleteColumnData(recentRecordDetails);

                    //Checking if there are NO records remaining
                    if (totalRemainingRecordCount == 0) {
                        //Hiding the delete actions when no records are present
                        hideDeleteActions();

                        //Clearing the content on Edit Text Input for 'Glass of Water' count
                        mWaterCupCountEditText.setText("");
                    }
                }
            });

            //Reloading the total record count of Database and that of 'glass_water_count'
            reloadRecordCounts();

        } else {
            //When failed to delete the record

            //Executing on UI thread
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    //Displaying a Toast message on failure of deleting the record
                    displayToast(getString(R.string.main_msg_failure_delete));
                }
            });

        }
    }

    /**
     * Method that deletes all the records from the database
     * when the 'CLEAR' Button ('R.id.button_main_clear_action') is clicked.
     * <p>
     * Runs on a worker thread.
     * </p>
     */
    private void doClearAction() {
        //Clearing the records from the database
        final int countOfRecordsDeleted = HydrationDao.deleteAllRecords(mDbHelper);

        if (countOfRecordsDeleted > 0) {
            //When all records are successfully deleted

            //Executing on UI thread
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    //Displaying a Toast message on success of deleting all the records
                    displayToast(getString(R.string.main_msg_success_delete_all));

                    //Clearing the content from 'Last Record Inserted' Column
                    clearInsertColumnContent();

                    //Clearing the content from 'Last Record Deleted' Column
                    clearDeleteColumnContent();

                    //Clearing the content on Edit Text Input for 'Glass of Water' count
                    mWaterCupCountEditText.setText("");

                    //Hiding the delete actions when no records are there
                    hideDeleteActions();
                }
            });

            //Reloading the total record count of Database and that of 'glass_water_count'
            reloadRecordCounts();

        } else {
            //When failed to delete all the records

            //Executing on UI thread
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    //Displaying a Toast message on failure of deleting the records
                    displayToast(getString(R.string.main_msg_failure_delete_all));
                }
            });

        }
    }

    /**
     * Method that displays the toast message after canceling any previous active toast
     * <p>
     * Runs on the UI thread.
     * </p>
     *
     * @param message String representing the message to be displayed
     */
    private void displayToast(String message) {
        //Clearing any active Toast first
        if (mToast != null) {
            mToast.cancel();
        }
        //Reinitializing the toast with the message requested
        mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        //Displaying the Toast
        mToast.show();
    }

    /**
     * Perform any final cleanup before an activity is destroyed.  This can
     * happen either because the activity is finishing (someone called
     * {@link #finish} on it, or because the system is temporarily destroying
     * this instance of the activity to save space.  You can distinguish
     * between these two scenarios with the {@link #isFinishing} method.
     *
     * @see #onPause
     * @see #onStop
     * @see #finish
     * @see #isFinishing
     */
    @Override
    protected void onDestroy() {
        //Releasing the database connection if any
        if (mDbHelper != null) {
            mDbHelper.close();
        }

        //Stopping the background thread if any and releasing the associated looper
        if (mDatabaseHandlerThread != null) {
            mDatabaseHandlerThread.quit();
        }

        //Propagating call to Super
        super.onDestroy();
    }

    /**
     * Method initializes the contents of the Activity's standard options menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflating the Menu options from activity_main.xml
        getMenuInflater().inflate(R.menu.activity_main, menu);

        //Returning True for the menu to be displayed
        return true;
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen.
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Handling the Menu item selected based on their id
        switch (item.getItemId()) {
            case R.id.menu_about:
                //For the "About" Menu

                //Starting the AboutActivity
                Intent aboutActivityIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutActivityIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}