package com.kplian.pxpui.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Osama 19.10.17
 */
public class CommonUtils {
    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static void customTypeface(@NonNull TextView view, @NonNull String typeface) {
        Typeface t = Typeface.createFromAsset(view.getContext().getAssets(), typeface);
        view.setTypeface(t);
    }

    public static void installApp(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("Install App", 0).edit();
        editor.putBoolean("IS_INSTALLED", true);
        editor.apply();
    }

    public static boolean isInstalled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("Install App", 0);
        return prefs.getBoolean("IS_INSTALLED", false);
    }

    public static void setDeviceToken(Context context, String token) {
        SharedPreferences.Editor editor = context.getSharedPreferences("Install App", 0).edit();
        editor.putString("DEVICE_TOKEN", token);
        editor.apply();
    }

    public static String getDeviceToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("Install App", 0);
        return prefs.getString("DEVICE_TOKEN", "");
    }


    public static File getFile(Context context, String filename) throws IOException {
        File result;
        String path = context.getExternalFilesDir(null).getAbsolutePath() + filename;
        result = new File(path);
        if (result.exists())
            return result;

        return null;
    }

    public static String timestampToDate(long timestamp) {
        if ( timestamp < 0 ) {
            return "";
        }

        Date date = new Date(timestamp * 1000L);
        DateFormat df = new SimpleDateFormat("MMMM, dd, yyyy h:mm a", Locale.US);
        df.setTimeZone(TimeZone.getDefault());

        return df.format(date);
    }

    public static String timestampToTime(long timestamp) {
        if ( timestamp < 0 ) {
            return "";
        }

        Date date = new Date(timestamp * 1000L);
        DateFormat df = new SimpleDateFormat("h:mm a", Locale.US);
        df.setTimeZone(TimeZone.getDefault());

        return df.format(date);
    }

    public static String timestampToSmartString(long timestamp) {
        if ( timestamp < 0 ) {
            return "";
        }

        Date date = new Date(timestamp * 1_000L);
        long days = daysBetween(date, new Date());
        if ( -1 == days ) {// date > new Date()
            return timestampToDate(timestamp);
        } else if ( 0 == days) {
            return "Today " + timestampToTime(timestamp);
        } else if ( 1 == days) {
            return "Yesterday " + timestampToTime(timestamp);
        } else {
            return timestampToDate(timestamp);
        }
    }

    public static long daysBetween(@Nullable Date startDate, @Nullable Date endDate) {
        if ( null == startDate || null == endDate ) {
            return -1;
        }

        Calendar sDate = Calendar.getInstance();
        sDate.setTime(resetDateToDay(startDate));
        Calendar eDate = Calendar.getInstance();
        eDate.setTime(resetDateToDay(endDate));

        if ( eDate.before(sDate) ) {
            return -1;
        }

        long daysBetween = 0;
        while ( sDate.before(eDate) ) {
            sDate.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }

        return daysBetween;
    }

    public static Calendar resetCalendarToDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    @Nullable
    public static Date resetDateToDay(@Nullable Date date) {
        if ( null == date ) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return resetCalendarToDay(calendar).getTime();
    }
}
