package com.techart.writersblock;

/**
 * Created by Kelvin on 06/08/2017.
 */



public final class NumberUtils {

    private static final int BILLION = 1000000000;
    private static final int MILLION = 1000000;
    private static final int THOUSAND = 1000;

    private NumberUtils()
    {
    }

    public static String shortenDigit(long count)
    {
        if (count < THOUSAND)
        {
            return Long.toString(count);
        }
        else if (count >= THOUSAND && count < MILLION)
        {
            return String.format(("%.1f"),((double)count)/THOUSAND) + "K";
        }
        else if (count >= MILLION && count < BILLION)
        {
            return String.format(("%.1f"),((double)count)/MILLION ) + "M";
        }
        else
        {
            return String.format(("%.1f"),((double)count)/BILLION )  + "G";
        }
    }
}
