package com.example.kaushiknsanji.hydrationtrackerdemo.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class that deals with DateTime and its formatting.
 *
 * @author Kaushik N Sanji
 */
public class DateUtility {

    //Constant for DateTime format that adheres to the ISO 8601 format
    private static final String ISO_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    /**
     * Private constructor to avoid instantiating this
     */
    private DateUtility() {
    }

    /**
     * Method that returns the date part in the sample format 'Jan 14, 2018'
     *
     * @param dateTimeInMillis is the datetime in milliseconds since unix epoch time
     * @return String representing the date part in the sample format 'Jan 14, 2018'
     */
    public static String getFormattedDate(long dateTimeInMillis) {
        //Check the datetime validity
        validateDateTime(dateTimeInMillis);
        //Construct the Date for the datetime passed
        Date date = new Date(dateTimeInMillis);
        //Return the formatted Date part in User's Locale
        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(date);
    }

    /**
     * Method that returns the time part in the sample format '1:50:00PM IST'
     *
     * @param dateTimeInMillis is the datetime in milliseconds since unix epoch time
     * @return String representing the time part in the sample format '1:50:00PM IST'
     */
    public static String getFormattedTime(long dateTimeInMillis) {
        //Check the datetime validity
        validateDateTime(dateTimeInMillis);
        //Construct the Date for the datetime passed
        Date date = new Date(dateTimeInMillis);
        //Return the formatted Time part in User's Locale
        return DateFormat.getTimeInstance(DateFormat.LONG).format(date);
    }

    /**
     * Method that returns the datetimestamp in the ISO format {@link #ISO_DATETIME_FORMAT}
     *
     * @param dateTimeInMillis is the datetime in milliseconds since unix epoch time
     * @return String representing the datetimestamp in the ISO format {@link #ISO_DATETIME_FORMAT}
     */
    public static String getDateTimeInIsoFormat(long dateTimeInMillis) {
        //Check the datetime validity
        validateDateTime(dateTimeInMillis);
        //Construct the Date for the datetime passed
        Date date = new Date(dateTimeInMillis);
        //Return the ISO formatted DateTime in User's Locale
        return new SimpleDateFormat(ISO_DATETIME_FORMAT, Locale.getDefault()).format(date);
    }

    /**
     * Method that validates the datetime passed.
     *
     * @param dateTimeInMillis is the datetime in milliseconds since unix epoch time
     */
    private static void validateDateTime(long dateTimeInMillis) {
        if (dateTimeInMillis <= 0) {
            throw new IllegalArgumentException("DateTime cannot be negative or 0. "
                    + "Please check the value passed.");
        }
    }
}
