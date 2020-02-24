package com.andysapps.superdo.todo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.andysapps.superdo.todo.enums.BucketColors;
import com.andysapps.superdo.todo.enums.TaskListing;
import com.andysapps.superdo.todo.model.SuperDate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * Created by Andrews on 20,August,2019
 */

public class Utils {

    private static final String TAG = "Utils";

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
        output2.set(Calendar.YEAR, 2023);
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

    public static Calendar getIncrementedDay(int i) {
        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.DATE, i);
        return c;
    }

    public static TaskListing getTaskListed(SuperDate superdate) {

        if (isSuperDateToday(superdate)) {
            return TaskListing.TODAY;
        } else if (isSuperDateTomorrow(superdate)) {
            return TaskListing.TOMORROW;
        } else if (isSuperdateThisWeek(superdate)) {
            return TaskListing.THIS_WEEK;
        } else if (isSuperdateThisMonth(superdate)) {
            return TaskListing.THIS_MONTH;
        } else if (isSuperdateIsUpcoming(superdate)) {
            return TaskListing.UPCOMING;
        }

        return TaskListing.UPCOMING;
    }

    public static boolean isSuperDateToday(SuperDate superdate) {

        if (superdate == null) {
            return false;
        }

        return superdate.getDate() == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                && superdate.getMonth() - 1 == Calendar.getInstance().get(Calendar.MONTH)
                && superdate.getYear() == Calendar.getInstance().get(Calendar.YEAR);
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

    public static boolean isSuperDateIsPast(SuperDate superdate) {

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

    public static boolean isSuperdateThisWeek(SuperDate superdate) {

        if (superdate == null) {
            return false;
        }

        int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        int remainingDaysInThisWeek = 7 - today;

        List<Integer> weekDays = new ArrayList<>();

        for (int i = 1 ; i <= remainingDaysInThisWeek ; i++) {
            weekDays.add(getIncrementedDay(i).get(Calendar.DAY_OF_MONTH));
        }

        if (!isSuperDateToday(superdate)
                && !isSuperDateTomorrow(superdate)
                && superdate.getYear() == Calendar.getInstance().get(Calendar.YEAR)
                && superdate.getMonth() - 1 == Calendar.getInstance().get(Calendar.MONTH) ) {
           if (weekDays.contains(superdate.getDate())) {
               return true;
           }
        }

        return false;
    }

    public static boolean isSuperdateThisMonth(SuperDate superdate) {

        if (superdate == null) {
            return false;
        }

        return superdate.getYear() == Calendar.getInstance().get(Calendar.YEAR)
                && superdate.getMonth() - 1 == Calendar.getInstance().get(Calendar.MONTH)
                && !isSuperDateToday(superdate)
                && !isSuperDateTomorrow(superdate)
                && !isSuperdateThisWeek(superdate);
    }

    public static boolean isSuperdateIsUpcoming(SuperDate superdate) {

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

        return false;
    }

    public static SuperDate getRandomMonthDate() {

        int date = 29;
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int year = Calendar.getInstance().get(Calendar.YEAR);

        return new SuperDate(date, month, year, 0, 0);
    }

    public static SuperDate getRandomWeekDay() {

        int date = getIncrementedDay(2).get(Calendar.DAY_OF_MONTH);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int year = Calendar.getInstance().get(Calendar.YEAR);

       /* int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        int remainingDaysInThisWeek = 7 - today;

        if (remainingDaysInThisWeek < 2) {
            // this should give next week date
        }

        List<Integer> weekDays = new ArrayList<>();

        for (int i = 1 ; i <= remainingDaysInThisWeek ; i++) {
            weekDays.add(getIncrementedDay(i).get(Calendar.DAY_OF_MONTH));
        }

        SuperDate superdate = new SuperDate(date, month, year, 0, 0);

        for (Integer int1 : weekDays) {
            superdate.setDoDate(int1, month, year);
            if (!isSuperDateToday(superdate)
                    && !isSuperDateTomorrow(superdate)
                    && superdate.getYear() == Calendar.getInstance().get(Calendar.YEAR)
                    && superdate.getMonth() - 1 == Calendar.getInstance().get(Calendar.MONTH) ) {
               return superdate;
            }
        } */



        return new SuperDate(date, month, year, 0, 0);
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

    public static String getWeekDay(SuperDate date) {

        if (date == null) {
            return " ";
        }

        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, date.getDate());
        c.set(Calendar.MONTH, date.getMonth() - 1);
        c.set(Calendar.YEAR, date.getYear());

        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        switch (dayOfWeek) {
            case 1:
                return "Sun";
            case 2:
                return "Mon";
            case 3:
                return "Tue";
            case 4:
                return "Wed";
            case 5:
                return "Thu";
            case 6:
                return "Fri";
            case 7:
                return "Sat";
        }

        return " ";
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
            case UPCOMING:
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
            return  context.getResources().getColor(R.color.grey2);
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

    ///////////////////
    ////// HABITS
    //////////////////

    public static ArrayList<String> getHealthSuggestionList() {
        ArrayList<String> health = new ArrayList<>();

        health.add("Eat six small, healthful meals a day");
        health.add("Reduce your sugar intake");
        health.add("Work out for a total of an hour a day, five days a week");
        health.add("Talk with a positive person");
        health.add("Express gratitude");
        health.add("Laugh");
        health.add("Make protein the focus of every meal");
        health.add("Eat veggies everyday");
        health.add("Eat fruits & veggies");
        health.add("Drink a fruit juice everyday");
        health.add("Replace something white with something green");
        health.add("Get some sun");
        health.add("Walk a dog or pet an animal");
        health.add("Ask for a hug from one person a day");
        health.add("Sit in the sun");
        health.add("Take a small risk daily");
        health.add("Do something just for fun");

        Collections.shuffle(health);

        return health;
    }

    public static ArrayList<String> getFinanceSuggestionList() {
        ArrayList<String> health = new ArrayList<>();
        health.add("Pay bill ahead on time");
        health.add("Read financial book ");
        health.add("Track your spending ");
        health.add("Shop without credit card");
        health.add("be organised in your spending");
        health.add("inspired reading");
        health.add("keep your wallet thin");
        health.add("Set a budget for every shopping");

        return health;
    }

    public static ArrayList<String> getProductivitySuggestionList() {
        ArrayList<String> health = new ArrayList<>();
        health.add("Focus on most important tasks (MITs) first");
        health.add("Use the 80/20 rule");
        health.add("Don’t multitask");
        health.add("Manage your energy (not just time)");
        health.add("Use the Eisenhower Matrix to identify long-term priorities");
        health.add("Say no for outings");
        health.add("Perform focus mode");

        return health;
    }

    public static ArrayList<String> getDigitalWellbeingSuggestionList() {
        ArrayList<String> health = new ArrayList<>();
        health.add("Remove digital distractions");
        health.add("Always ask why when you pull out your phone");
        health.add("Organise your social apps");
        health.add("Dont use social media for a week");
        health.add("Track your screen time");
        health.add("Use productive apps");
        health.add("No Screens on bed");
        health.add("Spend more time on reading ebooks");
        health.add("Delete one unused apps everyday");

        return health;
    }

    public static ArrayList<String> getMindfullnessSuggestionList() {
        ArrayList<String> health = new ArrayList<>();
        health.add("Eat mindfully");
        health.add("Meditate for 15 minutes everyday");
        health.add("Focus on one task at a time");
        health.add("Feel Feelings");
        health.add("Look at a random things an try to observe");
        health.add("Develop one habit at a time");
        health.add("Practice gratitude everyday");
        health.add("Breathe deeply");
        health.add("Take 6 deep breath and notice them");
        health.add("Listen, don’t just hear");
        health.add("Smile to a stranger everyday");
        health.add("Speak kindly to yourself and watch your self words");

        Collections.shuffle(health);

        return health;
    }

    public static ArrayList<String> getAddictionSuggestionList() {
        ArrayList<String> health = new ArrayList<>();
        health.add("Don't Drink alcohol for a week");
        health.add("Don't Smoke weed");
        health.add("Aware of your cravings and let it go");
        health.add("Count your substance craving everyday");
        health.add("Don't smoke cigarette");
        health.add("identify your habitual thought pattern");

        return health;
    }


}
