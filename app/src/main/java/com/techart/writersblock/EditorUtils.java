package com.techart.writersblock;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Kelvin on 17/09/2017.
 */

public final class EditorUtils {

    private static int lineCount = 10;


    private EditorUtils()
    {
    }

    public static boolean validateMainText(Context context,int layOutLineCount) {
        if (layOutLineCount <= lineCount) {
            Toast.makeText(context, "Text too short, at least " + lineCount + " lines", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public static boolean compareStrings(String oldText, String newText)
    {
        return oldText.equals(newText);
    }

    public static boolean isEmpty(Context context, String title, String placeHolder)
    {
        if (title.isEmpty())
        {
            Toast.makeText(context,"Type in "+ placeHolder,Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    public static boolean validateEntry(Context context, int iTemPosition,String title,TextView tv)
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

    public static boolean dropDownValidater(Context context, int iTemPosition)
    {
        if (iTemPosition == 0)
        {
            Toast.makeText(context,"Select who you would like to register as",Toast.LENGTH_LONG).show();
            return false;
        }
        else
        {
            return true;
        }
    }

    public static boolean doPassWordsMatch(Context context,String first, String second)
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
}
