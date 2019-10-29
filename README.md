# HydrationTracker - The Habit Tracking App

![GitHub](https://img.shields.io/github/license/kaushiknsanji/HydrationTracker_Demo_App)  ![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/kaushiknsanji/HydrationTracker_Demo_App)  ![GitHub repo size](https://img.shields.io/github/repo-size/kaushiknsanji/HydrationTracker_Demo_App)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/kaushiknsanji/HydrationTracker_Demo_App)  ![GitHub All Releases](https://img.shields.io/github/downloads/kaushiknsanji/HydrationTracker_Demo_App/total) ![GitHub search hit counter](https://img.shields.io/github/search/kaushiknsanji/HydrationTracker_Demo_App/Habit%20Tracker%20App) ![Minimum API level](https://img.shields.io/badge/API-15+-yellow)

This App has been developed as part of the **Udacity Android Basics Nanodegree Course** for the Exercise Project **"Habit Tracker App"**. The Habit considered here in this project is the daily habit of Hydration. It is recommended to drink 1-2 litres of Water per day for Good Health, but this also depends on how much one expends/dehydrates. So, at least 1 litre of Water per day is a Good Habit and 1 litre amounts to 8 Glasses of Water.

---

## App Compatibility

Android device running with Android OS 4.0.4 (API Level 15) or above. Best experienced on Android Nougat 7.1 and above. Designed for Phones and NOT for Tablets.

---

## Rubric followed for the Project

* The Database schema needs to have columns of at least two different datatypes (eg., INTEGER, TEXT). 
* There should be a Contract Class that defines the name of the Table and constants. Contains an Inner Class for each table created.
* Needs to have a class that extends `SQLiteOpenHelper` and overrides `onCreate()` and `onUpgrade()` methods.
* A method that inserts a record into the database using ContentValues object.
* A method that queries a record from the database and extracts data from the Cursor object returned.
* No external libraries to be used for the database CRUD calls.

---

## Things explored/developed in addition to the above defined Rubric

* Although defined in the Rubric that no UI is required, I went ahead and did a simple UI to display only the recent records, to know what is going on in the backend.
* Explored `Handlers` and `HandlerThreads` to perform database operations in a background thread.
* [TextAppearanceUtility](/app/src/main/java/com/example/kaushiknsanji/hydrationtrackerdemo/utils/TextAppearanceUtility.java) for decorating `TextViews` using Spannables, for html content in text, and coloring a part of the text.
* Explored the use of `TextInputLayout` and `TextInputEditText`.

---

## Design and Implementation of the App

<!-- Video of the App -->
[![Video of Complete App Flow](https://i.ytimg.com/vi/x1TKTl9uhRE/maxresdefault.jpg)](https://youtu.be/x1TKTl9uhRE)

App is designed to keep track of the Hydration Habit through a Simple UI. Under the hood, it stores your log /or track records in a `SQLite` database. Following is the structure of the database table used in this App.

**TABLE NAME:** 'hydration'

|COLUMN|DATATYPE|
|---|---|
|_id|INTEGER (PRIMARY KEY)|
|glass_water_count|INTEGER NOT NULL|
|activity_date|TEXT NOT NULL|
|activity_time|TEXT NOT NULL|
|datetimestamp|TEXT NOT NULL|

### SQLiteOpenHelper class

This database table is created and managed by the class that extends the `SQLiteOpenHelper`, [HydrationDbHelper](/app/src/main/java/com/example/kaushiknsanji/hydrationtrackerdemo/data/HydrationDbHelper.java). This class is exposed as a Singleton class so that only one instance/connection is created and used by the entire app. This creates the database table by executing the below SQL `CREATE TABLE` query.

```sql
CREATE TABLE hydration (
_id INTEGER PRIMARY KEY AUTOINCREMENT,
glass_water_count INTEGER NOT NULL DEFAULT 1,
activity_date TEXT NOT NULL,
activity_time TEXT NOT NULL,
datetimestamp TEXT NOT NULL
)
```

`HydrationDbHelper` is also responsible for Version management. When the version is updated (due to a new column being added/removed or the datatype/default values are being changed) and rebuilt, `onUpgrade()` method will be called which **Drops** the table using the query below and recreates the table again using the above SQL `CREATE TABLE` query.

```sql
DROP TABLE IF EXISTS hydration
```

### The Contract class

The above Table name and its Column Names are defined as constants in a class known as the Contract Class, [HydrationContract](/app/src/main/java/com/example/kaushiknsanji/hydrationtrackerdemo/data/HydrationContract.java). It contains an Inner class for the 'hydration' table, that extends the `BaseColumns` Interface which provides the basic mandatory constants required for a Table. Constants for the table 'hydration' are defined here. Defining such a Contract Class removes the possibility of introducing spelling errors when generating SQL commands and also for defining/updating the database schema.

### CRUD Operations

The Database CRUD Operations are managed by a separate class [HydrationDao](/app/src/main/java/com/example/kaushiknsanji/hydrationtrackerdemo/data/HydrationDao.java). This can do the following things -
* Insert a new record into the table 'hydration' with the count of 'Glass of Water' drank by the user.
* Delete a record identified by its Primary Key.
* Delete all the records in the table 'hydration'.
* Retrieve the details of the latest record in the table 'hydration'.
* Retrieve the sum of 'Glass of Water' drank by the user, till date.
* Retrieve the total number of records found in the table 'hydration'.

### UI of the App

The UI of the App displays the following list of information from the database, that tells the user as to what happens with each INSERT/DELETE operation-
* Shows the Sum of 'Glass of Water' drank/recorded by the user, till date.
* Shows the total number of records in the database.
* Shows the latest record that was inserted, if any.
* Shows the latest record that was deleted, if any.

<!-- Image for Database Counts -->
**Sample Images**

|PORTRAIT|LANDSCAPE|
|---|---|
|![database_counts_sample](https://user-images.githubusercontent.com/26028981/39393152-7882a7ee-4adf-11e8-844a-50939858f37a.png)|![database_counts_sample_land](https://user-images.githubusercontent.com/26028981/39393153-7abe071a-4adf-11e8-84f3-cd4f87503dc1.png)|
|![table_sample](https://user-images.githubusercontent.com/26028981/39393156-828d2886-4adf-11e8-8239-02ce2357afd8.png)|![table_sample_1_land](https://user-images.githubusercontent.com/26028981/39393157-846728f0-4adf-11e8-84d7-4c8602c0a23c.png)<br/>![table_sample_2_land](https://user-images.githubusercontent.com/26028981/39393158-85897562-4adf-11e8-981e-daab09ec2966.png)|

The layout shown here is [activity_main.xml](/app/src/main/res/layout/activity_main.xml), which is inflated and managed by the [MainActivity](/app/src/main/java/com/example/kaushiknsanji/hydrationtrackerdemo/MainActivity.java). During the initial launch of the App, there will be no records in the database table. In such a case, user will be presented with only the **INSERT** button that inserts a record into the database table. This also happens when the user inserts and deletes all the records in the database table.

<!-- Image for the initial launch -->
<img src="https://user-images.githubusercontent.com/26028981/39393159-8a1b2378-4adf-11e8-90d0-13988e8554e5.png" width="40%" />  <img src="https://user-images.githubusercontent.com/26028981/39393160-8c020a94-4adf-11e8-95fb-e0bb12a320b6.png" width="40%" />

All database CRUD operations are carried out in a background thread initiated by a `HandlerThread` with Background Thread Priority. When inserting a record into the database table using the **INSERT** Button, one needs to input the number of 'Glass of Water' drank, in the EditText above it, to record the same in database. Failing to do so, will show an Error message just below the EditText input. 

<!-- Image for empty input error -->
<!-- GIF for the INSERT Operation -->
<img src="https://user-images.githubusercontent.com/26028981/39393161-8f52c4e0-4adf-11e8-9538-e85469294314.png" width="40%" />  <img src="https://user-images.githubusercontent.com/26028981/39393164-98bf5a66-4adf-11e8-8d04-471be5ab5685.gif"/>

The `EditText` is framed using the `TextInputLayout` wrapper on `TextInputEditText`. `TextInputLayout` has been used to show the hint as a floating label when the user starts to type in the value. When the record is inserted successfully, the record details are reflected in the **Last Record Inserted** Column of the UI Table along with the updated values for the "total records" in Database and "Glass of Water" drank. The valid values for the input ranges from 1 to 3(inclusive). Any other number being input will not be accepted by the `TextInputEditText`.

Once a record is inserted into the database table, **DELETE** and **CLEAR** database actions buttons are revealed. The visibility of these buttons are controlled using the ConstraintLayout `Groups`. When there are no records in the database, these will be hidden again.

User can delete the latest record by clicking on the **DELETE** button. Once the latest record is deleted successfully, the details of the record deleted will appear in the **Last Record Deleted** Column of the UI Table. Corresponding decrease in the values for "total records" in Database and "Glass of Water" drank are updated as well.

<!-- GIF for the DELETE Operation -->
![delete_operation](https://user-images.githubusercontent.com/26028981/39393168-9c2e1444-4adf-11e8-965d-9038b0cc61f6.gif)

User can also delete all the records in the database table by just clicking on the **CLEAR** button. This will bring the UI of the App to its initial state, the state when there were no records in the database. 

<!-- GIF for the CLEAR Operation -->
![clear_operation](https://user-images.githubusercontent.com/26028981/39393169-a0ebf3d4-4adf-11e8-8a49-487a58223b81.gif)

The Key of the record, that is, for the column `_id` of the database table continues to auto-increment from its last value. So for any new inserts after clearing all the records from the database table, will have the value of the `_id` incremented from the value used for the previous record. **CLEAR** button will only reset the UI after deleting all the records in the database table and will NOT reset the database key. This is the inherent nature of database table keys.

Apart from the above, the UI also displays an intuitive icon for the Water cup. Water Cup is initially grey/empty when there are no records logged for the "Glass of Water" drank. When there are records, Water cup changes to a colored/filled Water Cup to indicate that the user has logged his/her hydration counts.

<!-- Image for empty cup -->
<!-- Image for filled cup -->
|Empty Water Cup|Filled Water Cup|
|---|---|
|![initial_launch_1](https://user-images.githubusercontent.com/26028981/39393159-8a1b2378-4adf-11e8-90d0-13988e8554e5.png)|![database_counts_sample](https://user-images.githubusercontent.com/26028981/39393152-7882a7ee-4adf-11e8-844a-50939858f37a.png)|

### The About Page

This can be viewed by going into the Overflow menu item **"About"** of the `MainActivity`. This page describes in brief about the app, and has links to my bio and the course details hosted by Udacity. This is shown by the activity [AboutActivity](/app/src/main/java/com/example/kaushiknsanji/hydrationtrackerdemo/AboutActivity.java) that inflates the layout [activity_about.xml](/app/src/main/res/layout/activity_about.xml).

<!-- Image for About page -->
<img src="https://user-images.githubusercontent.com/26028981/39393171-abba6872-4adf-11e8-8d1d-67d9260840c4.png" width="40%" />  <img src="https://user-images.githubusercontent.com/26028981/39393172-ad2d4a4e-4adf-11e8-98c8-eada3fbb931b.png" width="40%" />

---

## Branches in this Repository

* **[udacity](https://github.com/kaushiknsanji/HydrationTracker_Demo_App/tree/udacity)**
	* Contains the code submitted for review.
	* Contains the UI Table designed using `TableLayout`.
	* Updated Gradle version and applied valid lint corrections.
	* Added Copyright info.
* **[test-table_barrier](https://github.com/kaushiknsanji/HydrationTracker_Demo_App/tree/test-table_barrier)
	* Contains the UI Table designed using `ConstraintLayout + Barrier`.
	* Initially developed with barriers but owing to stability issues and slowness observed in Android 5 and lower, this was later redesigned with `TableLayout`.
	* This branch also contains the UI Table designed with `TableLayout` for the comparison + study on the lines of performance.

---

## Icon credits

Water cup icons used are made by <a href="http://www.freepik.com" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a>

---

## Review from the Reviewer (Udacity)

![review](https://user-images.githubusercontent.com/26028981/39418686-64334aaa-4c79-11e8-910f-2d4e5d0b6c92.PNG)

---

## License

```
Copyright 2018 Kaushik N. Sanji

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0
   
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```