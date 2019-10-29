/*
 * Copyright 2018 Kaushik N. Sanji
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.kaushiknsanji.hydrationtrackerdemo.data;

import android.provider.BaseColumns;

/**
 * API Contract class for Hydration Database
 *
 * @author Kaushik N Sanji
 */
public class HydrationContract {

    //Private Constructor to avoid instantiating the Contract
    private HydrationContract() {
    }

    /**
     * Inner class that defines the constant values for the 'Hydration' database table.
     * Each entry in the table represents a record of the Hydration Activity.
     */
    public static final class HydrationEntry implements BaseColumns {

        /**
         * Name of the Database Table
         */
        public static final String TABLE_NAME = "hydration";

        /**
         * The number of glasses of water drank.
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_GLASS_WATER_COUNT = "glass_water_count";

        /**
         * Alias Column Name for the SUM of 'glass_water_count'
         * <P>Type: INTEGER</P>
         */
        public static final String ALIAS_COLUMN_GLASS_WATER_COUNT_TOTAL = "glass_water_count_total";

        /**
         * Date of the Activity, stored in a readable form.
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_ACTIVITY_DATE = "activity_date";

        /**
         * Time of the Activity, stored in a readable form.
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_ACTIVITY_TIME = "activity_time";

        /**
         * DateTime of the Activity, stored in ISO DateTimestamp format.
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_DATETIMESTAMP = "datetimestamp";

    }
}
