package com.techart.writersblock;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Validates entries on UI components
 * Created by Kelvin on 17/09/2017.
 */

public final class EditorUtils {

    private static int lineCount = 10;
    private EditorUtils()
    {
    }

    protected static Typeface getTypeFace(Context context){
        return Typeface.createFromAsset(getAssets(context),"fonts/time-new-roman.ttf");
    }

    protected static AssetManager getAssets(Context context) {

        return context.getAssets();
    }

    /**
     * Inspects if the line count is not less than 10
     * @param context The context of the invoking method
     * @param layOutLineCount The number of lines written in a particular editText component
     * @return true if it confines to the condition
     */
    protected static boolean validateMainText(Context context, int layOutLineCount) {
        if (layOutLineCount <= lineCount) {
            Toast.makeText(context, "Text too short, at least " + lineCount + " lines", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    protected static boolean compareStrings(String oldText, String newText)
    {
        return oldText.equals(newText);
    }

    protected static boolean isEmpty(Context context, String title, String placeHolder)
    {
        if (title.isEmpty())
        {
            Toast.makeText(context,"Type in "+ placeHolder,Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    protected static boolean validateEntry(Context context, int iTemPosition, String title, TextView tv)
    {
        if (iTemPosition == 0)
        {
            Toast.makeText(context,"Kindly select category",Toast.LENGTH_LONG).show();
            return false;
        }
        else if (title.length() == 0)
        {
            Toast.makeText(context,"Kindly set story title",Toast.LENGTH_LONG).show();
            return false;
        }
        else if (tv.getLayout().getLineCount() < 2)
        {
            Toast.makeText(context,"Description should be at least 2 lines",Toast.LENGTH_LONG).show();
            return false;
        }
        else
        {
            return true;
        }
    }

    protected static boolean dropDownValidator(Context context, String signUpAs)
    {
        if (signUpAs == null)
        {
            Toast.makeText(context, "Select either Writer or Reader", Toast.LENGTH_LONG).show();
            return false;
        }
        else
        {
            return true;
        }
    }

    protected static boolean doPassWordsMatch(Context context, String first, String second)
    {
        if (first.equals(second))
        {
            return true;
        }
        else
        {
            Toast.makeText(context,"Ensure that passwords match",Toast.LENGTH_LONG).show();
            return false;
        }
    }

    protected static boolean isEmailValid(Context context, String email) {
        //CharSequence target = email;
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return true;
        } else {
            Toast.makeText(context, "Email address not valid", Toast.LENGTH_LONG).show();
            return false;
        }
    }
}
