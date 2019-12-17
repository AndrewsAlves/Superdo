package com.andysapps.superdo.todo;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.andysapps.superdo.todo.enums.BucketColors;
import com.andysapps.superdo.todo.enums.TaskListing;
import com.andysapps.superdo.todo.manager.SharedPrefsManager;
import com.andysapps.superdo.todo.model.Bucket;
import com.andysapps.superdo.todo.model.SuperDate;
import com.andysapps.superdo.todo.model.Task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Handler;

/**
 * Created by Andrews on 20,August,2019
 */

public class Utils {

    public static String[] monthDates = {"1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th", "9th", "10th", "11th", "12th",
            "13th", "14th", "15th", "16th", "17th", "18th", "19th", "20th", "21st", "22nd", "23rd", "24th", "25th", "26th", "27th", "28th", "29th", "30th", "31st"};

    public static void showSoftKeyboard(Context context, View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static Calendar getStartDate() {
        Calendar output = Calendar.getInstance();
        output.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
        output.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        output.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH));
        return output;
    }

    public static Calendar getEndDate() {
        Calendar output2 = Calendar.getInstance();
        output2.set(Calendar.YEAR, 2020);
        output2.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        output2.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH));
        return output2;
    }

    public static Calendar getTomorrow() {
        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.DATE, 1);
        return c;
    }

    public static boolean isSuperDateToday(SuperDate superdate) {

        if (superdate == null) {
            return false;
        }

        if (superdate.getDate() == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        && superdate.getMonth() - 1 == Calendar.getInstance().get(Calendar.MONTH)
        && superdate.getYear() == Calendar.getInstance().get(Calendar.YEAR)) {
            return true;
        }

        return false;
    }

    public static boolean isSuperDateTomorrow(SuperDate superdate) {

        if (superdate == null) {
            return false;
        }

        if (superdate.getDate() == getTomorrow().get(Calendar.DAY_OF_MONTH)
                && superdate.getMonth() - 1 == getTomorrow().get(Calendar.MONTH)
                && superdate.getYear() == getTomorrow().get(Calendar.YEAR)) {
            return true;
        }

        return false;
    }

    public static boolean isSuperdateIsPast(SuperDate superdate) {

        if (superdate == null) {
            return false;
        }

        if (superdate.getYear() < Calendar.getInstance().get(Calendar.YEAR)) {
            return true;
        }

        if (superdate.getYear() == Calendar.getInstance().get(Calendar.YEAR)
                && superdate.getMonth() - 1 < Calendar.getInstance().get(Calendar.MONTH)) {
            return true;
        }

        if (superdate.getYear() == Calendar.getInstance().get(Calendar.YEAR)
                && superdate.getMonth() - 1 == Calendar.getInstance().get(Calendar.MONTH)
                && superdate.getDate() < Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
            return true;
        }

        return false;
    }

    public static boolean isSuperdateIsSomeday(SuperDate superdate) {

        if (superdate == null) {
            return true;
        }

        if (superdate.getYear() > Calendar.getInstance().get(Calendar.YEAR)) {
            return true;
        }

        if (superdate.getYear() == Calendar.getInstance().get(Calendar.YEAR)
                && superdate.getMonth() - 1 > Calendar.getInstance().get(Calendar.MONTH)) {
            return true;
        }

        if (superdate.getYear() == Calendar.getInstance().get(Calendar.YEAR)
                && superdate.getMonth() - 1 == Calendar.getInstance().get(Calendar.MONTH)
                && superdate.getDate() > Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
            return true;
        }

        return false;
    }

    public static String getMonthString(int month) {
        switch (month) {
            case 1:
                return "JAN";
            case 2:
                return "FEB";
            case 3:
                return "MAR";
            case 4:
                return "APR";
            case 5:
                return "MAY";
            case 6:
                return "JUNE";
            case 7:
                return "JULY";
            case 8:
                return "AUG";
            case 9:
                return "SEPT";
            case 10:
                return "OCT";
            case 11:
                return "NOV";
            case 12:
                return "DEC";

                default:
                    return "   ";
        }
    }

    public static String getMonthStringLong(int month) {
        switch (month) {
            case 1:
                return "January";
            case 2:
                return "February";
            case 3:
                return "March";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "August";
            case 9:
                return "September";
            case 10:
                return "October";
            case 11:
                return "November";
            case 12:
                return "December";

            default:
                return "   ";
        }
    }

    public static String getDateString(Date date) {

        String dateString = new SimpleDateFormat("dd-MM-yyyy").format(date);
        String[] splits = dateString.split("-");

        return splits[0] + " "
                + getMonthString(Integer.parseInt(splits[1])) + " "
                + splits[2];
    }

    public static String getTimeString(SuperDate date) {

        String timeString;

        if (date.hours > 12) {
            timeString = date.hours + " : " + date.minutes + " pm";
        } else {
            timeString = date.hours + " : " + date.minutes + " am";
        }

        return timeString;
    }

    public static SuperDate getSuperdateFromTimeStamp(long timestamp) {
        SuperDate superDate = new SuperDate(timestamp);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String dateString  = dateFormat.format(timestamp);
        String[] dateParts = dateString.split("-");

        superDate.setDoDate(
                Integer.parseInt(dateParts[0]),
                Integer.parseInt(dateParts[1]),
                Integer.parseInt(dateParts[2]));
        superDate.setTimestamp(new Date(timestamp));


        return superDate;
    }

    public static int getDefaultTime(TaskListing listing) {

        switch (listing) {
            case TODAY:
                if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 9 ) {
                    return 9;
                } else if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 12) {
                    return 12;
                } else if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 15) {
                    return 15;
                } else if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 18) {
                    return 18;
                }
                break;
            case TOMORROW:
            case SOMEDAY:
                return 9;
        }

        return 9;
    }

    public static int getTimeIcon(int hours) {
        if (hours <= 12) {
            return R.drawable.ic_morning;
        } else if (hours <= 17) {
            return R.drawable.ic_afternoon;
        } else if (hours <= 24 ) {
            return R.drawable.ic_night;
        }

        return R.drawable.ic_time_off;
    }

    public static int getColor(Context context, String colors) {

        if (colors == null) {
            return  context.getResources().getColor(R.color.grey4);
        }

        BucketColors colors1 = BucketColors.valueOf(colors);

        int color = R.color.grey4;
        switch (colors1) {
            case Red:
                color = R.color.lightRed;
                break;
            case Green:
                color = R.color.green;
                break;
            case Orange:
                color = R.color.orange;
                break;
            case SkyBlue:
                color = R.color.skyblue;
                break;
            case InkBlue:
                color = R.color.inkBlue;
                break;

        }

        return context.getResources().getColor(color);
    }



}
