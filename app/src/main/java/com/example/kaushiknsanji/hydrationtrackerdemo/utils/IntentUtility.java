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

package com.example.kaushiknsanji.hydrationtrackerdemo.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Utility class that provides convenience methods for the common Intents
 * used in the app.
 *
 * @author Kaushik N Sanji
 */
public class IntentUtility {

    /**
     * Private constructor to avoid instantiating this
     */
    private IntentUtility() {
    }

    /**
     * Method that opens a webpage for the URL passed
     *
     * @param context is the Context of the Calling Activity/Fragment
     * @param webUrl  is the String containing the URL of the Web Page to be launched
     */
    public static void openLink(Context context, String webUrl) {
        //Parsing the URL
        Uri webPageUri = Uri.parse(webUrl);
        //Creating an ACTION_VIEW Intent with the URI
        Intent webIntent = new Intent(Intent.ACTION_VIEW, webPageUri);
        //Checking if there is an Activity that accepts the Intent
        if (webIntent.resolveActivity(context.getPackageManager()) != null) {
            //Launching the corresponding Activity and passing it the Intent
            context.startActivity(webIntent);
        }
    }

}
