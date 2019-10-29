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

import android.os.Build;
import android.support.annotation.ColorInt;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

/**
 * Utility class for Text Appearance related modifications done using classes like {@link Spannable}
 *
 * @author Kaushik N Sanji
 */
public class TextAppearanceUtility {

    /**
     * Private constructor to avoid instantiating this
     */
    private TextAppearanceUtility() {
    }

    /**
     * Method that sets the Html Text content on the TextView passed
     *
     * @param textView      is the TextView on which the Html content needs to be set
     * @param htmlTextToSet is the String containing the Html markup that needs to be set on the TextView
     */
    public static void setHtmlText(TextView textView, String htmlTextToSet) {
        //Initializing a SpannableStringBuilder to build the text
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //For Android N and above
            spannableStringBuilder.append(Html.fromHtml(htmlTextToSet, Html.FROM_HTML_MODE_COMPACT));
        } else {
            //For older versions
            spannableStringBuilder.append(Html.fromHtml(htmlTextToSet));
        }
        //Setting the Spannable Text on TextView with the SPANNABLE Buffer type,
        //for later modification on spannable if required
        textView.setText(spannableStringBuilder, TextView.BufferType.SPANNABLE);
    }

    /**
     * Method that updates the Text Color of a Text already set on a TextView
     *
     * @param textView    is the TextView whose Text is already set
     * @param textToColor is the Text in TextView that needs to be colored
     * @param color       is an Integer that represents the value of Color {@code AARRGGBB}
     *                    to be used for coloring the Text
     */
    public static void modifyTextColor(TextView textView, String textToColor, @ColorInt int color) {
        //Retrieving the Text from TextView as a Spannable Text
        Spannable spannable = (Spannable) textView.getText();

        //Setting the ForegroundColorSpan to color the part of the Text in the color passed
        int startIndex = TextUtils.indexOf(spannable, textToColor);
        int endIndex = startIndex + textToColor.length();
        spannable.setSpan(
                new ForegroundColorSpan(color),
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
    }

}
