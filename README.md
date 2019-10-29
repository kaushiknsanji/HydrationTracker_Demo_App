# HydrationTracker - The Habit Tracking App

![GitHub](https://img.shields.io/github/license/kaushiknsanji/HydrationTracker_Demo_App)  ![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/kaushiknsanji/HydrationTracker_Demo_App)  ![GitHub repo size](https://img.shields.io/github/repo-size/kaushiknsanji/HydrationTracker_Demo_App)  ![Minimum API level](https://img.shields.io/badge/API-15+-yellow)

This App has been developed as part of the **Udacity Android Basics Nanodegree Course** for the Exercise Project **"Habit Tracker App"**. The Habit considered here in this project is the daily habit of Hydration. It is recommended to drink 1-2 litres of Water per day for Good Health, but this also depends on how much one expends/dehydrates. So, at least 1 litre of Water per day is a Good Habit and 1 litre amounts to 8 Glasses of Water.

---

## App Compatibility

Android device running with Android OS 4.0.4 (API Level 15) or above. Best experienced on Android Nougat 7.1 and above. Designed for Phones and NOT for Tablets.

---

## Branch details

* In comparison to the [main/udacity](https://github.com/kaushiknsanji/HydrationTracker_Demo_App/tree/udacity) branch, this branch contains the UI Table designed using [ConstraintLayout + Barrier](/app/src/main/res/layout/layout_table_barrier.xml).
* Initially developed with barriers, but owing to stability issues and slowness observed in Android 5 and lower, this was later redesigned with [TableLayout](/app/src/main/res/layout/layout_table.xml).
* This branch also contains the UI Table designed with `TableLayout` for the comparison + study on the lines of performance. In comparison, we can observe that the UI Table designed with `ConstraintLayout + Barrier` takes more time to populate the data and also to reload on configuration change. The layout is just an `<include/>` in the [activity_main.xml](https://github.com/kaushiknsanji/HydrationTracker_Demo_App/blob/test-table_barrier/app/src/main/res/layout/activity_main.xml#L96-L105), so that they can easily be swapped for analysis.

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