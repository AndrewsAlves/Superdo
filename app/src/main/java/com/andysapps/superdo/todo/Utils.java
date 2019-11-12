package com.andysapps.superdo.todo;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.andysapps.superdo.todo.manager.SharedPrefsManager;
import com.andysapps.superdo.todo.model.Bucket;
import com.andysapps.superdo.todo.model.SuperDate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Andrews on 20,August,2019
 */

public class Utils {


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

    public static String getDateString(Date date) {

        String dateString = new SimpleDateFormat("dd-MM-yyyy").format(date);
        String[] splits = dateString.split("-");

        return splits[0] + " "
                + getMonthString(Integer.parseInt(splits[1])) + " "
                + splits[2];
    }

    public static SuperDate getSuperdateFromTimeStamp(long timestamp) {
        SuperDate superDate = new SuperDate(timestamp);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String dateString  = dateFormat.format(timestamp);
        String[] dateParts = dateString.split("-");

        superDate.setDate(Integer.parseInt(dateParts[0]));
        superDate.setMonth(Integer.parseInt(dateParts[1]));
        superDate.setYear(Integer.parseInt(dateParts[2]));

        return superDate;
    }



}
