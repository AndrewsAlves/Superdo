package com.andysapps.superdo.todo.notification;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;


import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.andysapps.superdo.todo.NotificationBroadcast;
import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.Utils;
import com.andysapps.superdo.todo.activity.MainActivity;
import com.andysapps.superdo.todo.model.SuperDate;

import java.util.Calendar;
import java.util.Random;

import io.grpc.okhttp.internal.Util;

public class SuperdoAlarmManager {

    private static SuperdoAlarmManager ourInstance;

    public static final String CHANNEL_TASK = "sd_notification_tasks";
    public static final String CHANNEL_DAILY = "sd_notification_recursive";
    public static final String CHANNEL_DEADLINE = "sd_notification_deadline";
    public static final String CHANNEL_REMINDER = "sd_notification_reminder";
    public static final String CHANNEL_FOCUS = "sd_notification_focus";
    public static final String CHANNEL_SUBSCRIPTIONS= "sd_notification_subscription";
    public static final String CHANNEL_RANDOM = "sd_notification_random";

    public static final int REQ_CODE_MORNING = 9;
    public static final int REQ_CODE_AFTERNOON = 15;
    public static final int REQ_CODE_EVENING = 18;
    public static final int REQ_CODE_NIGHT = 21;


    /**
     * NOTIFICATION SHOW STRATEGY
     *
     *
     *
     */

    public static SuperdoAlarmManager getInstance() {
        return ourInstance;
    }

    private SuperdoAlarmManager(Context context) {
    }

    public static void initialise(Context context) {
        ourInstance = new SuperdoAlarmManager(context);
    }

    public void createNotification(Context context, String channelId, String title, String content, String contentBig) {

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_superdo)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(contentBig))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(101, builder.build());
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
        setAlarmRepeat(context, date, REQ_CODE_MORNING);
        date.setTime(15,0);
        setAlarmRepeat(context, date, REQ_CODE_AFTERNOON);
        date.setTime(18,0);
        setAlarmRepeat(context, date, REQ_CODE_EVENING);
        date.setTime(21,0);
        setAlarmRepeat(context, date, REQ_CODE_NIGHT);
    }

    public void setAlarmExact(Context context, SuperDate date, int requestCode) {
        AlarmManager alarmManage = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, date.getDate());
        calendar.set(Calendar.MONTH, date.getMonth() - 1);
        calendar.set(Calendar.YEAR, date.getYear());
        calendar.set(Calendar.HOUR_OF_DAY, date.getHours());
        calendar.set(Calendar.MINUTE, date.getMinutes());
        calendar.set(Calendar.SECOND, 0);

        alarmManage.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public void setAlarmRepeat(Context context, SuperDate date, int requestCode) {
        AlarmManager alarmManage = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0);

        Calendar calendar = Calendar.getInstance();
        if (date.isHasDate()) {
            calendar.set(Calendar.DAY_OF_MONTH, date.getDate());
            calendar.set(Calendar.MONTH, date.getMonth() - 1);
            calendar.set(Calendar.YEAR, date.getYear());
        }
        calendar.set(Calendar.HOUR_OF_DAY, date.getHours());
        calendar.set(Calendar.MINUTE, date.getMinutes());
        calendar.set(Calendar.SECOND, 0);

        alarmManage.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public void testNotification(Context context) {
        SuperDate date = Utils.getSuperdateToday();
        date.setTime(13, 54);
        setAlarmExact(context, date, 1);
    }
}
