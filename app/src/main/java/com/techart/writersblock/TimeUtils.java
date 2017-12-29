package com.techart.writersblock;

import com.google.firebase.database.ServerValue;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Kelvin on 06/08/2017.
 */



public final class TimeUtils {

    private static final int  MINUTE = 60;
    private static final int  HOUR = 3600;
    private static final int  DAY= 86400;
    private static final int  WEEK= 604800;
    private static final int  MONTH= 2628000;

    private static final int MILLISECONDS_IN_A_SECOND = 1000;

    private static final int  MINUTES_LESS_THAN_HOUR= 60000;
    private static final int  HOURS_LESS_THAN_DAY= 3600000;
    private static final int  DAYS_LESS_THAN_3_DAYS= 86400000;
    private static final int  WEEKS_LESS_THAN_MONTH= 604800000;

    private TimeUtils()
    {
    }


    private static long secToMilliSeconds(long timePostedInMilliseconds)
    {
        return timePostedInMilliseconds / MILLISECONDS_IN_A_SECOND;
    }

    public static String timeElapsed(long timePostedInMilliseconds)
    {
        long timeInSeconds = secToMilliSeconds(timePostedInMilliseconds);
        if (timeInSeconds < MINUTE)
        {
            return "just now";
        }
        else if (timeInSeconds < HOUR)
        {
            timeInSeconds = timeInSeconds/MINUTE;
            return setPlurality(timeInSeconds,"min") + " ago";

           /* if (setPlurality(timeInSeconds))
            {
                return "a min ago";
            }
            else
            {
                return timeInSeconds + " mins ago";
            }*/
        }
        else if (timeInSeconds < DAY)
        {
            timeInSeconds = timeInSeconds/HOUR;
            return setPlurality(timeInSeconds,"hr")+ " ago";

            /*if (setPlurality(timeInSeconds))
            {
                return "an hr ago";
            }
            else
            {
                return timeInSeconds + " hrs ago";
            }*/
        }
        else if(timeInSeconds < WEEK)
        {
            timeInSeconds = timeInSeconds/DAY;
            return setPlurality(timeInSeconds,"day") + " ago";

           /* if (setPlurality(timeInSeconds))
            {
                return "a day ago";
            }
            else
            {
                return timeInSeconds + " days ago";
            }*/
        }
        else if (timeInSeconds < MONTH)
        {
            timeInSeconds = timeInSeconds/WEEK;
           return setPlurality(timeInSeconds,"week") + " ago";
           /*
            if (setPlurality(timeInSeconds))
            {
                return "a week ago";
            }
            else
            {
                return timeInSeconds + " weeks ago";
            }*/
        }
        else
        {
            return timeStampToDate(timePostedInMilliseconds);
        }
    }

    public static  String setPlurality(long value, String word)
    {
        if(value % 10 == 1)
        {
            return "a " + word;
        }
        return value + " " + word + "s";
    }

    public static String timeStampToDate(long timePostedInMilliseconds)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d, HH:mm:ss");
        return simpleDateFormat.format(timePostedInMilliseconds);
    }

    public static long currentTime()
    {
        Date date = new Date();
        return date.getTime();
    }
}
