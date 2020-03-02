package com.andysapps.superdo.todo.notification;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;


import com.andysapps.superdo.todo.NotificationBroadcast;
import com.andysapps.superdo.todo.Utils;
import com.andysapps.superdo.todo.enums.RemindType;
import com.andysapps.superdo.todo.enums.RepeatType;
import com.andysapps.superdo.todo.model.SuperDate;
import com.andysapps.superdo.todo.model.Task;
import com.andysapps.superdo.todo.model.sidekicks.Repeat;

import java.util.Calendar;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static com.andysapps.superdo.todo.notification.SuperdoNotificationManager.notification_id_afternoon;
import static com.andysapps.superdo.todo.notification.SuperdoNotificationManager.notification_id_morning;
import static com.andysapps.superdo.todo.notification.SuperdoNotificationManager.notification_id_night;
import static com.andysapps.superdo.todo.notification.SuperdoNotificationManager.notification_id_remind;

public class SuperdoAlarmManager {

    private static SuperdoAlarmManager ourInstance;

    public static final String CHANNEL_DAILY = "sd_notification_recursive";

    public static final String CHANNEL_TASK = "sd_notification_tasks";
    public static final String CHANNEL_DEADLINE = "sd_notification_deadline";
    public static final String CHANNEL_REMINDER = "sd_notification_reminder";
    public static final String CHANNEL_FOCUS = "sd_notification_focus";
    public static final String CHANNEL_SUBSCRIPTIONS= "sd_notification_subscription";
    public static final String CHANNEL_RANDOM = "sd_notification_random";

    public static final int REQ_CODE_MORNING = 9;
    public static final int REQ_CODE_AFTERNOON = 15;
    public static final int REQ_CODE_EVENING = 18;
    public static final int REQ_CODE_NIGHT = 21;

    public static final String intent_key_notification_id = "notification_id";
    public static final String intent_key_task_id = "task_id";

    public static SuperdoAlarmManager getInstance() {
        return ourInstance;
    }

    private SuperdoAlarmManager(Context context) {
    }

    public static void initialise(Context context) {
        ourInstance = new SuperdoAlarmManager(context);
    }

    public void createNotificationChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_DAILY, "Daily", importance);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            for (int i = 1 ; i <= 7 ; i++) {
                switch (i) {
                    case 1:
                        channel = new NotificationChannel(CHANNEL_DAILY, "Daily", importance);
                        break;
                    case 2:
                        channel = new NotificationChannel(CHANNEL_TASK, "Tasks", importance);
                        break;
                    case 3:
                        channel = new NotificationChannel(CHANNEL_DEADLINE, "Deadline", importance);
                        break;
                    case 4:
                        channel = new NotificationChannel(CHANNEL_REMINDER, "Reminders", importance);
                        break;
                    case 5:
                        channel = new NotificationChannel(CHANNEL_FOCUS, "Focus", importance);
                        break;
                    case 6:
                        channel = new NotificationChannel(CHANNEL_SUBSCRIPTIONS, "Subscription", importance);
                        break;
                    case 7:
                        channel = new NotificationChannel(CHANNEL_RANDOM, "Random", importance);
                        break;
                }
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public void registerDailyNotificationAlarms(Context context) {
        SuperDate date = new SuperDate();
        date.setTime(9,0);
        setAlarmRepeat(context, date, notification_id_morning, REQ_CODE_MORNING,AlarmManager.INTERVAL_DAY, FLAG_UPDATE_CURRENT);
        date.setTime(15,0);
        setAlarmRepeat(context, date, notification_id_afternoon, REQ_CODE_AFTERNOON,AlarmManager.INTERVAL_DAY, FLAG_UPDATE_CURRENT);
        date.setTime(21,0);
        setAlarmRepeat(context, date, notification_id_night, REQ_CODE_NIGHT,AlarmManager.INTERVAL_DAY, FLAG_UPDATE_CURRENT);
    }

    public void setAlarmRepeat(Context context, SuperDate date, String notificationId, int requestCode, long repeatInterval, int flag) {
        AlarmManager alarmManage = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
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

        alarmManage.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), repeatInterval, pendingIntent);
    }

    public void setRemindAlarm(Context context, Task task) {
        AlarmManager alarmManage = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationBroadcast.class);
        intent.putExtra(intent_key_notification_id, notification_id_remind);
        intent.putExtra(intent_key_task_id, task.getDocumentId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, task.getRemind().getRemindRequestCode(), intent, FLAG_UPDATE_CURRENT);

        switch (RemindType.valueOf(task.getRemind().getRemindType())) {
            case REMIND_ONCE:
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

                Repeat repeat = task.getRemind().getRemindRepeat();

                if (date.isHasDate()) {
                    calendar.set(Calendar.DAY_OF_MONTH, date.getDate());
                    calendar.set(Calendar.MONTH, date.getMonth() - 1);
                    calendar.set(Calendar.YEAR, date.getYear());
                }
                calendar.set(Calendar.HOUR_OF_DAY, date.getHours());
                calendar.set(Calendar.MINUTE, date.getMinutes());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                switch (RepeatType.valueOf(task.getRemind().getRemindRepeat().getRepeatType())) {

                    case Day:
                        long interval = AlarmManager.INTERVAL_DAY * repeat.getTimes();

                }
        }


    }

    public void clearAlarm(Context context, int requestCode) {
        AlarmManager alarmManage = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, FLAG_UPDATE_CURRENT);
        alarmManage.cancel(pendingIntent);
    }

    public void testNotification(Context context) {
        SuperDate date = Utils.getSuperdateToday();
        date.setTime(9, 0);

    }


}
