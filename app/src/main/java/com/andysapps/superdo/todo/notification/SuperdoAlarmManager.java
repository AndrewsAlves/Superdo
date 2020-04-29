package com.andysapps.superdo.todo.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;


import com.andysapps.superdo.todo.BackgroundBroadcast;
import com.andysapps.superdo.todo.NotificationBroadcast;
import com.andysapps.superdo.todo.Utils;
import com.andysapps.superdo.todo.manager.SharedPrefsManager;
import com.andysapps.superdo.todo.manager.TaskOrganiser;
import com.andysapps.superdo.todo.model.SuperDate;
import com.andysapps.superdo.todo.model.Task;
import com.andysapps.superdo.todo.model.taskfeatures.Deadline;

import java.util.Calendar;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static com.andysapps.superdo.todo.notification.SuperdoNotificationManager.notification_id_afternoon;
import static com.andysapps.superdo.todo.notification.SuperdoNotificationManager.notification_id_deadline;
import static com.andysapps.superdo.todo.notification.SuperdoNotificationManager.notification_id_deadline_morning;
import static com.andysapps.superdo.todo.notification.SuperdoNotificationManager.notification_id_deadline_daybefore;
import static com.andysapps.superdo.todo.notification.SuperdoNotificationManager.notification_id_deadline_done;
import static com.andysapps.superdo.todo.notification.SuperdoNotificationManager.notification_id_morning;
import static com.andysapps.superdo.todo.notification.SuperdoNotificationManager.notification_id_night;
import static com.andysapps.superdo.todo.notification.SuperdoNotificationManager.notification_id_remind;

public class SuperdoAlarmManager {

    private static SuperdoAlarmManager ourInstance;
    public static final String TAG = "AlarmManager";

    public static final int REQ_CODE_BACKGROUND_PROCESS = 101100101;

    public static final int REQ_CODE_MORNING = 9;
    public static final int REQ_CODE_AFTERNOON = 15;
    public static final int REQ_CODE_EVENING = 18;
    public static final int REQ_CODE_NIGHT = 21;

    public static final String intent_key_notification_id = "notification_id";
    public static final String intent_key_notification_deadline_type = "notification_deadline_key";

    public static final String intent_key_task_id = "task_id";

    AlarmManager alarmManager;

    public static SuperdoAlarmManager getInstance() {
        return ourInstance;
    }

    private SuperdoAlarmManager(Context context) {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public static void initialise(Context context) {
        ourInstance = new SuperdoAlarmManager(context);
    }

    public static void destroy() {
        ourInstance = null;
    }

    //////////////////////////////////////
    //////// ALARMS

    public void registerBackgroundProcesses(Context context, boolean reshedule) {

        Log.i(TAG, "registerBackgroundProcesses() called with: context = [" + context + "], reshedule = [" + reshedule + "]");

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, BackgroundBroadcast.class);
        intent.putExtra(BackgroundBroadcast.key_id_background_process, BackgroundBroadcast.id_process_register_daily_alarms);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQ_CODE_BACKGROUND_PROCESS, intent, 0);

        Calendar calendar = Calendar.getInstance();
        if (reshedule) {
            calendar = Utils.getTomorrow();
        }
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public void registerDailyNotificationsAndReminders(Context context) {
        Log.e(TAG, "registerDailyNotificationsAndReminders() called with: context = [" + context + "]");
        registerDailyQuoteAlarms(context);
        registerDailyReminders(context);
        SharedPrefsManager.saveBackgroundRegistriesTimeStamp(context, Calendar.getInstance().getTimeInMillis());
    }

    public void registerDailyQuoteAlarms(Context context) {
        SuperDate date = new SuperDate();
        date.setTime(9,0);
        setAlarmExact(context, date, notification_id_morning, REQ_CODE_MORNING,  FLAG_CANCEL_CURRENT);
        date.setTime(15,0);
        setAlarmExact(context, date, notification_id_afternoon, REQ_CODE_AFTERNOON, FLAG_CANCEL_CURRENT);
        date.setTime(21,0);
        setAlarmExact(context, date, notification_id_night, REQ_CODE_NIGHT, FLAG_CANCEL_CURRENT);
    }

