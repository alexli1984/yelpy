package com.alapplication.yelpy.util;

import android.content.Context;

import com.alapplication.yelpy.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    private static final long MINUTE = 60;
    private static final long HOUR = 60 * MINUTE;
    private static final long DAY = 24 * HOUR;
    private static final long MONTH = 31 * DAY; //estimate
    private static final long YEAR = 12 * MONTH; //estimate;

    public static String getTimeDifference(Context context, Date date) {
        Date today = new Date();
        long timeDiff = Math.abs(today.getTime() - date.getTime()) / 1000; //in seconds
        if (timeDiff < MINUTE) {
            return context.getString(R.string.time_just_now);
        }
        String unit;
        long value;
        if (timeDiff < HOUR) {
            value = timeDiff / MINUTE;
            unit = context.getResources().getQuantityString(R.plurals.minute, (int) value);
        } else if (timeDiff < DAY) {
            value = timeDiff / HOUR;
            unit = context.getResources().getQuantityString(R.plurals.hour, (int) value);
        } else if (timeDiff < MONTH) {
            value = timeDiff / DAY;
            unit = context.getResources().getQuantityString(R.plurals.day, (int) value);
        } else if (timeDiff < YEAR) {
            value = timeDiff / MONTH;
            unit = context.getResources().getQuantityString(R.plurals.month, (int) value);
        } else {
            value = timeDiff / YEAR;
            unit = context.getResources().getQuantityString(R.plurals.year, (int) value);
        }
        return context.getString(R.string.time_ago, value, unit);
    }

    public static boolean isTablet(Context context) {
        return context.getResources().getConfiguration().smallestScreenWidthDp >= 900;
    }
}
