package com.andysapps.superdo.todo;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.andysapps.superdo.todo.enums.BucketColors;
import com.andysapps.superdo.todo.enums.RepeatType;
import com.andysapps.superdo.todo.enums.TaskListing;
import com.andysapps.superdo.todo.manager.FirestoreManager;
import com.andysapps.superdo.todo.model.SuperDate;
import com.andysapps.superdo.todo.model.Task;
import com.andysapps.superdo.todo.model.taskfeatures.Repeat;

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

    public static boolean isNetworkConnected(Context context) {

        boolean hasNetwork = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        hasNetwork = cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();

        if (!hasNetwork) {
            makeToast(context, "Please connect to the internet");
        }

        return hasNetwork;
    }

    public static void makeToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

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

    public static SuperDate getSuperdateToday() {
        Calendar c = Calendar.getInstance();
        return new SuperDate(c.get(Calendar.DATE), c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR));
    }

    public static SuperDate getSuperdateTomorrow() {
        Calendar c = getTomorrow();
        return new SuperDate(c.get(Calendar.DATE), c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR));
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

    public static boolean isSuperDateYesterday(SuperDate superdate) {

        Calendar c1 = Calendar.getInstance(); // today
        c1.add(Calendar.DAY_OF_YEAR, -1); // yesterday

        Calendar c2 = getCalenderFromSuperDate(superdate);

        return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR));
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

    public static boolean isSuperDateIsFuture(SuperDate superdate) {

        if (superdate == null) {
            return false;
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

    public static boolean isBothDateAreSameDay(Calendar date1, Calendar date2 ) {
        if (date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR)
                && date1.get(Calendar.MONTH) == date2.get(Calendar.MONTH)
                && date1.get(Calendar.DAY_OF_MONTH) == date2.get(Calendar.DAY_OF_MONTH)) {
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

    public static boolean shouldAddTaskRepeat(Task task) {

        if (task.getRepeat() == null) {
            return false;
        }

        Repeat repeat = task.getRepeat();
        Calendar calendar = Calendar.getInstance();

        if (task.isTaskCompleted()) {
            if (task.getTaskCompletedDate() == null) {
                return false;
            }
            SuperDate completedDate = getSuperdateFromTimeStamp(task.getTaskCompletedDate().getTime());
            SuperDate todayDate = getSuperdateToday();
            if (completedDate.getDate() == todayDate.getDate()
                    && completedDate.getMonth() == todayDate.getMonth()
                    && completedDate.getYear() == todayDate.getYear()) {
                return false;
            }
        }
        
        switch (RepeatType.valueOf(task.getRepeat().getRepeatType())) {
            case Day:
                if (task.getRepeat().getDaysInterval() == 1) {
                    return true;
                } else {

                    if (isSuperDateToday(repeat.getStartDate())) {
                        return true;
                    }

                    Calendar intervalDate = getCalenderFromSuperDate(task.getRepeat().getStartDate());
                    intervalDate.set(Calendar.HOUR_OF_DAY, task.getRepeat().getStartDate().getHours());
                    intervalDate.set(Calendar.MINUTE, task.getRepeat().getStartDate().getMinutes());

                    while (isSuperDateIsPast(new SuperDate(intervalDate))) {

                        intervalDate.add(Calendar.DAY_OF_MONTH, task.getRepeat().getDaysInterval());

                        SuperDate superDate = new SuperDate(intervalDate);

                        if (isSuperDateToday(superDate)) {
                            return true;
                        }

                        if (isSuperDateIsFuture(superDate)) {
                            return false;
                        }
                    }

                   return false;
                }
            case Week:
                return isWeeklyRepeatFallsThisDate(Calendar.getInstance(), task.getRepeat());
            case Month:
                return task.getRepeat().getMonthDate() == calendar.get(Calendar.DAY_OF_MONTH);
        }

        return false;
    }

     public static void setNextDoDate(Task task) {

         if (task.getRepeat() == null) {
             return;
         }

         SuperDate startDate = task.getRepeat().getStartDate();
         SuperDate lastCompletedDate = task.getRepeat().getLastCompletedDate();

         switch (RepeatType.valueOf(task.getRepeat().getRepeatType())) {
             case Day:

                 if (!isSuperDateIsPast(startDate) && !isSuperDateToday(lastCompletedDate)) {
                     task.setDoDate(task.getRepeat().getStartDate());
                     break;
                 }

                 //Log.e(TAG, "setNextDoDate: setting next do date Days ");

                     Calendar intervalDate = getCalenderFromSuperDate(task.getRepeat().getStartDate());
                     intervalDate.set(Calendar.HOUR_OF_DAY, task.getRepeat().getStartDate().getHours());
                     intervalDate.set(Calendar.MINUTE, task.getRepeat().getStartDate().getMinutes());

                    SuperDate superDate = new SuperDate(intervalDate);

                     while (isSuperDateIsPast(superDate) || isSuperDateToday(superDate) || isSuperDateIsFuture(superDate)) {

                         if (isSuperDateIsFuture(superDate)) {
                             task.setDoDate(superDate);
                             break;
                         }

                         if (isSuperDateToday(superDate) && !isSuperDateToday(lastCompletedDate)) {
                             task.setDoDate(superDate);
                             break;
                         }

                         intervalDate.add(Calendar.DAY_OF_MONTH, task.getRepeat().getDaysInterval());
                         superDate = new SuperDate(intervalDate);
                     }

                 break;
             case Week:

                 Log.e(TAG, "setNextDoDate: setting next do date Weekly ");

                 Calendar calendar1 = Calendar.getInstance();
                 calendar1.set(Calendar.HOUR_OF_DAY, task.getRepeat().getStartDate().getHours());
                 calendar1.set(Calendar.MINUTE, task.getRepeat().getStartDate().getMinutes());

                 for (int i = 0 ; i < 15 ; i++) {
                     if (isWeeklyRepeatFallsThisDate(calendar1, task.getRepeat()) && !isSuperDateToday(lastCompletedDate)) {
                         task.setDoDate(new SuperDate(calendar1));
                         break;
                     }
                     calendar1.add(Calendar.DAY_OF_MONTH, 1);
                 }

                 break;

             case Month:

                 Log.e(TAG, "setNextDoDate: setting next do date monthly ");

                 Calendar calendar2 = Calendar.getInstance();
                 calendar2.set(Calendar.HOUR_OF_DAY, task.getRepeat().getStartDate().getHours());
                 calendar2.set(Calendar.MINUTE, task.getRepeat().getStartDate().getMinutes());

                 if (calendar2.get(Calendar.DAY_OF_MONTH) > task.getRepeat().getMonthDate() || isSuperDateToday(lastCompletedDate)) {
                     calendar2.add(Calendar.MONTH, + 1);

                     int maxDays = calendar2.getActualMaximum(Calendar.DAY_OF_MONTH);

                     if (maxDays < task.getRepeat().getMonthDate()) {
                         calendar2.set(Calendar.DAY_OF_MONTH, maxDays);
                     } else {
                         calendar2.set(Calendar.DAY_OF_MONTH, task.getRepeat().getMonthDate());
                     }

                 } else {
                     int maxDays = calendar2.getActualMaximum(Calendar.DAY_OF_MONTH);

                     if (maxDays < task.getRepeat().getMonthDate()) {
                         calendar2.set(Calendar.DAY_OF_MONTH, maxDays);
                     } else {
                         calendar2.set(Calendar.DAY_OF_MONTH, task.getRepeat().getMonthDate());
                     }
                 }
                 
                 task.setDoDate(new SuperDate(calendar2));
                 break;
         }

         FirestoreManager.getInstance().updateTask(task);
    }

    public static boolean isReminderMissed(SuperDate date) {
        if (date.getHours() < Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
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

    public static boolean isWeeklyRepeatFallsThisDate(Calendar calendar,Repeat repeat) {

        if (repeat == null) {
            return false;
        }

        int today = calendar.get(Calendar.DAY_OF_WEEK);

        switch (today) {
            case Calendar.SUNDAY:
                return repeat.isOnSunday();
            case Calendar.MONDAY:
                return repeat.isOnMonday();
            case Calendar.TUESDAY:
                return repeat.isOnTuesday();
            case Calendar.WEDNESDAY:
                return repeat.isOnWednesday();
            case Calendar.THURSDAY:
                return repeat.isOnThursday();
            case Calendar.FRIDAY:
                return repeat.isOnFriday();
            case Calendar.SATURDAY:
                return repeat.isOnSaturday();
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

    public static String getWeekDayStr(SuperDate date, boolean fullString) {

        if (date == null) {
            return " ";
        }

        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, date.getDate());
        c.set(Calendar.MONTH, date.getMonth() - 1);
        c.set(Calendar.YEAR, date.getYear());

        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        if (fullString) {
            switch (dayOfWeek) {
                case 1:
                    return "Sunday";
                case 2:
                    return "Monday";
                case 3:
                    return "Tuesday";
                case 4:
                    return "Wednesday";
                case 5:
                    return "Thursday";
                case 6:
                    return "Friday";
                case 7:
                    return "Saturday";
            }
        }

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

    public static String getDateStr(SuperDate date) {

        if (date == null) {
            return " ";
        }

        return monthDates[date.getDate() - 1];
    }

    public static String getDoDateString(SuperDate date) {

        if (isSuperDateToday(date)) return "Today";
        if (isSuperDateTomorrow(date)) return "Tomorrow";
        if (isSuperdateThisWeek(date)) return getWeekDayStr(date, true);

        return date.getSuperDateString();
    }

    public static String getMonthStrLong(int month) {
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

    public static Calendar getCalenderFromSuperDate(SuperDate date) {

        if (date == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        if (date.isHasDate()) {
            calendar.set(Calendar.DAY_OF_MONTH, date.getDate());
            calendar.set(Calendar.MONTH, date.getMonth() - 1);
            calendar.set(Calendar.YEAR, date.getYear());
        }
        calendar.set(Calendar.HOUR_OF_DAY, date.getHours());
        calendar.set(Calendar.MINUTE, date.getMinutes());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }

    public static int getDefaultTime() {

        if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 9 ) {
            return 9;
        } else if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 12) {
            return 12;
        } else if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 15) {
            return 15;
        } else if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 18) {
            return 18;
        } else if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 21) {
            return 21;
        }

        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
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

    public static String getNoTasksText() {

        if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) > 19 || Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 5) {
            return "No tasks for you today. \n Have a great night";
        } else if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) > 15) {
            return "No tasks for you today \n Enjoy your evening";
        } else if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) > 12 ) {
            return "No tasks for you today \n Enjoy afternoon";
        }

        return "Try adding some tasks today!";
    }

    public static int getNoTasksImg() {

        if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) > 19) {
            return R.drawable.illust_boy_wtih_coffee;
        } else if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) > 15) {
            return R.drawable.img_girl_with_cycle;
        } else if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) > 12 ) {
            return R.drawable.img_boy_girl_coffee;
        }

        return R.drawable.img_girl_with_coffee;
    }

    public static int getRankFromPoints(int points) {
        for (int i : Constants.trophyPoints) {
            if (points >= i) {
                return i+1;
            }
        }

        return 1;
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