    public void registerDailyReminders(Context context) {

        Log.e(TAG, "registerDailyReminders() called with: context = [" + context + "]");

        /// REMIND REMINDING TASKS
        for (Task task : TaskOrganiser.getInstance().remindingTasks) {
           setRemind(context, task);
        }

        /// REMIND DEADLINED TASKS
        for (Task task : TaskOrganiser.getInstance().deadlineTasks) {
            setDeadlineRemind(context, task);
        }
    }

    public void setRemind(Context context, Task task) {
        Intent intentRemind = new Intent(context, NotificationBroadcast.class);
        intentRemind.putExtra(intent_key_notification_id, notification_id_remind);
        PendingIntent pendingIntent;

        if (task.getRemindRequestCode() == 0) {
            task.generateNewRemindRequestCode();
        }

        if (Utils.isSuperDateToday(task.getDoDate())) {
            intentRemind.putExtra(intent_key_task_id, task.getDocumentId());
            pendingIntent = PendingIntent.getBroadcast(context, task.getRemindRequestCode(), intentRemind, FLAG_CANCEL_CURRENT);
            setAlarmExact(context, Utils.getCalenderFromSuperDate(task.getDoDate()), pendingIntent);
        }
    }

    public void setDeadlineRemind(Context context, Task task) {

        Intent intentDeadline = new Intent(context, NotificationBroadcast.class);
        intentDeadline.putExtra(intent_key_notification_id, notification_id_deadline);
        PendingIntent pendingIntent;

        Deadline deadline = task.getDeadline();
        SuperDate superdate = new SuperDate(deadline.date, deadline.month, deadline.year, deadline.hours, deadline.minutes);

        if (task.getDeadline().getDeadlineRequestCode() == 0) {
            task.getDeadline().generateNewRequestCode();
        }

        intentDeadline.putExtra(intent_key_task_id, task.getDocumentId());

        if (Utils.isSuperDateToday(superdate)) {

            Calendar todayCalender = Calendar.getInstance();
            todayCalender.set(Calendar.HOUR_OF_DAY, 9);
            todayCalender.set(Calendar.MINUTE, 0);
            todayCalender.set(Calendar.SECOND, 0);

            intentDeadline.putExtra(intent_key_notification_deadline_type, notification_id_deadline_morning);
            pendingIntent = PendingIntent.getBroadcast(context, task.getDeadline().getDeadlineRequestCode() + 9, intentDeadline, FLAG_CANCEL_CURRENT);
            setAlarmExact(context, todayCalender, pendingIntent);

            todayCalender.set(Calendar.HOUR_OF_DAY, deadline.hours);
            todayCalender.set(Calendar.MINUTE, deadline.minutes);
            todayCalender.set(Calendar.SECOND, 0);

            intentDeadline.putExtra(intent_key_notification_deadline_type, notification_id_deadline_done);
            pendingIntent = PendingIntent.getBroadcast(context, task.getDeadline().getDeadlineRequestCode(), intentDeadline, FLAG_CANCEL_CURRENT);
            setAlarmExact(context, todayCalender, pendingIntent);
        }

        if (Utils.isSuperDateTomorrow(superdate)) {

            Calendar todayCalender = Calendar.getInstance();
            todayCalender.set(Calendar.HOUR_OF_DAY, 18);
            todayCalender.set(Calendar.MINUTE, 0);
            todayCalender.set(Calendar.SECOND, 0);

            intentDeadline.putExtra(intent_key_notification_deadline_type, notification_id_deadline_daybefore);
            pendingIntent = PendingIntent.getBroadcast(context, task.getDeadline().getDeadlineRequestCode() - 1, intentDeadline, FLAG_CANCEL_CURRENT);
            setAlarmExact(context, todayCalender, pendingIntent);
        }
    }

    public void setAlarmRepeat(Context context, SuperDate date, String notificationId, int requestCode, long repeatInterval, int flag) {
        Intent intent = new Intent(context, NotificationBroadcast.class);
        intent.putExtra(intent_key_notification_id, notificationId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, flag);

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

        } else {

        }

        getAlarmManager(context).setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), repeatInterval, pendingIntent);
    }

    /* public void setRemindAlarm(Context context, Task task) {

        AlarmManager alarmManage = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationBroadcast.class);
        intent.putExtra(intent_key_notification_id, notification_id_remind);
        intent.putExtra(intent_key_task_id, task.getDocumentId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, task.getRemind().getRemindRequestCode(), intent, FLAG_UPDATE_CURRENT);

        switch (RemindType.valueOf(task.getRemind().getRemindType())) {
            case REMIND_ONCE:

                Log.e(TAG, "setting remind alarm for once...");

                SuperDate date = task.getRemind().getRemindDate();
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

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManage.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManage.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }

                break;
            case REMIND_REPEAT:

                Calendar startCalender = Calendar.getInstance();
                Repeat repeat = task.getRemind().getRemindRepeat();
                SuperDate startDate = repeat.getStartDate();

                if (startDate.isHasDate()) {
                    startCalender.set(Calendar.DAY_OF_MONTH, startDate.getDate());
                    startCalender.set(Calendar.MONTH, startDate.getMonth() - 1);
                    startCalender.set(Calendar.YEAR, startDate.getYear());
                }

                startCalender.set(Calendar.HOUR_OF_DAY, startDate.getHours());
                startCalender.set(Calendar.MINUTE, startDate.getMinutes());
                startCalender.set(Calendar.SECOND, 0);
                startCalender.set(Calendar.MILLISECOND, 0);

                switch (RepeatType.valueOf(task.getRemind().getRemindRepeat().getRepeatType())) {

                    case Day:
                        alarmManage.setInexactRepeating(AlarmManager.RTC_WAKEUP, startCalender.getTimeInMillis(), AlarmManager.INTERVAL_DAY * repeat.getDaysInterval(), pendingIntent);
                        break;

                    case Week:
                        Log.e(TAG, "setting remind alarm for repeat Week...");
                        alarmManage.setInexactRepeating(AlarmManager.RTC_WAKEUP, startCalender.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                        break;

                    case Month:

                        Log.e(TAG, "setting remind alarm for repeat month...");

                        Calendar cal = Calendar.getInstance();

                        for (int i = 1 ; i < 13 ; i++) {

                            PendingIntent pendingIntentMonthly = PendingIntent.getBroadcast(context, task.getRemind().getRemindRequestCode() + i, intent, FLAG_UPDATE_CURRENT);

                            int currentMonth = cal.get(Calendar.MONTH);

                            // move month ahead
                            if (i != 1) {
                                currentMonth++;
                            }

                            // check if has not exceeded threshold of december

                            if(currentMonth > Calendar.DECEMBER) {
                                // alright, reset month to jan and forward year by 1 e.g fro 2013 to 2014
                                currentMonth = Calendar.JANUARY;
                                // Move year ahead as well
                                cal.set(Calendar.YEAR, cal.get(Calendar.YEAR)+1);
                            }

                            // reset calendar to next month
                            cal.set(Calendar.MONTH, currentMonth);

                            // get the maximum possible days in this month

                            if (repeat.getMonthDate() > cal.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                            } else {
                                cal.set(Calendar.DAY_OF_MONTH,repeat.getMonthDate());
                            }

                            int setMonth = cal.get(Calendar.MONTH);

                            if (currentMonth != setMonth) {
                                cal.set(Calendar.MONTH, currentMonth);
                                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                            }

                            // set time
                            cal.set(Calendar.HOUR_OF_DAY, startDate.getHours());
                            cal.set(Calendar.MINUTE, startDate.getMinutes());
                            cal.set(Calendar.SECOND, 0);
                            cal.set(Calendar.MILLISECOND, 0);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                alarmManage.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntentMonthly);
                            } else {
                                alarmManage.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntentMonthly);
                            }

                            Log.e(TAG, "setRemindAlarm: date : " +  cal.get(Calendar.DATE)  + " month : " + Utils.getMonthString(cal.get(Calendar.MONTH) + 1) + " year : " + cal.get(Calendar.YEAR)
                                   + " max date : " + cal.getActualMaximum(Calendar.DAY_OF_MONTH));

                            // set the calendar to maximum day (e.g in case of fEB 28th, or leap 29th)
                            // this is time one month ahead
                        }

                        break;
                }

                break;
        }
    } */

    /*public void setDeadlineReminder(Context context, Task task) {

        AlarmManager alarmManage = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Deadline date = task.getDeadline();
        Calendar calendar = Calendar.getInstance();

        for (int i = 0 ; i < 3 ; i++) {

            Intent intent = new Intent(context, NotificationBroadcast.class);
            intent.putExtra(intent_key_notification_id, notification_id_deadline);
            intent.putExtra(intent_key_task_id, task.getDocumentId());

            if (i == 0) {
                intent.putExtra(intent_key_notification_deadline_type, notification_id_deadline_daybefore);
            } else if (i == 1) {
                intent.putExtra(intent_key_notification_deadline_type, notification_id_deadline_morning);
            } else {
                intent.putExtra(intent_key_notification_deadline_type, notification_id_deadline_done);
            }

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, task.getDeadline().getDeadlineRequestCode() + i, intent, 0);

            if (i == 0) {
                if (date.hasDate) {
                    calendar.set(Calendar.DAY_OF_MONTH, date.getDate());
                    calendar.set(Calendar.MONTH, date.getMonth() - 1);
                    calendar.set(Calendar.YEAR, date.getYear());
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                    Log.e(TAG, "has date");
                }
                calendar.set(Calendar.HOUR_OF_DAY, 9);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                Log.e(TAG, "setDeadlineAlarm DAY BEFORE !");
                Log.e(TAG, "deadline : " + task.getDeadline().getDeadlineRequestCode() + i);

                setAlarmExact(alarmManage, calendar, pendingIntent);
            }

            if (i == 1) {
                if (date.hours < 3) {
                    continue;
                }
                if (date.hasDate) {
                    calendar.set(Calendar.DAY_OF_MONTH, date.getDate());
                    calendar.set(Calendar.MONTH, date.getMonth() - 1);
                    calendar.set(Calendar.YEAR, date.getYear());
                    Log.e(TAG, "has date");
                }
                calendar.set(Calendar.HOUR_OF_DAY, (date.getHours() - 3));
                calendar.set(Calendar.MINUTE, date.getMinutes());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                Log.e(TAG, "setDeadlineAlarm 3 hours!");
                Log.e(TAG, "deadline : " + task.getDeadline().getDeadlineRequestCode() + i);

                setAlarmExact(alarmManage, calendar, pendingIntent);
            }

            if (i == 2) {
                if (date.hasDate) {
                    calendar.set(Calendar.DAY_OF_MONTH, date.getDate());
                    calendar.set(Calendar.MONTH, date.getMonth() - 1);
                    calendar.set(Calendar.YEAR, date.getYear());
                    Log.e(TAG, "has date");
                }
                calendar.set(Calendar.HOUR_OF_DAY, date.getHours());
                calendar.set(Calendar.MINUTE, date.getMinutes());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                Log.e(TAG, "setDeadlineAlarm DONE!");
                Log.e(TAG, "deadline : " + task.getDeadline().getDeadlineRequestCode() + i);

                setAlarmExact(alarmManage, calendar, pendingIntent);
            }
        }
    }*/

    public void setAlarmExact(Context context,Calendar calendar, PendingIntent pendingIntent) {

        Log.e(TAG, "millies : " + calendar.getTimeInMillis());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getAlarmManager(context).setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            getAlarmManager(context).setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    public void setAlarmExact(Context context, SuperDate date, String notificationId, int requestCode, int flag) {

        Log.e(TAG, "setAlarmExact: setting notification alarm " + notificationId + " " + requestCode);

        Intent intent = new Intent(context, NotificationBroadcast.class);
        intent.putExtra(intent_key_notification_id, notificationId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, flag);

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getAlarmManager(context).setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            getAlarmManager(context).setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    public AlarmManager getAlarmManager(Context context) {
        if (alarmManager == null) {
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }

        return alarmManager;
    }

    public void clearAlarm(Context context, int requestCode) {
        AlarmManager alarmManage = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, FLAG_UPDATE_CURRENT);
        alarmManage.cancel(pendingIntent);
    }

    public void clearRemindRepeatAlarm(Context context, int requestCode) {
        AlarmManager alarmManage = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationBroadcast.class);
        for (int i = 0 ; i < 13 ; i++ ) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode + i, intent, FLAG_UPDATE_CURRENT);
            alarmManage.cancel(pendingIntent);
        }
    }

    public void testNotification(Context context) {
        SuperDate date = Utils.getSuperdateToday();
        date.setTime(9, 0);
    }

}
